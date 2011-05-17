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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.BPMN2ProcessFactory;
import org.drools.compiler.ProcessBuilderFactory;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.process.Process;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.marshalling.impl.ProcessMarshallerFactory;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.ProcessRuntimeFactory;
import org.jbpm.bpmn2.BPMN2ProcessProviderImpl;
import org.jbpm.marshalling.impl.ProcessMarshallerFactoryServiceImpl;
import org.jbpm.process.audit.ProcessInstanceDbLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.audit.WorkingMemoryDbLogger;
import org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.process.workitem.wsht.CommandBasedWSHumanTaskHandler;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;

public class CommandDelegate {
	
	private static StatefulKnowledgeSession ksession;
	
	public CommandDelegate() {
		getSession();
	}
	
	private StatefulKnowledgeSession newStatefulKnowledgeSession() {
		try {
			KnowledgeBase kbase = null;
			try {
				ResourceChangeScannerConfiguration sconf = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
				sconf.setProperty( "drools.resource.scanner.interval", "10" );
				ResourceFactory.getResourceChangeScannerService().configure( sconf );
				ResourceFactory.getResourceChangeScannerService().start();
				ResourceFactory.getResourceChangeNotifierService().start();
				KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
				aconf.setProperty("drools.agent.newInstance", "false");
				KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent("Guvnor default", aconf);
				kagent.applyChangeSet(ResourceFactory.newClassPathResource("ChangeSet.xml"));
				kbase = kagent.getKnowledgeBase();
				for (Process process: kbase.getProcesses()) {
					System.out.println("Loading process from Guvnor: " + process.getId());
				}
			} catch (Throwable t) {
				if (t instanceof RuntimeException
						&& "KnowledgeAgent exception while trying to deserialize".equals(t.getMessage())) {
					System.out.println("Could not connect to guvnor");
					if (t.getCause() != null) {
						System.out.println(t.getCause().getMessage());
					}
				}
				System.out.println("Could not load processes from guvnor: " + t.getMessage());
				t.printStackTrace();
			}
			if (kbase == null) {
				kbase = KnowledgeBaseFactory.newKnowledgeBase();
			}
			String directory = System.getProperty("jbpm.console.directory");
			if (directory == null) {
				System.out.println("jbpm.console.directory property not found");
			} else {
				File file = new File(directory);
				if (!file.exists()) {
					throw new IllegalArgumentException("Could not find " + directory);
				}
				if (!file.isDirectory()) {
					throw new IllegalArgumentException(directory + " is not a directory");
				}
				ProcessBuilderFactory.setProcessBuilderFactoryService(new ProcessBuilderFactoryServiceImpl());
				ProcessMarshallerFactory.setProcessMarshallerFactoryService(new ProcessMarshallerFactoryServiceImpl());
				ProcessRuntimeFactory.setProcessRuntimeFactoryService(new ProcessRuntimeFactoryServiceImpl());
				BPMN2ProcessFactory.setBPMN2ProcessProvider(new BPMN2ProcessProviderImpl());
				KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
				for (File subfile: file.listFiles(new FilenameFilter() {
						public boolean accept(File dir, String name) {
							return name.endsWith(".bpmn") || name.endsWith("bpmn2");
						}})) {
					System.out.println("Loading process from file system: " + subfile.getName());
					kbuilder.add(ResourceFactory.newFileResource(subfile), ResourceType.BPMN2);
				}
				kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
			}
			StatefulKnowledgeSession ksession = null;
			EntityManagerFactory emf = Persistence.createEntityManagerFactory(
					"org.jbpm.persistence.jpa");
	        Environment env = KnowledgeBaseFactory.newEnvironment();
	        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
			Properties properties = new Properties();
			properties.put("drools.processInstanceManagerFactory", "org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory");
			properties.put("drools.processSignalManagerFactory", "org.jbpm.persistence.processinstance.JPASignalManagerFactory");
			KnowledgeSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
			try {
				System.out.println("Loading session data ...");
                ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(
					1, kbase, config, env);
			} catch (RuntimeException e) {
				System.out.println("Error loading session data: " + e.getMessage());
				if (e instanceof IllegalStateException) {
				    Throwable cause = ((IllegalStateException) e).getCause();
				    if (cause instanceof InvocationTargetException) {
				        cause = cause.getCause();
	                    if (cause != null && "Could not find session data for id 1".equals(cause.getMessage())) {
	                        System.out.println("Creating new session data ...");
	            	        env = KnowledgeBaseFactory.newEnvironment();
	            	        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
	                        ksession = JPAKnowledgeService.newStatefulKnowledgeSession(
	                            kbase, config, env);
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
			properties = new Properties();
			try {
				properties.load(CommandDelegate.class.getResourceAsStream("/jbpm.console.properties"));
			} catch (IOException e) {
				throw new RuntimeException("Could not load jbpm.console.properties", e);
			}
			handler.setConnection(
				properties.getProperty("jbpm.console.task.service.host"),
				new Integer(properties.getProperty("jbpm.console.task.service.port")));
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
	
	public List<ProcessInstanceLog> getActiveProcessInstanceLogsByProcessId(String processId) {
		return ProcessInstanceDbLog.findActiveProcessInstances(processId);
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
	/**
	 * This method the variables provided in the map to the instance.
	 * NOTE: the map will be added not replaced
	 * @param processInstanceId
	 * @param variables
	 */
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
