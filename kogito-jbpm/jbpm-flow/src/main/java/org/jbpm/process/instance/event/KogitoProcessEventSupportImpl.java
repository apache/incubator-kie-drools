/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.instance.event;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import org.kie.api.event.process.MessageEvent;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.event.process.SLAViolatedEvent;
import org.kie.api.event.process.SignalEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.internal.runtime.Closeable;
import org.kie.kogito.internal.process.event.HumanTaskDeadlineEvent;
import org.kie.kogito.internal.process.event.HumanTaskDeadlineEvent.DeadlineType;
import org.kie.kogito.internal.process.event.KogitoProcessEventListener;
import org.kie.kogito.internal.process.event.KogitoProcessEventSupport;
import org.kie.kogito.internal.process.event.ProcessWorkItemTransitionEvent;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.process.workitem.HumanTaskWorkItem;
import org.kie.kogito.process.workitem.Transition;

public class KogitoProcessEventSupportImpl implements KogitoProcessEventSupport {

    private List<KogitoProcessEventListener> listeners = new CopyOnWriteArrayList();

    public KogitoProcessEventSupportImpl(List<KogitoProcessEventListener> listeners) {
        listeners.forEach(l -> addEventListener(l));
    }

    /**
     * Do not use this constructor. It should be used just by deserialization.
     */
    public KogitoProcessEventSupportImpl() {
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
        final ProcessStartedEvent event = new ProcessStartedEventImpl(instance, kruntime);
        notifyAllListeners(l -> l.beforeProcessStarted(event));
    }

    @Override
    public void fireAfterProcessStarted(final KogitoProcessInstance instance, KieRuntime kruntime) {
        final ProcessStartedEvent event = new ProcessStartedEventImpl(instance, kruntime);
        notifyAllListeners(l -> l.afterProcessStarted(event));
    }

    @Override
    public void fireBeforeProcessCompleted(final KogitoProcessInstance instance, KieRuntime kruntime) {
        final ProcessCompletedEvent event = new ProcessCompletedEventImpl(instance, kruntime);
        notifyAllListeners(l -> l.beforeProcessCompleted(event));
    }

    @Override
    public void fireAfterProcessCompleted(final KogitoProcessInstance instance, KieRuntime kruntime) {
        final ProcessCompletedEvent event = new ProcessCompletedEventImpl(instance, kruntime);
        notifyAllListeners(l -> l.afterProcessCompleted(event));
    }

    @Override
    public void fireBeforeNodeTriggered(final KogitoNodeInstance nodeInstance, KieRuntime kruntime) {
        final ProcessNodeTriggeredEvent event = new KogitoProcessNodeTriggeredEventImpl(nodeInstance, kruntime);
        notifyAllListeners(l -> l.beforeNodeTriggered(event));
    }

    @Override
    public void fireAfterNodeTriggered(final KogitoNodeInstance nodeInstance, KieRuntime kruntime) {
        final ProcessNodeTriggeredEvent event = new KogitoProcessNodeTriggeredEventImpl(nodeInstance, kruntime);
        notifyAllListeners(l -> l.afterNodeTriggered(event));
    }

    @Override
    public void fireBeforeNodeLeft(final KogitoNodeInstance nodeInstance, KieRuntime kruntime) {
        final ProcessNodeLeftEvent event = new KogitoProcessNodeLeftEventImpl(nodeInstance, kruntime);
        notifyAllListeners(l -> l.beforeNodeLeft(event));
    }

    @Override
    public void fireAfterNodeLeft(final KogitoNodeInstance nodeInstance, KieRuntime kruntime) {
        final ProcessNodeLeftEvent event = new KogitoProcessNodeLeftEventImpl(nodeInstance, kruntime);
        notifyAllListeners(l -> l.afterNodeLeft(event));
    }

    @Override
    public void fireBeforeVariableChanged(final String id, final String instanceId,
            final Object oldValue, final Object newValue,
            final List<String> tags,
            final KogitoProcessInstance processInstance, KogitoNodeInstance nodeInstance, KieRuntime kruntime) {
        final ProcessVariableChangedEvent event = new KogitoProcessVariableChangedEventImpl(
                id, instanceId, oldValue, newValue, tags, processInstance, nodeInstance, kruntime);
        notifyAllListeners(l -> l.beforeVariableChanged(event));
    }

