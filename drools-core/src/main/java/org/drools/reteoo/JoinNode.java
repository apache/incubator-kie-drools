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

package org.drools.reteoo;

import org.drools.base.DroolsQuery;
import org.drools.common.BetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.SingleBetaConstraints;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListEntry;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Behavior;
import org.drools.rule.ContextEntry;
import org.drools.rule.MutableTypeConstraint;
import org.drools.rule.UnificationRestriction;
import org.drools.rule.VariableConstraint;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.PropagationContext;

public class JoinNode extends BetaNode {

    private static final long serialVersionUID = 510l;

    public JoinNode() {

    }

    public JoinNode(final int id,
                    final LeftTupleSource leftInput,
                    final ObjectSource rightInput,
                    final BetaConstraints binder,
                    final Behavior[] behaviors,
                    final BuildContext context) {
        super( id,
               context.getPartitionId(),
               context.getRuleBase().getConfiguration().isMultithreadEvaluation(),
               leftInput,
               rightInput,
               binder,
               behaviors );
        this.tupleMemoryEnabled = context.isTupleMemoryEnabled();
    }

    public void assertLeftTuple(final LeftTuple leftTuple,
                                final PropagationContext context,
                                final InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        memory.setOpen( true );
        
        RightTupleMemory rightMemory = memory.getRightTupleMemory();

        ContextEntry[] contextEntry = memory.getContext();
        
        boolean useLeftMemory = true;
        if ( this.tupleMemoryEnabled ) {
            memory.getLeftTupleMemory().add( leftTuple );
        } else {
            if ( memory.isOpen() ) {
                // we are re-entrant to force new ContextEntry
                contextEntry = this.constraints.createContext();
            }
            // This is a hack, to not add closed DroolsQuery objects
            Object object = ((InternalFactHandle) context.getFactHandle()).getObject();
            if ( object instanceof DroolsQuery && !((DroolsQuery) object).isOpen() ) {
                useLeftMemory = false;
            } else if ( memory.getLeftTupleMemory() != null ) {
                // LeftMemory will be null for sequential (still created for queries).
                memory.getLeftTupleMemory().add( leftTuple );
            }
        }

        this.constraints.updateFromTuple(contextEntry,
                                          workingMemory,
                                          leftTuple );
        
                
        FastIterator it = getRightIterator( rightMemory);
        
        for ( RightTuple rightTuple = getFirstRightTuple(leftTuple, rightMemory, context, it); rightTuple != null; rightTuple = (RightTuple) it.next(rightTuple)) {
            final InternalFactHandle handle = rightTuple.getFactHandle();
            if ( this.constraints.isAllowedCachedLeft( contextEntry,
                                                       handle ) ) {
                this.sink.propagateAssertLeftTuple( leftTuple,
                                                    rightTuple,
                                                    null,
                                                    null,
                                                    context,
                                                    workingMemory,
                                                    useLeftMemory );
            }
        }

        this.constraints.resetTuple( contextEntry );
        memory.setOpen( false );
    }
  

    public void assertObject(final InternalFactHandle factHandle,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        RightTuple rightTuple = createRightTuple( factHandle,
                                                  this );

        if ( !behavior.assertRightTuple( memory.getBehaviorContext(),
                                         rightTuple,
                                         workingMemory ) ) {
            // destroy right tuple
            rightTuple.unlinkFromRightParent();
            return;
        }

        memory.getRightTupleMemory().add( rightTuple );
        if ( memory.getLeftTupleMemory() == null || memory.getLeftTupleMemory().size() == 0 ) {
            // do nothing here, as no left memory
            return;
        }

        this.constraints.updateFromFactHandle( memory.getContext(),
                                               workingMemory,
                                               factHandle );
        
        FastIterator it = memory.getLeftTupleMemory().fastIterator();
        for ( LeftTuple leftTuple = memory.getLeftTupleMemory().getFirst( rightTuple ); leftTuple != null; leftTuple = (LeftTuple) it.next(leftTuple) ) {
            if ( this.constraints.isAllowedCachedRight( memory.getContext(),
                                                        leftTuple ) ) {
                // wm.marshaller.write( i, leftTuple )
                this.sink.propagateAssertLeftTuple( leftTuple,
                                                    rightTuple,
                                                    null,
                                                    null,
                                                    context,
                                                    workingMemory,
                                                    true );
            }
        }
        this.constraints.resetFactHandle( memory.getContext() );
    }

