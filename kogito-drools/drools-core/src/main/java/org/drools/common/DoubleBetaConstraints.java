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
import java.util.ArrayList;
import java.util.List;

import org.drools.base.evaluators.Operator;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.ContextEntry;
import org.drools.rule.VariableConstraint;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.util.FactHashTable;
import org.drools.util.FieldIndexHashTable;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListEntry;
import org.drools.util.TupleHashTable;
import org.drools.util.FieldIndexHashTable.FieldIndex;

public class DoubleBetaConstraints
    implements
    Serializable,
    BetaConstraints {

    /**
     * 
     */
    private static final long             serialVersionUID = 320L;

    private final BetaNodeFieldConstraint constraint0;
    private final BetaNodeFieldConstraint constraint1;

    private ContextEntry                  context0;
    private ContextEntry                  context1;

    private boolean                       indexed0;
    private boolean                       indexed1;

    public DoubleBetaConstraints(final BetaNodeFieldConstraint[] constraints) {
        final boolean i0 = isIndexable( constraints[0] );
        final boolean i1 = isIndexable( constraints[1] );

        if ( i0 ) {
            this.indexed0 = true;
            if ( i1 ) {
                this.indexed1 = true;
            }
        } else if ( i1 ) {
            this.indexed0 = true;
            final BetaNodeFieldConstraint temp = constraints[0];
            constraints[0] = constraints[1];
            constraints[1] = temp;
        }

        this.constraint0 = constraints[0];
        this.context0 = this.constraint0.getContextEntry();

        this.constraint1 = constraints[1];
        this.context1 = this.constraint1.getContextEntry();
    }

    private boolean isIndexable(final BetaNodeFieldConstraint constraint) {
        if ( constraint.getClass() == VariableConstraint.class ) {
            final VariableConstraint variableConstraint = (VariableConstraint) constraint;
            return (variableConstraint.getEvaluator().getOperator() == Operator.EQUAL);
        } else {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see org.drools.common.BetaNodeConstraints#updateFromTuple(org.drools.reteoo.ReteTuple)
     */
    public void updateFromTuple(final ReteTuple tuple) {
        this.context0.updateFromTuple( tuple );
        this.context1.updateFromTuple( tuple );
    }

    /* (non-Javadoc)
     * @see org.drools.common.BetaNodeConstraints#updateFromFactHandle(org.drools.common.InternalFactHandle)
     */
    public void updateFromFactHandle(final InternalFactHandle handle) {
        this.context0.updateFromFactHandle( handle );
        this.context1.updateFromFactHandle( handle );
    }

    /* (non-Javadoc)
     * @see org.drools.common.BetaNodeConstraints#isAllowedCachedLeft(java.lang.Object)
     */
    public boolean isAllowedCachedLeft(final Object object) {
        return (this.indexed0 || this.constraint0.isAllowedCachedLeft( this.context0,
                                                                       object )) && (this.indexed1 || this.constraint1.isAllowedCachedLeft( this.context1,
                                                                                                                                            object ));
    }

    /* (non-Javadoc)
     * @see org.drools.common.BetaNodeConstraints#isAllowedCachedRight(org.drools.reteoo.ReteTuple)
     */
    public boolean isAllowedCachedRight(final ReteTuple tuple) {
        return this.constraint0.isAllowedCachedRight( tuple,
                                                      this.context0 ) && this.constraint1.isAllowedCachedRight( tuple,
                                                                                                           this.context1 );
    }

    public boolean isIndexed() {
        return this.indexed0;
    }

    public boolean isEmpty() {
        return false;
    }

    public BetaMemory createBetaMemory() {
        BetaMemory memory;

        final List list = new ArrayList( 2 );
        if ( this.indexed0 ) {
            final VariableConstraint variableConstraint = (VariableConstraint) this.constraint0;
            final FieldIndex index = new FieldIndex( variableConstraint.getFieldExtractor(),
                                               variableConstraint.getRequiredDeclarations()[0] );
            list.add( index );

        }

        if ( this.indexed1 ) {
            final VariableConstraint variableConstraint = (VariableConstraint) this.constraint1;
            final FieldIndex index = new FieldIndex( variableConstraint.getFieldExtractor(),
                                               variableConstraint.getRequiredDeclarations()[0] );
            list.add( index );
        }

        if ( !list.isEmpty() ) {
            final FieldIndex[] indexes = (FieldIndex[]) list.toArray( new FieldIndex[list.size()] );
            memory = new BetaMemory( new TupleHashTable(),
                                     new FieldIndexHashTable( indexes ) );
        } else {
            memory = new BetaMemory( new TupleHashTable(),
                                     new FactHashTable() );
        }

        return memory;
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
        return this.constraint0.hashCode() ^ this.constraint0.hashCode();
    }

    /* (non-Javadoc)
     * @see org.drools.common.BetaNodeConstraints#getConstraints()
     */
    public LinkedList getConstraints() {
        final LinkedList list = new LinkedList();
        list.add( new LinkedListEntry( this.constraint0 ) );
        list.add( new LinkedListEntry( this.constraint1 ) );
        return list;
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

        final DoubleBetaConstraints other = (DoubleBetaConstraints) object;

        if ( this.constraint0 != other.constraint0 && this.constraint0.equals( other.constraint0 ) ) {
            return false;
        }

        if ( this.constraint1 != other.constraint1 && this.constraint1.equals( other.constraint1 ) ) {
            return false;
        }

        return true;
    }

}