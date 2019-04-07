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

package org.jbpm.services.task.impl.model.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.jbpm.services.task.impl.model.xml.InternalJaxbWrapper.GetterUser;
import org.kie.api.task.model.Attachment;
import org.kie.api.task.model.Comment;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskData;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.model.AccessType;
import org.kie.internal.task.api.model.InternalTaskData;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@XmlType(name = "task-data")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ JaxbComment.class, JaxbAttachment.class })
@JsonAutoDetect(getterVisibility=JsonAutoDetect.Visibility.NONE, setterVisibility=JsonAutoDetect.Visibility.NONE, fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class JaxbTaskData extends AbstractJaxbTaskObject<TaskData> implements TaskData {

    @XmlElement
    private Status status;

    @XmlElement
    private Status previousStatus;

    @XmlElement(name = "actual-owner")
    private String actualOwner;

    @XmlElement(name = "created-by")
    private String createdBy;

    @XmlElement(name = "created-on")
    @XmlSchemaType(name = "dateTime")
    private Date createdOn;

    @XmlElement(name = "activation-time")
    @XmlSchemaType(name = "dateTime")
    private Date activationTime;

    @XmlElement(name = "expiration-time")
    @XmlSchemaType(name = "dateTime")
    private Date expirationTime;

    @XmlElement
    @XmlSchemaType(name = "boolean")
    private Boolean skipable;

    @XmlElement(name = "work-item-id")
    @XmlSchemaType(name = "long")
    private Long workItemId;

    @XmlElement(name = "process-instance-id")
    @XmlSchemaType(name = "long")
    private Long processInstanceId;

    @XmlElement(name = "document-type")
    @XmlSchemaType(name = "string")
    private String documentType;

    @XmlElement(name = "document-access-type")
    private AccessType documentAccessType;

    @XmlElement(name = "document-content-id")
    @XmlSchemaType(name = "long")
    private Long documentContentId;

    @XmlElement(name = "output-type")
    @XmlSchemaType(name = "string")
    private String outputType;

    @XmlElement(name = "output-access-type")
    private AccessType outputAccessType;

    @XmlElement(name = "output-content-id")
    @XmlSchemaType(name = "long")
    private Long outputContentId;

    @XmlElement(name = "fault-name")
    @XmlSchemaType(name = "string")
    private String faultName;

    @XmlElement(name = "fault-access-type")
    private AccessType faultAccessType;

    @XmlElement(name = "fault-type")
    @XmlSchemaType(name = "string")
    private String faultType;

    @XmlElement(name = "fault-content-id")
    @XmlSchemaType(name = "long")
    private Long faultContentId;

    @XmlElement(name = "parent-id")
    @XmlSchemaType(name = "long")
    private Long parentId;

    @XmlElement(name = "process-id")
    @XmlSchemaType(name = "string")
    private String processId;

    @XmlElement(name = "process-session-id")
    @XmlSchemaType(name = "long")
    private Long processSessionId;

    @XmlElement
    private List<JaxbComment> comments;

    @XmlElement
    private List<JaxbAttachment> attachments;
    
    @XmlElement(name = "deployment-id")
    @XmlSchemaType(name = "string")
    private String deploymentId;

    public JaxbTaskData() {
        super(TaskData.class);
    }

    public JaxbTaskData(TaskData taskData) {
        super(TaskData.class);
       
        this.status = taskData.getStatus();
        this.previousStatus = taskData.getPreviousStatus();
        User actualOwnerUser = taskData.getActualOwner();
        if( actualOwnerUser != null ) { 
            this.actualOwner = actualOwnerUser.getId();
        }
        User createdByUser = taskData.getCreatedBy();
        if( createdByUser != null ) { 
            this.createdBy = createdByUser.getId();
        }
        this.createdOn = taskData.getCreatedOn();
        this.activationTime = taskData.getActivationTime();
        this.expirationTime = taskData.getExpirationTime();
        this.skipable = taskData.isSkipable();
        this.workItemId = taskData.getWorkItemId();
        this.processInstanceId = taskData.getProcessInstanceId();
        this.documentType = taskData.getDocumentType();
        if( taskData instanceof JaxbTaskData ) { 
            JaxbTaskData jaxbTaskData = (JaxbTaskData) taskData;
            this.documentAccessType = jaxbTaskData.getDocumentAccessType(); 
            this.outputAccessType = jaxbTaskData.getOutputAccessType();
            this.faultAccessType = jaxbTaskData.getFaultAccessType();
        } else if( taskData instanceof InternalTaskData ) { 
            InternalTaskData internalTaskData = (InternalTaskData) taskData;
            this.documentAccessType = internalTaskData.getDocumentAccessType(); 
            this.outputAccessType = internalTaskData.getOutputAccessType();
            this.faultAccessType = internalTaskData.getFaultAccessType();
        }
        this.documentContentId = taskData.getDocumentContentId();
        this.outputType = taskData.getOutputType();
        this.outputContentId = taskData.getOutputContentId();
        this.faultName = taskData.getFaultName();
        this.faultType = taskData.getFaultType();
        this.faultContentId = taskData.getFaultContentId();
        this.parentId = taskData.getParentId();
        this.processId = taskData.getProcessId();
        this.processSessionId = taskData.getProcessSessionId();
        if( taskData.getComments() != null ) { 
            List<JaxbComment> commentList = new ArrayList<JaxbComment>();
            for (Object comment : taskData.getComments() ) {
                commentList.add(new JaxbComment((Comment) comment));
            }
            this.comments = commentList;
        }
        List<JaxbAttachment> attachList = new ArrayList<JaxbAttachment>();
        for (Object attach : taskData.getAttachments() ) { 
            attachList.add(new JaxbAttachment((Attachment) attach));
        }
        this.attachments = attachList;
        this.deploymentId = taskData.getDeploymentId();
    }

    @Override
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public Status getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(Status previousStatus) {
        this.previousStatus = previousStatus;
    }

    @Override
    public User getActualOwner() {
        return new GetterUser(actualOwner);
    }

    public String getActualOwnerId() {
        return actualOwner;
    }

    public void setActualOwnerId(String actualOwnerId) {
        this.actualOwner = actualOwnerId;
    }

    @Override
    public User getCreatedBy() {
        return new GetterUser(createdBy);
    }

    public String getCreatedById() {
        return createdBy;
    }

    public void setCreatedById(String createdById) {
        this.createdBy = createdById;
    }

    @Override
    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public Date getActivationTime() {
        return activationTime;
    }

    public void setActivationTime(Date activationTime) {
        this.activationTime = activationTime;
    }

    @Override
    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    @Override
    public boolean isSkipable() {
        return skipable;
    }

    public void setSkipable(Boolean skipable) {
        this.skipable = skipable;
    }

    @Override
    public long getWorkItemId() {
        return whenNull(workItemId, -1l);
    }

    public void setWorkItemId(Long workItemId) {
        this.workItemId = workItemId;
    }

    @Override
    public long getProcessInstanceId() {
        return whenNull(processInstanceId, -1l);
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    @Override
    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public AccessType getDocumentAccessType() {
        return documentAccessType;
    }

    public void setDocumentAccessType( AccessType documentAccessType ) {
        this.documentAccessType = documentAccessType;
    }

    @Override
    public long getDocumentContentId() {
        return whenNull(documentContentId, -1l);
    }

    public void setDocumentContentId(Long documentContentId) {
        this.documentContentId = documentContentId;
    }

    @Override
    public String getOutputType() {
        return outputType;
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    public AccessType getOutputAccessType() {
        return outputAccessType;
    }

    public void setOutputAccessType( AccessType outputAccessType ) {
        this.outputAccessType = outputAccessType;
    }

    @Override
    public Long getOutputContentId() {
        return whenNull(outputContentId, -1l);
    }

    public void setOutputContentId(Long outputContentId) {
        this.outputContentId = outputContentId;
    }

    @Override
    public String getFaultName() {
        return faultName;
    }

    public void setFaultName(String faultName) {
        this.faultName = faultName;
    }

    public AccessType getFaultAccessType() {
        return faultAccessType;
    }

    public void setFaultAccessType( AccessType faultAccessType ) {
        this.faultAccessType = faultAccessType;
    }

    @Override
    public String getFaultType() {
        return faultType;
    }

    public void setFaultType(String faultType) {
        this.faultType = faultType;
    }

    @Override
    public long getFaultContentId() {
        return whenNull(faultContentId, -1l);
    }

    public void setFaultContentId(Long faultContentId) {
        this.faultContentId = faultContentId;
    }

    @Override
    public long getParentId() {
        return whenNull(parentId, -1l);
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @Override
    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    @Override
    public long getProcessSessionId() {
        return whenNull(processSessionId, -1l);
    }

    public void setProcessSessionId(Long processSessionId) {
        this.processSessionId = processSessionId;
    }

    @Override
    public List<Comment> getComments() {
        List<Comment> commentList = new ArrayList<Comment>();
        if (comments != null) {
            for (JaxbComment jaxbComment : comments) {
                commentList.add(jaxbComment);
            }
        }
        return Collections.unmodifiableList(commentList);
    }

    public void setJaxbComments(List<JaxbComment> comments) {
        this.comments = comments;
    }

    @Override
    public List<Attachment> getAttachments() {
        List<Attachment> attachmentList = new ArrayList<Attachment>();
        if (attachments != null) {
            for (JaxbAttachment jaxbAttachment : attachments) {
                attachmentList.add(jaxbAttachment);
            }
        }
        return Collections.unmodifiableList(attachmentList);
    }

    @Override
    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    @Override
    public Map<String, Object> getTaskInputVariables() {
        return new HashMap<String, Object>();
    }

    @Override
    public Map<String, Object> getTaskOutputVariables() {
        return new HashMap<String, Object>();
    }

}
