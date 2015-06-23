/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.reteoo.nodes;

import org.drools.core.base.DroolsQuery;
import org.drools.core.common.BetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleMemory;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.LeftTupleSourceUtils;
import org.drools.core.reteoo.ModifyPreviousTuples;
import org.drools.core.reteoo.NotNode;
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
import org.drools.core.util.index.RightTupleList;

public class ReteNotNode extends NotNode {

    public ReteNotNode() {
    }

    public ReteNotNode(final int id,
                          final LeftTupleSource leftInput,
                          final ObjectSource rightInput,
                          final BetaConstraints joinNodeBinder,
                          final BuildContext context) {
        super( id,
               leftInput,
               rightInput,
               joinNodeBinder,
               context );
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

    public void assertLeftTuple(final LeftTuple leftTuple,
                                final PropagationContext context,
                                final InternalWorkingMemory workingMemory) {
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

        this.constraints.updateFromTuple( contextEntry,
                                          workingMemory,
                                          leftTuple );
        FastIterator it = getRightIterator(rightMemory);

        for ( RightTuple rightTuple = getFirstRightTuple(leftTuple, rightMemory, (InternalFactHandle) context.getFactHandle(), it); rightTuple != null; rightTuple = (RightTuple) it.next(rightTuple)) {
            if ( this.constraints.isAllowedCachedLeft( contextEntry,
                                                       rightTuple.getFactHandle() ) ) {
                leftTuple.setBlocker( rightTuple );

                if ( useLeftMemory ) {
                    rightTuple.addBlocked( leftTuple );
                }

                break;
            }
        }

        this.constraints.resetTuple(contextEntry);

        if ( leftTuple.getBlocker() == null ) {
            // tuple is not blocked, so add to memory so other fact handles can attempt to match
            if ( useLeftMemory ) {
                memory.getLeftTupleMemory().add( leftTuple );
            }

            this.sink.propagateAssertLeftTuple( leftTuple,
                                                context,
                                                workingMemory,
                                                useLeftMemory );
        }
    }

    public void assertObject( final InternalFactHandle factHandle,
                              final PropagationContext pctx,
                              final InternalWorkingMemory wm ) {
        RightTuple rightTuple = createRightTuple( factHandle,
                                                  this,
                                                  pctx);

        rightTuple.setPropagationContext(pctx);

        // NotNodes must always propagate, they never get staged.
        assertRightTuple(rightTuple, pctx, wm );
    }

    public void assertRightTuple( final RightTuple rightTuple,
                                  final PropagationContext context,
                                  final InternalWorkingMemory workingMemory ) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        memory.getRightTupleMemory().add( rightTuple );

        if ( memory.getLeftTupleMemory() == null || memory.getLeftTupleMemory().size() == 0  ) {
            // do nothing here, as no left memory
            return;
        }

        this.constraints.updateFromFactHandle( memory.getContext(),
                                               workingMemory,
                                               rightTuple.getFactHandle() );
        LeftTupleMemory leftMemory = memory.getLeftTupleMemory();
        FastIterator it = getLeftIterator( leftMemory );
        for (LeftTuple leftTuple = getFirstLeftTuple( rightTuple, leftMemory, it );  leftTuple != null; ) {
            // preserve next now, in case we remove this leftTuple
            LeftTuple temp = (LeftTuple) it.next(leftTuple);

            // we know that only unblocked LeftTuples are  still in the memory
            if ( this.constraints.isAllowedCachedRight( memory.getContext(),
                                                        leftTuple ) ) {
                leftTuple.setBlocker( rightTuple );
                rightTuple.addBlocked( leftTuple );

                // this is now blocked so remove from memory
                memory.getLeftTupleMemory().remove( leftTuple );

                // subclasses like ForallNotNode might override this propagation
                propagateRetractLeftTuple( context,
                                           workingMemory,
                                           leftTuple );
            }

            leftTuple = temp;
        }

        this.constraints.resetFactHandle(memory.getContext());
    }

    public void retractRightTuple(final RightTuple rightTuple,
                                  final PropagationContext context,
                                  final InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        rightTuple.setPropagationContext( context );

        RightTupleMemory rtm = memory.getRightTupleMemory();
        if ( rightTuple.getBlocked() != null ) {
            updateLeftTupleToNewBlocker(rightTuple, context, workingMemory, memory, memory.getLeftTupleMemory(), rightTuple.getBlocked(), rtm, false);
            rightTuple.nullBlocked();
        } else {
            // it's also removed in the updateLeftTupleToNewBlocker
            rtm.remove(rightTuple);
        }

        this.constraints.resetTuple( memory.getContext() );
    }

    public void retractLeftTuple(final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
        RightTuple blocker = leftTuple.getBlocker();
        if ( blocker == null ) {
            final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
            memory.getLeftTupleMemory().remove( leftTuple );

            this.sink.propagateRetractLeftTuple( leftTuple,
                                                 context,
                                                 workingMemory );
        } else {
            blocker.removeBlocked( leftTuple );
        }
    }

    public void modifyLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        RightTupleMemory rightMemory = memory.getRightTupleMemory();

