package org.kie.internal.ruleunit;

public class RuleUnitUtil {
    public static final String RULE_UNIT_ENTRY_POINT = "$$units$$";
    public static final String RULE_UNIT_DECLARATION = "$$unit";

    public static boolean isDataSource(Class<?> clazz) {
        RuleUnitComponentFactory ruleUnitComponent = RuleUnitComponentFactory.get();
        return ruleUnitComponent != null && ruleUnitComponent.isDataSourceClass( clazz );
    }
}
