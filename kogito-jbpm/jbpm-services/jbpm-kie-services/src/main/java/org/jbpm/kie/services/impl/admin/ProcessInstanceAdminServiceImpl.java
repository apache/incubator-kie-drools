/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.kie.services.impl.admin;

import java.util.Collection;

import org.jbpm.kie.services.impl.admin.commands.CancelNodeInstanceCommand;
import org.jbpm.kie.services.impl.admin.commands.ListNodesCommand;
import org.jbpm.kie.services.impl.admin.commands.ListTimersCommand;
import org.jbpm.kie.services.impl.admin.commands.RetriggerNodeInstanceCommand;
import org.jbpm.kie.services.impl.admin.commands.TriggerNodeCommand;
import org.jbpm.process.instance.command.RelativeUpdateTimerCommand;
import org.jbpm.process.instance.command.UpdateTimerCommand;
import org.jbpm.services.api.NodeInstanceNotFoundException;
import org.jbpm.services.api.NodeNotFoundException;
import org.jbpm.services.api.ProcessInstanceNotFoundException;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.admin.ProcessInstanceAdminService;
import org.jbpm.services.api.admin.ProcessNode;
import org.jbpm.services.api.admin.TimerInstance;
import org.jbpm.services.api.model.NodeInstanceDesc;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.kie.api.runtime.query.QueryContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

public class ProcessInstanceAdminServiceImpl implements ProcessInstanceAdminService {

    private ProcessService processService;    
    private RuntimeDataService runtimeDataService;
    
    public void setProcessService(ProcessService processService) {
        this.processService = processService;
    }
    
    public void setRuntimeDataService(RuntimeDataService runtimeDataService) {
        this.runtimeDataService = runtimeDataService;
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

}
