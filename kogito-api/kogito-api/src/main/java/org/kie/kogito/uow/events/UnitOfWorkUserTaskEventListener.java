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
package org.kie.kogito.uow.events;

import org.kie.kogito.uow.UnitOfWorkManager;
import org.kie.kogito.uow.WorkUnit;
import org.kie.kogito.usertask.UserTaskEventListener;
import org.kie.kogito.usertask.events.UserTaskAssignmentEvent;
import org.kie.kogito.usertask.events.UserTaskAttachmentEvent;
import org.kie.kogito.usertask.events.UserTaskCommentEvent;
import org.kie.kogito.usertask.events.UserTaskDeadlineEvent;
import org.kie.kogito.usertask.events.UserTaskEvent;
import org.kie.kogito.usertask.events.UserTaskStateEvent;
import org.kie.kogito.usertask.events.UserTaskVariableEvent;

public class UnitOfWorkUserTaskEventListener implements UserTaskEventListener {

    UnitOfWorkManager unitOfWorkManager;

    public UnitOfWorkUserTaskEventListener(UnitOfWorkManager unitOfWorkManager) {
        this.unitOfWorkManager = unitOfWorkManager;
    }

    private void intercept(UserTaskEvent event) {
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
        }));
    }

    public void onUserTaskDeadline(UserTaskDeadlineEvent event) {
        intercept(event);
    }

    public void onUserTaskState(UserTaskStateEvent event) {
        intercept(event);
    }

    public void onUserTaskAssignment(UserTaskAssignmentEvent event) {
        intercept(event);
    }

    public void onUserTaskInputVariable(UserTaskVariableEvent event) {
        intercept(event);
    }

    public void onUserTaskOutputVariable(UserTaskVariableEvent event) {
        intercept(event);
    }

    public void onUserTaskAttachmentAdded(UserTaskAttachmentEvent event) {
        intercept(event);
    }

    public void onUserTaskAttachmentDeleted(UserTaskAttachmentEvent event) {
        intercept(event);
    }

    public void onUserTaskAttachmentChange(UserTaskAttachmentEvent event) {
        intercept(event);
    }

    public void onUserTaskCommentChange(UserTaskCommentEvent event) {
        intercept(event);
    }

    public void onUserTaskCommentAdded(UserTaskCommentEvent event) {
        intercept(event);
    }

    public void onUserTaskCommentDeleted(UserTaskCommentEvent event) {
        intercept(event);
    }
}
