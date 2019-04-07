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

package org.jbpm.services.task.impl.model;


import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;

import org.jbpm.services.task.utils.CollectionUtils;
import org.kie.api.task.model.Attachment;
import org.kie.api.task.model.Comment;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.User;
import org.kie.api.task.model.Group;
import org.kie.internal.task.api.model.AccessType;
import org.kie.internal.task.api.model.ContentData;
import org.kie.internal.task.api.model.FaultData;
import org.kie.internal.task.api.model.InternalTaskData;

@Embeddable
public class TaskDataImpl implements InternalTaskData {
    @Enumerated(EnumType.STRING)
    private Status status = Status.Created;         // initial default state

    private Status previousStatus = null;

    @ManyToOne(targetEntity=UserImpl.class)
    private User actualOwner;

    @ManyToOne(targetEntity=UserImpl.class)
    private User createdBy;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date createdOn;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date activationTime;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date expirationTime;

    private boolean skipable;

    private long workItemId = -1;
    
    private long processInstanceId = -1;

    private AccessType documentAccessType;

    private String documentType;

    private long documentContentId = -1;

    private AccessType outputAccessType;

    private String outputType;

    private long outputContentId = -1;

    private String faultName;

    private AccessType faultAccessType;

    private String faultType;

    private long faultContentId = -1;

    private long parentId = -1;
    
    private String processId;
    
    private String deploymentId;
    
    private long processSessionId;

    @OneToMany(cascade = CascadeType.ALL, targetEntity=CommentImpl.class)
    @JoinColumn(name = "TaskData_Comments_Id", nullable = true)
    @OrderBy("id ASC")
    private List<Comment> comments = Collections.emptyList();

    @OneToMany(cascade = CascadeType.ALL, targetEntity=AttachmentImpl.class)
    @JoinColumn(name = "TaskData_Attachments_Id", nullable = true)
    @OrderBy("id ASC")
    private List<Attachment> attachments = Collections.emptyList();
   

    // transient task variables for improved access
    private transient Map<String, Object> taskInputVariables;
    private transient Map<String, Object> taskOutputVariables;
    
    @Override
    public Map<String, Object> getTaskInputVariables() {
        return taskInputVariables;
    }

    @Override
    public void setTaskInputVariables(Map<String, Object> taskInputVariables) {
        this.taskInputVariables = taskInputVariables;
    }

    @Override
    public Map<String, Object> getTaskOutputVariables() {
        return taskOutputVariables;
    }

