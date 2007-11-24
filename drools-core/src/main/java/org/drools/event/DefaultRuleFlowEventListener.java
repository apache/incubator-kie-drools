package org.drools.event;

import org.drools.WorkingMemory;

public class DefaultRuleFlowEventListener
    implements
    RuleFlowEventListener {

    public void beforeRuleFlowCompleted(final RuleFlowCompletedEvent event,
                                        final WorkingMemory workingMemory) {
        // intentionally left blank
    }

    public void afterRuleFlowCompleted(final RuleFlowCompletedEvent event,
                                       final WorkingMemory workingMemory) {
        // intentionally left blank
    }

    public void beforeRuleFlowGroupActivated(final RuleFlowGroupActivatedEvent event,
                                             final WorkingMemory workingMemory) {
        // intentionally left blank
    }

    public void afterRuleFlowGroupActivated(final RuleFlowGroupActivatedEvent event,
                                            final WorkingMemory workingMemory) {
        // intentionally left blank
    }

    public void beforeRuleFlowGroupDeactivated(final RuleFlowGroupDeactivatedEvent event,
                                               final WorkingMemory workingMemory) {
        // intentionally left blank
    }

    public void afterRuleFlowGroupDeactivated(final RuleFlowGroupDeactivatedEvent event,
                                              final WorkingMemory workingMemory) {
        // intentionally left blank
    }

    public void beforeRuleFlowStarted(final RuleFlowStartedEvent event,
                                      final WorkingMemory workingMemory) {
        // intentionally left blank
    }

    public void afterRuleFlowStarted(final RuleFlowStartedEvent event,
                                     final WorkingMemory workingMemory) {
        // intentionally left blank
    }

    public void afterRuleFlowNodeTriggered(final RuleFlowNodeTriggeredEvent event,
                                           final WorkingMemory workingMemory) {
        // intentionally left blank
    }

    public void beforeRuleFlowNodeTriggered(final RuleFlowNodeTriggeredEvent event,
                                            final WorkingMemory workingMemory) {
        // intentionally left blank
    }

}
