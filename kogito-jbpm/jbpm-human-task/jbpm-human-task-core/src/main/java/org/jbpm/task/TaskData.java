/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.task;

import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.FaultData;
import org.jbpm.task.service.IllegalTaskStateException;
import org.jbpm.task.utils.CollectionUtils;

import javax.persistence.*;

import java.io.EOFException;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Embeddable
public class TaskData
        implements
        Externalizable {
    @Enumerated(EnumType.STRING)
    private Status status = Status.Created;         // initial default state

    private Status previousStatus = null;

    @ManyToOne()
    private User actualOwner;

    @ManyToOne()
    private User createdBy;

    private Date createdOn;

    private Date completedOn;
    
    private Date activationTime;

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
    
    private int processSessionId;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "TaskData_Comments_Id", nullable = true)
    private List<Comment> comments = Collections.<Comment>emptyList();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "TaskData_Attachments_Id", nullable = true)
    private List<Attachment> attachments = Collections.<Attachment>emptyList();

    public void writeExternal(ObjectOutput out) throws IOException {
        short currentFormatVersion = 1;
        
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
            out.writeInt(processSessionId);
        } else {
            out.writeBoolean(false);
        }

        CollectionUtils.writeCommentList(comments,
                out);
        CollectionUtils.writeAttachmentList(attachments,
                out);
        
        // Forwards compatibility
        out.writeShort(currentFormatVersion);
        if( completedOn != null ) { 
            out.writeBoolean(true);
            out.writeLong(completedOn.getTime());
        } 
        else { 
            out.writeBoolean(false);
        }
        
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        short currentFormatVersion = 1;
        
        if (in.readBoolean()) {
            status = Status.valueOf(in.readUTF());
        }

        if (in.readBoolean()) {
            previousStatus = Status.valueOf(in.readUTF());
        }

        if (in.readBoolean()) {
            actualOwner = new User();
            actualOwner.readExternal(in);
        }

        if (in.readBoolean()) {
            createdBy = new User();
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
            processSessionId = in.readInt();
        }
        
        comments = CollectionUtils.readCommentList(in);
        attachments = CollectionUtils.readAttachmentList(in);
        
        // Forwards compatibility
        short version = in.readShort();
        if( version == currentFormatVersion ) { 
            if( in.readBoolean() ) { 
                completedOn = new Date(in.readLong());
            }
        }

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

    /**
     * This method will potentially assign the actual owner of this TaskData and set the status
     * of the data.
     * <li>If there is only 1 potential owner, and it is a <code>User</code>, that will become the actual
     * owner of the TaskData and the status will be set to <code>Status.Reserved</code>.</li>
     * <li>f there is only 1 potential owner, and it is a <code>Group</code>,  no owner will be assigned
     * and the status will be set to <code>Status.Ready</code>.</li>
     * <li>If there are more than 1 potential owners, the status will be set to <code>Status.Ready</code>.</li>
     * <li>otherwise, the task data will be unchanged</li>
     *
     * @param potentialOwners - list of potential owners
     * @return current status of task data
     */
    public Status assignOwnerAndStatus(List<OrganizationalEntity> potentialOwners) {
        if (getStatus() != Status.Created) {
            throw new IllegalTaskStateException("Can only assign task owner if status is Created!");
        }

        Status assignedStatus = null;

        if (potentialOwners.size() == 1) {
            // if there is a single potential owner, assign and set status to Reserved
            OrganizationalEntity potentialOwner = potentialOwners.get(0);
            // if there is a single potential user owner, assign and set status to Reserved
            if (potentialOwner instanceof User) {
                setActualOwner((User) potentialOwner);

                assignedStatus = Status.Reserved;
            }
            //If there is a group set as potentialOwners, set the status to Ready ??
            if (potentialOwner instanceof Group) {

                assignedStatus = Status.Ready;
            }
        } else if (potentialOwners.size() > 1) {
            // multiple potential owners, so set to Ready so one can claim.
            assignedStatus = Status.Ready;
        } else {
            //@TODO we have no potential owners
        }

        if (assignedStatus != null) {
            setStatus(assignedStatus);
        } else {
            // status wasn't assigned, so just return the currrent status
            assignedStatus = getStatus();
        }

        return assignedStatus;
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
        this.actualOwner = actualOwner;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }
    
    public Date getCompletedOn() {
        return completedOn;
    }

    public void setCompletedOn(Date completedOn) {
        this.completedOn = completedOn;
    }
    
    Date getActivationTime() {
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
	
	public int getProcessSessionId() {
		return processSessionId;
	}

	public void setProcessSessionId(int processSessionId) {
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

    void setOutputAccessType(AccessType outputAccessType) {
        this.outputAccessType = outputAccessType;
    }

    public String getOutputType() {
        return outputType;
    }

    void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    public long getOutputContentId() {
        return outputContentId;
    }

    void setOutputContentId(long outputContentId) {
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

    void setFaultName(String faultName) {
        this.faultName = faultName;
    }

    public AccessType getFaultAccessType() {
        return faultAccessType;
    }

    void setFaultAccessType(AccessType faultAccessType) {
        this.faultAccessType = faultAccessType;
    }

    public String getFaultType() {
        return faultType;
    }

    void setFaultType(String faultType) {
        this.faultType = faultType;
    }

    public long getFaultContentId() {
        return faultContentId;
    }

    void setFaultContentId(long faultContentId) {
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

        comments.add(comment);
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

        attachments.add(attachment);
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
        this.attachments = attachments;
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
        result = prime * result + ((completedOn == null) ? 0 : completedOn.hashCode());
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
        if (!(obj instanceof TaskData)) return false;
        TaskData other = (TaskData) obj;

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
        if (completedOn == null) {
            if (other.completedOn != null) return false;
        } else if (completedOn.getTime() != other.completedOn.getTime()) return false;
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
        return CollectionUtils.equals(attachments,
                other.attachments) && CollectionUtils.equals(comments,
                other.comments);
    }

}
