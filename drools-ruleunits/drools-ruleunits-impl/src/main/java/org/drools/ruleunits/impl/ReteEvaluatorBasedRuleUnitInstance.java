package org.drools.ruleunits.impl;

import org.drools.core.common.ReteEvaluator;
import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.conf.RuleConfig;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.time.SessionClock;

public abstract class ReteEvaluatorBasedRuleUnitInstance<T extends RuleUnitData> extends AbstractRuleUnitInstance<ReteEvaluator, T> {

    protected ReteEvaluatorBasedRuleUnitInstance(RuleUnit<T> unit, T unitMemory, ReteEvaluator evaluator) {
        super(unit, unitMemory, evaluator);
    }

    protected ReteEvaluatorBasedRuleUnitInstance(RuleUnit<T> unit, T unitMemory, ReteEvaluator evaluator, RuleConfig ruleConfig) {
        super(unit, unitMemory, evaluator, ruleConfig);
    }

    @Override
    protected void addEventListeners() {
        ruleConfig.getAgendaEventListeners().stream().forEach(l -> evaluator.getAgendaEventSupport().addEventListener(l));
        ruleConfig.getRuleRuntimeListeners().stream().forEach(l -> evaluator.getRuleRuntimeEventSupport().addEventListener(l));
        ruleConfig.getRuleEventListeners().stream().forEach(l -> evaluator.getRuleEventSupport().addEventListener(l));
    }

    @Override
    public int fire() {
        return evaluator.fireAllRules();
    }

    @Override
    public int fire(AgendaFilter agendaFilter) {
        return evaluator.fireAllRules(agendaFilter);
    }

    @Override
    public void close() {
        evaluator.dispose();
    }

    @Override
    public QueryResults executeQuery(String query, Object... arguments) {
        fire();
        return evaluator.getQueryResults(query, arguments);
    }

    @Override
    public <C extends SessionClock> C getClock() {
        return (C) evaluator.getSessionClock();
    }
}
