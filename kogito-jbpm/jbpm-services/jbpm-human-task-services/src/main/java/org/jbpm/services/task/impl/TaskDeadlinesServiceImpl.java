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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.seam.transaction.Transactional;
import org.jbpm.services.task.impl.model.ContentImpl;
import org.jbpm.services.task.impl.model.DeadlineImpl;
import org.jbpm.services.task.impl.model.TaskImpl;
import org.jbpm.services.task.query.DeadlineSummaryImpl;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.jbpm.shared.services.cdi.Startup;
import org.kie.api.runtime.Environment;
import org.kie.internal.task.api.TaskDeadlinesService;
import org.kie.internal.task.api.model.Deadline;
import org.kie.internal.task.api.model.Escalation;
import org.kie.internal.task.api.model.Notification;
import org.kie.internal.task.api.model.NotificationEvent;
import org.kie.internal.task.api.model.NotificationType;
import org.kie.internal.task.api.model.Reassignment;
import org.kie.internal.task.api.model.Status;
import org.kie.internal.task.api.model.TaskData;


/**
 *
 *
 */
@Transactional
@ApplicationScoped
@Startup
public class TaskDeadlinesServiceImpl implements TaskDeadlinesService {

    private ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(3);
    private Map<Long, List<ScheduledFuture<ScheduledTaskDeadline>>> startScheduledTaskDeadlines = new ConcurrentHashMap<Long, List<ScheduledFuture<ScheduledTaskDeadline>>>();
    private Map<Long, List<ScheduledFuture<ScheduledTaskDeadline>>> endScheduledTaskDeadlines = new ConcurrentHashMap<Long, List<ScheduledFuture<ScheduledTaskDeadline>>>();

    @Inject 
    private JbpmServicesPersistenceManager pm;
 
    @Inject
    private Event<NotificationEvent> notificationEvents;
    
    private Logger logger = Logger.getLogger(this.getClass().getName());

    
    public TaskDeadlinesServiceImpl() {
    }

    public void setPm(JbpmServicesPersistenceManager pm) {
        this.pm = pm;
    }

    public void setNotificationEvents(Event<NotificationEvent> notificationEvents) {
        this.notificationEvents = notificationEvents;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
    
    

    @PostConstruct
    public void init() {

       // UserTransaction ut = setupEnvironment();
        
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
        //completeOperation(ut,((JbpmServicesPersistenceManagerImpl)pm).getEm());
    }

    private void executeEscalatedDeadline(long taskId, long deadlineId, DeadlineType type) {
       // UserTransaction ut = setupEnvironment();
       // EntityManager entityManager = getEntityManager();
        
        TaskImpl task = (TaskImpl) pm.find(TaskImpl.class, taskId);
        Deadline deadline = (DeadlineImpl) pm.find(DeadlineImpl.class, deadlineId);

        TaskData taskData = task.getTaskData();
        
        
        if (taskData != null) {
            // check if task is still in valid status
            if (type.isValidStatus(taskData.getStatus())) {
                Map<String, Object> variables = null;


                    ContentImpl content = (ContentImpl) pm.find(ContentImpl.class, taskData.getDocumentContentId());

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
        }
       // completeOperation(ut, entityManager);
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

    
    /*
     * following are supporting methods to allow execution on application startup
     * as at that time RequestScoped entity manager cannot be used so instead
     * use EntityMnagerFactory and manage transaction manually
     */
//    protected EntityManager getEntityManager() {
//        try {
//            ((JbpmServicesPersistenceManagerImpl)this.pm).getEm().toString();          
//            return ((JbpmServicesPersistenceManagerImpl)this.pm).getEm();
//        } catch (ContextNotActiveException e) {
//            EntityManager em = ((JbpmServicesPersistenceManagerImpl)this.pm).getEm().getEntityManagerFactory().createEntityManager();
//            return em;
//        }
//    }
//    
//    protected UserTransaction setupEnvironment() {
//        UserTransaction ut = null;
//        try {
//            ((JbpmServicesPersistenceManagerImpl)this.pm).getEm().toString();
//        } catch (ContextNotActiveException e) {
//            try {
//                ut = InitialContext.doLookup("java:comp/UserTransaction");
//            } catch (Exception ex) {
//                try {
//                    ut = InitialContext.doLookup(System.getProperty("jbpm.ut.jndi.lookup", "java:jboss/UserTransaction"));
//                    
//                } catch (Exception e1) {
//                    throw new RuntimeException("Cannot find UserTransaction", e1);
//                }
//            }
//            try {
//                ut.begin();
//            } catch (Exception ex) {
//                
//            }
//        }
//        
//        return ut;
//    }
//    protected void completeOperation(UserTransaction ut, EntityManager entityManager) {
//        if (ut != null) {
//            try {
//                ut.commit();
//                if (entityManager != null) {
//                    entityManager.clear();
//                    entityManager.close();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
