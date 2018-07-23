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
package org.jbpm.services.task.wih;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.ClassObjectFilter;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.jbpm.process.core.timer.DateTimeUtils;
import org.jbpm.services.task.impl.util.HumanTaskHandlerHelper;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.jbpm.services.task.utils.OnErrorAction;
import org.jbpm.services.task.wih.util.PeopleAssignmentHelper;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.CaseData;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.PeopleAssignments;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.ContentData;
import org.kie.internal.task.api.model.InternalI18NText;
import org.kie.internal.task.api.model.InternalOrganizationalEntity;
import org.kie.internal.task.api.model.InternalTask;
import org.kie.internal.task.api.model.InternalTaskData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 *
 *
 */
public abstract class AbstractHTWorkItemHandler implements WorkItemHandler {

    private static final Logger logger = LoggerFactory.getLogger(AbstractHTWorkItemHandler.class);
    
    protected static final String ADMIN_USER = System.getProperty("org.jbpm.ht.admin.user", "Administrator");
    
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
        InternalTask task = (InternalTask) TaskModelProvider.getFactory().newTask();
        String taskName = (String) workItem.getParameter("NodeName");
        CaseData caseFile = null;
        
        String locale = (String) workItem.getParameter("Locale");
        if (locale == null) {
            locale = "en-UK";
        }
        
        if (taskName != null) {
            List<I18NText> names = new ArrayList<I18NText>();
            I18NText text = TaskModelProvider.getFactory().newI18NText();
            ((InternalI18NText) text).setLanguage(locale);
            ((InternalI18NText) text).setText(taskName);
            names.add(text);
            task.setNames(names);
        }
        task.setName(taskName);
        // this should be replaced by FormName filled by designer
        // TaskName shouldn't be trimmed if we are planning to use that for the task lists
        String formName = (String) workItem.getParameter("TaskName"); 
        if(formName != null){
            task.setFormName(formName);
        }
        
        String comment = (String) workItem.getParameter("Comment");
        if (comment == null) {
            comment = "";
        }
        
        String description = (String) workItem.getParameter("Description");
        if (description == null) {
            description = comment;
        }
        
        List<I18NText> descriptions = new ArrayList<I18NText>();
        I18NText descText = TaskModelProvider.getFactory().newI18NText();
        ((InternalI18NText) descText).setLanguage(locale);
        ((InternalI18NText) descText).setText(description);
        descriptions.add(descText);
        task.setDescriptions(descriptions);
        
        task.setDescription(description);
        
        List<I18NText> subjects = new ArrayList<I18NText>();
        I18NText subjectText = TaskModelProvider.getFactory().newI18NText();
        ((InternalI18NText) subjectText).setLanguage(locale);
        ((InternalI18NText) subjectText).setText(comment);
        subjects.add(subjectText);
        task.setSubjects(subjects);
        
        task.setSubject(comment);
        
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
        
        InternalTaskData taskData = (InternalTaskData) TaskModelProvider.getFactory().newTaskData();        
        taskData.setWorkItemId(workItem.getId());
        taskData.setProcessInstanceId(workItem.getProcessInstanceId());
        if (session != null) {
            if (session.getProcessInstance(workItem.getProcessInstanceId()) != null) {
                taskData.setProcessId(session.getProcessInstance(workItem.getProcessInstanceId()).getProcess().getId());
                String deploymentId = ((WorkItemImpl) workItem).getDeploymentId();
                taskData.setDeploymentId(deploymentId);            
            }
            if (session instanceof KieSession) {
                taskData.setProcessSessionId(((KieSession) session).getIdentifier());
            }
            @SuppressWarnings("unchecked")
            Collection<CaseData> caseFiles = (Collection<CaseData>) session.getObjects(new ClassObjectFilter(CaseData.class));
            if (caseFiles != null && caseFiles.size() == 1) {
                caseFile = caseFiles.iterator().next();
            }
        }
        taskData.setSkipable(!"false".equals(workItem.getParameter("Skippable")));
        //Sub Task Data
        Long parentId = (Long) workItem.getParameter("ParentId");
        if (parentId != null) {
            taskData.setParentId(parentId);
        }
        
        String createdBy = (String) workItem.getParameter("CreatedBy");
        if (createdBy != null && createdBy.trim().length() > 0) {
        	User user = TaskModelProvider.getFactory().newUser();
        	((InternalOrganizationalEntity) user).setId(createdBy);
            taskData.setCreatedBy(user);            
        }
        String dueDateString = (String) workItem.getParameter("DueDate");
        Date date = null;
        if(dueDateString != null && !dueDateString.isEmpty()){
            if(DateTimeUtils.isPeriod(dueDateString)){
                Long longDateValue = DateTimeUtils.parseDateAsDuration(dueDateString.substring(1));
                date = new Date(System.currentTimeMillis() + longDateValue);
            }else{
                date = new Date(DateTimeUtils.parseDateTime(dueDateString));
            }
        }
        if(date != null){
            taskData.setExpirationTime(date);
        }
        
        PeopleAssignmentHelper peopleAssignmentHelper = new PeopleAssignmentHelper(caseFile);
        peopleAssignmentHelper.handlePeopleAssignments(workItem, task, taskData);
        
        PeopleAssignments peopleAssignments = task.getPeopleAssignments();
        List<OrganizationalEntity> businessAdministrators = peopleAssignments.getBusinessAdministrators();
        
        taskData.initialize();
        task.setTaskData(taskData);
        task.setDeadlines(HumanTaskHandlerHelper.setDeadlines(workItem.getParameters(), businessAdministrators, session.getEnvironment()));
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
                env = session.getEnvironment();
            }
            content = ContentMarshallerHelper.marshal(null, contentObject, env);
        }
        return content;
    }
    
    protected Map<String, Object> createTaskDataBasedOnWorkItemParams(KieSession session, WorkItem workItem) {
        Map<String, Object> data = new HashMap<String, Object>();
        Object contentObject = workItem.getParameter("Content");
        if (contentObject == null) {
            data = new HashMap<String, Object>(workItem.getParameters());
        } else {
            data.put("Content", contentObject);
        }
        
        return data;
    }
    
    protected boolean isAutoClaim(KieSession session, WorkItem workItem, Task task) {
        String autoclaim = (String) session.getEnvironment().get("Autoclaim");
        
        if(autoclaim != null && !Boolean.parseBoolean(autoclaim.trim())) {
            return false; 
        } else {
             String swimlaneUser = (String) workItem.getParameter("SwimlaneActorId");
             if (swimlaneUser != null  && !"".equals(swimlaneUser) && task.getTaskData().getStatus() == Status.Ready) {
                 return true;
             }
        }
        return false;
    }

    
    public abstract void executeWorkItem(WorkItem workItem, WorkItemManager manager);

    public abstract void abortWorkItem(WorkItem workItem, WorkItemManager manager);
}
