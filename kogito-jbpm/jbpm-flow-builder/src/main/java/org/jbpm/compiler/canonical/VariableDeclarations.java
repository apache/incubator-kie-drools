/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.compiler.canonical;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;

public class VariableDeclarations {

    public static VariableDeclarations of(VariableScope vscope) {
        HashMap<String, String> vs = new HashMap<>();
        for (Variable variable : vscope.getVariables()) {
            if (variable.hasTag(Variable.INTERNAL_TAG)) {
                continue;
            }
            
            vs.put(variable.getName(), variable.getType().getStringType());
        }
        return of(vs);
    }
    
    public static VariableDeclarations ofInput(VariableScope vscope) {
        
        return of(vscope, variable -> variable.hasTag(Variable.INTERNAL_TAG) || variable.hasTag(Variable.OUTPUT_TAG));
    }
    
    public static VariableDeclarations ofOutput(VariableScope vscope) {
        
        return of(vscope, variable -> variable.hasTag(Variable.INTERNAL_TAG) || variable.hasTag(Variable.INPUT_TAG));
    }
    
    public static VariableDeclarations of(VariableScope vscope, Predicate<Variable> filterOut) {
        HashMap<String, String> vs = new HashMap<>();
        for (Variable variable : vscope.getVariables()) {
            if (filterOut.test(variable)) {
                continue;
            }
            
            vs.put(variable.getName(), variable.getType().getStringType());
        }
        return of(vs);
    }

    public static VariableDeclarations of(Map<String, String> vscope) {
        return new VariableDeclarations(vscope);
    }

    private final Map<String, String> vscope;

    public VariableDeclarations(Map<String, String> vscope) {
        this.vscope = vscope;
    }

    public String getType(String vname) {
        return vscope.get(vname);
    }

    public Map<String, String> getTypes() {
        return vscope;
    }
}
