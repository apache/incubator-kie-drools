package org.jbpm.process.workitem.wsht;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.runtime.Environment;
import org.kie.runtime.process.WorkItem;
import org.drools.time.TimeUtils;
import org.jbpm.process.core.timer.BusinessCalendar;
import org.jbpm.task.Deadline;
import org.jbpm.task.Deadlines;
import org.jbpm.task.EmailNotification;
import org.jbpm.task.EmailNotificationHeader;
import org.jbpm.task.Escalation;
import org.jbpm.task.Group;
import org.jbpm.task.I18NText;
import org.jbpm.task.Language;
import org.jbpm.task.Notification;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.Reassignment;
import org.jbpm.task.User;

public class HumanTaskHandlerHelper {
	
	private static final String COMPONENT_SEPARATOR = "\\^";
	private static final String ELEMENT_SEPARATOR = "@";
	private static final String ATTRIBUTES_SEPARATOR = "\\|";
	private static final String ATTRIBUTES_ELEMENTS_SEPARATOR = ",";
	private static final String KEY_VALUE_SEPARATOR = ":";
	
	private static final String[] KNOWN_KEYS = {"users", "groups", "from", "tousers", "togroups", "replyto", "subject","body"}; 
	
	public static Deadlines setDeadlines(WorkItem workItem, List<OrganizationalEntity> businessAdministrators, Environment environment) {
		String notStartedReassign = (String) workItem.getParameter("NotStartedReassign");
		String notStartedNotify = (String) workItem.getParameter("NotStartedNotify");
		String notCompletedReassign = (String) workItem.getParameter("NotCompletedReassign");
		String notCompletedNotify = (String) workItem.getParameter("NotCompletedNotify");
		

	    Deadlines deadlinesTotal = new Deadlines();
	    
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
	                taskDeadline = new Deadline();
	                if (businessCalendar != null) {
	                	taskDeadline.setDate(businessCalendar.calculateBusinessTimeAsDate(expiresAt));
	                } else {
	                	taskDeadline.setDate(new Date(System.currentTimeMillis() + TimeUtils.parseTimeString(expiresAt)));
	                }
	                List<Escalation> escalations = new ArrayList<Escalation>();
	                
	                Escalation escalation = new Escalation();
	                escalations.add(escalation);
	                
	                escalation.setName("Default escalation");
	                
	                taskDeadline.setEscalations(escalations);
	                escalation.setReassignments(parseReassignment(actionComponent));
	                escalation.setNotifications(parseNotifications(actionComponent, businessAdministrators));
	                
	                deadlines.add(taskDeadline);
	            }
	        } else {
	            System.out.println("Incorrect syntax of deadline property");
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
			EmailNotification emailNotification = new EmailNotification();
			notifications.add(emailNotification);

			emailNotification.setBusinessAdministrators(businessAdministrators);

			Map<Language, EmailNotificationHeader> emailHeaders = new HashMap<Language, EmailNotificationHeader>();
			List<I18NText> subjects = new ArrayList<I18NText>();
			List<I18NText> names = new ArrayList<I18NText>();
			List<OrganizationalEntity> notificationRecipients = new ArrayList<OrganizationalEntity>();

			EmailNotificationHeader emailHeader = new EmailNotificationHeader();
			emailHeader.setBody(parameters.get("body"));
			emailHeader.setFrom(parameters.get("from"));
			emailHeader.setReplyTo(parameters.get("replyto"));
			emailHeader.setLanguage(locale);
			emailHeader.setSubject(parameters.get("subject"));

			emailHeaders.put(new Language(locale), emailHeader);

			subjects.add(new I18NText(locale, emailHeader.getSubject()));

			names.add(new I18NText(locale, emailHeader.getSubject()));

			String recipients = parameters.get("tousers");
			if (recipients != null && recipients.trim().length() > 0) {
				String[] recipientsIds = recipients.split(ATTRIBUTES_ELEMENTS_SEPARATOR);

				for (String id : recipientsIds) {
					notificationRecipients.add(new User(id.trim()));
				}

			}
			String groupRecipients = parameters.get("togroups");
			if (groupRecipients != null && groupRecipients.trim().length() > 0) {
				String[] groupRecipientsIds = groupRecipients.split(ATTRIBUTES_ELEMENTS_SEPARATOR);

				for (String id : groupRecipientsIds) {
					notificationRecipients.add(new Group(id.trim()));
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
	        
            Reassignment reassignment = new Reassignment();
            List<OrganizationalEntity> reassignmentUsers = new ArrayList<OrganizationalEntity>();
            String recipients = parameters.get("users");
            if (recipients != null && recipients.trim().length() > 0) {
                String[] recipientsIds = recipients.split(ATTRIBUTES_ELEMENTS_SEPARATOR);
                for (String id: recipientsIds) {
                    reassignmentUsers.add(new User(id.trim()));
                }
            }
            
            recipients = parameters.get("groups");
            if (recipients != null && recipients.trim().length() > 0) {
                String[] recipientsIds = recipients.split(ATTRIBUTES_ELEMENTS_SEPARATOR);
                for (String id: recipientsIds) {
                    reassignmentUsers.add(new Group(id.trim()));
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
