package org.kie.api.event.rule;

import java.util.EventListener;

public interface AgendaEventListener
    extends
    EventListener {
    void matchCreated(MatchCreatedEvent event);

    void matchCancelled(MatchCancelledEvent event);

    void beforeMatchFired(BeforeMatchFiredEvent event);

    void afterMatchFired(AfterMatchFiredEvent event);

    void agendaGroupPopped(AgendaGroupPoppedEvent event);

    void agendaGroupPushed(AgendaGroupPushedEvent event);

    void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event);

    void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event);

    void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event);

    void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event);
}
