package org.drools.core.event;

import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugAgendaEventListener
    implements
    AgendaEventListener {

    protected static final transient Logger logger = LoggerFactory.getLogger(DebugAgendaEventListener.class);

    public DebugAgendaEventListener() {
        // intentionally left blank
    }

    public void matchCreated(MatchCreatedEvent event) {
        logger.info( event.toString() );
    }

    public void matchCancelled(MatchCancelledEvent event) {
        logger.info( event.toString() );
    }

    public void beforeMatchFired(BeforeMatchFiredEvent event) {
        logger.info( event.toString() );
    }

    public void afterMatchFired(AfterMatchFiredEvent event) {
        logger.info( event.toString() );
    }

    public void agendaGroupPopped(org.kie.api.event.rule.AgendaGroupPoppedEvent event) {
        logger.info( event.toString() );
    }

    public void agendaGroupPushed(org.kie.api.event.rule.AgendaGroupPushedEvent event) {
        logger.info( event.toString() );
    }

    public void beforeRuleFlowGroupActivated(org.kie.api.event.rule.RuleFlowGroupActivatedEvent event) {
        logger.info( event.toString() );
    }

    public void afterRuleFlowGroupActivated(org.kie.api.event.rule.RuleFlowGroupActivatedEvent event) {
        logger.info( event.toString() );
    }

    public void beforeRuleFlowGroupDeactivated(org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent event) {
        logger.info( event.toString() );
    }

    public void afterRuleFlowGroupDeactivated(org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent event) {
        logger.info( event.toString() );
    }
}
