package org.drools.ruleunits.impl;

import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.RuleUnitInstance;

public interface InternalRuleUnit<T extends RuleUnitData> extends RuleUnit<T> {

    Class<T> getRuleUnitDataClass();

    RuleUnitInstance<T> createInstance(T data, String name);
}
