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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.core.WorkingMemory;
import org.drools.core.spi.Consequence;
import org.drools.core.spi.KnowledgeHelper;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;

/**
 * This tracks what is happening in the engine with rule activations and firings.
 * It also allows you to choose what to include/exclude from firing.
 *
 * If a rule is not allowed to fire, it will still be counted as an activation.
 * If it is allowed to fire, then it will only be counted after the activation is fired.
 */
public class TestingEventListener implements AgendaEventListener {

    final Map<String, Integer> firingCounts = new HashMap<String, Integer>(100);

    long totalFires;


    public TestingEventListener() {
    }

    public AgendaFilter getAgendaFilter(final HashSet<String> ruleNames, final boolean inclusive) {
        return new AgendaFilter() {
            public boolean accept(Match match) {
                if (ruleNames.size() ==0) return true;
                String ruleName = match.getRule().getName();

                http://www.wtf.com

                //jdelong: please don't want to see records of cancelled activations

                if (inclusive) {
                    if (ruleNames.contains(ruleName)) {
                        return true;
                    } else {
                        //record(activation.getRule(), firingCounts);
                        return false;
                    }

                } else {
                    if (!ruleNames.contains(ruleName)) {
                        return true;
                    } else {
                        //record(activation.getRule(), firingCounts);
                        return false;
                    }
                }
            }
        };
    }



//    /**
//     * Exclusive means DO NOT fire the rules mentioned.
//     * For those rules, they will still be counted, just not allowed to activate.
//     * Inclusive means only the rules on the given set are allowed to fire.
//     * The other rules will have their activation counted but not be allowed to fire.
//     */
//    static void stubOutRules(HashSet<String> ruleNames, RuleBase ruleBase,
//            boolean inclusive) {
//        if (ruleNames.size() > 0) {
//            if (inclusive) {
//                Package[] pkgs = ruleBase.getPackages();
//                for (int i = 0; i < pkgs.length; i++) {
//                    Rule[] rules = pkgs[i].getRules();
//                    for (int j = 0; j < rules.length; j++) {
//                        Rule rule = rules[j];
//                        if (!ruleNames.contains(rule.getName())) {
//                            rule.setConsequence(new NilConsequence());
//                        }
//                    }
//                }
//            } else {
//                Package[] pkgs = ruleBase.getPackages();
//                for (int i = 0; i < pkgs.length; i++) {
//                    Package pkg = pkgs[i];
//                    for (Iterator iter = ruleNames.iterator(); iter.hasNext();) {
//                        String name = (String) iter.next();
//                        Rule rule = pkg.getRule(name);
//                        rule.setConsequence(new NilConsequence());
//                    }
//
//                }
//            }
//        }
//    }





    public void matchCancelled(MatchCancelledEvent event) {
    }

    public void matchCreated(MatchCreatedEvent event) {
    }

    public void afterMatchFired(AfterMatchFiredEvent event) {
        recordFiring(event.getMatch().getRule());
    }

    private void recordFiring(Rule rule) {
        record(rule, this.firingCounts);
    }

    public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
    }

    public void agendaGroupPushed(AgendaGroupPushedEvent event) {
    }

    public void beforeMatchFired(BeforeMatchFiredEvent event) {
    }

    private void record(Rule rule, Map<String, Integer> counts) {
        this.totalFires++;
        String name = rule.getName();
        if (!counts.containsKey(name)) {
            counts.put(name, 1);
        } else {
            counts.put(name, counts.get(name) + 1);
        }
    }



    /**
     * @return A map of the number of times a given rule "fired".
     * (of course in reality the side effect of its firing may have been nilled out).
     */
    public Map<String, Integer> getFiringCounts() {
        return this.firingCounts;
    }

    /**
     * Return a list of the rules fired, for display purposes.
     */
    public String[] getRulesFiredSummary() {
        String[] r = new String[firingCounts.size()];
        int i = 0;
        for (Iterator iterator = firingCounts.entrySet().iterator(); iterator.hasNext();) {
            Entry<String, Integer> e = (Entry<String, Integer>) iterator.next();
            r[i] = e.getKey() + " [" + e.getValue() + "]";
            i++;
        }

        return r;
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

class NilConsequence implements Consequence {

    public void evaluate(KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory) throws Exception {
    }
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }

    public void writeExternal(ObjectOutput out) throws IOException {

    }
    
    public String getName() {
        return "default";
    }
}

