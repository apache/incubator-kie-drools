package org.jbpm.services.task.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;

import org.jbpm.services.task.deadlines.NotificationListener;
import org.jbpm.services.task.deadlines.notifications.impl.email.EmailNotificationListener;
import org.jbpm.services.task.query.DeadlineSummaryImpl;
import org.jbpm.services.task.utils.ClassUtil;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskData;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.kie.internal.task.api.TaskDeadlinesService.DeadlineType;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.UserInfo;
import org.kie.internal.task.api.model.Deadline;
import org.kie.internal.task.api.model.EmailNotification;
import org.kie.internal.task.api.model.EmailNotificationHeader;
import org.kie.internal.task.api.model.Escalation;
import org.kie.internal.task.api.model.Language;
import org.kie.internal.task.api.model.Notification;
import org.kie.internal.task.api.model.NotificationEvent;
import org.kie.internal.task.api.model.NotificationType;
import org.mvel2.templates.TemplateRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name="execute-reminder-command")
@XmlAccessorType(XmlAccessType.NONE)
public class ExecuteReminderCommand extends TaskCommand<Void> {
	private static final long serialVersionUID = -1167914440425583650L;
	private static final Logger logger = LoggerFactory.getLogger(ExecuteReminderCommand.class);
	
	private static final String defaultEmailBody="<html> "
    		+ "<body>"
    		+ "<b> You have been assigned to a task ( task_name = ${taskName}, task_id = ${taskId} )</b>"
    		+ "</br>"
    		+ "<b>"
    		+ "Process instance information:"
    		+ "</b>"
    		+ "</hr>"
    		+ "<ul>"
    		+ "<li> DeploymentId : ${deploymentId} </li>"
    		+ "<li> ProcessId : ${processId} </li>" 
    		+ "<li> ProcessInstanceId : ${processInstanceId} </li>" 
    		+ "	</ul>"
    		+ "</b>"
    		+ "</body>"
    		+ "</html>";
    private static final String defaultEmailSubject="You have a task ( ${taskName} ) of process ( ${processId} )";
	@XmlElement
	@XmlSchemaType(name="String")
    private String fromUser;
	@XmlTransient
	private NotificationListener notificationListener;
	
	public ExecuteReminderCommand() {
		
	}
	
	public ExecuteReminderCommand(long taskId,String fromUser) {
		this.taskId = taskId;
		this.fromUser = fromUser;
	}
	
	@Override
	public Void execute(Context context) {
		TaskContext ctx = (TaskContext) context;
		if (notificationListener == null) {
			this.notificationListener = new EmailNotificationListener((UserInfo) context.get(EnvironmentName.TASK_USER_INFO));
		}
		
		TaskPersistenceContext persistenceContext = ctx.getPersistenceContext();
	        
		try {
			Task task = persistenceContext.findTask(taskId);
			TaskData taskData = task.getTaskData();
			
			List<DeadlineSummaryImpl> resultList =null;
			resultList = getAlldeadlines(persistenceContext, taskData);
			
			if( resultList == null || resultList.size() == 0 ){
				if ( taskData.getActualOwner() == null )
					return null ;
        		if ( taskData != null ) {
        		    // check if task is still in valid status
        		    if ( DeadlineType.START.isValidStatus(taskData.getStatus()) || DeadlineType.END.isValidStatus(taskData.getStatus())) {
        		    	logger.debug("Sending an Email");
        		    	Map<String, Object> variables = getVariables(ctx, persistenceContext, task,
        							taskData);
        		        Notification notification = buildDefaultNotification(taskData,task);
        		        notificationListener.onNotification(new NotificationEvent(notification, task, variables));
        		    }
        		}
        	}else{
				for(DeadlineSummaryImpl deadlineSummaryImpl : resultList){
					executedeadLine(ctx, persistenceContext, task, deadlineSummaryImpl, taskData);
				}
        	}
        } catch (Exception e) {

        	logger.error("Error when executing deadlines", e);
        }
		return null;
	}

	private List<DeadlineSummaryImpl> getAlldeadlines(TaskPersistenceContext persistenceContext, TaskData taskData) {
		List<DeadlineSummaryImpl> resultList;
		// get no-completed notification 
		if(Status.InProgress == taskData.getStatus() || Status.Suspended == taskData.getStatus()){
			resultList = persistenceContext.queryWithParametersInTransaction("UnescalatedEndDeadlinesByTaskIdForReminder", 
		            persistenceContext.addParametersToMap("taskId", taskId),
		            ClassUtil.<List<DeadlineSummaryImpl>>castClass(List.class));
		}else{
			// get no-started notification 
			 resultList =persistenceContext.queryWithParametersInTransaction("UnescalatedStartDeadlinesByTaskIdForReminder", 
		                persistenceContext.addParametersToMap("taskId", taskId),
		                ClassUtil.<List<DeadlineSummaryImpl>>castClass(List.class));
		}
		return resultList;
	}

