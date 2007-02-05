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

import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.base.evaluators.Operator;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.FactHandleMemory;
import org.drools.reteoo.ReteTuple;
import org.drools.reteoo.TupleMemory;
import org.drools.rule.ContextEntry;
import org.drools.rule.VariableConstraint;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.util.FactHashTable;
import org.drools.util.FactHandleIndexHashTable;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListEntry;
import org.drools.util.TupleHashTable;
import org.drools.util.TupleIndexHashTable;
import org.drools.util.AbstractHashTable.FieldIndex;

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
    
    private RuleBaseConfiguration         conf;

    public SingleBetaConstraints(final BetaNodeFieldConstraint constraint,
                                 RuleBaseConfiguration conf) {
        this.conf = conf;
        if ( !conf.isIndexLeftBetaMemory() && !conf.isIndexRightBetaMemory() ) {
            this.indexed = false;
        } else {
            int depth = conf.getCompositeKeyDepth();
            // Determine  if this constraint is indexable
            this.indexed = depth >= 1 && isIndexable( constraint );
        }

        this.constraint = constraint;
        this.context = constraint.getContextEntry();
    }

    private boolean isIndexable(final BetaNodeFieldConstraint constraint) {
        if ( constraint instanceof VariableConstraint ) {
            final VariableConstraint variableConstraint = (VariableConstraint) constraint;
            return (variableConstraint.getEvaluator().getOperator() == Operator.EQUAL);
        } else {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see org.drools.common.BetaNodeConstraints#updateFromTuple(org.drools.reteoo.ReteTuple)
     */
    public void updateFromTuple(final InternalWorkingMemory workingMemory,
                                final ReteTuple tuple) {
        this.context.updateFromTuple( workingMemory,
                                      tuple );
    }

    /* (non-Javadoc)
     * @see org.drools.common.BetaNodeConstraints#updateFromFactHandle(org.drools.common.InternalFactHandle)
     */
    public void updateFromFactHandle(final InternalWorkingMemory workingMemory,
                                     final InternalFactHandle handle) {
        this.context.updateFromFactHandle( workingMemory,
                                           handle );
    }

    /* (non-Javadoc)
     * @see org.drools.common.BetaNodeConstraints#isAllowedCachedLeft(java.lang.Object)
     */
    public boolean isAllowedCachedLeft(final Object object) {
        return this.indexed || this.constraint.isAllowedCachedLeft( this.context,
                                                                    object );
    }

    /* (non-Javadoc)
     * @see org.drools.common.BetaNodeConstraints#isAllowedCachedRight(org.drools.reteoo.ReteTuple)
     */
    public boolean isAllowedCachedRight(final ReteTuple tuple) {
        return this.constraint.isAllowedCachedRight( tuple,
                                                     this.context );
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
            final VariableConstraint variableConstraint = (VariableConstraint) this.constraint;
            final FieldIndex index = new FieldIndex( variableConstraint.getFieldExtractor(),
                                                     variableConstraint.getRequiredDeclarations()[0],
                                                     variableConstraint.getEvaluator() );
            TupleMemory tupleMemory;
            if ( conf.isIndexLeftBetaMemory() ) {
                tupleMemory = new TupleIndexHashTable( new FieldIndex[]{index} );
            } else {
                tupleMemory = new TupleHashTable();
            }

            FactHandleMemory factHandleMemory;
            if ( conf.isIndexRightBetaMemory() ) {
                factHandleMemory = new FactHandleIndexHashTable( new FieldIndex[]{index} );           
            }  else {
                factHandleMemory = new FactHashTable();
            }
            memory = new BetaMemory( tupleMemory,
                                     factHandleMemory );
        } else {
            memory = new BetaMemory( new TupleHashTable(),
                                     new FactHashTable() );
        }

        return memory;
    }

    public int hashCode() {
        return this.constraint.hashCode();
    }

    /* (non-Javadoc)
     * @see org.drools.common.BetaNodeConstraints#getConstraints()
     */
    public LinkedList getConstraints() {
        final LinkedList list = new LinkedList();
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