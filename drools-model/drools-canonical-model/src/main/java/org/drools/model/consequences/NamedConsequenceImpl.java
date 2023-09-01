package org.drools.model.consequences;

import org.drools.model.Condition;
import org.drools.model.Variable;
import org.drools.model.impl.ModelComponent;

public class NamedConsequenceImpl implements Condition, ModelComponent {

    private static final Variable<?>[] BOUND_VARIABLES = new Variable<?>[0];

    private final String name;
    private final boolean breaking;

    public NamedConsequenceImpl( String name, boolean breaking ) {
        this.name = name;
        this.breaking = breaking;
    }

    public String getName() {
        return name;
    }

    public boolean isBreaking() {
        return breaking;
    }

    @Override
    public Type getType() {
        return Type.CONSEQUENCE;
    }

    @Override
    public Variable<?>[] getBoundVariables() {
        return BOUND_VARIABLES;
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( !(o instanceof NamedConsequenceImpl) ) return false;

        NamedConsequenceImpl that = ( NamedConsequenceImpl ) o;

        if ( breaking != that.breaking ) return false;
        return name != null ? name.equals( that.name ) : that.name == null;
    }

    @Override
    public String toString() {
        return "NamedConsequenceImpl '" + name + "' (breaking: " + breaking + ")";
    }
}