    @Override
    public void setTaskOutputVariables(Map<String, Object> taskOutputVariables) {
        this.taskOutputVariables = taskOutputVariables;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        if (status != null) {
            out.writeBoolean(true);
            out.writeUTF(status.toString());
        } else {
            out.writeBoolean(false);
        }

        if (previousStatus != null) {
            out.writeBoolean(true);
            out.writeUTF(previousStatus.toString());
        } else {
            out.writeBoolean(false);
        }

        if (actualOwner != null) {
            out.writeBoolean(true);
            actualOwner.writeExternal(out);
        } else {
            out.writeBoolean(false);
        }

        if (createdBy != null) {
            out.writeBoolean(true);
            createdBy.writeExternal(out);
        } else {
            out.writeBoolean(false);
        }

        if (createdOn != null) {
            out.writeBoolean(true);
            out.writeLong(createdOn.getTime());
        } else {
            out.writeBoolean(false);
        }

        if (activationTime != null) {
            out.writeBoolean(true);
            out.writeLong(activationTime.getTime());
        } else {
            out.writeBoolean(false);
        }

        if (expirationTime != null) {
            out.writeBoolean(true);
            out.writeLong(expirationTime.getTime());
        } else {
            out.writeBoolean(false);
        }

        out.writeBoolean(skipable);

        if (workItemId != -1) {
            out.writeBoolean(true);
            out.writeLong(workItemId);
        } else {
            out.writeBoolean(false);
        }

        if (processInstanceId != -1) {
            out.writeBoolean(true);
            out.writeLong(processInstanceId);
        } else {
            out.writeBoolean(false);
        }

        if (documentAccessType != null) {
            out.writeBoolean(true);
            out.writeObject(documentAccessType);
        } else {
            out.writeBoolean(false);
        }

        if (documentType != null) {
            out.writeBoolean(true);
            out.writeUTF(documentType);
        } else {
            out.writeBoolean(false);
        }

        if (documentContentId != -1) {
            out.writeBoolean(true);
            out.writeLong(documentContentId);
        } else {
            out.writeBoolean(false);
        }

        if (outputAccessType != null) {
            out.writeBoolean(true);
            out.writeObject(outputAccessType);
        } else {
            out.writeBoolean(false);
        }

        if (outputType != null) {
            out.writeBoolean(true);
            out.writeUTF(outputType);
        } else {
            out.writeBoolean(false);
        }

        if (outputContentId != -1) {
            out.writeBoolean(true);
            out.writeLong(outputContentId);
        } else {
            out.writeBoolean(false);
        }

        if (faultName != null) {
            out.writeBoolean(true);
            out.writeUTF(faultName);
        } else {
            out.writeBoolean(false);
        }

        if (faultAccessType != null) {
            out.writeBoolean(true);
            out.writeObject(faultAccessType);
        } else {
            out.writeBoolean(false);
        }

        if (faultType != null) {
            out.writeBoolean(true);
            out.writeUTF(faultType);
        } else {
            out.writeBoolean(false);
        }

        if (faultContentId != -1) {
            out.writeBoolean(true);
            out.writeLong(faultContentId);
        } else {
            out.writeBoolean(false);
        }

        if (parentId != -1) {
            out.writeBoolean(true);
            out.writeLong(parentId);
        } else {
            out.writeBoolean(false);
        }
        
        if (processId != null) {
            out.writeBoolean(true);
            out.writeUTF(processId);
        } else {
            out.writeBoolean(false);
        }
        
        if (processSessionId != -1) {
            out.writeBoolean(true);
            out.writeLong(processSessionId);
        } else {
            out.writeBoolean(false);
        }

        CollectionUtils.writeCommentList(comments,
                out);
        CollectionUtils.writeAttachmentList(attachments,
                out);
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        if (in.readBoolean()) {
            status = Status.valueOf(in.readUTF());
        }

        if (in.readBoolean()) {
            previousStatus = Status.valueOf(in.readUTF());
        }

        if (in.readBoolean()) {
            actualOwner = new UserImpl();
            actualOwner.readExternal(in);
        }

        if (in.readBoolean()) {
            createdBy = new UserImpl();
            createdBy.readExternal(in);
        }

        if (in.readBoolean()) {
            createdOn = new Date(in.readLong());
        }

        if (in.readBoolean()) {
            activationTime = new Date(in.readLong());
        }

        if (in.readBoolean()) {
            expirationTime = new Date(in.readLong());
        }

        skipable = in.readBoolean();

        if (in.readBoolean()) {
            workItemId = in.readLong();
        }

        if (in.readBoolean()) {
            processInstanceId = in.readLong();
        }

        if (in.readBoolean()) {
            documentAccessType = (AccessType) in.readObject();
        }

        if (in.readBoolean()) {
            documentType = in.readUTF();
        }

        if (in.readBoolean()) {
            documentContentId = in.readLong();
        }

        if (in.readBoolean()) {
            outputAccessType = (AccessType) in.readObject();
        }

        if (in.readBoolean()) {
            outputType = in.readUTF();
        }

        if (in.readBoolean()) {
            outputContentId = in.readLong();
        }

        if (in.readBoolean()) {
            faultName = in.readUTF();
        }

        if (in.readBoolean()) {
            faultAccessType = (AccessType) in.readObject();
        }

        if (in.readBoolean()) {
            faultType = in.readUTF();
        }

        if (in.readBoolean()) {
            faultContentId = in.readLong();
        }

        if (in.readBoolean()) {
            parentId = in.readLong();
        }
        
        if (in.readBoolean()) {
            processId = in.readUTF();
        }
        
        if (in.readBoolean()) {
            processSessionId = in.readLong();
        }
        
        comments = CollectionUtils.readCommentList(in);
        attachments = CollectionUtils.readAttachmentList(in);

    }

    /**
     * Initializes the state of the TaskData, i.e. sets the <field>createdOn</field>, <field>activationTime</field>
     * and sets the state to <code>Status.Created</code>.
     *
     * @return returns the current state of the TaskData
     */
    public Status initialize() {
        Date createdOn = getCreatedOn();
        // set the CreatedOn date if it's not already set
        if (createdOn == null) {
            createdOn = new Date();
            setCreatedOn(createdOn);
        }

        //@FIXME for now we activate on creation, unless date is supplied
        if (getActivationTime() == null) {
            setActivationTime(createdOn);
        }

        setStatus(Status.Created);

        return Status.Created;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        previousStatus = this.status;
        this.status = status;
    }

    public Status getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(Status previousStatus) {
        this.previousStatus = previousStatus;
    }

