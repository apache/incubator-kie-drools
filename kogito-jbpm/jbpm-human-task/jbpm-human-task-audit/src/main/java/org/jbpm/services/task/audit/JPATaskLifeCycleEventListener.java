/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.services.task.audit;

import java.util.Date;
import org.jbpm.services.task.audit.impl.model.AuditTaskImpl;
import org.jbpm.services.task.audit.impl.model.TaskEventImpl;
import org.jbpm.services.task.lifecycle.listeners.TaskLifeCycleEventListener;
import org.jbpm.services.task.utils.ClassUtil;
import org.kie.api.task.TaskEvent;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskContext;
import org.kie.internal.task.api.TaskPersistenceContext;

/**
 *
 */
public class JPATaskLifeCycleEventListener implements TaskLifeCycleEventListener {

    public JPATaskLifeCycleEventListener() {
    }

    @Override
    public void afterTaskStartedEvent(TaskEvent event) {
        String userId = "";
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = ((TaskContext)event.getTaskContext()).getPersistenceContext();
        if (ti.getTaskData().getActualOwner() != null) {
            userId = ti.getTaskData().getActualOwner().getId();
        }
        persistenceContext.persist(new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.STARTED, userId, new Date()));
        
//      Update UserAuditTask to Lucene        
//        UserAuditTask task = persistenceContext.queryWithParametersInTransaction("getUserAuditTaskById", true,
//				persistenceContext.addParametersToMap("taskId", ti.getId()),
//				ClassUtil.<UserAuditTask>castClass(UserAuditTask.class));
//        if (task != null) {
//            task.setStatus(ti.getTaskData().getStatus().name());
//            persistenceContext.persist(task);
//        }
         AuditTaskImpl auditTaskImpl = persistenceContext.queryWithParametersInTransaction("getAuditTaskById", true, 
				persistenceContext.addParametersToMap("taskId", ti.getId()),
				ClassUtil.<AuditTaskImpl>castClass(AuditTaskImpl.class));
        auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
            
        persistenceContext.persist(auditTaskImpl);
    }

    @Override
    public void afterTaskActivatedEvent(TaskEvent event) {
        String userId = "";
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = ((TaskContext)event.getTaskContext()).getPersistenceContext();
        if (ti.getTaskData().getActualOwner() != null) {
            userId = ti.getTaskData().getActualOwner().getId();
        }
        persistenceContext.persist(new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.ACTIVATED, userId, new Date()));
        
        //      Update UserAuditTask to Lucene
//        UserAuditTask task = persistenceContext.queryWithParametersInTransaction("getUserAuditTaskById", true,
//				persistenceContext.addParametersToMap("taskId", ti.getId()),
//				ClassUtil.<UserAuditTask>castClass(UserAuditTask.class));
//        task.setStatus(ti.getTaskData().getStatus().name());
//        persistenceContext.persist(task);
        
         AuditTaskImpl auditTaskImpl = persistenceContext.queryWithParametersInTransaction("getAuditTaskById", true, 
				persistenceContext.addParametersToMap("taskId", ti.getId()),
				ClassUtil.<AuditTaskImpl>castClass(AuditTaskImpl.class));
        auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
            
        persistenceContext.persist(auditTaskImpl);
    }

    @Override
    public void afterTaskClaimedEvent(TaskEvent event) {
        String userId = "";
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = ((TaskContext)event.getTaskContext()).getPersistenceContext();
        if (ti.getTaskData().getActualOwner() != null) {
            userId = ti.getTaskData().getActualOwner().getId();
        }
        persistenceContext.persist(new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.CLAIMED, userId, new Date()));
        //      Remove  GroupAuditTask to Lucene
//        GroupAuditTask task = persistenceContext.queryWithParametersInTransaction("getGroupAuditTaskById", true,
//				persistenceContext.addParametersToMap("taskId", ti.getId()),
//				ClassUtil.<GroupAuditTask>castClass(GroupAuditTask.class));
//        if (task != null) {
//            persistenceContext.remove(task);
//        }
        //      Create new   UserAuditTask to Lucene