        FastIterator rightIt = getRightIterator( rightMemory );
        RightTuple firstRightTuple = getFirstRightTuple(leftTuple, rightMemory, (InternalFactHandle) context.getFactHandle(), rightIt);

        // If in memory, remove it, because we'll need to add it anyway if it's not blocked, to ensure iteration order
        RightTuple blocker = leftTuple.getBlocker();
        if ( blocker == null ) {
            memory.getLeftTupleMemory().remove( leftTuple );
        } else {
            // check if we changed bucket
            if ( rightMemory.isIndexed() && !rightIt.isFullIterator()  ) {
                // if newRightTuple is null, we assume there was a bucket change and that bucket is empty
                if ( firstRightTuple == null || firstRightTuple.getMemory() != blocker.getMemory() ) {
                    // we changed bucket, so blocker no longer blocks
                    blocker.removeBlocked( leftTuple );
                    blocker = null;
                }
            }
        }

        this.constraints.updateFromTuple( memory.getContext(),
                                          workingMemory,
                                          leftTuple );

        if ( blocker != null && !isLeftUpdateOptimizationAllowed() ) {
            blocker.removeBlocked(leftTuple);
            blocker = null;
        }

        // if we where not blocked before (or changed buckets), or the previous blocker no longer blocks, then find the next blocker
        if ( blocker == null || !this.constraints.isAllowedCachedLeft( memory.getContext(),
                                                                       blocker.getFactHandle() ) ) {

            if ( blocker != null ) {
                // remove previous blocker if it exists, as we know it doesn't block any more
                blocker.removeBlocked( leftTuple );
            }

            // find first blocker, because it's a modify, we need to start from the beginning again
            for ( RightTuple newBlocker = firstRightTuple; newBlocker != null; newBlocker = (RightTuple) rightIt.next(newBlocker) ) {
                if ( this.constraints.isAllowedCachedLeft( memory.getContext(),
                                                           newBlocker.getFactHandle() ) ) {
                    leftTuple.setBlocker( newBlocker );
                    newBlocker.addBlocked( leftTuple );

                    break;
                }
            }

            if ( leftTuple.getBlocker() != null ) {
                // blocked

                if ( leftTuple.getFirstChild() != null ) {
                    // blocked, with previous children, so must have not been previously blocked, so retract
                    // no need to remove, as we removed at the start
                    // to be matched against, as it's now blocked
                    propagateRetractLeftTuple( context,
                                               workingMemory,
                                               leftTuple );
                } // else: it's blocked now and no children so blocked before, thus do nothing
            } else if ( leftTuple.getFirstChild() == null ) {
                // not blocked, with no children, must have been previously blocked so assert
                memory.getLeftTupleMemory().add( leftTuple ); // add to memory so other fact handles can attempt to match
                propagateAssertLeftTuple( context,
                                          workingMemory,
                                          leftTuple );
            } else {
                // not blocked, with children, so wasn't previous blocked and still isn't so modify
                memory.getLeftTupleMemory().add( leftTuple ); // add to memory so other fact handles can attempt to match
                propagateModifyChildLeftTuple( context,
                                               workingMemory,
                                               leftTuple );
            }
        }

