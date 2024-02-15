/**
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.kie.dmn.feel.lang.SimpleType;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.FEELFunction.Param;

public class GenFnType implements SimpleType {

    private final List<Type> argsGen;
    private final Type returnGen;

    public GenFnType(List<Type> argsGen, Type returnGen) {
        this.argsGen = new ArrayList<>(argsGen);
        this.returnGen = returnGen;
    }

    @Override
    public boolean isInstanceOf(Object o) {
        if (o instanceof FEELFunction) {
            FEELFunction oFn = (FEELFunction) o;
            List<List<Param>> signatures = oFn.getParameters().stream().filter(signature -> signature.size() == argsGen.size()).collect(Collectors.toList());
            for (List<Param> signature : signatures) {
                if (signature.size() == argsGen.size() && IntStream.range(0, argsGen.size()).allMatch(i -> argsGen.get(i).conformsTo(signature.get(i).type))) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    @Override
    public boolean isAssignableValue(Object value) {
        if ( value == null ) {
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
        if (t instanceof GenFnType) {
            GenFnType fnT = (GenFnType) t;
            return fnT.argsGen.size() == this.argsGen.size() &&
                   IntStream.range(0, argsGen.size()).allMatch(i -> fnT.argsGen.get(i).conformsTo(this.argsGen.get(i))) &&
                   this.returnGen.conformsTo(fnT.returnGen);
        } else {
            return t == BuiltInType.FUNCTION;
        }
    }
}
