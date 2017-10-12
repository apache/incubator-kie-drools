package org.drools.model.patterns;

import org.drools.model.*;
import org.drools.model.functions.Function1;

import java.util.Map;

public class AccumulatePatternImpl<T> extends AbstractSinglePattern implements AccumulatePattern<T> {

    private final Pattern<T> pattern;
    private final AccumulateFunction<T, ?, ?>[] functions;
    private final Variable[] boundVariables;

    public AccumulatePatternImpl(Pattern<T> pattern, AccumulateFunction<T, ?, ?>... functions) {
        this.pattern = pattern;
        this.functions = functions;
        boundVariables = new Variable[functions.length];
        for (int i = 0; i < functions.length; i++) {
            boundVariables[i] = functions[i].getVariable();
        }
    }

    @Override
    public AccumulateFunction<T, ?, ?>[] getFunctions() {
        return functions;
    }

    @Override
    public Variable[] getInputVariables() {
        return pattern.getInputVariables();
    }

    @Override
    public DataSourceDefinition getDataSourceDefinition() {
        return pattern.getDataSourceDefinition();
    }

    @Override
    public Variable<T> getPatternVariable() {
        return pattern.getPatternVariable();
    }

    @Override
    public Map<Variable, Function1<T, ?>> getBindings() {
        return pattern.getBindings();
    }

    @Override
    public Variable<?>[] getBoundVariables() {
        return boundVariables;
    }

    @Override
    public Constraint getConstraint() {
        return pattern.getConstraint();
    }

    @Override
    public Type getType() {
        return Type.ACCUMULATE;
    }
}
