package org.drools.ruleunit.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

import org.drools.ruleunit.RuleUnit;
import org.kie.internal.ruleunit.RuleUnitVariable;

import static org.drools.reflective.util.ClassUtils.convertFromPrimitiveType;
import static org.drools.reflective.util.ClassUtils.getter2property;
import static org.drools.reflective.util.ClassUtils.ucFirst;

public final class ReflectiveRuleUnitVariable implements RuleUnitVariable {

    private final String name;
    private final Class<?> type;
    private final Class<?> dataSourceParameterType;
    private final Class<?> boxedVarType;
    private final String getter;
    private final String setter;
    private final Method getterMethod;


    public ReflectiveRuleUnitVariable(String name, Method getterMethod) {
        Objects.requireNonNull(name, "Invalid name was given: null");

        if (!RuleUnit.class.isAssignableFrom(getterMethod.getDeclaringClass())) {
            throw new IllegalArgumentException(
                    String.format("The given method '%s' is not from a RuleUnit instance", getterMethod));
        }

        if (getterMethod.getParameterCount() != 0) {
            throw new IllegalArgumentException(
                    String.format("The given method '%s' is not from a RuleUnit instance", getterMethod));
        }

        if (getterMethod.getName().equals("getClass")) {
            throw new IllegalArgumentException("'getClass' is not a valid method for a rule unit variable");
        }

        String id = getter2property(getterMethod.getName());

        if (id == null) {
            throw new IllegalArgumentException(
                    String.format("Could not parse getter name for method '%s'", getterMethod));
        }

        this.name = name;
        this.getter = getterMethod.getName();
        this.getterMethod = getterMethod;
        this.setter = "set" + ucFirst(name);
        this.type = getterMethod.getReturnType();
        this.dataSourceParameterType = getUnitVarType(getterMethod);
        this.boxedVarType = convertFromPrimitiveType(type);
    }

    private Class<?> getUnitVarType(Method m) {
        Class<?> returnClass = m.getReturnType();
        if (returnClass.isArray()) {
            return returnClass.getComponentType();
        } else if (Iterable.class.isAssignableFrom( returnClass )) {
            Type returnType = m.getGenericReturnType();
            Class<?> sourceType = returnType instanceof ParameterizedType ?
                    (Class<?>) ( (ParameterizedType) returnType ).getActualTypeArguments()[0] :
                    Object.class;
            return sourceType;
        } else {
            return returnClass;
        }
    }


    @Override
    public boolean isDataSource() {
        return dataSourceParameterType != null;
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
    public Class<?> getType() {
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

    public Object getValue(RuleUnit ruleUnit) {
        try {
            return getterMethod.invoke(ruleUnit);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "ReflectiveRuleUnitVariable{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", dataSourceParameterType=" + dataSourceParameterType +
                '}';
    }
}