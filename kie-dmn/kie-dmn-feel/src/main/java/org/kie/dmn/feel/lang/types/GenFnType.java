/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.kie.dmn.feel.lang.types;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.SimpleType;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.FEELFunction.Param;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;


public class GenFnType implements SimpleType {

    private final List<Type> evaluatedTypeArgs;
    private final Type functionReturnType;

    public GenFnType(List<Type> evaluatedTypeArgs, Type functionReturnType) {
        this.evaluatedTypeArgs = new ArrayList<>(evaluatedTypeArgs);
        this.functionReturnType = functionReturnType;
    }

    @Override
    public boolean isInstanceOf(Object o) {
        if (o instanceof FEELFunction oFn) {
            List<List<Param>> currentGenFnTypeParams = oFn.getParameters();
            if (currentGenFnTypeParams.isEmpty()) {
                //this is used to consider function as parameter
                return oFn.isCompatible(evaluatedTypeArgs.toArray(new Type[0]), functionReturnType);
            }
            return checkSignatures(currentGenFnTypeParams, evaluatedTypeArgs);
        }
        return false;
    }

    @Override
    public boolean isAssignableValue(Object value) {
        if (value == null) {
            return true; // a null-value can be assigned to any type.
        }
        return isInstanceOf(value);
    }

    @Override
    public String getName() {
        return "[anonymous]";
    }

    @Override
    public boolean conformsTo(Type t) {
        if (t instanceof GenFnType fnT) {
            return fnT.evaluatedTypeArgs.size() == this.evaluatedTypeArgs.size() &&
                    IntStream.range(0, evaluatedTypeArgs.size()).allMatch(i -> fnT.evaluatedTypeArgs.get(i).conformsTo(this.evaluatedTypeArgs.get(i))) &&
                    this.functionReturnType.conformsTo(fnT.functionReturnType);
        } else {
            return t == BuiltInType.FUNCTION;
        }
    }

    static boolean checkSignatures(List<List<Param>> currentGenFnTypeParams, List<Type> evaluatedTypeArgs) {
        List<List<Param>> signatures = currentGenFnTypeParams.stream().filter(signature -> signature.size() == evaluatedTypeArgs.size()).toList();
        for (List<Param> signature : signatures) {
            if (signature.size() == evaluatedTypeArgs.size() && IntStream.range(0, evaluatedTypeArgs.size()).allMatch(i -> evaluatedTypeArgs.get(i).conformsTo(signature.get(i).type))) {
                return true;
            }
        }
        return false;
    }
}