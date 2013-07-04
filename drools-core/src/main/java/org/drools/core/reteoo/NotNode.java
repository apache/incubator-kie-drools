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

package org.drools.core.reteoo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
 
import org.drools.core.base.DroolsQuery;
import org.drools.core.common.BetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PropagationContextImpl;
import org.drools.core.common.RightTupleSets;
import org.drools.core.common.SynchronizedRightTupleSets;
import org.drools.core.phreak.RightTupleEntry;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;
import org.drools.core.util.index.RightTupleList;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.ContextEntry;
import org.drools.core.spi.PropagationContext;

public class NotNode extends BetaNode {
    private static final long serialVersionUID = 510l;

    static int                notAssertObject  = 0;
    static int                notAssertTuple   = 0;
    
    // The reason why this is here is because forall can inject a
    //  "this == " + BASE_IDENTIFIER $__forallBaseIdentifier
    // Which we don't want to actually count in the case of forall node linking
    private boolean           emptyBetaConstraints;

    public NotNode() {

    }

    public NotNode(final int id,
                   final LeftTupleSource leftInput,
                   final ObjectSource rightInput,
                   final BetaConstraints joinNodeBinder,
                   final BuildContext context) {
        super( id,
               context.getPartitionId(),
               context.getRuleBase().getConfiguration().isMultithreadEvaluation(),
               leftInput,
               rightInput,
               joinNodeBinder,
               context );
        this.tupleMemoryEnabled = context.isTupleMemoryEnabled();
        
        // The reason why this is here is because forall can inject a
        //  "this == " + BASE_IDENTIFIER $__forallBaseIdentifier
        // Which we don't want to actually count in the case of forall node linking
        emptyBetaConstraints = joinNodeBinder.getConstraints().length == 0 || context.isEmptyForAllBetaConstraints();
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        emptyBetaConstraints = in.readBoolean();
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeBoolean( emptyBetaConstraints );
    }

    public boolean isEmptyBetaConstraints() {
        return emptyBetaConstraints;
    }

