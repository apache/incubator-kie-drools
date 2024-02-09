/**
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
package org.drools.core.event;

import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.RuleFlowGroup;
import org.drools.core.event.rule.impl.ActivationCancelledEventImpl;
import org.drools.core.event.rule.impl.ActivationCreatedEventImpl;
import org.drools.core.event.rule.impl.AfterActivationFiredEventImpl;
import org.drools.core.event.rule.impl.AgendaGroupPoppedEventImpl;
import org.drools.core.event.rule.impl.AgendaGroupPushedEventImpl;
import org.drools.core.event.rule.impl.BeforeActivationFiredEventImpl;
import org.drools.core.event.rule.impl.RuleFlowGroupActivatedEventImpl;
import org.drools.core.event.rule.impl.RuleFlowGroupDeactivatedEventImpl;
import org.drools.core.rule.consequence.InternalMatch;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledCause;

public class AgendaEventSupport extends AbstractEventSupport<AgendaEventListener> {

    public AgendaEventSupport() { }

    private InternalKnowledgeRuntime getKRuntime(ReteEvaluator reteEvaluator) {
        return reteEvaluator instanceof InternalWorkingMemory ? ((InternalWorkingMemory) reteEvaluator).getKnowledgeRuntime() : null;
    }

    public void fireActivationCreated(final InternalMatch internalMatch,
                                      final ReteEvaluator reteEvaluator) {
        if ( hasListeners() ) {
            ActivationCreatedEventImpl event = new ActivationCreatedEventImpl(internalMatch, getKRuntime(reteEvaluator) );
            notifyAllListeners( event, ( l, e ) -> l.matchCreated( e ) );
        }
    }

    public void fireActivationCancelled(final InternalMatch internalMatch,
                                        final ReteEvaluator reteEvaluator,
                                        final MatchCancelledCause cause) {
        if ( hasListeners() ) {
            ActivationCancelledEventImpl event = new ActivationCancelledEventImpl(internalMatch, getKRuntime(reteEvaluator), cause );
            notifyAllListeners( event, ( l, e ) -> l.matchCancelled( e ) );
        }
    }

    public BeforeMatchFiredEvent fireBeforeActivationFired(final InternalMatch internalMatch,
                                                           final ReteEvaluator reteEvaluator) {
        if ( hasListeners() ) {
            BeforeMatchFiredEvent event = new BeforeActivationFiredEventImpl(internalMatch, getKRuntime(reteEvaluator));
            notifyAllListeners( event, ( l, e ) -> l.beforeMatchFired( e ) );
            return event;
        }
        return null;
    }

    public void fireAfterActivationFired(final InternalMatch internalMatch,
                                         final ReteEvaluator reteEvaluator, BeforeMatchFiredEvent beforeMatchFiredEvent) {
        if ( hasListeners() ) {
            AfterMatchFiredEvent event = new AfterActivationFiredEventImpl(internalMatch, getKRuntime(reteEvaluator), beforeMatchFiredEvent );
            notifyAllListeners( event, ( l, e ) -> l.afterMatchFired( e ) );
        }
    }

    public void fireAgendaGroupPopped(final InternalAgendaGroup agendaGroup,
                                      final ReteEvaluator reteEvaluator) {
        if ( hasListeners() ) {
            AgendaGroupPoppedEventImpl event = new AgendaGroupPoppedEventImpl( agendaGroup, getKRuntime( reteEvaluator ) );
            notifyAllListeners( event, ( l, e ) -> l.agendaGroupPopped( e ) );
        }
    }

    public void fireAgendaGroupPushed(final InternalAgendaGroup agendaGroup,
                                      final ReteEvaluator reteEvaluator) {
        if ( hasListeners() ) {
            AgendaGroupPushedEventImpl event = new AgendaGroupPushedEventImpl( agendaGroup, getKRuntime( reteEvaluator ) );
            notifyAllListeners( event, ( l, e ) -> l.agendaGroupPushed( e ) );
        }
    }

    public void fireBeforeRuleFlowGroupActivated(
            final RuleFlowGroup ruleFlowGroup,
            final ReteEvaluator reteEvaluator) {
        if ( hasListeners() ) {
            RuleFlowGroupActivatedEventImpl event = new RuleFlowGroupActivatedEventImpl( ruleFlowGroup, getKRuntime( reteEvaluator ) );
            notifyAllListeners( event, ( l, e ) -> l.beforeRuleFlowGroupActivated( e ) );
        }
    }

    public void fireAfterRuleFlowGroupActivated(
            final RuleFlowGroup ruleFlowGroup,
            final ReteEvaluator reteEvaluator) {
        if ( hasListeners() ) {
            RuleFlowGroupActivatedEventImpl event = new RuleFlowGroupActivatedEventImpl( ruleFlowGroup, getKRuntime( reteEvaluator ) );
            notifyAllListeners( event, ( l, e ) -> l.afterRuleFlowGroupActivated( e ) );
        }
    }

    public void fireBeforeRuleFlowGroupDeactivated(
            final RuleFlowGroup ruleFlowGroup,
            final ReteEvaluator reteEvaluator) {
        if ( hasListeners() ) {
            RuleFlowGroupDeactivatedEventImpl event = new RuleFlowGroupDeactivatedEventImpl( ruleFlowGroup, getKRuntime( reteEvaluator ) );
            notifyAllListeners( event, ( l, e ) -> l.beforeRuleFlowGroupDeactivated( e ) );
        }
    }

    public void fireAfterRuleFlowGroupDeactivated(
            final RuleFlowGroup ruleFlowGroup,
            final ReteEvaluator reteEvaluator) {
        if ( hasListeners() ) {
            RuleFlowGroupDeactivatedEventImpl event = new RuleFlowGroupDeactivatedEventImpl( ruleFlowGroup, getKRuntime( reteEvaluator ) );
            notifyAllListeners( event, ( l, e ) -> l.afterRuleFlowGroupDeactivated( e ) );
        }
    }
}
