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

package org.jbpm.process.instance;

import java.util.Map;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.kie.api.definition.process.Process;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.runtime.manager.InternalRuntimeManager;

public abstract class AbstractProcessInstanceFactory implements ProcessInstanceFactory {
	
	public ProcessInstance createProcessInstance(Process process, CorrelationKey correlationKey, 
			                                     InternalKnowledgeRuntime kruntime,
			                                     Map<String, Object> parameters) {
		ProcessInstance processInstance = (ProcessInstance) createProcessInstance();
		processInstance.setKnowledgeRuntime( kruntime );
        processInstance.setProcess( process );
        
        if (correlationKey != null) {
        	processInstance.getMetaData().put("CorrelationKey", correlationKey);
        }
        InternalRuntimeManager manager = (InternalRuntimeManager) kruntime.getEnvironment().get("RuntimeManager");
        if (manager != null) {
            processInstance.setDeploymentId(manager.getIdentifier());
        }
        
        ((InternalProcessRuntime) kruntime.getProcessRuntime()).getProcessInstanceManager()
    		.addProcessInstance( processInstance, correlationKey );

        // set variable default values
        // TODO: should be part of processInstanceImpl?
        VariableScope variableScope = (VariableScope) ((ContextContainer) process).getDefaultContext( VariableScope.VARIABLE_SCOPE );
        VariableScopeInstance variableScopeInstance = (VariableScopeInstance) processInstance.getContextInstance( VariableScope.VARIABLE_SCOPE );
        // set input parameters
        if ( parameters != null ) {
            if ( variableScope != null ) {
                for ( Map.Entry<String, Object> entry : parameters.entrySet() ) {
                	
                	variableScope.validateVariable(process.getName(), entry.getKey(), entry.getValue());
                    variableScopeInstance.setVariable( entry.getKey(), entry.getValue() );
                }
            } else {
                throw new IllegalArgumentException( "This process does not support parameters!" );
            }
        }
        
        return processInstance;
	}
	

	
	public abstract ProcessInstance createProcessInstance();

}
