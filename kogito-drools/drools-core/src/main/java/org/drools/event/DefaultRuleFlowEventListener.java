package org.drools.event;

import org.drools.WorkingMemory;

public class DefaultRuleFlowEventListener
    implements
    RuleFlowEventListener {

    public void ruleFlowCompleted(RuleFlowCompletedEvent event,
                                  WorkingMemory workingMemory) {
        // intentionally left blank
    }

    public void ruleFlowGroupActivated(RuleFlowGroupActivatedEvent event,
                                       WorkingMemory workingMemory) {
        // intentionally left blank
    }

    public void ruleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event,
                                         WorkingMemory workingMemory) {
        // intentionally left blank
    }

    public void ruleFlowStarted(RuleFlowStartedEvent event,
                                WorkingMemory workingMemory) {
        // intentionally left blank
    }

}