    public void retractRightTuple(final RightTuple rightTuple,
                                  final PropagationContext context,
                                  final InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        behavior.retractRightTuple( memory.getBehaviorContext(),
                                    rightTuple,
                                    workingMemory );
        memory.getRightTupleMemory().remove( rightTuple );

        if ( rightTuple.firstChild != null ) {
            this.sink.propagateRetractRightTuple( rightTuple,
                                                  context,
                                                  workingMemory );
        }
    }

    public void retractLeftTuple(final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        memory.getLeftTupleMemory().remove( leftTuple );
        if ( leftTuple.firstChild != null ) {
            this.sink.propagateRetractLeftTuple( leftTuple,
                                                 context,
                                                 workingMemory );
        }
    }

    public void modifyRightTuple(final RightTuple rightTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        // WTD here
        //                if ( !behavior.assertRightTuple( memory.getBehaviorContext(),
        //                                                 rightTuple,
        //                                                 workingMemory ) ) {
        //                    // destroy right tuple
        //                    rightTuple.unlinkFromRightParent();
        //                    return;
        //                }

        // Add and remove to make sure we are in the right bucket and at the end
        // this is needed to fix for indexing and deterministic iteration
        memory.getRightTupleMemory().remove( rightTuple );
        memory.getRightTupleMemory().add( rightTuple );

        if ( memory.getLeftTupleMemory() != null && memory.getLeftTupleMemory().size() == 0 ) {
            // do nothing here, as we know there are no left tuples.
            return;
        }

        LeftTuple childLeftTuple = rightTuple.firstChild;

        LeftTupleMemory leftMemory = memory.getLeftTupleMemory();

        FastIterator it = leftMemory.fastIterator();
        LeftTuple leftTuple = leftMemory.getFirst( rightTuple );

        this.constraints.updateFromFactHandle( memory.getContext(),
                                               workingMemory,
                                               rightTuple.getFactHandle() );

        // first check our index (for indexed nodes only) hasn't changed and we are returning the same bucket
        if ( childLeftTuple != null && leftMemory.isIndexed() && (leftTuple == null || (leftTuple.getMemory() != childLeftTuple.getLeftParent().getMemory())) ) {
            // our index has changed, so delete all the previous propagations
            this.sink.propagateRetractRightTuple( rightTuple,
                                                  context,
                                                  workingMemory );

            childLeftTuple = null; // null so the next check will attempt matches for new bucket
        }

        // we can't do anything if LeftTupleMemory is empty
        if ( leftTuple != null ) {
            if ( childLeftTuple == null ) {
                // either we are indexed and changed buckets or
                // we had no children before, but there is a bucket to potentially match, so try as normal assert
                for ( ; leftTuple != null; leftTuple = (LeftTuple) it.next(leftTuple) ) {
                    if ( this.constraints.isAllowedCachedRight( memory.getContext(),
                                                                leftTuple ) ) {
                        this.sink.propagateAssertLeftTuple( leftTuple,
                                                            rightTuple,
                                                            null,
                                                            null,
                                                            context,
                                                            workingMemory,
                                                            true );
                    }
                }
            } else {
                // in the same bucket, so iterate and compare
                for ( ; leftTuple != null; leftTuple = (LeftTuple) it.next(leftTuple) ) {
                    if ( this.constraints.isAllowedCachedRight( memory.getContext(),
                                                                leftTuple ) ) {
                        if ( childLeftTuple == null || childLeftTuple.getLeftParent() != leftTuple ) {
                            this.sink.propagateAssertLeftTuple( leftTuple,
                                                                rightTuple,
                                                                null,
                                                                childLeftTuple,
                                                                context,
                                                                workingMemory,
                                                                true );
                        } else {
                            childLeftTuple = this.sink.propagateModifyChildLeftTuple( childLeftTuple,
                                                                                      leftTuple,
                                                                                      context,
                                                                                      workingMemory,
                                                                                      true );
                        }
                    } else if ( childLeftTuple != null && childLeftTuple.getLeftParent() == leftTuple ) {
                        childLeftTuple = this.sink.propagateRetractChildLeftTuple( childLeftTuple,
                                                                                   leftTuple,
                                                                                   context,
                                                                                   workingMemory );
                    }
                    // else do nothing, was false before and false now.
                }
            }
        }

        this.constraints.resetFactHandle( memory.getContext() );
    }

