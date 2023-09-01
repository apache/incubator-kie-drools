package org.kie.internal.builder.fluent;

import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.RuleRuntime;
import org.kie.api.runtime.rule.StatefulRuleSession;

/**
 * See {@link RuleRuntime} and {@link StatefulRuleSession}
 */
public interface RuleFluent<T, U> {

    T fireAllRules();

    T setGlobal( String identifier, Object object );

    T getGlobal(String identifier);

    T insert(Object object);

    T update( FactHandle handle, Object object );

    T delete(FactHandle handle);

    T setActiveRuleFlowGroup(String ruleFlowGroup);

    T setActiveAgendaGroup(String agendaGroup);

    U dispose();

}
