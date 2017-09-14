package org.drools.model;

public interface Pattern<T> extends Condition {

    DataSourceDefinition getDataSourceDefinition();

    Variable<T> getPatternVariable();

    Variable[] getInputVariables();

    Constraint getConstraint();
}
