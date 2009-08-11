/*
 * Copyright 2006 JBoss Inc
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

import org.drools.common.BetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Behavior;
import org.drools.spi.PropagationContext;

/**
 * <code>ExistsNode</code> extends <code>BetaNode</code> to perform tests for
 * the existence of a Fact plus one or more conditions. Where existence
 * is found the left ReteTuple is copied and propagated. Further to this it
 * maintains the "truth" by canceling any
 * <code>Activation<code>s that are no longer
 * considered true by the retraction of ReteTuple's or FactHandleImpl.
 * Tuples are considered to be asserted from the left input and facts from the right input.
 * The <code>BetaNode</code> provides the BetaMemory to store asserted ReteTuples and
 * <code>FactHandleImpl<code>s. Each fact handle is stored in the right
 * memory.
 *
 * @author <a href="mailto:etirelli@redhat.com">Edson Tirelli</a>
 *
 */
public class ExistsNode extends BetaNode {
    
    private static final long serialVersionUID = 400L;        

    static int                notAssertObject  = 0;
    static int                notAssertTuple   = 0;

    public ExistsNode() {
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Construct.
     *
     * @param id
     *            The unique id for this node.
     * @param leftInput
     *            The left input <code>TupleSource</code>.
     * @param rightInput
     *            The right input <code>ObjectSource</code>.
     * @param joinNodeBinder
     *            The constraints to be applied to the right objects
     */
    public ExistsNode(final int id,
                      final LeftTupleSource leftInput,
                      final ObjectSource rightInput,
                      final BetaConstraints joinNodeBinder,
                      final Behavior[] behaviors,
                      final BuildContext context) {
        super( id,
               context.getPartitionId(),
               context.getRuleBase().getConfiguration().isMultithreadEvaluation(),
               leftInput,
               rightInput,
               joinNodeBinder,
               behaviors );
        this.tupleMemoryEnabled = context.isTupleMemoryEnabled();
    }

    /**
     * Assert a new <code>ReteTuple</code> from the left input. It iterates
     * over the right <code>FactHandleImpl</code>'s and if any match is found,
     * a copy of the <code>ReteTuple</code> is made and propagated.
     *
     * @param tuple
     *            The <code>Tuple</code> being asserted.
     * @param context
     *            The <code>PropagationContext</code>
     * @param workingMemory
     *            The working memory session.
     */
    public void assertLeftTuple(final LeftTuple leftTuple,
                                final PropagationContext context,
                                final InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        this.constraints.updateFromTuple( memory.getContext(),
                                          workingMemory,
                                          leftTuple );

        for ( RightTuple rightTuple = memory.getRightTupleMemory().getFirst( leftTuple ); rightTuple != null; rightTuple = (RightTuple) rightTuple.getNext() ) {
            if ( this.constraints.isAllowedCachedLeft( memory.getContext(),
                                                       rightTuple.getFactHandle() ) ) {

                leftTuple.setBlocker( rightTuple );
                rightTuple.setBlocked( leftTuple );

                break;
            }
        }

        this.constraints.resetTuple( memory.getContext() );

        if ( leftTuple.getBlocker() != null ) {
            // tuple is not blocked to propagate
            this.sink.propagateAssertLeftTuple( leftTuple,
                                                context,
                                                workingMemory,
                                                this.tupleMemoryEnabled);
        } else if ( this.tupleMemoryEnabled ) {
            // LeftTuple is not blocked, so add to memory so other RightTuples can match
            memory.getLeftTupleMemory().add( leftTuple );
        }
    }

    /**
     * Assert a new <code>FactHandleImpl</code> from the right input. If it
     * matches any left ReteTuple's that had no matches before, propagate
     * tuple as an assertion.
     *
     * @param factHandle
     *            The <code>FactHandleImpl</code> being asserted.
     * @param context
     *            The <code>PropagationContext</code>
     * @param workingMemory
     *            The working memory session.
     */
    public void assertObject(final InternalFactHandle factHandle,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {
        //        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        //        memory.getRightTupleMemory().add( factHandle );
        //
        //        if ( !this.tupleMemoryEnabled ) {
        //            // do nothing here, as we know there are no left tuples at this stage in sequential mode.
        //            return;
        //        }
        //
        //        final Iterator it = memory.getLeftTupleMemory().iterator( factHandle );
        //        this.constraints.updateFromFactHandle( memory.getContext(),
        //                                               workingMemory,
        //                                               factHandle );
        //        for ( LeftTuple tuple = (LeftTuple) it.next(); tuple != null; tuple = (LeftTuple) it.next() ) {
        //            if ( this.constraints.isAllowedCachedRight( memory.getContext(),
        //                                                        tuple ) && tuple.getMatch() == null ) {
        //                tuple.setMatch( factHandle );
        //                this.sink.propagateAssertLeftTuple( tuple,
        //                                                    context,
        //                                                    workingMemory );
        //            }
        //        }
        //
        //        this.constraints.resetFactHandle( memory.getContext() );

        final RightTuple rightTuple = new RightTuple( factHandle,
                                                      this );

        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        if ( !behavior.assertRightTuple( memory.getBehaviorContext(),
                                         rightTuple,
                                         workingMemory ) ) {
            // destroy right tuple
            rightTuple.unlinkFromRightParent();
            return;
        }

        memory.getRightTupleMemory().add( rightTuple );

        if ( !this.tupleMemoryEnabled ) {
            // do nothing here, as we know there are no left tuples at this stage in sequential mode.
            return;
        }

        this.constraints.updateFromFactHandle( memory.getContext(),
                                               workingMemory,
                                               factHandle );
        for ( LeftTuple leftTuple = memory.getLeftTupleMemory().getFirst( rightTuple ); leftTuple != null; ) {
            // preserve next now, in case we remove this leftTuple 
            LeftTuple temp = (LeftTuple) leftTuple.getNext();

            // we know that only unblocked LeftTuples are  still in the memory
            if ( this.constraints.isAllowedCachedRight( memory.getContext(),
                                                        leftTuple ) ) {
                leftTuple.setBlocker( rightTuple );
                rightTuple.setBlocked( leftTuple );

                if ( this.tupleMemoryEnabled ) {
                    // this is now blocked so remove it from memory
                    memory.getLeftTupleMemory().remove( leftTuple );
                }
                this.sink.propagateAssertLeftTuple( leftTuple,
                                                    context,
                                                    workingMemory,
                                                    this.tupleMemoryEnabled );
            }

            leftTuple = temp;
        }

        this.constraints.resetFactHandle( memory.getContext() );
    }

    /**
     * Retract the <code>FactHandleImpl</code>. If the handle has any
     * <code>ReteTuple</code> matches and those tuples now have no
     * other match, retract tuple
     *
     * @param handle
     *            the <codeFactHandleImpl</code> being retracted
     * @param context
     *            The <code>PropagationContext</code>
     * @param workingMemory
     *            The working memory session.
     */
    public void retractRightTuple(final RightTuple rightTuple,
                                  final PropagationContext context,
                                  final InternalWorkingMemory workingMemory) {
        //        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        //        if ( !memory.getRightTupleMemory().remove( handle ) ) {
        //            return;
        //        }
        //
        //        final Iterator it = memory.getLeftTupleMemory().iterator( handle );
        //        this.constraints.updateFromFactHandle( memory.getContext(),
        //                                               workingMemory,
        //                                               handle );
        //        for ( LeftTuple tuple = (LeftTuple) it.next(); tuple != null; tuple = (LeftTuple) it.next() ) {
        //            if ( this.constraints.isAllowedCachedRight( memory.getContext(),
        //                                                        tuple ) ) {
        //                if ( tuple.getMatch() == handle ) {
        //                    // reset the match
        //                    tuple.setMatch( null );
        //
        //                    // find next match, remember it and break.
        //                    final Iterator tupleIt = memory.getRightTupleMemory().iterator( tuple );
        //                    this.constraints.updateFromTuple( memory.getContext(),
        //                                                      workingMemory,
        //                                                      tuple );
        //
        //                    for ( FactEntry entry = (FactEntry) tupleIt.next(); entry != null; entry = (FactEntry) tupleIt.next() ) {
        //                        final InternalFactHandle rightHandle = entry.getFactHandle();
        //                        if ( this.constraints.isAllowedCachedLeft( memory.getContext(),
        //                                                                   rightHandle ) ) {
        //                            tuple.setMatch( rightHandle );
        //                            break;
        //                        }
        //                    }
        //
        //                    this.constraints.resetTuple( memory.getContext() );
        //
        //                    // if there is now no new tuple match then propagate assert.
        //                    if ( tuple.getMatch() == null ) {
        //                        this.sink.propagateRetractLeftTuple( tuple,
        //                                                             context,
        //                                                             workingMemory );
        //                    }
        //                }
        //
        //            }
        //        }
        //
        //        this.constraints.resetFactHandle( memory.getContext() );
        // assign now, so we can remove from memory before doing any possible propagations
        final RightTuple rootBlocker = (RightTuple) rightTuple.getNext();

        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        behavior.retractRightTuple( memory.getBehaviorContext(),
                                    rightTuple,
                                    workingMemory );
        memory.getRightTupleMemory().remove( rightTuple );

        if ( rightTuple.getBlocked() == null ) {
            return;
        }

        for ( LeftTuple leftTuple = (LeftTuple) rightTuple.getBlocked(); leftTuple != null; ) {
            LeftTuple temp = leftTuple.getBlockedNext();

            leftTuple.setBlocker( null );
            leftTuple.setBlockedPrevious( null );
            leftTuple.setBlockedNext( null );

            this.constraints.updateFromTuple( memory.getContext(),
                                              workingMemory,
                                              leftTuple );

            // we know that older tuples have been checked so continue previously
            for ( RightTuple newBlocker = rootBlocker; newBlocker != null; newBlocker = (RightTuple) newBlocker.getNext() ) {
                if ( this.constraints.isAllowedCachedLeft( memory.getContext(),
                                                           newBlocker.getFactHandle() ) ) {
                    leftTuple.setBlocker( newBlocker );
                    newBlocker.setBlocked( leftTuple );

                    break;
                }
            }

            if ( leftTuple.getBlocker() == null ) {
                    // was previous blocked and not in memory, so add
                    memory.getLeftTupleMemory().add( leftTuple );

                this.sink.propagateRetractLeftTuple( leftTuple,
                                                     context,
                                                     workingMemory );
            }

            leftTuple = temp;
        }
        this.constraints.resetTuple( memory.getContext() );
    }

    /**
     * Retract the
     * <code>ReteTuple<code>, any resulting propagated joins are also retracted.
     *
     * @param leftTuple
     *            The tuple being retracted
     * @param context
     *            The <code>PropagationContext</code>
     * @param workingMemory
     *            The working memory session.
     */
    public void retractLeftTuple(final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
        //        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        //
        //        // Must use the tuple in memory as it has the tuple matches count
        //        final LeftTuple tuple = memory.getLeftTupleMemory().remove( leftTuple );
        //        if ( tuple == null ) {
        //            return;
        //        }
        //
        //        if ( tuple.getMatch() != null ) {
        //            this.sink.propagateRetractLeftTuple( tuple,
        //                                                 context,
        //                                                 workingMemory );
        //        }

        RightTuple blocker = leftTuple.getBlocker();
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        if ( blocker != null ) {
            this.sink.propagateRetractLeftTuple( leftTuple,
                                                 context,
                                                 workingMemory );

            blocker.removeBlocked( leftTuple );
        } else {
            memory.getLeftTupleMemory().remove( leftTuple );            
        }
    }

    /**
     * Updates the given sink propagating all previously propagated tuples to it
     *
     */
    public void updateSink(final LeftTupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        //        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        //
        //        final Iterator tupleIter = memory.getLeftTupleMemory().iterator();
        //        for ( LeftTuple tuple = (LeftTuple) tupleIter.next(); tuple != null; tuple = (LeftTuple) tupleIter.next() ) {
        //            if ( tuple.getMatch() != null ) {
        //                sink.assertLeftTuple( new LeftTuple( tuple ),
        //                                      context,
        //                                      workingMemory );
        //            }
        //        }
        // @FIXME
    }

    public String toString() {
        ObjectSource source = this.rightInput;
        while ( source.getClass() != ObjectTypeNode.class ) {
            source = source.source;
        }

        return "[ExistsNode - " + ((ObjectTypeNode) source).getObjectType() + "]";
    }
    
    public short getType() {
        return NodeTypeEnums.ExistsNode;
    }     

}