//        persistenceContext.persist(new UserAuditTaskImpl(userId, ti.getId(), ti.getTaskData().getStatus().name(),
//                ti.getTaskData().getActivationTime(), ti.getName(),
//                ti.getDescription(), ti.getPriority(),
//                (ti.getTaskData().getCreatedBy() == null) ? "" : ti.getTaskData().getCreatedBy().getId(),
//                ti.getTaskData().getCreatedOn(), ti.getTaskData().getExpirationTime(),
//                ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getProcessId(), ti.getTaskData().getProcessSessionId(),
//                ti.getTaskData().getParentId()));
         AuditTaskImpl auditTaskImpl = persistenceContext.queryWithParametersInTransaction("getAuditTaskById", true, 
				persistenceContext.addParametersToMap("taskId", ti.getId()),
				ClassUtil.<AuditTaskImpl>castClass(AuditTaskImpl.class));
        auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
            
        persistenceContext.persist(auditTaskImpl);
    }

    @Override
    public void afterTaskSkippedEvent(TaskEvent event) {
        String userId = "";
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = ((TaskContext)event.getTaskContext()).getPersistenceContext();
        if (ti.getTaskData().getActualOwner() != null) {
            userId = ti.getTaskData().getActualOwner().getId();
        }
        persistenceContext.persist(new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.SKIPPED, userId, new Date()));
        // Find the UserAuditTask in the lucene index
//        UserAuditTask task = persistenceContext.queryWithParametersInTransaction("getUserAuditTaskById", true,
//				persistenceContext.addParametersToMap("taskId", ti.getId()),
//				ClassUtil.<UserAuditTask>castClass(UserAuditTask.class));
       // if (task != null) {
            // If the UserAuditTask is in lucene remove it
            //persistenceContext.remove(task);
        
        //Create the History Audit Task Impl, store it in the DB and also into lucene
           AuditTaskImpl auditTaskImpl = persistenceContext.queryWithParametersInTransaction("getAuditTaskById", true, 
				persistenceContext.addParametersToMap("taskId", ti.getId()),
				ClassUtil.<AuditTaskImpl>castClass(AuditTaskImpl.class));
        auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
            
        persistenceContext.persist(auditTaskImpl);
//        There is also the possibility that a GroupAuditTask exist in Lucene.. make sure that you remove it as well        
//        }else{
//            GroupAuditTask groupTask = persistenceContext.queryWithParametersInTransaction("getGroupAuditTaskById", true, 
//				persistenceContext.addParametersToMap("taskId", ti.getId()),
//				ClassUtil.<GroupAuditTask>castClass(GroupAuditTask.class));
//            if (groupTask != null) {
//                 persistenceContext.remove(groupTask);
//                 HistoryAuditTaskImpl historyAuditTaskImpl = new HistoryAuditTaskImpl(groupTask.getPotentialOwners(), groupTask.getTaskId(), ti.getTaskData().getStatus().name(),
//                                                                                groupTask.getActivationTime(), groupTask.getName(),
//                                                                                groupTask.getDescription(), groupTask.getPriority(),
//                                                                                groupTask.getCreatedBy(), groupTask.getCreatedOn(), 
//                                                                                groupTask.getDueDate(), groupTask.getProcessInstanceId(), 
//                                                                                groupTask.getProcessId(), groupTask.getProcessSessionId(),
//                                                                                groupTask.getParentId());
//                persistenceContext.persist(historyAuditTaskImpl);
           // }
            
       // }
        
    }

    @Override
    public void afterTaskStoppedEvent(TaskEvent event) {
        String userId = "";
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = ((TaskContext)event.getTaskContext()).getPersistenceContext();
        if (ti.getTaskData().getActualOwner() != null) {
            userId = ti.getTaskData().getActualOwner().getId();
        }
        persistenceContext.persist(new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.STOPPED, userId, new Date()));
        
        // Same as skipped
