package org.kie.pmml.models.drools.utils;

import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.slf4j.Logger;

/**
 * Class used to provide an <code>AgendaEventListener</code> instance
 */
public class KiePMMLAgendaListenerUtils {

    public static AgendaEventListener getAgendaEventListener(final Logger logger) {
        return new AgendaEventListener() {
            public void matchCancelled(MatchCancelledEvent event) { if (logger.isDebugEnabled()) {
                logger.debug(event.toString());
            }}

            public void matchCreated(MatchCreatedEvent event) { if (logger.isDebugEnabled()) {
                logger.debug(event.toString());
            }}

            public void afterMatchFired(AfterMatchFiredEvent event) {
                if (logger.isDebugEnabled()) {
                    logger.debug(event.toString());
                }
            }

            public void agendaGroupPopped(AgendaGroupPoppedEvent event) { if (logger.isDebugEnabled()) {
                logger.debug(event.toString());
            }}

            public void agendaGroupPushed(AgendaGroupPushedEvent event) {
                if (logger.isDebugEnabled()) {
                    logger.debug(event.toString());
                }
            }

            public void beforeMatchFired(BeforeMatchFiredEvent event) {
                if (logger.isDebugEnabled()) {
                    logger.debug(event.toString());
                }
            }

            public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) { if (logger.isDebugEnabled()) {
                logger.debug(event.toString());
            }}

            public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) { if (logger.isDebugEnabled()) {
                logger.debug(event.toString());
            }}

            public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) { if (logger.isDebugEnabled()) {
                logger.debug(event.toString());
            }}

            public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) { if (logger.isDebugEnabled()) {
                logger.debug(event.toString());
            }}
        };
    }
}