    public User getActualOwner() {
        return actualOwner;
    }

    public void setActualOwner(User actualOwner) {
        this.actualOwner = convertToUserImpl(actualOwner);
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = convertToUserImpl(createdBy);
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

    public boolean isSkipable() {
        return skipable;
    }

    public void setSkipable(boolean isSkipable) {
        this.skipable = isSkipable;
    }

    public void setWorkItemId(long workItemId) {
        this.workItemId = workItemId;
    }

    public long getWorkItemId() {
        return workItemId;
    }
    
    public void setProcessInstanceId(long processInstanceId) {
    	this.processInstanceId = processInstanceId;
    }
    
    public long getProcessInstanceId() {
    	return processInstanceId;
    }
    
    public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}
	
	public long getProcessSessionId() {
		return processSessionId;
	}

	public void setProcessSessionId(long processSessionId) {
		this.processSessionId = processSessionId;
	}

	/**
     * Sets the document content data for this task data. It will set the <field>documentContentId</field> from the specified
     * documentID, <field>documentAccessType</field>, <field>documentType</field> from the specified
     * documentConentData.
     * @param documentID id of document content
     * @param documentConentData ContentData
     */
    public void setDocument(long documentID, ContentData documentConentData) {
        setDocumentContentId(documentID);
        setDocumentAccessType(documentConentData.getAccessType());
        setDocumentType(documentConentData.getType());
    }

    public AccessType getDocumentAccessType() {
        return documentAccessType;
    }

    public void setDocumentAccessType(AccessType accessType) {
        this.documentAccessType = accessType;
    }

    public String getDocumentType() {
        return documentType;
    }

    public long getDocumentContentId() {
        return documentContentId;
    }

