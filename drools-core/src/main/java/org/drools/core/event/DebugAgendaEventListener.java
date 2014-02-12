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

package org.drools.core.event;

import org.drools.core.WorkingMemory;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugAgendaEventListener
    implements
    AgendaEventListener {

    protected static final transient Logger logger = LoggerFactory.getLogger(DebugAgendaEventListener.class);

    public DebugAgendaEventListener() {
        // intentionally left blank
    }

    public void matchCreated(MatchCreatedEvent event) {
        logger.info( event.toString() );
    }

    public void matchCancelled(MatchCancelledEvent event) {
        logger.info( event.toString() );
    }

    public void beforeMatchFired(BeforeMatchFiredEvent event) {
        logger.info( event.toString() );
    }

    public void afterMatchFired(AfterMatchFiredEvent event) {
        logger.info( event.toString() );
    }

    public void agendaGroupPopped(org.kie.api.event.rule.AgendaGroupPoppedEvent event) {
        logger.info( event.toString() );
    }

    public void agendaGroupPushed(org.kie.api.event.rule.AgendaGroupPushedEvent event) {
        logger.info( event.toString() );
    }

    public void beforeRuleFlowGroupActivated(org.kie.api.event.rule.RuleFlowGroupActivatedEvent event) {
        logger.info( event.toString() );
    }

    public void afterRuleFlowGroupActivated(org.kie.api.event.rule.RuleFlowGroupActivatedEvent event) {
        logger.info( event.toString() );
    }

    public void beforeRuleFlowGroupDeactivated(org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent event) {
        logger.info( event.toString() );
    }

    public void afterRuleFlowGroupDeactivated(org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent event) {
        logger.info( event.toString() );
    }
}
