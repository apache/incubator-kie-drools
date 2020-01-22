/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel.lang.types;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.dmn.feel.lang.SimpleType;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.runtime.FEELFunction;

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
            List<List<String>> signs = oFn.getParameters().stream().filter(sign -> sign.size() == argsGen.size()).collect(Collectors.toList());
            
            return true;
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
}
