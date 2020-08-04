package org.kie.kogito.rules.units;

import org.kie.internal.ruleunit.RuleUnitVariable;

import static org.drools.reflective.util.ClassUtils.convertFromPrimitiveType;
import static org.kie.kogito.rules.units.StringUtils.capitalize;

public final class SimpleRuleUnitVariable implements RuleUnitVariable {

    private final String name;
    private final Class<?> type;
    private final Class<?> dataSourceParameterType;
    private final Class<?> boxedVarType;
    private final String getter;
    private final String setter;

    public SimpleRuleUnitVariable(String name, Class<?> type, Class<?> dataSourceParameterType, boolean writable) {
        this.name = name;
        this.getter = "get" + capitalize(name);
        this.setter = writable? "set" + capitalize(name) : null;
        this.type = type;
        this.dataSourceParameterType = dataSourceParameterType;
        this.boxedVarType = convertFromPrimitiveType(type);
    }

    public SimpleRuleUnitVariable(String name, Class<?> type) {
        this(name, type, null, true);
    }

    public boolean isDataSource() {
        return dataSourceParameterType != null;
    }

    public String getName() {
        return name;
    }

    public String getter() {
        return getter;
    }

    public String setter() {
        return setter;
    }

    public Class<?> getType() {
        return type;
    }

    public Class<?> getDataSourceParameterType() {
        return dataSourceParameterType;
    }

    public Class<?> getBoxedVarType() {
        return boxedVarType;
    }
}