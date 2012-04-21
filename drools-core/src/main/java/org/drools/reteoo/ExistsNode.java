/*
 * Copyright 2010 JBoss Inc
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
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;
import org.drools.core.util.RightTupleList;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Behavior;
import org.drools.rule.ContextEntry;
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
 */
public class ExistsNode extends BetaNode {

    private static final long serialVersionUID = 510l;

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
        RightTupleMemory rightMemory = memory.getRightTupleMemory();
        
        ContextEntry[] contextEntry = memory.getContext();


        boolean useLeftMemory = true;
        if ( !this.tupleMemoryEnabled ) {
            // This is a hack, to not add closed DroolsQuery objects
            Object object = ((InternalFactHandle) context.getFactHandle()).getObject();
            if ( !(object instanceof DroolsQuery) || !((DroolsQuery) object).isOpen() ) {
                useLeftMemory = false;
            }
        }

        this.constraints.updateFromTuple( contextEntry,
                                          workingMemory,
                                          leftTuple );
        
        FastIterator it = getRightIterator( rightMemory );
        
        for ( RightTuple rightTuple = getFirstRightTuple(leftTuple, rightMemory, context, it); rightTuple != null; rightTuple = (RightTuple) it.next(rightTuple)) {
            if ( this.constraints.isAllowedCachedLeft( contextEntry,
                                                       rightTuple.getFactHandle() ) ) {

                leftTuple.setBlocker( rightTuple );
                if ( useLeftMemory ) {
                    rightTuple.addBlocked( leftTuple );
                }

                break;
            }
        }

        this.constraints.resetTuple( contextEntry );

