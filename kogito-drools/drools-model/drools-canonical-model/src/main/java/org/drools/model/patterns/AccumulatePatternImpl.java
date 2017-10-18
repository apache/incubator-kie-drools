package org.drools.model.patterns;

import org.drools.model.AccumulateFunction;
import org.drools.model.AccumulatePattern;
import org.drools.model.Binding;
import org.drools.model.Constraint;
import org.drools.model.DataSourceDefinition;
import org.drools.model.Pattern;
import org.drools.model.Variable;

import java.util.List;

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
    public List<Binding> getBindings() {
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