	private Void executedeadLine(TaskContext ctx,
			TaskPersistenceContext persistenceContext, Task task,
			DeadlineSummaryImpl deadlineSummaryImpl,TaskData taskData) {
		Deadline deadline = persistenceContext.findDeadline(deadlineSummaryImpl.getDeadlineId());
		if (task == null || deadline == null) {
			return null;
		}
		
		if (taskData != null) {
		    // check if task is still in valid status
		    if (DeadlineType.START.isValidStatus(taskData.getStatus()) || DeadlineType.END.isValidStatus(taskData.getStatus())) {
		        Map<String, Object> variables = getVariables(ctx, persistenceContext, task, taskData);
		        if (deadline == null || deadline.getEscalations() == null ) {
		            return null;
		        }

		        for (Escalation escalation : deadline.getEscalations()) {
		            for (Notification notification : escalation.getNotifications()) {
		                if (notification.getNotificationType() == NotificationType.Email) {
		                    logger.debug("Sending an Email");
		                    notificationListener.onNotification(new NotificationEvent(notification, task, variables));
		                }
		            }
		        }
		    }
		    
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getVariables(TaskContext ctx, TaskPersistenceContext persistenceContext, Task task,
			TaskData taskData) {
		Map<String, Object> variables;
		Content content = persistenceContext.findContent(taskData.getDocumentContentId());

		if (content != null) {
		    ContentMarshallerContext mContext = ctx.getTaskContentService().getMarshallerContext(task);
		    Object objectFromBytes = ContentMarshallerHelper.unmarshall(content.getContent(), mContext.getEnvironment(), mContext.getClassloader());

		    if (objectFromBytes instanceof Map) {
		        variables = (Map<String, Object>) objectFromBytes;

		    } else {

		        variables = new HashMap<String, Object>();
		        variables.put("content", objectFromBytes);
		    }
		} else {
		    variables = Collections.emptyMap();
		}
		return variables;
	}
	
	private Notification buildDefaultNotification( TaskData taskData,Task task ){
		TaskModelProvider.getFactory().newEmailNotificationHeader();
		TaskModelProvider.getFactory().newLanguage();
		EmailNotification emailNotificationImpl =  TaskModelProvider.getFactory().newEmialNotification();
		Map<Language, EmailNotificationHeader> map = new HashMap<Language, EmailNotificationHeader>();
		EmailNotificationHeader emailNotificationHeaderImpl = TaskModelProvider.getFactory().newEmailNotificationHeader();
		emailNotificationHeaderImpl.setBody(buildDefafultEmailBody(taskData,task));
		emailNotificationHeaderImpl.setFrom(fromUser);
		emailNotificationHeaderImpl.setReplyTo(fromUser);
		emailNotificationHeaderImpl.setLanguage("en-UK");
		emailNotificationHeaderImpl.setSubject(buildDefafultEmailSubject(taskData, task));
		Language language  = TaskModelProvider.getFactory().newLanguage();
		language.setMapkey("en-UK");
		map.put(language, emailNotificationHeaderImpl);
		emailNotificationImpl.setEmailHeaders(map);
		
		List<OrganizationalEntity> recipients = new ArrayList<OrganizationalEntity>();
		recipients.add(taskData.getActualOwner());
		emailNotificationImpl.setRecipients(recipients);
		
		return emailNotificationImpl;
	}
	
	private String buildDefafultEmailBody(TaskData taskData,Task task){
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("taskName", task.getName());
		vars.put("taskId", task.getId());
		vars.put("deploymentId", taskData.getDeploymentId());
		vars.put("processId", taskData.getProcessId());
		vars.put("processInstanceId", taskData.getProcessInstanceId());
		String body = (String) TemplateRuntime.eval(defaultEmailBody, vars);
		return body;
	}
	
	private String buildDefafultEmailSubject(TaskData taskData,Task task){
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("taskName", task.getName());
		vars.put("processId", taskData.getProcessId());
		String body = (String) TemplateRuntime.eval(defaultEmailSubject, vars);
		return body;
	}
}