//        UserAuditTask task = persistenceContext.queryWithParametersInTransaction("getUserAuditTaskById", true, 
//				persistenceContext.addParametersToMap("taskId", ti.getId()),
//				ClassUtil.<UserAuditTask>castClass(UserAuditTask.class));
//        if (task != null) {
//            persistenceContext.remove(task);
           AuditTaskImpl auditTaskImpl = persistenceContext.queryWithParametersInTransaction("getAuditTaskById", true, 
				persistenceContext.addParametersToMap("taskId", ti.getId()),
				ClassUtil.<AuditTaskImpl>castClass(AuditTaskImpl.class));
        auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
            
        persistenceContext.persist(auditTaskImpl);
//        }else{
//            GroupAuditTask groupTask = persistenceContext.queryWithParametersInTransaction("getGroupAuditTaskById", true,
//				persistenceContext.addParametersToMap("taskId", ti.getId()),
//				ClassUtil.<GroupAuditTask>castClass(GroupAuditTask.class));
//            if (groupTask != null) {
//                 persistenceContext.remove(groupTask);
//                 HistoryAuditTaskImpl historyAuditTaskImpl = new HistoryAuditTaskImpl(groupTask.getPotentialOwners(), groupTask.getTaskId(), ti.getTaskData().getStatus().name(),
//                                                                                groupTask.getActivationTime(), groupTask.getName(),
//                                                                                groupTask.getDescription(), groupTask.getPriority(),
//                                                                                groupTask.getCreatedBy(), groupTask.getCreatedOn(), 
//                                                                                groupTask.getDueDate(), groupTask.getProcessInstanceId(), 
//                                                                                groupTask.getProcessId(), groupTask.getProcessSessionId(),
//                                                                                groupTask.getParentId());
//                persistenceContext.persist(historyAuditTaskImpl);
//            }
//            
//        }
    }

    @Override
    public void afterTaskCompletedEvent(TaskEvent event) {
        String userId = "";
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = ((TaskContext)event.getTaskContext()).getPersistenceContext();
        if (ti.getTaskData().getActualOwner() != null) {
            userId = ti.getTaskData().getActualOwner().getId();
        }
        persistenceContext.persist(new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.COMPLETED, userId, new Date()));

//      Make sure that you find and remove the USerAuditTask from lucene once it is completed
//        UserAuditTask task = persistenceContext.queryWithParametersInTransaction("getUserAuditTaskById", true,
//				persistenceContext.addParametersToMap("taskId", ti.getId()),
//				ClassUtil.<UserAuditTask>castClass(UserAuditTask.class));
//        if (task != null) {
//            persistenceContext.remove(task);
        // Create a new HistoryAuditTask to keep track about the task. 
            AuditTaskImpl auditTaskImpl = persistenceContext.queryWithParametersInTransaction("getAuditTaskById", true, 
				persistenceContext.addParametersToMap("taskId", ti.getId()),
				ClassUtil.<AuditTaskImpl>castClass(AuditTaskImpl.class));
        auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
            
        persistenceContext.persist(auditTaskImpl);
//        }
    }

    @Override
    public void afterTaskFailedEvent(TaskEvent event) {
        String userId = "";
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = ((TaskContext)event.getTaskContext()).getPersistenceContext();
        if (ti.getTaskData().getActualOwner() != null) {
            userId = ti.getTaskData().getActualOwner().getId();
        }
        persistenceContext.persist(new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.FAILED, userId, new Date()));
        
        // Same as task skipped
