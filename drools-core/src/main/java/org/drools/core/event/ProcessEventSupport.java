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

import java.util.Iterator;
import java.util.List;

import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.event.process.SLAViolatedEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;

public class ProcessEventSupport extends AbstractEventSupport<ProcessEventListener> {

    public void fireBeforeProcessStarted(final ProcessInstance instance, KieRuntime kruntime ) {
        final Iterator<ProcessEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final ProcessStartedEvent event = new ProcessStartedEventImpl(instance, kruntime);

            do{
                iter.next().beforeProcessStarted(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterProcessStarted(final ProcessInstance instance, KieRuntime kruntime) {
        final Iterator<ProcessEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final ProcessStartedEvent event = new ProcessStartedEventImpl(instance, kruntime);

            do {
                iter.next().afterProcessStarted(event);
            } while (iter.hasNext());
        }
    }

    public void fireBeforeProcessCompleted(final ProcessInstance instance, KieRuntime kruntime) {
        final Iterator<ProcessEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final ProcessCompletedEvent event = new ProcessCompletedEventImpl(instance, kruntime);

            do {
                iter.next().beforeProcessCompleted(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterProcessCompleted(final ProcessInstance instance, KieRuntime kruntime) {
        final Iterator<ProcessEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final ProcessCompletedEvent event = new ProcessCompletedEventImpl(instance, kruntime);

            do {
                iter.next().afterProcessCompleted(event);
            } while (iter.hasNext());
        }
    }

    public void fireBeforeNodeTriggered(final NodeInstance nodeInstance, KieRuntime kruntime) {
        final Iterator<ProcessEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final ProcessNodeTriggeredEvent event = new ProcessNodeTriggeredEventImpl(nodeInstance, kruntime);

            do {
                iter.next().beforeNodeTriggered(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterNodeTriggered(final NodeInstance nodeInstance, KieRuntime kruntime) {
        final Iterator<ProcessEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final ProcessNodeTriggeredEvent event = new ProcessNodeTriggeredEventImpl(nodeInstance, kruntime);

            do{
                iter.next().afterNodeTriggered(event);
            } while (iter.hasNext());
        }
    }

    public void fireBeforeNodeLeft(final NodeInstance nodeInstance, KieRuntime kruntime) {
        final Iterator<ProcessEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final ProcessNodeLeftEvent event = new ProcessNodeLeftEventImpl(nodeInstance, kruntime);

            do{
                iter.next().beforeNodeLeft(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterNodeLeft(final NodeInstance nodeInstance, KieRuntime kruntime) {
        final Iterator<ProcessEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final ProcessNodeLeftEvent event = new ProcessNodeLeftEventImpl(nodeInstance, kruntime);

            do{
                iter.next().afterNodeLeft(event);
            } while (iter.hasNext());
        }
    }

    public void fireBeforeVariableChanged(final String id, final String instanceId, 
            final Object oldValue, final Object newValue,
            final List<String> tags,
            final ProcessInstance processInstance, KieRuntime kruntime) {
        final Iterator<ProcessEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final ProcessVariableChangedEvent event = new ProcessVariableChangedEventImpl(
                id, instanceId, oldValue, newValue, tags, processInstance, kruntime);

            do {
                iter.next().beforeVariableChanged(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterVariableChanged(final String name, final String id, 
            final Object oldValue, final Object newValue,
            final List<String> tags,
            final ProcessInstance processInstance, KieRuntime kruntime) {
        final Iterator<ProcessEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final ProcessVariableChangedEvent event = new ProcessVariableChangedEventImpl(
                name, id, oldValue, newValue, tags, processInstance, kruntime);

            do {
                iter.next().afterVariableChanged(event);
            } while (iter.hasNext());
        }
    }

    /**
     * Do not use this constructor. It should be used just by deserialization.
     */
    public ProcessEventSupport() {
    }

    public void fireBeforeSLAViolated(final ProcessInstance instance, KieRuntime kruntime ) {
        final Iterator<ProcessEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final SLAViolatedEvent event = new SLAViolatedEventImpl(instance, kruntime);

            do{
                iter.next().beforeSLAViolated(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterSLAViolated(final ProcessInstance instance, KieRuntime kruntime) {
        final Iterator<ProcessEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final SLAViolatedEvent event = new SLAViolatedEventImpl(instance, kruntime);

            do {
                iter.next().afterSLAViolated(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireBeforeSLAViolated(final ProcessInstance instance, NodeInstance nodeInstance, KieRuntime kruntime ) {
        final Iterator<ProcessEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final SLAViolatedEvent event = new SLAViolatedEventImpl(instance, nodeInstance, kruntime);

            do{
                iter.next().beforeSLAViolated(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterSLAViolated(final ProcessInstance instance, NodeInstance nodeInstance, KieRuntime kruntime) {
        final Iterator<ProcessEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final SLAViolatedEvent event = new SLAViolatedEventImpl(instance, nodeInstance, kruntime);

            do {
                iter.next().afterSLAViolated(event);
            } while (iter.hasNext());
        }
    }

    public void reset() {
        this.clear();
    }
}
