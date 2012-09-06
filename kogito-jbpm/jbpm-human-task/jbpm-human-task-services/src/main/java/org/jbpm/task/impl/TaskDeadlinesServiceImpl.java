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

import java.util.ArrayList;
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
import org.jbpm.task.annotations.TaskPersistence;
import org.jbpm.task.query.DeadlineSummary;

/**
 *
 *
 */
@Transactional
public class TaskDeadlinesServiceImpl implements TaskDeadlinesService {

    private ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(3);
    private Map<Long, List<ScheduledFuture<ScheduledTaskDeadline>>> scheduledTaskDeadlines = new ConcurrentHashMap<Long, List<ScheduledFuture<ScheduledTaskDeadline>>>();
    protected List<Status> validStatuses = new ArrayList<Status>();
    @Inject @TaskPersistence
    private EntityManager em;
    @Inject
    private Event<NotificationEvent> notificationEvents;
    @Inject 
    private Logger logger;
    
    public TaskDeadlinesServiceImpl() {
    }

    @PostConstruct
    public void init() {
        setValidStatuses();
        long now = System.currentTimeMillis();
        List<DeadlineSummary> resultList = em.createNamedQuery("UnescalatedDeadlines").getResultList();
        for (DeadlineSummary summary : resultList) {
            long delay = summary.getDate().getTime() - now;
            schedule(summary.getTaskId(), summary.getDeadlineId(), delay);
        }
    }

