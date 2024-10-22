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
package org.kie.kogito.usertask.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.internal.usertask.event.KogitoUserTaskEventSupport;
import org.kie.kogito.internal.usertask.event.KogitoUserTaskEventSupport.AssignmentType;
import org.kie.kogito.usertask.UserTask;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.UserTaskInstances;
import org.kie.kogito.usertask.lifecycle.UserTaskLifeCycle;
import org.kie.kogito.usertask.lifecycle.UserTaskState;
import org.kie.kogito.usertask.lifecycle.UserTaskTransitionToken;
import org.kie.kogito.usertask.model.Attachment;
import org.kie.kogito.usertask.model.Comment;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DefaultUserTaskInstance implements UserTaskInstance {

    private String id;

    private String userTaskId;

    private UserTaskState status;
    private String actualOwner;
    private String taskName;
    private String taskDescription;
    private String taskPriority;
    private Set<String> potentialUsers;
    private Set<String> potentialGroups;
    private Set<String> adminUsers;
    private Set<String> adminGroups;
    private Set<String> excludedUsers;
    private List<Attachment> attachments;
    private List<Comment> comments;
    private String externalReferenceId;

    private Map<String, Object> inputs;
    private Map<String, Object> outputs;

    private Map<String, Object> metadata;
    @JsonIgnore
    private UserTaskInstances instances;

    @JsonIgnore
    private UserTask userTask;
    @JsonIgnore
    private KogitoUserTaskEventSupport userTaskEventSupport;
    @JsonIgnore
    private UserTaskLifeCycle setUserTaskLifeCycle;

    public DefaultUserTaskInstance() {
        this.inputs = new HashMap<>();
        this.outputs = new HashMap<>();
        this.status = UserTaskState.initalized();
        this.metadata = new HashMap<>();
        this.attachments = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.potentialUsers = new HashSet<>();
        this.potentialGroups = new HashSet<>();
        this.adminUsers = new HashSet<>();
        this.adminGroups = new HashSet<>();
        this.excludedUsers = new HashSet<>();
    }

    public DefaultUserTaskInstance(UserTask userTask) {
        this();
        this.id = UUID.randomUUID().toString();
        this.userTask = userTask;
        this.instances = userTask.instances();
    }

    public void setUserTaskEventSupport(KogitoUserTaskEventSupport userTaskEventSupport) {
        this.userTaskEventSupport = userTaskEventSupport;
    }

    public void setUserTaskLifeCycle(UserTaskLifeCycle userTaskLifeCycle) {
        this.setUserTaskLifeCycle = userTaskLifeCycle;
    }

    public void setInstances(UserTaskInstances instances) {
        this.instances = instances;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getUserTaskId() {
        return userTaskId;
    }

    public void setUserTaskId(String userTaskId) {
        this.userTaskId = userTaskId;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public void setStatus(UserTaskState status) {
        this.status = status;
    }

    @Override
    public UserTaskState getStatus() {
        return status;
    }

    @Override
    public boolean hasActualOwner() {
        return actualOwner != null;
    }

    @Override
    public void setActuaOwner(String actualOwner) {
        this.actualOwner = actualOwner;
        if (this.userTaskEventSupport != null) {
            this.userTaskEventSupport.fireOneUserTaskStateChange(this, this.status, this.status);
        }
        updatePersistence();
    }

    @Override
    public String getActualOwner() {
        return actualOwner;
    }

    @Override
    public String getExternalReferenceId() {
        return externalReferenceId;
    }

    public void setExternalReferenceId(String externalReferenceId) {
        this.externalReferenceId = externalReferenceId;
    }

    @Override
    public void transition(String transitionId, Map<String, Object> data, IdentityProvider identity) {
        Optional<UserTaskTransitionToken> next = Optional.of(this.setUserTaskLifeCycle.newTransitionToken(transitionId, this, data));
        while (next.isPresent()) {
            UserTaskTransitionToken transition = next.get();
            next = this.setUserTaskLifeCycle.transition(this, transition, identity);
            this.status = transition.target();
            this.updatePersistenceOrRemove();
            this.userTaskEventSupport.fireOneUserTaskStateChange(this, transition.source(), transition.target());
        }

    }

    private void updatePersistence() {
        if (this.instances != null) {
            this.instances.update(this);
        }
    }

    private void updatePersistenceOrRemove() {
        if (this.status.isTerminate()) {
            this.instances.remove(this.id);
        } else {
            this.instances.update(this);
        }
    }

    @Override
    public UserTask getUserTask() {
        return userTask;
    }

    public void setUserTask(UserTask userTask) {
        this.userTask = userTask;
    }

    public Map<String, Object> getInputs() {
        return inputs;
    }

    public void setInputs(Map<String, Object> inputs) {
        inputs.forEach(this::setInput);
    }

    public Map<String, Object> getOutputs() {
        return outputs;
    }

    public void setOutputs(Map<String, Object> outputs) {
        outputs.forEach(this::setOutput);
    }

    @Override
    public void setInput(String key, Object newValue) {
        Object oldValue = this.inputs.put(key, newValue);
        if (this.userTaskEventSupport != null) {
            this.userTaskEventSupport.fireOnUserTaskInputVariableChange(this, key, oldValue, newValue);
        }
        updatePersistence();
    }

    @Override
    public void setOutput(String key, Object newValue) {
        Object oldValue = this.outputs.put(key, newValue);
        if (this.userTaskEventSupport != null) {
            this.userTaskEventSupport.fireOnUserTaskOutputVariableChange(this, key, oldValue, newValue);
        }
        updatePersistence();
    }

    /**
     * Returns name of the task
     * 
     * @return task name
     */
    @Override
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
        if (this.userTaskEventSupport != null) {
            this.userTaskEventSupport.fireOneUserTaskStateChange(this, this.status, this.status);
        }
        updatePersistence();
    }

    public void fireInitialStateChange() {
        if (this.userTaskEventSupport != null) {
            this.userTaskEventSupport.fireOneUserTaskStateChange(this, this.status, this.status);
        }
    }

    /**
     * Returns optional description of the task
     * 
     * @return task description if present
     */
    @Override
    public String getTaskDescription() {
        return this.taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
        if (this.userTaskEventSupport != null) {
            this.userTaskEventSupport.fireOneUserTaskStateChange(this, this.status, this.status);
        }
        updatePersistence();
    }

    /**
     * Returns optional priority of the task
     * 
     * @return task priority if present
     */
    @Override
    public String getTaskPriority() {
        return this.taskPriority;
    }

    public void setTaskPriority(String taskPriority) {
        this.taskPriority = taskPriority;
        if (this.userTaskEventSupport != null) {
            this.userTaskEventSupport.fireOneUserTaskStateChange(this, this.status, this.status);
        }
        updatePersistence();
    }

    /**
     * Returns potential users that can work on this task
     * 
     * @return potential users
     */
    @Override
    public Set<String> getPotentialUsers() {
        return this.potentialUsers;
    }

    public void setPotentialUsers(Set<String> potentialUsers) {
        Set<String> oldValues = new HashSet<>(this.potentialUsers);
        this.potentialUsers = potentialUsers;
        if (this.userTaskEventSupport != null) {
            this.userTaskEventSupport.fireOnUserTaskAssignmentChange(this, AssignmentType.USER_OWNERS, oldValues, potentialUsers);
        }
        updatePersistence();
    }

    /**
     * Returns potential groups that can work on this task
     * 
     * @return potential groups
     */
    @Override
    public Set<String> getPotentialGroups() {
        return this.potentialGroups;
    }

    public void setPotentialGroups(Set<String> potentialGroups) {
        Set<String> oldValues = new HashSet<>(this.potentialGroups);
        this.potentialGroups = potentialGroups;
        if (this.userTaskEventSupport != null) {
            this.userTaskEventSupport.fireOnUserTaskAssignmentChange(this, AssignmentType.USER_GROUPS, oldValues, potentialGroups);
        }
        updatePersistence();
    }

    /**
     * Returns admin users that can administer this task
     * 
     * @return admin users
     */
    @Override
    public Set<String> getAdminUsers() {
        return this.adminUsers;
    }

    public void setAdminUsers(Set<String> adminUsers) {
        Set<String> oldValues = new HashSet<>(this.adminUsers);
        this.adminUsers = adminUsers;
        if (this.userTaskEventSupport != null) {
            this.userTaskEventSupport.fireOnUserTaskAssignmentChange(this, AssignmentType.ADMIN_USERS, oldValues, adminUsers);
        }
        updatePersistence();
    }

    /**
     * Returns admin groups that can administer this task
     * 
     * @return admin groups
     */
    @Override
    public Set<String> getAdminGroups() {
        return this.adminGroups;
    }

    public void setAdminGroups(Set<String> adminGroups) {
        Set<String> oldValues = new HashSet<>(this.adminGroups);
        this.adminGroups = adminGroups;
        if (this.userTaskEventSupport != null) {
            this.userTaskEventSupport.fireOnUserTaskAssignmentChange(this, AssignmentType.ADMIN_GROUPS, oldValues, adminGroups);
        }
        updatePersistence();
    }

    /**
     * Returns excluded users that cannot work on this task
     * 
     * @return excluded users
     */
    @Override
    public Set<String> getExcludedUsers() {
        return this.excludedUsers;
    }

    public void setExcludedUsers(Set<String> excludedUsers) {
        Set<String> oldValues = new HashSet<>(this.excludedUsers);
        this.excludedUsers = excludedUsers;
        if (this.userTaskEventSupport != null) {
            this.userTaskEventSupport.fireOnUserTaskAssignmentChange(this, AssignmentType.USERS_EXCLUDED, oldValues, excludedUsers);
        }
        updatePersistence();
    }

    /**
     * Returns task attachments
     * 
     * @return A map which key is the attachment id and value the attachment object
     */

    public List<Attachment> getAttachments() {
        return attachments;
    }

    @Override
    public Attachment addAttachment(Attachment attachment) {
        attachment.setId(UUID.randomUUID().toString());
        attachment.setUpdatedAt(new Date());
        this.attachments.add(attachment);
        if (this.userTaskEventSupport != null) {
            this.userTaskEventSupport.fireOnUserTaskAttachmentAdded(this, attachment);
        }
        updatePersistence();
        return attachment;
    }

    @Override
    public Attachment updateAttachment(Attachment newAttachment) {
        Optional<Attachment> oldAttachment = this.attachments.stream().filter(e -> e.getId().equals(newAttachment.getId())).findFirst();
        if (oldAttachment.isEmpty()) {
            return null;
        }
        this.attachments.remove(oldAttachment.get());
        if (newAttachment.getName() == null) {
            String path = newAttachment.getContent().getPath();
            int idx = path.lastIndexOf("/");
            if (idx > 0) {
                path = path.substring(idx + 1);
            }
            newAttachment.setName(path);
        }
        newAttachment.setUpdatedAt(new Date());
        this.attachments.add(newAttachment);
        if (this.userTaskEventSupport != null) {
            this.userTaskEventSupport.fireOnUserTaskAttachmentChange(this, oldAttachment.get(), newAttachment);
        }
        updatePersistence();
        return newAttachment;
    }

    @Override
    public Attachment removeAttachment(Attachment attachment) {
        Optional<Attachment> oldAttachment = this.attachments.stream().filter(e -> e.getId().equals(attachment.getId())).findFirst();
        if (oldAttachment.isEmpty()) {
            return null;
        }
        this.attachments.remove(attachment);
        if (this.userTaskEventSupport != null) {
            this.userTaskEventSupport.fireOnUserTaskAttachmentDeleted(this, oldAttachment.get());
        }
        updatePersistence();
        return oldAttachment.get();
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
        updatePersistence();

    }

    /**
     * Returns task comments
     * 
     * @return A map which key is the comment id and value the comment object
     */

    public List<Comment> getComments() {
        return comments;
    }

    @Override
    public Comment addComment(Comment comment) {
        comment.setId(UUID.randomUUID().toString());
        comment.setUpdatedAt(new Date());
        this.comments.add(comment);
        if (this.userTaskEventSupport != null) {
            this.userTaskEventSupport.fireOnUserTaskCommentAdded(this, comment);
        }
        updatePersistence();
        return comment;
    }

    @Override
    public Comment updateComment(Comment newComment) {
        Optional<Comment> oldComment = this.comments.stream().filter(e -> e.getId().equals(newComment.getId())).findFirst();
        if (oldComment.isEmpty()) {
            return null;
        }
        this.comments.remove(oldComment.get());
        newComment.setUpdatedAt(new Date());
        this.comments.add(newComment);
        if (this.userTaskEventSupport != null) {
            this.userTaskEventSupport.fireOnUserTaskCommentChange(this, oldComment.get(), newComment);
        }
        updatePersistence();
        return newComment;
    }

    @Override
    public Comment removeComment(Comment comment) {
        Optional<Comment> oldComment = this.comments.stream().filter(e -> e.getId().equals(comment.getId())).findFirst();
        if (oldComment.isEmpty()) {
            return null;
        }
        this.comments.remove(comment);
        if (this.userTaskEventSupport != null) {
            this.userTaskEventSupport.fireOnUserTaskCommentDeleted(this, oldComment.get());
        }
        updatePersistence();
        return oldComment.get();
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
        updatePersistence();
    }

    public void setMetadata(String key, Object value) {
        this.metadata.put(key, value);
        updatePersistence();
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
        updatePersistence();
    }

    @Override
    public Attachment findAttachmentById(String attachmentId) {
        return this.attachments.stream().filter(e -> e.getId().equals(attachmentId)).findAny().orElse(null);
    }

    @Override
    public Comment findCommentById(String commentId) {
        return this.comments.stream().filter(e -> e.getId().equals(commentId)).findAny().orElse(null);
    }

    @Override
    public String toString() {
        return "DefaultUserTaskInstance [id=" + id + ", status=" + status + ", actualOwner=" + actualOwner + ", taskName=" + taskName + ", taskDescription=" + taskDescription + ", taskPriority="
                + taskPriority + "]";
    }

}
