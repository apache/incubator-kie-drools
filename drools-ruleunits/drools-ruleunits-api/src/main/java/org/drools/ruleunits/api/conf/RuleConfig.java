package org.drools.ruleunits.api.conf;

import java.util.List;

import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.internal.event.rule.RuleEventListener;

/**
 * A class which encapsulates configurations to create {@link org.drools.ruleunits.api.RuleUnitInstance}
 *
 */
public interface RuleConfig {

    List<AgendaEventListener> getAgendaEventListeners();

    List<RuleRuntimeEventListener> getRuleRuntimeListeners();

    List<RuleEventListener> getRuleEventListeners();

}
