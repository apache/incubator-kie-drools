package org.drools.core.common;

import java.util.Collection;

import org.drools.core.event.AgendaEventSupport;
import org.drools.core.event.RuleEventListenerSupport;
import org.drools.core.event.RuleRuntimeEventSupport;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;

public interface EventSupport {

    Collection<AgendaEventListener> getAgendaEventListeners();

    Collection<RuleRuntimeEventListener> getRuleRuntimeEventListeners();

    AgendaEventSupport getAgendaEventSupport();
    
    RuleRuntimeEventSupport getRuleRuntimeEventSupport();

    RuleEventListenerSupport getRuleEventSupport();

}
