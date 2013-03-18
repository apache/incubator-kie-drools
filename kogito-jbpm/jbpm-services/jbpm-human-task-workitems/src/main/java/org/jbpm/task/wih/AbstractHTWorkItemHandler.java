/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jbpm.task.wih;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jbpm.task.impl.model.I18NTextImpl;
import org.jbpm.task.impl.model.TaskDataImpl;
import org.jbpm.task.impl.model.TaskImpl;
import org.jbpm.task.impl.model.UserImpl;
import org.jbpm.task.utils.ContentMarshallerHelper;
import org.jbpm.task.utils.OnErrorAction;
import org.jbpm.task.wih.util.HumanTaskHandlerHelper;
import org.jbpm.task.wih.util.PeopleAssignmentHelper;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.task.api.model.ContentData;
import org.kie.internal.task.api.model.I18NText;
import org.kie.internal.task.api.model.OrganizationalEntity;
import org.kie.internal.task.api.model.PeopleAssignments;
import org.kie.internal.task.api.model.Task;
import org.kie.internal.task.api.model.TaskData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 *
 *
 */
public abstract class AbstractHTWorkItemHandler implements WorkItemHandler {

    private static final Logger logger = LoggerFactory.getLogger(AbstractHTWorkItemHandler.class);
    
    protected OnErrorAction action = OnErrorAction.LOG;

    public AbstractHTWorkItemHandler() {
    }

    public AbstractHTWorkItemHandler( OnErrorAction action) {
        
        this.action = action;
    }

    public void setAction(OnErrorAction action) {
        this.action = action;
    }

    protected Task createTaskBasedOnWorkItemParams(KieSession session, WorkItem workItem) {
        Task task = new TaskImpl();
        String taskName = (String) workItem.getParameter("TaskName");
        if (taskName != null) {
            List<I18NText> names = new ArrayList<I18NText>();
            names.add(new I18NTextImpl("en-UK", taskName));
            task.setNames(names);
        }
        String comment = (String) workItem.getParameter("Comment");
        if (comment == null) {
            comment = "";
        }
        List<I18NText> descriptions = new ArrayList<I18NText>();
        descriptions.add(new I18NTextImpl("en-UK", comment));
        task.setDescriptions(descriptions);
        List<I18NText> subjects = new ArrayList<I18NText>();
        subjects.add(new I18NTextImpl("en-UK", comment));
        task.setSubjects(subjects);
        String priorityString = (String) workItem.getParameter("Priority");
        int priority = 0;
        if (priorityString != null) {
            try {
                priority = new Integer(priorityString);
            } catch (NumberFormatException e) {
                // do nothing
            }
        }
        task.setPriority(priority);
        TaskData taskData = new TaskDataImpl();
        taskData.setWorkItemId(workItem.getId());
        taskData.setProcessInstanceId(workItem.getProcessInstanceId());
        if (session != null && session.getProcessInstance(workItem.getProcessInstanceId()) != null) {
            taskData.setProcessId(session.getProcessInstance(workItem.getProcessInstanceId()).getProcess().getId());
        }
        if (session != null && (session instanceof KieSession)) {
            taskData.setProcessSessionId(((KieSession) session).getId());
        }
        taskData.setSkipable(!"false".equals(workItem.getParameter("Skippable")));
        //Sub Task Data
        Long parentId = (Long) workItem.getParameter("ParentId");
        if (parentId != null) {
            taskData.setParentId(parentId);
        }
        String createdBy = (String) workItem.getParameter("CreatedBy");
        if (createdBy != null && createdBy.trim().length() > 0) {
            taskData.setCreatedBy(new UserImpl(createdBy));
        }
        PeopleAssignmentHelper peopleAssignmentHelper = new PeopleAssignmentHelper();
        peopleAssignmentHelper.handlePeopleAssignments(workItem, task, taskData);
        
        PeopleAssignments peopleAssignments = task.getPeopleAssignments();
        List<OrganizationalEntity> businessAdministrators = peopleAssignments.getBusinessAdministrators();
        
        task.setTaskData(taskData);
        task.setDeadlines(HumanTaskHandlerHelper.setDeadlines(workItem, businessAdministrators, session.getEnvironment()));
        return task;
    }

    protected ContentData createTaskContentBasedOnWorkItemParams(KieSession session, WorkItem workItem) {
        ContentData content = null;
        Object contentObject = workItem.getParameter("Content");
        if (contentObject == null) {
            contentObject = new HashMap<String, Object>(workItem.getParameters());
        }
        if (contentObject != null) {
            Environment env = null;
            if(session != null){
                session.getEnvironment();
            }
            content = ContentMarshallerHelper.marshal(contentObject, env);
        }
        return content;
    }
    
    protected boolean isAutoClaim(WorkItem workItem, Task task) {
        String swimlaneUser = (String) workItem.getParameter("SwimlaneActorId");
        if (swimlaneUser != null  && !"".equals(swimlaneUser)) {
            return true;
        }
        
        return false;
    }

    
    public abstract void executeWorkItem(WorkItem workItem, WorkItemManager manager);

    public abstract void abortWorkItem(WorkItem workItem, WorkItemManager manager);
}
