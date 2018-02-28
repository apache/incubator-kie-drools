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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.services.task.audit;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;

import org.jbpm.services.task.audit.impl.model.AuditTaskImpl;
import org.jbpm.services.task.audit.impl.model.TaskEventImpl;
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

/**
 *
 */
public class JPATaskLifeCycleEventListener extends PersistableEventListener implements TaskLifeCycleEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(JPATaskLifeCycleEventListener.class);
    
    private static final List<String> SKIPPED_TASK_VARIABLES = Arrays.asList(new String[]{"ActorId", "TaskName", "NodeName"});  

    public JPATaskLifeCycleEventListener(boolean flag) {
        super(null);
    }
    
    public JPATaskLifeCycleEventListener(EntityManagerFactory emf) {
        super(emf);
    }

    @Override
    public void afterTaskStartedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = getPersistenceContext(((TaskContext)event.getTaskContext()).getPersistenceContext());
        try {
            
            persistenceContext.persist(new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.STARTED, ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId ));
                 
    
            AuditTaskImpl auditTaskImpl = getAuditTask(event, persistenceContext, ti);
            if (auditTaskImpl == null) {
                logger.warn("Unable find audit task entry for task id {} '{}', skipping audit task update", ti.getId(), ti.getName());
                return;
            }
            auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
            auditTaskImpl.setActualOwner(getActualOwner(ti));
            auditTaskImpl.setLastModificationDate(event.getEventDate());
            persistenceContext.merge(auditTaskImpl);
        } finally {
            cleanup(persistenceContext);
        }
    }

    @Override
    public void afterTaskActivatedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = getPersistenceContext(((TaskContext)event.getTaskContext()).getPersistenceContext());
        try {            
            persistenceContext.persist(new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.ACTIVATED, ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId));
                  
            AuditTaskImpl auditTaskImpl = getAuditTask(event, persistenceContext, ti);
            if (auditTaskImpl == null) {
                logger.warn("Unable find audit task entry for task id {} '{}', skipping audit task update", ti.getId(), ti.getName());
                return;
            }
            auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
            auditTaskImpl.setActualOwner(getActualOwner(ti));
            auditTaskImpl.setDescription(ti.getDescription());    
            auditTaskImpl.setLastModificationDate(event.getEventDate());
            persistenceContext.merge(auditTaskImpl);
        } finally {
            cleanup(persistenceContext);
        }
    }

    @Override
    public void afterTaskClaimedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = getPersistenceContext(((TaskContext)event.getTaskContext()).getPersistenceContext());
        try {            
            persistenceContext.persist(new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.CLAIMED, ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId));
            
            AuditTaskImpl auditTaskImpl = getAuditTask(event, persistenceContext, ti);
            if (auditTaskImpl == null) {
                logger.warn("Unable find audit task entry for task id {} '{}', skipping audit task update", ti.getId(), ti.getName());
                return;
            }
            auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
            auditTaskImpl.setActualOwner(getActualOwner(ti));
            auditTaskImpl.setDescription(ti.getDescription());
            auditTaskImpl.setLastModificationDate(event.getEventDate());
            persistenceContext.merge(auditTaskImpl);
        } finally {
            cleanup(persistenceContext);
        }
    }

    @Override
    public void afterTaskSkippedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = getPersistenceContext(((TaskContext)event.getTaskContext()).getPersistenceContext());
        try {            
            persistenceContext.persist(new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.SKIPPED, ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId));
           
            AuditTaskImpl auditTaskImpl = getAuditTask(event, persistenceContext, ti);
            if (auditTaskImpl == null) {
                logger.warn("Unable find audit task entry for task id {} '{}', skipping audit task update", ti.getId(), ti.getName());
                return;
            }
            auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
            auditTaskImpl.setActualOwner(getActualOwner(ti));
            auditTaskImpl.setDescription(ti.getDescription());
            auditTaskImpl.setLastModificationDate(event.getEventDate());
            persistenceContext.merge(auditTaskImpl);
            
        } finally {
            cleanup(persistenceContext);
        }
        
    }

    @Override
    public void afterTaskStoppedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = getPersistenceContext(((TaskContext)event.getTaskContext()).getPersistenceContext());
        try {            
            persistenceContext.persist(new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.STOPPED, ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId));
                      
            AuditTaskImpl auditTaskImpl = getAuditTask(event, persistenceContext, ti);
            if (auditTaskImpl == null) {
                logger.warn("Unable find audit task entry for task id {} '{}', skipping audit task update", ti.getId(), ti.getName());
                return;
            }
            auditTaskImpl.setDescription(ti.getDescription());
            auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
            auditTaskImpl.setActualOwner(getActualOwner(ti));
            auditTaskImpl.setLastModificationDate(event.getEventDate());
            persistenceContext.merge(auditTaskImpl);
        } finally {
            cleanup(persistenceContext);
        }

    }

    @Override
    public void afterTaskCompletedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = getPersistenceContext(((TaskContext)event.getTaskContext()).getPersistenceContext());
        try {            
            persistenceContext.persist(new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.COMPLETED, ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId));    
            
            AuditTaskImpl auditTaskImpl = getAuditTask(event, persistenceContext, ti);
            if (auditTaskImpl == null) {
                logger.warn("Unable find audit task entry for task id {} '{}', skipping audit task update", ti.getId(), ti.getName());
                return;
            }
            auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
            auditTaskImpl.setActualOwner(getActualOwner(ti));
            auditTaskImpl.setLastModificationDate(event.getEventDate());
            persistenceContext.merge(auditTaskImpl);
        } finally {
            cleanup(persistenceContext);
        }
    }

    @Override
    public void afterTaskFailedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = getPersistenceContext(((TaskContext)event.getTaskContext()).getPersistenceContext());
        try {            
            persistenceContext.persist(new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.FAILED, ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId));
            
            AuditTaskImpl auditTaskImpl = getAuditTask(event, persistenceContext, ti);
            if (auditTaskImpl == null) {
                logger.warn("Unable find audit task entry for task id {} '{}', skipping audit task update", ti.getId(), ti.getName());
                return;
            }
            auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
            auditTaskImpl.setActualOwner(getActualOwner(ti));
            auditTaskImpl.setLastModificationDate(event.getEventDate());
            persistenceContext.merge(auditTaskImpl);
        } finally {
            cleanup(persistenceContext);
        }
    }

    @Override
    public void afterTaskAddedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = getPersistenceContext(((TaskContext)event.getTaskContext()).getPersistenceContext());
        try {
            if(ti.getTaskData().getProcessId() != null){
                userId = ti.getTaskData().getProcessId();
            }
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
                ti.getTaskData().getWorkItemId()
            );
            persistenceContext.persist(auditTaskImpl);
            persistenceContext.persist(new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.ADDED, ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId));
        } finally {
            cleanup(persistenceContext);
        }
    }

    @Override
    public void afterTaskExitedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = getPersistenceContext(((TaskContext)event.getTaskContext()).getPersistenceContext());
        try {            
            persistenceContext.persist(new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.EXITED, ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId));
    
            AuditTaskImpl auditTaskImpl = getAuditTask(event, persistenceContext, ti);
            if (auditTaskImpl == null) {
                logger.warn("Unable find audit task entry for task id {} '{}', skipping audit task update", ti.getId(), ti.getName());
                return;
            }
            
            auditTaskImpl.setDescription(ti.getDescription());
            auditTaskImpl.setName(ti.getName());  
            auditTaskImpl.setActivationTime(ti.getTaskData().getActivationTime());
            auditTaskImpl.setPriority(ti.getPriority());
            auditTaskImpl.setDueDate(ti.getTaskData().getExpirationTime());
            auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
            auditTaskImpl.setActualOwner(getActualOwner(ti));
            auditTaskImpl.setLastModificationDate(event.getEventDate());
            persistenceContext.merge(auditTaskImpl);
        } finally {
            cleanup(persistenceContext);
        }
        
    }

    @Override
    public void afterTaskReleasedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = getPersistenceContext(((TaskContext)event.getTaskContext()).getPersistenceContext());
        try {
             
            AuditTaskImpl auditTaskImpl = getAuditTask(event, persistenceContext, ti);
            if (auditTaskImpl == null) {
                logger.warn("Unable find audit task entry for task id {} '{}', skipping audit task update", ti.getId(), ti.getName());
                return;
            }
   
            auditTaskImpl.setDescription(ti.getDescription());
            auditTaskImpl.setName(ti.getName());  
            auditTaskImpl.setActivationTime(ti.getTaskData().getActivationTime());
            auditTaskImpl.setPriority(ti.getPriority());
            auditTaskImpl.setDueDate(ti.getTaskData().getExpirationTime());
            auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
            auditTaskImpl.setActualOwner(getActualOwner(ti));
            auditTaskImpl.setLastModificationDate(event.getEventDate());
            persistenceContext.merge(auditTaskImpl);
        } finally {
            cleanup(persistenceContext);
        }

    }

    @Override
    public void afterTaskResumedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = getPersistenceContext(((TaskContext)event.getTaskContext()).getPersistenceContext());
        try {            
            persistenceContext.persist(new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.RESUMED, ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId));
    
            
            AuditTaskImpl auditTaskImpl = getAuditTask(event, persistenceContext, ti);
            if (auditTaskImpl == null) {
                logger.warn("Unable find audit task entry for task id {} '{}', skipping audit task update", ti.getId(), ti.getName());
                return;
            }
            auditTaskImpl.setDescription(ti.getDescription());
            auditTaskImpl.setName(ti.getName());  
            auditTaskImpl.setActivationTime(ti.getTaskData().getActivationTime());
            auditTaskImpl.setPriority(ti.getPriority());
            auditTaskImpl.setDueDate(ti.getTaskData().getExpirationTime());
            auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
            auditTaskImpl.setActualOwner(getActualOwner(ti));
            auditTaskImpl.setLastModificationDate(event.getEventDate());
            persistenceContext.merge(auditTaskImpl);
        } finally {
            cleanup(persistenceContext);
        }
    }

    @Override
    public void afterTaskSuspendedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = getPersistenceContext(((TaskContext)event.getTaskContext()).getPersistenceContext());
        try {
            
            persistenceContext.persist(new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.SUSPENDED, ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId));
    
            AuditTaskImpl auditTaskImpl = getAuditTask(event, persistenceContext, ti);
            if (auditTaskImpl == null) {
                logger.warn("Unable find audit task entry for task id {} '{}', skipping audit task update", ti.getId(), ti.getName());
                return;
            }
              auditTaskImpl.setDescription(ti.getDescription());
            auditTaskImpl.setName(ti.getName());  
            auditTaskImpl.setActivationTime(ti.getTaskData().getActivationTime());
            auditTaskImpl.setPriority(ti.getPriority());
            auditTaskImpl.setDueDate(ti.getTaskData().getExpirationTime());
            auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
            auditTaskImpl.setActualOwner(getActualOwner(ti));
            auditTaskImpl.setLastModificationDate(event.getEventDate());
            persistenceContext.merge(auditTaskImpl);
        } finally {
            cleanup(persistenceContext);
        }
    }

    @Override
    public void afterTaskForwardedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = getPersistenceContext(((TaskContext)event.getTaskContext()).getPersistenceContext());
        try {
            StringBuilder message = new StringBuilder();
            String entitiesAsString = (ti.getPeopleAssignments().getPotentialOwners()).stream().map(oe -> oe.getId()).collect(Collectors.joining(","));
            message.append("Forward to [" + entitiesAsString + "]");
            
            persistenceContext.persist(new TaskEventImpl(ti.getId(), 
                                                         org.kie.internal.task.api.model.TaskEvent.TaskEventType.FORWARDED, 
                                                         ti.getTaskData().getProcessInstanceId(), 
                                                         ti.getTaskData().getWorkItemId(), 
                                                         userId, 
                                                         message.toString()));
            
            AuditTaskImpl auditTaskImpl = getAuditTask(event, persistenceContext, ti);
            if (auditTaskImpl == null) {
                logger.warn("Unable find audit task entry for task id {} '{}', skipping audit task update", ti.getId(), ti.getName());
                return;
            }
            auditTaskImpl.setDescription(ti.getDescription());
            auditTaskImpl.setName(ti.getName());  
            auditTaskImpl.setActivationTime(ti.getTaskData().getActivationTime());
            auditTaskImpl.setPriority(ti.getPriority());
            auditTaskImpl.setDueDate(ti.getTaskData().getExpirationTime());
            auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
            auditTaskImpl.setActualOwner(getActualOwner(ti));
            auditTaskImpl.setLastModificationDate(event.getEventDate());
            persistenceContext.merge(auditTaskImpl);
        } finally {
            cleanup(persistenceContext);
        }
    }

    @Override
    public void afterTaskDelegatedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = getPersistenceContext(((TaskContext)event.getTaskContext()).getPersistenceContext());
        try {           
            persistenceContext.persist(new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.DELEGATED, ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId));
            
    
            AuditTaskImpl auditTaskImpl = getAuditTask(event, persistenceContext, ti);
            if (auditTaskImpl == null) {
                logger.warn("Unable find audit task entry for task id {} '{}', skipping audit task update", ti.getId(), ti.getName());
                return;
            }
            auditTaskImpl.setDescription(ti.getDescription());
            auditTaskImpl.setName(ti.getName());  
            auditTaskImpl.setActivationTime(ti.getTaskData().getActivationTime());
            auditTaskImpl.setPriority(ti.getPriority());
            auditTaskImpl.setDueDate(ti.getTaskData().getExpirationTime());
            auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
            auditTaskImpl.setActualOwner(getActualOwner(ti));
            auditTaskImpl.setLastModificationDate(event.getEventDate());
            persistenceContext.merge(auditTaskImpl);
        } finally {
            cleanup(persistenceContext);
        }
    }
    
    @Override
    public void afterTaskNominatedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = getPersistenceContext(((TaskContext)event.getTaskContext()).getPersistenceContext());
        try {           
            persistenceContext.persist(new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.NOMINATED, userId, new Date()));
    
            AuditTaskImpl auditTaskImpl = getAuditTask(event, persistenceContext, ti);
            if (auditTaskImpl == null) {
                logger.warn("Unable find audit task entry for task id {} '{}', skipping audit task update", ti.getId(), ti.getName());
                return;
            }
            auditTaskImpl.setDescription(ti.getDescription());
            auditTaskImpl.setName(ti.getName());  
            auditTaskImpl.setActivationTime(ti.getTaskData().getActivationTime());
            auditTaskImpl.setPriority(ti.getPriority());
            auditTaskImpl.setDueDate(ti.getTaskData().getExpirationTime());
            auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
            auditTaskImpl.setActualOwner(getActualOwner(ti));
            auditTaskImpl.setLastModificationDate(event.getEventDate());
            persistenceContext.merge(auditTaskImpl);
        } finally {
            cleanup(persistenceContext);
        }
    }
    
    /*
     * helper methods - start
     */
    
    protected AuditTaskImpl getAuditTask(TaskEvent event, TaskPersistenceContext persistenceContext, Task ti) {
        AuditTaskImpl auditTaskImpl = persistenceContext.queryWithParametersInTransaction("getAuditTaskById", true, 
                persistenceContext.addParametersToMap("taskId", ti.getId()),
                ClassUtil.<AuditTaskImpl>castClass(AuditTaskImpl.class));
        
        return auditTaskImpl;
    }

    /*
     * helper methods - end
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
    public void beforeTaskReleasedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = getPersistenceContext(((TaskContext)event.getTaskContext()).getPersistenceContext());
        try {            
            persistenceContext.persist(new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.RELEASED, ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId));
          
        } finally {
            cleanup(persistenceContext);
        }
        
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
    public boolean equals(Object obj) {
        if ( this == obj ) 
            return true;
        if ( obj == null ) 
            return false;
        if ( (obj instanceof JPATaskLifeCycleEventListener) ) 
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

    @Override
    public void beforeTaskUpdatedEvent(TaskEvent event) {
        
        
    }

    @Override
    public void afterTaskUpdatedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        
        TaskPersistenceContext persistenceContext = getPersistenceContext(((TaskContext)event.getTaskContext()).getPersistenceContext());
        try {
            
            AuditTaskImpl auditTaskImpl = getAuditTask(event, persistenceContext, ti);
            if (auditTaskImpl == null) {
                logger.warn("Unable find audit task entry for task id {} '{}', skipping audit task update", ti.getId(), ti.getName());
                return;
            }

            
            if((ti.getDescription() != null && !ti.getDescription().equals(auditTaskImpl.getDescription()))
                    || (ti.getDescription() == null && auditTaskImpl.getDescription() != null)){
                String message ="Updated Description {From: "+auditTaskImpl.getDescription()+
                                                                    ", to: "+ti.getDescription()+"}";
                persistenceContext.persist(new TaskEventImpl(ti.getId(),
                            org.kie.internal.task.api.model.TaskEvent.TaskEventType.UPDATED,
                            ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId, message));
            }
            if( (ti.getName() != null && !ti.getName().equals(auditTaskImpl.getName()))
                    || (ti.getName() == null && auditTaskImpl.getName() != null)){
                String message ="Updated Name {From: "+auditTaskImpl.getName()+
                                                                    ", to: "+ti.getName()+"}";
                persistenceContext.persist(new TaskEventImpl(ti.getId(),
                            org.kie.internal.task.api.model.TaskEvent.TaskEventType.UPDATED,
                            ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId, message));
            }
            if( auditTaskImpl.getPriority() != ti.getPriority()){
                String message ="Updated Priority {From: "+auditTaskImpl.getPriority()+
                                                                    ", to: "+ti.getPriority()+"}";
                persistenceContext.persist(new TaskEventImpl(ti.getId(),
                            org.kie.internal.task.api.model.TaskEvent.TaskEventType.UPDATED,
                            ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId, message));
            }

            if((auditTaskImpl.getDueDate() != null && ti.getTaskData().getExpirationTime() != null 
                    && auditTaskImpl.getDueDate().getTime() != ti.getTaskData().getExpirationTime().getTime()) 
                    || (auditTaskImpl.getDueDate() == null && ti.getTaskData().getExpirationTime() != null)
                    || (auditTaskImpl.getDueDate() != null && ti.getTaskData().getExpirationTime() == null)){
                String message ="Updated DueDate {From: "+auditTaskImpl.getDueDate()+
                                                                    ", to: "+ti.getTaskData().getExpirationTime()+"}";
                persistenceContext.persist(new TaskEventImpl(ti.getId(),
                            org.kie.internal.task.api.model.TaskEvent.TaskEventType.UPDATED,
                            ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId, message));
            }
    
            auditTaskImpl.setDescription(ti.getDescription());
            auditTaskImpl.setName(ti.getName());
            auditTaskImpl.setPriority(ti.getPriority());
            auditTaskImpl.setDueDate(ti.getTaskData().getExpirationTime());
            auditTaskImpl.setLastModificationDate(event.getEventDate());
            persistenceContext.merge(auditTaskImpl);
            
        } catch(Exception e){
            e.printStackTrace();

        }

        finally {
            cleanup(persistenceContext);
        }
    }

    @Override
    public void beforeTaskReassignedEvent(TaskEvent event) {

    }

    @Override
    public void afterTaskReassignedEvent(TaskEvent event) {
        String userId = event.getTaskContext().getUserId();
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = getPersistenceContext(((TaskContext)event.getTaskContext()).getPersistenceContext());
        try {
           
            persistenceContext.persist(new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.DELEGATED, ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getWorkItemId(), userId));
            
    
            AuditTaskImpl auditTaskImpl = getAuditTask(event, persistenceContext, ti);
            if (auditTaskImpl == null) {
                logger.warn("Unable find audit task entry for task id {} '{}', skipping audit task update", ti.getId(), ti.getName());
                return;
            }
            auditTaskImpl.setDescription(ti.getDescription());
            auditTaskImpl.setName(ti.getName());  
            auditTaskImpl.setActivationTime(ti.getTaskData().getActivationTime());
            auditTaskImpl.setPriority(ti.getPriority());
            auditTaskImpl.setDueDate(ti.getTaskData().getExpirationTime());
            auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
            auditTaskImpl.setActualOwner(getActualOwner(ti));
            auditTaskImpl.setLastModificationDate(event.getEventDate());
            persistenceContext.merge(auditTaskImpl);
        } finally {
            cleanup(persistenceContext);
        }
    }

    @Override
    public void beforeTaskNotificationEvent(TaskEvent event) {

    }

    @Override
    public void afterTaskNotificationEvent(TaskEvent event) {

    }

    @Override
    public void afterTaskOutputVariableChangedEvent(TaskEvent event, Map<String, Object> variables) {
        String userId = event.getTaskContext().getUserId();
        Task task = event.getTask();        
        TaskPersistenceContext persistenceContext = getPersistenceContext(((TaskContext)event.getTaskContext()).getPersistenceContext());
        // first cleanup previous values if any
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("type", VariableType.OUTPUT);
        params.put("taskId", task.getId());
        int removed = persistenceContext.executeUpdate("DeleteTaskVariableForTask", params);
        logger.debug("Deleted {} output variables logs for task id {}", removed, task.getId());
        
        if (variables == null || variables.isEmpty()) {
            return;
        }
        
        indexAndPersistVariables(task, variables, persistenceContext, VariableType.OUTPUT);
        String message = "Task output data updated";
        persistenceContext.persist(new TaskEventImpl(task.getId(), 
                                                     org.kie.internal.task.api.model.TaskEvent.TaskEventType.UPDATED, 
                                                     task.getTaskData().getProcessInstanceId(), 
                                                     task.getTaskData().getWorkItemId(), 
                                                     userId, message));
        AuditTaskImpl auditTaskImpl = getAuditTask(event, persistenceContext, task);
        if (auditTaskImpl == null) {
            logger.warn("Unable find audit task entry for task id {} '{}', skipping audit task update", task.getId(), task.getName());
            return;
        }
        auditTaskImpl.setLastModificationDate(event.getEventDate());
        persistenceContext.merge(auditTaskImpl);
    }

    @Override
    public void afterTaskInputVariableChangedEvent(TaskEvent event, Map<String, Object> variables) {
        if (variables == null || variables.isEmpty()) {
            return;
        }
        Task task = event.getTask();        
        TaskPersistenceContext persistenceContext = getPersistenceContext(((TaskContext)event.getTaskContext()).getPersistenceContext());

        indexAndPersistVariables(task, variables, persistenceContext, VariableType.INPUT);
        
    }
    
    protected void indexAndPersistVariables(Task task, Map<String, Object> variables, TaskPersistenceContext persistenceContext, VariableType type) {
        TaskIndexerManager manager = TaskIndexerManager.get();
        
        for (Map.Entry<String, Object> variable : variables.entrySet()) {
            if (SKIPPED_TASK_VARIABLES.contains(variable.getKey()) || variable.getValue() == null) {
                continue;
            }
            List<TaskVariable> taskVars = manager.index(task, variable.getKey(), variable.getValue());
            
            if (taskVars != null) {
                for (TaskVariable tVariable : taskVars) {
                    tVariable.setType(type);
                    persistenceContext.persist(tVariable);
                }
            }
        }
    }
    
    @Override
    public void afterTaskAssignmentsAddedEvent(TaskEvent event, AssignmentType type, List<OrganizationalEntity> entities) {                
        assignmentsUpadted(event, type, entities, "] have been added");    
    }

    @Override
    public void afterTaskAssignmentsRemovedEvent(TaskEvent event, AssignmentType type, List<OrganizationalEntity> entities) {
        assignmentsUpadted(event, type, entities, "] have been removed");
    }
    
    protected void assignmentsUpadted(TaskEvent event, AssignmentType type, List<OrganizationalEntity> entities, String messageSufix) {
        if (entities == null || entities.isEmpty()) {
            return;
        }
        String userId = event.getTaskContext().getUserId();
        Task task = event.getTask();        
        TaskPersistenceContext persistenceContext = getPersistenceContext(((TaskContext)event.getTaskContext()).getPersistenceContext());
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
        
        persistenceContext.persist(new TaskEventImpl(task.getId(), 
                                                     org.kie.internal.task.api.model.TaskEvent.TaskEventType.UPDATED, 
                                                     task.getTaskData().getProcessInstanceId(), 
                                                     task.getTaskData().getWorkItemId(), 
                                                     userId, message.toString()));
    }

    
    protected String getActualOwner(Task ti) {
        String userId = "";
        if (ti.getTaskData().getActualOwner() != null) {
            userId = ti.getTaskData().getActualOwner().getId();
        }
        
        return userId;
    }

}
