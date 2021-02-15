/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.compiler.canonical;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;

public class VariableDeclarations {

    public static VariableDeclarations of(VariableScope vscope) {
        HashMap<String, Variable> vs = new HashMap<>();
        for (Variable variable : vscope.getVariables()) {
            if (variable.hasTag(Variable.INTERNAL_TAG)) {
                continue;
            }
            
            vs.put(variable.getName(), variable);
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
        HashMap<String, Variable> vs = new HashMap<>();
        for (Variable variable : vscope.getVariables()) {
            if (filterOut.test(variable)) {
                continue;
            }
            
            vs.put(variable.getName(), variable);
        }
        return of(vs);
    }

    public static VariableDeclarations of(Map<String, Variable> vscope) {
        return new VariableDeclarations(vscope);
    }
    
    public static VariableDeclarations ofRawInfo(Map<String, String> vscope) {
        HashMap<String, Variable> vs = new HashMap<>();

        if(vscope != null) {
            for (Entry<String, String> entry : vscope.entrySet()) {
                Variable variable = new Variable();
                variable.setName(entry.getKey());
                variable.setType(new ObjectDataType(entry.getValue()));
                vs.put(entry.getKey(), variable);
            }
        }
        
        return new VariableDeclarations(vs);
    }

    private final Map<String, Variable> vscope;

    public VariableDeclarations(Map<String, Variable> vscope) {
        this.vscope = vscope;
    }

    public String getType(String vname) {
        return vscope.get(vname).getType().getStringType();
    }
    
    public List<String> getTags(String vname) {
        return vscope.get(vname).getTags();
    }

    public Map<String, Variable> getTypes() {
        return vscope;
    }
}
