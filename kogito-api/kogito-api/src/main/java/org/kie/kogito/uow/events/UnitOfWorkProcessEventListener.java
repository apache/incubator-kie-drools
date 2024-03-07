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

import org.kie.api.event.process.ErrorEvent;
import org.kie.api.event.process.MessageEvent;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEvent;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.event.process.SLAViolatedEvent;
import org.kie.api.event.process.SignalEvent;
import org.kie.api.event.usertask.UserTaskAssignmentEvent;
import org.kie.api.event.usertask.UserTaskAttachmentEvent;
import org.kie.api.event.usertask.UserTaskCommentEvent;
import org.kie.api.event.usertask.UserTaskDeadlineEvent;
import org.kie.api.event.usertask.UserTaskEvent;
import org.kie.api.event.usertask.UserTaskStateEvent;
import org.kie.api.event.usertask.UserTaskVariableEvent;
import org.kie.kogito.internal.process.event.DefaultKogitoProcessEventListener;
import org.kie.kogito.internal.process.event.ProcessWorkItemTransitionEvent;
import org.kie.kogito.uow.UnitOfWorkManager;
import org.kie.kogito.uow.WorkUnit;

public class UnitOfWorkProcessEventListener extends DefaultKogitoProcessEventListener {

    UnitOfWorkManager unitOfWorkManager;

    public UnitOfWorkProcessEventListener(UnitOfWorkManager unitOfWorkManager) {
        this.unitOfWorkManager = unitOfWorkManager;
    }

    private void intercept(ProcessEvent event) {
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
        }));
    }

    private void intercept(UserTaskEvent event) {
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
        }));
    }

    @Override
    public void beforeProcessStarted(ProcessStartedEvent event) {
        intercept(event);
    }

    @Override
    public void afterProcessStarted(ProcessStartedEvent event) {
    }

    @Override
    public void beforeProcessCompleted(ProcessCompletedEvent event) {

    }

    @Override
    public void afterProcessCompleted(ProcessCompletedEvent event) {
        intercept(event);
    }

    @Override
    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
        intercept(event);
    }

    @Override
    public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {

    }

    @Override
    public void beforeNodeLeft(ProcessNodeLeftEvent event) {

    }

    @Override
    public void afterNodeLeft(ProcessNodeLeftEvent event) {
        intercept(event);
    }

    @Override
    public void beforeVariableChanged(ProcessVariableChangedEvent event) {

    }

    @Override
    public void afterVariableChanged(ProcessVariableChangedEvent event) {
        intercept(event);
    }

    @Override
    public void beforeSLAViolated(SLAViolatedEvent event) {
        intercept(event);
    }

    @Override
    public void afterSLAViolated(SLAViolatedEvent event) {

    }

    @Override
    public void beforeWorkItemTransition(ProcessWorkItemTransitionEvent event) {

    }

    @Override
    public void afterWorkItemTransition(ProcessWorkItemTransitionEvent event) {
        intercept(event);
    }

    @Override
    public void onSignal(SignalEvent event) {
        intercept(event);
    }

    @Override
    public void onMessage(MessageEvent event) {
        intercept(event);
    }

    // user tasks
    @Override
    public void onUserTaskDeadline(UserTaskDeadlineEvent event) {
        intercept(event);
    }

    @Override
    public void onUserTaskState(UserTaskStateEvent event) {
        intercept(event);
    }

    @Override
    public void onUserTaskAssignment(UserTaskAssignmentEvent event) {
        intercept(event);
    }

    @Override
    public void onUserTaskAttachmentAdded(UserTaskAttachmentEvent event) {
        intercept(event);
    }

    @Override
    public void onUserTaskAttachmentChange(UserTaskAttachmentEvent event) {
        intercept(event);
    }

    @Override
    public void onUserTaskAttachmentDeleted(UserTaskAttachmentEvent event) {
        intercept(event);
    }

    @Override
    public void onUserTaskCommentAdded(UserTaskCommentEvent event) {
        intercept(event);
    }

    @Override
    public void onUserTaskCommentChange(UserTaskCommentEvent event) {
        intercept(event);
    }

    @Override
    public void onUserTaskCommentDeleted(UserTaskCommentEvent event) {
        intercept(event);
    }

    @Override
    public void onUserTaskInputVariable(UserTaskVariableEvent event) {
        intercept(event);
    }

    @Override
    public void onUserTaskOutputVariable(UserTaskVariableEvent event) {
        intercept(event);
    }

    @Override
    public void onError(ErrorEvent event) {
        intercept(event);
    }

}
