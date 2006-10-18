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
import org.drools.base.evaluators.Operator;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.ContextEntry;
import org.drools.rule.VariableConstraint;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.util.CompositeFieldIndexHashTable;
import org.drools.util.FactHashTable;
import org.drools.util.FieldIndexHashTable;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListEntry;
import org.drools.util.TupleHashTable;
import org.drools.util.CompositeFieldIndexHashTable.FieldIndex;

public class SingleBetaConstraints
    implements
    Serializable,
    BetaConstraints {

    /**
     * 
     */
    private static final long             serialVersionUID = 320L;

    private final BetaNodeFieldConstraint constraint;

    private ContextEntry                  context;

    private boolean                       indexed;

    public SingleBetaConstraints(final BetaNodeFieldConstraint constraint) {
        this.constraint = constraint;
        // Determine  if this constraint is indexable
        if ( isIndexable( constraint ) ) {
            this.indexed = true;
        }

        this.context = constraint.getContextEntry();
    }

    private boolean isIndexable(final BetaNodeFieldConstraint constraint) {
        if ( constraint.getClass() == VariableConstraint.class ) {
            VariableConstraint variableConstraint = (VariableConstraint) constraint;
            return (variableConstraint.getEvaluator().getOperator() == Operator.EQUAL);
        } else {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see org.drools.common.BetaNodeConstraints#updateFromTuple(org.drools.reteoo.ReteTuple)
     */
    public void updateFromTuple(ReteTuple tuple) {
        context.updateFromTuple( tuple );
    }

    /* (non-Javadoc)
     * @see org.drools.common.BetaNodeConstraints#updateFromFactHandle(org.drools.common.InternalFactHandle)
     */
    public void updateFromFactHandle(InternalFactHandle handle) {
        context.updateFromFactHandle( handle );
    }

    /* (non-Javadoc)
     * @see org.drools.common.BetaNodeConstraints#isAllowedCachedLeft(java.lang.Object)
     */
    public boolean isAllowedCachedLeft(Object object) {
        return this.indexed || this.constraint.isAllowedCachedLeft( context,
                                                                    object );
    }

    /* (non-Javadoc)
     * @see org.drools.common.BetaNodeConstraints#isAllowedCachedRight(org.drools.reteoo.ReteTuple)
     */
    public boolean isAllowedCachedRight(ReteTuple tuple) {
        return this.constraint.isAllowedCachedRight( tuple,
                                                     context );
    }

    public boolean isIndexed() {
        return this.indexed;
    }

    public boolean isEmpty() {
        return false;
    }

    public BetaMemory createBetaMemory() {
        BetaMemory memory;
        if ( this.indexed ) {
            VariableConstraint variableConstraint = (VariableConstraint) this.constraint;
            FieldIndex  index  = new  FieldIndex(variableConstraint.getFieldExtractor(), variableConstraint.getRequiredDeclarations()[0]);
            memory = new BetaMemory( new TupleHashTable(),
                                     new CompositeFieldIndexHashTable( new FieldIndex[] { index }  ) );
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
        return this.constraint.hashCode();
    }

    /* (non-Javadoc)
     * @see org.drools.common.BetaNodeConstraints#getConstraints()
     */
    public LinkedList getConstraints() {
        LinkedList list = new LinkedList();
        list.add( new LinkedListEntry( this.constraint ) );
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

        final SingleBetaConstraints other = (SingleBetaConstraints) object;

        return this.constraint == other.constraint || this.constraint.equals( other );
    }

}