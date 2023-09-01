package org.drools.ruleunits.api;

/**
 * A register for {@link RuleUnit}s.
 */
public interface RuleUnits {

    <T extends RuleUnitData> RuleUnit<T> create(Class<T> clazz);

    void register(RuleUnit<?> unit);

    void register(String name, RuleUnitInstance<?> unitInstance);

    RuleUnitInstance<?> getRegisteredInstance(String name);
}