        if ( leftTuple.getBlocker() != null ) {
            // tuple is not blocked to propagate
            this.sink.propagateAssertLeftTuple( leftTuple,
                                                context,
                                                workingMemory,
                                                useLeftMemory );
        } else if ( useLeftMemory ) {
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

        final RightTuple rightTuple = new RightTuple( factHandle,
                                                      this );

        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        if ( !behavior.assertRightTuple( context,
                                         memory.getBehaviorContext(),
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
        LeftTupleMemory leftMemory = memory.getLeftTupleMemory();        
        FastIterator it = getLeftIterator( leftMemory );
        for (LeftTuple leftTuple = getFirstLeftTuple( rightTuple, leftMemory, context, it );  leftTuple != null; ) {        
            // preserve next now, in case we remove this leftTuple 
            LeftTuple temp = (LeftTuple) it.next(leftTuple);

            // we know that only unblocked LeftTuples are  still in the memory
            if ( this.constraints.isAllowedCachedRight( memory.getContext(),
                                                        leftTuple ) ) {
                leftTuple.setBlocker( rightTuple );
                rightTuple.addBlocked( leftTuple );

                memory.getLeftTupleMemory().remove( leftTuple );

                this.sink.propagateAssertLeftTuple( leftTuple,
                                                    context,
                                                    workingMemory,
                                                    true );
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
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        
        FastIterator it = memory.getRightTupleMemory().fastIterator();
        final RightTuple rootBlocker = (RightTuple) it.next(rightTuple);

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
            for ( RightTuple newBlocker = rootBlocker; newBlocker != null; newBlocker = (RightTuple) it.next(newBlocker ) ) {
                if ( this.constraints.isAllowedCachedLeft( memory.getContext(),
                                                           newBlocker.getFactHandle() ) ) {
                    leftTuple.setBlocker( newBlocker );
                    newBlocker.addBlocked( leftTuple );

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
        rightTuple.nullBlocked();
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

    public void modifyLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        RightTupleMemory rightMemory = memory.getRightTupleMemory();
        
        FastIterator rightIt = getRightIterator( rightMemory );         
        RightTuple firstRightTuple = getFirstRightTuple(leftTuple, rightMemory, context, rightIt);

        // If in memory, remove it, because we'll need to add it anyway if it's not blocked, to ensure iteration order
        RightTuple blocker = leftTuple.getBlocker();
        if ( blocker == null ) {
            memory.getLeftTupleMemory().remove( leftTuple );
        } else {
            // check if we changed bucket
            if ( rightMemory.isIndexed()&& !rightIt.isFullIterator()  ) {                
                // if newRightTuple is null, we assume there was a bucket change and that bucket is empty                
                if ( firstRightTuple == null || firstRightTuple.getMemory() != blocker.getMemory() ) {
                    // we changed bucket, so blocker no longer blocks
                    blocker.removeBlocked( leftTuple );
                    leftTuple.setBlocker( null );
                    leftTuple.setBlockedPrevious( null );
                    leftTuple.setBlockedNext( null );
                    blocker = null;
                }
            }            
        }

        this.constraints.updateFromTuple( memory.getContext(),
                                          workingMemory,
                                          leftTuple );

        // if we where not blocked before (or changed buckets), or the previous blocker no longer blocks, then find the next blocker
        if ( blocker == null || !this.constraints.isAllowedCachedLeft( memory.getContext(),
                                                                       blocker.getFactHandle() ) ) {

            if ( blocker != null ) {
                // remove previous blocker if it exists, as we know it doesn't block any more
                blocker.removeBlocked( leftTuple );
                leftTuple.setBlocker( null );
                leftTuple.setBlockedPrevious( null );
                leftTuple.setBlockedNext( null );
            }

            FastIterator it = memory.getRightTupleMemory().fastIterator();
            
            // find first blocker, because it's a modify, we need to start from the beginning again        
            for ( RightTuple newBlocker = firstRightTuple; newBlocker != null; newBlocker = (RightTuple) rightIt.next(newBlocker) ) {
                if ( this.constraints.isAllowedCachedLeft( memory.getContext(),
                                                           newBlocker.getFactHandle() ) ) {
                    leftTuple.setBlocker( newBlocker );
                    newBlocker.addBlocked( leftTuple );

                    break;
                }
            }
        }

        if ( leftTuple.getBlocker() == null ) {
            // not blocked
            memory.getLeftTupleMemory().add( leftTuple ); // add to memory so other fact handles can attempt to match                    

            if ( leftTuple.getFirstChild() != null ) {
                // with previous children, retract
                this.sink.propagateRetractLeftTuple( leftTuple,
                                                     context,
                                                     workingMemory );
            }
            // with no previous children. do nothing.
        } else if ( leftTuple.getFirstChild() == null ) {
            // blocked, with no previous children, assert
            this.sink.propagateAssertLeftTuple( leftTuple,
                                                context,
                                                workingMemory,
                                                true );
        } else {
            // blocked, with previous children, modify
            this.sink.propagateModifyChildLeftTuple( leftTuple,
                                                     context,
                                                     workingMemory,
                                                     true );
        }
        
        this.constraints.resetTuple( memory.getContext() );
    }

    public void modifyRightTuple(RightTuple rightTuple,
                                 PropagationContext context,
                                 InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        if ( memory.getLeftTupleMemory() == null || (memory.getLeftTupleMemory().size() == 0 && rightTuple.getBlocked() == null) ) {
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
        LeftTuple firstLeftTuple = getFirstLeftTuple( rightTuple, leftMemory, context, leftIt );
        
        LeftTuple firstBlocked = rightTuple.getBlocked();
        // we now have  reference to the first Blocked, so null it in the rightTuple itself, so we can rebuild
        rightTuple.nullBlocked();
        
        // first process non-blocked tuples, as we know only those ones are in the left memory.
        for ( LeftTuple leftTuple = firstLeftTuple; leftTuple != null; ) {
            // preserve next now, in case we remove this leftTuple 
            LeftTuple temp = (LeftTuple) leftIt.next( leftTuple );

            // we know that only unblocked LeftTuples are  still in the memory
            if ( this.constraints.isAllowedCachedRight( memory.getContext(),
                                                        leftTuple ) ) {
                leftTuple.setBlocker( rightTuple );
                rightTuple.addBlocked( leftTuple );

                // this is now blocked so remove from memory
                leftMemory.remove( leftTuple );

                // subclasses like ForallNotNode might override this propagation
                this.sink.propagateAssertLeftTuple( leftTuple,
                                                    context,
                                                    workingMemory,
                                                    true );
            }

            leftTuple = temp;
        }

        if ( firstBlocked != null ) {
            // now process existing blocks, we only process existing and not new from above loop
            FastIterator rightIt = getRightIterator( memory.getRightTupleMemory() );
            RightTuple rootBlocker = (RightTuple) rightIt.next(rightTuple);
          
            RightTupleList list = rightTuple.getMemory();
            
            // we must do this after we have the next in memory
            // We add to the end to give an opportunity to re-match if in same bucket
            memory.getRightTupleMemory().removeAdd( rightTuple );

            if ( rootBlocker == null && list == rightTuple.getMemory() ) {
                // we are at the end of the list, but still in same bucket, so set to self, to give self a chance to rematch
                rootBlocker = rightTuple;
            }  
            
            // iterate all the existing previous blocked LeftTuples
            for ( LeftTuple leftTuple = (LeftTuple) firstBlocked; leftTuple != null; ) {
                LeftTuple temp = leftTuple.getBlockedNext();

                leftTuple.setBlockedPrevious( null ); // must null these as we are re-adding them to the list
                leftTuple.setBlockedNext( null );

                leftTuple.setBlocker( null );

                this.constraints.updateFromTuple( memory.getContext(),
                                                  workingMemory,
                                                  leftTuple );

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
                    memory.getLeftTupleMemory().add( leftTuple );

                    // subclasses like ForallNotNode might override this propagation
                    this.sink.propagateRetractLeftTuple( leftTuple,
                                                         context,
                                                         workingMemory );
                }

                leftTuple = temp;
            }
        } else {
            // we had to do this at the end, rather than beginning as this 'if' block needs the next memory tuple
            memory.getRightTupleMemory().removeAdd( rightTuple );         
        }

        this.constraints.resetFactHandle( memory.getContext() );
        this.constraints.resetTuple( memory.getContext() );

    }

    /**
     * Updates the given sink propagating all previously propagated tuples to it
     */
    public void updateSink(final LeftTupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        Iterator it = memory.getRightTupleMemory().iterator();

        // Relies on the fact that any propagated LeftTuples are blocked, but due to lazy blocking
        // they will only be blocked once. So we can iterate the right memory to find the left tuples to propagate
        for ( RightTuple rightTuple = (RightTuple) it.next(); rightTuple != null; rightTuple = (RightTuple) it.next() ) {
            LeftTuple leftTuple = rightTuple.getBlocked();
            while ( leftTuple != null ) {
                sink.assertLeftTuple( sink.createLeftTuple( leftTuple,
                                                            sink,
                                                            true ),
                                      context,
                                      workingMemory );
                leftTuple = leftTuple.getBlockedNext();
            }
        }
    }  
    
    public String toString() {
        ObjectSource source = this.rightInput;
        while ( source != null && source.getClass() != ObjectTypeNode.class ) {
            source = source.source;
        }
        return "[ExistsNode(" + this.getId() + ") - " + ((source != null) ? ((ObjectTypeNode) source).getObjectType() : "<source from a subnetwork>") + "]";
    }

    public short getType() {
        return NodeTypeEnums.ExistsNode;
    }
    
    public LeftTuple createLeftTuple(InternalFactHandle factHandle,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new NotNodeLeftTuple(factHandle, sink, leftTupleMemoryEnabled );
    }    
    
    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new NotNodeLeftTuple(leftTuple,sink, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTupleSink sink) {
        return new NotNodeLeftTuple(leftTuple, rightTuple, sink );
    }   
    
    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTuple currentLeftChild,
                                     LeftTuple currentRightChild,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new NotNodeLeftTuple(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled );        
    }            

}
