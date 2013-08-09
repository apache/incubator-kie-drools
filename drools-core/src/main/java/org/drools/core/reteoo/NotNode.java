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

import org.drools.core.common.BetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.RightTupleSets;
import org.drools.core.phreak.RightTupleEntry;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.PropagationContext;

import static org.drools.core.util.BitMaskUtil.intersect;

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

    @Override
    public void assertRightTuple(RightTuple rightTuple, PropagationContext context, InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }

    public void assertObject( final InternalFactHandle factHandle,
                              final PropagationContext pctx,
                              final InternalWorkingMemory wm ) {
        final BetaMemory memory = (BetaMemory) getBetaMemoryFromRightInput(this, wm);

        RightTuple rightTuple = createRightTuple( factHandle,
                                                  this,
                                                  pctx);

        rightTuple.setPropagationContext(pctx);

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
    }

    public void retractRightTuple(final RightTuple rightTuple,
                                  final PropagationContext context,
                                  final InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        rightTuple.setPropagationContext( context );
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
    }

    @Override
    public void modifyRightTuple(RightTuple rightTuple, PropagationContext context, InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void assertLeftTuple(LeftTuple leftTuple, PropagationContext context, InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void retractLeftTuple(LeftTuple leftTuple, PropagationContext context, InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void modifyLeftTuple(InternalFactHandle factHandle, ModifyPreviousTuples modifyPreviousTuples, PropagationContext context, InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void modifyLeftTuple(LeftTuple leftTuple, PropagationContext context, InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateSink(LeftTupleSink sink, PropagationContext context, InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void doRemove(RuleRemovalContext context, ReteooBuilder builder, InternalWorkingMemory[] workingMemories) {
        if ( !isInUse() ) {
            getLeftTupleSource().removeTupleSink( this );
            getRightInput().removeObjectSink( this );
        }
    }

    @Override
    public void modifyObject(InternalFactHandle factHandle, ModifyPreviousTuples modifyPreviousTuples, PropagationContext context, InternalWorkingMemory wm) {
        RightTuple rightTuple = modifyPreviousTuples.peekRightTuple();

        // if the peek is for a different OTN we assume that it is after the current one and then this is an assert
        while ( rightTuple != null &&
                (( BetaNode ) rightTuple.getRightTupleSink()).getRightInputOtnId().before( getRightInputOtnId() ) ) {
            modifyPreviousTuples.removeRightTuple();

            // we skipped this node, due to alpha hashing, so retract now
            rightTuple.setPropagationContext( context );
            BetaMemory bm  = getBetaMemory( (BetaNode) rightTuple.getRightTupleSink(), wm );
            (( BetaNode ) rightTuple.getRightTupleSink()).doDeleteRightTuple( rightTuple, wm, bm );
            rightTuple = modifyPreviousTuples.peekRightTuple();
        }

        if ( rightTuple != null && (( BetaNode ) rightTuple.getRightTupleSink()).getRightInputOtnId().equals(getRightInputOtnId()) ) {
            modifyPreviousTuples.removeRightTuple();
            rightTuple.reAdd();
            if ( rightTuple.getStagedType() != LeftTuple.INSERT ) {
                // things staged as inserts, are left as inserts and use the pctx associated from the time of insertion
                rightTuple.setPropagationContext( context );
            }
            if ( intersect( context.getModificationMask(), getRightInferredMask() ) ) {
                // RightTuple previously existed, so continue as modify
                BetaMemory bm = getBetaMemory( this, wm );
                rightTuple.setPropagationContext( context );
                doUpdateRightTuple(rightTuple, wm, bm);
            }
        } else {
            if ( intersect( context.getModificationMask(), getRightInferredMask() ) ) {
                // RightTuple does not exist for this node, so create and continue as assert
                assertObject( factHandle,
                              context,
                              wm );
            }
        }
    }

    public boolean isLeftUpdateOptimizationAllowed() {
        return getRawConstraints().isLeftUpdateOptimizationAllowed();
    }
}
