/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.integration.console;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.definition.process.Process;
import org.jboss.bpm.console.client.model.ProcessDefinitionRef;
import org.jboss.bpm.console.client.model.ProcessInstanceRef;
import org.jboss.bpm.console.client.model.ProcessInstanceRef.RESULT;
import org.jboss.bpm.console.client.model.ProcessInstanceRef.STATE;
import org.jbpm.process.audit.ProcessInstanceLog;

public class ProcessManagement implements org.jboss.bpm.console.server.integration.ProcessManagement {

	private CommandDelegate delegate;
	
    public ProcessManagement() {
        delegate = new CommandDelegate();
    }
    
	public List<ProcessDefinitionRef> getProcessDefinitions() {
		List<Process> processes = delegate.getProcesses();
		List<ProcessDefinitionRef> result = new ArrayList<ProcessDefinitionRef>();
		for (Process process: processes) {
			result.add(Transform.processDefinition(process));
		}
		return result;
	}

	public ProcessDefinitionRef getProcessDefinition(String definitionId) {
		Process process = delegate.getProcess(definitionId);
		return Transform.processDefinition(process);
	}

	/**
	 * method unsupported
	 */
	public List<ProcessDefinitionRef> removeProcessDefinition(String definitionId) {
		delegate.removeProcess(definitionId); 
	    return getProcessDefinitions();
	}

	public ProcessInstanceRef getProcessInstance(String instanceId) {
		ProcessInstanceLog processInstance = delegate.getProcessInstanceLog(instanceId);
		return Transform.processInstance(processInstance);
	}

	public List<ProcessInstanceRef> getProcessInstances(String definitionId) {
		List<ProcessInstanceLog> processInstances = delegate.getActiveProcessInstanceLogsByProcessId(definitionId);
		List<ProcessInstanceRef> result = new ArrayList<ProcessInstanceRef>();
		for (ProcessInstanceLog processInstance: processInstances) {
			result.add(Transform.processInstance(processInstance));
		}
		return result;
	}

	public ProcessInstanceRef newInstance(String definitionId) {
		ProcessInstanceLog processInstance = delegate.startProcess(definitionId, null);
		return Transform.processInstance(processInstance);
	}
	
	public ProcessInstanceRef newInstance(String definitionId, Map<String, Object> processVars) {
		ProcessInstanceLog processInstance = delegate.startProcess(definitionId, processVars);
		return Transform.processInstance(processInstance);
	}

	public void setProcessState(String instanceId, STATE nextState) {
		if (nextState == STATE.ENDED) {
			delegate.abortProcessInstance(instanceId);
		} else {
			throw new UnsupportedOperationException();
		}
	}
	
	public Map<String, Object> getInstanceData(String instanceId) {
		return delegate.getProcessInstanceVariables(instanceId);
	}

	public void setInstanceData(String instanceId, Map<String, Object> data) {
		delegate.setProcessInstanceVariables(instanceId, data);
	}

	
	public void signalExecution(String executionId, String signal) {
		delegate.signalExecution(executionId, signal);
	}

	public void deleteInstance(String instanceId) {
		delegate.abortProcessInstance(instanceId);
	}

	//result means nothing
	public void endInstance(String instanceId, RESULT result) {
		delegate.abortProcessInstance(instanceId);
	}

}