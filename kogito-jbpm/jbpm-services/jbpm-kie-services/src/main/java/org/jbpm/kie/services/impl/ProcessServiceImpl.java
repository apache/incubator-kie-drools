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

package org.jbpm.kie.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.command.impl.RegistryContext;
import org.drools.core.command.runtime.process.SetProcessInstanceVariablesCommand;
import org.drools.core.command.runtime.process.StartProcessCommand;
import org.drools.core.process.instance.WorkItemManager;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.jbpm.process.instance.impl.util.VariableUtil;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.services.api.DeploymentNotFoundException;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessInstanceNotFoundException;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.WorkItemNotFoundException;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.api.model.NodeInstanceDesc;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.api.service.ServiceRegistry;
import org.jbpm.workflow.core.node.BoundaryEventNode;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.jbpm.workflow.instance.node.CompositeNodeInstance;
import org.jbpm.workflow.instance.node.EventNodeInstance;
import org.kie.api.command.Command;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.definition.process.Node;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.Context;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.runtime.manager.SessionNotFoundException;
import org.kie.internal.runtime.manager.context.CaseContext;
import org.kie.internal.runtime.manager.context.CorrelationKeyContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.internal.utils.LazyLoaded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessServiceImpl implements ProcessService, VariablesAware {

	private static final Logger logger = LoggerFactory.getLogger(ProcessServiceImpl.class);

	protected DeploymentService deploymentService;
	protected RuntimeDataService dataService;
	
	
    public ProcessServiceImpl() {
        ServiceRegistry.get().register(ProcessService.class.getSimpleName(), this);
    }

	public void setDeploymentService(DeploymentService deploymentService) {
		this.deploymentService = deploymentService;
	}

	public void setDataService(RuntimeDataService dataService) {
		this.dataService = dataService;
	}

	@Override
	public Long startProcess(String deploymentId, String processId) {

		return startProcess(deploymentId, processId, new HashMap<>());
	}

	@Override
	public Long startProcess(String deploymentId, String processId, Map<String, Object> params) {
		DeployedUnit deployedUnit = deploymentService.getDeployedUnit(deploymentId);
		if (deployedUnit == null) {
			throw new DeploymentNotFoundException("No deployments available for " + deploymentId);
		}
		if (!deployedUnit.isActive()) {
			throw new DeploymentNotFoundException("Deployments " + deploymentId + " is not active");
		}

		RuntimeManager manager = deployedUnit.getRuntimeManager();

		params = process(params, ((InternalRuntimeManager) manager).getEnvironment().getClassLoader());
        RuntimeEngine engine = manager.getRuntimeEngine(getContext(params));
        KieSession ksession = engine.getKieSession();
        ProcessInstance pi;
        try {
            pi = ksession.startProcess(processId, params);
            return pi.getId();
        } finally {
        	disposeRuntimeEngine(manager, engine);
        }
	}

	@Override
	public Long startProcess(String deploymentId, String processId, CorrelationKey correlationKey) {
		return startProcess(deploymentId, processId, correlationKey, new HashMap<>());
	}

	@Override
	public Long startProcess(String deploymentId, String processId, CorrelationKey correlationKey, Map<String, Object> params) {
		DeployedUnit deployedUnit = deploymentService.getDeployedUnit(deploymentId);
		if (deployedUnit == null) {
			throw new DeploymentNotFoundException("No deployments available for " + deploymentId);
		}
		if (!deployedUnit.isActive()) {
			throw new DeploymentNotFoundException("Deployments " + deploymentId + " is not active");
		}

		RuntimeManager manager = deployedUnit.getRuntimeManager();

		params = process(params, ((InternalRuntimeManager) manager).getEnvironment().getClassLoader());
        RuntimeEngine engine = manager.getRuntimeEngine(getContext(params));
        KieSession ksession = engine.getKieSession();
        ProcessInstance pi;
        try {
            pi = ((CorrelationAwareProcessRuntime)ksession).startProcess(processId, correlationKey, params);
            return pi.getId();
        } finally {
        	disposeRuntimeEngine(manager, engine);
        }
	}

	protected Context<?> getContext(Map<String, Object> params) {
	    if (params == null) {
	        return ProcessInstanceIdContext.get();
	    }
	    String caseId = (String) params.get(EnvironmentName.CASE_ID);
	    if (caseId != null && !caseId.isEmpty()) {
	        return CaseContext.get(caseId);
	    }

	    return ProcessInstanceIdContext.get();
	}

	@Override
    public void abortProcessInstance(Long processInstanceId) {
        ProcessInstanceDesc piDesc = dataService.getProcessInstanceById(processInstanceId);
        if (piDesc == null || piDesc.getState() != ProcessInstance.STATE_ACTIVE) {
            throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " was not found");
        }
        
        abortProcessInstance(piDesc.getDeploymentId(), processInstanceId);
	}
	
	@Override
	public void abortProcessInstance(String deploymentId, Long processInstanceId) {
		
		DeployedUnit deployedUnit = deploymentService.getDeployedUnit(deploymentId);
		if (deployedUnit == null) {
			throw new DeploymentNotFoundException("No deployments available for " + deploymentId);
		}
		RuntimeManager manager = deployedUnit.getRuntimeManager();
        RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
        try {
            KieSession ksession = engine.getKieSession();
            ksession.abortProcessInstance(processInstanceId);
        } catch(SessionNotFoundException e) {
            throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " was not found", e);
        } catch(IllegalArgumentException e) {
            throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " was not found", e);
        } finally {
        	disposeRuntimeEngine(manager, engine);
        }
	}
	
	@Override
    public void abortProcessInstances(List<Long> processInstanceIds) {
	    for (long processInstanceId : processInstanceIds) {
            abortProcessInstance(processInstanceId);
        }
	}

	@Override
	public void abortProcessInstances(String deploymentId, List<Long> processInstanceIds) {
		for (long processInstanceId : processInstanceIds) {
			abortProcessInstance(deploymentId, processInstanceId);
		}
	}
	
	@Override
    public void signalProcessInstance(Long processInstanceId, String signalName, Object event) {
        ProcessInstanceDesc piDesc = dataService.getProcessInstanceById(processInstanceId);
        if (piDesc == null || piDesc.getState() != ProcessInstance.STATE_ACTIVE) {
            throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " was not found");
        }
        
        signalProcessInstance(piDesc.getDeploymentId(), processInstanceId, signalName, event);
	}

	@Override
	public void signalProcessInstance(String deploymentId, Long processInstanceId, String signalName, Object event) {
		
		DeployedUnit deployedUnit = deploymentService.getDeployedUnit(deploymentId);
		if (deployedUnit == null) {
			throw new DeploymentNotFoundException("No deployments available for " + deploymentId);
		}
		RuntimeManager manager = deployedUnit.getRuntimeManager();
		event = process(event, ((InternalRuntimeManager) manager).getEnvironment().getClassLoader());
        RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
        try {
            KieSession ksession = engine.getKieSession();
            ksession.signalEvent(signalName, event, processInstanceId);
        } catch(SessionNotFoundException e) {
            throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " was not found", e);
        } finally {
        	disposeRuntimeEngine(manager, engine);
        }
	}

	@Override
	public void signalProcessInstances(List<Long> processInstanceIds, String signalName, Object event) {
		for (Long processInstanceId : processInstanceIds) {
			signalProcessInstance(processInstanceId, signalName, event);
		}

	}

	@Override
    public void signalProcessInstances(String deploymentId, List<Long> processInstanceIds, String signalName, Object event) {
        for (Long processInstanceId : processInstanceIds) {
            signalProcessInstance(deploymentId, processInstanceId, signalName, event);
        }

    }

    @Override
    public void signalEvent(String deploymentId, String signalName, Object event) {
        DeployedUnit deployedUnit = deploymentService.getDeployedUnit(deploymentId);
        if (deployedUnit == null) {
            throw new DeploymentNotFoundException("No deployments available for " + deploymentId);
        }
        RuntimeManager manager = deployedUnit.getRuntimeManager();
        event = process(event, ((InternalRuntimeManager) manager).getEnvironment().getClassLoader());
        manager.signalEvent(signalName, event);
    }
    
    @Override
    public ProcessInstance getProcessInstance(Long processInstanceId) {
        ProcessInstanceDesc piDesc = dataService.getProcessInstanceById(processInstanceId);
        if (piDesc == null) {
            return null;
        }
        
        return getProcessInstance(piDesc.getDeploymentId(), processInstanceId);
    }

	@Override
	public ProcessInstance getProcessInstance(String deploymentId, Long processInstanceId) {
		
		DeployedUnit deployedUnit = deploymentService.getDeployedUnit(deploymentId);
		if (deployedUnit == null) {
			throw new DeploymentNotFoundException("No deployments available for " + deploymentId);
		}
		RuntimeManager manager = deployedUnit.getRuntimeManager();
        RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
        try {
            KieSession ksession = engine.getKieSession();
            return ksession.getProcessInstance(processInstanceId);
        } catch(SessionNotFoundException e) {
            throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " was not found", e);
        } finally {
        	disposeRuntimeEngine(manager, engine);
        }
	}
	
	@Override
    public ProcessInstance getProcessInstance(CorrelationKey key) {
        ProcessInstanceDesc piDesc = dataService.getProcessInstanceByCorrelationKey(key);
        if (piDesc == null) {
            return null;
        }
        
        return getProcessInstance(piDesc.getDeploymentId(), key);
	}

	@Override
	public ProcessInstance getProcessInstance(String deploymentId, CorrelationKey key) {
		
		DeployedUnit deployedUnit = deploymentService.getDeployedUnit(deploymentId);
		if (deployedUnit == null) {
			throw new DeploymentNotFoundException("No deployments available for " + deploymentId);
		}
		RuntimeManager manager = deployedUnit.getRuntimeManager();
        RuntimeEngine engine = manager.getRuntimeEngine(CorrelationKeyContext.get(key));
        KieSession ksession = engine.getKieSession();
        try {
        	return ((CorrelationAwareProcessRuntime)ksession).getProcessInstance(key);
        } finally {
        	disposeRuntimeEngine(manager, engine);
        }
	}
	
	@Override
    public void setProcessVariable(Long processInstanceId, String variableId, Object value) {
        ProcessInstanceDesc piDesc = dataService.getProcessInstanceById(processInstanceId);
        if (piDesc == null || piDesc.getState() != ProcessInstance.STATE_ACTIVE) {
            throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " was not found");
        }
        
        setProcessVariable(piDesc.getDeploymentId(), processInstanceId, variableId, value);
	}

	@Override
	public void setProcessVariable(String deploymentId, Long processInstanceId, String variableId, Object value) {
		
		DeployedUnit deployedUnit = deploymentService.getDeployedUnit(deploymentId);
		if (deployedUnit == null) {
			throw new DeploymentNotFoundException("No deployments available for " + deploymentId);
		}
		RuntimeManager manager = deployedUnit.getRuntimeManager();
		value = process(value, ((InternalRuntimeManager) manager).getEnvironment().getClassLoader());
        RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
        try {
            KieSession ksession = engine.getKieSession();
            ProcessInstance pi = ksession.getProcessInstance(processInstanceId); 
            if (pi == null) {
                throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " was not found");
            }
            ksession.execute(new SetProcessInstanceVariablesCommand(processInstanceId, Collections.singletonMap(variableId, value)));
        } catch(SessionNotFoundException e) {
            throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " was not found", e);
        } finally {
        	disposeRuntimeEngine(manager, engine);
        }
	}
	
	@Override
    public void setProcessVariables(Long processInstanceId, Map<String, Object> variables) {
        ProcessInstanceDesc piDesc = dataService.getProcessInstanceById(processInstanceId);
        if (piDesc == null || piDesc.getState() != ProcessInstance.STATE_ACTIVE) {
            throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " was not found");
        }
        
        setProcessVariables(piDesc.getDeploymentId(), processInstanceId, variables);
	}

	@Override
	public void setProcessVariables(String deploymentId, Long processInstanceId, Map<String, Object> variables) {
		
		DeployedUnit deployedUnit = deploymentService.getDeployedUnit(deploymentId);
		if (deployedUnit == null) {
			throw new DeploymentNotFoundException("No deployments available for " + deploymentId);
		}
		RuntimeManager manager = deployedUnit.getRuntimeManager();
		variables = process(variables, ((InternalRuntimeManager) manager).getEnvironment().getClassLoader());
        RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
        try {
            KieSession ksession = engine.getKieSession();
            ProcessInstance pi = ksession.getProcessInstance(processInstanceId); 
            if (pi == null) {
                throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " was not found");
            }
            ksession.execute(new SetProcessInstanceVariablesCommand(processInstanceId, variables));
        } catch(SessionNotFoundException e) {
            throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " was not found", e);
        } finally {
        	disposeRuntimeEngine(manager, engine);
        }
	}
	
	@Override
    public Object getProcessInstanceVariable(Long processInstanceId, String variableName) {
        ProcessInstanceDesc piDesc = dataService.getProcessInstanceById(processInstanceId);
        if (piDesc == null || piDesc.getState() != ProcessInstance.STATE_ACTIVE) {
            throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " was not found");
        }
        
        return getProcessInstanceVariable(piDesc.getDeploymentId(), processInstanceId, variableName);
	}

	@Override
	public Object getProcessInstanceVariable(String deploymentId, Long processInstanceId, String variableName) {
		
		DeployedUnit deployedUnit = deploymentService.getDeployedUnit(deploymentId);
		if (deployedUnit == null) {
			throw new DeploymentNotFoundException("No deployments available for " + deploymentId);
		}
		RuntimeManager manager = deployedUnit.getRuntimeManager();
        RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
        try {
            KieSession ksession = engine.getKieSession();
           return ksession.execute(new ExecutableCommand<Object>() {

                private static final long serialVersionUID = -2693525229757876896L;

                @Override
                public Object execute(org.kie.api.runtime.Context context) {
                    KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
                    WorkflowProcessInstance pi = (WorkflowProcessInstance) ksession.getProcessInstance(processInstanceId, true);
                    if (pi == null) {
                        throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " was not found");
                    }
                    Object variable = pi.getVariable(variableName);
                    if (variable instanceof LazyLoaded<?>) {
                        ((LazyLoaded<?>) variable).load();
                    }
                    return variable;
                }
            });
        } catch(SessionNotFoundException e) {
            throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " was not found", e);
        } finally {
        	disposeRuntimeEngine(manager, engine);
        }
	}
	
	@Override
    public Map<String, Object> getProcessInstanceVariables(Long processInstanceId) {
        ProcessInstanceDesc piDesc = dataService.getProcessInstanceById(processInstanceId);
        if (piDesc == null || piDesc.getState() != ProcessInstance.STATE_ACTIVE) {
            throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " was not found");
        }
        
        return getProcessInstanceVariables(piDesc.getDeploymentId(), processInstanceId);                
	}

	@Override
	public Map<String, Object> getProcessInstanceVariables(String deploymentId, Long processInstanceId) {
		
		DeployedUnit deployedUnit = deploymentService.getDeployedUnit(deploymentId);
		if (deployedUnit == null) {
			throw new DeploymentNotFoundException("No deployments available for " + deploymentId);
		}
		RuntimeManager manager = deployedUnit.getRuntimeManager();
        RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
        try {
            KieSession ksession = engine.getKieSession();
            WorkflowProcessInstanceImpl pi = (WorkflowProcessInstanceImpl) ksession.getProcessInstance(processInstanceId, true);
            if (pi == null) {
                throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " was not found");
            }
        	Map<String, Object> variables = pi.getVariables();
        	
        	for (Object variable : variables.values()) {
        	    if (variable instanceof LazyLoaded<?>) {
                    ((LazyLoaded<?>) variable).load();
                }
        	}
        	
        	return variables;
        } catch(SessionNotFoundException e) {
            throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " was not found", e);
        } finally {
        	disposeRuntimeEngine(manager, engine);
        }
	}
	
	@Override
    public Collection<String> getAvailableSignals(Long processInstanceId) {
        ProcessInstanceDesc piDesc = dataService.getProcessInstanceById(processInstanceId);
        if (piDesc == null) {
            throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " was not found");
        }
        
        return getAvailableSignals(piDesc.getDeploymentId(), processInstanceId);                
	}

	@Override
	public Collection<String> getAvailableSignals(String deploymentId, Long processInstanceId) {
		
		DeployedUnit deployedUnit = deploymentService.getDeployedUnit(deploymentId);
		if (deployedUnit == null) {
			throw new DeploymentNotFoundException("No deployments available for " + deploymentId);
		}
		RuntimeManager manager = deployedUnit.getRuntimeManager();
        RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
        try {
            KieSession ksession = engine.getKieSession();
            ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
            Collection<String> activeSignals = new ArrayList<>();

            if (processInstance != null) {
                ((ProcessInstanceImpl) processInstance)
                        .setProcess(ksession.getKieBase().getProcess(processInstance.getProcessId()));
                Collection<NodeInstance> activeNodes = ((WorkflowProcessInstance) processInstance).getNodeInstances();
                Collection<String> activeBoundaryNodesSignals = getActiveBoundaryNodesSignals(processInstance, activeNodes);

                activeSignals.addAll(collectActiveSignals(activeNodes));
                activeSignals.addAll(activeBoundaryNodesSignals);
            }

            return activeSignals;
        } catch(SessionNotFoundException e) {
            throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " was not found", e);
        } finally {
        	disposeRuntimeEngine(manager, engine);
        }
	}
	
	@Override
    public void completeWorkItem(Long id, Map<String, Object> results) {
        NodeInstanceDesc nodeDesc = dataService.getNodeInstanceForWorkItem(id);
        if (nodeDesc == null) {
            throw new WorkItemNotFoundException("Work item with id " + id + " was not found");
        }
        
        completeWorkItem(nodeDesc.getDeploymentId(), nodeDesc.getProcessInstanceId(), id, results);
	}

	@Override
	public void completeWorkItem(String deploymentId, Long processInstanceId, Long id, Map<String, Object> results) {
		
		DeployedUnit deployedUnit = deploymentService.getDeployedUnit(deploymentId);
		if (deployedUnit == null) {
			throw new DeploymentNotFoundException("No deployments available for " + deploymentId);
		}
		
		RuntimeManager manager = deployedUnit.getRuntimeManager();
		results = process(results, ((InternalRuntimeManager) manager).getEnvironment().getClassLoader());
        RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
        try {
            KieSession ksession = engine.getKieSession();
            WorkItem workItem = ((WorkItemManager)ksession.getWorkItemManager()).getWorkItem(id);
            if (workItem == null) {
                throw new WorkItemNotFoundException("Work item with id " + id + " was not found");
            }
            ksession.getWorkItemManager().completeWorkItem(id, results);
        } catch(SessionNotFoundException e) {
            throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " was not found", e);
        } finally {
        	disposeRuntimeEngine(manager, engine);
        }

	}
	
	@Override
    public void abortWorkItem(Long id) {
        NodeInstanceDesc nodeDesc = dataService.getNodeInstanceForWorkItem(id);
        if (nodeDesc == null) {
            throw new WorkItemNotFoundException("Work item with id " + id + " was not found");
        }
        
        abortWorkItem(nodeDesc.getDeploymentId(), nodeDesc.getProcessInstanceId(), id);
	}

	@Override
	public void abortWorkItem(String deploymentId, Long processInstanceId, Long id) {
		
		DeployedUnit deployedUnit = deploymentService.getDeployedUnit(deploymentId);
		if (deployedUnit == null) {
			throw new DeploymentNotFoundException("No deployments available for " + deploymentId);
		}
		
		RuntimeManager manager = deployedUnit.getRuntimeManager();
        RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
        try {
            KieSession ksession = engine.getKieSession();
            WorkItem workItem = ((WorkItemManager)ksession.getWorkItemManager()).getWorkItem(id);
            if (workItem == null) {
                throw new WorkItemNotFoundException("Work item with id " + id + " was not found");
            }
            ksession.getWorkItemManager().abortWorkItem(id);
        } catch(SessionNotFoundException e) {
            throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " was not found", e);
        } finally {
        	disposeRuntimeEngine(manager, engine);
        }
	}
	
	@Override
    public WorkItem getWorkItem(Long id) {
        NodeInstanceDesc nodeDesc = dataService.getNodeInstanceForWorkItem(id);
        if (nodeDesc == null) {
            throw new WorkItemNotFoundException("Work item with id " + id + " was not found");
        }
        
        return getWorkItem(nodeDesc.getDeploymentId(), nodeDesc.getProcessInstanceId(), id);
	}

	@Override
	public WorkItem getWorkItem(String deploymentId, Long processInstanceId, Long id) {
		
		DeployedUnit deployedUnit = deploymentService.getDeployedUnit(deploymentId);
		if (deployedUnit == null) {
			throw new DeploymentNotFoundException("No deployments available for " + deploymentId);
		}		
		RuntimeManager manager = deployedUnit.getRuntimeManager();
        RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
        try {
            KieSession ksession = engine.getKieSession();
            WorkItem workItem = ((WorkItemManager)ksession.getWorkItemManager()).getWorkItem(id);
            if (workItem == null) {
                throw new WorkItemNotFoundException("Work item with id " + id + " was not found");
            }
            return workItem;
        } catch(SessionNotFoundException e) {
            throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " was not found", e);
        } finally {
        	disposeRuntimeEngine(manager, engine);
        }
	}
	
	@Override
    public List<WorkItem> getWorkItemByProcessInstance(Long processInstanceId) {
        ProcessInstanceDesc piDesc = dataService.getProcessInstanceById(processInstanceId);
        if (piDesc == null || piDesc.getState() != ProcessInstance.STATE_ACTIVE) {
            throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " was not found");
        }
        
        return getWorkItemByProcessInstance(piDesc.getDeploymentId(), processInstanceId);
	}

	@Override
	public List<WorkItem> getWorkItemByProcessInstance(String deploymentId, Long processInstanceId) {
		
		DeployedUnit deployedUnit = deploymentService.getDeployedUnit(deploymentId);
		if (deployedUnit == null) {
			throw new DeploymentNotFoundException("No deployments available for " + deploymentId);
		}
		RuntimeManager manager = deployedUnit.getRuntimeManager();
        RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
        try {
            KieSession ksession = engine.getKieSession();
            List<WorkItem> workItems = new ArrayList<>();

        	Collection<NodeInstanceDesc> nodes = dataService.getProcessInstanceHistoryActive(processInstanceId, null);

        	for (NodeInstanceDesc node : nodes) {
        		if (node.getWorkItemId() != null) {
        			workItems.add(((WorkItemManager)ksession.getWorkItemManager()).getWorkItem(node.getWorkItemId()));
        		}
        	}

        	return workItems;
        } catch(SessionNotFoundException e) {
            throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " was not found", e);
        } finally {
        	disposeRuntimeEngine(manager, engine);
        }
	}

	@Override
	public <T> T execute(String deploymentId, Command<T> command) {
		Long processInstanceId = CommonUtils.getProcessInstanceId(command);
		logger.debug("Executing command {} with process instance id {} as contextual data", command, processInstanceId);

		DeployedUnit deployedUnit = deploymentService.getDeployedUnit(deploymentId);
		if (deployedUnit == null) {
			throw new DeploymentNotFoundException("No deployments available for " + deploymentId);
		}
		disallowWhenNotActive(deployedUnit, command);

		RuntimeManager manager = deployedUnit.getRuntimeManager();
        RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
        try {
            KieSession ksession = engine.getKieSession();
            return ksession.execute(command);
        } catch(SessionNotFoundException e) {
            throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " was not found", e);
        } finally {
        	disposeRuntimeEngine(manager, engine);
        }
	}


	@Override
	public <T> T execute(String deploymentId, Context<?> context, Command<T> command) {
		DeployedUnit deployedUnit = deploymentService.getDeployedUnit(deploymentId);
		if (deployedUnit == null) {
			throw new DeploymentNotFoundException("No deployments available for " + deploymentId);
		}
		disallowWhenNotActive(deployedUnit, command);

		RuntimeManager manager = deployedUnit.getRuntimeManager();
        RuntimeEngine engine = manager.getRuntimeEngine(context);
        try {
            KieSession ksession = engine.getKieSession();
            return ksession.execute(command);
        } catch(SessionNotFoundException e) {
            throw new ProcessInstanceNotFoundException("Process instance with context id " + context.getContextId() + " was not found", e);
        } finally {
        	disposeRuntimeEngine(manager, engine);
        }
	}

	protected void disallowWhenNotActive(DeployedUnit deployedUnit, Command<?> cmd) {
		if (!deployedUnit.isActive() &&
				cmd instanceof StartProcessCommand) {
			throw new DeploymentNotFoundException("Deployments " + deployedUnit.getDeploymentUnit().getIdentifier() + " is not active");
		}
	}


	protected Collection<String> collectActiveSignals(
			Collection<NodeInstance> activeNodes) {
		Collection<String> activeNodesComposite = new ArrayList<>();
		for (NodeInstance nodeInstance : activeNodes) {
			if (nodeInstance instanceof EventNodeInstance) {
				String type = ((EventNodeInstance) nodeInstance).getEventNode().getType();
				if (type != null && !type.startsWith("Message-")) {
					activeNodesComposite.add(VariableUtil.resolveVariable(type, nodeInstance));
				}

			}
			if (nodeInstance instanceof CompositeNodeInstance) {
				Collection<NodeInstance> currentNodeInstances = ((CompositeNodeInstance) nodeInstance).getNodeInstances();

				// recursively check current nodes
				activeNodesComposite
						.addAll(collectActiveSignals(currentNodeInstances));
			}
		}

		return activeNodesComposite;
	}

    protected List<String> getActiveBoundaryNodesSignals(ProcessInstance processInstance, Collection<NodeInstance> activeNodes) {
        WorkflowProcessInstance workflowProcessInstance = (WorkflowProcessInstance) processInstance;
        ArrayList<Node> processNodesList = new ArrayList(Arrays.asList(((RuleFlowProcessInstance) workflowProcessInstance).getNodeContainer().getNodes()));
        List<String> activeBoundaryNodesSignals = new ArrayList<>();

        Map<String, NodeInstance> uniqueIdNodeInstanceMap = new HashMap<>();
        for(NodeInstance activeNode : activeNodes) {
            uniqueIdNodeInstanceMap.put((String) activeNode.getNode().getMetaData().get("UniqueId"), activeNode);
        }


        try {
            for(Node processNode : processNodesList) {
                if(processNode instanceof BoundaryEventNode && uniqueIdNodeInstanceMap.containsKey(((BoundaryEventNode) processNode).getAttachedToNodeId())) {
                    activeBoundaryNodesSignals.add(VariableUtil.resolveVariable((String) processNode.getMetaData().get("SignalName"),
                                                                                uniqueIdNodeInstanceMap.get(((BoundaryEventNode) processNode).getAttachedToNodeId())));
                }
            }

            return activeBoundaryNodesSignals;
        } catch(Exception e) {
            logger.debug("Unable to retrieve boundary event nodes for active nodes in processInstance {}", processInstance.getId());
            return new ArrayList<>();
        }
    }

	@Override
	public <T> T process(T variables, ClassLoader cl) {
		// do nothing here as there is no need to process variables
		return variables;
	}

	protected void disposeRuntimeEngine(RuntimeManager manager, RuntimeEngine engine) {
		manager.disposeRuntimeEngine(engine);
	}


}
