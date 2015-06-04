package org.drools.reteoo.nodes;

import org.drools.core.base.DroolsQuery;
import org.drools.core.common.BetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleMemory;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.LeftTupleSourceUtils;
import org.drools.core.reteoo.ModifyPreviousTuples;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ReteooBuilder;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RightTupleMemory;
import org.drools.core.reteoo.RuleRemovalContext;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.ContextEntry;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;

public class ReteJoinNode extends JoinNode {

    public ReteJoinNode() {
    }

    public ReteJoinNode(int id, LeftTupleSource leftInput, ObjectSource rightInput, BetaConstraints binder, BuildContext context) {
        super(id, leftInput, rightInput, binder, context);
    }

    public void assertObject( final InternalFactHandle factHandle,
                              final PropagationContext pctx,
                              final InternalWorkingMemory wm ) {
        ReteBetaNodeUtils.assertObject(this, factHandle, pctx, wm);
    }

    public void attach(BuildContext context) {
        ReteBetaNodeUtils.attach(this, context);
    }

    public void doRemove(RuleRemovalContext context, ReteooBuilder builder, InternalWorkingMemory[] workingMemories) {
        ReteBetaNodeUtils.doRemove(this, context, builder, workingMemories);
    }

    public void modifyObject(InternalFactHandle factHandle, ModifyPreviousTuples modifyPreviousTuples, PropagationContext context, InternalWorkingMemory workingMemory) {
        ReteBetaNodeUtils.modifyObject(this, factHandle, modifyPreviousTuples, context, workingMemory);
    }

    public void modifyLeftTuple(InternalFactHandle factHandle,
                                ModifyPreviousTuples modifyPreviousTuples,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        LeftTupleSourceUtils.doModifyLeftTuple(factHandle, modifyPreviousTuples, context, workingMemory,
                                               (LeftTupleSink) this, getLeftInputOtnId(), getLeftInferredMask());
    }

    public void assertLeftTuple( final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory ) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        RightTupleMemory rightMemory = memory.getRightTupleMemory();

        ContextEntry[] contextEntry = memory.getContext();
        boolean useLeftMemory = true;

        if ( !this.tupleMemoryEnabled ) {
            // This is a hack, to not add closed DroolsQuery objects
            Object object = ((InternalFactHandle) leftTuple.get( 0 )).getObject();
            if ( !(object instanceof DroolsQuery) || !((DroolsQuery) object).isOpen() ) {
                useLeftMemory = false;
            }
        }

        if ( useLeftMemory ) {
            memory.getLeftTupleMemory().add( leftTuple );
        }

        this.constraints.updateFromTuple( contextEntry,
                                          workingMemory,
                                          leftTuple );

        FastIterator it = getRightIterator( rightMemory );

        for ( RightTuple rightTuple = getFirstRightTuple( leftTuple,
                                                          rightMemory,
                                                          (InternalFactHandle) context.getFactHandle(),
                                                          it ); rightTuple != null; rightTuple = (RightTuple) it.next( rightTuple ) ) {

            propagateFromLeft( rightTuple, leftTuple, contextEntry, useLeftMemory, context, workingMemory );
        }