//        UserAuditTask task = persistenceContext.queryWithParametersInTransaction("getUserAuditTaskById", true,
//				persistenceContext.addParametersToMap("taskId", ti.getId()),
//				ClassUtil.<UserAuditTask>castClass(UserAuditTask.class));
//        if (task != null) {
//            persistenceContext.remove(task);
//            HistoryAuditTaskImpl historyAuditTaskImpl = new HistoryAuditTaskImpl(task.getActualOwner(), task.getTaskId(), ti.getTaskData().getStatus().name(),
//                                                                                task.getActivationTime(), task.getName(),
//                                                                                task.getDescription(), task.getPriority(),
//                                                                                task.getCreatedBy(), task.getCreatedOn(), 
//                                                                                task.getDueDate(), task.getProcessInstanceId(), 
//                                                                                task.getProcessId(), task.getProcessSessionId(),
//                                                                                task.getParentId());
//            persistenceContext.persist(historyAuditTaskImpl);
//        }else{
//            GroupAuditTask groupTask = persistenceContext.queryWithParametersInTransaction("getGroupAuditTaskById", true, 
//				persistenceContext.addParametersToMap("taskId", ti.getId()),
//				ClassUtil.<GroupAuditTask>castClass(GroupAuditTask.class));
//            if (groupTask != null) {
//                 persistenceContext.remove(groupTask);
//                 HistoryAuditTaskImpl historyAuditTaskImpl = new HistoryAuditTaskImpl(groupTask.getPotentialOwners(), groupTask.getTaskId(), ti.getTaskData().getStatus().name(),
//                                                                                groupTask.getActivationTime(), groupTask.getName(),
//                                                                                groupTask.getDescription(), groupTask.getPriority(),
//                                                                                groupTask.getCreatedBy(), groupTask.getCreatedOn(), 
//                                                                                groupTask.getDueDate(), groupTask.getProcessInstanceId(), 
//                                                                                groupTask.getProcessId(), groupTask.getProcessSessionId(),
//                                                                                groupTask.getParentId());
//                persistenceContext.persist(historyAuditTaskImpl);
//            }
//            
//        }
        
        
         AuditTaskImpl auditTaskImpl = persistenceContext.queryWithParametersInTransaction("getAuditTaskById", true, 
				persistenceContext.addParametersToMap("taskId", ti.getId()),
				ClassUtil.<AuditTaskImpl>castClass(AuditTaskImpl.class));
        auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
            
        persistenceContext.persist(auditTaskImpl);
    }

    @Override
    public void afterTaskAddedEvent(TaskEvent event) {
        String userId = "";
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = ((TaskContext)event.getTaskContext()).getPersistenceContext();
        
        AuditTaskImpl auditTaskImpl = new AuditTaskImpl( ti.getId(),ti.getName(),  ti.getTaskData().getStatus().name(),
                                                                                ti.getTaskData().getActivationTime() ,
                                                                                (ti.getTaskData().getActualOwner() != null)?ti.getTaskData().getActualOwner().getId():"",
                                                                                ti.getDescription(), ti.getPriority(),
                                                                                (ti.getTaskData().getCreatedBy() != null)?ti.getTaskData().getCreatedBy().getId():"",
                                                                                ti.getTaskData().getCreatedOn(), 
                                                                                ti.getTaskData().getExpirationTime(), ti.getTaskData().getProcessInstanceId(), 
                                                                                ti.getTaskData().getProcessId(), ti.getTaskData().getProcessSessionId(),
                                                                                ti.getTaskData().getDeploymentId(),
                                                                                ti.getTaskData().getParentId());
        persistenceContext.persist(auditTaskImpl);
            
        
        
// Create User or Group Task for Lucene

        
//        if (ti.getTaskData().getActualOwner() != null) {
//            userId = ti.getTaskData().getActualOwner().getId();
//            persistenceContext.persist(new UserAuditTaskImpl(userId, ti.getId(), ti.getTaskData().getStatus().name(),
//                    ti.getTaskData().getActivationTime(), ti.getName(),
//                    ti.getDescription(), ti.getPriority(),
//                    (ti.getTaskData().getCreatedBy() == null) ? "" : ti.getTaskData().getCreatedBy().getId(),
//                    ti.getTaskData().getCreatedOn(), ti.getTaskData().getExpirationTime(),
//                    ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getProcessId(), ti.getTaskData().getProcessSessionId(),
//                    ti.getTaskData().getParentId()));
//        } else if (!ti.getPeopleAssignments().getPotentialOwners().isEmpty()) {
//            StringBuilder sb = new StringBuilder();
//            for (OrganizationalEntity o : ti.getPeopleAssignments().getPotentialOwners()) {
//                sb.append(o.getId()).append("|");
//            }
//            persistenceContext.persist(new GroupAuditTaskImpl(sb.toString(), ti.getId(), ti.getTaskData().getStatus().name(),
//                    ti.getTaskData().getActivationTime(), ti.getName(),
//                    ti.getDescription(), ti.getPriority(),
//                    (ti.getTaskData().getCreatedBy() == null) ? "" : ti.getTaskData().getCreatedBy().getId(),
//                    ti.getTaskData().getCreatedOn(), ti.getTaskData().getExpirationTime(),
//                    ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getProcessId(), ti.getTaskData().getProcessSessionId(),
//                    ti.getTaskData().getParentId()));
//        }
        persistenceContext.persist(new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.ADDED, userId, new Date()));
    }

    @Override
    public void afterTaskExitedEvent(TaskEvent event) {
        String userId = "";
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = ((TaskContext)event.getTaskContext()).getPersistenceContext();
        if (ti.getTaskData().getActualOwner() != null) {
            userId = ti.getTaskData().getActualOwner().getId();
        }
        persistenceContext.persist(new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.EXITED, userId, new Date()));
        
        // Same as skipped
