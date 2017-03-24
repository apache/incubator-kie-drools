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

import org.drools.core.WorkingMemory;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.event.rule.impl.ActivationCancelledEventImpl;
import org.drools.core.event.rule.impl.ActivationCreatedEventImpl;
import org.drools.core.event.rule.impl.AfterActivationFiredEventImpl;
import org.drools.core.event.rule.impl.AgendaGroupPoppedEventImpl;
import org.drools.core.event.rule.impl.AgendaGroupPushedEventImpl;
import org.drools.core.event.rule.impl.BeforeActivationFiredEventImpl;
import org.drools.core.event.rule.impl.RuleFlowGroupActivatedEventImpl;
import org.drools.core.event.rule.impl.RuleFlowGroupDeactivatedEventImpl;
import org.drools.core.spi.Activation;
import org.drools.core.spi.AgendaGroup;
import org.drools.core.spi.RuleFlowGroup;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledCause;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;

public class AgendaEventSupport extends AbstractEventSupport<AgendaEventListener> {

    public AgendaEventSupport() { }

    private InternalKnowledgeRuntime getKRuntime(WorkingMemory workingMemory) {
        return ((InternalWorkingMemory) workingMemory).getKnowledgeRuntime();
    }

    public void fireActivationCreated(final Activation activation,
                                      final WorkingMemory workingMemory) {
        Iterator<AgendaEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            MatchCreatedEvent event = new ActivationCreatedEventImpl(activation, getKRuntime(workingMemory));

            do{
                iter.next().matchCreated(event);
            }  while (iter.hasNext());
        }
    }

    public void fireActivationCancelled(final Activation activation,
                                        final WorkingMemory workingMemory,
                                        final MatchCancelledCause cause) {
        Iterator<AgendaEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            MatchCancelledEvent event = new ActivationCancelledEventImpl(activation, getKRuntime(workingMemory), cause);

            do{
                iter.next().matchCancelled(event);
            }  while (iter.hasNext());
        }
    }

    public void fireBeforeActivationFired(final Activation activation,
                                          final WorkingMemory workingMemory) {
        Iterator<AgendaEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            BeforeMatchFiredEvent event = new BeforeActivationFiredEventImpl(activation, getKRuntime(workingMemory));

            do{
                iter.next().beforeMatchFired(event);
            }  while (iter.hasNext());
        }
    }

    public void fireAfterActivationFired(final Activation activation,
                                         final InternalWorkingMemory workingMemory) {
        Iterator<AgendaEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            AfterMatchFiredEvent event = new AfterActivationFiredEventImpl(activation, getKRuntime(workingMemory));

            do{
                iter.next().afterMatchFired(event);
            }  while (iter.hasNext());
        }
    }

    public void fireAgendaGroupPopped(final AgendaGroup agendaGroup,
                                      final InternalWorkingMemory workingMemory) {
        Iterator<AgendaEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            AgendaGroupPoppedEventImpl event = new AgendaGroupPoppedEventImpl(agendaGroup, getKRuntime(workingMemory));

            do{
                iter.next().agendaGroupPopped(event);
            }  while (iter.hasNext());
        }
    }

    public void fireAgendaGroupPushed(final AgendaGroup agendaGroup,
                                      final InternalWorkingMemory workingMemory) {
        Iterator<AgendaEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            AgendaGroupPushedEventImpl event = new AgendaGroupPushedEventImpl(agendaGroup, getKRuntime(workingMemory));

            do{
                iter.next().agendaGroupPushed(event);
            }  while (iter.hasNext());
        }
    }

    public void fireBeforeRuleFlowGroupActivated(
            final RuleFlowGroup ruleFlowGroup,
            final InternalWorkingMemory workingMemory) {
        Iterator<AgendaEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            RuleFlowGroupActivatedEventImpl event = new RuleFlowGroupActivatedEventImpl(ruleFlowGroup, getKRuntime(workingMemory));

            do {
                iter.next().beforeRuleFlowGroupActivated(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterRuleFlowGroupActivated(
            final RuleFlowGroup ruleFlowGroup,
            final InternalWorkingMemory workingMemory) {
        Iterator<AgendaEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            RuleFlowGroupActivatedEventImpl event = new RuleFlowGroupActivatedEventImpl(ruleFlowGroup, getKRuntime(workingMemory));

            do {
                iter.next().afterRuleFlowGroupActivated(event);
            } while (iter.hasNext());
        }
    }

    public void fireBeforeRuleFlowGroupDeactivated(
            final RuleFlowGroup ruleFlowGroup,
            final InternalWorkingMemory workingMemory) {
        Iterator<AgendaEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            RuleFlowGroupDeactivatedEventImpl event = new RuleFlowGroupDeactivatedEventImpl(ruleFlowGroup, getKRuntime(workingMemory));

            do {
                iter.next().beforeRuleFlowGroupDeactivated(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterRuleFlowGroupDeactivated(
            final RuleFlowGroup ruleFlowGroup,
            final InternalWorkingMemory workingMemory) {
        Iterator<AgendaEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
             RuleFlowGroupDeactivatedEventImpl event = new RuleFlowGroupDeactivatedEventImpl(ruleFlowGroup, getKRuntime(workingMemory));

            do {
                iter.next().afterRuleFlowGroupDeactivated(event);
            } while (iter.hasNext());
        }
    }
}
