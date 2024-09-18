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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.internal.usertask.event.KogitoUserTaskEventSupport;
import org.kie.kogito.usertask.UserTaskEventListener;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.events.UserTaskDeadlineEvent;
import org.kie.kogito.usertask.events.UserTaskDeadlineEvent.DeadlineType;
import org.kie.kogito.usertask.events.UserTaskVariableEvent.VariableEventType;
import org.kie.kogito.usertask.impl.events.UserTaskAssignmentEventImpl;
import org.kie.kogito.usertask.impl.events.UserTaskAttachmentEventImpl;
import org.kie.kogito.usertask.impl.events.UserTaskCommentEventImpl;
import org.kie.kogito.usertask.impl.events.UserTaskDeadlineEventImpl;
import org.kie.kogito.usertask.impl.events.UserTaskStateEventImpl;
import org.kie.kogito.usertask.impl.events.UserTaskVariableEventImpl;
import org.kie.kogito.usertask.model.Attachment;
import org.kie.kogito.usertask.model.Comment;

public class KogitoUserTaskEventSupportImpl implements KogitoUserTaskEventSupport {

    private List<UserTaskEventListener> listeners;

    private IdentityProvider identityProvider;

    /**
     * Do not use this constructor. It should be used just by deserialization.
     */
    public KogitoUserTaskEventSupportImpl(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
        this.listeners = new CopyOnWriteArrayList<>();
    }

    private void notifyAllListeners(Consumer<UserTaskEventListener> consumer) {
        this.listeners.forEach(consumer::accept);
    }

    // users tasks events
    @Override
    public void fireOnUserTaskNotStartedDeadline(
            UserTaskInstance userTaskInstance,
            Map<String, Object> notification) {
        fireUserTaskNotification(userTaskInstance, notification, DeadlineType.Started);
    }

    @Override
    public void fireOnUserTaskNotCompletedDeadline(
            UserTaskInstance userTaskInstance,
            Map<String, Object> notification) {
        fireUserTaskNotification(userTaskInstance, notification, DeadlineType.Completed);
    }

    private void fireUserTaskNotification(
            UserTaskInstance userTaskInstance,
            Map<String, Object> notification,
            DeadlineType type) {
        UserTaskDeadlineEvent event = new UserTaskDeadlineEventImpl(userTaskInstance, notification, type, identityProvider.getName());
        notifyAllListeners(l -> l.onUserTaskDeadline(event));
    }

    @Override
    public void fireOneUserTaskStateChange(
            UserTaskInstance userTaskInstance,
            String oldStatus, String newStatus) {
        UserTaskStateEventImpl event = new UserTaskStateEventImpl(userTaskInstance, oldStatus, newStatus, identityProvider.getName());
        event.setOldStatus(oldStatus);
        event.setNewStatus(newStatus);
        notifyAllListeners(l -> l.onUserTaskState(event));
    }

    @Override
    public void fireOnUserTaskAssignmentChange(
            UserTaskInstance userTaskInstance,
            AssignmentType assignmentType,
            Set<String> oldUsersId, Set<String> newUsersId) {
        UserTaskAssignmentEventImpl event = new UserTaskAssignmentEventImpl(userTaskInstance, assignmentType.name(), oldUsersId, newUsersId, identityProvider.getName());
        event.setAssignmentType(assignmentType.name());
        notifyAllListeners(l -> l.onUserTaskAssignment(event));
    }

    @Override
    public void fireOnUserTaskInputVariableChange(
            UserTaskInstance userTaskInstance,
            String variableName, Object newValue, Object oldValue) {
        UserTaskVariableEventImpl event = new UserTaskVariableEventImpl(userTaskInstance, variableName, oldValue, newValue, VariableEventType.INPUT, identityProvider.getName());
        notifyAllListeners(l -> l.onUserTaskInputVariable(event));
    }

    @Override
    public void fireOnUserTaskOutputVariableChange(
            UserTaskInstance userTaskInstance,
            String variableName, Object newValue, Object oldValue) {
        UserTaskVariableEventImpl event = new UserTaskVariableEventImpl(userTaskInstance, variableName, oldValue, newValue, VariableEventType.OUTPUT, identityProvider.getName());
        notifyAllListeners(l -> l.onUserTaskOutputVariable(event));
    }

    @Override
    public void fireOnUserTaskAttachmentAdded(
            UserTaskInstance userTaskInstance,
            Attachment addedAttachment) {

        UserTaskAttachmentEventImpl event = new UserTaskAttachmentEventImpl(userTaskInstance, identityProvider.getName());
        event.setNewAttachment(addedAttachment);
        notifyAllListeners(l -> l.onUserTaskAttachmentAdded(event));
    }

    @Override
    public void fireOnUserTaskAttachmentChange(
            UserTaskInstance userTaskInstance,
            Attachment oldAttachment, Attachment newAttachment) {
        UserTaskAttachmentEventImpl event = new UserTaskAttachmentEventImpl(userTaskInstance, identityProvider.getName());
        event.setOldAttachment(oldAttachment);
        event.setNewAttachment(newAttachment);
        notifyAllListeners(l -> l.onUserTaskAttachmentChange(event));
    }

    @Override
    public void fireOnUserTaskAttachmentDeleted(
            UserTaskInstance userTaskInstance,
            Attachment deletedAttachment) {
        UserTaskAttachmentEventImpl event = new UserTaskAttachmentEventImpl(userTaskInstance, identityProvider.getName());
        event.setOldAttachment(deletedAttachment);
        notifyAllListeners(l -> l.onUserTaskAttachmentDeleted(event));
    }

    @Override
    public void fireOnUserTaskCommentAdded(
            UserTaskInstance userTaskInstance,
            Comment addedComment) {
        UserTaskCommentEventImpl event = new UserTaskCommentEventImpl(userTaskInstance, identityProvider.getName());
        event.setNewComment(addedComment);
        notifyAllListeners(l -> l.onUserTaskCommentAdded(event));
    }

    @Override
    public void fireOnUserTaskCommentChange(
            UserTaskInstance userTaskInstance,
            Comment oldComment, Comment newComment) {
        UserTaskCommentEventImpl event = new UserTaskCommentEventImpl(userTaskInstance, identityProvider.getName());
        event.setOldComment(oldComment);
        event.setNewComment(newComment);
        notifyAllListeners(l -> l.onUserTaskCommentChange(event));
    }

    @Override
    public void fireOnUserTaskCommentDeleted(
            UserTaskInstance userTaskInstance,
            Comment deletedComment) {
        UserTaskCommentEventImpl event = new UserTaskCommentEventImpl(userTaskInstance, identityProvider.getName());
        event.setOldComment(deletedComment);
        notifyAllListeners(l -> l.onUserTaskCommentDeleted(event));
    }

    @Override
    public void reset() {
        this.listeners.clear();
    }

    @Override
    public void addEventListener(UserTaskEventListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    @Override
    public void removeEventListener(UserTaskEventListener listener) {
        this.listeners.remove(listener);
    }

}