    public void modifyLeftTuple(final LeftTuple leftTuple,
                                final PropagationContext context,
                                final InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        // Add and remove to make sure we are in the right bucket and at the end
        // this is needed to fix for indexing and deterministic iteration
        memory.getLeftTupleMemory().remove( leftTuple );
        memory.getLeftTupleMemory().add( leftTuple );

        this.constraints.updateFromTuple( memory.getContext(),
                                          workingMemory,
                                          leftTuple );
        LeftTuple childLeftTuple = leftTuple.firstChild;

        RightTupleMemory rightMemory = memory.getRightTupleMemory();

        FastIterator it = rightMemory.fastIterator();
        RightTuple rightTuple = rightMemory.getFirst( leftTuple,
                                                      (InternalFactHandle) context.getFactHandle() );

        // first check our index (for indexed nodes only) hasn't changed and we are returning the same bucket
        if ( childLeftTuple != null && rightMemory.isIndexed() && (rightTuple == null || (rightTuple.getMemory() != childLeftTuple.getRightParent().getMemory())) ) {
            // our index has changed, so delete all the previous propagations
            this.sink.propagateRetractLeftTuple( leftTuple,
                                                 context,
                                                 workingMemory );

            childLeftTuple = null; // null so the next check will attempt matches for new bucket
        }

        // we can't do anything if RightTupleMemory is empty
        if ( rightTuple != null ) {
            if ( childLeftTuple == null ) {
                // either we are indexed and changed buckets or
                // we had no children before, but there is a bucket to potentially match, so try as normal assert
                for ( ; rightTuple != null; rightTuple = (RightTuple) it.next( rightTuple ) ) {
                    final InternalFactHandle handle = rightTuple.getFactHandle();
                    if ( this.constraints.isAllowedCachedLeft( memory.getContext(),
                                                               handle ) ) {
                        this.sink.propagateAssertLeftTuple( leftTuple,
                                                            rightTuple,
                                                            null,
                                                            null,
                                                            context,
                                                            workingMemory,
                                                            true );
                    }
                }
            } else {
                // in the same bucket, so iterate and compare
                for ( ; rightTuple != null; rightTuple = (RightTuple) it.next( rightTuple )  ) {
                    final InternalFactHandle handle = rightTuple.getFactHandle();

                    if ( this.constraints.isAllowedCachedLeft( memory.getContext(),
                                                               handle ) ) {
                        if ( childLeftTuple == null || childLeftTuple.getRightParent() != rightTuple ) {
                            this.sink.propagateAssertLeftTuple( leftTuple,
                                                                rightTuple,
                                                                childLeftTuple,
                                                                null,
                                                                context,
                                                                workingMemory,
                                                                true );
                        } else {
                            childLeftTuple = this.sink.propagateModifyChildLeftTuple( childLeftTuple,
                                                                                      rightTuple,
                                                                                      context,
                                                                                      workingMemory,
                                                                                      true );
                        }
                    } else if ( childLeftTuple != null && childLeftTuple.getRightParent() == rightTuple ) {
                        childLeftTuple = this.sink.propagateRetractChildLeftTuple( childLeftTuple,
                                                                                   rightTuple,
                                                                                   context,
                                                                                   workingMemory );
                    }
                    // else do nothing, was false before and false now.
                }
            }
        }

        this.constraints.resetTuple( memory.getContext() );
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.BaseNode#updateNewNode(org.drools.reteoo.WorkingMemoryImpl, org.drools.spi.PropagationContext)
     */
    public void updateSink(final LeftTupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {

        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        FastIterator it = memory.getLeftTupleMemory().fastIterator();
        
        final Iterator tupleIter = memory.getLeftTupleMemory().iterator();
        for ( LeftTuple leftTuple = (LeftTuple) tupleIter.next(); leftTuple != null; leftTuple = (LeftTuple) tupleIter.next() ) {
            this.constraints.updateFromTuple( memory.getContext(),
                                              workingMemory,
                                              leftTuple );
            for ( RightTuple rightTuple = memory.getRightTupleMemory().getFirst( leftTuple,
                                                                                 (InternalFactHandle) context.getFactHandle() ); rightTuple != null; rightTuple = (RightTuple) it.next( rightTuple ) ) {
                if ( this.constraints.isAllowedCachedLeft( memory.getContext(),
                                                           rightTuple.getFactHandle() ) ) {
                    sink.assertLeftTuple( new LeftTuple( leftTuple,
                                                         rightTuple,
                                                         null,
                                                         null,
                                                         sink,
                                                         true ),
                                          context,
                                          workingMemory );
                }
            }

            this.constraints.resetTuple( memory.getContext() );
        }
    }

    public short getType() {
        return NodeTypeEnums.JoinNode;
    }

    public String toString() {
        ObjectSource source = this.rightInput;
        while ( !(source instanceof ObjectTypeNode) ) {
            source = source.source;
        }

        return "[JoinNode(" + this.getId() + ") - " + ((ObjectTypeNode) source).getObjectType() + "]";
    }
}
