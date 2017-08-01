/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.instance.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.process.ProcessContext;

public class JavaScriptReturnValueEvaluator implements ReturnValueEvaluator, Externalizable {
    
    private static final long   serialVersionUID = 630l;

    private String expr;

    public JavaScriptReturnValueEvaluator() {
    }

    public JavaScriptReturnValueEvaluator(String expr) {
        this.expr = expr;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        expr = in.readUTF();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF( expr );
    }

    public Object evaluate(ProcessContext context) throws Exception {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        
        // insert globals into context
        Globals globals = context.getKieRuntime().getGlobals();
        
        if (globals != null && globals.getGlobalKeys() != null) {
            for (String gKey : globals.getGlobalKeys()) {
                engine.put(gKey, globals.get(gKey));
            }
        }
        // insert process kcontext
        engine.put("kcontext", context);
        if (context.getProcessInstance() != null && context.getProcessInstance().getProcess() != null) {
            // insert process variables
            VariableScopeInstance variableScope = (VariableScopeInstance) ((WorkflowProcessInstance)context.getProcessInstance())
                    .getContextInstance(VariableScope.VARIABLE_SCOPE);
    
            Map<String, Object> variables = variableScope.getVariables();
            if (variables != null ) {
                for (Entry<String, Object> variable : variables.entrySet()) {
                    engine.put(variable.getKey(), variable.getValue());
                }
            }
        }

        Object value = engine.eval(expr);

        if ( !(value instanceof Boolean) ) {
            throw new RuntimeException( "Constraints must return boolean values: " + 
        		expr + " returns " + value + 
        		(value == null? "" : " (type=" + value.getClass()));
        }
        
        return ((Boolean) value).booleanValue();
    }

    public String toString() {
        return this.expr;
    }    

}
