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
package org.jbpm.services.task.impl.model.xml;

import static org.jbpm.services.task.impl.model.xml.AbstractJaxbTaskObject.unsupported;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.jbpm.services.task.impl.model.xml.AbstractJaxbTaskObject.GetterUser;
import org.jbpm.services.task.impl.model.xml.adapter.SubTasksStrategyXmlAdapter;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.model.SubTasksStrategy;

/**
 * FYI: information in this class must satisfy one of the following requirements: 
 * 
 * 1. Have both a getter and a setter method
 * 2. Have a getter method and be represented via a field
 * 3. The field must be 
 *
 * If you add a new field to this class, then it *MUST* be a 
 *
 */
@XmlRootElement(name="task-summary")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso(value={Status.class, SubTasksStrategy.class})
@JsonIgnoreProperties({"statusId"})
public class JaxbTaskSummary implements TaskSummary {

    @XmlElement
    @XmlSchemaType(name="long")
    private Long id;
    
    @XmlElement
    @XmlSchemaType(name="string")
    private String name;
    
    @XmlElement
    @XmlSchemaType(name="string")
    private String subject;
    
    @XmlElement
    @XmlSchemaType(name="string")
    private String description;
    
    @XmlElement
    private Status status;
    
    @XmlElement
    @XmlSchemaType(name="int")
    private Integer priority;
    
    @XmlElement
    @XmlSchemaType(name="boolean")
    private Boolean skipable;
    
    @XmlElement(name="actual-owner")
    private String actualOwnerId;
    
    @XmlElement(name="created-by")
    private String createdById;
    
    @XmlElement(name="created-on")
    @XmlSchemaType(name="dateTime")
    private Date createdOn;
    
    @XmlElement(name="activation-time")
    @XmlSchemaType(name="dateTime")
    private Date activationTime;
    
    @XmlElement(name="expiration-time")
    @XmlSchemaType(name="dateTime")
    private Date expirationTime;
    
    @XmlElement(name="process-instance-id")
    @XmlSchemaType(name="long")
    private long processInstanceId;
    
    @XmlElement(name="process-id")
    @XmlSchemaType(name="string")
    private String processId;
    
    @XmlElement(name="process-session-id")
    @XmlSchemaType(name="int")
    private Integer processSessionId;
    
    @XmlElement(name="deployment-id")
    @XmlSchemaType(name="string")
    private String deploymentId;

    @XmlElement(name="quick-task-summary")
    @XmlSchemaType(name="boolean")
    private Boolean quickTaskSummary;
    
    @XmlElement(name="sub-task-strategy")
    @XmlJavaTypeAdapter(value=SubTasksStrategyXmlAdapter.class, type=SubTasksStrategy.class)
    private SubTasksStrategy subTaskStrategy;
    
    @XmlElement(name="parent-id")
    @XmlSchemaType(name="long")
    private Long parentId;
    
    @XmlElement(name="potential-owner")
    private List<String> potentialOwners;

    public JaxbTaskSummary(TaskSummary taskSum) {
        this.id = taskSum.getId();
        this.processInstanceId = taskSum.getProcessInstanceId();
        this.name = taskSum.getName();
        this.subject = taskSum.getSubject();
        this.description = taskSum.getDescription();
        this.status = taskSum.getStatus();
        this.priority = taskSum.getPriority();
        this.skipable = taskSum.isSkipable();
        User actual = taskSum.getActualOwner();
        if( actual != null ) { 
            this.actualOwnerId = actual.getId();
        }
        User created = taskSum.getCreatedBy();
        if( created != null ) { 
           this.createdById = created.getId(); 
        }
        this.createdOn = taskSum.getCreatedOn();
        this.activationTime = taskSum.getActivationTime();
        this.expirationTime = taskSum.getExpirationTime();
        this.processId = taskSum.getProcessId();
        this.processSessionId = taskSum.getProcessSessionId();
        this.deploymentId = taskSum.getDeploymentId();
        this.quickTaskSummary = false;
        this.parentId = taskSum.getParentId();
    }
    
    

    public JaxbTaskSummary() {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        unsupported(Task.class); 
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        unsupported(Task.class); 
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(long processInstanceId) {
        this.processInstanceId = processInstanceId;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Boolean isSkipable() {
        return skipable;
    }

    public void setSkipable(boolean skipable) {
        this.skipable = skipable;
    }

    @JsonIgnore
    public User getActualOwner() {
        if( actualOwnerId == null ) { 
            return null;
        }
        return new GetterUser(actualOwnerId);
    }

    public void setActualOwner(User actualOwner) {
        if( actualOwner != null ) { 
            this.actualOwnerId = actualOwner.getId();
        } else { 
            this.actualOwnerId = null;
        }
        
    }

    public String getActualOwnerId() {
        return actualOwnerId;
    }

    public void setActualOwnerId(String id) {
        this.actualOwnerId = id;
    }

    @JsonIgnore
    public User getCreatedBy() {
        if( createdById == null ) { 
            return null;
        }
        return new GetterUser(this.createdById);
    }

    public void setCreatedBy(User createdBy) {
        if( createdBy != null ) { 
            this.createdById = createdBy.getId();
        } else { 
            this.createdById = null;
        }
    }

    public String getCreatedById() {
        return createdById;
    }

    public void setCreatedById(String id) {
        this.createdById = id;
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

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public Integer getProcessSessionId() {
        return processSessionId;
    }

    public void setProcessSessionId(int processSessionId) {
        this.processSessionId = processSessionId;
    }

    public List<String> getPotentialOwners() {
        return potentialOwners;
    }

    public void setPotentialOwners(List<String> potentialOwners) {
        this.potentialOwners = potentialOwners;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    @Override
    public String getStatusId() {
        return (status != null)?status.name():"";
    }

    @Override
    public Boolean isQuickTaskSummary() {
        return quickTaskSummary;
    }

    @Override
    public Long getParentId() {
        return parentId;
    }

}