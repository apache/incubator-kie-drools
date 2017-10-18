package org.drools.model;

import java.util.List;

public interface Pattern<T> extends Condition {

    DataSourceDefinition getDataSourceDefinition();

    Variable<T> getPatternVariable();

    Variable[] getInputVariables();

    Constraint getConstraint();

    List<Binding> getBindings();
}
