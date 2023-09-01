package org.drools.ruleunits.impl;

import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.drools.ruleunits.api.conf.RuleConfig;

public abstract class AbstractRuleUnitInstance<E, T extends RuleUnitData> implements RuleUnitInstance<T> {

    private final T unitMemory;
    private final RuleUnit<T> unit;
    protected final E evaluator;
    protected RuleConfig ruleConfig = RuleUnitProvider.get().newRuleConfig();

    public AbstractRuleUnitInstance(RuleUnit<T> unit, T unitMemory, E evaluator) {
        this.unit = unit;
        this.evaluator = evaluator;
        this.unitMemory = unitMemory;
        bind(evaluator, unitMemory);
    }

    public AbstractRuleUnitInstance(RuleUnit<T> unit, T unitMemory, E evaluator, RuleConfig ruleConfig) {
        this.unit = unit;
        this.evaluator = evaluator;
        this.unitMemory = unitMemory;
        this.ruleConfig = ruleConfig;
        addEventListeners();
        bind(evaluator, unitMemory);
    }

    @Override
    public RuleUnit<T> unit() {
        return unit;
    }

    public T ruleUnitData() {
        return unitMemory;
    }

    public E getEvaluator() {
        return evaluator;
    }

    protected void addEventListeners() {
        // no-op by default
    }

    protected abstract void bind(E evaluator, T workingMemory);
}
