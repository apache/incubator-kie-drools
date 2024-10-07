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
package org.kie.kogito.internal.usertask.event;

import java.util.Map;
import java.util.Set;

import org.kie.kogito.usertask.UserTaskEventListener;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.lifecycle.UserTaskState;
import org.kie.kogito.usertask.model.Attachment;
import org.kie.kogito.usertask.model.Comment;

public interface KogitoUserTaskEventSupport {
    enum AssignmentType {
        USER_OWNERS,
        USER_GROUPS,
        USERS_EXCLUDED,
        ADMIN_GROUPS,
        ADMIN_USERS
    };

    void fireOneUserTaskStateChange(
            UserTaskInstance instance,
            UserTaskState oldPhaseStatus, UserTaskState newPhaseStatus);

    void fireOnUserTaskNotStartedDeadline(
            UserTaskInstance instance,
            Map<String, Object> notification);

    void fireOnUserTaskNotCompletedDeadline(
            UserTaskInstance instance,
            Map<String, Object> notification);

    void fireOnUserTaskAssignmentChange(
            UserTaskInstance instance,
            AssignmentType assignmentType,
            Set<String> oldUsersId, Set<String> newUsersId);

    void fireOnUserTaskInputVariableChange(
            UserTaskInstance instance,
            String variableName,
            Object newValue, Object oldValue);

    void fireOnUserTaskOutputVariableChange(
            UserTaskInstance instance,
            String variableName,
            Object newValue, Object oldValue);

    void fireOnUserTaskAttachmentAdded(
            UserTaskInstance instance,
            Attachment addedAttachment);

    void fireOnUserTaskAttachmentDeleted(
            UserTaskInstance instance,
            Attachment deletedAttachment);

    void fireOnUserTaskAttachmentChange(
            UserTaskInstance instance,
            Attachment oldAttachment, Attachment newAttachment);

    void fireOnUserTaskCommentChange(
            UserTaskInstance instance,
            Comment oldComment, Comment newComment);

    void fireOnUserTaskCommentDeleted(
            UserTaskInstance instance,
            Comment deletedComment);

    void fireOnUserTaskCommentAdded(
            UserTaskInstance instance,
            Comment addedComment);

    void reset();

    void addEventListener(UserTaskEventListener listener);

    void removeEventListener(UserTaskEventListener listener);

}
