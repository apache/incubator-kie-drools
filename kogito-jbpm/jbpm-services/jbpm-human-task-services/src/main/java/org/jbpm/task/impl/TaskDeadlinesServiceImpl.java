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
package org.jbpm.task.impl;

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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.seam.transaction.Transactional;
import org.jbpm.task.Content;
import org.jbpm.task.Deadline;
import org.jbpm.task.Escalation;
import org.jbpm.task.Notification;
import org.jbpm.task.NotificationType;
import org.jbpm.task.Reassignment;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.TaskData;
import org.jbpm.task.api.TaskDeadlinesService;
import org.jbpm.task.events.NotificationEvent;
import org.jbpm.task.query.DeadlineSummary;
import org.jbpm.task.utils.ContentMarshallerHelper;
import org.kie.runtime.Environment;

/**
 *
 *
 */
@Transactional
@ApplicationScoped
public class TaskDeadlinesServiceImpl implements TaskDeadlinesService {

    private ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(3);
    private Map<Long, List<ScheduledFuture<ScheduledTaskDeadline>>> startScheduledTaskDeadlines = new ConcurrentHashMap<Long, List<ScheduledFuture<ScheduledTaskDeadline>>>();
    private Map<Long, List<ScheduledFuture<ScheduledTaskDeadline>>> endScheduledTaskDeadlines = new ConcurrentHashMap<Long, List<ScheduledFuture<ScheduledTaskDeadline>>>();

    @Inject 
    private EntityManager em;
    @Inject
    private Event<NotificationEvent> notificationEvents;
    @Inject 
    private Logger logger;
    
    public TaskDeadlinesServiceImpl() {
    }

    @PostConstruct
    public void init() {
        long now = System.currentTimeMillis();
        List<DeadlineSummary> resultList = em.createNamedQuery("UnescalatedStartDeadlines").getResultList();
        for (DeadlineSummary summary : resultList) {
            long delay = summary.getDate().getTime() - now;
            schedule(summary.getTaskId(), summary.getDeadlineId(), delay, DeadlineType.START);
        }
        
        resultList = em.createNamedQuery("UnescalatedEndDeadlines").getResultList();
        for (DeadlineSummary summary : resultList) {
            long delay = summary.getDate().getTime() - now;
            schedule(summary.getTaskId(), summary.getDeadlineId(), delay, DeadlineType.END);
        }
    }

    private void executeEscalatedDeadline(long taskId, long deadlineId, DeadlineType type) {
        Task task = (Task) em.find(Task.class, taskId);
        Deadline deadline = (Deadline) em.find(Deadline.class, deadlineId);

        TaskData taskData = task.getTaskData();
        
        // check if task is still in valid status
        if (type.isValidStatus(taskData.getStatus())) {
            Map<String, Object> variables = null;
            
            if (taskData != null) {
                Content content = (Content) em.find(Content.class, taskData.getDocumentContentId());
                
                if (content != null) {
                    Object objectFromBytes = ContentMarshallerHelper.unmarshall(content.getContent(), getEnvironment(), getClassLoader());
     
                    if (objectFromBytes instanceof Map) {
                        variables = (Map) objectFromBytes;

                    } else {

                        variables = new HashMap<String, Object>();
                        variables.put("content", objectFromBytes);
                    }
                } else {
                    variables = Collections.emptyMap();
                }
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
    
                    task.getTaskData().setStatus(Status.Ready);
                    List potentialOwners = new ArrayList(reassignment.getPotentialOwners());
                    task.getPeopleAssignments().setPotentialOwners(potentialOwners);
                    task.getTaskData().setActualOwner(null);
    
                }
                for (Notification notification : escalation.getNotifications()) {
                    if (notification.getNotificationType() == NotificationType.Email) {
                        logger.log(Level.INFO, " ### Sending an Email");
                        notificationEvents.fire(new NotificationEvent(notification, task, variables));
                    }
                }
            }
        }

        deadline.setEscalated(true);
    }

    public void schedule(long taskId, long deadlineId, long delay, DeadlineType type) {
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

    public void unschedule(long taskId, DeadlineType type) {
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
                logger.log(Level.SEVERE," XXX :Error while cancelling scheduled deadline task for Task with id " + taskId + " -> " + e);
            }
        }
    }
    
    protected Environment getEnvironment() {
        return null;
    }
    
    protected ClassLoader getClassLoader() {
        return null;
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

}
