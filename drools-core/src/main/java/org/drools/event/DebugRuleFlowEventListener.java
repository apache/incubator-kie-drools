package org.drools.event;

import org.drools.WorkingMemory;

public class DebugRuleFlowEventListener
    implements
    RuleFlowEventListener {

    public void ruleFlowCompleted(RuleFlowCompletedEvent event,
                                  WorkingMemory workingMemory) {
        System.err.println( event );
    }

    public void ruleFlowGroupActivated(RuleFlowGroupActivatedEvent event,
                                       WorkingMemory workingMemory) {
        System.err.println( event );
    }

    public void ruleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event,
                                         WorkingMemory workingMemory) {
        System.err.println( event );
    }

    public void ruleFlowStarted(RuleFlowStartedEvent event,
                                WorkingMemory workingMemory) {
        System.err.println( event );
    }

}
