/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

import org.drools.core.common.BetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.PropagationContext;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import static org.drools.core.phreak.AddRemoveRule.flushLeftTupleIfNecessary;

public class NotNode extends BetaNode {
    private static final long serialVersionUID = 510l;

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
        super(id,
              leftInput,
              rightInput,
              joinNodeBinder,
              context);
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
                                     Sink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new NotNodeLeftTuple(factHandle, sink, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(final InternalFactHandle factHandle,
                                     final LeftTuple leftTuple,
                                     final Sink sink) {
        return new NotNodeLeftTuple(factHandle,leftTuple, sink );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     Sink sink,
                                     PropagationContext pctx, boolean leftTupleMemoryEnabled) {
        return new NotNodeLeftTuple(leftTuple,sink, pctx, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     Sink sink) {
        return new NotNodeLeftTuple(leftTuple, rightTuple, sink );
    }   
    
    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTuple currentLeftChild,
                                     LeftTuple currentRightChild,
                                     Sink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new NotNodeLeftTuple(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled );        
    }       

    public String toString() {
        ObjectTypeNode source = getObjectTypeNode();
        return "[NotNode(" + this.getId() + ") - " + ((source != null) ? source.getObjectType() : "<source from a subnetwork>") + "]";
    }

    @Override
    public void assertRightTuple(RightTuple rightTuple, PropagationContext context, InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }

    public void assertObject( final InternalFactHandle factHandle,
                              final PropagationContext pctx,
                              final InternalWorkingMemory wm ) {
        final BetaMemory memory = getBetaMemoryFromRightInput(this, wm);

        RightTuple rightTuple = createRightTuple( factHandle,
                                                  this,
                                                  pctx);

        rightTuple.setPropagationContext(pctx);

        // strangely we link here, this is actually just to force a network evaluation
        // The assert is then processed and the rule unlinks then.
        // This is because we need the first RightTuple to link with it's blocked
        if ( memory.getStagedRightTuples().isEmpty() ) {
            memory.setNodeDirtyWithoutNotify();
        }
        boolean stagedInsertWasEmpty = memory.getStagedRightTuples().addInsert( rightTuple );

        if (  memory.getAndIncCounter() == 0 && isEmptyBetaConstraints()  ) {
            // NotNodes can only be unlinked, if they have no variable constraints
            memory.linkNode( wm );
        } else if ( stagedInsertWasEmpty ) {
            // nothing staged before, notify rule, so it can evaluate network
            memory.setNodeDirty(wm);
        }

        flushLeftTupleIfNecessary(wm, memory.getSegmentMemory(), null, isStreamMode());
    }

    public void retractRightTuple(final RightTuple rightTuple,
                                  final PropagationContext pctx,
                                  final InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        rightTuple.setPropagationContext( pctx );
        doDeleteRightTuple( rightTuple,
                            workingMemory,
                            memory );
    }

    public void doDeleteRightTuple(final RightTuple rightTuple,
                                   final InternalWorkingMemory wm,
                                   final BetaMemory memory) {
        TupleSets<RightTuple> stagedRightTuples = memory.getStagedRightTuples();
        if ( stagedRightTuples.isEmpty() ) {
            memory.setNodeDirtyWithoutNotify();
        }
        boolean stagedDeleteWasEmpty = stagedRightTuples.addDelete( rightTuple );

        if (  memory.getAndDecCounter() == 1 && isEmptyBetaConstraints()  ) {
            // NotNodes can only be unlinked, if they have no variable constraints
            memory.linkNode( wm );
        }  else if ( stagedDeleteWasEmpty ) {
            // nothing staged before, notify rule, so it can evaluate network
            memory.setNodeDirty( wm );
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
    public boolean doRemove(RuleRemovalContext context, ReteooBuilder builder, InternalWorkingMemory[] workingMemories) {
        if ( !isInUse() ) {
            getLeftTupleSource().removeTupleSink( this );
            getRightInput().removeObjectSink( this );
            return true;
        }
        return false;
    }

    public boolean isLeftUpdateOptimizationAllowed() {
        return getRawConstraints().isLeftUpdateOptimizationAllowed();
    }
}