//        UserAuditTask task = persistenceContext.queryWithParametersInTransaction("getUserAuditTaskById", true,
//				persistenceContext.addParametersToMap("taskId", ti.getId()),
//				ClassUtil.<UserAuditTask>castClass(UserAuditTask.class));
//        if (task != null) {
//            persistenceContext.remove(task);
//            HistoryAuditTaskImpl historyAuditTaskImpl = new HistoryAuditTaskImpl(task.getActualOwner(), task.getTaskId(), ti.getTaskData().getStatus().name(),
//                                                                                task.getActivationTime(), task.getName(),
//                                                                                task.getDescription(), task.getPriority(),
//                                                                                task.getCreatedBy(), task.getCreatedOn(), 
//                                                                                task.getDueDate(), task.getProcessInstanceId(), 
//                                                                                task.getProcessId(), task.getProcessSessionId(),
//                                                                                task.getParentId());
//            persistenceContext.persist(historyAuditTaskImpl);
//        }else{
//            GroupAuditTask groupTask = persistenceContext.queryWithParametersInTransaction("getGroupAuditTaskById", true,
//				persistenceContext.addParametersToMap("taskId", ti.getId()),
//				ClassUtil.<GroupAuditTask>castClass(GroupAuditTask.class));
//            if (groupTask != null) {
//                 persistenceContext.remove(groupTask);
//                 HistoryAuditTaskImpl historyAuditTaskImpl = new HistoryAuditTaskImpl(groupTask.getPotentialOwners(), groupTask.getTaskId(), ti.getTaskData().getStatus().name(),
//                                                                                groupTask.getActivationTime(), groupTask.getName(),
//                                                                                groupTask.getDescription(), groupTask.getPriority(),
//                                                                                groupTask.getCreatedBy(), groupTask.getCreatedOn(), 
//                                                                                groupTask.getDueDate(), groupTask.getProcessInstanceId(), 
//                                                                                groupTask.getProcessId(), groupTask.getProcessSessionId(),
//                                                                                groupTask.getParentId());
//                persistenceContext.persist(historyAuditTaskImpl);
//            }
//            
//        }
         AuditTaskImpl auditTaskImpl = persistenceContext.queryWithParametersInTransaction("getAuditTaskById", true, 
				persistenceContext.addParametersToMap("taskId", ti.getId()),
				ClassUtil.<AuditTaskImpl>castClass(AuditTaskImpl.class));
        auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
            
        persistenceContext.persist(auditTaskImpl);
        
    }

    @Override
    public void afterTaskReleasedEvent(TaskEvent event) {
        String userId = "";
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = ((TaskContext)event.getTaskContext()).getPersistenceContext();
        if (ti.getTaskData().getActualOwner() != null) {
            userId = ti.getTaskData().getActualOwner().getId();
        }
        persistenceContext.persist(new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.RELEASED, userId, new Date()));
      
         AuditTaskImpl auditTaskImpl = persistenceContext.queryWithParametersInTransaction("getAuditTaskById", true, 
				persistenceContext.addParametersToMap("taskId", ti.getId()),
				ClassUtil.<AuditTaskImpl>castClass(AuditTaskImpl.class));
        auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
            
        persistenceContext.persist(auditTaskImpl);
        
        // Remove UserAuditTask and create a new GroupAuditTask for lucene
