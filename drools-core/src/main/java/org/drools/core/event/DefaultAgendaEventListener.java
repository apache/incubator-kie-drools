package org.drools.core.event;

import org.kie.api.event.rule.*;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;

public class DefaultAgendaEventListener
    implements
    AgendaEventListener {
    public DefaultAgendaEventListener() {
        // intentionally left blank
    }

    public void matchCreated(MatchCreatedEvent event) {
        // intentionally left blank
    }

    public void matchCancelled(MatchCancelledEvent event) {
        // intentionally left blank
    }

    public void beforeMatchFired(BeforeMatchFiredEvent event) {
        // intentionally left blank
    }

    public void afterMatchFired(AfterMatchFiredEvent event) {
        // intentionally left blank
    }

    public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
        // intentionally left blank
    }

    public void agendaGroupPushed(AgendaGroupPushedEvent event) {
        // intentionally left blank
    }

    public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
        // intentionally left blank
    }

    public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
        // intentionally left blank
    }

    public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
        // intentionally left blank
    }

    public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
        // intentionally left blank
    }
}
