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

package org.drools.common;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import org.drools.RuleBaseConfiguration;
import org.drools.core.util.LeftTupleIndexHashTable;
import org.drools.core.util.LeftTupleList;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListEntry;
import org.drools.core.util.RightTupleIndexHashTable;
import org.drools.core.util.RightTupleList;
import org.drools.core.util.AbstractHashTable.FieldIndex;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleMemory;
import org.drools.reteoo.RightTupleMemory;
import org.drools.rule.ContextEntry;
import org.drools.rule.IndexableConstraint;
import org.drools.rule.constraint.MvelConstraint;
import org.drools.spi.BetaNodeFieldConstraint;

public class DoubleBetaConstraints
    implements
    BetaConstraints {

    private static final long             serialVersionUID = 510l;

    private BetaNodeFieldConstraint constraint0;
    private BetaNodeFieldConstraint constraint1;

    private boolean                       indexed0;
    private boolean                       indexed1;

    public DoubleBetaConstraints() {

    }
    public DoubleBetaConstraints(final BetaNodeFieldConstraint[] constraints,
                                 final RuleBaseConfiguration conf) {
        this( constraints,
              conf,
              false );
    }

    public DoubleBetaConstraints(final BetaNodeFieldConstraint[] constraints,
                                 final RuleBaseConfiguration conf,
                                 final boolean disableIndexing) {
        if ( disableIndexing || (!conf.isIndexLeftBetaMemory() && !conf.isIndexRightBetaMemory()) ) {
            this.indexed0 = false;
            this.indexed1 = false;
        } else {
            int depth = conf.getCompositeKeyDepth();
            if ( !DefaultBetaConstraints.compositeAllowed(constraints) ) {
                // UnificationRestrictions cannot be allowed in composite indexes
                // We also ensure that if there is a mixture that standard restriction is first
                depth = 1;
            }

            // Determine  if this constraints are indexable
            final boolean i0 = isIndexable( constraints[0] );
            final boolean i1 = isIndexable( constraints[1] );

            if ( depth >= 1 && i0 ) {
                this.indexed0 = true;
            }

            if ( i1 ) {
                if ( depth >= 1 && !i0 ) {
                    this.indexed0 = true;
                    swap( constraints,
                          1,
                          0 );
                } else if ( depth >= 2 ) {
                    this.indexed1 = true;
                }
            }
        }

        this.constraint0 = constraints[0];
        this.constraint1 = constraints[1];
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        constraint0 = (BetaNodeFieldConstraint)in.readObject();
        constraint1 = (BetaNodeFieldConstraint)in.readObject();
        indexed0    = in.readBoolean();
        indexed1    = in.readBoolean();

    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(constraint0);
        out.writeObject(constraint1);
        out.writeBoolean(indexed0);
        out.writeBoolean(indexed1);

    }
    private void swap(final BetaNodeFieldConstraint[] constraints,
                      final int p1,
                      final int p2) {
        final BetaNodeFieldConstraint temp = constraints[p2];
        constraints[p2] = constraints[p1];
        constraints[p1] = temp;
    }

    private boolean isIndexable(final BetaNodeFieldConstraint constraint) {
        return DefaultBetaConstraints.isIndexable( constraint );
    }

    /* (non-Javadoc)
     * @see org.drools.common.BetaNodeConstraints#updateFromTuple(org.drools.reteoo.ReteTuple)
     */
    public void updateFromTuple(final ContextEntry[] context,
                                final InternalWorkingMemory workingMemory,
                                final LeftTuple tuple) {
        context[0].updateFromTuple( workingMemory,
                                    tuple );
        context[1].updateFromTuple( workingMemory,
                                    tuple );
    }

    /* (non-Javadoc)
     * @see org.drools.common.BetaNodeConstraints#updateFromFactHandle(org.drools.common.InternalFactHandle)
     */
    public void updateFromFactHandle(final ContextEntry[] context,
                                     final InternalWorkingMemory workingMemory,
                                     final InternalFactHandle handle) {
        context[0].updateFromFactHandle( workingMemory,
                                         handle );
        context[1].updateFromFactHandle( workingMemory,
                                         handle );
    }

    public void resetTuple(final ContextEntry[] context) {
        context[0].resetTuple();
        context[1].resetTuple();
    }

    public void resetFactHandle(final ContextEntry[] context) {
        context[0].resetFactHandle();
        context[1].resetFactHandle();
    }

    /* (non-Javadoc)
     * @see org.drools.common.BetaNodeConstraints#isAllowedCachedLeft(java.lang.Object)
     */
    public boolean isAllowedCachedLeft(final ContextEntry[] context,
                                       final InternalFactHandle handle) {
        return (this.indexed0 || this.constraint0.isAllowedCachedLeft( context[0],
                                                                       handle )) && (this.indexed1 || this.constraint1.isAllowedCachedLeft( context[1],
                                                                                                                                            handle ));
    }

    /* (non-Javadoc)
     * @see org.drools.common.BetaNodeConstraints#isAllowedCachedRight(org.drools.reteoo.ReteTuple)
     */
    public boolean isAllowedCachedRight(final ContextEntry[] context,
                                        final LeftTuple tuple) {
        return this.constraint0.isAllowedCachedRight( tuple,
                                                      context[0] ) && this.constraint1.isAllowedCachedRight( tuple,
                                                                                                             context[1] );
    }

    public boolean isIndexed() {
        return this.indexed0;
    }

    public int getIndexCount() {
        int count = 0;
        if ( this.indexed0 ) {
            count++;
        }
        if ( this.indexed1 ) {
            count++;
        }
        return count;
    }

    public boolean isEmpty() {
        return false;
    }

    public BetaMemory createBetaMemory(final RuleBaseConfiguration config) {
        BetaMemory memory;

        final List<FieldIndex> list = new ArrayList<FieldIndex>( 2 );
        if ( this.indexed0 ) {
            final IndexableConstraint indexableConstraint = (IndexableConstraint) this.constraint0;
            final FieldIndex index = indexableConstraint.getFieldIndex();
            list.add( index );
        }

        if ( this.indexed1 ) {
            final IndexableConstraint indexableConstraint = (IndexableConstraint) this.constraint1;
            final FieldIndex index = indexableConstraint.getFieldIndex();
            list.add( index );
        }

        if ( !list.isEmpty() ) {
            final FieldIndex[] indexes = list.toArray( new FieldIndex[list.size()] );

            LeftTupleMemory tupleMemory;
            if ( config.isIndexLeftBetaMemory() ) {
                tupleMemory = new LeftTupleIndexHashTable( indexes );
            } else {
                tupleMemory = new LeftTupleList();
            }

            RightTupleMemory factHandleMemory;
            if ( config.isIndexRightBetaMemory() ) {
                factHandleMemory = new RightTupleIndexHashTable( indexes );
            } else {
                factHandleMemory = new RightTupleList();
            }
            memory = new BetaMemory( config.isSequential() ? null : tupleMemory,
                                     factHandleMemory,
                                     this.createContext() );
        } else {
            memory = new BetaMemory( config.isSequential() ? null : new LeftTupleList(),
                                     new RightTupleList(),
                                     this.createContext() );
        }

        return memory;
    }

    public int hashCode() {
        return this.constraint0.hashCode() ^ this.constraint1.hashCode();
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

        if ( object == null || !(object instanceof DoubleBetaConstraints) ) {
            return false;
        }

        final DoubleBetaConstraints other = (DoubleBetaConstraints) object;

        if ( this.constraint0 != other.constraint0 && !this.constraint0.equals( other.constraint0 ) ) {
            return false;
        }

        if ( this.constraint1 != other.constraint1 && !this.constraint1.equals( other.constraint1 ) ) {
            return false;
        }

        return true;
    }

    public ContextEntry[] createContext() {
        return new ContextEntry[]{this.constraint0.createContextEntry(), this.constraint1.createContextEntry()};
    }
    
    public BetaConstraints getOriginalConstraint() {
        throw new UnsupportedOperationException();
    }

    public long getListenedPropertyMask(List<String> settableProperties) {
        if (constraint0 instanceof MvelConstraint && constraint1 instanceof MvelConstraint) {
            return ((MvelConstraint)constraint0).getListenedPropertyMask(settableProperties) |
                    ((MvelConstraint)constraint1).getListenedPropertyMask(settableProperties);
        }
        return Long.MAX_VALUE;
    }
}
