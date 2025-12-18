/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.usertask;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.jobs.descriptors.UserTaskInstanceJobDescription;
import org.kie.kogito.usertask.lifecycle.UserTaskLifeCycle;
import org.kie.kogito.usertask.lifecycle.UserTaskState;
import org.kie.kogito.usertask.model.*;

public interface UserTaskInstance {

    String getId();

    UserTask getUserTask();

    UserTaskState getStatus();

    ProcessInfo getProcessInfo();

    String getUserTaskId();

    boolean hasActualOwner();

    void setActualOwner(String actualOwner);

    String getActualOwner();

    void initialize(Map<String, Object> data, IdentityProvider identity);

    void transition(String transitionId, Map<String, Object> data, IdentityProvider identityProvider);

    String getExternalReferenceId();

    String getTaskName();

    String getTaskDescription();

    String getTaskPriority();

    Map<String, Object> getMetadata();

    Map<String, Object> getOutputs();

    Map<String, Object> getInputs();

    Date getSlaDueDate();

    void setInput(String key, Object value);

    void setOutput(String key, Object value);

    /**
     * Returns potential users that can work on this task
     * 
     * @return potential users
     */
    Set<String> getPotentialUsers();

    /**
     * Returns potential groups that can work on this task
     * 
     * @return potential groups
     */
    Set<String> getPotentialGroups();

    /**
     * Returns admin users that can administer this task
     * 
     * @return admin users
     */
    Set<String> getAdminUsers();

    /**
     * Returns admin groups that can administer this task
     * 
     * @return admin groups
     */
    Set<String> getAdminGroups();

    /**
     * Returns excluded users that cannot work on this task
     * 
     * @return excluded users
     */
    Set<String> getExcludedUsers();

    Attachment findAttachmentById(String attachmentId);

    Attachment addAttachment(Attachment attachment);

    Attachment updateAttachment(Attachment newAttachment);

    Attachment removeAttachment(Attachment oldAttachment);

    Collection<Attachment> getAttachments();

    Comment findCommentById(String commentId);

    Comment addComment(Comment comment);

    Comment updateComment(Comment newComment);

    Comment removeComment(Comment comment);

    Collection<Comment> getComments();

    void trigger(UserTaskInstanceJobDescription userTaskInstanceJobDescription);

    Collection<DeadlineInfo<Notification>> getNotStartedDeadlines();

    Collection<DeadlineInfo<Notification>> getNotCompletedDeadlines();

    Collection<DeadlineInfo<Reassignment>> getNotStartedReassignments();

    Collection<DeadlineInfo<Reassignment>> getNotCompletedReassignments();

    void startNotStartedDeadlines();

    void startNotCompletedDeadlines();

    void startNotStartedReassignments();

    void startNotCompletedReassignments();

    void stopNotStartedDeadlines();

    void stopNotStartedReassignments();

    void stopNotCompletedDeadlines();

    void stopNotCompletedReassignments();

    void setPotentialUsers(Set<String> potentialUsers);

    UserTaskLifeCycle getUserTaskLifeCycle();
}
