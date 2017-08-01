/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.instance.event.listeners;

import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.api.runtime.KieSession;

/**
 * Dedicated AgendaEventListener that will fireAllRules as soon as:
 * <ul>
 *  <li>match is created</li>
 *  <li>after rule flow group is activated</li>
 * </ul>
 * This listener should be used to automatically fire rules as soon as they get activated. 
 * Especially useful for executing business rule tasks as part of the process.
 */
public class TriggerRulesEventListener implements AgendaEventListener {
    
    private KieSession ksession;
    
    public TriggerRulesEventListener(KieSession ksession) {

        this.ksession = ksession;
    }

    @Override
    public void matchCreated(MatchCreatedEvent event) {
    }

    @Override
    public void matchCancelled(MatchCancelledEvent event) {
        
    }

    @Override
    public void beforeMatchFired(BeforeMatchFiredEvent event) {
        
    }

    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
          
    }

    @Override
    public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
          
    }

    @Override
    public void agendaGroupPushed(AgendaGroupPushedEvent event) {
          
    }

    @Override
    public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
       
    }

    @Override
    public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
        ksession.fireAllRules();
        
    }

    @Override
    public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
        
    }

    @Override
    public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
       
    }
}