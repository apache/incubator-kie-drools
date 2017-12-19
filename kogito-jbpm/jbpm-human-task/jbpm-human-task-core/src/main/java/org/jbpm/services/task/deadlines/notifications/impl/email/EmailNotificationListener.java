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
package org.jbpm.services.task.deadlines.notifications.impl.email;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.MimetypesFileTypeMap;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.jbpm.services.task.deadlines.NotificationListener;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.UserInfo;
import org.kie.internal.task.api.model.EmailNotification;
import org.kie.internal.task.api.model.EmailNotificationHeader;
import org.kie.internal.task.api.model.InternalOrganizationalEntity;
import org.kie.internal.task.api.model.Language;
import org.kie.internal.task.api.model.NotificationEvent;
import org.mvel2.templates.TemplateRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailNotificationListener implements NotificationListener {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationListener.class);

    private Session mailSession = EmailSessionProducer.produceSession();
    
    
    @Override
    public void onNotification(NotificationEvent event, UserInfo userInfo) {
        
        if (userInfo == null || mailSession == null) {
            logger.info("Missing mail session or userinfo - skipping email notification listener processing");
            return;
        }
        
        if (event.getNotification() instanceof EmailNotification) {
  
            EmailNotification notification = (EmailNotification) event.getNotification();
            
            Task task = event.getTask();

            // group users into languages
            Map<String, List<User>> users = new HashMap<String, List<User>>();
            for (OrganizationalEntity entity : notification.getBusinessAdministrators()) {
                if (entity instanceof Group) {
                    buildMapByLanguage(users, (Group) entity, userInfo);
                } else {
                    buildMapByLanguage(users, (User) entity, userInfo);
                }
            }

            for (OrganizationalEntity entity : notification.getRecipients()) {
                if (entity instanceof Group) {
                    buildMapByLanguage(users, (Group) entity, userInfo);
                } else {
                    buildMapByLanguage(users, (User) entity, userInfo);
                }
            }

            Map<String, Object> variables = event.getContent();


            Map<? extends Language, ? extends EmailNotificationHeader> headers = notification.getEmailHeaders();

            for (Iterator<Map.Entry<String, List<User>>> it = users.entrySet()
                    .iterator(); it.hasNext();) {
               
                try { 
                    Map.Entry<String, List<User>> entry = it.next();
                    Language lang = TaskModelProvider.getFactory().newLanguage();
                    lang.setMapkey(entry.getKey());
                    EmailNotificationHeader header = headers.get(lang);
    
                    Message msg = new MimeMessage(mailSession);
                    Set<String> toAddresses = new HashSet<String>();
                    for (User user : entry.getValue()) {
    
                        String emailAddress = userInfo.getEmailForEntity(user);
                        if (emailAddress != null && !toAddresses.contains(emailAddress)) {                        	
                        	msg.addRecipients( Message.RecipientType.TO, InternetAddress.parse( emailAddress, false));
                        	toAddresses.add(emailAddress);
                        } else {
                        	logger.warn("Email address not found for user {}", user.getId());
                        }
                    }
                    
    
                    if (header.getFrom() != null && header.getFrom().trim().length() > 0) {
                    	User user = TaskModelProvider.getFactory().newUser();
                    	((InternalOrganizationalEntity) user).setId(header.getFrom());
                        msg.setFrom( new InternetAddress(userInfo.getEmailForEntity(user)));
                    } else {
                        msg.setFrom( new InternetAddress(mailSession.getProperty("mail.from")));
                    }
    
                    if (header.getReplyTo() != null && header.getReplyTo().trim().length() > 0) {
                    	User user = TaskModelProvider.getFactory().newUser();
                    	((InternalOrganizationalEntity) user).setId(header.getReplyTo());
                        msg.setReplyTo( new InternetAddress[] {  
                                new InternetAddress(userInfo.getEmailForEntity(user))});
                    } else if (mailSession.getProperty("mail.replyto") != null) {
                        msg.setReplyTo( new InternetAddress[] {  new InternetAddress(mailSession.getProperty("mail.replyto"))});
                    }
                    
                    Map<String, Object> vars = new HashMap<String, Object>();
                    vars.put("doc", variables);
                    // add internal items to be able to reference them in templates
                    vars.put("processInstanceId", task.getTaskData().getProcessInstanceId());
                    vars.put("processSessionId", task.getTaskData().getProcessSessionId());
                    vars.put("workItemId", task.getTaskData().getWorkItemId());
                    vars.put("expirationTime", task.getTaskData().getExpirationTime());
                    vars.put("taskId", task.getId());
                    if (task.getPeopleAssignments() != null) {
                        vars.put("owners", task.getPeopleAssignments().getPotentialOwners());
                    }
    
                    String subject = (String) TemplateRuntime.eval(header.getSubject(), vars);
                    String body = (String) TemplateRuntime.eval(header.getBody(), vars);
    
                    if (variables.containsKey("attachments")) {
                        Multipart multipart = new MimeMultipart();
                        // prepare body as first mime body part
                        MimeBodyPart messageBodyPart = new MimeBodyPart();
    
                        messageBodyPart.setDataHandler( new DataHandler( new ByteArrayDataSource( body, "text/html" ) ) );         
                        multipart.addBodyPart(messageBodyPart);
                        
                        List<String> attachments = getAttachements(variables.get("attachments"));
                        for (String attachment : attachments) {
                            MimeBodyPart attachementBodyPart = new MimeBodyPart();
                            URL attachmentUrl = getAttachemntURL(attachment);
                            String contentType = MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(attachmentUrl.getFile());
                            attachementBodyPart.setDataHandler(new DataHandler(new ByteArrayDataSource( attachmentUrl.openStream(), contentType ) ));
                            String fileName = new File(attachmentUrl.getFile()).getName();
                            attachementBodyPart.setFileName(fileName);
                            attachementBodyPart.setContentID("<"+fileName+">");
    
                            multipart.addBodyPart(attachementBodyPart);
                        }
                        // Put parts in message
                        msg.setContent(multipart);
                    } else {
                        msg.setDataHandler( new DataHandler( new ByteArrayDataSource( body, "text/html" ) ) );
                    }
                    
                    msg.setSubject( subject );
                    
                    msg.setHeader( "X-Mailer", "jbpm human task service" );
                    msg.setSentDate( new Date() );

                    Transport.send(msg);

                } catch (Exception e) {
                    logger.error("Unable to send email notification due to {}", e.getMessage());
                    logger.debug("Stacktrace:", e);
                }
            }
        }
    }
    
    protected URL getAttachemntURL(String attachment) throws MalformedURLException {
        if (attachment.startsWith("classpath:")) {
            String location = attachment.replaceFirst("classpath:", "");
            return this.getClass().getResource(location);
        } else {
            URL attachmentUrl = new URL(attachment);
            
            return attachmentUrl;
        }
    }
    
    @SuppressWarnings("unchecked")
    protected List<String> getAttachements(Object attachementsFromVariables) { 
        if (attachementsFromVariables instanceof List) {
            return (List<String>) attachementsFromVariables;
        } else {
            String attachementsAsString = attachementsFromVariables.toString();
            return Arrays.asList(attachementsAsString.split(","));
        }
    }
    
    protected void buildMapByLanguage(Map<String, List<User>> map, Group group, UserInfo userInfo) {
    	Iterator<OrganizationalEntity> it = userInfo.getMembersForGroup(group);
    	if (it != null) {
	    	while (it.hasNext()) {
	            OrganizationalEntity entity = it.next();
	            if (entity instanceof Group) {
	                buildMapByLanguage(map, (Group) entity, userInfo);
	            } else {
	                buildMapByLanguage(map, (User) entity, userInfo);
	            }
	        }
    	}
    }

    protected void buildMapByLanguage(Map<String, List<User>> map, User user, UserInfo userInfo) {
        String language = userInfo.getLanguageForEntity(user);
        List<User> list = map.get(language);
        if (list == null) {
            list = new ArrayList<User>();
            map.put(language, list);
        }
        list.add(user);
    }

}
