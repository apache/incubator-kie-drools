package org.drools.core.event;

import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugRuleRuntimeEventListener
    implements
    RuleRuntimeEventListener {

    protected static final transient Logger logger = LoggerFactory.getLogger(DebugRuleRuntimeEventListener.class);

    public DebugRuleRuntimeEventListener() {
        // intentionally left blank
    }

    public void objectInserted(ObjectInsertedEvent event) {
        logger.info( event.toString() );
    }

    public void objectUpdated(ObjectUpdatedEvent event) {
        logger.info( event.toString() );
    }

    public void objectDeleted(ObjectDeletedEvent event) {
        logger.info( event.toString() );
    }

}
