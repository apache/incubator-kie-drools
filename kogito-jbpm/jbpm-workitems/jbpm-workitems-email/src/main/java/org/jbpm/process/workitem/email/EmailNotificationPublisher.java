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

package org.jbpm.process.workitem.email;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jbpm.runtime.manager.impl.identity.UserDataServiceProvider;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.UserInfo;
import org.kie.internal.utils.NotificationPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EmailNotificationPublisher implements NotificationPublisher {

    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationPublisher.class);
    private boolean active = Boolean.parseBoolean(System.getProperty("org.jbpm.email.publisher.enabled", "true"));
    
    private UserInfo userInfo;
    private Connection connection;
    
    private TemplateManager templateManager = TemplateManager.get();
    
    public EmailNotificationPublisher() {
        this.userInfo = UserDataServiceProvider.getUserInfo();
        Properties conf = new Properties();
        try {
            conf.load(this.getClass().getResourceAsStream("/email.properties")); 
            
            connection = new Connection();
            connection.setHost(conf.getProperty("mail.smtp.host", "localhost"));
            connection.setPort(conf.getProperty("mail.smtp.port", "25"));
            connection.setUserName(conf.getProperty("mail.username", ""));
            connection.setPassword(conf.getProperty("mail.password", ""));
            connection.setStartTls(Boolean.parseBoolean(conf.getProperty("mail.tls", "true")));
        } catch (Exception e) {
            logger.warn("email.properties was not found on classpath");
        } 
    }
    
    public EmailNotificationPublisher(Connection connection, UserInfo userInfo) {
        this.connection = connection;
        this.userInfo = userInfo;
    }
    
    @Override
    public void publish(String sender, String subject, Set<OrganizationalEntity> recipients, String body) {
        Email emailNotification = createEmail(sender, subject, recipients, body, connection);
        
        SendHtml.sendHtml(emailNotification, false);
    }

    @Override
    public void publish(String sender, String subject, Set<OrganizationalEntity> recipients, String template, Map<String, Object> parameters) {
        String body = templateManager.render(template, parameters);
       
        publish(sender, subject, recipients, body);
    }
    
    protected String getEmailAddress(OrganizationalEntity entity) {
        String emailAddress = userInfo.getEmailForEntity(entity);
        if (emailAddress != null) {
            return emailAddress;
        }
        
        return entity.getId();
    }
    
    protected Email createEmail(String sender, String subject, Set<OrganizationalEntity> recipients, String body, Connection connection) {
        Email email = new Email();
        Message message = new Message();
        message.setFrom(sender);

        Set<String> emailRecipients = new LinkedHashSet<>();
        for (OrganizationalEntity entity : recipients) {            
            collectRecipients(emailRecipients, entity);
        }
        
        
        // Set recipients
        Recipients mrecipients = new Recipients();        
        if (emailRecipients.isEmpty() ) {
            throw new RuntimeException( "Email must have one or more recipients" );
        }
        for (String recipientAddress : emailRecipients) {            
            Recipient recipient = new Recipient();
            recipient.setEmail(recipientAddress);
            recipient.setType( "To" );
            mrecipients.addRecipient(recipient);            
        }

        // Fill message
        message.setRecipients(mrecipients);
        message.setSubject(subject);
        message.setBody(body);

        // setup email
        email.setMessage(message);
        email.setConnection(connection);

        return email;
    }
    
    protected void collectRecipients(Set<String> emailRecipients, OrganizationalEntity entity) {
        
        if (entity instanceof User) {
            String recipientAddress = getEmailAddress(entity);
            
            emailRecipients.add(recipientAddress);
        } else if (entity instanceof Group) {
            Iterator<OrganizationalEntity> members = userInfo.getMembersForGroup((Group) entity);
            if (members != null) {
                while (members.hasNext()) {
                    OrganizationalEntity member = (OrganizationalEntity) members.next();
                    collectRecipients(emailRecipients, member);
                }
            }
        }
    
    }

    @Override
    public boolean isActive() {
        return active;
    }

}
