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

package org.jbpm.kie.services.impl.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.kie.services.impl.admin.commands.CancelNodeInstanceCommand;
import org.jbpm.kie.services.impl.admin.commands.ListNodesCommand;
import org.jbpm.kie.services.impl.admin.commands.ListTimersCommand;
import org.jbpm.kie.services.impl.admin.commands.RetriggerNodeInstanceCommand;
import org.jbpm.kie.services.impl.admin.commands.TriggerNodeCommand;
import org.jbpm.process.instance.command.RelativeUpdateTimerCommand;
import org.jbpm.process.instance.command.UpdateTimerCommand;
import org.jbpm.runtime.manager.impl.AbstractRuntimeManager;
import org.jbpm.services.api.NodeInstanceNotFoundException;
import org.jbpm.services.api.NodeNotFoundException;
import org.jbpm.services.api.ProcessInstanceNotFoundException;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.admin.ExecutionErrorNotFoundException;
import org.jbpm.services.api.admin.ProcessInstanceAdminService;
import org.jbpm.services.api.admin.ProcessNode;
import org.jbpm.services.api.admin.TimerInstance;
import org.jbpm.services.api.model.NodeInstanceDesc;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.api.service.ServiceRegistry;
import org.jbpm.shared.services.impl.QueryManager;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.shared.services.impl.commands.QueryNameCommand;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.query.QueryContext;
import org.kie.internal.identity.IdentityProvider;
import org.kie.internal.runtime.error.ExecutionError;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

public class ProcessInstanceAdminServiceImpl implements ProcessInstanceAdminService {

    private ProcessService processService;    
    private RuntimeDataService runtimeDataService;
    private IdentityProvider identityProvider;
    private TransactionalCommandService commandService;
    
    
    public ProcessInstanceAdminServiceImpl() {
        ServiceRegistry.get().register(ProcessInstanceAdminService.class.getSimpleName(), this);
    }
    
    public void setProcessService(ProcessService processService) {
        this.processService = processService;
    }
    
    public void setRuntimeDataService(RuntimeDataService runtimeDataService) {
        this.runtimeDataService = runtimeDataService;
    }

    public void setIdentityProvider(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }
    
    public void setCommandService(TransactionalCommandService commandService) {
        this.commandService = commandService;
    }

    @Override
    public Collection<ProcessNode> getProcessNodes(long processInstanceId) throws ProcessInstanceNotFoundException {
        ProcessInstanceDesc pi = runtimeDataService.getProcessInstanceById(processInstanceId);
        if (pi == null) {
            throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " not found");
        }
        
