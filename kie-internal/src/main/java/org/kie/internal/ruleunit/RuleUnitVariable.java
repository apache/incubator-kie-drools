package org.kie.internal.ruleunit;

import java.lang.reflect.Type;

public interface RuleUnitVariable {

    boolean isDataSource();

    String getName();

    String getter();

    String setter();

    Type getType();

    Class<?> getDataSourceParameterType();

    Class<?> getBoxedVarType();
}