        this.constraints.resetTuple( contextEntry );
    }

    public void assertRightTuple( final RightTuple rightTuple,
                                  final PropagationContext context,
                                  final InternalWorkingMemory workingMemory ) {
        final BetaMemory memory = (BetaMemory)  workingMemory.getNodeMemory( this );;

        memory.getRightTupleMemory().add( rightTuple );
        if ( memory.getLeftTupleMemory() == null || memory.getLeftTupleMemory().size() == 0 ) {
            // do nothing here, as no left memory
            return;
        }

        LeftTupleMemory leftMemory = memory.getLeftTupleMemory();

        this.constraints.updateFromFactHandle( memory.getContext(),
                                               workingMemory,
                                               rightTuple.getFactHandle() );

        FastIterator it = getLeftIterator( leftMemory );
        for ( LeftTuple leftTuple = getFirstLeftTuple( rightTuple, leftMemory, it ); leftTuple != null; leftTuple = (LeftTuple) it.next( leftTuple ) ) {
            propagateFromRight( rightTuple, leftTuple, memory, context, workingMemory );
        }

        this.constraints.resetFactHandle( memory.getContext() );
    }


    protected void propagateFromRight( RightTuple rightTuple, LeftTuple leftTuple, BetaMemory memory, PropagationContext context, InternalWorkingMemory workingMemory ) {
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

    protected void propagateFromLeft( RightTuple rightTuple, LeftTuple leftTuple, ContextEntry[] contextEntry, boolean useLeftMemory, PropagationContext context, InternalWorkingMemory workingMemory ) {
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


    public void retractRightTuple( final RightTuple rightTuple,
                                   final PropagationContext pctx,
                                   final InternalWorkingMemory wm ) {
        final BetaMemory memory = (BetaMemory) wm.getNodeMemory( this );

        memory.getRightTupleMemory().remove( rightTuple );

        this.sink.propagateRetractRightTuple( rightTuple,
                                              pctx,
                                              wm );
    }

    public void retractLeftTuple( final LeftTuple leftTuple,
                                  final PropagationContext context,
                                  final InternalWorkingMemory workingMemory ) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );


        memory.getLeftTupleMemory().remove( leftTuple );

        if ( leftTuple.getFirstChild() != null ) {
            this.sink.propagateRetractLeftTuple( leftTuple,
                                                 context,
                                                 workingMemory );
        }
    }

    public void modifyRightTuple( final RightTuple rightTuple,
                                  final PropagationContext context,
                                  final InternalWorkingMemory workingMemory ) {
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
        memory.getRightTupleMemory().removeAdd( rightTuple );

        if ( memory.getLeftTupleMemory() != null && memory.getLeftTupleMemory().size() == 0 ) {
            // do nothing here, as we know there are no left tuples.
            return;
        }

        LeftTuple childLeftTuple = rightTuple.firstChild;

        LeftTupleMemory leftMemory = memory.getLeftTupleMemory();


        FastIterator it = getLeftIterator( leftMemory );
        LeftTuple leftTuple = getFirstLeftTuple( rightTuple, leftMemory, it );

        this.constraints.updateFromFactHandle( memory.getContext(),
                                               workingMemory,
                                               rightTuple.getFactHandle() );

        // first check our index (for indexed nodes only) hasn't changed and we are returning the same bucket
        // We assume a bucket change if leftTuple == null
        if ( childLeftTuple != null && leftMemory.isIndexed() && !it.isFullIterator() && (leftTuple == null || (leftTuple.getMemory() != childLeftTuple.getLeftParent().getMemory())) ) {
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
                for ( ; leftTuple != null; leftTuple = (LeftTuple) it.next( leftTuple ) ) {
                    propagateFromRight( rightTuple, leftTuple, memory, context, workingMemory );
                }
            } else {
                // in the same bucket, so iterate and compare
                for ( ; leftTuple != null; leftTuple = (LeftTuple) it.next( leftTuple ) ) {
                    childLeftTuple = propagateOrModifyFromRight( rightTuple, leftTuple, childLeftTuple, memory, context, workingMemory );
                }
            }
        }

        this.constraints.resetFactHandle( memory.getContext() );
    }

    protected LeftTuple propagateOrModifyFromRight( RightTuple rightTuple, LeftTuple leftTuple, LeftTuple childLeftTuple, BetaMemory memory, PropagationContext context, InternalWorkingMemory workingMemory ) {
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
        return childLeftTuple;
    }

    public void modifyLeftTuple( final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory ) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        ContextEntry[] contextEntry = memory.getContext();

        // Add and remove to make sure we are in the right bucket and at the end
        // this is needed to fix for indexing and deterministic iteration
        memory.getLeftTupleMemory().removeAdd( leftTuple );

        this.constraints.updateFromTuple( contextEntry,
                                          workingMemory,
                                          leftTuple );
        LeftTuple childLeftTuple = leftTuple.getFirstChild();

        RightTupleMemory rightMemory = memory.getRightTupleMemory();

        FastIterator it = getRightIterator( rightMemory );

        RightTuple rightTuple = getFirstRightTuple( leftTuple,
                                                    rightMemory,
                                                    (InternalFactHandle) context.getFactHandle(),
                                                    it );

        // first check our index (for indexed nodes only) hasn't changed and we are returning the same bucket
        // if rightTuple is null, we assume there was a bucket change and that bucket is empty
        if ( childLeftTuple != null && rightMemory.isIndexed() && !it.isFullIterator() && (rightTuple == null || (rightTuple.getMemory() != childLeftTuple.getRightParent().getMemory())) ) {
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
                    propagateFromLeft( rightTuple, leftTuple, contextEntry, true, context, workingMemory );
                }
            } else {
                // in the same bucket, so iterate and compare
                for ( ; rightTuple != null; rightTuple = (RightTuple) it.next( rightTuple ) ) {
                    childLeftTuple = propagateOrModifyFromLeft( rightTuple, leftTuple, childLeftTuple, contextEntry, context, workingMemory );
                }
            }
        }

        this.constraints.resetTuple( contextEntry );
    }

    protected LeftTuple propagateOrModifyFromLeft( RightTuple rightTuple,
                                                   LeftTuple leftTuple,
                                                   LeftTuple childLeftTuple,
                                                   ContextEntry[] contextEntry,
                                                   PropagationContext context,
                                                   InternalWorkingMemory workingMemory ) {
        final InternalFactHandle handle = rightTuple.getFactHandle();

        if ( this.constraints.isAllowedCachedLeft( contextEntry,
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
        return childLeftTuple;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.BaseNode#updateNewNode(org.kie.reteoo.WorkingMemoryImpl, org.kie.spi.PropagationContext)
     */
    public void updateSink( final LeftTupleSink sink,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory ) {

        BetaMemory memory = ( BetaMemory ) workingMemory.getNodeMemory( this );

        FastIterator it = memory.getLeftTupleMemory().fastIterator();

        final Iterator tupleIter = memory.getLeftTupleMemory().iterator();
        for ( LeftTuple leftTuple = (LeftTuple) tupleIter.next(); leftTuple != null; leftTuple = (LeftTuple) tupleIter.next() ) {
            this.constraints.updateFromTuple( memory.getContext(),
                                              workingMemory,
                                              leftTuple );
            for ( RightTuple rightTuple = memory.getRightTupleMemory().getFirst( leftTuple, (InternalFactHandle) context.getFactHandle(), it );
                  rightTuple != null; rightTuple = (RightTuple) it.next( rightTuple ) ) {
                if ( this.constraints.isAllowedCachedLeft( memory.getContext(),
                                                           rightTuple.getFactHandle() ) ) {
                    sink.assertLeftTuple( sink.createLeftTuple( leftTuple,
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


}