        this.constraints.resetTuple( memory.getContext() );
    }

    public void modifyRightTuple(RightTuple rightTuple,
                                 PropagationContext context,
                                 InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        if ( memory.getLeftTupleMemory() == null || ( memory.getLeftTupleMemory().size() == 0 && rightTuple.getBlocked() == null ) ) {
            // do nothing here, as we know there are no left tuples

            //normally do this at the end, but as we are exiting early, make sure the buckets are still correct.
            memory.getRightTupleMemory().removeAdd( rightTuple );
            return;
        }

        // TODO: wtd with behaviours?
        //        if ( !behavior.assertRightTuple( memory.getBehaviorContext(),
        //                                         rightTuple,
        //                                         workingMemory ) ) {
        //            // destroy right tuple
        //            rightTuple.unlinkFromRightParent();
        //            return;
        //        }
        this.constraints.updateFromFactHandle( memory.getContext(),
                                               workingMemory,
                                               rightTuple.getFactHandle() );

        LeftTupleMemory leftMemory = memory.getLeftTupleMemory();

        FastIterator leftIt = getLeftIterator( leftMemory );
        LeftTuple firstLeftTuple = getFirstLeftTuple( rightTuple, leftMemory, leftIt );

        LeftTuple firstBlocked = rightTuple.getBlocked();
        // we now have  reference to the first Blocked, so null it in the rightTuple itself, so we can rebuild
        rightTuple.nullBlocked();


        // first process non-blocked tuples, as we know only those ones are in the left memory.
        for ( LeftTuple leftTuple = firstLeftTuple; leftTuple != null; ) {
            // preserve next now, in case we remove this leftTuple
            LeftTuple temp = (LeftTuple) leftIt.next(leftTuple);

            // we know that only unblocked LeftTuples are  still in the memory
            if ( this.constraints.isAllowedCachedRight( memory.getContext(),
                                                        leftTuple ) ) {
                leftTuple.setBlocker( rightTuple );
                rightTuple.addBlocked( leftTuple );

                // this is now blocked so remove from memory
                leftMemory.remove( leftTuple );

                // subclasses like ForallNotNode might override this propagation
                propagateRetractLeftTuple( context,
                                           workingMemory,
                                           leftTuple );
            }

            leftTuple = temp;
        }

        RightTupleMemory rightTupleMemory = memory.getRightTupleMemory();
        if ( firstBlocked != null ) {
            updateLeftTupleToNewBlocker(rightTuple, context, workingMemory, memory, leftMemory, firstBlocked, rightTupleMemory, true);


        } else {
            // we had to do this at the end, rather than beginning as this 'if' block needs the next memory tuple
            rightTupleMemory.removeAdd( rightTuple );
        }

        this.constraints.resetFactHandle( memory.getContext() );
        this.constraints.resetTuple( memory.getContext() );
    }

    private void updateLeftTupleToNewBlocker(RightTuple rightTuple, PropagationContext context, InternalWorkingMemory workingMemory, BetaMemory memory, LeftTupleMemory leftMemory, LeftTuple firstBlocked, RightTupleMemory rightTupleMemory, boolean removeAdd) {// will attempt to resume from the last blocker, if it's not a comparison or unification index.
        boolean resumeFromCurrent =  !(indexedUnificationJoin || rightTupleMemory.getIndexType().isComparison());

        FastIterator rightIt = null;
        RightTuple rootBlocker = null;
        if ( resumeFromCurrent ) {
            RightTupleList currentRtm = rightTuple.getMemory();
            rightIt = currentRtm.fastIterator(); // only needs to iterate the current bucket, works for equality indexed and non indexed.
            rootBlocker = (RightTuple) rightTuple.getNext();

            if ( removeAdd ) {
                // we must do this after we have the next in memory
                // We add to the end to give an opportunity to re-match if in same bucket
                rightTupleMemory.removeAdd( rightTuple );
            } else {
                rightTupleMemory.remove( rightTuple );
            }

            if ( rootBlocker == null && rightTuple.getMemory() == currentRtm) {
                // there was no next root blocker, but the current was re-added to same list, so set for re-match attempt.
                rootBlocker = rightTuple;
            }
        } else {
            rightIt = getRightIterator( rightTupleMemory );
            if ( removeAdd ) {
                rightTupleMemory.removeAdd( rightTuple );
            }  else {
                rightTupleMemory.remove( rightTuple );
            }
        }

        // iterate all the existing previous blocked LeftTuples
        for ( LeftTuple leftTuple = (LeftTuple) firstBlocked; leftTuple != null; ) {
            LeftTuple temp = leftTuple.getBlockedNext();

            leftTuple.clearBlocker();

            this.constraints.updateFromTuple( memory.getContext(),
                                              workingMemory,
                                              leftTuple );

            if (!resumeFromCurrent) {
                rootBlocker = getFirstRightTuple( leftTuple, rightTupleMemory, (InternalFactHandle) context.getFactHandle(), rightIt );
            }

            // we know that older tuples have been checked so continue next
            for ( RightTuple newBlocker = rootBlocker; newBlocker != null; newBlocker = (RightTuple) rightIt.next( newBlocker ) ) {
                if ( this.constraints.isAllowedCachedLeft( memory.getContext(),
                                                           newBlocker.getFactHandle() ) ) {
                    leftTuple.setBlocker( newBlocker );
                    newBlocker.addBlocked( leftTuple );

                    break;
                }
            }

            if ( leftTuple.getBlocker() == null ) {
                // was previous blocked and not in memory, so add
                leftMemory.add( leftTuple );

                // subclasses like ForallNotNode might override this propagation
                propagateAssertLeftTuple( context,
                                          workingMemory,
                                          leftTuple );
            }

            leftTuple = temp;
        }
    }

    protected void propagateAssertLeftTuple(final PropagationContext context,
                                            final InternalWorkingMemory workingMemory,
                                            LeftTuple leftTuple) {
        this.sink.propagateAssertLeftTuple(leftTuple,
                                           context,
                                           workingMemory,
                                           true);
    }

    protected void propagateRetractLeftTuple(final PropagationContext context,
                                             final InternalWorkingMemory workingMemory,
                                             LeftTuple leftTuple) {
        this.sink.propagateRetractLeftTuple(leftTuple,
                                            context,
                                            workingMemory);
    }

    protected void propagateModifyChildLeftTuple(final PropagationContext context,
                                                 final InternalWorkingMemory workingMemory,
                                                 LeftTuple leftTuple) {
        this.sink.propagateModifyChildLeftTuple( leftTuple,
                                                 context,
                                                 workingMemory,
                                                 true );
    }

    public void updateSink(final LeftTupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );


        final Iterator tupleIter = memory.getLeftTupleMemory().iterator();
        for ( LeftTuple leftTuple = (LeftTuple) tupleIter.next(); leftTuple != null; leftTuple = (LeftTuple) tupleIter.next() ) {
            sink.assertLeftTuple( sink.createLeftTuple( leftTuple,
                                                        sink,
                                                        context, true),
                                  context,
                                  workingMemory );
        }
    }
}
