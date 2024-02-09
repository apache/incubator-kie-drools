/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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

    public AccumulatePatternImpl(Condition condition,  Variable var, AccumulateFunction... accumulateFunctions) {
        this.condition = condition;
        this.accumulateFunctions = accumulateFunctions;
        int extraVar = var == null ? 0 : 1; // this is the groupbyKey var
        boundVariables = new Variable[accumulateFunctions.length + extraVar];
        for (int i = 0; i < accumulateFunctions.length; i++) {
            boundVariables[i] = accumulateFunctions[i].getResult();
        }
        if (var != null) {
            boundVariables[boundVariables.length-1] = var; // add extra var to end
        }
        this.pattern = findPatternImplSource();
    }

    private Pattern findPatternImplSource() {
        if (condition instanceof Pattern) {
            return ( Pattern ) condition;
        }
        if (condition instanceof QueryCallPattern) {
            return (( QueryCallPattern ) condition).getResultPattern();
        }

        if (accumulateFunctions.length == 0) {
            return null;
        }

        final Argument source = accumulateFunctions[0].getSource();
        if (source == null) {
            return null;
        }

        for (Condition subCondition : condition.getSubConditions()) {
            if (subCondition instanceof PatternImpl) {
                PatternImpl patternImpl = (PatternImpl) subCondition;

                if ( source.equals( patternImpl.getPatternVariable() ) ) {
                    return patternImpl;
                }

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
    public boolean isQuerySource() {
        return condition instanceof QueryCallPattern;
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
    public boolean isPassive() {
        return pattern.isPassive();
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
