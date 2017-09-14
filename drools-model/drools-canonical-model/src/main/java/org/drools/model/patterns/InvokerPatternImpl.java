package org.drools.model.patterns;

import org.drools.model.Constraint;
import org.drools.model.DataSourceDefinition;
import org.drools.model.InvokerPattern;
import org.drools.model.Variable;

public abstract class InvokerPatternImpl<T> extends AbstractSinglePattern implements InvokerPattern<T> {

    private final DataSourceDefinition dataSourceDefinition;
    private final Variable<T> variable;
    private final Variable[] inputVariables;

    InvokerPatternImpl(DataSourceDefinition dataSourceDefinition, Variable<T> boundVariable, Variable... inputVariables) {
        this.dataSourceDefinition = dataSourceDefinition;
        this.variable = boundVariable;
        this.inputVariables = inputVariables;
    }

    @Override
    public Variable<T> getPatternVariable() {
        return variable;
    }

    @Override
    public Variable<?>[] getBoundVariables() {
        return new Variable[] { variable };
    }

    @Override
    public Variable[] getInputVariables() {
        return inputVariables;
    }

    @Override
    public DataSourceDefinition getDataSourceDefinition() {
        return dataSourceDefinition;
    }

    @Override
    public Constraint getConstraint() {
        throw new UnsupportedOperationException("An InvokerPattern doesn't have a Constraint");
    }
}
