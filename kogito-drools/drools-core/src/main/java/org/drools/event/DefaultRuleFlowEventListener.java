package org.drools.event;

import org.drools.WorkingMemory;

public class DefaultRuleFlowEventListener
    implements
    RuleFlowEventListener {

    public void ruleFlowCompleted(final RuleFlowCompletedEvent event,
                                  final WorkingMemory workingMemory) {
        // intentionally left blank
    }

    public void ruleFlowGroupActivated(final RuleFlowGroupActivatedEvent event,
                                       final WorkingMemory workingMemory) {
        // intentionally left blank
    }

    public void ruleFlowGroupDeactivated(final RuleFlowGroupDeactivatedEvent event,
                                         final WorkingMemory workingMemory) {
        // intentionally left blank
    }

    public void ruleFlowStarted(final RuleFlowStartedEvent event,
                                final WorkingMemory workingMemory) {
        // intentionally left blank
    }

}
