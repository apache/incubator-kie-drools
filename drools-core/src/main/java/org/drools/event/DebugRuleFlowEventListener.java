package org.drools.event;

import org.drools.WorkingMemory;

public class DebugRuleFlowEventListener
    implements
    RuleFlowEventListener {

    public void ruleFlowCompleted(final RuleFlowCompletedEvent event,
                                  final WorkingMemory workingMemory) {
        System.err.println( event );
    }

    public void ruleFlowGroupActivated(final RuleFlowGroupActivatedEvent event,
                                       final WorkingMemory workingMemory) {
        System.err.println( event );
    }

    public void ruleFlowGroupDeactivated(final RuleFlowGroupDeactivatedEvent event,
                                         final WorkingMemory workingMemory) {
        System.err.println( event );
    }

    public void ruleFlowStarted(final RuleFlowStartedEvent event,
                                final WorkingMemory workingMemory) {
        System.err.println( event );
    }

}
