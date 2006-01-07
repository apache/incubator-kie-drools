package org.drools.reteoo;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.spi.BetaNodeConstraint;
import org.drools.spi.Tuple;

public class BetaNodeBinder {
    private final BetaNodeConstraint[] constraints;

    public BetaNodeBinder() {
        this.constraints = null;
    }

    public BetaNodeBinder(BetaNodeConstraint constraint) {
        this.constraints = new BetaNodeConstraint[]{constraint};
    }

    public BetaNodeBinder(BetaNodeConstraint[] constraints) {
        this.constraints = constraints;
    }

    boolean isAllowed(Object object,
                      FactHandle handle,
                      Tuple tuple,
                      WorkingMemory workingMemory) {
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
                      WorkingMemory workingMemory) {
        if ( this.constraints == null ) {
            return true;
        }
        
        Object object = workingMemory.getObject( handle );

        return isAllowed( object,
                          handle,
                          tuple,
                          workingMemory );
    }

    public Set getRequiredDeclarations() {
        Set declarations = new HashSet();
        for ( int i = 0; i < this.constraints.length; i++ ) {
            Declaration[] array = this.constraints[i].getRequiredDeclarations();
            for (int j = 0; j < array.length; j++) {
                declarations.add( array[j] );
            }
        }
        return declarations;
    }

    public int hashCode() {
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
    public boolean equals(Object object) {
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
