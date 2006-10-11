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
import org.drools.base.evaluators.Operator;
import org.drools.common.InstanceNotEqualsConstraint.InstanceNotEqualsConstraintContextEntry;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.ObjectHashTable;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.ContextEntry;
import org.drools.rule.Declaration;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.VariableConstraint;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.Evaluator;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.FieldExtractor;
import org.drools.spi.Tuple;
import org.drools.util.FactHashTable;
import org.drools.util.FieldIndexHashTable;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListEntry;
import org.drools.util.TupleHashTable;

public class BetaNodeConstraints
    implements
    Serializable {

    /**
     * 
     */
    private static final long               serialVersionUID         = 320L;

    public final static BetaNodeConstraints emptyBetaNodeConstraints = new BetaNodeConstraints();

    private final LinkedList                constraints;

    private ContextEntry                    contexts;

    public BetaNodeConstraints() {
        this.constraints = null;
        this.contexts = null;
    }

    public BetaNodeConstraints(final BetaNodeFieldConstraint constraint) {
        this( new BetaNodeFieldConstraint[]{constraint} );
    }

    public BetaNodeConstraints(final BetaNodeFieldConstraint[] constraints) {
        this.constraints = new LinkedList();
        ContextEntry current = null;
        for ( int i = 0, length = constraints.length; i < length; i++ ) {
            this.constraints.add( new LinkedListEntry( constraints[i] ) );
            ContextEntry context = constraints[i].getContextEntry();
            if ( current == null ) {
                current = context;
                this.contexts = context;
            } else {
                current.setNext( context );
            }
            current = context;
        }
    }

    public void updateFromTuple(ReteTuple tuple) {
        for ( ContextEntry context = this.contexts; context != null; context = context.getNext() ) {
            context.updateFromTuple( tuple );
        }
    }

    public void updateFromFactHandle(InternalFactHandle handle) {
        for ( ContextEntry context = this.contexts; context != null; context = context.getNext() ) {
            context.updateFromFactHandle( handle );
        }
    }
    
    public boolean isAllowedCachedLeft(Object object ) {       
        if ( this.constraints == null ) {
            return true;
        }

        LinkedListEntry entry = (LinkedListEntry) this.constraints.getFirst();
        ContextEntry context = this.contexts;
        while ( entry != null ) {
            if ( !((BetaNodeFieldConstraint) entry.getObject()).isAllowedCachedLeft(context, object ) ) {
                return false;
            }
            entry = (LinkedListEntry) entry.getNext();
            context = context.getNext();
        }
        return true;        
    }    
    
    public boolean isAllowedCachedRight(ReteTuple tuple) {
        if ( this.constraints == null ) {
            return true;
        }

        LinkedListEntry entry = (LinkedListEntry) this.constraints.getFirst();
        ContextEntry context = this.contexts;
        while ( entry != null ) {
            if ( !((BetaNodeFieldConstraint) entry.getObject()).isAllowedCachedRight(tuple, context ) ) {
                return false;
            }
            entry = (LinkedListEntry) entry.getNext();
            context = context.getNext();
        }
        return true;        
    }          

    //    public Set getRequiredDeclarations() {
    //        final Set declarations = new HashSet();
    //        for ( int i = 0; i < this.constraints.length; i++ ) {
    //            final Declaration[] array = this.constraints[i].getRequiredDeclarations();
    //            for ( int j = 0; j < array.length; j++ ) {
    //                declarations.add( array[j] );
    //            }
    //        }
    //        return declarations;
    //    }

    public int hashCode() {
        return this.constraints.hashCode();
    }

    public LinkedList getConstraints() {
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

        final BetaNodeConstraints other = (BetaNodeConstraints) object;

        if ( this.constraints == other.constraints ) {
            return true;
        }

        if ( this.constraints.size() != other.constraints.size() ) {
            return false;
        }

        return this.constraints.equals( other );
    }

}