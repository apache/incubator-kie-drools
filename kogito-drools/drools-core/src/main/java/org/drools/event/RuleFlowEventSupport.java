package org.drools.event;

/*
 * Copyright 2005 JBoss Inc
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

import java.util.Iterator;

import org.drools.WorkingMemory;
import org.drools.common.InternalWorkingMemory;
import org.drools.process.instance.ProcessInstance;
import org.drools.runtime.process.NodeInstance;
import org.drools.spi.RuleFlowGroup;

/**
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 * @author <a href="mailto:stampy88@yahoo.com">dave sinclair</a>
 */
public class RuleFlowEventSupport extends AbstractEventSupport<RuleFlowEventListener> {

    // TODO separate out process level stuff

    public void fireBeforeRuleFlowProcessStarted(final ProcessInstance instance,
                                                 final InternalWorkingMemory workingMemory) {
        final Iterator<RuleFlowEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final RuleFlowStartedEvent event = new RuleFlowStartedEvent(instance);

            do{
                iter.next().beforeRuleFlowStarted(event, workingMemory);
            } while (iter.hasNext());
        }
    }

    public void fireAfterRuleFlowProcessStarted(final ProcessInstance instance,
                                                final InternalWorkingMemory workingMemory) {
        final Iterator<RuleFlowEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final RuleFlowStartedEvent event = new RuleFlowStartedEvent(instance);

            do {
                iter.next().afterRuleFlowStarted(event, workingMemory);
            } while (iter.hasNext());
        }
    }

    public void fireBeforeRuleFlowProcessCompleted(final ProcessInstance instance,
                                                   final InternalWorkingMemory workingMemory) {
        final Iterator<RuleFlowEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final RuleFlowCompletedEvent event = new RuleFlowCompletedEvent(instance);

            do {
                iter.next().beforeRuleFlowCompleted(event, workingMemory);
            } while (iter.hasNext());
        }
    }

    public void fireAfterRuleFlowProcessCompleted(final ProcessInstance instance,
                                                  final InternalWorkingMemory workingMemory) {
        final Iterator<RuleFlowEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final RuleFlowCompletedEvent event = new RuleFlowCompletedEvent(instance);

            do {
                iter.next().afterRuleFlowCompleted(event, workingMemory);
            } while (iter.hasNext());
        }
    }

    public void fireBeforeRuleFlowGroupActivated(final RuleFlowGroup ruleFlowGroup,
                                                 final InternalWorkingMemory workingMemory) {
        final Iterator<RuleFlowEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final RuleFlowGroupActivatedEvent event = new RuleFlowGroupActivatedEvent(ruleFlowGroup);

            do {
                iter.next().beforeRuleFlowGroupActivated(event, workingMemory);
            } while (iter.hasNext());
        }
    }

    public void fireAfterRuleFlowGroupActivated(final RuleFlowGroup ruleFlowGroup,
                                                final InternalWorkingMemory workingMemory) {
        final Iterator<RuleFlowEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final RuleFlowGroupActivatedEvent event = new RuleFlowGroupActivatedEvent(ruleFlowGroup);

            do{
                iter.next().afterRuleFlowGroupActivated(event, workingMemory);
            } while (iter.hasNext());
        }
    }

    public void fireBeforeRuleFlowGroupDeactivated(final RuleFlowGroup ruleFlowGroup,
                                                   final InternalWorkingMemory workingMemory) {
        final Iterator<RuleFlowEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final RuleFlowGroupDeactivatedEvent event = new RuleFlowGroupDeactivatedEvent(ruleFlowGroup);

            do{
                iter.next().beforeRuleFlowGroupDeactivated(event, workingMemory);
            } while (iter.hasNext());
        }
    }

    public void fireAfterRuleFlowGroupDeactivated(final RuleFlowGroup ruleFlowGroup,
                                                  final InternalWorkingMemory workingMemory) {
        final Iterator<RuleFlowEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final RuleFlowGroupDeactivatedEvent event = new RuleFlowGroupDeactivatedEvent(ruleFlowGroup);

            do {
                iter.next().afterRuleFlowGroupDeactivated(event, workingMemory);
            } while (iter.hasNext());
        }
    }

    public void fireBeforeRuleFlowNodeTriggered(final NodeInstance ruleFlowNodeInstance,
                                                final InternalWorkingMemory workingMemory) {
        final Iterator<RuleFlowEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final RuleFlowNodeTriggeredEvent event = new RuleFlowNodeTriggeredEvent(ruleFlowNodeInstance);

            do {
                iter.next().beforeRuleFlowNodeTriggered(event, workingMemory);
            } while (iter.hasNext());
        }
    }

    public void fireAfterRuleFlowNodeTriggered(final NodeInstance ruleFlowNodeInstance,
                                               final InternalWorkingMemory workingMemory) {
        final Iterator<RuleFlowEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final RuleFlowNodeTriggeredEvent event = new RuleFlowNodeTriggeredEvent(ruleFlowNodeInstance);

            do{
                iter.next().afterRuleFlowNodeTriggered(event, workingMemory);
            } while (iter.hasNext());
        }
    }

    public void fireBeforeRuleFlowNodeLeft(final NodeInstance ruleFlowNodeInstance,
                                           final InternalWorkingMemory workingMemory) {
        final Iterator<RuleFlowEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final RuleFlowNodeTriggeredEvent event = new RuleFlowNodeTriggeredEvent(ruleFlowNodeInstance);

            do{
                iter.next().beforeRuleFlowNodeLeft(event, workingMemory);
            } while (iter.hasNext());
        }
    }

    public void fireAfterRuleFlowNodeLeft(final NodeInstance ruleFlowNodeInstance,
                                          final InternalWorkingMemory workingMemory) {
        final Iterator<RuleFlowEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final RuleFlowNodeTriggeredEvent event = new RuleFlowNodeTriggeredEvent(ruleFlowNodeInstance);

            do{
                iter.next().afterRuleFlowNodeLeft(event, workingMemory);
            } while (iter.hasNext());
        }
    }

    public void fireBeforeVariableChange(final ProcessInstance instance,
                                         final String name,
                                         final Object value,
                                         WorkingMemory workingMemory) {
        final Iterator<RuleFlowEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final RuleFlowVariableChangeEvent event = new RuleFlowVariableChangeEvent(instance, name, value);

            do{
                RuleFlowEventListener listener = iter.next();
                if (listener instanceof RuleFlowEventListenerExtension) {
                    ((RuleFlowEventListenerExtension) listener).beforeVariableChange(event, workingMemory);
                }
            } while (iter.hasNext());
        }
    }

    public void fireAfterVariableChange(final ProcessInstance instance,
                                        final String name,
                                        final Object value,
                                        WorkingMemory workingMemory) {
        final Iterator<RuleFlowEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final RuleFlowVariableChangeEvent event = new RuleFlowVariableChangeEvent(instance, name, value);

            do{
                RuleFlowEventListener listener = iter.next();
                if (listener instanceof RuleFlowEventListenerExtension) {
                    ((RuleFlowEventListenerExtension) listener).afterVariableChange(event, workingMemory);
                }
            } while (iter.hasNext());
        }
    }

    public void reset() {
        this.clear();
    }
}
