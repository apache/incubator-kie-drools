/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.services.event.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEvent;
import org.kie.api.event.process.ProcessNodeEvent;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessWorkItemTransitionEvent;
import org.kie.api.runtime.process.HumanTaskWorkItem;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventBatch;
import org.kie.kogito.services.event.ProcessInstanceDataEvent;
import org.kie.kogito.services.event.UserTaskInstanceDataEvent;


public class ProcessInstanceEventBatch implements EventBatch {
    
    private final String service;
    private List<ProcessEvent> rawEvents = new ArrayList<>();

    public ProcessInstanceEventBatch(String service) {
        this.service = service;
    }

    @Override
    public void append(Object rawEvent) {
        if (rawEvent instanceof ProcessEvent) {
            rawEvents.add((ProcessEvent) rawEvent);
        }
    }

    @Override
    public Collection<DataEvent<?>> events() {
        Map<String, ProcessInstanceEventBody> processInstances = new LinkedHashMap<>();
        Map<String, UserTaskInstanceEventBody> userTaskInstances = new LinkedHashMap<>();
        
        for (ProcessEvent event : rawEvents) {
            ProcessInstanceEventBody body = processInstances.computeIfAbsent(event.getProcessInstance().getId(), key -> create(event));
            
            if (event instanceof ProcessNodeTriggeredEvent) {
                
                NodeInstanceEventBody nodeInstanceBody = create((ProcessNodeEvent)event);
                if (!body.getNodeInstances().contains(nodeInstanceBody)) {
                    // add it only if it does not exist
                    body.update().nodeInstance(nodeInstanceBody);
                }
            } else if (event instanceof ProcessNodeLeftEvent) {
                
                NodeInstanceEventBody nodeInstanceBody = create((ProcessNodeEvent)event);
                // if it's already there, remove it
                body.getNodeInstances().remove(nodeInstanceBody);
                // and add it back as the node left event has latest information
                body.update().nodeInstance(nodeInstanceBody);                
            } else if (event instanceof ProcessCompletedEvent) {
                // in case this is a process complete event always updated and date and state 
                body.update()
                .endDate(((WorkflowProcessInstance)event.getProcessInstance()).getEndDate())
                .state(event.getProcessInstance().getState());
            } else if (event instanceof ProcessWorkItemTransitionEvent) {
                ProcessWorkItemTransitionEvent workItemTransitionEvent = (ProcessWorkItemTransitionEvent) event;
                WorkItem workItem = workItemTransitionEvent.getWorkItem();
                if (workItem instanceof HumanTaskWorkItem && workItemTransitionEvent.isTransitioned()) {
                    userTaskInstances.putIfAbsent(workItem.getId(), createUserTask(workItemTransitionEvent));                    
                }
            }
            
        }
        
        Collection<DataEvent<?>> processedEvents = new ArrayList<>();
                
        processInstances.values().stream().map(pi -> new ProcessInstanceDataEvent(extractProcessId(pi.metaData()), pi.metaData(), pi)).forEach(processedEvents::add);
        userTaskInstances.values().stream().map(pi -> new UserTaskInstanceDataEvent(extractProcessId(pi.metaData()), pi.metaData(), pi)).forEach(processedEvents::add);
        
        return processedEvents;
    }

    private UserTaskInstanceEventBody createUserTask(ProcessWorkItemTransitionEvent workItemTransitionEvent) {
        WorkflowProcessInstance pi = (WorkflowProcessInstance) workItemTransitionEvent.getProcessInstance();
        HumanTaskWorkItem workItem = (HumanTaskWorkItem) workItemTransitionEvent.getWorkItem();
        return UserTaskInstanceEventBody.create()
                .id(workItem.getId())
                .state(workItem.getPhaseStatus())
                .taskName(workItem.getTaskName())
                .taskDescription(workItem.getTaskDescription())
                .taskPriority(workItem.getTaskPriority())
                .referenceName(workItem.getReferenceName())
                .actualOwner(workItem.getActualOwner())
                .startDate(workItem.getStartDate())
                .completeDate(workItem.getCompleteDate())
                .adminGroups(workItem.getAdminGroups())
                .adminUsers(workItem.getAdminUsers())
                .excludedUsers(workItem.getExcludedUsers())
                .potentialGroups(workItem.getPotentialGroups())
                .potentialUsers(workItem.getPotentialUsers())
                .processInstanceId(pi.getId())
                .rootProcessInstanceId(pi.getRootProcessInstanceId())
                .processId(pi.getProcessId())
                .rootProcessId(pi.getRootProcessId())
                .inputs(workItem.getParameters())
                .outputs(workItem.getResults())
                .build();
  
    }

    protected ProcessInstanceEventBody create(ProcessEvent event) {
        WorkflowProcessInstance pi = (WorkflowProcessInstance) event.getProcessInstance();
        ProcessInstanceEventBody.Builder eventBuilder = ProcessInstanceEventBody.create()
                .id(pi.getId())
                .parentInstanceId(pi.getParentProcessInstanceId())
                .rootInstanceId(pi.getRootProcessInstanceId())
                .processId(pi.getProcessId())
                .rootProcessId(pi.getRootProcessId())
                .processName(pi.getProcessName())
                .startDate(pi.getStartDate())
                .endDate(pi.getEndDate())
                .state(pi.getState())
                .variables(pi.getVariables());
        
        if (pi.getState() == ProcessInstance.STATE_ERROR) {
            eventBuilder.error(ProcessErrorEventBody.create()
                               .nodeDefinitionId(pi.getNodeIdInError())
                               .errorMessage(pi.getErrorMessage())
                               .build());
        }
        
        String securityRoles = (String) pi.getProcess().getMetaData().get("securityRoles");
        if (securityRoles != null) {
            eventBuilder.roles(securityRoles.split(","));
        }
        
        return eventBuilder.build();
    }
    
    protected NodeInstanceEventBody create(ProcessNodeEvent event) {
        NodeInstance ni = event.getNodeInstance();
        
        return NodeInstanceEventBody.create()
                .id(ni.getId())
                .nodeId(String.valueOf(ni.getNodeId()))
                .nodeDefinitionId(ni.getNodeDefinitionId())
                .nodeName(ni.getNodeName())
                .nodeType(ni.getNode().getClass().getSimpleName())
                .triggerTime(ni.getTriggerTime())
                .leaveTime(ni.getLeaveTime())
                .build();
    }
    
    protected String extractProcessId(Map<String, String> metadata) {
        String processId = metadata.get(ProcessInstanceEventBody.PROCESS_ID_META_DATA);
        if (processId.contains(".")) {
            return processId.substring(processId.lastIndexOf('.') + 1);
        }

        return service + "/" + processId;
    }
}
