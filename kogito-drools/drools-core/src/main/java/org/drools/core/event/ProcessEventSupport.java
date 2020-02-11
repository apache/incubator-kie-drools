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
import org.kie.api.event.process.ProcessWorkItemTransitionEvent;
import org.kie.api.event.process.SLAViolatedEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.kogito.process.workitem.Transition;
import org.kie.kogito.uow.UnitOfWorkManager;
import org.kie.kogito.uow.WorkUnit;

public class ProcessEventSupport extends AbstractEventSupport<ProcessEventListener> {

    private UnitOfWorkManager unitOfWorkManager;

    public ProcessEventSupport(UnitOfWorkManager unitOfWorkManager) {
        this.unitOfWorkManager = unitOfWorkManager;
    }

    /**
     * Do not use this constructor. It should be used just by deserialization.
     */
    public ProcessEventSupport() {
    }

    public void fireBeforeProcessStarted(final ProcessInstance instance, KieRuntime kruntime ) {
        final Iterator<ProcessEventListener> iter = getEventListenersIterator();

        final ProcessStartedEvent event = new ProcessStartedEventImpl(instance, kruntime);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
            if (iter.hasNext()) {
                do{
                    iter.next().beforeProcessStarted(e);
                } while (iter.hasNext());        
            }
        }));
    }

    public void fireAfterProcessStarted(final ProcessInstance instance, KieRuntime kruntime) {
        final Iterator<ProcessEventListener> iter = getEventListenersIterator();

        final ProcessStartedEvent event = new ProcessStartedEventImpl(instance, kruntime);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
            if (iter.hasNext()) {            
                do {
                    iter.next().afterProcessStarted(e);
                } while (iter.hasNext());            
            }
        }));
    }

    public void fireBeforeProcessCompleted(final ProcessInstance instance, KieRuntime kruntime) {
        final Iterator<ProcessEventListener> iter = getEventListenersIterator();

        final ProcessCompletedEvent event = new ProcessCompletedEventImpl(instance, kruntime);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
            if (iter.hasNext()) {            
                do {
                    iter.next().beforeProcessCompleted(e);
                } while (iter.hasNext());            
            }
        }));
    }

    public void fireAfterProcessCompleted(final ProcessInstance instance, KieRuntime kruntime) {
        final Iterator<ProcessEventListener> iter = getEventListenersIterator();

        final ProcessCompletedEvent event = new ProcessCompletedEventImpl(instance, kruntime);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
            if (iter.hasNext()) {
            
                do {
                    iter.next().afterProcessCompleted(e);
                } while (iter.hasNext());            
            }
        }));
    }

    public void fireBeforeNodeTriggered(final NodeInstance nodeInstance, KieRuntime kruntime) {
        final Iterator<ProcessEventListener> iter = getEventListenersIterator();

        final ProcessNodeTriggeredEvent event = new ProcessNodeTriggeredEventImpl(nodeInstance, kruntime);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
            if (iter.hasNext()) {            
            
                do {
                    iter.next().beforeNodeTriggered(e);
                } while (iter.hasNext());            
            }
        }));
    }

    public void fireAfterNodeTriggered(final NodeInstance nodeInstance, KieRuntime kruntime) {
        final Iterator<ProcessEventListener> iter = getEventListenersIterator();

        final ProcessNodeTriggeredEvent event = new ProcessNodeTriggeredEventImpl(nodeInstance, kruntime);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
            if (iter.hasNext()) {
            
                do{
                    iter.next().afterNodeTriggered(e);
                } while (iter.hasNext());            
            }
        }));
    }

    public void fireBeforeNodeLeft(final NodeInstance nodeInstance, KieRuntime kruntime) {
        final Iterator<ProcessEventListener> iter = getEventListenersIterator();
        
        final ProcessNodeLeftEvent event = new ProcessNodeLeftEventImpl(nodeInstance, kruntime);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
            if (iter.hasNext()) {
            
                do{
                    iter.next().beforeNodeLeft(e);
                } while (iter.hasNext());            
            }
        }));
    }

    public void fireAfterNodeLeft(final NodeInstance nodeInstance, KieRuntime kruntime) {
        final Iterator<ProcessEventListener> iter = getEventListenersIterator();

        final ProcessNodeLeftEvent event = new ProcessNodeLeftEventImpl(nodeInstance, kruntime);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
            if (iter.hasNext()) {            
            
                do{
                    iter.next().afterNodeLeft(e);
                } while (iter.hasNext());            
            }
        }));
    }

    public void fireBeforeVariableChanged(final String id, final String instanceId, 
            final Object oldValue, final Object newValue,
            final List<String> tags,
            final ProcessInstance processInstance, KieRuntime kruntime) {
        final Iterator<ProcessEventListener> iter = getEventListenersIterator();
        
        final ProcessVariableChangedEvent event = new ProcessVariableChangedEventImpl(
                                                                                      id, instanceId, oldValue, newValue, tags, processInstance, kruntime);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
            if (iter.hasNext()) {            
            
                do {
                    iter.next().beforeVariableChanged(e);
                } while (iter.hasNext());            
            }
        }));
    }

    public void fireAfterVariableChanged(final String name, final String id, 
            final Object oldValue, final Object newValue,
            final List<String> tags,
            final ProcessInstance processInstance, KieRuntime kruntime) {
        final Iterator<ProcessEventListener> iter = getEventListenersIterator();
        final ProcessVariableChangedEvent event = new ProcessVariableChangedEventImpl(
                                                                                      name, id, oldValue, newValue, tags, processInstance, kruntime);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
            if (iter.hasNext()) {
                      
                do {
                    iter.next().afterVariableChanged(e);
                } while (iter.hasNext());            
            }
        }));
    }
    
    public void fireBeforeSLAViolated(final ProcessInstance instance, KieRuntime kruntime ) {
        final Iterator<ProcessEventListener> iter = getEventListenersIterator();

        final SLAViolatedEvent event = new SLAViolatedEventImpl(instance, kruntime);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
            if (iter.hasNext()) {            
            
                do{
                    iter.next().beforeSLAViolated(e);
                } while (iter.hasNext());            
            }
        }));
    }

    public void fireAfterSLAViolated(final ProcessInstance instance, KieRuntime kruntime) {
        final Iterator<ProcessEventListener> iter = getEventListenersIterator();

        final SLAViolatedEvent event = new SLAViolatedEventImpl(instance, kruntime);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
            if (iter.hasNext()) {
            
                do {
                    iter.next().afterSLAViolated(e);
                } while (iter.hasNext());            
            }
        }));
    }
    
    public void fireBeforeSLAViolated(final ProcessInstance instance, NodeInstance nodeInstance, KieRuntime kruntime ) {
        final Iterator<ProcessEventListener> iter = getEventListenersIterator();

        final SLAViolatedEvent event = new SLAViolatedEventImpl(instance, nodeInstance, kruntime);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
            if (iter.hasNext()) {            
                do{
                    iter.next().beforeSLAViolated(e);
                } while (iter.hasNext());
            }
        }));
    }

    public void fireAfterSLAViolated(final ProcessInstance instance, NodeInstance nodeInstance, KieRuntime kruntime) {
        final Iterator<ProcessEventListener> iter = getEventListenersIterator();
        final SLAViolatedEvent event = new SLAViolatedEventImpl(instance, nodeInstance, kruntime);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, e -> {
            if (iter.hasNext()) {           
            
                do {
                    iter.next().afterSLAViolated(e);
                } while (iter.hasNext());            
            }
        }));
    }
    
    public void fireBeforeWorkItemTransition(final ProcessInstance instance, WorkItem workitem, Transition<?> transition, KieRuntime kruntime ) {
        final Iterator<ProcessEventListener> iter = getEventListenersIterator();

        final ProcessWorkItemTransitionEvent event = new ProcessWorkItemTransitionEventImpl(instance, workitem, transition, kruntime, false);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, (e) -> {
            if (iter.hasNext()) {
                do{
                    iter.next().beforeWorkItemTransition(e);
                } while (iter.hasNext());        
            }
        }));
    }

    public void fireAfterWorkItemTransition(final ProcessInstance instance, WorkItem workitem, Transition<?> transition, KieRuntime kruntime) {
        final Iterator<ProcessEventListener> iter = getEventListenersIterator();

        final ProcessWorkItemTransitionEvent event = new ProcessWorkItemTransitionEventImpl(instance, workitem, transition, kruntime, true);
        unitOfWorkManager.currentUnitOfWork().intercept(WorkUnit.create(event, (e) -> {
            if (iter.hasNext()) {            
                do {
                    iter.next().afterWorkItemTransition(e);
                } while (iter.hasNext());            
            }
        }));
    }

    public void reset() {
        this.clear();
    }
}
