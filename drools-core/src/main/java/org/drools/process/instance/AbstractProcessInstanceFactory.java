package org.drools.process.instance;

import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.common.InternalWorkingMemory;
import org.drools.definition.process.Process;
import org.drools.process.core.ContextContainer;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.instance.context.variable.VariableScopeInstance;

public abstract class AbstractProcessInstanceFactory implements ProcessInstanceFactory {
	
	public ProcessInstance createProcessInstance(Process process,
			                                     WorkingMemory workingMemory,
			                                     Map<String, Object> parameters) {
		ProcessInstance processInstance = (ProcessInstance) createProcessInstance();
		processInstance.setWorkingMemory( (InternalWorkingMemory) workingMemory );
        processInstance.setProcess( process );
        
        // set variable default values
        // TODO: should be part of processInstanceImpl?
        VariableScope variableScope = (VariableScope) ((ContextContainer) process).getDefaultContext( VariableScope.VARIABLE_SCOPE );
        VariableScopeInstance variableScopeInstance = (VariableScopeInstance) processInstance.getContextInstance( VariableScope.VARIABLE_SCOPE );
        // set input parameters
        if ( parameters != null ) {
            if ( variableScope != null ) {
                for ( Map.Entry<String, Object> entry : parameters.entrySet() ) {
                    variableScopeInstance.setVariable( entry.getKey(),
                                                       entry.getValue() );
                }
            } else {
                throw new IllegalArgumentException( "This process does not support parameters!" );
            }
        }
        
        ((InternalWorkingMemory) workingMemory).getProcessInstanceManager()
        	.addProcessInstance( processInstance );
        return processInstance;
	}
	
	public abstract ProcessInstance createProcessInstance();

}
