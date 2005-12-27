package org.drools.reteoo;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.spi.Constraint;
import org.drools.spi.Tuple;

public class BetaNodeBinder {
    private final Constraint[] constraints;

    public BetaNodeBinder(){
        this.constraints = null;
    }

    public BetaNodeBinder(Constraint constraint){
        this.constraints = new Constraint[]{constraint};
    }

    public BetaNodeBinder(Constraint[] constraints){
        this.constraints = constraints;
    }

    boolean isAllowed(Object object,
                      FactHandle handle,
                      Tuple tuple,
                      WorkingMemory workingMemory){
        if ( this.constraints == null ) {
            return true;
        }

        for ( int i = 0; i < this.constraints.length; i++ ) {
            if ( !this.constraints[i].isAllowed( object,
                                                 handle,
                                                 tuple ) ) {
                return false;
            }
        }

        return true;

    }

    boolean isAllowed(FactHandle handle,
                      Tuple tuple,
                      WorkingMemory workingMemory){
        Object object = workingMemory.getObject( handle );

        return isAllowed( object,
                          handle,
                          tuple,
                          workingMemory );
    }

    public Set getRequiredDeclarations(){
        Set declarations = new HashSet();
        for ( int i = 0; i < this.constraints.length; i++ ) {
            Collections.addAll( declarations,
                                this.constraints[i].getRequiredDeclarations() );
        }
        return declarations;
    }

    public int hashCode(){
        return this.constraints.hashCode();
    }

    /**
     * Determine if another object is equal to this.
     * 
     * @param object
     *            The object to test.
     * 
     * @return <code>true</code> if <code>object</code> is equal to this,
     *         otherwise <code>false</code>.
     */
    public boolean equals(Object object){
        if ( this == object ) {
            return true;
        }

        if ( object == null || getClass() != object.getClass() ) {
            return false;
        }

        BetaNodeBinder other = (BetaNodeBinder) object;

        if ( this.constraints.length != other.constraints.length ) {
            return false;
        }

        for ( int i = 0; i < this.constraints.length; i++ ) {
            if ( !this.constraints[i].equals( other.constraints[i] ) ) {
                return false;
            }
        }

        return true;
    }

}