    public void setEmptyBetaConstraints(boolean emptyBetaConstraints) {
        this.emptyBetaConstraints = emptyBetaConstraints;
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
        final BetaMemory memory = (BetaMemory) getBetaMemoryFromRightInput(this, wm); 

        RightTuple rightTuple = createRightTuple( factHandle,
                                                  this,
                                                  pctx);
        
        rightTuple.setPropagationContext(pctx);

        if ( isUnlinkingEnabled() ) {              
            // strangely we link here, this is actually just to force a network evaluation
            // The assert is then processed and the rule unlinks then. 
            // This is because we need the first RightTuple to link with it's blocked
            boolean stagedInsertWasEmpty = false;
            if ( streamMode ) {
                stagedInsertWasEmpty = memory.getSegmentMemory().getTupleQueue().isEmpty();
                memory.getSegmentMemory().getTupleQueue().add(new RightTupleEntry(rightTuple, pctx, memory ));
                //log.trace( "NotNode insert queue={} size={} lt={}", System.identityHashCode( memory.getSegmentMemory().getTupleQueue() ), memory.getSegmentMemory().getTupleQueue().size(), rightTuple );
            }  else {
                stagedInsertWasEmpty = memory.getStagedRightTuples().addInsert( rightTuple );
            }

            if (  memory.getAndIncCounter() == 0 && isEmptyBetaConstraints()  ) {
                // NotNodes can only be unlinked, if they have no variable constraints
                memory.linkNode( wm ); 
            } else if ( stagedInsertWasEmpty ) {
                // nothing staged before, notify rule, so it can evaluate network
                memory.getSegmentMemory().notifyRuleLinkSegment( wm );
            } 

            return;
        }     
        
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
        for (LeftTuple leftTuple = getFirstLeftTuple( rightTuple, leftMemory, context, it );  leftTuple != null; ) {      
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

    public void doDeleteRightTuple(final RightTuple rightTuple,
                                   final InternalWorkingMemory wm,
                                   final BetaMemory memory) {
        RightTupleSets stagedRightTuples = memory.getStagedRightTuples();

        boolean stagedDeleteWasEmpty = false;
        if ( isStreamMode() ) {
            stagedDeleteWasEmpty = memory.getSegmentMemory().getTupleQueue().isEmpty();
            memory.getSegmentMemory().getTupleQueue().add(new RightTupleEntry(rightTuple, rightTuple.getPropagationContext(), memory ));
            if ( log.isTraceEnabled() ) {
                log.trace( "JoinNode delete queue={} size={} pctx={} lt={}", System.identityHashCode( memory.getSegmentMemory().getTupleQueue() ), memory.getSegmentMemory().getTupleQueue().size(), PropagationContextImpl.intEnumToString(rightTuple.getPropagationContext()), rightTuple );
            }
        } else {
            stagedDeleteWasEmpty = stagedRightTuples.addDelete( rightTuple );
        }

        if ( memory.getAndDecCounter() == 1 ) {
            memory.linkNode( wm );
        } else if ( stagedDeleteWasEmpty ) {
            // nothing staged before, notify rule, so it can evaluate network
            memory.getSegmentMemory().notifyRuleLinkSegment( wm );
        };
    }

    public void retractRightTuple(final RightTuple rightTuple,
                                  final PropagationContext context,
                                  final InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        rightTuple.setPropagationContext( context );
        if ( isUnlinkingEnabled() ) {
            RightTupleSets stagedRightTuples = memory.getStagedRightTuples();
            boolean  stagedDeleteWasEmpty = false;
            if ( streamMode ) {
                stagedDeleteWasEmpty = memory.getSegmentMemory().getTupleQueue().isEmpty();
                memory.getSegmentMemory().getTupleQueue().add(new RightTupleEntry(rightTuple, context, memory ));
                //log.trace( "NotNode delete queue={} size={} lt={}", System.identityHashCode( memory.getSegmentMemory().getTupleQueue() ), memory.getSegmentMemory().getTupleQueue().size(), rightTuple );
            } else {
                stagedDeleteWasEmpty = stagedRightTuples.addDelete( rightTuple );
            }
            
            if (  memory.getAndDecCounter() == 1 && isEmptyBetaConstraints()  ) {
                // NotNodes can only be unlinked, if they have no variable constraints
                memory.linkNode( workingMemory ); 
            }  else if ( stagedDeleteWasEmpty ) {
                // nothing staged before, notify rule, so it can evaluate network
                memory.getSegmentMemory().notifyRuleLinkSegment( workingMemory );
            }
            return;
        }

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
        LeftTuple firstLeftTuple = getFirstLeftTuple( rightTuple, leftMemory, context, leftIt );
        
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

    public short getType() {
        return NodeTypeEnums.NotNode;
    }
    
    public LeftTuple createPeer(LeftTuple original) {
        NotNodeLeftTuple peer = new NotNodeLeftTuple();
        peer.initPeer( (BaseLeftTuple) original, this );
        original.setPeer( peer );
        return peer;
    }    

    public LeftTuple createLeftTuple(InternalFactHandle factHandle,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new NotNodeLeftTuple(factHandle, sink, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(final InternalFactHandle factHandle,
                                     final LeftTuple leftTuple,
                                     final LeftTupleSink sink) {
        return new NotNodeLeftTuple(factHandle,leftTuple, sink );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     LeftTupleSink sink,
                                     PropagationContext pctx, boolean leftTupleMemoryEnabled) {
        return new NotNodeLeftTuple(leftTuple,sink, pctx, leftTupleMemoryEnabled );
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

    public String toString() {
        ObjectTypeNode source = getObjectTypeNode();
        return "[NotNode(" + this.getId() + ") - " + ((source != null) ? ((ObjectTypeNode) source).getObjectType() : "<source from a subnetwork>") + "]";
    }
}
