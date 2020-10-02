/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.testframework;

import java.util.HashSet;
import java.util.Set;

import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;

/**
 * Measure the rule coverage.
 */
public class RuleCoverageListener implements AgendaEventListener {

    private final Set<String> rules;
    private int totalCount;

    /**
     * Pass in the expected rules to fire.
     * @param expectedRuleNames
     */
    public RuleCoverageListener(HashSet<String> expectedRuleNames) {
        this.rules = expectedRuleNames;
        this.totalCount = expectedRuleNames.size();
    }

    public void matchCancelled(MatchCancelledEvent event) {
    }

    public void matchCreated(MatchCreatedEvent event) {
    }

    public void afterMatchFired(AfterMatchFiredEvent event) {
        rules.remove(event.getMatch().getRule().getName());
    }

    public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
    }

    public void agendaGroupPushed(AgendaGroupPushedEvent event) {
    }

    public void beforeMatchFired(BeforeMatchFiredEvent event) {
    }

    public Set<String> getRules() {
        return rules;
    }

    /**
     * @return A set of rules that were not fired.
     */
    public String[] getUnfiredRules() {
        return rules.toArray(new String[rules.size()]);
    }

    public int getPercentCovered() {
        float left = totalCount - rules.size();

        return (int) ((left / totalCount) * 100);
    }

    public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
        // TODO Auto-generated method stub

    }

    public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
        // TODO Auto-generated method stub

    }

    public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
        // TODO Auto-generated method stub

    }

    public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
        // TODO Auto-generated method stub

    }



}
