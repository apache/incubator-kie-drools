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

package org.jbpm.services.task.commands;

import org.jbpm.services.task.assignment.AssignmentService;
import org.jbpm.services.task.assignment.AssignmentServiceProvider;
import org.jbpm.services.task.deadlines.notifications.impl.NotificationListenerManager;
import org.jbpm.services.task.events.TaskEventSupport;
import org.jbpm.services.task.impl.util.DeadlineSchedulerHelper;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskData;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.kie.internal.task.api.TaskDeadlinesService.DeadlineType;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.UserInfo;
import org.kie.internal.task.api.model.Deadline;
import org.kie.internal.task.api.model.Escalation;
import org.kie.internal.task.api.model.InternalPeopleAssignments;
import org.kie.internal.task.api.model.InternalTask;
import org.kie.internal.task.api.model.InternalTaskData;
import org.kie.internal.task.api.model.Notification;
import org.kie.internal.task.api.model.NotificationEvent;
import org.kie.internal.task.api.model.NotificationType;
import org.kie.internal.task.api.model.Reassignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement(name="execute-deadlines-command")
@XmlAccessorType(XmlAccessType.NONE)
public class ExecuteDeadlinesCommand extends TaskCommand<Void> {

	private static final long serialVersionUID = 3140157192156956692L;
	private static final Logger logger = LoggerFactory.getLogger(ExecuteDeadlinesCommand.class);
	
	@XmlElement
	@XmlSchemaType(name="long")
	private Long deadlineId; 
	@XmlElement
	private DeadlineType type;

	
	public ExecuteDeadlinesCommand() {
		
	}
	
	public ExecuteDeadlinesCommand(long taskId, long deadlineId, DeadlineType type) {
		this.taskId = taskId;
		this.deadlineId = deadlineId;
		this.type = type;
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public Void execute(Context context) {
		TaskContext ctx = (TaskContext) context;
		UserInfo userInfo = (UserInfo) context.get(EnvironmentName.TASK_USER_INFO);
		
		TaskPersistenceContext persistenceContext = ctx.getPersistenceContext();
		
		try {
	        Task task = persistenceContext.findTask(taskId);
	        Deadline deadline = persistenceContext.findDeadline(deadlineId);
	        if (task == null || deadline == null) {
	        	return null;
	        }
	        TaskData taskData = task.getTaskData();
	        
	        
	        if (taskData != null) {
	            // check if task is still in valid status
	            if (type.isValidStatus(taskData.getStatus())) {
	                Map<String, Object> variables = null;
	
	
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
	
	                if (deadline == null || deadline.getEscalations() == null ) {
	                    return null;
	                }
	                
	                TaskEventSupport taskEventSupport = ctx.getTaskEventSupport();
	
	                for (Escalation escalation : deadline.getEscalations()) {
	
	                    // we won't impl constraints for now
	                    //escalation.getConstraints()
	
	                    // run reassignment first to allow notification to be send to new potential owners
	                    if (!escalation.getReassignments().isEmpty()) {
	                        
	                        taskEventSupport.fireBeforeTaskReassigned(task, ctx);
	                        
	                        // get first and ignore the rest.
	                        Reassignment reassignment = escalation.getReassignments().get(0);
	                        logger.debug("Reassigning to {}", reassignment.getPotentialOwners());
	                        ((InternalTaskData) task.getTaskData()).setStatus(Status.Ready);
	                        
	                        List<OrganizationalEntity> potentialOwners = new ArrayList<OrganizationalEntity>(reassignment.getPotentialOwners());
	                        ((InternalPeopleAssignments) task.getPeopleAssignments()).setPotentialOwners(potentialOwners);
	                        ((InternalTaskData) task.getTaskData()).setActualOwner(null);
	                        
	                        // use assignment service to directly assign actual owner if enabled
	                        AssignmentService assignmentService = AssignmentServiceProvider.get();
	                        if (assignmentService.isEnabled()) {
	                            assignmentService.assignTask(task, ctx);
	                        }

	                        taskEventSupport.fireAfterTaskReassigned(task, ctx);
	                    }
	                    for (Notification notification : escalation.getNotifications()) {
	                        if (notification.getNotificationType() == NotificationType.Email) {
	                            
	                            taskEventSupport.fireBeforeTaskNotified(task, ctx);
	                            logger.debug("Sending an Email");
	                            NotificationListenerManager.get().broadcast(new NotificationEvent(notification, task, variables), userInfo);
	                            
	                            taskEventSupport.fireAfterTaskNotified(task, ctx);
	                        }
	                    }
	                }
	            }
	            
	        }
	        
	        deadline.setEscalated(true);
	        persistenceContext.updateDeadline(deadline);
	        persistenceContext.updateTask(task);
            DeadlineSchedulerHelper.rescheduleDeadlinesForTask((InternalTask) task, ctx, type);
        } catch (Exception e) {

        	logger.error("Error when executing deadlines", e);
        }
		return null;
	}

}
