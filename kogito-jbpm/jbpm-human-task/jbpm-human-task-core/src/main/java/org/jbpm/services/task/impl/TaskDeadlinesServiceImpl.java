    /*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.services.task.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.drools.core.time.TimerService;
import org.drools.core.time.Trigger;
import org.drools.core.time.impl.IntervalTrigger;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.process.core.timer.NamedJobContext;
import org.jbpm.process.core.timer.TimerServiceRegistry;
import org.jbpm.process.core.timer.impl.GlobalTimerService;
import org.jbpm.services.task.impl.model.ContentImpl;
import org.jbpm.services.task.impl.model.DeadlineImpl;
import org.jbpm.services.task.impl.model.TaskImpl;
import org.jbpm.services.task.query.DeadlineSummaryImpl;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.jbpm.shared.services.impl.JbpmJTATransactionManager;
import org.jbpm.shared.services.impl.JbpmServicesPersistenceManagerImpl;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskData;
import org.kie.commons.services.cdi.Startup;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.kie.internal.task.api.TaskContentService;
import org.kie.internal.task.api.TaskDeadlinesService;
import org.kie.internal.task.api.TaskQueryService;
import org.kie.internal.task.api.model.Deadline;
import org.kie.internal.task.api.model.Deadlines;
import org.kie.internal.task.api.model.Escalation;
import org.kie.internal.task.api.model.InternalPeopleAssignments;
import org.kie.internal.task.api.model.InternalTaskData;
import org.kie.internal.task.api.model.Notification;
import org.kie.internal.task.api.model.NotificationEvent;
import org.kie.internal.task.api.model.NotificationType;
import org.kie.internal.task.api.model.Reassignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 */
@Transactional
@Startup
@Singleton
public class TaskDeadlinesServiceImpl implements TaskDeadlinesService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskDeadlinesServiceImpl.class);

    // use single ThreadPoolExecutor for all instances of task services within same JVM
    private static ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(3);
    private Map<Long, List<ScheduledFuture<ScheduledTaskDeadline>>> startScheduledTaskDeadlines = new ConcurrentHashMap<Long, List<ScheduledFuture<ScheduledTaskDeadline>>>();
    private Map<Long, List<ScheduledFuture<ScheduledTaskDeadline>>> endScheduledTaskDeadlines = new ConcurrentHashMap<Long, List<ScheduledFuture<ScheduledTaskDeadline>>>();
    private Map<String, JobHandle> jobHandles = new ConcurrentHashMap<String, JobHandle>();

    @Inject 
    private JbpmServicesPersistenceManager pm;
    @Inject
    private TaskContentService taskContentService;
    @Inject
    private TaskQueryService taskQueryService;
    @Inject
    private Event<NotificationEvent> notificationEvents;   

    
    public TaskDeadlinesServiceImpl() {
        
    }

    public void setPm(JbpmServicesPersistenceManager pm) {
        this.pm = pm;
    }

    public void setNotificationEvents(Event<NotificationEvent> notificationEvents) {
        this.notificationEvents = notificationEvents;
    } 

    public void setTaskContentService(TaskContentService taskContentService) {
        this.taskContentService = taskContentService;
    }


    @PostConstruct
    public void init() {
        // make sure it has tx manager as it runs as background thread - no request scope available
        if (!((JbpmServicesPersistenceManagerImpl) pm).hasTransactionManager()) {
            ((JbpmServicesPersistenceManagerImpl) pm).setTransactionManager(new JbpmJTATransactionManager());
        }
        
        long now = System.currentTimeMillis();
        List<DeadlineSummaryImpl> resultList = (List<DeadlineSummaryImpl>)pm.queryInTransaction("UnescalatedStartDeadlines");
        for (DeadlineSummaryImpl summary : resultList) {
            long delay = summary.getDate().getTime() - now;
            schedule(summary.getTaskId(), summary.getDeadlineId(), delay, DeadlineType.START);

        }
        
        resultList = (List<DeadlineSummaryImpl>)pm.queryInTransaction("UnescalatedEndDeadlines");
        for (DeadlineSummaryImpl summary : resultList) {
            long delay = summary.getDate().getTime() - now;
            schedule(summary.getTaskId(), summary.getDeadlineId(), delay, DeadlineType.END);
        }
    }

    private void executeEscalatedDeadline(long taskId, long deadlineId, DeadlineType type) {
        // make sure it has tx manager as it runs as background thread - no request scope available
        if (!((JbpmServicesPersistenceManagerImpl) pm).hasTransactionManager()) {
            ((JbpmServicesPersistenceManagerImpl) pm).setTransactionManager(new JbpmJTATransactionManager());
        }
        
        TaskImpl task = (TaskImpl) pm.find(TaskImpl.class, taskId);
        Deadline deadline = (DeadlineImpl) pm.find(DeadlineImpl.class, deadlineId);

        TaskData taskData = task.getTaskData();
        
        
        if (taskData != null) {
            // check if task is still in valid status
            if (type.isValidStatus(taskData.getStatus())) {
                Map<String, Object> variables = null;


                    ContentImpl content = (ContentImpl) pm.find(ContentImpl.class, taskData.getDocumentContentId());

                    if (content != null) {
                        ContentMarshallerContext context = taskContentService.getMarshallerContext(task);
                        Object objectFromBytes = ContentMarshallerHelper.unmarshall(content.getContent(), context.getEnvironment(), context.getClassloader());

                        if (objectFromBytes instanceof Map) {
                            variables = (Map) objectFromBytes;

                        } else {

                            variables = new HashMap<String, Object>();
                            variables.put("content", objectFromBytes);
                        }
                    } else {
                        variables = Collections.emptyMap();
                    }

                if (deadline == null || deadline.getEscalations() == null ) {
                    return;
                }

                for (Escalation escalation : deadline.getEscalations()) {

                    // we won't impl constraints for now
                    //escalation.getConstraints()

                    // run reassignment first to allow notification to be send to new potential owners
                    if (!escalation.getReassignments().isEmpty()) {
                        // get first and ignore the rest.
                        Reassignment reassignment = escalation.getReassignments().get(0);
                        logger.debug("Reassigning to {}", reassignment.getPotentialOwners());
                        ((InternalTaskData) task.getTaskData()).setStatus(Status.Ready);
                        List potentialOwners = new ArrayList(reassignment.getPotentialOwners());
                        ((InternalPeopleAssignments) task.getPeopleAssignments()).setPotentialOwners(potentialOwners);
                        ((InternalTaskData) task.getTaskData()).setActualOwner(null);

                    }
                    for (Notification notification : escalation.getNotifications()) {
                        if (notification.getNotificationType() == NotificationType.Email) {
                            logger.debug("Sending an Email");
                            notificationEvents.fire(new NotificationEvent(notification, task, variables));
                        }
                    }
                }
            }
            
        }
        
        deadline.setEscalated(true);
    }

    public void schedule(long taskId, long deadlineId, long delay, DeadlineType type) {
        TaskImpl task = (TaskImpl) pm.find(TaskImpl.class, taskId);
        String deploymentId = task.getTaskData().getDeploymentId();

        TimerService timerService = TimerServiceRegistry.getInstance().get(deploymentId + TimerServiceRegistry.TIMER_SERVICE_SUFFIX);
        if (timerService != null && timerService instanceof GlobalTimerService) {
            TaskDeadlineJob deadlineJob = new TaskDeadlineJob(taskId, deadlineId, type);
            Trigger trigger = new IntervalTrigger( timerService.getCurrentTime(),
                    null,
                    null,
                    -1,
                    delay,
                    0,
                    null,
                    null ) ;
            JobHandle handle = timerService.scheduleJob(deadlineJob, new TaskDeadlineJobContext(deadlineJob.getId()), trigger);
            logger.debug( "scheduling timer job for deadline {} and task {}  using timer service {}", deadlineJob.getId(), taskId, timerService);
            jobHandles.put(deadlineJob.getId(), handle);

        } else {
            ScheduledFuture<ScheduledTaskDeadline> scheduled = scheduler.schedule(new ScheduledTaskDeadline(taskId, deadlineId, type), delay, TimeUnit.MILLISECONDS);
            
            List<ScheduledFuture<ScheduledTaskDeadline>> knownFutures = null;
            if (type == DeadlineType.START) {
                knownFutures = this.startScheduledTaskDeadlines.get(taskId);
            } else if (type == DeadlineType.END) {
                knownFutures = this.endScheduledTaskDeadlines.get(taskId);
            }
            if (knownFutures == null) {
                knownFutures = new CopyOnWriteArrayList<ScheduledFuture<ScheduledTaskDeadline>>();
            }

            knownFutures.add(scheduled);
                        
            if (type == DeadlineType.START) {
                this.startScheduledTaskDeadlines.put(taskId, knownFutures);
            } else if (type == DeadlineType.END) {
                this.endScheduledTaskDeadlines.put(taskId, knownFutures);
            }
        }

    }

    public void unschedule(long taskId, DeadlineType type) {
        TaskImpl task = (TaskImpl) pm.find(TaskImpl.class, taskId);
        String deploymentId = task.getTaskData().getDeploymentId();
        
        Deadlines deadlines = ((TaskImpl)task).getDeadlines();

        TimerService timerService = TimerServiceRegistry.getInstance().get(deploymentId + TimerServiceRegistry.TIMER_SERVICE_SUFFIX);
        if (timerService != null && timerService instanceof GlobalTimerService) {
 
            if (type == DeadlineType.START) {
                List<Deadline> startDeadlines = deadlines.getStartDeadlines();
                List<DeadlineSummaryImpl> resultList = (List<DeadlineSummaryImpl>)pm.queryInTransaction("UnescalatedStartDeadlines");
                for (DeadlineSummaryImpl summary : resultList) {
                    TaskDeadlineJob deadlineJob = new TaskDeadlineJob(summary.getTaskId(), summary.getDeadlineId(), DeadlineType.START);
                    logger.debug("unscheduling timer job for deadline {} and task {}  using timer service {}", deadlineJob.getId(), taskId, timerService);
                    JobHandle jobHandle = jobHandles.remove(deadlineJob.getId()); 
                    if (jobHandle == null) {        
                        jobHandle = ((GlobalTimerService) timerService).buildJobHandleForContext(new TaskDeadlineJobContext(deadlineJob.getId()));
                    }
                    timerService.removeJob(jobHandle);
                    // mark the deadlines so they won't be rescheduled again
                    for (Deadline deadline : startDeadlines) {
                        if (deadline.getId() == summary.getDeadlineId()) {
                            deadline.setEscalated(true);
                        }
                    }
                }
            } else if (type == DeadlineType.END) {
                List<Deadline> endDeadlines = deadlines.getStartDeadlines();
                List<DeadlineSummaryImpl> resultList = (List<DeadlineSummaryImpl>)pm.queryInTransaction("UnescalatedEndDeadlines");
                for (DeadlineSummaryImpl summary : resultList) {
                    
                    TaskDeadlineJob deadlineJob = new TaskDeadlineJob(summary.getTaskId(), summary.getDeadlineId(), DeadlineType.END);
                    logger.debug("unscheduling timer job for deadline {} and task {}  using timer service {}", deadlineJob.getId(), taskId, timerService);
                    JobHandle jobHandle = jobHandles.remove(deadlineJob.getId()); 
                    if (jobHandle == null) {        
                        jobHandle = ((GlobalTimerService) timerService).buildJobHandleForContext(new TaskDeadlineJobContext(deadlineJob.getId()));
                    }
                    timerService.removeJob(jobHandle);
                    // mark the deadlines so they won't be rescheduled again
                    for (Deadline deadline : endDeadlines) {
                        if (deadline.getId() == summary.getDeadlineId()) {
                            deadline.setEscalated(true);
                        }
                    }
                }
            }
            
        } else {
            List<ScheduledFuture<ScheduledTaskDeadline>> knownFutures = null;
            if (type == DeadlineType.START) {
                knownFutures = this.startScheduledTaskDeadlines.get(taskId);
            } else if (type == DeadlineType.END) {
                knownFutures = this.endScheduledTaskDeadlines.get(taskId);
            }
            if (knownFutures == null) {
                return;
            }
            Iterator<ScheduledFuture<ScheduledTaskDeadline>> it = knownFutures.iterator();
            while (it.hasNext()) {
                ScheduledFuture<ScheduledTaskDeadline> scheduled = it.next();
                try {
                    if (!scheduled.isDone() && !scheduled.isCancelled()) {
                        scheduled.cancel(true);
                    }
    
                } catch (Exception e) {
                    logger.error("Error while cancelling scheduled deadline task for Task with id {} -> {}", taskId, e);
                }
            }
        }
    }

    public class ScheduledTaskDeadline implements
            Callable, Serializable {

        private static final long serialVersionUID = 1L;

        private long taskId;
        private long deadlineId;
        private DeadlineType type;

        public ScheduledTaskDeadline(long taskId,
                long deadlineId, DeadlineType type) {
            this.taskId = taskId;
            this.deadlineId = deadlineId;
            this.type = type;
        }

        public long getTaskId() {
            return taskId;
        }

        public long getDeadlineId() {
            return deadlineId;
        }
        
        public DeadlineType getType() {
            return this.type;
        }

        public Object call() throws Exception {

            executeEscalatedDeadline(taskId, deadlineId, type);

            return null;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (int) (deadlineId ^ (deadlineId >>> 32));
            result = prime * result + (int) (taskId ^ (taskId >>> 32));
            result = prime * result + type.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof ScheduledTaskDeadline)) {
                return false;
            }
            ScheduledTaskDeadline other = (ScheduledTaskDeadline) obj;
            if (deadlineId != other.deadlineId) {
                return false;
            }
            if (taskId != other.taskId) {
                return false;
            }
            if (type == null) {
                if (other.getType() != null) {
                    return false;
                }
            } else if (type.equals(other.getType())) {
                return false;
            }
            return true;
        }
    }
    
    private class TaskDeadlineJob implements Job, Serializable {

        private static final long serialVersionUID = -2453658968872574615L;
        private long taskId;
        private long deadlineId;
        private DeadlineType type;

        public TaskDeadlineJob(long taskId, long deadlineId, DeadlineType type) {
            this.taskId = taskId;
            this.deadlineId = deadlineId;
            this.type = type;
        }

        public long getTaskId() {
            return taskId;
        }

        public long getDeadlineId() {
            return deadlineId;
        }
        
        public DeadlineType getType() {
            return this.type;
        }
        @Override
        public void execute(JobContext ctx) {
            
            executeEscalatedDeadline(taskId, deadlineId, type);
            
        }
        
        public String getId() {
            return taskId +"_"+deadlineId+"_"+type;
        }
        
    }
    
    private class TaskDeadlineJobContext implements NamedJobContext {
        private JobHandle jobHandle;
        private String jobName;
        
        public TaskDeadlineJobContext(String jobName) {
            this.jobName = jobName;
        }
        
        @Override
        public void setJobHandle(JobHandle jobHandle) {
            this.jobHandle = jobHandle;
        }

        @Override
        public JobHandle getJobHandle() {
            return jobHandle;
        }

        @Override
        public String getJobName() {
            return jobName;
        }
        
    }

    public void setTaskQueryService(TaskQueryService taskQueryService) {
        this.taskQueryService = taskQueryService;
    }

}