//        UserAuditTask task = persistenceContext.queryWithParametersInTransaction("getUserAuditTaskById", true, 
//				persistenceContext.addParametersToMap("taskId", ti.getId()),
//				ClassUtil.<UserAuditTask>castClass(UserAuditTask.class));
//        if (task != null) {
//            persistenceContext.remove(task);
//        }
//        StringBuilder sb = new StringBuilder();
//        for (OrganizationalEntity o : ti.getPeopleAssignments().getPotentialOwners()) {
//            sb.append(o.getId()).append("|");
//            
//        }
//        persistenceContext.persist(new GroupAuditTaskImpl(sb.toString(), ti.getId(), ti.getTaskData().getStatus().name(),
//                ti.getTaskData().getActivationTime(), ti.getName(),
//                ti.getDescription(), ti.getPriority(),
//                (ti.getTaskData().getCreatedBy() == null) ? "" : ti.getTaskData().getCreatedBy().getId(),
//                ti.getTaskData().getCreatedOn(), ti.getTaskData().getExpirationTime(),
//                ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getProcessId(), ti.getTaskData().getProcessSessionId(),
//                ti.getTaskData().getParentId()));

    }

    @Override
    public void afterTaskResumedEvent(TaskEvent event) {
        String userId = "";
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = ((TaskContext)event.getTaskContext()).getPersistenceContext();
        if (ti.getTaskData().getActualOwner() != null) {
            userId = ti.getTaskData().getActualOwner().getId();
        }
        persistenceContext.persist(new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.RESUMED, userId, new Date()));
       // Update Lucene UserAudit Task
//        UserAuditTask task = persistenceContext.queryWithParametersInTransaction("getUserAuditTaskById", true,
//				persistenceContext.addParametersToMap("taskId", ti.getId()),
//				ClassUtil.<UserAuditTask>castClass(UserAuditTask.class));
//        task.setStatus(ti.getTaskData().getStatus().name());
//        persistenceContext.persist(task);
        
         AuditTaskImpl auditTaskImpl = persistenceContext.queryWithParametersInTransaction("getAuditTaskById", true, 
				persistenceContext.addParametersToMap("taskId", ti.getId()),
				ClassUtil.<AuditTaskImpl>castClass(AuditTaskImpl.class));
        auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
            
        persistenceContext.persist(auditTaskImpl);
    }

    @Override
    public void afterTaskSuspendedEvent(TaskEvent event) {
        String userId = "";
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = ((TaskContext)event.getTaskContext()).getPersistenceContext();
        if (ti.getTaskData().getActualOwner() != null) {
            userId = ti.getTaskData().getActualOwner().getId();
        }
        persistenceContext.persist(new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.SUSPENDED, userId, new Date()));
        
        // Update Lucene Audit Task
