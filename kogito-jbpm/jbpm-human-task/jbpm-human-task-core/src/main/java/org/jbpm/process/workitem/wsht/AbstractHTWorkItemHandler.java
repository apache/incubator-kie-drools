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
package org.jbpm.process.workitem.wsht;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.runtime.KnowledgeRuntime;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemHandler;
import org.kie.runtime.process.WorkItemManager;
import org.jbpm.eventmessaging.EventResponseHandler;
import org.jbpm.task.I18NText;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.PeopleAssignments;
import org.jbpm.task.SubTasksStrategy;
import org.jbpm.task.SubTasksStrategyFactory;
import org.jbpm.task.Task;
import org.jbpm.task.TaskData;
import org.jbpm.task.event.TaskEventKey;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.utils.ContentMarshallerHelper;
import org.jbpm.task.utils.OnErrorAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 *
 *
 */
public abstract class AbstractHTWorkItemHandler implements WorkItemHandler {

    private static final Logger logger = LoggerFactory.getLogger(AbstractHTWorkItemHandler.class);
    protected KnowledgeRuntime session;
    protected OnErrorAction action;
    protected Map<TaskEventKey, EventResponseHandler> eventHandlers = new HashMap<TaskEventKey, EventResponseHandler>();

    public AbstractHTWorkItemHandler(KnowledgeRuntime session) {
        this.session = session;
        this.action = OnErrorAction.LOG;
    }

    public AbstractHTWorkItemHandler(KnowledgeRuntime session, OnErrorAction action) {
        this.session = session;
        this.action = action;
    }

    public void setAction(OnErrorAction action) {
        this.action = action;
    }
    
    protected Task createTaskBasedOnWorkItemParams(WorkItem workItem) {
        Task task = new Task();
        String locale = (String) workItem.getParameter("Locale");
        if (locale == null) {
            locale = "en-UK";
        }
        String taskName = (String) workItem.getParameter("TaskName");
        if (taskName != null) {
            List<I18NText> names = new ArrayList<I18NText>();
            names.add(new I18NText(locale, taskName));
            task.setNames(names);
        }
        String comment = (String) workItem.getParameter("Comment");
        if (comment == null) {
            comment = "";
        }
        List<I18NText> descriptions = new ArrayList<I18NText>();
        descriptions.add(new I18NText(locale, comment));
        task.setDescriptions(descriptions);
        List<I18NText> subjects = new ArrayList<I18NText>();
        subjects.add(new I18NText(locale, comment));
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
        TaskData taskData = new TaskData();
        taskData.setWorkItemId(workItem.getId());
        taskData.setProcessInstanceId(workItem.getProcessInstanceId());
        if (session != null && session.getProcessInstance(workItem.getProcessInstanceId()) != null) {
            taskData.setProcessId(session.getProcessInstance(workItem.getProcessInstanceId()).getProcess().getId());
        }
        if (session != null && (session instanceof StatefulKnowledgeSession)) {
            taskData.setProcessSessionId(((StatefulKnowledgeSession) session).getId());
        }
        taskData.setSkipable(!"false".equals(workItem.getParameter("Skippable")));
        //Sub Task Data
        Long parentId = (Long) workItem.getParameter("ParentId");
        if (parentId != null) {
            taskData.setParentId(parentId);
        }
        String subTaskStrategiesCommaSeparated = (String) workItem.getParameter("SubTaskStrategies");
        if (subTaskStrategiesCommaSeparated != null && !subTaskStrategiesCommaSeparated.equals("")) {
            String[] subTaskStrategies = subTaskStrategiesCommaSeparated.split(",");
            List<SubTasksStrategy> strategies = new ArrayList<SubTasksStrategy>();
            for (String subTaskStrategyString : subTaskStrategies) {
                SubTasksStrategy subTaskStrategy = SubTasksStrategyFactory.newStrategy(subTaskStrategyString);
                strategies.add(subTaskStrategy);
            }
            task.setSubTaskStrategies(strategies);
        }        
        
        task.setTaskData(taskData);
        
        PeopleAssignmentHelper peopleAssignmentHelper = new PeopleAssignmentHelper();
        peopleAssignmentHelper.handlePeopleAssignments(workItem, task, taskData);
        
        PeopleAssignments peopleAssignments = task.getPeopleAssignments();
        List<OrganizationalEntity> businessAdministrators = peopleAssignments.getBusinessAdministrators();
        
        task.setDeadlines(HumanTaskHandlerHelper.setDeadlines(workItem, businessAdministrators, session.getEnvironment()));
        return task;
    }

    protected ContentData createTaskContentBasedOnWorkItemParams(WorkItem workItem) {
        ContentData content = null;
        Object contentObject = workItem.getParameter("Content");
        if (contentObject == null) {
            contentObject = new HashMap<String, Object>(workItem.getParameters());
        }
        if (contentObject != null) {
            content = ContentMarshallerHelper.marshal(contentObject, session.getEnvironment());
        }
        return content;
    }

    
    public abstract void executeWorkItem(WorkItem workItem, WorkItemManager manager);

    public abstract void abortWorkItem(WorkItem workItem, WorkItemManager manager);
}
