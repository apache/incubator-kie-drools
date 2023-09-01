package org.drools.ruleunits.impl;

import java.lang.reflect.Type;

import org.drools.ruleunits.api.DataSource;
import org.kie.internal.ruleunit.RuleUnitVariable;

import static org.drools.util.ClassUtils.rawType;
import static org.drools.util.StringUtils.ucFirst;
import static org.drools.wiring.api.util.ClassUtils.convertFromPrimitiveType;

public final class SimpleRuleUnitVariable implements RuleUnitVariable {

    private final String name;
    private final Type type;
    private final Class<?> dataSourceParameterType;
    private final Class<?> boxedVarType;
    private final String getter;
    private final String setter;

    public SimpleRuleUnitVariable(String name, Type type, Class<?> dataSourceParameterType, boolean writable) {
        this(name, type, dataSourceParameterType, writable ? "set" + ucFirst(name) : null);
    }

    public SimpleRuleUnitVariable(String name, Type type, Class<?> dataSourceParameterType, String setter) {
        this.name = name;
        this.getter = "get" + ucFirst(name);
        this.setter = setter;
        this.type = type;
        this.dataSourceParameterType = dataSourceParameterType;
        this.boxedVarType = type instanceof Class ? convertFromPrimitiveType((Class)type) : rawType(type);
    }

    public SimpleRuleUnitVariable(String name, Class<?> type) {
        this(name, type, null, true);
    }

    @Override
    public boolean isDataSource() {
        return DataSource.class.isAssignableFrom(boxedVarType) && dataSourceParameterType != null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getter() {
        return getter;
    }

    @Override
    public String setter() {
        return setter;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Class<?> getDataSourceParameterType() {
        return dataSourceParameterType;
    }

    @Override
    public Class<?> getBoxedVarType() {
        return boxedVarType;
    }
}