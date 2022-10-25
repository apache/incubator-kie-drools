package org.drools.ruleunits.impl;

import java.util.function.Function;

import org.drools.core.common.ReteEvaluator;
import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.RuleUnitInstance;

public interface InternalRuleUnit<T extends RuleUnitData> extends RuleUnit<T> {

    Class<T> getRuleUnitDataClass();

    RuleUnitInstance<T> createInstance(T data, String name);

    void setEvaluatorConfigurator(Function<ReteEvaluator, ReteEvaluator> evaluatorConfigurator);
}
