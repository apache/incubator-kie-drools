/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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

public class ProcessEventSupport extends AbstractEventSupport<ProcessEventListener> {

    public void fireBeforeProcessStarted(final ProcessInstance instance, KieRuntime kruntime ) {
        if ( hasListeners() ) {
            final ProcessStartedEvent event = new ProcessStartedEventImpl(instance, kruntime);
            notifyAllListeners( event, ( l, e ) -> l.beforeProcessStarted( e ) );
        }
    }

    public void fireAfterProcessStarted(final ProcessInstance instance, KieRuntime kruntime) {
        if ( hasListeners() ) {
            final ProcessStartedEvent event = new ProcessStartedEventImpl(instance, kruntime);
            notifyAllListeners( event, ( l, e ) -> l.afterProcessStarted( e ) );
        }
    }

    public void fireBeforeProcessCompleted(final ProcessInstance instance, KieRuntime kruntime) {
        if ( hasListeners() ) {
            final ProcessCompletedEvent event = new ProcessCompletedEventImpl(instance, kruntime);
            notifyAllListeners( event, ( l, e ) -> l.beforeProcessCompleted( e ) );
        }
    }

    public void fireAfterProcessCompleted(final ProcessInstance instance, KieRuntime kruntime) {
        if ( hasListeners() ) {
            final ProcessCompletedEvent event = new ProcessCompletedEventImpl(instance, kruntime);
            notifyAllListeners( event, ( l, e ) -> l.afterProcessCompleted( e ) );
        }
    }

    public void fireBeforeNodeTriggered(final NodeInstance nodeInstance, KieRuntime kruntime) {
        if ( hasListeners() ) {
            final ProcessNodeTriggeredEvent event = new ProcessNodeTriggeredEventImpl(nodeInstance, kruntime);
            notifyAllListeners( event, ( l, e ) -> l.beforeNodeTriggered( e ) );
        }
    }

    public void fireAfterNodeTriggered(final NodeInstance nodeInstance, KieRuntime kruntime) {
        if ( hasListeners() ) {
            final ProcessNodeTriggeredEvent event = new ProcessNodeTriggeredEventImpl(nodeInstance, kruntime);
            notifyAllListeners( event, ( l, e ) -> l.afterNodeTriggered( e ) );
        }
    }

    public void fireBeforeNodeLeft(final NodeInstance nodeInstance, KieRuntime kruntime) {
        if ( hasListeners() ) {
            final ProcessNodeLeftEvent event = new ProcessNodeLeftEventImpl(nodeInstance, kruntime);
            notifyAllListeners( event, ( l, e ) -> l.beforeNodeLeft( e ) );
        }
    }

    public void fireAfterNodeLeft(final NodeInstance nodeInstance, KieRuntime kruntime) {
        if ( hasListeners() ) {
            final ProcessNodeLeftEvent event = new ProcessNodeLeftEventImpl(nodeInstance, kruntime);
            notifyAllListeners( event, ( l, e ) -> l.afterNodeLeft( e ) );
        }
    }

    public void fireBeforeVariableChanged(final String id, final String instanceId, 
            final Object oldValue, final Object newValue,
            final List<String> tags,
            final ProcessInstance processInstance, KieRuntime kruntime) {
        if ( hasListeners() ) {
            final ProcessVariableChangedEvent event = new ProcessVariableChangedEventImpl(
                    id, instanceId, oldValue, newValue, tags, processInstance, kruntime);
            notifyAllListeners( event, ( l, e ) -> l.beforeVariableChanged( e ) );
        }
    }

    public void fireAfterVariableChanged(final String name, final String id, 
            final Object oldValue, final Object newValue,
            final List<String> tags,
            final ProcessInstance processInstance, KieRuntime kruntime) {
        if ( hasListeners() ) {
            final ProcessVariableChangedEvent event = new ProcessVariableChangedEventImpl(
                    name, id, oldValue, newValue, tags, processInstance, kruntime);
            notifyAllListeners( event, ( l, e ) -> l.afterVariableChanged( e ) );
        }
    }

    /**
     * Do not use this constructor. It should be used just by deserialization.
     */
    public ProcessEventSupport() {
    }

    public void fireBeforeSLAViolated(final ProcessInstance instance, KieRuntime kruntime ) {
        if ( hasListeners() ) {
            final SLAViolatedEvent event = new SLAViolatedEventImpl(instance, kruntime);
            notifyAllListeners( event, ( l, e ) -> l.beforeSLAViolated( e ) );
        }
    }

    public void fireAfterSLAViolated(final ProcessInstance instance, KieRuntime kruntime) {
        if ( hasListeners() ) {
            final SLAViolatedEvent event = new SLAViolatedEventImpl(instance, kruntime);
            notifyAllListeners( event, ( l, e ) -> l.afterSLAViolated( e ) );
        }
    }
    
    public void fireBeforeSLAViolated(final ProcessInstance instance, NodeInstance nodeInstance, KieRuntime kruntime ) {
        if ( hasListeners() ) {
            final SLAViolatedEvent event = new SLAViolatedEventImpl(instance, nodeInstance, kruntime);
            notifyAllListeners( event, ( l, e ) -> l.beforeSLAViolated( e ) );
        }
    }

    public void fireAfterSLAViolated(final ProcessInstance instance, NodeInstance nodeInstance, KieRuntime kruntime) {
        if ( hasListeners() ) {
            final SLAViolatedEvent event = new SLAViolatedEventImpl(instance, nodeInstance, kruntime);
            notifyAllListeners( event, ( l, e ) -> l.afterSLAViolated( e ) );
        }
    }

    public void fireOnSignal(final ProcessInstance instance,
                             NodeInstance nodeInstance,
                             KieRuntime kruntime,
                             String signalName,
                             Object signalObject) {
        if (hasListeners()) {
            final SignalEvent event = new SignalEventImpl(instance, kruntime, nodeInstance, signalName, signalObject);
            notifyAllListeners(event, ProcessEventListener::onSignal);
        }
    }

    public void fireOnMessage(final ProcessInstance instance,
                              NodeInstance nodeInstance,
                              KieRuntime kruntime,
                              String messageName,
                              Object messageObject) {
        if (hasListeners()) {
            final MessageEvent event = new MessageEventImpl(instance, kruntime, nodeInstance, messageName,
                    messageObject);
            notifyAllListeners(event, ProcessEventListener::onMessage);
        }
    }

    public void reset() {
        this.clear();
    }
}