    public void setDocumentContentId(long documentContentId) {
        this.documentContentId = documentContentId;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    /**
     * Sets the content data for this task data. It will set the <field>outputContentId</field> from the specified
     * outputContentId, <field>outputAccessType</field>, <field>outputType</field> from the specified
     * outputContentData.
     * @param outputContentId id of output content
     * @param outputContentData contentData
     */
    public void setOutput(long outputContentId, ContentData outputContentData) {
        setOutputContentId(outputContentId);
        setOutputAccessType(outputContentData.getAccessType());
        setOutputType(outputContentData.getType());
    }

    public AccessType getOutputAccessType() {
        return outputAccessType;
    }

    public void setOutputAccessType(AccessType outputAccessType) {
        this.outputAccessType = outputAccessType;
    }

    public String getOutputType() {
        return outputType;
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    public Long getOutputContentId() {
        return outputContentId;
    }

    public void setOutputContentId(long outputContentId) {
        this.outputContentId = outputContentId;
    }

    /**
     * Sets the fault data for this task data. It will set the <field>faultContentId</field> from the specified
     * faultContentId, <field>faultAccessType</field>, <field>faultType</field>, <field>faultName</field> from the
     * specified faultData.
     * @param faultContentId id of fault content
     * @param faultData FaultData
     */
    public void setFault(long faultContentId, FaultData faultData) {
        setFaultContentId(faultContentId);
        setFaultAccessType(faultData.getAccessType());
        setFaultType(faultData.getType());
        setFaultName(faultData.getFaultName());
    }

    public String getFaultName() {
        return faultName;
    }

    public void setFaultName(String faultName) {
        this.faultName = faultName;
    }

    public AccessType getFaultAccessType() {
        return faultAccessType;
    }

    public void setFaultAccessType(AccessType faultAccessType) {
        this.faultAccessType = faultAccessType;
    }

    public String getFaultType() {
        return faultType;
    }

    public void setFaultType(String faultType) {
        this.faultType = faultType;
    }

    public long getFaultContentId() {
        return faultContentId;
    }

    public void setFaultContentId(long faultContentId) {
        this.faultContentId = faultContentId;
    }

    public List<Comment> getComments() {
        return comments;
    }

    /**
     * Adds the specified comment to our list of comments.
     *
     * @param comment comment to add
     */
    public void addComment(Comment comment) {
        if (comments == null || comments.size() == 0) {
            comments = new ArrayList<Comment>();
        }

        comments.add((CommentImpl) comment);
    }

    /**
     * Removes the Comment specified by the commentId.
     *
     * @param commentId id of Comment to remove
     * @return removed Comment or null if one was not found with the id
     */
    public Comment removeComment(final long commentId) {
        Comment removedComment = null;

        if (comments != null) {
            for (int index = comments.size() - 1; index >= 0; --index) {
                Comment currentComment = comments.get(index);

                if (currentComment.getId() == commentId) {
                    removedComment = comments.remove(index);
                    break;
                }
            }
        }

        return removedComment;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    /**
     * Adds the specified attachment to our list of Attachments.
     *
     * @param attachment attachment to add
     */
    public void addAttachment(Attachment attachment) {
        if (attachments == null || attachments == Collections.<Attachment>emptyList()) {
            attachments = new ArrayList<Attachment>();
        }

        attachments.add((AttachmentImpl) attachment);
    }

    /**
     * Removes the Attachment specified by the attachmentId.
     *
     * @param attachmentId id of attachment to remove
     * @return removed Attachment or null if one was not found with the id
     */
    public Attachment removeAttachment(final long attachmentId) {
        Attachment removedAttachment = null;

        if (attachments != null) {
            for (int index = attachments.size() - 1; index >= 0; --index) {
                Attachment currentAttachment = attachments.get(index);

                if (currentAttachment.getId() == attachmentId) {
                    removedAttachment = attachments.remove(index);
                    break;
                }
            }
        }

        return removedAttachment;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments =  attachments;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((activationTime == null) ? 0 : activationTime.hashCode());
        result = prime * result + CollectionUtils.hashCode(attachments);
        result = prime * result + CollectionUtils.hashCode(comments);
        result = prime * result + ((createdOn == null) ? 0 : createdOn.hashCode());
        result = prime * result + ((expirationTime == null) ? 0 : expirationTime.hashCode());
        result = prime * result + (skipable ? 1231 : 1237);
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((previousStatus == null) ? 0 : previousStatus.hashCode());
        result = prime * result + ((workItemId == -1) ? 0 : (int) workItemId);
        //Should I add parentId to this hashCode?
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof TaskDataImpl)) return false;
        TaskDataImpl other = (TaskDataImpl) obj;

        if (actualOwner == null) {
            if (other.actualOwner != null) return false;
        } else if (!actualOwner.equals(other.actualOwner)) {
            return false;
        }

        if (createdBy == null) {
            if (other.createdBy != null) return false;
        } else if (!createdBy.equals(other.createdBy)) {
            return false;
        }

        if (createdOn == null) {
            if (other.createdOn != null) return false;
        } else if (createdOn.getTime() != other.createdOn.getTime()) return false;
        if (expirationTime == null) {
            if (other.expirationTime != null) return false;
        } else if (expirationTime.getTime() != other.expirationTime.getTime()) return false;
        if (skipable != other.skipable) return false;
        if (workItemId != other.workItemId) return false;
        if (status == null) {
            if (other.status != null) return false;
        } else if (!status.equals(other.status)) return false;
        if (previousStatus == null) {
            if (other.previousStatus != null) return false;
        } else if (!previousStatus.equals(other.previousStatus)) return false;
        if (activationTime == null) {
            if (other.activationTime != null) return false;
        } else if (activationTime.getTime() != other.activationTime.getTime()) return false;

        if (workItemId != other.workItemId) return false;

        if (documentAccessType == null) {
            if (other.documentAccessType != null) return false;
        } else if (!documentAccessType.equals(other.documentAccessType)) return false;

        if (documentContentId != other.documentContentId) return false;
        if (documentType == null) {
            if (other.documentType != null) return false;
        } else if (!documentType.equals(other.documentType)) return false;
        // I think this is OK!
        if (parentId != other.parentId) return false;
        if (processId == null) {
            if (other.processId != null) return false;
        } else if (!processId.equals(other.processId)) return false;
        if (processSessionId != other.processSessionId) return false;        
        if (deploymentId == null) {
            if (other.deploymentId != null) return false;
        } else if (!deploymentId.equals(other.deploymentId)) return false;        
        return CollectionUtils.equals(attachments,
                other.attachments) && CollectionUtils.equals(comments,
                other.comments);
    }

    @Override
    public String getDeploymentId() {
        return deploymentId;
    }

    @Override
    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }
    
    static UserImpl convertToUserImpl(User user) { 
        if( user == null ) { 
            return null;
        }
        if( user instanceof UserImpl ) { 
            return (UserImpl) user;
        } else { 
            return new UserImpl(user.getId());
        }
    }

    static GroupImpl convertToGroupImpl(Group group) { 
        if( group == null ) { 
            return null;
        }
        if( group instanceof GroupImpl ) { 
            return (GroupImpl) group;
        } else { 
            return new GroupImpl(group.getId());
        }
    }

}
