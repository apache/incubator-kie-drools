package org.drools.model.patterns;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.drools.model.Binding;
import org.drools.model.Constraint;
import org.drools.model.DataSourceDefinition;
import org.drools.model.Pattern;
import org.drools.model.SingleConstraint;
import org.drools.model.Variable;
import org.drools.model.constraints.AbstractConstraint;
import org.drools.model.impl.DataSourceDefinitionImpl;
import org.drools.model.impl.ModelComponent;

public class PatternImpl<T> extends AbstractSinglePattern implements Pattern<T>, ModelComponent {

    private final Variable<T> variable;
    private Variable[] inputVariables;
    private final DataSourceDefinition dataSourceDefinition;
    private Constraint constraint;
    private List<Binding> bindings;
    private Collection<String> watchedProps;

    public PatternImpl(Variable<T> variable) {
        this(variable, SingleConstraint.EMPTY, DataSourceDefinitionImpl.DEFAULT);
    }

    public PatternImpl(Variable<T> variable, Constraint constraint, DataSourceDefinition dataSourceDefinition) {
        this.variable = variable;
        this.constraint = constraint;
        this.dataSourceDefinition = dataSourceDefinition;
    }

    @Override
    public DataSourceDefinition getDataSourceDefinition() {
        return dataSourceDefinition;
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

    public void addConstraint( Constraint constraint ) {
        this.constraint = ( (AbstractConstraint) this.constraint ).and( constraint );
    }

    public void addBinding(Binding binding) {
        if (bindings == null) {
            bindings = new ArrayList<>();
        }
        bindings.add(binding);
    }

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
        for (String prop : props) {
            watchedProps.add(prop);
        }
    }

    public String[] getWatchedProps() {
        return watchedProps != null ? watchedProps.toArray( new String[watchedProps.size()] ) : new String[0];
    }

    private Variable[] collectInputVariables() {
        Set<Variable> varSet = new HashSet<Variable>();
        collectInputVariables(constraint, varSet);
        return varSet.toArray(new Variable[varSet.size()]);
    }

    private void collectInputVariables(Constraint constraint, Set<Variable> varSet) {
        if (constraint instanceof SingleConstraint) {
            for (Variable var : ((SingleConstraint)constraint).getVariables()) {
                varSet.add(var);
            }
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
        if ( dataSourceDefinition != null ? !dataSourceDefinition.equals( pattern.dataSourceDefinition ) : pattern.dataSourceDefinition != null )
            return false;
        if ( !ModelComponent.areEqualInModel( constraint, pattern.constraint ) ) return false;
        if ( !ModelComponent.areEqualInModel( bindings, pattern.bindings ) ) return false;
        return watchedProps != null ? watchedProps.equals( pattern.watchedProps ) : pattern.watchedProps == null;
    }
}