    private void executeEscalatedDeadline(long taskId, long deadlineId) {
        Task task = (Task) em.find(Task.class, taskId);
        Deadline deadline = (Deadline) em.find(Deadline.class, deadlineId);

        TaskData taskData = task.getTaskData();
        Content content = null;
        if (taskData != null) {
            content = (Content) em.find(Content.class, taskData.getDocumentContentId());
        }
        if (deadline == null || deadline.getEscalations() == null || !isInValidStatus(task)) {
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
                    //@TODO: this should only send an event and a specific NotificationObserver 
                    //     should be implemented for Email Notifications
                    //executeEmailNotification((EmailNotification) notification, task, content);
                    notificationEvents.fire(new NotificationEvent(notification, task, content));
                }
            }
        }

        deadline.setEscalated(true);
    }

    public void schedule(long taskId, long deadlineId, long delay) {
        ScheduledFuture<ScheduledTaskDeadline> scheduled = scheduler.schedule(new ScheduledTaskDeadline(taskId, deadlineId),
                delay,
                TimeUnit.MILLISECONDS);
        List<ScheduledFuture<ScheduledTaskDeadline>> knownFutures = scheduledTaskDeadlines.get(taskId);
        if (knownFutures == null) {
            knownFutures = new CopyOnWriteArrayList<ScheduledFuture<ScheduledTaskDeadline>>();
        }
        knownFutures.add(scheduled);
        this.scheduledTaskDeadlines.put(taskId, knownFutures);
    }

    public void unschedule(long taskId) {
        List<ScheduledFuture<ScheduledTaskDeadline>> knownFeatures = scheduledTaskDeadlines.remove(taskId);
        if (knownFeatures == null) {
            return;
        }
        Iterator<ScheduledFuture<ScheduledTaskDeadline>> it = knownFeatures.iterator();
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

//     public void executeEmailNotification(EmailNotification notification,
//                                         Task task,
//                                         Content content) {
//
//        // group users into languages
//        Map<String, List<User>> users = new HashMap<String, List<User>>();
//        for ( OrganizationalEntity entity : notification.getBusinessAdministrators() ) {
//            if ( entity instanceof Group ) {
//                buildMapByLanguage( users,
//                                    (Group) entity );
//            } else {
//                buildMapByLanguage( users,
//                                    (User) entity );
//            }
//        }
//
//        for ( OrganizationalEntity entity : notification.getRecipients() ) {
//            if ( entity instanceof Group ) {
//                buildMapByLanguage( users,
//                                    (Group) entity );
//            } else {
//                buildMapByLanguage( users,
//                                    (User) entity );
//            }
//        }
//
//        Map<String, Object> doc = null;
//        if ( content != null ) {
//            Object objectFromBytes = null;
//            try {
//                objectFromBytes = ContentMarshallerHelper.unmarshall( content.getContent(), environment, classLoader);
//
//            } catch (Exception e) {
//                //objectFromBytes = TaskService.eval( new InputStreamReader(new ByteArrayInputStream(content.getContent())) );
//            }
//            if (objectFromBytes instanceof Map) {
//                doc = (Map)objectFromBytes;
//
//            } else {
//
//                doc = new HashMap<String, Object>();
//                doc.put("content", objectFromBytes);
//            }
//        } else {
//            doc = Collections.emptyMap();
//        }
//
//        Map<Language, EmailNotificationHeader> headers = notification.getEmailHeaders();
//        
//        for ( Iterator<Map.Entry<String, List<User>>> it = users.entrySet().iterator(); it.hasNext(); ) {
//            Map.Entry<String, List<User>> entry = it.next();
//            EmailNotificationHeader header = headers.get( new Language(entry.getKey())  );
//
//            Map<String, Object> email = new HashMap<String, Object>();
//            StringBuilder to = new StringBuilder();
//            boolean first = true;
//            for ( User user : entry.getValue() ) {
//                if ( !first ) {
//                    to.append( ';' );
//                }
//                String emailAddress = userInfo.getEmailForEntity( user );
//                to.append( emailAddress );
//                first = false;
//            }
//            email.put( "To",
//                       to.toString() );
//
//            if ( header.getFrom() != null && header.getFrom().trim().length() > 0 ) {
//                email.put( "From",
//                           header.getFrom() );
//            } else {
//                email.put( "From",
//                           from );
//            }
//
//            if ( header.getReplyTo() != null && header.getReplyTo().trim().length() > 0 ) {
//                email.put( "Reply-To",
//                           header.getReplyTo() );
//            } else {
//                email.put( "Reply-To",
//                           replyTo );
//            }
//
//            Map<String, Object> vars = new HashMap<String, Object>();
//            vars.put( "doc",
//                      doc );
//            // add internal items to be able to reference them in templates
//            vars.put("processInstanceId", task.getTaskData().getProcessInstanceId());
//            vars.put("processSessionId", task.getTaskData().getProcessSessionId());
//            vars.put("workItemId", task.getTaskData().getWorkItemId());            
//            vars.put("expirationTime", task.getTaskData().getExpirationTime());
//            vars.put("taskId", task.getId());
//            vars.put("owners", task.getPeopleAssignments().getPotentialOwners());
//            
//            String subject = (String) TemplateRuntime.eval( header.getSubject(),
//                                                            vars );
//            String body = (String) TemplateRuntime.eval( header.getBody(),
//                                                         vars );
//
//            email.put( "Subject",
//                       subject );
//            email.put( "Body",
//                       body );
//
//            WorkItemImpl workItem = new WorkItemImpl();
//            workItem.setParameters( email );
//
//            handler.executeWorkItem( workItem,
//                                     manager );
//        }
//    }
    public class ScheduledTaskDeadline
            implements
            Callable {

        private long taskId;
        private long deadlineId;

        public ScheduledTaskDeadline(long taskId,
                long deadlineId) {
            this.taskId = taskId;
            this.deadlineId = deadlineId;

        }

        public long getTaskId() {
            return taskId;
        }

        public long getDeadlineId() {
            return deadlineId;
        }

        public Object call() throws Exception {

            executeEscalatedDeadline(taskId,
                    deadlineId);

            return null;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (int) (deadlineId ^ (deadlineId >>> 32));
            result = prime * result + (int) (taskId ^ (taskId >>> 32));
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
            return true;
        }
    }

//     private void buildMapByLanguage(Map<String, List<User>> map,
//                                    Group group) {
//        for ( Iterator<OrganizationalEntity> it = userInfo.getMembersForGroup( group ); it.hasNext(); ) {
//            OrganizationalEntity entity = it.next();
//            if ( entity instanceof Group ) {
//                buildMapByLanguage( map,
//                                    (Group) entity );
//            } else {
//                buildMapByLanguage( map,
//                                    (User) entity );
//            }
//        }
//    }
//
//    private void buildMapByLanguage(Map<String, List<User>> map,
//                                    User user) {
//        String language = userInfo.getLanguageForEntity( user );
//        List<User> list = map.get( language );
//        if ( list == null ) {
//            list = new ArrayList<User>();
//            map.put( language,
//                     list );
//        }
//        list.add( user );
//    }
    protected void setValidStatuses() {
        validStatuses.add(Status.Created);
        validStatuses.add(Status.Ready);
        validStatuses.add(Status.Reserved);
        validStatuses.add(Status.InProgress);
        validStatuses.add(Status.Suspended);
    }

    protected boolean isInValidStatus(Task task) {

        if (this.validStatuses.contains(task.getTaskData().getStatus())) {
            return true;
        }
        return false;

    }
}
