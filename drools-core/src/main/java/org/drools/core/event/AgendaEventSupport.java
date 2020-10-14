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

public class AgendaEventSupport extends AbstractEventSupport<AgendaEventListener> {

    public AgendaEventSupport() { }

    private InternalKnowledgeRuntime getKRuntime(WorkingMemory workingMemory) {
        return ((InternalWorkingMemory) workingMemory).getKnowledgeRuntime();
    }

    public void fireActivationCreated(final Activation activation,
                                      final WorkingMemory workingMemory) {
        if ( hasListeners() ) {
            ActivationCreatedEventImpl event = new ActivationCreatedEventImpl( activation, getKRuntime( workingMemory ) );
            notifyAllListeners( event, ( l, e ) -> l.matchCreated( e ) );
        }
    }

    public void fireActivationCancelled(final Activation activation,
                                        final WorkingMemory workingMemory,
                                        final MatchCancelledCause cause) {
        if ( hasListeners() ) {
            ActivationCancelledEventImpl event = new ActivationCancelledEventImpl( activation, getKRuntime( workingMemory ), cause );
            notifyAllListeners( event, ( l, e ) -> l.matchCancelled( e ) );
        }
    }

    public BeforeMatchFiredEvent fireBeforeActivationFired(final Activation activation,
                                                           final WorkingMemory workingMemory) {
        if ( hasListeners() ) {
            BeforeMatchFiredEvent event = new BeforeActivationFiredEventImpl(activation, getKRuntime(workingMemory));
            notifyAllListeners( event, ( l, e ) -> l.beforeMatchFired( e ) );
            return event;
        }
        return null;
    }

    public void fireAfterActivationFired(final Activation activation,
                                         final InternalWorkingMemory workingMemory, BeforeMatchFiredEvent beforeMatchFiredEvent) {
        if ( hasListeners() ) {
            AfterMatchFiredEvent event = new AfterActivationFiredEventImpl( activation, getKRuntime( workingMemory ), beforeMatchFiredEvent );
            notifyAllListeners( event, ( l, e ) -> l.afterMatchFired( e ) );
        }
    }

    public void fireAgendaGroupPopped(final AgendaGroup agendaGroup,
                                      final InternalWorkingMemory workingMemory) {
        if ( hasListeners() ) {
            AgendaGroupPoppedEventImpl event = new AgendaGroupPoppedEventImpl( agendaGroup, getKRuntime( workingMemory ) );
            notifyAllListeners( event, ( l, e ) -> l.agendaGroupPopped( e ) );
        }
    }

    public void fireAgendaGroupPushed(final AgendaGroup agendaGroup,
                                      final InternalWorkingMemory workingMemory) {
        if ( hasListeners() ) {
            AgendaGroupPushedEventImpl event = new AgendaGroupPushedEventImpl( agendaGroup, getKRuntime( workingMemory ) );
            notifyAllListeners( event, ( l, e ) -> l.agendaGroupPushed( e ) );
        }
    }

    public void fireBeforeRuleFlowGroupActivated(
            final RuleFlowGroup ruleFlowGroup,
            final InternalWorkingMemory workingMemory) {
        if ( hasListeners() ) {
            RuleFlowGroupActivatedEventImpl event = new RuleFlowGroupActivatedEventImpl( ruleFlowGroup, getKRuntime( workingMemory ) );
            notifyAllListeners( event, ( l, e ) -> l.beforeRuleFlowGroupActivated( e ) );
        }
    }

    public void fireAfterRuleFlowGroupActivated(
            final RuleFlowGroup ruleFlowGroup,
            final InternalWorkingMemory workingMemory) {
        if ( hasListeners() ) {
            RuleFlowGroupActivatedEventImpl event = new RuleFlowGroupActivatedEventImpl( ruleFlowGroup, getKRuntime( workingMemory ) );
            notifyAllListeners( event, ( l, e ) -> l.afterRuleFlowGroupActivated( e ) );
        }
    }

    public void fireBeforeRuleFlowGroupDeactivated(
            final RuleFlowGroup ruleFlowGroup,
            final InternalWorkingMemory workingMemory) {
        if ( hasListeners() ) {
            RuleFlowGroupDeactivatedEventImpl event = new RuleFlowGroupDeactivatedEventImpl( ruleFlowGroup, getKRuntime( workingMemory ) );
            notifyAllListeners( event, ( l, e ) -> l.beforeRuleFlowGroupDeactivated( e ) );
        }
    }

    public void fireAfterRuleFlowGroupDeactivated(
            final RuleFlowGroup ruleFlowGroup,
            final InternalWorkingMemory workingMemory) {
        if ( hasListeners() ) {
            RuleFlowGroupDeactivatedEventImpl event = new RuleFlowGroupDeactivatedEventImpl( ruleFlowGroup, getKRuntime( workingMemory ) );
            notifyAllListeners( event, ( l, e ) -> l.afterRuleFlowGroupDeactivated( e ) );
        }
    }
}
