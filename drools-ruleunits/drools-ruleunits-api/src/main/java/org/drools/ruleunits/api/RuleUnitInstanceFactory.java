package org.drools.ruleunits.api;

public class RuleUnitInstanceFactory {

    public static <T extends RuleUnitData> RuleUnitInstance<T> instance(T ruleUnit) {
        try {
            return createRuleUnitInstance( ruleUnit, Class.forName(getRuleUnitClassName(ruleUnit)) );
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Do you forget to run code generation?", e);
        }
    }

    protected static <T extends RuleUnitData> String getRuleUnitClassName(T ruleUnit) {
        return ruleUnit.getClass().getCanonicalName() + "RuleUnit";
    }

    protected static <T extends RuleUnitData> RuleUnitInstance<T> createRuleUnitInstance(T ruleUnit, Class<?> unitClass) {
        try {
            RuleUnit<T> unit = (RuleUnit<T>) unitClass.getField("INSTANCE").get(null);
            return unit.createInstance(ruleUnit);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Do you forget to run code generation?", e);
        }
    }
}
