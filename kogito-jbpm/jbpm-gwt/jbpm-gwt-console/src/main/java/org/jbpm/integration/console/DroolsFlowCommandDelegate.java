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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.process.Process;
import org.drools.io.ResourceFactory;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.process.audit.ProcessInstanceDbLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.audit.WorkingMemoryDbLogger;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.process.workitem.wsht.CommandBasedWSHumanTaskHandler;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;

public class DroolsFlowCommandDelegate {
	
	private static StatefulKnowledgeSession ksession;
	
	public DroolsFlowCommandDelegate() {
		getSession();
	}
	
	private StatefulKnowledgeSession newStatefulKnowledgeSession() {
		try {
			KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent("Guvnor default");
			kagent.applyChangeSet(ResourceFactory.newClassPathResource("ChangeSet.xml"));
			kagent.monitorResourceChangeEvents(false);
			KnowledgeBase kbase = kagent.getKnowledgeBase();
			StatefulKnowledgeSession ksession = null;
			EntityManagerFactory emf = Persistence.createEntityManagerFactory(
					"org.drools.persistence.jpa");
	        Environment env = KnowledgeBaseFactory.newEnvironment();
	        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
			try {
				System.out.println("Loading session data ...");
                ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(
					1, kbase, null, env);
			} catch (RuntimeException e) {
				System.out.println("Error loading session data: " + e.getMessage());
				if (e instanceof IllegalStateException) {
				    Throwable cause = ((IllegalStateException) e).getCause();
				    if (cause instanceof InvocationTargetException) {
				        cause = cause.getCause();
	                    if (cause != null && "Could not find session data for id 1".equals(cause.getMessage())) {
	                        System.out.println("Creating new session data ...");
	                        ksession = JPAKnowledgeService.newStatefulKnowledgeSession(
	                            kbase, null, env);
	                    } else {
	                        System.err.println("Error loading session data: " + cause);
	                        throw e;
	                    }
				    } else {
                        System.err.println("Error loading session data: " + cause);
    					throw e;
    				}
				} else {
                    System.err.println("Error loading session data: " + e.getMessage());
                    throw e;
				}
			}
			new WorkingMemoryDbLogger(ksession);
			CommandBasedWSHumanTaskHandler handler = new CommandBasedWSHumanTaskHandler(ksession);
			ksession.getWorkItemManager().registerWorkItemHandler(
				"Human Task", handler);
			handler.connect();
			System.out.println("Successfully loaded default package from Guvnor");
			return ksession;
		} catch (Throwable t) {
			throw new RuntimeException(
				"Could not initialize stateful knowledge session: "
					+ t.getMessage(), t);
		}
	}
	
	private StatefulKnowledgeSession getSession() {
		if (ksession == null) {
			ksession = newStatefulKnowledgeSession();
		}
		return ksession;
	}
	
	public List<Process> getProcesses() {
		List<Process> result = new ArrayList<Process>();
		for (KnowledgePackage kpackage: getSession().getKnowledgeBase().getKnowledgePackages()) {
			result.addAll(kpackage.getProcesses());
		}
		return result;
	}
	
	public Process getProcess(String processId) {
		for (KnowledgePackage kpackage: getSession().getKnowledgeBase().getKnowledgePackages()) {
			for (Process process: kpackage.getProcesses()) {
				if (processId.equals(process.getId())) {
					return process;
				}
			}
		}
		return null;
	}
	
	public Process getProcessByName(String name) {
		for (KnowledgePackage kpackage: getSession().getKnowledgeBase().getKnowledgePackages()) {
			for (Process process: kpackage.getProcesses()) {
				if (name.equals(process.getName())) {
					return process;
				}
			}
		}
		return null;
	}

	public void removeProcess(String processId) {
		throw new UnsupportedOperationException();
	}
	
	public ProcessInstanceLog getProcessInstanceLog(String processInstanceId) {
		return ProcessInstanceDbLog.findProcessInstance(new Long(processInstanceId));
	}

	public List<ProcessInstanceLog> getProcessInstanceLogsByProcessId(String processId) {
		return ProcessInstanceDbLog.findProcessInstances(processId);
	}
	
	public ProcessInstanceLog startProcess(String processId, Map<String, Object> parameters) {
		long processInstanceId = ksession.startProcess(processId, parameters).getId();
		return ProcessInstanceDbLog.findProcessInstance(processInstanceId);
	}
	
	public void abortProcessInstance(String processInstanceId) {
		ProcessInstance processInstance = ksession.getProcessInstance(new Long(processInstanceId));
		if (processInstance != null) {
			ksession.abortProcessInstance(new Long(processInstanceId));
		} else {
			throw new IllegalArgumentException("Could not find process instance " + processInstanceId);
		}
	}
	
	public Map<String, Object> getProcessInstanceVariables(String processInstanceId) {
		ProcessInstance processInstance = ksession.getProcessInstance(new Long(processInstanceId));
		if (processInstance != null) {
		    Map<String, Object> variables = 
		        ((WorkflowProcessInstanceImpl) processInstance).getVariables();
            if (variables == null) {
				return new HashMap<String, Object>();
			}
			// filter out null values
			Map<String, Object> result = new HashMap<String, Object>();
			for (Map.Entry<String, Object> entry: variables.entrySet()) {
				if (entry.getValue() != null) {
					result.put(entry.getKey(), entry.getValue());
				}
			}
			return result;
		} else {
			throw new IllegalArgumentException("Could not find process instance " + processInstanceId);
		}
	}
	
	public void setProcessInstanceVariables(String processInstanceId, Map<String, Object> variables) {
		ProcessInstance processInstance = ksession.getProcessInstance(new Long(processInstanceId));
		if (processInstance != null) {
			VariableScopeInstance variableScope = (VariableScopeInstance) 
				((org.jbpm.process.instance.ProcessInstance) processInstance)
					.getContextInstance(VariableScope.VARIABLE_SCOPE);
			if (variableScope == null) {
				throw new IllegalArgumentException(
					"Could not find variable scope for process instance " + processInstanceId);
			}
			for (Map.Entry<String, Object> entry: variables.entrySet()) {
				variableScope.setVariable(entry.getKey(), entry.getValue());
			}
		} else {
			throw new IllegalArgumentException("Could not find process instance " + processInstanceId);
		}
	}
	
	public void signalExecution(String executionId, String signal) {
		ksession.getProcessInstance(new Long(executionId))
			.signalEvent("signal", signal);
	}

}
