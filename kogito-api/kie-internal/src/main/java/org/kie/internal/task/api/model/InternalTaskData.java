/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.kie.api.task.model.Attachment;
import org.kie.api.task.model.Comment;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskData;
import org.kie.api.task.model.User;

public interface InternalTaskData extends TaskData {

    /**
     * Initializes the state of the TaskData, i.e. sets the createdOn, activationTime
     * and sets the state to <code>Status.Created</code>.
     *
     * @return returns the current state of the TaskData
     */
    Status initialize();

    void setStatus(Status status);

    void setPreviousStatus(Status previousStatus);

    void setActualOwner(User actualOwner);

    void setCreatedBy(User createdBy);

    void setCreatedOn(Date createdOn);

    void setActivationTime(Date activationTime);

    void setExpirationTime(Date expirationTime);

    void setSkipable(boolean isSkipable);

    void setWorkItemId(long workItemId);

    void setProcessInstanceId(long processInstanceId);

    void setProcessId(String processId);

    void setDeploymentId(String deploymentId);

    void setProcessSessionId(long processSessionId);

    /**
     * Sets the document content data for this task data. It will set the documentContentId from the specified
     * documentID, documentAccessType, documentType from the specified
     * documentConentData.
     * @param documentID id of document content
     * @param documentConentData ContentData
     */
    void setDocument(long documentID, ContentData documentConentData);

    AccessType getDocumentAccessType();

    void setDocumentAccessType(AccessType accessType);

    void setDocumentContentId(long documentContentId);

    void setDocumentType(String documentType);

    /**
     * Sets the content data for this task data. It will set the outputContentId from the specified
     * outputContentId, outputAccessType, outputType from the specified
     * outputContentData.
     * @param outputContentId id of output content
     * @param outputContentData contentData
     */
    void setOutput(long outputContentId, ContentData outputContentData);

    AccessType getOutputAccessType();

    void setOutputAccessType(AccessType outputAccessType);

    void setOutputType(String outputType);

    void setOutputContentId(long outputContentId);

    /**
     * Sets the fault data for this task data. It will set the faultContentId from the specified
     * faultContentId, faultAccessType, faultType, faultName from the
     * specified faultData.
     * @param faultContentId id of fault content
     * @param faultData FaultData
     */
    void setFault(long faultContentId, FaultData faultData);

    void setFaultName(String faultName);

    AccessType getFaultAccessType();

    void setFaultAccessType(AccessType faultAccessType);

    void setFaultType(String faultType);

    void setFaultContentId(long faultContentId);

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

    void setTaskInputVariables(Map<String, Object> taskInputVariables);

    void setTaskOutputVariables(Map<String, Object> taskOutputVariables);

}
