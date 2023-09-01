package org.drools.model.patterns;

import java.util.Collections;
import java.util.List;

import org.drools.model.Condition;
import org.drools.model.Variable;
import org.drools.model.impl.ModelComponent;

public class ExistentialPatternImpl implements Condition, ModelComponent {

    private final Condition condition;
    private final Type type;

    public ExistentialPatternImpl( Condition condition, Type type ) {
        this.condition = condition;
        this.type = type;
    }

    @Override
    public List<Condition> getSubConditions() {
        return Collections.singletonList( condition );
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Variable<?>[] getBoundVariables() {
        return new Variable<?>[0];
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !(o instanceof ExistentialPatternImpl) ) return false;

        ExistentialPatternImpl that = ( ExistentialPatternImpl ) o;

        if ( condition != null ? !condition.equals( that.condition ) : that.condition != null ) return false;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result = condition != null ? condition.hashCode() : 0;
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( !(o instanceof ExistentialPatternImpl) ) return false;

        ExistentialPatternImpl that = ( ExistentialPatternImpl ) o;

        return type == that.type && ModelComponent.areEqualInModel( condition, that.condition );
    }

    @Override
    public String toString() {
        return "ExistentialPatternImpl (" +
                "type: " + type + ", " +
                "condition: " + condition + ")";
    }

    @Override
    public Condition cloneCondition() {
        return new ExistentialPatternImpl( condition.cloneCondition(), type );
    }
}
