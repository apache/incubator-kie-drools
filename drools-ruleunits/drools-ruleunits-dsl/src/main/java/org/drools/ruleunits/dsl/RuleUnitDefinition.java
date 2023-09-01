package org.drools.ruleunits.dsl;

import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitData;

/**
 * A {@link RuleUnitData} allowing to define not only the set of data used by a {@link RuleUnit}, but also,
 * through a convenient fluent Java DSL, the set of rules belonging to it.
 */
public interface RuleUnitDefinition extends RuleUnitData {

    /**
     * The method to be implemented to define the set of rules for this {@link RuleUnit}.
     */
    void defineRules(RulesFactory rulesFactory);
}
