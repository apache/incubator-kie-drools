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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.drools.model.Binding;
import org.drools.model.Condition;
import org.drools.model.Constraint;
import org.drools.model.Pattern;
import org.drools.model.SingleConstraint;
import org.drools.model.Variable;
import org.drools.model.constraints.AbstractConstraint;
import org.drools.model.impl.ModelComponent;

public class PatternImpl<T> extends AbstractSinglePattern implements Pattern<T>, ModelComponent {

    private final Variable<T> variable;
    private final Condition.Type type;
    private Variable[] inputVariables;
    private Constraint constraint;
    private List<Binding> bindings;
    private Collection<String> watchedProps;
    private boolean passive;

    public PatternImpl(Variable<T> variable) {
        this(variable, SingleConstraint.TRUE );
    }

    public PatternImpl(Variable<T> variable, Constraint constraint) {
        this(variable, constraint, null, Type.PATTERN);
    }

    public PatternImpl(Variable<T> variable, Condition.Type type) {
        this(variable, SingleConstraint.TRUE, type );
    }

    public PatternImpl(Variable<T> variable, Constraint constraint, Condition.Type type) {
        this(variable, constraint, null, type);
    }

    public PatternImpl(Variable<T> variable, Constraint constraint, List<Binding> bindings) {
        this(variable, constraint, bindings, Type.PATTERN);
    }

    public PatternImpl(Variable<T> variable, Constraint constraint, List<Binding> bindings, Condition.Type type) {
        this.variable = variable;
        this.constraint = constraint;
        this.bindings = bindings;
        this.type = type;
    }

    @Override
    public Condition.Type getType() {
        return type;
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
        if (inputVariables == null) {
            this.inputVariables = collectInputVariables();
        }
        return inputVariables;
    }

    @Override
    public Constraint getConstraint() {
        return constraint;
    }

    public boolean hasConstraints() {
        return this.constraint != SingleConstraint.TRUE;
    }

    public void addConstraint( Constraint constraint ) {
        this.constraint = hasConstraints() ? ( (AbstractConstraint) this.constraint ).with( constraint ) : constraint;
    }

    public void addBinding(Binding binding) {
        if (bindings == null) {
            bindings = new ArrayList<>();
        }
        bindings.add(binding);
    }

    @Override
    public Collection<Binding> getBindings() {
        return bindings != null ? bindings : Collections.emptyList();
    }

    public void addWatchedProps(String[] props) {
        if (props == null || props.length == 0) {
            return;
        }
        if (watchedProps == null) {
            watchedProps = new LinkedHashSet<>();
        }
        Collections.addAll(watchedProps, props);
    }

    @Override
    public String[] getWatchedProps() {
        return watchedProps != null ? watchedProps.toArray( new String[watchedProps.size()] ) : new String[0];
    }

    public boolean isPassive() {
        return passive;
    }

    public void setPassive( boolean passive ) {
        this.passive = passive;
    }

    private Variable[] collectInputVariables() {
        Set<Variable> varSet = new LinkedHashSet<>();
        collectInputVariables(constraint, varSet);
        return varSet.toArray(new Variable[varSet.size()]);
    }

    private void collectInputVariables(Constraint constraint, Set<Variable> varSet) {
        if (constraint instanceof SingleConstraint) {
            Collections.addAll(varSet, ((SingleConstraint) constraint).getVariables());
        } else {
            for (Constraint child : constraint.getChildren()) {
                collectInputVariables(child, varSet);
            }
        }
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( !(o instanceof PatternImpl) ) return false;

        PatternImpl<?> pattern = ( PatternImpl<?> ) o;

        if ( !ModelComponent.areEqualInModel( variable, pattern.variable ) ) return false;
        if ( !ModelComponent.areEqualInModel( inputVariables, pattern.inputVariables ) ) return false;
        if ( !ModelComponent.areEqualInModel( constraint, pattern.constraint ) ) return false;
        if ( !ModelComponent.areEqualInModel( bindings, pattern.bindings ) ) return false;
        return watchedProps != null ? watchedProps.equals( pattern.watchedProps ) : pattern.watchedProps == null;
    }

    public PatternImpl<T> negate() {
        this.constraint = constraint.negate();
        return this;
    }

    @Override
    public String toString() {
        return "PatternImpl (type: " +  type + ", " +
                "inputVars: " + Arrays.toString(inputVariables) + ", " +
                "outputVar: " + variable + ", " +
                "constraint: " + constraint + ")";
    }

    @Override
    public PatternImpl cloneCondition() {
        return new PatternImpl(variable, (( AbstractConstraint ) constraint).cloneConstraint(), bindings == null ? null : new ArrayList<>(bindings), type);
    }
}
