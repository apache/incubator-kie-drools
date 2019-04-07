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
package org.jbpm.services.task.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jbpm.services.task.impl.model.GroupImpl;
import org.jbpm.services.task.impl.model.I18NTextImpl;
import org.jbpm.services.task.impl.model.PeopleAssignmentsImpl;
import org.jbpm.services.task.impl.model.TaskDataImpl;
import org.jbpm.services.task.impl.model.TaskImpl;
import org.jbpm.services.task.impl.model.UserImpl;
import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;

public class TaskFluent {

    private TaskImpl task;
    private PeopleAssignmentsImpl assignments;
    private List<I18NText> names;
    private List<I18NText> descriptions;
    private List<I18NText> subjects;

    public TaskFluent() {
        if (task == null) {
            task = new TaskImpl();
            task.setTaskData(new TaskDataImpl());
            assignments = new PeopleAssignmentsImpl();
            task.setPeopleAssignments(assignments);
            names = new ArrayList<I18NText>();
            task.setNames(names);
            descriptions = new ArrayList<I18NText>();
            task.setDescriptions(descriptions);
            subjects = new ArrayList<I18NText>();
            task.setSubjects(subjects);
        }
    }

    public Task getTask() {
        if(((TaskDataImpl)task.getTaskData()).getCreatedOn() == null){
            ((TaskDataImpl)task.getTaskData()).setCreatedOn(new Date());
        }
        return task;
    }

    public TaskFluent setName(String name) {
        task.setName(name);
        return this;
    }
    
    public TaskFluent setDescription(String description) {
        task.setDescription(description);
        return this;
    }
    
    public TaskFluent setSubject(String subject) {
        task.setSubject(subject);
        return this;
    }
    
    public TaskFluent setWorkItemId(long workItemId){
        ((TaskDataImpl)task.getTaskData()).setWorkItemId(workItemId);
        return this;
    }
    
    public TaskFluent setWorkItemId(String deploymentId){
        ((TaskDataImpl)task.getTaskData()).setDeploymentId(deploymentId);
        return this;
    }
    
    public TaskFluent setProcessId(String processId){
        ((TaskDataImpl)task.getTaskData()).setProcessId(processId);
        return this;
    }
    
    public TaskFluent setProcessInstanceId(long processInstanceId){
        ((TaskDataImpl)task.getTaskData()).setProcessInstanceId(processInstanceId);
        return this;
    }
    
    public TaskFluent setProcessSessionId(int processSessionId){
        ((TaskDataImpl)task.getTaskData()).setProcessSessionId(processSessionId);
        return this;
    }
    
    public TaskFluent setParentId(long parentId){
        ((TaskDataImpl)task.getTaskData()).setParentId(parentId);
        return this;
    }
    
    public TaskFluent setCreatedBy(String userId){
        ((TaskDataImpl)task.getTaskData()).setCreatedBy(new UserImpl(userId));
        return this;
    }
    
    public TaskFluent setCreatedOn(Date createdOn){
        ((TaskDataImpl)task.getTaskData()).setCreatedOn(createdOn);
        return this;
    }
    
    public TaskFluent setDueDate(Date dueDate){
        ((TaskDataImpl)task.getTaskData()).setExpirationTime(dueDate);
        return this;
    }
    
    public TaskFluent addI18NName(String language, String name){
        task.getNames().add(new I18NTextImpl(language, name));
        return this;
    }
    
    public TaskFluent addI18NDescription(String language, String description){
        task.getDescriptions().add(new I18NTextImpl(language, description));
        return this;
    }
    
    public TaskFluent addI18NSubject(String language, String subject){
        task.getSubjects().add(new I18NTextImpl(language, subject));
        return this;
    }

    public TaskFluent setPriority(int priority) {
        task.setPriority(priority);
        return this;
    }

    public TaskFluent addPotentialUser(String userId) {
        if(assignments.getPotentialOwners().isEmpty()){
            List<OrganizationalEntity> potentialOwners = new ArrayList<OrganizationalEntity>();
            assignments.setPotentialOwners(potentialOwners);    
        }
        assignments.getPotentialOwners().add(new UserImpl(userId));
        
        return this;
    }
    
    public TaskFluent addPotentialGroup(String groupId) {
        if(assignments.getPotentialOwners().isEmpty()){
            List<OrganizationalEntity> potentialOwners = new ArrayList<OrganizationalEntity>();
            assignments.setPotentialOwners(potentialOwners);    
        }
        assignments.getPotentialOwners().add(new GroupImpl(groupId));
        
        return this;
    }
    
    public TaskFluent setAdminUser(String userId) {
        if(assignments.getBusinessAdministrators().isEmpty()){
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            assignments.setBusinessAdministrators(businessAdmins);    
        }
        assignments.getBusinessAdministrators().add(new UserImpl(userId));
        
        return this;
    }
    
     public TaskFluent setAdminGroup(String groupId) {
        if(assignments.getBusinessAdministrators().isEmpty()){
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            assignments.setBusinessAdministrators(businessAdmins);    
        }
        assignments.getBusinessAdministrators().add(new GroupImpl(groupId));
        
        return this;
    }
     
    public TaskFluent setFormName(String formName){
        task.setFormName(formName);
        return this;
    }
    
    public TaskFluent setDeploymentID(String deploymentId){
        ((TaskDataImpl)task.getTaskData()).setDeploymentId(deploymentId);
        return this;
    }

}
