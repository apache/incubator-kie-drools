package org.drools.event;

import org.drools.WorkingMemory;

public class DebugRuleFlowEventListener
    implements
    RuleFlowEventListener {

    public void beforeRuleFlowCompleted(final RuleFlowCompletedEvent event,
                                        final WorkingMemory workingMemory) {
        System.err.println( event );
    }

    public void afterRuleFlowCompleted(final RuleFlowCompletedEvent event,
                                       final WorkingMemory workingMemory) {
        System.err.println(event);
    }

    public void beforeRuleFlowGroupActivated(
            final RuleFlowGroupActivatedEvent event,
            final WorkingMemory workingMemory) {
        System.err.println( event );
    }

    public void afterRuleFlowGroupActivated(
            final RuleFlowGroupActivatedEvent event,
            final WorkingMemory workingMemory) {
        System.err.println( event );
    }

    public void beforeRuleFlowGroupDeactivated(
            final RuleFlowGroupDeactivatedEvent event,
            final WorkingMemory workingMemory) {
        System.err.println( event );
    }

    public void afterRuleFlowGroupDeactivated(
            final RuleFlowGroupDeactivatedEvent event,
            final WorkingMemory workingMemory) {
        System.err.println( event );
    }

    public void beforeRuleFlowStarted(final RuleFlowStartedEvent event,
                                      final WorkingMemory workingMemory) {
        System.err.println( event );
    }

    public void afterRuleFlowStarted(final RuleFlowStartedEvent event,
                                     final WorkingMemory workingMemory) {
        System.err.println(event);
    }

    public void afterRuleFlowNodeTriggered(final RuleFlowNodeTriggeredEvent event,
                                           final WorkingMemory workingMemory) {
        System.err.println(event);
    }

    public void beforeRuleFlowNodeTriggered(final RuleFlowNodeTriggeredEvent event,
                                            final WorkingMemory workingMemory) {
        System.err.println(event);
    }

	public void afterRuleFlowNodeLeft(final RuleFlowNodeTriggeredEvent event,
			                          final WorkingMemory workingMemory) {
        System.err.println(event);
	}

	public void beforeRuleFlowNodeLeft(final RuleFlowNodeTriggeredEvent event,
			                           final WorkingMemory workingMemory) {
        System.err.println(event);
	}

}
