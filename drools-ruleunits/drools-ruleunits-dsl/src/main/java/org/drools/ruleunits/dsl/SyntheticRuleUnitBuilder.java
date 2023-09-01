package org.drools.ruleunits.dsl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.dsl.util.DataSourceDefinition;

public class SyntheticRuleUnitBuilder {
    private final String unitName;

    private Map<String, DataSourceDefinition> dataSources = new HashMap<>();
    private Map<String, Object> globals = new HashMap<>();

    private SyntheticRuleUnitBuilder(String unitName) {
        this.unitName = unitName;
    }

    public static SyntheticRuleUnitBuilder build(String unitName) {
        return new SyntheticRuleUnitBuilder(unitName);
    }

    public SyntheticRuleUnitBuilder registerDataSource(String name, DataSource dataSource, Class<?> dataClass) {
        dataSources.put(name, new DataSourceDefinition(dataSource, dataClass));
        return this;
    }

    public SyntheticRuleUnitBuilder registerGlobal(String name, Object global) {
        globals.put(name, global);
        return this;
    }

    public SyntheticRuleUnit defineRules(Consumer<RulesFactory> rulesDefinition) {
        return new SyntheticRuleUnit(unitName, dataSources, globals, rulesDefinition);
    }
}
