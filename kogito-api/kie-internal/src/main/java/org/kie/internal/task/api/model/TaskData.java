/*
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

package org.kie.internal.task.api.model;


import java.io.Externalizable;
import java.util.Date;
import java.util.List;

public interface TaskData extends Externalizable {

    /**
     * Initializes the state of the TaskData, i.e. sets the <field>createdOn</field>, <field>activationTime</field>
     * and sets the state to <code>Status.Created</code>.
     *
     * @return returns the current state of the TaskData
     */
    Status initialize();

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
    Status assignOwnerAndStatus(List<OrganizationalEntity> potentialOwners);

    Status getStatus();

    void setStatus(Status status);

    Status getPreviousStatus();

    void setPreviousStatus(Status previousStatus);

    User getActualOwner();

    void setActualOwner(User actualOwner);

    User getCreatedBy();

    void setCreatedBy(User createdBy);

    Date getCreatedOn();

    void setCreatedOn(Date createdOn);

    Date getActivationTime();

    void setActivationTime(Date activationTime);

    Date getExpirationTime();

    void setExpirationTime(Date expirationTime);

    boolean isSkipable();

    void setSkipable(boolean isSkipable);

    void setWorkItemId(long workItemId);

    long getWorkItemId();
    
    void setProcessInstanceId(long processInstanceId);
    
    long getProcessInstanceId();
    
    String getProcessId();

	void setProcessId(String processId);
	
	int getProcessSessionId();

	void setProcessSessionId(int processSessionId);

	/**
     * Sets the document content data for this task data. It will set the <field>documentContentId</field> from the specified
     * documentID, <field>documentAccessType</field>, <field>documentType</field> from the specified
     * documentConentData.
     * @param documentID id of document content
     * @param documentConentData ContentData
     */
    void setDocument(long documentID, ContentData documentConentData);

    AccessType getDocumentAccessType();

    void setDocumentAccessType(AccessType accessType);

    String getDocumentType();

    long getDocumentContentId();

    void setDocumentContentId(long documentContentId);

    void setDocumentType(String documentType);

    /**
     * Sets the content data for this task data. It will set the <field>outputContentId</field> from the specified
     * outputContentId, <field>outputAccessType</field>, <field>outputType</field> from the specified
     * outputContentData.
     * @param outputContentId id of output content
     * @param outputContentData contentData
     */
    void setOutput(long outputContentId, ContentData outputContentData);

    AccessType getOutputAccessType();

    void setOutputAccessType(AccessType outputAccessType);

    String getOutputType();

    void setOutputType(String outputType);

    long getOutputContentId();

    void setOutputContentId(long outputContentId);

    /**
     * Sets the fault data for this task data. It will set the <field>faultContentId</field> from the specified
     * faultContentId, <field>faultAccessType</field>, <field>faultType</field>, <field>faultName</field> from the
     * specified faultData.
     * @param faultContentId id of fault content
     * @param faultData FaultData
     */
    void setFault(long faultContentId, FaultData faultData);

    String getFaultName();

    void setFaultName(String faultName);

    AccessType getFaultAccessType();

    void setFaultAccessType(AccessType faultAccessType);

    String getFaultType();

    void setFaultType(String faultType);

    long getFaultContentId();

    void setFaultContentId(long faultContentId);

    List<Comment> getComments();

    /**
     * Adds the specified comment to our list of comments.
     *
     * @param comment comment to add
     */
    void addComment(Comment comment);

    /**
     * Removes the Comment specified by the commentId.
     *
     * @param commentId id of Comment to remove
     * @return removed Comment or null if one was not found with the id
     */
    Comment removeComment(final long commentId);

    void setComments(List<Comment> comments);

    List<Attachment> getAttachments();

    /**
     * Adds the specified attachment to our list of Attachments.
     *
     * @param attachment attachment to add
     */
    void addAttachment(Attachment attachment);

    /**
     * Removes the Attachment specified by the attachmentId.
     *
     * @param attachmentId id of attachment to remove
     * @return removed Attachment or null if one was not found with the id
     */
    Attachment removeAttachment(final long attachmentId);

    void setAttachments(List<Attachment> attachments);

    long getParentId();

    void setParentId(long parentId);

}
