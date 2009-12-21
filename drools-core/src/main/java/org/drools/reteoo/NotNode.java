package org.drools.reteoo;

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

import org.drools.common.BetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Behavior;
import org.drools.spi.PropagationContext;
import org.drools.util.Iterator;

/**
 * <code>NotNode</code> extends <code>BetaNode</code> to perform tests for
 * the non existence of a Fact plus one or more conditions. Where none existence
 * is found the left ReteTuple is copied and propgated. Further to this it
 * maintains the "truth" by cancelling any
 * <code>Activation<code>s that are nolonger
 * considered true by the assertion of ReteTuple's or FactHandleImpl.  Tuples are considered to be asserted from the left input and facts from the right input.
 * The <code>BetaNode</code> provides the BetaMemory to store assserted ReteTuples and <code>FactHandleImpl<code>s. Each fact handle is stored in the right
 * memory as a key in a <code>HashMap</code>, the value is an <code>ObjectMatches</code> instance which maintains a <code>LinkedList of <code>TuplesMatches -
 * The tuples that are matched with the handle. the left memory is a <code>LinkedList</code> of <code>ReteTuples</code> which maintains a <code>HashMa</code>,
 * where the keys are the matching <code>FactHandleImpl</code>s and the value is populated <code>TupleMatche</code>es, the keys are matched fact handles.
 * <code>TupleMatch</code> maintains a <code>List</code> of resulting joins, where there is joined <code>ReteTuple</code> per <code>TupleSink</code>.
 *
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 *
 */
public class NotNode extends BetaNode {
    private static final long serialVersionUID = 400L;

    static int                notAssertObject  = 0;
    static int                notAssertTuple   = 0;

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------
    public NotNode() {

    }

    /**
     * Construct.
     *
     * @param leftInput
     *            The left input <code>TupleSource</code>.
     * @param rightInput
     *            The right input <code>TupleSource</code>.
     */
    public NotNode(final int id,
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
     * over the right <code>FactHandleImpl</code>'s if no matches are found
     * the a copy of the <code>ReteTuple</code> is made and propagated.
     *
     * @param tuple
     *            The <code>Tuple</code> being asserted.
     * @param context
     *            The <code>PropagationContext</code>
     * @param workingMemory
     *            The working memory seesion.
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

                if ( this.tupleMemoryEnabled ) {
                    rightTuple.setBlocked( leftTuple );
                }

                break;
            }
        }

        this.constraints.resetTuple( memory.getContext() );

        if ( leftTuple.getBlocker() == null ) {
            // tuple is not blocked, so add to memory so other fact handles can attempt to match
            if ( this.tupleMemoryEnabled ) {
                memory.getLeftTupleMemory().add( leftTuple );
            }

            this.sink.propagateAssertLeftTuple( leftTuple,
                                                context,
                                                workingMemory,
                                                this.tupleMemoryEnabled );
        }
    }

    /**
     * Assert a new <code>FactHandleImpl</code> from the right input. If it
     * matches any left ReteTuple's that already has propagations then those
     * propagations are retracted.
     *
     * @param factHandle
     *            The <code>FactHandleImpl</code> being asserted.
     * @param context
     *            The <code>PropagationContext</code>
     * @param workingMemory
     *            The working memory seesion.
     */
    public void assertObject(final InternalFactHandle factHandle,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {
        final RightTuple rightTuple = createRightTuple( factHandle,
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

                // this is now blocked so remove from memory
                memory.getLeftTupleMemory().remove( leftTuple );

                // subclasses like ForallNotNode might override this propagation
                propagateRetractLeftTuple( context,
                                           workingMemory,
                                           leftTuple );
            }

            leftTuple = temp;
        }

        this.constraints.resetFactHandle( memory.getContext() );
    }

    /**
     * This is a hook method so that subclasses (like ForallNotNode) can 
     * override the default behaviour.
     * 
     * @param context
     * @param workingMemory
     * @param leftTuple
     */
    protected void propagateRetractLeftTuple(final PropagationContext context,
                                             final InternalWorkingMemory workingMemory,
                                             LeftTuple leftTuple) {
        this.sink.propagateRetractLeftTuple( leftTuple,
                                             context,
                                             workingMemory );
    }

    /**
     * Retract the <code>FactHandleImpl</code>. If the handle has any
     * <code>ReteTuple</code> matches then those matches copied are propagated
     * as new joins.
     *
     * @param handle
     *            the <codeFactHandleImpl</code> being retracted
     * @param context
     *            The <code>PropagationContext</code>
     * @param workingMemory
     *            The working memory seesion.
     * @throws AssertionException
     */
    public void retractRightTuple(final RightTuple rightTuple,
                                  final PropagationContext context,
                                  final InternalWorkingMemory workingMemory) {
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

            // we know that older tuples have been checked so continue next
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

                // subclasses like ForallNotNode might override this propagation
                propagateAssertLeftTuple( context,
                                          workingMemory,
                                          leftTuple );
            }

            leftTuple = temp;
        }
        rightTuple.setBlocked( null );
        this.constraints.resetTuple( memory.getContext() );
    }

    /**
     * This is a hook method so that subclasses (like ForallNotNode) can 
     * override the default behaviour.
     * 
     * @param context
     * @param workingMemory
     * @param leftTuple
     */
    protected void propagateAssertLeftTuple(final PropagationContext context,
                                            final InternalWorkingMemory workingMemory,
                                            LeftTuple leftTuple) {
        this.sink.propagateAssertLeftTuple( leftTuple,
                                            context,
                                            workingMemory,
                                            this.tupleMemoryEnabled );
    }

    /**
     * Retract the
     * <code>ReteTuple<code>, any resulting proppagated joins are also retracted.
     *
     * @param key
     *            The tuple key.
     * @param context
     *            The <code>PropagationContext</code>
     * @param workingMemory
     *            The working memory seesion.
     */
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

    /* (non-Javadoc)
     * @see org.drools.reteoo.BaseNode#updateNewNode(org.drools.reteoo.WorkingMemoryImpl, org.drools.spi.PropagationContext)
     */
    public void updateSink(final LeftTupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        final Iterator tupleIter = memory.getLeftTupleMemory().iterator();
        for ( LeftTuple leftTuple = (LeftTuple) tupleIter.next(); leftTuple != null; leftTuple = (LeftTuple) tupleIter.next() ) {
            sink.assertLeftTuple( new LeftTuple( leftTuple,
                                                 sink,
                                                 this.tupleMemoryEnabled ),
                                  context,
                                  workingMemory );
        }
    }

    public short getType() {
        return NodeTypeEnums.NotNode;
    }

    public String toString() {
        ObjectSource source = this.rightInput;
        while ( source != null && !(source instanceof ObjectTypeNode) ) {
            source = source.source;
        }

        return "[NotNode("+this.getId()+") - " + ((source != null) ? ((ObjectTypeNode) source).getObjectType() : "<source from a subnetwork>" )  + "]";
    }
}
