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
package org.jbpm.process.instance.event;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import org.jbpm.workflow.instance.node.HumanTaskNodeInstance;
import org.kie.api.event.process.MessageEvent;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.event.process.SLAViolatedEvent;
import org.kie.api.event.process.SignalEvent;
import org.kie.api.event.usertask.UserTaskDeadlineEvent;
import org.kie.api.event.usertask.UserTaskDeadlineEvent.DeadlineType;
import org.kie.api.event.usertask.UserTaskVariableEvent.VariableEventType;
import org.kie.api.runtime.KieRuntime;
import org.kie.internal.runtime.Closeable;
import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.internal.process.event.KogitoProcessEventListener;
import org.kie.kogito.internal.process.event.KogitoProcessEventSupport;
import org.kie.kogito.internal.process.event.ProcessWorkItemTransitionEvent;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.process.workitem.Attachment;
import org.kie.kogito.process.workitem.Comment;
import org.kie.kogito.process.workitem.HumanTaskWorkItem;
import org.kie.kogito.process.workitem.Transition;

public class KogitoProcessEventSupportImpl implements KogitoProcessEventSupport {

    private final List<KogitoProcessEventListener> listeners = new CopyOnWriteArrayList<>();

    private final IdentityProvider identityProvider;

    /**
     * Do not use this constructor. It should be used just by deserialization.
     */
    public KogitoProcessEventSupportImpl(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }

    private void notifyAllListeners(Consumer<KogitoProcessEventListener> consumer) {
        this.listeners.forEach(l -> consumer.accept(l));
    }

