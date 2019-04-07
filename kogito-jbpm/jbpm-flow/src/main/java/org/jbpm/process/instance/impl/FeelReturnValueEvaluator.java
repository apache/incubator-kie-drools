/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessContext;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.FEELProfile;

public class FeelReturnValueEvaluator implements ReturnValueEvaluator, Externalizable {
    
    private static final long   serialVersionUID = 630l;

    private String expr;

    public FeelReturnValueEvaluator() {
    }

    public FeelReturnValueEvaluator(String expr) {
        this.expr = expr;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        expr = in.readUTF();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF( expr );
    }

    public Object evaluate(ProcessContext context) throws Exception {
        Map<String, Object> variables = new HashMap<>();
        variables.put("kcontext", context);  
        
        // insert globals into context
        Globals globals = context.getKieRuntime().getGlobals();
        
        if (globals != null && globals.getGlobalKeys() != null) {
            for (String gKey : globals.getGlobalKeys()) {
                variables.put(gKey, globals.get(gKey));
            }
        }
        if (context.getProcessInstance() != null && context.getProcessInstance().getProcess() != null) {
            // insert process variables
            VariableScopeInstance variableScope = (VariableScopeInstance) ((WorkflowProcessInstance)context.getProcessInstance())
                    .getContextInstance(VariableScope.VARIABLE_SCOPE);
    
            if (variables != null ) {
                variables.putAll(variableScope.getVariables());
            }
        }
        DMNRuntime runtime = ((KieSession) context.getKieRuntime()).getKieRuntime(DMNRuntime.class);
        List<FEELProfile> profiles = (List)((DMNRuntimeImpl) runtime).getProfiles();
        FEEL feel = FEEL.newInstance(runtime.getRootClassLoader(), profiles);
        
        Object value = feel.evaluate(expr, variables);

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
