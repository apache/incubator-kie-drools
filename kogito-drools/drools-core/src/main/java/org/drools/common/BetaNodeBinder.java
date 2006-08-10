package org.drools.common;

/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.spi.FieldConstraint;
import org.drools.spi.Tuple;

public class BetaNodeBinder
    implements
    Serializable {

    /**
     * 
     */
    private static final long              serialVersionUID  = -2793835336853071181L;

    public final static BetaNodeBinder     simpleBinder      = new BetaNodeBinder();

    private final FieldConstraint[]        constraints;

    private static final FieldConstraint[] EMPTY_CONSTRAINTS = new FieldConstraint[0];

    public BetaNodeBinder() {
        this.constraints = BetaNodeBinder.EMPTY_CONSTRAINTS;
    }

    public BetaNodeBinder(final FieldConstraint constraint) {
        this.constraints = new FieldConstraint[]{constraint};
    }

    public BetaNodeBinder(final FieldConstraint[] constraints) {
        this.constraints = constraints;
    }

    public boolean isAllowed(final InternalFactHandle handle,
                             final Tuple tuple,
                             final WorkingMemory workingMemory) {
        if ( this.constraints == null ) {
            return true;
        }

        for ( int i = 0; i < this.constraints.length; i++ ) {
            if ( !this.constraints[i].isAllowed( handle.getObject(),
                                                 tuple,
                                                 workingMemory ) ) {
                return false;
            }
        }
        return true;
    }

    public Set getRequiredDeclarations() {
        final Set declarations = new HashSet();
        for ( int i = 0; i < this.constraints.length; i++ ) {
            final Declaration[] array = this.constraints[i].getRequiredDeclarations();
            for ( int j = 0; j < array.length; j++ ) {
                declarations.add( array[j] );
            }
        }
        return declarations;
    }

    public int hashCode() {
        return this.constraints.hashCode();
    }

    public FieldConstraint[] getConstraints() {
        return this.constraints;
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
    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || getClass() != object.getClass() ) {
            return false;
        }

        final BetaNodeBinder other = (BetaNodeBinder) object;

        if ( this.constraints == other.constraints ) {
            return true;
        }

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