package org.kie.internal.ruleunit;

public interface RuleUnitVariable {

    boolean isDataSource();

    String getName();

    String getter();

    Class<?> getType();

    Class<?> getDataSourceParameterType();

    Class<?> getBoxedVarType();
}
