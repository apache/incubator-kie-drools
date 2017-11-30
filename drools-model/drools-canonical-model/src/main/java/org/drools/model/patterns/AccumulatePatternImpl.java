package org.drools.model.patterns;

import java.util.Collection;
import java.util.Optional;

import org.drools.model.AccumulateFunction;
import org.drools.model.AccumulatePattern;
import org.drools.model.Binding;
import org.drools.model.Constraint;
import org.drools.model.DataSourceDefinition;
import org.drools.model.Pattern;
import org.drools.model.Variable;
import org.drools.model.impl.ModelComponent;

public class AccumulatePatternImpl<T> extends AbstractSinglePattern implements AccumulatePattern<T>, ModelComponent {

    private final Pattern<T> pattern;
    private final Optional<CompositePatterns> compositePatterns;
    private final AccumulateFunction<T, ?, ?>[] functions;
    private final Variable[] boundVariables;

    public AccumulatePatternImpl(Pattern<T> pattern, Optional<CompositePatterns> compositePatterns, AccumulateFunction<T, ?, ?>... functions) {
        this.pattern = pattern;
        this.compositePatterns = compositePatterns;
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
    public Optional<CompositePatterns> getCompositePatterns() {
        return compositePatterns;
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
        if(pattern == null) {
            return null;
        }
        return pattern.getPatternVariable();
    }

    @Override
    public Collection<Binding> getBindings() {
        return pattern.getBindings();
    }

    @Override
    public String[] getWatchedProps() {
        return pattern.getWatchedProps();
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

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( !(o instanceof AccumulatePatternImpl) ) return false;

        AccumulatePatternImpl<?> that = ( AccumulatePatternImpl<?> ) o;

        if ( !ModelComponent.areEqualInModel( pattern, that.pattern ) ) return false;
        if ( !ModelComponent.areEqualInModel( functions, that.functions ) ) return false;
        return ModelComponent.areEqualInModel( boundVariables, that.boundVariables );
    }
}
