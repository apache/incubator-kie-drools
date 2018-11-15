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

package org.jbpm.services.task.audit.jms;

import static org.kie.soup.commons.xstream.XStreamUtils.createTrustingXStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.jbpm.services.task.audit.TaskLifeCycleEventConstants;
import org.jbpm.services.task.audit.impl.model.AuditTaskData;
import org.jbpm.services.task.audit.impl.model.AuditTaskImpl;
import org.jbpm.services.task.audit.impl.model.TaskEventImpl;
import org.jbpm.services.task.audit.impl.model.TaskVariableImpl;
import org.jbpm.services.task.audit.variable.TaskIndexerManager;
import org.jbpm.services.task.lifecycle.listeners.TaskLifeCycleEventListener;
import org.jbpm.services.task.persistence.PersistableEventListener;
import org.jbpm.services.task.utils.ClassUtil;
import org.kie.api.task.TaskEvent;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskContext;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.TaskVariable;
import org.kie.internal.task.api.TaskVariable.VariableType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class AsyncTaskLifeCycleEventProducer extends PersistableEventListener implements TaskLifeCycleEventListener {

    private static final Logger logger = LoggerFactory.getLogger(AsyncTaskLifeCycleEventProducer.class);

    private ConnectionFactory connectionFactory;    
    private Queue queue;
    private boolean transacted = true;
    private XStream xstream;
    
    public AsyncTaskLifeCycleEventProducer() {
        super(null);
        initXStream();
    }
    
    private void initXStream() {
        if(xstream==null) {
            xstream = createTrustingXStream();
            String[] voidDeny = {"void.class", "Void.class"};
            xstream.denyTypes(voidDeny);
        }
    }
    
    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Queue getQueue() {
        return queue;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }
    
    public boolean isTransacted() {
        return transacted;
    }

    public void setTransacted(boolean transacted) {
        this.transacted = transacted;
    }
    
    @Override
    public void afterTaskStartedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        
        TaskEventImpl taskEvent = new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.STARTED, ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId );                     
        AuditTaskImpl auditTaskImpl = createAuditTask(ti, event.getEventDate());         
        auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
        auditTaskImpl.setActualOwner(getActualOwner(ti));
        auditTaskImpl.setLastModificationDate(event.getEventDate());
        
        sendMessage(new AuditTaskData(auditTaskImpl, taskEvent), 5);
    }

    @Override
    public void afterTaskActivatedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        TaskEventImpl taskEvent = new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.ACTIVATED, ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId);
                  
        AuditTaskImpl auditTaskImpl = createAuditTask(ti, event.getEventDate());    
        auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
        auditTaskImpl.setActualOwner(getActualOwner(ti));
        auditTaskImpl.setDescription(ti.getDescription());    
        auditTaskImpl.setLastModificationDate(event.getEventDate());
       
        sendMessage(new AuditTaskData(auditTaskImpl, taskEvent), 8);
    }

    @Override
    public void afterTaskClaimedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        TaskEventImpl taskEvent = new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.CLAIMED, ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId);

        AuditTaskImpl auditTaskImpl = createAuditTask(ti, event.getEventDate());

        auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
        auditTaskImpl.setActualOwner(getActualOwner(ti));
        auditTaskImpl.setDescription(ti.getDescription());
        auditTaskImpl.setLastModificationDate(event.getEventDate());
        
        sendMessage(new AuditTaskData(auditTaskImpl, taskEvent), 8);

    }

    @Override
    public void afterTaskSkippedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        TaskEventImpl taskEvent = new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.SKIPPED, ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId);

        AuditTaskImpl auditTaskImpl = createAuditTask(ti, event.getEventDate());

        auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
        auditTaskImpl.setActualOwner(getActualOwner(ti));
        auditTaskImpl.setDescription(ti.getDescription());
        auditTaskImpl.setLastModificationDate(event.getEventDate());
        sendMessage(new AuditTaskData(auditTaskImpl, taskEvent), 2);
    }

    @Override
    public void afterTaskStoppedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        TaskEventImpl taskEvent = new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.STOPPED, ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId);

        AuditTaskImpl auditTaskImpl = createAuditTask(ti, event.getEventDate());
        auditTaskImpl.setDescription(ti.getDescription());
        auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
        auditTaskImpl.setActualOwner(getActualOwner(ti));
        auditTaskImpl.setLastModificationDate(event.getEventDate());

        sendMessage(new AuditTaskData(auditTaskImpl, taskEvent), 4);
    }

    @Override
    public void afterTaskCompletedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        TaskEventImpl taskEvent = new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.COMPLETED, ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId);

        AuditTaskImpl auditTaskImpl = createAuditTask(ti, event.getEventDate());
        auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
        auditTaskImpl.setActualOwner(getActualOwner(ti));
        auditTaskImpl.setLastModificationDate(event.getEventDate());
        sendMessage(new AuditTaskData(auditTaskImpl, taskEvent), 2);
    }

    @Override
    public void afterTaskFailedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        TaskEventImpl taskEvent = new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.FAILED, ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId);

        AuditTaskImpl auditTaskImpl = createAuditTask(ti, event.getEventDate());
        auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
        auditTaskImpl.setActualOwner(getActualOwner(ti));
        auditTaskImpl.setLastModificationDate(event.getEventDate());
        sendMessage(new AuditTaskData(auditTaskImpl, taskEvent), 2);
    }

    @Override
    public void afterTaskAddedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();   
        if(ti.getTaskData().getProcessId() != null){
            userId = ti.getTaskData().getProcessId();
        }
        AuditTaskImpl auditTask = createAuditTask(ti, event.getEventDate());            
        TaskEventImpl taskEvent = new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.ADDED, ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId);
        sendMessage(new AuditTaskData(auditTask, taskEvent), 9);
    }

    @Override
    public void afterTaskExitedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        TaskEventImpl taskEvent = new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.EXITED, ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId);

        AuditTaskImpl auditTaskImpl = createAuditTask(ti, event.getEventDate());
        auditTaskImpl.setDescription(ti.getDescription());
        auditTaskImpl.setName(ti.getName());
        auditTaskImpl.setActivationTime(ti.getTaskData().getActivationTime());
        auditTaskImpl.setPriority(ti.getPriority());
        auditTaskImpl.setDueDate(ti.getTaskData().getExpirationTime());
        auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
        auditTaskImpl.setActualOwner(getActualOwner(ti));
        auditTaskImpl.setLastModificationDate(event.getEventDate());

        sendMessage(new AuditTaskData(auditTaskImpl, taskEvent), 2);
    }

    @Override
    public void afterTaskReleasedEvent(TaskEvent event) {        
        Task ti = event.getTask();
        AuditTaskImpl auditTaskImpl = createAuditTask(ti, event.getEventDate());

        auditTaskImpl.setDescription(ti.getDescription());
        auditTaskImpl.setName(ti.getName());
        auditTaskImpl.setActivationTime(ti.getTaskData().getActivationTime());
        auditTaskImpl.setPriority(ti.getPriority());
        auditTaskImpl.setDueDate(ti.getTaskData().getExpirationTime());
        auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
        auditTaskImpl.setActualOwner(getActualOwner(ti));
        auditTaskImpl.setLastModificationDate(event.getEventDate());

        sendMessage(new AuditTaskData(auditTaskImpl), 8);
    }

    @Override
    public void afterTaskResumedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        TaskEventImpl taskEvent = new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.RESUMED, ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId);

        AuditTaskImpl auditTaskImpl = createAuditTask(ti, event.getEventDate());
        auditTaskImpl.setDescription(ti.getDescription());
        auditTaskImpl.setName(ti.getName());
        auditTaskImpl.setActivationTime(ti.getTaskData().getActivationTime());
        auditTaskImpl.setPriority(ti.getPriority());
        auditTaskImpl.setDueDate(ti.getTaskData().getExpirationTime());
        auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
        auditTaskImpl.setActualOwner(getActualOwner(ti));
        auditTaskImpl.setLastModificationDate(event.getEventDate());
        sendMessage(new AuditTaskData(auditTaskImpl, taskEvent), 6);
    }

    @Override
    public void afterTaskSuspendedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        TaskEventImpl taskEvent = new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.SUSPENDED, ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId);

        AuditTaskImpl auditTaskImpl = createAuditTask(ti, event.getEventDate());

        auditTaskImpl.setDescription(ti.getDescription());
        auditTaskImpl.setName(ti.getName());
        auditTaskImpl.setActivationTime(ti.getTaskData().getActivationTime());
        auditTaskImpl.setPriority(ti.getPriority());
        auditTaskImpl.setDueDate(ti.getTaskData().getExpirationTime());
        auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
        auditTaskImpl.setActualOwner(getActualOwner(ti));
        auditTaskImpl.setLastModificationDate(event.getEventDate());
        sendMessage(new AuditTaskData(auditTaskImpl, taskEvent), 6);
    }

    @Override
    public void afterTaskForwardedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();

        StringBuilder message = new StringBuilder();
        String entitiesAsString = (ti.getPeopleAssignments().getPotentialOwners()).stream().map(oe -> oe.getId()).collect(Collectors.joining(","));
        message.append("Forward to [" + entitiesAsString + "]");

        TaskEventImpl taskEvent = new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.FORWARDED, ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId, message.toString());

        AuditTaskImpl auditTaskImpl = createAuditTask(ti, event.getEventDate());
        auditTaskImpl.setDescription(ti.getDescription());
        auditTaskImpl.setName(ti.getName());
        auditTaskImpl.setActivationTime(ti.getTaskData().getActivationTime());
        auditTaskImpl.setPriority(ti.getPriority());
        auditTaskImpl.setDueDate(ti.getTaskData().getExpirationTime());
        auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
        auditTaskImpl.setActualOwner(getActualOwner(ti));
        auditTaskImpl.setLastModificationDate(event.getEventDate());

        sendMessage(new AuditTaskData(auditTaskImpl, taskEvent), 4);
    }

    @Override
    public void afterTaskDelegatedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        TaskEventImpl taskEvent = new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.DELEGATED, ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId);

        AuditTaskImpl auditTaskImpl = createAuditTask(ti, event.getEventDate());
        auditTaskImpl.setDescription(ti.getDescription());
        auditTaskImpl.setName(ti.getName());
        auditTaskImpl.setActivationTime(ti.getTaskData().getActivationTime());
        auditTaskImpl.setPriority(ti.getPriority());
        auditTaskImpl.setDueDate(ti.getTaskData().getExpirationTime());
        auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
        auditTaskImpl.setActualOwner(getActualOwner(ti));
        auditTaskImpl.setLastModificationDate(event.getEventDate());
        sendMessage(new AuditTaskData(auditTaskImpl, taskEvent), 4);
    }
    
    @Override
    public void afterTaskNominatedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        TaskEventImpl taskEvent = new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.NOMINATED, userId, new Date());

        AuditTaskImpl auditTaskImpl = createAuditTask(ti, event.getEventDate());
        auditTaskImpl.setDescription(ti.getDescription());
        auditTaskImpl.setName(ti.getName());
        auditTaskImpl.setActivationTime(ti.getTaskData().getActivationTime());
        auditTaskImpl.setPriority(ti.getPriority());
        auditTaskImpl.setDueDate(ti.getTaskData().getExpirationTime());
        auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
        auditTaskImpl.setActualOwner(getActualOwner(ti));
        auditTaskImpl.setLastModificationDate(event.getEventDate());
        sendMessage(new AuditTaskData(auditTaskImpl, taskEvent), 4);
    }


    @Override
    public void beforeTaskReleasedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        TaskEventImpl taskEvent = new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.RELEASED, ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId);
        sendMessage(new AuditTaskData(null, taskEvent), 7);
    }


    @Override
    public void afterTaskUpdatedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        
        List<TaskEventImpl> taskEvents = new ArrayList<>();
        
        TaskPersistenceContext persistenceContext = getPersistenceContext(((TaskContext)event.getTaskContext()).getPersistenceContext());
        try {
            AuditTaskImpl auditTaskImpl = getAuditTask(persistenceContext, ti); 
            if((ti.getDescription() != null && !ti.getDescription().equals(auditTaskImpl.getDescription()))
                    || (ti.getDescription() == null && auditTaskImpl.getDescription() != null)){
                String message = getUpdateFieldLog("Description", auditTaskImpl.getDescription(), ti.getDescription());
    
                TaskEventImpl taskEvent = new TaskEventImpl(ti.getId(),
                            org.kie.internal.task.api.model.TaskEvent.TaskEventType.UPDATED,
                            ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId, message);
                taskEvents.add(taskEvent);
            }
            if( (ti.getName() != null && !ti.getName().equals(auditTaskImpl.getName()))
                    || (ti.getName() == null && auditTaskImpl.getName() != null)){
                String message = getUpdateFieldLog("Name", auditTaskImpl.getName(), ti.getName());
                TaskEventImpl taskEvent = new TaskEventImpl(ti.getId(),
                            org.kie.internal.task.api.model.TaskEvent.TaskEventType.UPDATED,
                            ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId, message);
                taskEvents.add(taskEvent);
            }
            if( auditTaskImpl.getPriority() != ti.getPriority()){
                String message = getUpdateFieldLog("Priority", String.valueOf(auditTaskImpl.getPriority()), String.valueOf(ti.getPriority()));
                TaskEventImpl taskEvent = new TaskEventImpl(ti.getId(),
                            org.kie.internal.task.api.model.TaskEvent.TaskEventType.UPDATED,
                            ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId, message);
                taskEvents.add(taskEvent);
            }
    
            if((auditTaskImpl.getDueDate() != null && ti.getTaskData().getExpirationTime() != null 
                    && auditTaskImpl.getDueDate().getTime() != ti.getTaskData().getExpirationTime().getTime()) 
                    || (auditTaskImpl.getDueDate() == null && ti.getTaskData().getExpirationTime() != null)
                    || (auditTaskImpl.getDueDate() != null && ti.getTaskData().getExpirationTime() == null)){
                String fromDate = (auditTaskImpl.getDueDate() != null ? new Date(auditTaskImpl.getDueDate().getTime()).toString(): null);
                String toDate = (ti.getTaskData().getExpirationTime()!= null ? ti.getTaskData().getExpirationTime().toString() : "" );
                String message = getUpdateFieldLog( "DueDate",
                                                    fromDate,
                                                    toDate );
                TaskEventImpl taskEvent = new TaskEventImpl(ti.getId(),
                            org.kie.internal.task.api.model.TaskEvent.TaskEventType.UPDATED,
                            ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId, message);
                taskEvents.add(taskEvent);
            }
    
            auditTaskImpl.setDescription(ti.getDescription());
            auditTaskImpl.setName(ti.getName());
            auditTaskImpl.setPriority(ti.getPriority());
            auditTaskImpl.setDueDate(ti.getTaskData().getExpirationTime());
            auditTaskImpl.setLastModificationDate(event.getEventDate());
            
            sendMessage(new AuditTaskData(auditTaskImpl, taskEvents), 4);
        } finally {
            cleanup(persistenceContext);
        }
    }


    @Override
    public void afterTaskReassignedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        TaskEventImpl taskEvent = new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.DELEGATED, ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId);
                
        AuditTaskImpl auditTaskImpl = createAuditTask(ti, event.getEventDate());
        auditTaskImpl.setDescription(ti.getDescription());
        auditTaskImpl.setName(ti.getName());  
        auditTaskImpl.setActivationTime(ti.getTaskData().getActivationTime());
        auditTaskImpl.setPriority(ti.getPriority());
        auditTaskImpl.setDueDate(ti.getTaskData().getExpirationTime());
        auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
        auditTaskImpl.setActualOwner(getActualOwner(ti));
        auditTaskImpl.setLastModificationDate(event.getEventDate());
        
        sendMessage(new AuditTaskData(auditTaskImpl, taskEvent), 4);
    }

    
    @Override
    public void afterTaskOutputVariableChangedEvent(TaskEvent event, Map<String, Object> variables) {
        String userId = event.getTaskContext().getUserId();
        Task task = event.getTask();        

        if (variables == null || variables.isEmpty()) {
            return;
        }
        
        List<TaskVariableImpl> taskVariables = indexVariables(task, variables, VariableType.OUTPUT);
        String message = "Task output data updated";
        TaskEventImpl taskEvent = new TaskEventImpl(task.getId(), 
                                                     org.kie.internal.task.api.model.TaskEvent.TaskEventType.UPDATED, 
                                                     task.getTaskData().getProcessInstanceId(), 
                                                     task.getTaskData().getWorkItemId(), 
                                                     userId, message);
        AuditTaskImpl auditTaskImpl = createAuditTask(task, event.getEventDate());
        auditTaskImpl.setLastModificationDate(event.getEventDate());
        
        sendMessage(new AuditTaskData(auditTaskImpl, Collections.singletonList(taskEvent), null, taskVariables), 2);
    }

    @Override
    public void afterTaskInputVariableChangedEvent(TaskEvent event, Map<String, Object> variables) {
        if (variables == null || variables.isEmpty()) {
            return;
        }
        Task task = event.getTask();               
        List<TaskVariableImpl> taskVariables = indexVariables(task, variables, VariableType.INPUT);
        
        sendMessage(new AuditTaskData(null, null, taskVariables, null), 2);
    }
    
    protected List<TaskVariableImpl> indexVariables(Task task, Map<String, Object> variables, VariableType type) {
        TaskIndexerManager manager = TaskIndexerManager.get();
        List<TaskVariableImpl> taskVariables = new ArrayList<>();
        for (Map.Entry<String, Object> variable : variables.entrySet()) {
            if (TaskLifeCycleEventConstants.SKIPPED_TASK_VARIABLES.contains(variable.getKey()) || variable.getValue() == null) {
                continue;
            }
            List<TaskVariable> taskVars = manager.index(task, variable.getKey(), variable.getValue());
            
            if (taskVars != null) {
                for (TaskVariable tVariable : taskVars) {
                    tVariable.setType(type);
                    taskVariables.add((TaskVariableImpl) tVariable);
                }
            }
        }
        
        return taskVariables;
    }
    
    @Override
    public void afterTaskAssignmentsAddedEvent(TaskEvent event, AssignmentType type, List<OrganizationalEntity> entities) {                
        assignmentsUpdated(event, type, entities, "] have been added");    
    }

    @Override
    public void afterTaskAssignmentsRemovedEvent(TaskEvent event, AssignmentType type, List<OrganizationalEntity> entities) {
        assignmentsUpdated(event, type, entities, "] have been removed");
    }
    
    protected void assignmentsUpdated(TaskEvent event, AssignmentType type, List<OrganizationalEntity> entities, String messageSufix) {
        if (entities == null || entities.isEmpty()) {
            return;
        }
        String userId = event.getTaskContext().getUserId();
        Task task = event.getTask();        
        
        StringBuilder message = new StringBuilder();
        
        switch (type) {
            case POT_OWNER:
                message.append("Potential owners [");
                break;
            case EXCL_OWNER:
                message.append("Excluded owners [");
                break;
            case ADMIN:
                message.append("Business administrators [");
                break;
            default:
                break;
        }
        String entitiesAsString = entities.stream().map(oe -> oe.getId()).collect(Collectors.joining(","));
        message.append(entitiesAsString);
        message.append(messageSufix);
        
        TaskEventImpl taskEvent = new TaskEventImpl(task.getId(), 
                                                     org.kie.internal.task.api.model.TaskEvent.TaskEventType.UPDATED, 
                                                     task.getTaskData().getProcessInstanceId(), 
                                                     task.getTaskData().getWorkItemId(), 
                                                     userId, message.toString());
        
        sendMessage(new AuditTaskData(null, taskEvent), 2);
    }

    /*
     * Helper methods
     */
    


    protected String getUpdateFieldLog(String fieldName, String previousValue, String value){
        return "Updated "+ fieldName
                + " {From: '"+ (previousValue!=null ? previousValue :  "" )
                + "' to: '"+ (value!=null ? value :  "" ) + "'}" ;
    }
    
    protected String getActualOwner(Task ti) {
        String userId = "";
        if (ti.getTaskData().getActualOwner() != null) {
            userId = ti.getTaskData().getActualOwner().getId();
        }
        
        return userId;
    }
    
    protected AuditTaskImpl createAuditTask(Task ti, Date date) {
        AuditTaskImpl auditTaskImpl = new AuditTaskImpl(
                ti.getId(),
                ti.getName(),
                ti.getTaskData().getStatus().name(),
                ti.getTaskData().getActivationTime(),
                (ti.getTaskData().getActualOwner() != null) ? ti.getTaskData().getActualOwner().getId() : "",
                ti.getDescription(),
                ti.getPriority(),
                (ti.getTaskData().getCreatedBy() != null) ? ti.getTaskData().getCreatedBy().getId() : "",
                ti.getTaskData().getCreatedOn(),
                ti.getTaskData().getExpirationTime(),
                ti.getTaskData().getProcessInstanceId(),
                ti.getTaskData().getProcessId(),
                ti.getTaskData().getProcessSessionId(),
                ti.getTaskData().getDeploymentId(),
                ti.getTaskData().getParentId(),
                ti.getTaskData().getWorkItemId(),
                date
            );
        
        return auditTaskImpl;
    }
    
    protected void sendMessage(AuditTaskData auditTaskData, int priority) {
        if (connectionFactory == null && queue == null) {
            throw new IllegalStateException("ConnectionFactory and Queue cannot be null");
        }
        Connection queueConnection = null;
        Session queueSession = null;
        MessageProducer producer = null;
        try {
            queueConnection = connectionFactory.createConnection();
            queueSession = queueConnection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);

            String eventXml = xstream.toXML(auditTaskData);
            TextMessage message = queueSession.createTextMessage(eventXml);
            message.setStringProperty("LogType", "Task");
            producer = queueSession.createProducer(queue);  
            producer.setPriority(priority);
            producer.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error when sending JMS message with working memory event", e);
        } finally {
            if (producer != null) {
                try {
                    producer.close();
                } catch (JMSException e) {
                    logger.warn("Error when closing producer", e);
                }
            }
            
            if (queueSession != null) {
                try {
                    queueSession.close();
                } catch (JMSException e) {
                    logger.warn("Error when closing queue session", e);
                }
            }
            
            if (queueConnection != null) {
                try {
                    queueConnection.close();
                } catch (JMSException e) {
                    logger.warn("Error when closing queue connection", e);
                }
            }
        }
    }
    
    protected AuditTaskImpl getAuditTask(TaskPersistenceContext persistenceContext, Task ti) {
        AuditTaskImpl auditTaskImpl = persistenceContext.queryWithParametersInTransaction("getAuditTaskById", true, 
                persistenceContext.addParametersToMap("taskId", ti.getId()),
                ClassUtil.<AuditTaskImpl>castClass(AuditTaskImpl.class));
        
        return auditTaskImpl;
    }
    
    /*
     * Not implemented task life cycle methods
     */
    
    @Override
    public void beforeTaskActivatedEvent(TaskEvent event) {

    }

    @Override
    public void beforeTaskClaimedEvent(TaskEvent event) {

    }

    @Override
    public void beforeTaskSkippedEvent(TaskEvent event) {

    }

    @Override
    public void beforeTaskStartedEvent(TaskEvent event) {

    }

    @Override
    public void beforeTaskStoppedEvent(TaskEvent event) {

    }

    @Override
    public void beforeTaskCompletedEvent(TaskEvent event) {

    }

    @Override
    public void beforeTaskFailedEvent(TaskEvent event) {

    }

    @Override
    public void beforeTaskAddedEvent(TaskEvent event) {
        
    }

    @Override
    public void beforeTaskExitedEvent(TaskEvent event) {

    }

    @Override
    public void beforeTaskResumedEvent(TaskEvent event) {

    }

    @Override
    public void beforeTaskSuspendedEvent(TaskEvent event) {

    }

    @Override
    public void beforeTaskForwardedEvent(TaskEvent event) {

    }

    @Override
    public void beforeTaskDelegatedEvent(TaskEvent event) {

    }
    
    @Override
    public void beforeTaskNominatedEvent(TaskEvent event) {

    }    
    @Override
    public void beforeTaskUpdatedEvent(TaskEvent event) {
        
        
    }
    @Override
    public void beforeTaskReassignedEvent(TaskEvent event) {

    }

    @Override
    public void beforeTaskNotificationEvent(TaskEvent event) {

    }

    @Override
    public void afterTaskNotificationEvent(TaskEvent event) {

    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) 
            return true;
        if ( obj == null ) 
            return false;
        if ( (obj instanceof AsyncTaskLifeCycleEventProducer) ) 
            return true;
        
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.getClass().getName().hashCode();
        
        return result;
    }


}
