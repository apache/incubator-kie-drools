package org.drools.model;

import org.drools.model.functions.Function1;

import java.util.Map;

public interface Pattern<T> extends Condition {

    DataSourceDefinition getDataSourceDefinition();

    Variable<T> getPatternVariable();

    Variable[] getInputVariables();

    Constraint getConstraint();

    Map<Variable, Function1<T,?>> getBindings();
}
