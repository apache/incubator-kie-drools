package org.drools.ruleunits.dsl;

import java.util.Map;
import java.util.function.Consumer;

import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.DataStream;
import org.drools.ruleunits.api.SingletonStore;
import org.drools.ruleunits.dsl.util.DataSourceDefinition;
import org.drools.ruleunits.impl.NamedRuleUnitData;

public class SyntheticRuleUnit implements RuleUnitDefinition, NamedRuleUnitData {

    private final String unitName;

    private final Map<String, DataSourceDefinition> dataSources;

    private final Map<String, Object> globals;

    private final Consumer<RulesFactory> rulesDefinition;

    SyntheticRuleUnit(String unitName, Map<String, DataSourceDefinition> dataSources, Map<String, Object> globals, Consumer<RulesFactory> rulesDefinition) {
        this.unitName = unitName;
        this.dataSources = dataSources;
        this.globals = globals;
        this.rulesDefinition = rulesDefinition;
    }

    @Override
    public String getUnitName() {
        return unitName;
    }

    @Override
    public void defineRules(RulesFactory rulesFactory) {
        rulesDefinition.accept(rulesFactory);
    }

    public Map<String, DataSourceDefinition> getDataSourceDefinitions() {
        return dataSources;
    }

    public Map<String, Object> getGlobals() {
        return globals;
    }

    public <T> DataStore<T> getDataStore(String name, Class<T> clazz) {
        return (DataStore<T>) dataSources.get(name).getDataSource();
    }

    public <T> DataStream<T> getDataStream(String name, Class<T> clazz) {
        return (DataStream<T>) dataSources.get(name).getDataSource();
    }

    public <T> SingletonStore<T> getSingletonStore(String name, Class<T> clazz) {
        return (SingletonStore<T>) dataSources.get(name).getDataSource();
    }

    public <T> T getGlobal(String name, Class<T> clazz) {
        return (T)globals.get(name);
    }
}
