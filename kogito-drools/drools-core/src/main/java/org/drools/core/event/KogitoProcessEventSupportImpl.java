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
package org.drools.core.event;

import java.util.List;

import org.kie.api.event.process.MessageEvent;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.event.process.SLAViolatedEvent;
import org.kie.api.event.process.SignalEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.internal.process.event.ProcessWorkItemTransitionEvent;
import org.kie.kogito.internal.process.event.KogitoProcessEventListener;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.process.workitem.Transition;
import org.kie.kogito.uow.UnitOfWorkManager;
import org.kie.kogito.uow.WorkUnit;

import org.kie.kogito.internal.process.event.KogitoProcessEventSupport;

public class KogitoProcessEventSupportImpl extends AbstractEventSupport<KogitoProcessEventListener> implements KogitoProcessEventSupport {

    private UnitOfWorkManager unitOfWorkManager;

    public KogitoProcessEventSupportImpl(UnitOfWorkManager unitOfWorkManager) {
        this.unitOfWorkManager = unitOfWorkManager;
    }

    /**
     * Do not use this constructor. It should be used just by deserialization.
     */
    public KogitoProcessEventSupportImpl() {
    }

    @Override
    public void fireBeforeProcessStarted(final ProcessInstance instance, KieRuntime kruntime ) {
        final ProcessStartedEvent event = new ProcessStartedEventImpl(instance, kruntime);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
            if ( hasListeners() ) {
                notifyAllListeners( event, ( l, e1 ) -> l.beforeProcessStarted( e1 ) );
            }
        }));
    }

    @Override
    public void fireAfterProcessStarted(final ProcessInstance instance, KieRuntime kruntime) {
        final ProcessStartedEvent event = new ProcessStartedEventImpl(instance, kruntime);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
            if ( hasListeners() ) {
                notifyAllListeners( event, ( l, e1 ) -> l.afterProcessStarted( e1 ) );
            }
        }));
    }

    @Override
    public void fireBeforeProcessCompleted(final ProcessInstance instance, KieRuntime kruntime) {
        final ProcessCompletedEvent event = new ProcessCompletedEventImpl(instance, kruntime);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
            if ( hasListeners() ) {
                notifyAllListeners( event, ( l, e1 ) -> l.beforeProcessCompleted( e1 ) );
            }
        }));
    }

    @Override
    public void fireAfterProcessCompleted(final ProcessInstance instance, KieRuntime kruntime) {
        final ProcessCompletedEvent event = new ProcessCompletedEventImpl(instance, kruntime);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
            if ( hasListeners() ) {
                notifyAllListeners( event, ( l, e1 ) -> l.afterProcessCompleted( e1 ) );
            }
        }));
    }

    @Override
    public void fireBeforeNodeTriggered(final NodeInstance nodeInstance, KieRuntime kruntime) {
        final ProcessNodeTriggeredEvent event = new KogitoProcessNodeTriggeredEventImpl(nodeInstance, kruntime);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
            if ( hasListeners() ) {
                notifyAllListeners( event, ( l, e1 ) -> l.beforeNodeTriggered( e1 ) );
            }
        }));
    }

    @Override
    public void fireAfterNodeTriggered(final NodeInstance nodeInstance, KieRuntime kruntime) {
        final ProcessNodeTriggeredEvent event = new KogitoProcessNodeTriggeredEventImpl(nodeInstance, kruntime);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
            if ( hasListeners() ) {
                notifyAllListeners( event, ( l, e1 ) -> l.afterNodeTriggered( e1 ) );
            }
        }));
    }

    @Override
    public void fireBeforeNodeLeft(final NodeInstance nodeInstance, KieRuntime kruntime) {
        final ProcessNodeLeftEvent event = new KogitoProcessNodeLeftEventImpl(nodeInstance, kruntime);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
            if ( hasListeners() ) {
                notifyAllListeners( event, ( l, e1 ) -> l.beforeNodeLeft( e1 ) );
            }
        }));
    }

    @Override
    public void fireAfterNodeLeft(final NodeInstance nodeInstance, KieRuntime kruntime) {
        final ProcessNodeLeftEvent event = new KogitoProcessNodeLeftEventImpl(nodeInstance, kruntime);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
            if ( hasListeners() ) {
                notifyAllListeners( event, ( l, e1 ) -> l.afterNodeLeft( e1 ) );
            }
        }));
    }

    @Override
    public void fireBeforeVariableChanged( final String id, final String instanceId,
                                           final Object oldValue, final Object newValue,
                                           final List<String> tags,
                                           final ProcessInstance processInstance, KogitoNodeInstance nodeInstance, KieRuntime kruntime) {
        final ProcessVariableChangedEvent event = new KogitoProcessVariableChangedEventImpl(
                id, instanceId, oldValue, newValue, tags, processInstance, nodeInstance, kruntime);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
            if ( hasListeners() ) {
                notifyAllListeners( event, ( l, e1 ) -> l.beforeVariableChanged( e1 ) );
            }
        }));
    }

    @Override
    public void fireAfterVariableChanged(final String name, final String id,
                                         final Object oldValue, final Object newValue,
                                         final List<String> tags,
                                         final ProcessInstance processInstance, KogitoNodeInstance nodeInstance, KieRuntime kruntime) {
        final ProcessVariableChangedEvent event = new KogitoProcessVariableChangedEventImpl(
                name, id, oldValue, newValue, tags, processInstance, nodeInstance, kruntime);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
            if ( hasListeners() ) {
                notifyAllListeners( event, ( l, e1 ) -> l.afterVariableChanged( e1 ) );
            }
        }));
    }

    @Override
    public void fireBeforeSLAViolated(final ProcessInstance instance, KieRuntime kruntime ) {
        final SLAViolatedEvent event = new SLAViolatedEventImpl(instance, kruntime);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
            if ( hasListeners() ) {
                notifyAllListeners( event, ( l, e1 ) -> l.beforeSLAViolated( e1 ) );
            }
        }));
    }

    @Override
    public void fireAfterSLAViolated(final ProcessInstance instance, KieRuntime kruntime) {
        final SLAViolatedEvent event = new SLAViolatedEventImpl(instance, kruntime);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
            if ( hasListeners() ) {
                notifyAllListeners( event, ( l, e1 ) -> l.afterSLAViolated( e1 ) );
            }
        }));
    }

    @Override
    public void fireBeforeSLAViolated(final ProcessInstance instance, NodeInstance nodeInstance, KieRuntime kruntime ) {
        final SLAViolatedEvent event = new SLAViolatedEventImpl(instance, nodeInstance, kruntime);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
            if ( hasListeners() ) {
                notifyAllListeners( event, ( l, e1 ) -> l.beforeSLAViolated( e1 ) );
            }
        }));
    }

    @Override
    public void fireAfterSLAViolated(final ProcessInstance instance, NodeInstance nodeInstance, KieRuntime kruntime) {
        final SLAViolatedEvent event = new SLAViolatedEventImpl(instance, nodeInstance, kruntime);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
            if ( hasListeners() ) {
                notifyAllListeners( event, ( l, e1 ) -> l.afterSLAViolated( e1 ) );
            }
        }));
    }

    @Override
    public void fireBeforeWorkItemTransition( final ProcessInstance instance, KogitoWorkItem workitem, Transition<?> transition, KieRuntime kruntime ) {
        final ProcessWorkItemTransitionEvent event = new KogitoProcessWorkItemTransitionEventImpl(instance, workitem, transition, kruntime, false);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
            if ( hasListeners() ) {
                notifyAllListeners( event, ( l, e1 ) -> l.beforeWorkItemTransition( e1 ) );
            }
        }));
    }

    @Override
    public void fireAfterWorkItemTransition(final ProcessInstance instance, KogitoWorkItem workitem, Transition<?> transition, KieRuntime kruntime) {
            final ProcessWorkItemTransitionEvent event = new KogitoProcessWorkItemTransitionEventImpl(instance, workitem, transition, kruntime, true);
            unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
                if ( hasListeners() ) {
                    notifyAllListeners( event, ( l, e1 ) -> l.afterWorkItemTransition( e1 ) );
                }
            }));
    }

    @Override
    public void fireOnSignal(final ProcessInstance instance, NodeInstance nodeInstance, KieRuntime kruntime, String signalName, Object signalObject) {
        if (hasListeners()) {
            final SignalEvent event = new SignalEventImpl(instance, kruntime, nodeInstance, signalName, signalObject);
            notifyAllListeners(event, ProcessEventListener::onSignal);
        }
    }

    @Override
    public void fireOnMessage(final ProcessInstance instance, NodeInstance nodeInstance, KieRuntime kruntime, String messageName, Object messageObject) {
        if (hasListeners()) {
            final MessageEvent event = new MessageEventImpl(instance, kruntime, nodeInstance, messageName,
                    messageObject);
            notifyAllListeners(event, ProcessEventListener::onMessage);
        }
    }

    @Override
    public void reset() {
        this.clear();
    }
}