        Collection<ProcessNode> nodes = processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(processInstanceId), new ListNodesCommand(processInstanceId));
        return nodes;
    }

    @Override
    public void cancelNodeInstance(long processInstanceId, long nodeInstanceId) throws NodeInstanceNotFoundException, ProcessInstanceNotFoundException {
        ProcessInstanceDesc pi = runtimeDataService.getProcessInstanceById(processInstanceId);
        if (pi == null) {
            throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " not found");
        }
        
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(processInstanceId), new CancelNodeInstanceCommand(processInstanceId, nodeInstanceId));
    }

    @Override
    public void retriggerNodeInstance(long processInstanceId, long nodeInstanceId) throws NodeInstanceNotFoundException, ProcessInstanceNotFoundException {
        ProcessInstanceDesc pi = runtimeDataService.getProcessInstanceById(processInstanceId);
        if (pi == null) {
            throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " not found");
        }
        
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(processInstanceId), new RetriggerNodeInstanceCommand(processInstanceId, nodeInstanceId));
    }

    @Override
    public void updateTimer(long processInstanceId, long timerId, long delay, long period, int repeatLimit) throws NodeInstanceNotFoundException, ProcessInstanceNotFoundException {
        ProcessInstanceDesc pi = runtimeDataService.getProcessInstanceById(processInstanceId);
        if (pi == null) {
            throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " not found");
        }
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(processInstanceId), new UpdateTimerCommand(processInstanceId, timerId, delay, period, repeatLimit));
    }

    @Override
    public void updateTimerRelative(long processInstanceId, long timerId, long delay, long period, int repeatLimit) throws NodeInstanceNotFoundException, ProcessInstanceNotFoundException {
        ProcessInstanceDesc pi = runtimeDataService.getProcessInstanceById(processInstanceId);
        if (pi == null) {
            throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " not found");
        }
        
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(processInstanceId), new RelativeUpdateTimerCommand(processInstanceId, timerId, delay, period, repeatLimit));
    }

    @Override
    public Collection<TimerInstance> getTimerInstances(long processInstanceId) throws ProcessInstanceNotFoundException {
        ProcessInstanceDesc pi = runtimeDataService.getProcessInstanceById(processInstanceId);
        if (pi == null) {
            throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " not found");
        }
        Collection<TimerInstance> timers = processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(processInstanceId), new ListTimersCommand(processInstanceId));
        return timers;
    }

    @Override
    public void triggerNode(long processInstanceId, long nodeId) throws NodeNotFoundException, ProcessInstanceNotFoundException {
        ProcessInstanceDesc pi = runtimeDataService.getProcessInstanceById(processInstanceId);
        if (pi == null) {
            throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " not found");
        }
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(processInstanceId), new TriggerNodeCommand(processInstanceId, nodeId));
    }

    @Override
    public Collection<NodeInstanceDesc> getActiveNodeInstances(long processInstanceId) throws ProcessInstanceNotFoundException {
        return runtimeDataService.getProcessInstanceHistoryActive(processInstanceId, new QueryContext(0, 1000));
    }
    
    @Override
    public void acknowledgeError(String... errorId) throws ExecutionErrorNotFoundException {
        for (String error : errorId) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("errorId", error);               
            params.put("ack", new Short("0"));
            List<ExecutionError> execErrors = commandService.execute(new QueryNameCommand<List<ExecutionError>>("getErrorById",params));
            
            if (execErrors.isEmpty()) {
                throw new ExecutionErrorNotFoundException("No execution error found for id " + errorId);
            }
            
            ExecutionError errorInstance = execErrors.get(0);
            RuntimeManager runtimeManager = RuntimeManagerRegistry.get().getManager(errorInstance.getDeploymentId());
            if (runtimeManager != null) {
                ((AbstractRuntimeManager) runtimeManager).getExecutionErrorManager().getStorage().acknowledge(identityProvider.getName(), errorInstance.getErrorId());
            }
        }
    }
    
    
    @Override
    public ExecutionError getError(String errorId) throws ExecutionErrorNotFoundException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("errorId", errorId);               
        List<ExecutionError> execErrors = commandService.execute(new QueryNameCommand<List<ExecutionError>>("getErrorByIdSkipAckCheck",params));
        
        if (execErrors.isEmpty()) {
            throw new ExecutionErrorNotFoundException("No execution error found for id " + errorId);
        }
        ExecutionError error = execErrors.get(0);
        return error;
    }
    
    @Override
    public List<ExecutionError> getErrors(boolean includeAcknowledged, QueryContext queryContext) {
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ack", getAckMode(includeAcknowledged));
        applyQueryContext(params, queryContext);        
        
        List<ExecutionError> execErrors = commandService.execute(new QueryNameCommand<List<ExecutionError>>("getErrors",params));
        return execErrors;
    }


    @Override
    public List<ExecutionError> getErrorsByProcessId(String deploymentId, String processId, boolean includeAcknowledged, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("deploymentId", deploymentId);
        params.put("processId", processId);
        params.put("ack", getAckMode(includeAcknowledged));
        applyQueryContext(params, queryContext);        
        
        List<ExecutionError> execErrors = commandService.execute(new QueryNameCommand<List<ExecutionError>>("getErrorsByProcessId",params));
        return execErrors;
    }

    @Override
    public List<ExecutionError> getErrorsByProcessInstanceId(long processInstanceId, boolean includeAcknowledged, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("processInstanceId", processInstanceId);
        params.put("ack", getAckMode(includeAcknowledged));
        applyQueryContext(params, queryContext);        
        
        List<ExecutionError> execErrors = commandService.execute(new QueryNameCommand<List<ExecutionError>>("getErrorsByProcessInstanceId",params));
        return execErrors;
    }

    @Override
    public List<ExecutionError> getErrorsByProcessInstanceId(long processInstanceId, String nodeName, boolean includeAcknowledged, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("processInstanceId", processInstanceId);
        params.put("nodeName", nodeName);
        params.put("ack", getAckMode(includeAcknowledged));
        applyQueryContext(params, queryContext);        
        
        List<ExecutionError> execErrors = commandService.execute(new QueryNameCommand<List<ExecutionError>>("getErrorsByProcessInstanceIdNodeName",params));
        return execErrors;
    }
    
    /*
     * Helper methods
     */
    
    protected void applyQueryContext(Map<String, Object> params, QueryContext queryContext) {
        if (queryContext != null) {
            params.put("firstResult", queryContext.getOffset());
            params.put("maxResults", queryContext.getCount());

            if (queryContext.getOrderBy() != null && !queryContext.getOrderBy().isEmpty()) {
                params.put(QueryManager.ORDER_BY_KEY, queryContext.getOrderBy());

                if (queryContext.isAscending()) {
                    params.put(QueryManager.ASCENDING_KEY, "true");
                } else {
                    params.put(QueryManager.DESCENDING_KEY, "true");
                }
            }
        }
    }
    
    protected List<Short> getAckMode(boolean includeAcknowledged) {
        List<Short> ackMode = new ArrayList<>();
        ackMode.add(new Short("0"));
        if (includeAcknowledged) {
            ackMode.add(new Short("1"));
        }
        
        return ackMode;
    }

}
