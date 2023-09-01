package org.drools.ruleunits.api;

import org.drools.ruleunits.api.conf.RuleConfig;

/**
 * A rule unit is an atomic module defining a set of rules and a set of strongly typed {@link DataSource}s through which
 * the facts processed by the rule engine are inserted. Users never need to implement this interface since the concrete
 * implementation, reflecting what has been defined in the corresponding {@link RuleUnitData} is automatically generated
 * by the engine. It is possible to obtain an instance of the generated rule unit programmatically via the {@link RuleUnitProvider}
 * or declaratively via dependency injection.
 *
 * @param <T> The {@link RuleUnitData} for which this rule unit is generated.
 */
public interface RuleUnit<T extends RuleUnitData> {

    /**
     * Creates a {@link RuleUnitInstance} using the given {@link RuleUnitData}.
     */
    RuleUnitInstance<T> createInstance(T data);

    /**
     * Creates a {@link RuleUnitInstance} using the given {@link RuleUnitData} and {@link RuleConfig}.
     */
    RuleUnitInstance<T> createInstance(T data, RuleConfig ruleConfig);
}
