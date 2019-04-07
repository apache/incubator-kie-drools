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

package org.jbpm.persistence.api.integration.model;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jbpm.persistence.api.integration.InstanceView;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.model.InternalPeopleAssignments;

/**
 * InstanceView dedicated for <code>org.kie.api.task.model.Task</code>
 * copeFromSource is performed directly when TaskInstanceView is initialized with a source
 */
public class TaskInstanceView implements InstanceView<Task> {
    
    private static final long serialVersionUID = 8214656362310328071L;
    
    private String compositeId;
    private Long id;    
    private Integer priority;
    private String name;
    private String subject;
    private String description;
    private String taskType;
    private String formName;
    private String status;
    private String actualOwner;
    private String createdBy;
    private Date createdOn;
    private Date activationTime;
    private Date expirationDate;
    private Boolean skipable;
    private Long workItemId;
    private Long processInstanceId;
    private Long parentId;
    private String processId;
    private String containerId;

    private List<String> potentialOwners;

    private List<String> excludedOwners;

    private List<String> businessAdmins;

    private Map<String, Object> inputData;

    private Map<String, Object> outputData;

    private transient Task source;
    
    public TaskInstanceView() {        
    }
    
    public TaskInstanceView(Task source) {
        this.source = source;
        copyFromSource();
    }
    
    public String getCompositeId() {
        return compositeId;
    }
    
    public void setCompositeId(String compositeId) {
        this.compositeId = compositeId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getActualOwner() {
        return actualOwner;
    }

    public void setActualOwner(String actualOwner) {
        this.actualOwner = actualOwner;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getActivationTime() {
        return activationTime;
    }

    public void setActivationTime(Date activationTime) {
        this.activationTime = activationTime;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Boolean getSkipable() {
        return skipable;
    }

    public void setSkipable(Boolean skipable) {
        this.skipable = skipable;
    }

    public Long getWorkItemId() {
        return workItemId;
    }

    public void setWorkItemId(Long workItemId) {
        this.workItemId = workItemId;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public List<String> getPotentialOwners() {
        return potentialOwners;
    }

    public void setPotentialOwners(List<String> potentialOwners) {
        this.potentialOwners = potentialOwners;
    }

    public List<String> getExcludedOwners() {
        return excludedOwners;
    }

    public void setExcludedOwners(List<String> excludedOwners) {
        this.excludedOwners = excludedOwners;
    }

    public List<String> getBusinessAdmins() {
        return businessAdmins;
    }

    public void setBusinessAdmins(List<String> businessAdmins) {
        this.businessAdmins = businessAdmins;
    }

    public Map<String, Object> getInputData() {
        return inputData;
    }

    public void setInputData(Map<String, Object> inputData) {
        this.inputData = inputData;
    }

    public Map<String, Object> getOutputData() {
        return outputData;
    }

    public void setOutputData(Map<String, Object> outputData) {
        this.outputData = outputData;
    }

    @Override
    public String toString() {
        return "TaskInstance{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", actualOwner='" + actualOwner + '\'' +
                ", processInstanceId=" + processInstanceId +
                ", processId='" + processId + '\'' +
                ", containerId='" + containerId + '\'' +
                '}';
    }

    @Override
    public Task getSource() {
        return source;
    }
    
    @Override
    public void copyFromSource() {
        if (this.id != null) {
            return;
        }
        this.compositeId = System.getProperty("org.kie.server.id", "") + "_" + source.getId();
        this.activationTime = source.getTaskData().getActivationTime();
        this.actualOwner = safeOrgEntity(source.getTaskData().getActualOwner());
        this.businessAdmins = source.getPeopleAssignments().getBusinessAdministrators()
                .stream()
                .map(entity -> safeOrgEntity(entity))
                .collect(Collectors.toList());
        this.containerId = source.getTaskData().getDeploymentId();
        this.createdBy = safeOrgEntity(source.getTaskData().getCreatedBy());
        this.createdOn = source.getTaskData().getCreatedOn();
        this.description = source.getDescription();
        this.excludedOwners = ((InternalPeopleAssignments)source.getPeopleAssignments()).getExcludedOwners()
                .stream()
                .map(entity -> safeOrgEntity(entity))
                .collect(Collectors.toList());
        this.expirationDate = source.getTaskData().getExpirationTime();
        this.formName = source.getFormName();
        this.id = source.getId();
        this.inputData = source.getTaskData().getTaskInputVariables();
        this.name = source.getName();
        this.outputData = source.getTaskData().getTaskOutputVariables();
        this.parentId = source.getTaskData().getParentId();
        this.potentialOwners = source.getPeopleAssignments().getPotentialOwners()
                .stream()
                .map(entity -> safeOrgEntity(entity))
                .collect(Collectors.toList());
        this.priority = source.getPriority();
        this.processId = source.getTaskData().getProcessId();
        this.processInstanceId = source.getTaskData().getProcessInstanceId();
        this.skipable = source.getTaskData().isSkipable();
        this.status = source.getTaskData().getStatus().name();
        this.subject = source.getSubject();
        this.taskType = source.getTaskType();
        this.workItemId = source.getTaskData().getWorkItemId();
    }
    
    protected String safeOrgEntity(OrganizationalEntity entity) {
        if (entity != null) {
            return entity.getId();
        }
        
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TaskInstanceView other = (TaskInstanceView) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
