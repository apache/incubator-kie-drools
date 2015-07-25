/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.internal.task.api;

import org.kie.api.task.model.Attachment;
import org.kie.api.task.model.Comment;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.PeopleAssignments;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskData;
import org.kie.api.task.model.TaskSummary;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.model.BooleanExpression;
import org.kie.internal.task.api.model.ContentData;
import org.kie.internal.task.api.model.Deadline;
import org.kie.internal.task.api.model.DeadlineSummary;
import org.kie.internal.task.api.model.Deadlines;
import org.kie.internal.task.api.model.Delegation;
import org.kie.internal.task.api.model.EmailNotification;
import org.kie.internal.task.api.model.EmailNotificationHeader;
import org.kie.internal.task.api.model.Escalation;
import org.kie.internal.task.api.model.FaultData;
import org.kie.internal.task.api.model.Language;
import org.kie.internal.task.api.model.Notification;
import org.kie.internal.task.api.model.Reassignment;
import org.kie.internal.task.api.model.TaskDef;

public interface TaskModelFactory {

	Attachment newAttachment();
	
	BooleanExpression newBooleanExpression();
	
	Comment newComment();
	
	ContentData newContentData();
	
	Content newContent();
	
	Deadline newDeadline();
	
	DeadlineSummary newDeadlineSummary();
	
	Deadlines newDeadlines();
	
	Delegation newDelegation();
	
	EmailNotificationHeader newEmailNotificationHeader();
	
	EmailNotification newEmialNotification();
	
	Escalation newEscalation();
	
	FaultData newFaultData();
	
	Group newGroup();
        
    Group newGroup(String id);
	
	I18NText newI18NText();
	
	Language newLanguage();
	
	Notification newNotification();
	
	OrganizationalEntity newOrgEntity();
	
	PeopleAssignments newPeopleAssignments();
	
	Reassignment newReassignment();
	
	TaskData newTaskData();
	
	TaskDef newTaskDef();
	
	Task newTask();
	
	TaskSummary newTaskSummary();
	
	User newUser();
        
	User newUser(String id);
}