//        UserAuditTask task = persistenceContext.queryWithParametersInTransaction("getUserAuditTaskById", true,
//				persistenceContext.addParametersToMap("taskId", ti.getId()),
//				ClassUtil.<UserAuditTask>castClass(UserAuditTask.class));
//        task.setStatus(ti.getTaskData().getStatus().name());
//        persistenceContext.persist(task);
         AuditTaskImpl auditTaskImpl = persistenceContext.queryWithParametersInTransaction("getAuditTaskById", true, 
				persistenceContext.addParametersToMap("taskId", ti.getId()),
				ClassUtil.<AuditTaskImpl>castClass(AuditTaskImpl.class));
        auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
            
        persistenceContext.persist(auditTaskImpl);
    }

    @Override
    public void afterTaskForwardedEvent(TaskEvent event) {
        String userId = "";
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = ((TaskContext)event.getTaskContext()).getPersistenceContext();
        if (ti.getTaskData().getActualOwner() != null) {
            userId = ti.getTaskData().getActualOwner().getId();
        }
        persistenceContext.persist(new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.FORWARDED, userId, new Date()));
        // Update Lucene Audit Task
//        UserAuditTask task = persistenceContext.queryWithParametersInTransaction("getUserAuditTaskById", true,
//				persistenceContext.addParametersToMap("taskId", ti.getId()),
//				ClassUtil.<UserAuditTask>castClass(UserAuditTask.class));
//        task.setStatus(ti.getTaskData().getStatus().name());
//        persistenceContext.persist(task);
         AuditTaskImpl auditTaskImpl = persistenceContext.queryWithParametersInTransaction("getAuditTaskById", true, 
				persistenceContext.addParametersToMap("taskId", ti.getId()),
				ClassUtil.<AuditTaskImpl>castClass(AuditTaskImpl.class));
        auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
            
        persistenceContext.persist(auditTaskImpl);
    }

    @Override
    public void afterTaskDelegatedEvent(TaskEvent event) {
        String userId = "";
        Task ti = event.getTask();
        TaskPersistenceContext persistenceContext = ((TaskContext)event.getTaskContext()).getPersistenceContext();
        if (ti.getTaskData().getActualOwner() != null) {
            userId = ti.getTaskData().getActualOwner().getId();
        }
        persistenceContext.persist(new TaskEventImpl(ti.getId(), org.kie.internal.task.api.model.TaskEvent.TaskEventType.DELEGATED, userId, new Date()));
        
        // Do I need to remove the USerAuditTask and create a GroupAuditTask in lucene???
//        UserAuditTask task = persistenceContext.queryWithParametersInTransaction("getUserAuditTaskById", true,
//				persistenceContext.addParametersToMap("taskId", ti.getId()),
//				ClassUtil.<UserAuditTask>castClass(UserAuditTask.class));
//        if (task != null) {
//            persistenceContext.remove(task);
//        }
//        StringBuilder sb = new StringBuilder();
//        for (OrganizationalEntity o : ti.getPeopleAssignments().getPotentialOwners()) {
//            sb.append(o.getId());
//        }
//        persistenceContext.persist(new GroupAuditTaskImpl(sb.toString(), ti.getId(), ti.getTaskData().getStatus().name(),
//                ti.getTaskData().getActivationTime(), ti.getName(),
//                ti.getDescription(), ti.getPriority(),
//                (ti.getTaskData().getCreatedBy() == null) ? "" : ti.getTaskData().getCreatedBy().getId(),
//                ti.getTaskData().getCreatedOn(), ti.getTaskData().getExpirationTime(),
//                ti.getTaskData().getProcessInstanceId(), ti.getTaskData().getProcessId(), ti.getTaskData().getProcessSessionId(),
//                ti.getTaskData().getParentId()));
         AuditTaskImpl auditTaskImpl = persistenceContext.queryWithParametersInTransaction("getAuditTaskById", true, 
				persistenceContext.addParametersToMap("taskId", ti.getId()),
				ClassUtil.<AuditTaskImpl>castClass(AuditTaskImpl.class));
        auditTaskImpl.setStatus(ti.getTaskData().getStatus().name());
            
        persistenceContext.persist(auditTaskImpl);
    }

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

}
