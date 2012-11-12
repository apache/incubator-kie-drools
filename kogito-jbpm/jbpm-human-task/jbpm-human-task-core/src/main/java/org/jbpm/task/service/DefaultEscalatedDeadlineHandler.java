/**
 * Copyright 2010 JBoss Inc
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

package org.jbpm.task.service;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.drools.process.instance.impl.WorkItemImpl;
import org.jbpm.process.workitem.email.EmailWorkItemHandler;
import org.jbpm.task.Content;
import org.jbpm.task.Deadline;
import org.jbpm.task.EmailNotification;
import org.jbpm.task.EmailNotificationHeader;
import org.jbpm.task.Escalation;
import org.jbpm.task.Group;
import org.jbpm.task.Language;
import org.jbpm.task.Notification;
import org.jbpm.task.NotificationType;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.Reassignment;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.User;
import org.jbpm.task.UserInfo;
import org.jbpm.task.utils.ContentMarshallerHelper;
import org.kie.runtime.Environment;
import org.kie.runtime.process.WorkItemManager;
import org.kie.util.ChainedProperties;
import org.kie.util.ClassLoaderUtil;
import org.mvel2.templates.TemplateRuntime;

public class DefaultEscalatedDeadlineHandler
    implements
    EscalatedDeadlineHandler {

    private UserInfo     userInfo;

    private String       from;

    private String       replyTo;

    EmailWorkItemHandler handler;

    WorkItemManager      manager;
    
    private Environment  environment;
    
    private ClassLoader  classLoader;
    
    protected List<Status> validStatuses = new ArrayList<Status>(); 
    
    public DefaultEscalatedDeadlineHandler(Properties properties) {
        handler = new EmailWorkItemHandler();
        
        String host = properties.getProperty( "mail.smtp.host", "localhost" );
        String port = properties.getProperty( "mail.smtp.port", "25" );    
        String user = properties.getProperty( "mail.smtp.user" );
        String password = properties.getProperty( "mail.smtp.password" ); 
        
        from = properties.getProperty( "from", null );
        replyTo = properties.getProperty( "replyTo", null );
        
        handler.setConnection( host, port, user, password );
        setValidStatuses();
    }
    
    public DefaultEscalatedDeadlineHandler(Properties properties, ClassLoader classLoader) {
        handler = new EmailWorkItemHandler();
        
        String host = properties.getProperty( "mail.smtp.host", "localhost" );
        String port = properties.getProperty( "mail.smtp.port", "25" );    
        String user = properties.getProperty( "mail.smtp.user" );
        String password = properties.getProperty( "mail.smtp.password" ); 
        
        from = properties.getProperty( "from", null );
        replyTo = properties.getProperty( "replyTo", null );
        
        handler.setConnection( host, port, user, password );
        this.classLoader = classLoader;
        setValidStatuses();
    }
    
    public DefaultEscalatedDeadlineHandler() {
        handler = new EmailWorkItemHandler();
        
        ChainedProperties conf = new ChainedProperties("email.conf",  ClassLoaderUtil.getClassLoader( null, getClass(), false ) );
        String host = conf.getProperty( "host", null );
        String port = conf.getProperty( "port", "25" );
        String user = conf.getProperty( "user", null );
        String password = conf.getProperty( "password", null ); 
        
        from = conf.getProperty( "from", null );
        replyTo = conf.getProperty( "replyTo", null );
        
        handler.setConnection( host, port, user, password );
        setValidStatuses();
    }
    
    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public WorkItemManager getManager() {
        return manager;
    }

    public void setManager(WorkItemManager manager) {
        this.manager = manager;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    
    
    public void executeEscalatedDeadline(Task task,
                                         Deadline deadline,
                                         Content content,
                                         TaskService service) {
        if ( deadline == null || deadline.getEscalations() == null || !isInValidStatus(task) ) {
            return;
        }
        
        for ( Escalation escalation : deadline.getEscalations() ) {

            // we won't impl constraints for now
            //escalation.getConstraints()

            // run reassignment first to allow notification to be send to new potential owners
            if ( !escalation.getReassignments().isEmpty()) {
                // get first and ignore the rest.
                Reassignment reassignment = escalation.getReassignments().get( 0 );
                
                task.getTaskData().setStatus( Status.Ready );
                List potentialOwners = new ArrayList( reassignment.getPotentialOwners() );
                task.getPeopleAssignments().setPotentialOwners( potentialOwners );
                task.getTaskData().setActualOwner(null);

            }       
            for ( Notification notification : escalation.getNotifications() ) {
                if ( notification.getNotificationType() == NotificationType.Email) {
                    executeEmailNotification( (EmailNotification) notification, task, content );
                }        
            }
        }
        
        deadline.setEscalated( true );
    }

    public void executeEmailNotification(EmailNotification notification,
                                         Task task,
                                         Content content) {

        // group users into languages
        Map<String, List<User>> users = new HashMap<String, List<User>>();
        for ( OrganizationalEntity entity : notification.getBusinessAdministrators() ) {
            if ( entity instanceof Group ) {
                buildMapByLanguage( users,
                                    (Group) entity );
            } else {
                buildMapByLanguage( users,
                                    (User) entity );
            }
        }

        for ( OrganizationalEntity entity : notification.getRecipients() ) {
            if ( entity instanceof Group ) {
                buildMapByLanguage( users,
                                    (Group) entity );
            } else {
                buildMapByLanguage( users,
                                    (User) entity );
            }
        }

        Map<String, Object> doc = null;

        if ( content != null ) {
            Object objectFromBytes = null;
            try {
                objectFromBytes = ContentMarshallerHelper.unmarshall( content.getContent(), environment, classLoader);

            } catch (Exception e) {
                objectFromBytes = TaskService.eval( new InputStreamReader(new ByteArrayInputStream(content.getContent())) );
            }
            if (objectFromBytes instanceof Map) {
                doc = (Map)objectFromBytes;

            } else {

                doc = new HashMap<String, Object>();
                doc.put("content", objectFromBytes);
            }
        } else {
            doc = Collections.emptyMap();
        }

        Map<Language, EmailNotificationHeader> headers = notification.getEmailHeaders();
        
        for ( Iterator<Entry<String, List<User>>> it = users.entrySet().iterator(); it.hasNext(); ) {
            Entry<String, List<User>> entry = it.next();
            EmailNotificationHeader header = headers.get( new Language(entry.getKey())  );

            Map<String, Object> email = new HashMap<String, Object>();
            StringBuilder to = new StringBuilder();
            boolean first = true;
            for ( User user : entry.getValue() ) {
                if ( !first ) {
                    to.append( ';' );
                }
                String emailAddress = userInfo.getEmailForEntity( user );
                to.append( emailAddress );
                first = false;
            }
            email.put( "To",
                       to.toString() );

            if ( header.getFrom() != null && header.getFrom().trim().length() > 0 ) {
                email.put( "From",
                           header.getFrom() );
            } else {
                email.put( "From",
                           from );
            }

            if ( header.getReplyTo() != null && header.getReplyTo().trim().length() > 0 ) {
                email.put( "Reply-To",
                           header.getReplyTo() );
            } else {
                email.put( "Reply-To",
                           replyTo );
            }

            Map<String, Object> vars = new HashMap<String, Object>();
            vars.put( "doc",
                      doc );
            // add internal items to be able to reference them in templates
            vars.put("processInstanceId", task.getTaskData().getProcessInstanceId());
            vars.put("processSessionId", task.getTaskData().getProcessSessionId());
            vars.put("workItemId", task.getTaskData().getWorkItemId());            
            vars.put("expirationTime", task.getTaskData().getExpirationTime());
            vars.put("taskId", task.getId());
            vars.put("owners", task.getPeopleAssignments().getPotentialOwners());
            
            String subject = (String) TemplateRuntime.eval( header.getSubject(),
                                                            vars );
            String body = (String) TemplateRuntime.eval( header.getBody(),
                                                         vars );

            email.put( "Subject",
                       subject );
            email.put( "Body",
                       body );

            WorkItemImpl workItem = new WorkItemImpl();
            workItem.setParameters( email );

            handler.executeWorkItem( workItem,
                                     manager );

        }
    }

    private void buildMapByLanguage(Map<String, List<User>> map,
                                    Group group) {
        for ( Iterator<OrganizationalEntity> it = userInfo.getMembersForGroup( group ); it.hasNext(); ) {
            OrganizationalEntity entity = it.next();
            if ( entity instanceof Group ) {
                buildMapByLanguage( map,
                                    (Group) entity );
            } else {
                buildMapByLanguage( map,
                                    (User) entity );
            }
        }
    }

    private void buildMapByLanguage(Map<String, List<User>> map,
                                    User user) {
        String language = userInfo.getLanguageForEntity( user );
        List<User> list = map.get( language );
        if ( list == null ) {
            list = new ArrayList<User>();
            map.put( language,
                     list );
        }
        list.add( user );
    }

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
