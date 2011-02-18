package org.drools.testframework;

import java.util.HashSet;
import java.util.Set;

import org.drools.WorkingMemory;
import org.drools.event.ActivationCancelledEvent;
import org.drools.event.ActivationCreatedEvent;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.event.AgendaEventListener;
import org.drools.event.AgendaGroupPoppedEvent;
import org.drools.event.AgendaGroupPushedEvent;
import org.drools.event.BeforeActivationFiredEvent;
import org.drools.event.RuleFlowGroupActivatedEvent;
import org.drools.event.RuleFlowGroupDeactivatedEvent;

/**
 * Measure the rule coverage.
 * @author Michael Neale
 */
public class RuleCoverageListener implements AgendaEventListener {

    final Set<String> rules;
    private int totalCount;

    /**
     * Pass in the expected rules to fire.
     * @param expectedRuleNames
     */
    public RuleCoverageListener(HashSet<String> expectedRuleNames) {
        this.rules = expectedRuleNames;
        this.totalCount = expectedRuleNames.size();
    }

    public void activationCancelled(ActivationCancelledEvent event,
            WorkingMemory workingMemory) {
    }

    public void activationCreated(ActivationCreatedEvent event,
            WorkingMemory workingMemory) {
    }

    public void afterActivationFired(AfterActivationFiredEvent event,
            WorkingMemory workingMemory) {
        rules.remove(event.getActivation().getRule().getName());
    }

    public void agendaGroupPopped(AgendaGroupPoppedEvent event,
            WorkingMemory workingMemory) {
    }

    public void agendaGroupPushed(AgendaGroupPushedEvent event,
            WorkingMemory workingMemory) {
    }

    public void beforeActivationFired(BeforeActivationFiredEvent event,
            WorkingMemory workingMemory) {
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

    public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event,
            WorkingMemory workingMemory) {
        // TODO Auto-generated method stub

    }

    public void afterRuleFlowGroupDeactivated(
            RuleFlowGroupDeactivatedEvent event, WorkingMemory workingMemory) {
        // TODO Auto-generated method stub

    }

    public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event,
            WorkingMemory workingMemory) {
        // TODO Auto-generated method stub

    }

    public void beforeRuleFlowGroupDeactivated(
            RuleFlowGroupDeactivatedEvent event, WorkingMemory workingMemory) {
        // TODO Auto-generated method stub

    }



}
