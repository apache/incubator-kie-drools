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

import org.kie.kogito.usertask.events.UserTaskAssignmentEvent;
import org.kie.kogito.usertask.events.UserTaskAttachmentEvent;
import org.kie.kogito.usertask.events.UserTaskCommentEvent;
import org.kie.kogito.usertask.events.UserTaskDeadlineEvent;
import org.kie.kogito.usertask.events.UserTaskStateEvent;
import org.kie.kogito.usertask.events.UserTaskVariableEvent;

public interface UserTaskEventListener {

    default void onUserTaskDeadline(UserTaskDeadlineEvent event) {
        // nothing
    }

    default void onUserTaskState(UserTaskStateEvent event) {
        // nothing
    }

    default void onUserTaskAssignment(UserTaskAssignmentEvent event) {
        // nothing
    }

    default void onUserTaskInputVariable(UserTaskVariableEvent event) {
        // nothing
    }

    default void onUserTaskOutputVariable(UserTaskVariableEvent event) {
        // nothing
    }

    default void onUserTaskAttachmentAdded(UserTaskAttachmentEvent event) {
        // nothing
    }

    default void onUserTaskAttachmentDeleted(UserTaskAttachmentEvent event) {
        // nothing
    }

    default void onUserTaskAttachmentChange(UserTaskAttachmentEvent event) {
        // nothing
    }

    default void onUserTaskCommentChange(UserTaskCommentEvent event) {
        // nothing
    }

    default void onUserTaskCommentAdded(UserTaskCommentEvent event) {
        // nothing
    }

    default void onUserTaskCommentDeleted(UserTaskCommentEvent event) {
        // nothing
    }
}
