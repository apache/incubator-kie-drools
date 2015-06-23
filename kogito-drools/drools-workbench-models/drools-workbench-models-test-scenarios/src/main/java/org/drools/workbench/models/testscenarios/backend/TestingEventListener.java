/*
 * Copyright 2015 JBoss Inc
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

package org.drools.workbench.models.testscenarios.backend;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

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
 * <p/>
 * If a rule is not allowed to fire, it will still be counted as an activation.
 * If it is allowed to fire, then it will only be counted after the activation is fired.
 */
public class TestingEventListener
        implements
        AgendaEventListener {

    final Map<String, Integer> firingCounts = new HashMap<String, Integer>( 100 );

    long totalFires;

    public TestingEventListener() {
    }

    public AgendaFilter getAgendaFilter( final HashSet<String> ruleNames,
                                         final boolean inclusive ) {
        return new AgendaFilter() {
            public boolean accept( Match activation ) {
                if ( ruleNames.isEmpty() ) {
                    return true;
                }
                String ruleName = activation.getRule().getName();

                if ( inclusive ) {
                    return ruleNames.contains( ruleName );
                } else {
                    return !ruleNames.contains( ruleName );
                }
            }
        };
    }

    public void afterMatchFired( AfterMatchFiredEvent event ) {
        recordFiring( event.getMatch().getRule() );
    }

    private void recordFiring( Rule rule ) {
        record( rule, this.firingCounts );
    }

    public void agendaGroupPopped( AgendaGroupPoppedEvent event ) {
    }

    public void agendaGroupPushed( AgendaGroupPushedEvent event ) {
    }

    public void beforeMatchFired( BeforeMatchFiredEvent event ) {
    }

    private void record( Rule rule,
                         Map<String, Integer> counts ) {
        this.totalFires++;
        String name = rule.getName();
        if ( !counts.containsKey( name ) ) {
            counts.put( name, 1 );
        } else {
            counts.put( name, counts.get( name ) + 1 );
        }
    }

    /**
     * @return A map of the number of times a given rule "fired".
     *         (of course in reality the side effect of its firing may have been nilled out).
     */
    public Map<String, Integer> getFiringCounts() {
        return this.firingCounts;
    }

    /**
     * Return a list of the rules fired, for display purposes.
     */
    public String[] getRulesFiredSummary() {
        String[] r = new String[ firingCounts.size() ];
        int i = 0;
        for ( Entry<String, Integer> e : firingCounts.entrySet() ) {
            r[ i ] = e.getKey() + " [" + e.getValue() + "]";
            i++;
        }
        return r;
    }

    @Override
    public void matchCreated( MatchCreatedEvent event ) {
    }

    @Override
    public void matchCancelled( MatchCancelledEvent event ) {
    }

    @Override
    public void beforeRuleFlowGroupActivated( RuleFlowGroupActivatedEvent event ) {
    }

    @Override
    public void afterRuleFlowGroupActivated( RuleFlowGroupActivatedEvent event ) {
    }

    @Override
    public void beforeRuleFlowGroupDeactivated( RuleFlowGroupDeactivatedEvent event ) {
    }

    @Override
    public void afterRuleFlowGroupDeactivated( RuleFlowGroupDeactivatedEvent event ) {
    }

}

