/*
 * Copyright 2010 JBoss Inc
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

package org.drools.event.rule;

public class DefaultAgendaEventListener
    implements
    AgendaEventListener {

    public void activationCancelled(ActivationCancelledEvent event) {
        // intentionally left blank
    }

    public void activationCreated(ActivationCreatedEvent event) {
        // intentionally left blank
    }

    public void afterActivationFired(AfterActivationFiredEvent event) {
        // intentionally left blank
    }

    public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
        // intentionally left blank
    }

    public void agendaGroupPushed(AgendaGroupPushedEvent event) {
        // intentionally left blank
    }

    public void beforeActivationFired(BeforeActivationFiredEvent event) {
        // intentionally left blank
    }

    public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
        // intentionally left blank 
    }

    public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
        // intentionally left blank 
    }

    public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
        // intentionally left blank  
    }

    public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
        // intentionally left blank
    }

}
