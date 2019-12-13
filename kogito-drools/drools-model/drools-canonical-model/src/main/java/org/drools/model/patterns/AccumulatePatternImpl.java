package org.drools.model.patterns;

import java.util.Arrays;
import java.util.Collection;

import org.drools.model.AccumulatePattern;
import org.drools.model.Argument;
import org.drools.model.Binding;
import org.drools.model.Condition;
import org.drools.model.Constraint;
import org.drools.model.Pattern;
import org.drools.model.Variable;
import org.drools.model.functions.accumulate.AccumulateFunction;
import org.drools.model.impl.ModelComponent;

public class AccumulatePatternImpl<T> extends AbstractSinglePattern implements AccumulatePattern<T>, ModelComponent {

    private final Condition condition;
    private final AccumulateFunction[] accumulateFunctions;
    private final Variable[] boundVariables;
    private final Pattern<T> pattern;

    public AccumulatePatternImpl(Condition condition, AccumulateFunction... accumulateFunctions) {
        this.condition = condition;
        this.accumulateFunctions = accumulateFunctions;
        boundVariables = new Variable[accumulateFunctions.length];
        for (int i = 0; i < accumulateFunctions.length; i++) {
            boundVariables[i] = accumulateFunctions[i].getResult();
        }
        this.pattern = findPatternImplSource();
    }

    private Pattern findPatternImplSource() {
        if (condition instanceof Pattern) {
            return ( Pattern ) condition;
        }

        if (accumulateFunctions.length == 0) {
            return null;
        }

        final Argument source = accumulateFunctions[0].getSource();

        for (Condition subCondition : condition.getSubConditions()) {
            if (subCondition instanceof PatternImpl) {
                PatternImpl patternImpl = (PatternImpl) subCondition;

                boolean isSource =  patternImpl
                        .getBindings()
                        .stream()
                        .anyMatch(b -> (b instanceof Binding) && ((Binding) b).getBoundVariable().equals(source));
                if (isSource) {
                    return patternImpl;
                }

            }
        }
        return null;
    }

    @Override
    public AccumulateFunction[] getAccumulateFunctions() {
        return accumulateFunctions;
    }

    @Override
    public boolean isCompositePatterns() {
        return condition instanceof CompositePatterns;
    }

    @Override
    public Condition getCondition() {
        return condition;
    }

    @Override
    public Pattern getPattern() {
        return pattern;
    }

    @Override
    public Variable[] getInputVariables() {
        return pattern.getInputVariables();
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
        if ( !ModelComponent.areEqualInModel(accumulateFunctions, that.accumulateFunctions) ) return false;
        return ModelComponent.areEqualInModel( boundVariables, that.boundVariables );
    }

    @Override
    public String toString() {
        return "AccumulatePatternImpl (" +
                "functions: " + Arrays.toString(accumulateFunctions) + ", " +
                "condition: " + condition + ", " +
                "pattern: " + pattern + ")";
    }
}
