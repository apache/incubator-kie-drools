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

package org.drools.event;

import java.util.Iterator;

import org.drools.WorkingMemory;
import org.drools.common.InternalWorkingMemory;
import org.drools.event.rule.ActivationCancelledCause;
import org.drools.spi.Activation;
import org.drools.spi.AgendaGroup;
import org.drools.spi.RuleFlowGroup;

/**
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris </a>
 * @author <a href="mailto:stampy88@yahoo.com">dave sinclair</a>
 */
public class AgendaEventSupport extends AbstractEventSupport<AgendaEventListener> {

    public AgendaEventSupport() {
    }

    public void fireActivationCreated(final Activation activation,
                                      final WorkingMemory workingMemory) {
        final Iterator<AgendaEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final ActivationCreatedEvent event = new ActivationCreatedEvent(activation);

            do{
                iter.next().activationCreated(event, workingMemory);
            }  while (iter.hasNext());
        }
    }

    public void fireActivationCancelled(final Activation activation,
                                        final WorkingMemory workingMemory,
                                        final ActivationCancelledCause cause) {
        final Iterator<AgendaEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final ActivationCancelledEvent event = new ActivationCancelledEvent(activation, cause);

            do{
                iter.next().activationCancelled(event, workingMemory);
            }  while (iter.hasNext());
        }
    }

    public void fireBeforeActivationFired(final Activation activation,
                                          final WorkingMemory workingMemory) {
        final Iterator<AgendaEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final BeforeActivationFiredEvent event = new BeforeActivationFiredEvent(activation);

            do{
                iter.next().beforeActivationFired(event, workingMemory);
            }  while (iter.hasNext());
        }
    }

    public void fireAfterActivationFired(final Activation activation,
                                         final InternalWorkingMemory workingMemory) {
        final Iterator<AgendaEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final AfterActivationFiredEvent event = new AfterActivationFiredEvent(activation);

            do{
                iter.next().afterActivationFired(event, workingMemory);
            }  while (iter.hasNext());
        }
    }

    public void fireAgendaGroupPopped(final AgendaGroup agendaGroup,
                                      final InternalWorkingMemory workingMemory) {
        final Iterator<AgendaEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final AgendaGroupPoppedEvent event = new AgendaGroupPoppedEvent(agendaGroup);

            do{
                iter.next().agendaGroupPopped(event, workingMemory);
            }  while (iter.hasNext());
        }
    }

    public void fireAgendaGroupPushed(final AgendaGroup agendaGroup,
                                      final InternalWorkingMemory workingMemory) {
        final Iterator<AgendaEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final AgendaGroupPushedEvent event = new AgendaGroupPushedEvent(agendaGroup);

            do{
                iter.next().agendaGroupPushed(event, workingMemory);
            }  while (iter.hasNext());
        }
    }

    public void fireBeforeRuleFlowGroupActivated(
        	final RuleFlowGroup ruleFlowGroup,
        	final InternalWorkingMemory workingMemory) {
        final Iterator<AgendaEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
        	final RuleFlowGroupActivatedEvent event = new RuleFlowGroupActivatedEvent(
        			ruleFlowGroup);

        	do {
        		iter.next().beforeRuleFlowGroupActivated(event, workingMemory);
        	} while (iter.hasNext());
        }
    }

    public void fireAfterRuleFlowGroupActivated(
        	final RuleFlowGroup ruleFlowGroup,
        	final InternalWorkingMemory workingMemory) {
        final Iterator<AgendaEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
        	final RuleFlowGroupActivatedEvent event = new RuleFlowGroupActivatedEvent(
        			ruleFlowGroup);

        	do {
        		iter.next().afterRuleFlowGroupActivated(event, workingMemory);
        	} while (iter.hasNext());
        }
    }

    public void fireBeforeRuleFlowGroupDeactivated(
        	final RuleFlowGroup ruleFlowGroup,
        	final InternalWorkingMemory workingMemory) {
        final Iterator<AgendaEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
        	final RuleFlowGroupDeactivatedEvent event = new RuleFlowGroupDeactivatedEvent(
        			ruleFlowGroup);

        	do {
        		iter.next()
        				.beforeRuleFlowGroupDeactivated(event, workingMemory);
        	} while (iter.hasNext());
        }
    }

    public void fireAfterRuleFlowGroupDeactivated(
        	final RuleFlowGroup ruleFlowGroup,
        	final InternalWorkingMemory workingMemory) {
        final Iterator<AgendaEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
        	final RuleFlowGroupDeactivatedEvent event = new RuleFlowGroupDeactivatedEvent(
        			ruleFlowGroup);

        	do {
        		iter.next().afterRuleFlowGroupDeactivated(event, workingMemory);
        	} while (iter.hasNext());
        }
    }

    public void reset() {
        this.clear();
    }
}