    @Override
    public void fireAfterVariableChanged(final String name, final String id,
            final Object oldValue, final Object newValue,
            final List<String> tags,
            final KogitoProcessInstance processInstance, KogitoNodeInstance nodeInstance, KieRuntime kruntime) {
        final ProcessVariableChangedEvent event = new KogitoProcessVariableChangedEventImpl(
                name, id, oldValue, newValue, tags, processInstance, nodeInstance, kruntime);
        notifyAllListeners(l -> l.afterVariableChanged(event));
    }

    @Override
    public void fireBeforeSLAViolated(final KogitoProcessInstance instance, KieRuntime kruntime) {
        final SLAViolatedEvent event = new SLAViolatedEventImpl(instance, kruntime);
        notifyAllListeners(l -> l.beforeSLAViolated(event));
    }

    @Override
    public void fireAfterSLAViolated(final KogitoProcessInstance instance, KieRuntime kruntime) {
        final SLAViolatedEvent event = new SLAViolatedEventImpl(instance, kruntime);
        notifyAllListeners(l -> l.afterSLAViolated(event));
    }

    @Override
    public void fireBeforeSLAViolated(final KogitoProcessInstance instance, KogitoNodeInstance nodeInstance, KieRuntime kruntime) {
        final SLAViolatedEvent event = new SLAViolatedEventImpl(instance, nodeInstance, kruntime);
        notifyAllListeners(l -> l.beforeSLAViolated(event));
    }

    @Override
    public void fireAfterSLAViolated(final KogitoProcessInstance instance, KogitoNodeInstance nodeInstance, KieRuntime kruntime) {
        final SLAViolatedEvent event = new SLAViolatedEventImpl(instance, nodeInstance, kruntime);
        notifyAllListeners(l -> l.afterSLAViolated(event));
    }

    @Override
    public void fireBeforeWorkItemTransition(final KogitoProcessInstance instance, KogitoWorkItem workitem, Transition<?> transition, KieRuntime kruntime) {
        final ProcessWorkItemTransitionEvent event = new KogitoProcessWorkItemTransitionEventImpl(instance, workitem, transition, kruntime, false);
        notifyAllListeners(l -> l.beforeWorkItemTransition(event));
    }

    @Override
    public void fireAfterWorkItemTransition(final KogitoProcessInstance instance, KogitoWorkItem workitem, Transition<?> transition, KieRuntime kruntime) {
        final ProcessWorkItemTransitionEvent event = new KogitoProcessWorkItemTransitionEventImpl(instance, workitem, transition, kruntime, true);
        notifyAllListeners(l -> l.afterWorkItemTransition(event));
    }

    @Override
    public void fireOnSignal(final KogitoProcessInstance instance, KogitoNodeInstance nodeInstance, KieRuntime kruntime, String signalName, Object signalObject) {
        final SignalEvent event = new SignalEventImpl(instance, kruntime, nodeInstance, signalName, signalObject);
        notifyAllListeners(l -> l.onSignal(event));
    }

    @Override
    public void fireOnMessage(final KogitoProcessInstance instance, KogitoNodeInstance nodeInstance, KieRuntime kruntime, String messageName, Object messageObject) {
        final MessageEvent event = new MessageEventImpl(instance, kruntime, nodeInstance, messageName,
                messageObject);
        notifyAllListeners(l -> l.onMessage(event));
    }

    @Override
    public void fireOnTaskNotStartedDeadline(KogitoProcessInstance instance,
            HumanTaskWorkItem workItem,
            Map<String, Object> notification,
            KieRuntime kruntime) {
        fireTaskNotification(instance, workItem, notification, DeadlineType.Started, kruntime);
    }

    @Override
    public void fireOnTaskNotCompletedDeadline(KogitoProcessInstance instance,
            HumanTaskWorkItem workItem,
            Map<String, Object> notification,
            KieRuntime kruntime) {
        fireTaskNotification(instance, workItem, notification, DeadlineType.Completed, kruntime);
    }

    private void fireTaskNotification(KogitoProcessInstance instance,
            HumanTaskWorkItem workItem,
            Map<String, Object> notification,
            DeadlineType type,
            KieRuntime kruntime) {
        final HumanTaskDeadlineEvent event = new HumanTaskDeadlineEventImpl(instance, workItem, notification, type, kruntime);
        notifyAllListeners(l -> l.onHumanTaskDeadline(event));
    }

    @Override
    public void reset() {
        this.clear();
    }
}
