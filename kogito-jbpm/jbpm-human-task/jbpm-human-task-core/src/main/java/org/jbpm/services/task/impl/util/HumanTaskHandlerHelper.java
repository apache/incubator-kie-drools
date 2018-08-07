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

package org.jbpm.services.task.impl.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.time.TimeUtils;
import org.jbpm.process.core.timer.BusinessCalendar;
import org.kie.api.runtime.Environment;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.Deadline;
import org.kie.internal.task.api.model.Deadlines;
import org.kie.internal.task.api.model.EmailNotification;
import org.kie.internal.task.api.model.EmailNotificationHeader;
import org.kie.internal.task.api.model.Escalation;
import org.kie.internal.task.api.model.InternalI18NText;
import org.kie.internal.task.api.model.InternalOrganizationalEntity;
import org.kie.internal.task.api.model.Language;
import org.kie.internal.task.api.model.Notification;
import org.kie.internal.task.api.model.Reassignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HumanTaskHandlerHelper {
    private static final Logger logger = LoggerFactory.getLogger(HumanTaskHandlerHelper.class);
	
	private static final String COMPONENT_SEPARATOR = "\\^";
	private static final String ELEMENT_SEPARATOR = "@";
	private static final String ATTRIBUTES_SEPARATOR = "\\|";
	private static final String ATTRIBUTES_ELEMENTS_SEPARATOR = ",";
	private static final String KEY_VALUE_SEPARATOR = ":";
	
	private static final String[] KNOWN_KEYS = {"users", "groups", "from", "tousers", "togroups", "replyto", "subject","body"}; 
	
	public static Deadlines setDeadlines(Map<String, Object> parameters, List<OrganizationalEntity> businessAdministrators, Environment environment) {
		String notStartedReassign = (String) parameters.get("NotStartedReassign");
		String notStartedNotify = (String) parameters.get("NotStartedNotify");
		String notCompletedReassign = (String) parameters.get("NotCompletedReassign");
		String notCompletedNotify = (String) parameters.get("NotCompletedNotify");
		

	    Deadlines deadlinesTotal = TaskModelProvider.getFactory().newDeadlines();
	    
	    List<Deadline> startDeadlines = new ArrayList<Deadline>();
	    startDeadlines.addAll(parseDeadlineString(notStartedNotify, businessAdministrators, environment));
	    startDeadlines.addAll(parseDeadlineString(notStartedReassign, businessAdministrators, environment));
	    List<Deadline> endDeadlines = new ArrayList<Deadline>();
	    endDeadlines.addAll(parseDeadlineString(notCompletedNotify, businessAdministrators, environment));
	    endDeadlines.addAll(parseDeadlineString(notCompletedReassign, businessAdministrators, environment));
	    
	    
	    if(!startDeadlines.isEmpty()) {
	        deadlinesTotal.setStartDeadlines(startDeadlines);
	    }
	    if (!endDeadlines.isEmpty()) {
	        deadlinesTotal.setEndDeadlines(endDeadlines);
	    }

		return deadlinesTotal;
	}
	
	protected static List<Deadline> parseDeadlineString(String deadlineInfo, List<OrganizationalEntity> businessAdministrators, Environment environment) {
		if (deadlineInfo == null || deadlineInfo.length() == 0) {
			return new ArrayList<Deadline>();
		}
        List<Deadline> deadlines = new ArrayList<Deadline>();
        String[] allComponents = deadlineInfo.split(COMPONENT_SEPARATOR);
        BusinessCalendar businessCalendar = null;
        if (environment != null && environment.get("jbpm.business.calendar") != null){
        	businessCalendar = (BusinessCalendar) environment.get("jbpm.business.calendar");
        }
        
        for (String component : allComponents) {
	        String[] mainComponents = component.split(ELEMENT_SEPARATOR);
	        
	        if (mainComponents!= null && mainComponents.length == 2) {
	            String actionComponent = mainComponents[0].substring(1, mainComponents[0].length()-1);
	            String expireComponents = mainComponents[1].substring(1, mainComponents[1].length()-1);
	 
	            String[] expireElements = expireComponents.split(ATTRIBUTES_ELEMENTS_SEPARATOR);
	            Deadline taskDeadline = null;
	            
	            for (String expiresAt : expireElements) {
	            	logger.debug("Expires at is {}", expiresAt);
	                taskDeadline = TaskModelProvider.getFactory().newDeadline();
	                if (businessCalendar != null) {
	                	taskDeadline.setDate(businessCalendar.calculateBusinessTimeAsDate(expiresAt));
	                } else {
	                	taskDeadline.setDate(new Date(System.currentTimeMillis() + TimeUtils.parseTimeString(expiresAt)));
	                }
	                logger.debug("Calculated date of execution is {} and current date {}", taskDeadline.getDate(), new Date());
	                List<Escalation> escalations = new ArrayList<Escalation>();
	                
	                Escalation escalation = TaskModelProvider.getFactory().newEscalation();
	                escalations.add(escalation);
	                
	                escalation.setName("Default escalation");
	                
	                taskDeadline.setEscalations(escalations);
	                escalation.setReassignments(parseReassignment(actionComponent));
	                escalation.setNotifications(parseNotifications(actionComponent, businessAdministrators));
	                
	                deadlines.add(taskDeadline);
	            }
	        } else {
	            logger.warn("Incorrect syntax of deadline property {}", deadlineInfo);
	        }
        }
        return deadlines;
    }
    
	protected static List<Notification> parseNotifications(String notificationString, List<OrganizationalEntity> businessAdministrators) {

		List<Notification> notifications = new ArrayList<Notification>();
		Map<String, String> parameters = asMap(notificationString);
		if (parameters.containsKey("tousers") || parameters.containsKey("togroups")) {
			String locale = parameters.get("locale");
			if (locale == null) {
				locale = "en-UK";
			}
			EmailNotification emailNotification = TaskModelProvider.getFactory().newEmialNotification();
			notifications.add(emailNotification);

			emailNotification.setBusinessAdministrators(new ArrayList<>(businessAdministrators));

			Map<Language, EmailNotificationHeader> emailHeaders = new HashMap<Language, EmailNotificationHeader>();
			List<I18NText> subjects = new ArrayList<I18NText>();
			List<I18NText> names = new ArrayList<I18NText>();
			List<OrganizationalEntity> notificationRecipients = new ArrayList<OrganizationalEntity>();

			EmailNotificationHeader emailHeader = TaskModelProvider.getFactory().newEmailNotificationHeader();
			emailHeader.setBody(parameters.get("body"));
			emailHeader.setFrom(parameters.get("from"));
			emailHeader.setReplyTo(parameters.get("replyto"));
			emailHeader.setLanguage(locale);
			emailHeader.setSubject(parameters.get("subject"));

			Language lang = TaskModelProvider.getFactory().newLanguage();
			lang.setMapkey(locale);
			emailHeaders.put(lang, emailHeader);

			I18NText subject = TaskModelProvider.getFactory().newI18NText();
			((InternalI18NText) subject).setLanguage(locale);
			((InternalI18NText) subject).setText(emailHeader.getSubject());;
			
			subjects.add(subject);
			names.add(subject);

			String recipients = parameters.get("tousers");
			if (recipients != null && recipients.trim().length() > 0) {
				String[] recipientsIds = recipients.split(ATTRIBUTES_ELEMENTS_SEPARATOR);

				for (String id : recipientsIds) {
					User user = TaskModelProvider.getFactory().newUser();
                	((InternalOrganizationalEntity) user).setId(id.trim());
					notificationRecipients.add(user);
				}

			}
			String groupRecipients = parameters.get("togroups");
			if (groupRecipients != null && groupRecipients.trim().length() > 0) {
				String[] groupRecipientsIds = groupRecipients.split(ATTRIBUTES_ELEMENTS_SEPARATOR);

				for (String id : groupRecipientsIds) {
					Group group = TaskModelProvider.getFactory().newGroup();
                	((InternalOrganizationalEntity) group).setId(id.trim());
					notificationRecipients.add(group);
				}
			}

			emailNotification.setEmailHeaders(emailHeaders);
			emailNotification.setNames(names);
			emailNotification.setRecipients(notificationRecipients);
			emailNotification.setSubjects(subjects);

		}

		return notifications;
	}

    protected static List<Reassignment> parseReassignment(String reassignString) {
       
    	List<Reassignment> reassignments = new ArrayList<Reassignment>();
    	Map<String, String> parameters = asMap(reassignString);
    	
    	if (parameters.containsKey("users") || parameters.containsKey("groups")) {
	        
            Reassignment reassignment = TaskModelProvider.getFactory().newReassignment();
            List<OrganizationalEntity> reassignmentUsers = new ArrayList<OrganizationalEntity>();
            String recipients = parameters.get("users");
            if (recipients != null && recipients.trim().length() > 0) {
                String[] recipientsIds = recipients.split(ATTRIBUTES_ELEMENTS_SEPARATOR);
                for (String id: recipientsIds) {
                	User user = TaskModelProvider.getFactory().newUser();
                	((InternalOrganizationalEntity) user).setId(id.trim());
                    reassignmentUsers.add(user);
                }
            }
            
            recipients = parameters.get("groups");
            if (recipients != null && recipients.trim().length() > 0) {
                String[] recipientsIds = recipients.split(ATTRIBUTES_ELEMENTS_SEPARATOR);
                for (String id: recipientsIds) {
                	Group group = TaskModelProvider.getFactory().newGroup();
                	((InternalOrganizationalEntity) group).setId(id.trim());
                    reassignmentUsers.add(group);
                }
            }
            reassignment.setPotentialOwners(reassignmentUsers);
            
            reassignments.add(reassignment);
        }
    	
        
        
        return reassignments;
    }
    
    protected static Map<String, String> asMap(String parsableString) {
        String [] actionElements = parsableString.split(ATTRIBUTES_SEPARATOR);
        Map<String, String> parameters = new HashMap<String, String>();
        
        for (String actionElem : actionElements) {
        	
        	for (String knownKey : KNOWN_KEYS) {
        		if (actionElem.startsWith(knownKey)) {
        			try {
        				parameters.put(knownKey, actionElem.substring(knownKey.length()+KEY_VALUE_SEPARATOR.length()));
        			} catch (IndexOutOfBoundsException e) {
        				parameters.put(knownKey, "");
					}
        		}
        	}
             
        }
        
        return parameters;
    }
}