    @Override
    public final synchronized void addEventListener(KogitoProcessEventListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    @Override
    public final void removeEventListener(KogitoProcessEventListener listener) {
        this.listeners.remove(listener);
    }

    public List<KogitoProcessEventListener> getEventListeners() {
        return Collections.unmodifiableList(this.listeners);
    }

    private void clear() {
        this.listeners.forEach(l -> {
            if (l instanceof Closeable) {
                ((Closeable) l).close();
            }
        });

        this.listeners.clear();
    }

    @Override
    public void fireBeforeProcessStarted(final KogitoProcessInstance instance, KieRuntime kruntime) {
        final ProcessStartedEvent event = new ProcessStartedEventImpl(instance, kruntime, identityProvider.getName());
        notifyAllListeners(l -> l.beforeProcessStarted(event));
    }

    @Override
    public void fireAfterProcessStarted(final KogitoProcessInstance instance, KieRuntime kruntime) {
        final ProcessStartedEvent event = new ProcessStartedEventImpl(instance, kruntime, identityProvider.getName());
        notifyAllListeners(l -> l.afterProcessStarted(event));
    }

    @Override
    public void fireBeforeProcessCompleted(final KogitoProcessInstance instance, KieRuntime kruntime) {
        final ProcessCompletedEvent event = new ProcessCompletedEventImpl(instance, kruntime, identityProvider.getName());
        notifyAllListeners(l -> l.beforeProcessCompleted(event));
    }

    @Override
    public void fireAfterProcessCompleted(final KogitoProcessInstance instance, KieRuntime kruntime) {
        final ProcessCompletedEvent event = new ProcessCompletedEventImpl(instance, kruntime, identityProvider.getName());
        notifyAllListeners(l -> l.afterProcessCompleted(event));
    }

    @Override
    public void fireBeforeNodeTriggered(final KogitoNodeInstance nodeInstance, KieRuntime kruntime) {
        final ProcessNodeTriggeredEvent event = new KogitoProcessNodeTriggeredEventImpl(nodeInstance, kruntime, identityProvider.getName());
        notifyAllListeners(l -> l.beforeNodeTriggered(event));
    }

    @Override
    public void fireAfterNodeTriggered(final KogitoNodeInstance nodeInstance, KieRuntime kruntime) {
        final ProcessNodeTriggeredEvent event = new KogitoProcessNodeTriggeredEventImpl(nodeInstance, kruntime, identityProvider.getName());
        notifyAllListeners(l -> l.afterNodeTriggered(event));
    }

    @Override
    public void fireBeforeNodeLeft(final KogitoNodeInstance nodeInstance, KieRuntime kruntime) {
        final ProcessNodeLeftEvent event = new KogitoProcessNodeLeftEventImpl(nodeInstance, kruntime, identityProvider.getName());
        notifyAllListeners(l -> l.beforeNodeLeft(event));
    }

    @Override
    public void fireAfterNodeLeft(final KogitoNodeInstance nodeInstance, KieRuntime kruntime) {
        final ProcessNodeLeftEvent event = new KogitoProcessNodeLeftEventImpl(nodeInstance, kruntime, identityProvider.getName());
        notifyAllListeners(l -> l.afterNodeLeft(event));
    }

    @Override
    public void fireBeforeVariableChanged(final String id, final String instanceId,
            final Object oldValue, final Object newValue,
            final List<String> tags,
            final KogitoProcessInstance processInstance, KogitoNodeInstance nodeInstance, KieRuntime kruntime) {
        final ProcessVariableChangedEvent event = new KogitoProcessVariableChangedEventImpl(
                id, instanceId, oldValue, newValue, tags, processInstance, nodeInstance, kruntime, identityProvider.getName());
        notifyAllListeners(l -> l.beforeVariableChanged(event));
    }

    @Override
    public void fireAfterVariableChanged(final String name, final String id,
            final Object oldValue, final Object newValue,
            final List<String> tags,
            final KogitoProcessInstance processInstance, KogitoNodeInstance nodeInstance, KieRuntime kruntime) {
        final ProcessVariableChangedEvent event = new KogitoProcessVariableChangedEventImpl(
                name, id, oldValue, newValue, tags, processInstance, nodeInstance, kruntime, identityProvider.getName());
        notifyAllListeners(l -> l.afterVariableChanged(event));
    }

    @Override
    public void fireBeforeSLAViolated(final KogitoProcessInstance instance, KieRuntime kruntime) {
        final SLAViolatedEvent event = new SLAViolatedEventImpl(instance, kruntime, identityProvider.getName());
        notifyAllListeners(l -> l.beforeSLAViolated(event));
    }

    @Override
    public void fireAfterSLAViolated(final KogitoProcessInstance instance, KieRuntime kruntime) {
        final SLAViolatedEvent event = new SLAViolatedEventImpl(instance, kruntime, identityProvider.getName());
        notifyAllListeners(l -> l.afterSLAViolated(event));
    }

    @Override
    public void fireBeforeSLAViolated(final KogitoProcessInstance instance, KogitoNodeInstance nodeInstance, KieRuntime kruntime) {
        final SLAViolatedEvent event = new SLAViolatedEventImpl(instance, nodeInstance, kruntime, identityProvider.getName());
        notifyAllListeners(l -> l.beforeSLAViolated(event));
    }

    @Override
    public void fireAfterSLAViolated(final KogitoProcessInstance instance, KogitoNodeInstance nodeInstance, KieRuntime kruntime) {
        final SLAViolatedEvent event = new SLAViolatedEventImpl(instance, nodeInstance, kruntime, identityProvider.getName());
        notifyAllListeners(l -> l.afterSLAViolated(event));
    }

    @Override
    public void fireBeforeWorkItemTransition(final KogitoProcessInstance instance, KogitoWorkItem workitem, Transition<?> transition, KieRuntime kruntime) {
        final ProcessWorkItemTransitionEvent event = new KogitoProcessWorkItemTransitionEventImpl(instance, workitem, transition, kruntime, false, identityProvider.getName());
        notifyAllListeners(l -> l.beforeWorkItemTransition(event));
    }

    @Override
    public void fireAfterWorkItemTransition(final KogitoProcessInstance instance, KogitoWorkItem workitem, Transition<?> transition, KieRuntime kruntime) {
        final ProcessWorkItemTransitionEvent event = new KogitoProcessWorkItemTransitionEventImpl(instance, workitem, transition, kruntime, true, identityProvider.getName());
        notifyAllListeners(l -> l.afterWorkItemTransition(event));
    }

    @Override
    public void fireOnSignal(final KogitoProcessInstance instance, KogitoNodeInstance nodeInstance, KieRuntime kruntime, String signalName, Object signalObject) {
        final SignalEvent event = new SignalEventImpl(instance, kruntime, nodeInstance, signalName, signalObject, identityProvider.getName());
        notifyAllListeners(l -> l.onSignal(event));
    }

    @Override
    public void fireOnMessage(final KogitoProcessInstance instance, KogitoNodeInstance nodeInstance, KieRuntime kruntime, String messageName, Object messageObject) {
        final MessageEvent event = new MessageEventImpl(instance, kruntime, nodeInstance, messageName, messageObject, identityProvider.getName());
        notifyAllListeners(l -> l.onMessage(event));
    }

    // users tasks events
    @Override
    public void fireOnUserTaskNotStartedDeadline(KogitoProcessInstance instance,
            KogitoNodeInstance nodeInstance,
            HumanTaskWorkItem workItem,
            Map<String, Object> notification,
            KieRuntime kruntime) {
        fireUserTaskNotification(instance, nodeInstance, workItem, notification, DeadlineType.Started, kruntime);
    }

    @Override
    public void fireOnUserTaskNotCompletedDeadline(KogitoProcessInstance instance,
            KogitoNodeInstance nodeInstance,
            HumanTaskWorkItem workItem,
            Map<String, Object> notification,
            KieRuntime kruntime) {
        fireUserTaskNotification(instance, nodeInstance, workItem, notification, DeadlineType.Completed, kruntime);
    }

    private void fireUserTaskNotification(KogitoProcessInstance instance,
            KogitoNodeInstance nodeInstance,
            HumanTaskWorkItem workItem,
            Map<String, Object> notification,
            DeadlineType type,
            KieRuntime kruntime) {
        UserTaskDeadlineEvent event = new UserTaskDeadlineEventImpl(instance, (HumanTaskNodeInstance) nodeInstance, workItem, notification, type, kruntime, identityProvider.getName());
        notifyAllListeners(l -> l.onUserTaskDeadline(event));
    }

    @Override
    public void fireOneUserTaskStateChange(
            KogitoProcessInstance instance,
            KogitoNodeInstance nodeInstance,
            KieRuntime kruntime,
            String oldStatus, String newStatus) {
        UserTaskStateEventImpl event = new UserTaskStateEventImpl(instance, (HumanTaskNodeInstance) nodeInstance, kruntime, identityProvider.getName());
        event.setOldStatus(oldStatus);
        event.setNewStatus(newStatus);
        notifyAllListeners(l -> l.onUserTaskState(event));
    }

    @Override
    public void fireOnUserTaskAssignmentChange(
            KogitoProcessInstance instance,
            KogitoNodeInstance nodeInstance,
            KieRuntime kruntime,
            AssignmentType assignmentType,
            Set<String> oldUsersId, Set<String> newUsersId) {
        UserTaskAssignmentEventImpl event = new UserTaskAssignmentEventImpl(instance, (HumanTaskNodeInstance) nodeInstance, kruntime, identityProvider.getName());
        event.setAssignmentType(assignmentType.name());
        event.setOldUsersId(oldUsersId);
        event.setNewUsersId(newUsersId);
        notifyAllListeners(l -> l.onUserTaskAssignment(event));
    }

    @Override
    public void fireOnUserTaskInputVariableChange(
            KogitoProcessInstance instance,
            KogitoNodeInstance nodeInstance,
            KieRuntime kruntime,
            String variableName, Object newValue, Object oldValue) {
        UserTaskVariableEventImpl event = new UserTaskVariableEventImpl(instance, (HumanTaskNodeInstance) nodeInstance, kruntime, identityProvider.getName());
        event.setVariableName(variableName);
        event.setOldValue(oldValue);
        event.setNewValue(newValue);
        event.setVariableType(VariableEventType.INPUT);
        notifyAllListeners(l -> l.onUserTaskInputVariable(event));
    }

    @Override
    public void fireOnUserTaskOutputVariableChange(
            KogitoProcessInstance instance,
            KogitoNodeInstance nodeInstance,
            KieRuntime kruntime,
            String variableName, Object newValue, Object oldValue) {
        UserTaskVariableEventImpl event = new UserTaskVariableEventImpl(instance, (HumanTaskNodeInstance) nodeInstance, kruntime, identityProvider.getName());
        event.setVariableName(variableName);
        event.setOldValue(oldValue);
        event.setNewValue(newValue);
        event.setVariableType(VariableEventType.OUTPUT);
        notifyAllListeners(l -> l.onUserTaskOutputVariable(event));
    }

    @Override
    public void fireOnUserTaskAttachmentAdded(
            KogitoProcessInstance instance,
            KogitoNodeInstance nodeInstance,
            KieRuntime kruntime,
            Attachment addedAttachment) {

        UserTaskAttachmentEventImpl event = new UserTaskAttachmentEventImpl(instance, (HumanTaskNodeInstance) nodeInstance, kruntime, identityProvider.getName());
        event.setNewAttachment(addedAttachment);
        notifyAllListeners(l -> l.onUserTaskAttachmentAdded(event));
    }

    @Override
    public void fireOnUserTaskAttachmentChange(
            KogitoProcessInstance instance,
            KogitoNodeInstance nodeInstance,
            KieRuntime kruntime,
            Attachment oldAttachment, Attachment newAttachment) {
        UserTaskAttachmentEventImpl event = new UserTaskAttachmentEventImpl(instance, (HumanTaskNodeInstance) nodeInstance, kruntime, identityProvider.getName());
        event.setOldAttachment(oldAttachment);
        event.setNewAttachment(newAttachment);
        notifyAllListeners(l -> l.onUserTaskAttachmentChange(event));
    }

    @Override
    public void fireOnUserTaskAttachmentDeleted(
            KogitoProcessInstance instance,
            KogitoNodeInstance nodeInstance,
            KieRuntime kruntime,
            Attachment deletedAttachment) {
        UserTaskAttachmentEventImpl event = new UserTaskAttachmentEventImpl(instance, (HumanTaskNodeInstance) nodeInstance, kruntime, identityProvider.getName());
        event.setOldAttachment(deletedAttachment);
        notifyAllListeners(l -> l.onUserTaskAttachmentDeleted(event));
    }

    @Override
    public void fireOnUserTaskCommentAdded(
            KogitoProcessInstance instance,
            KogitoNodeInstance nodeInstance,
            KieRuntime kruntime,
            Comment addedComment) {
        UserTaskCommentEventImpl event = new UserTaskCommentEventImpl(instance, (HumanTaskNodeInstance) nodeInstance, kruntime, identityProvider.getName());
        event.setNewComment(addedComment);
        notifyAllListeners(l -> l.onUserTaskCommentAdded(event));
    }

    @Override
    public void fireOnUserTaskCommentChange(
            KogitoProcessInstance instance,
            KogitoNodeInstance nodeInstance,
            KieRuntime kruntime,
            Comment oldComment, Comment newComment) {
        UserTaskCommentEventImpl event = new UserTaskCommentEventImpl(instance, (HumanTaskNodeInstance) nodeInstance, kruntime, identityProvider.getName());
        event.setOldComment(oldComment);
        event.setNewComment(newComment);
        notifyAllListeners(l -> l.onUserTaskCommentChange(event));
    }

    @Override
    public void fireOnUserTaskCommentDeleted(
            KogitoProcessInstance instance,
            KogitoNodeInstance nodeInstance,
            KieRuntime kruntime,
            Comment deletedComment) {
        UserTaskCommentEventImpl event = new UserTaskCommentEventImpl(instance, (HumanTaskNodeInstance) nodeInstance, kruntime, identityProvider.getName());
        event.setOldComment(deletedComment);
        notifyAllListeners(l -> l.onUserTaskCommentDeleted(event));
    }

    @Override
    public void reset() {
        this.clear();
    }

    @Override
    public void fireOnError(KogitoProcessInstance instance, KogitoNodeInstance nodeInstance, KieRuntime kruntime, Exception exception) {
        ErrorEventImpl event = new ErrorEventImpl(instance, kruntime, nodeInstance, exception);
        notifyAllListeners(l -> l.onError(event));
    }

}
