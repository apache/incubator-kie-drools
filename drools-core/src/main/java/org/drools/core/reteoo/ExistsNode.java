/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.core.common.BetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.common.PropagationContext;

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

    public ExistsNode() { }

    public ExistsNode(final int id,
                      final LeftTupleSource leftInput,
                      final ObjectSource rightInput,
                      final BetaConstraints joinNodeBinder,
                      final BuildContext context) {
        super( id,
               leftInput,
               rightInput,
               joinNodeBinder,
               context );
        this.tupleMemoryEnabled = context.isTupleMemoryEnabled();
        this.setObjectCount(leftInput.getObjectCount()); // 'exists' nodes do not increase the count
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
    
    public AbstractLeftTuple createLeftTuple(InternalFactHandle factHandle,
                                     boolean leftTupleMemoryEnabled) {
        return new NotNodeLeftTuple(factHandle, this, leftTupleMemoryEnabled );
    }

    public AbstractLeftTuple createLeftTuple(final InternalFactHandle factHandle,
                                     final AbstractLeftTuple leftTuple,
                                     final Sink sink) {
        return new NotNodeLeftTuple(factHandle,leftTuple, sink );
    }

    public AbstractLeftTuple createLeftTuple(AbstractLeftTuple leftTuple,
                                     Sink sink,
                                     PropagationContext pctx,
                                     boolean leftTupleMemoryEnabled) {
        return new NotNodeLeftTuple(leftTuple,sink, pctx, leftTupleMemoryEnabled );
    }

    public AbstractLeftTuple createLeftTuple(AbstractLeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     Sink sink) {
        return new NotNodeLeftTuple(leftTuple, rightTuple, sink );
    }   
    
    public AbstractLeftTuple createLeftTuple(AbstractLeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     AbstractLeftTuple currentLeftChild,
                                     AbstractLeftTuple currentRightChild,
                                     Sink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new NotNodeLeftTuple(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled );        
    }
    
    public AbstractLeftTuple createPeer(AbstractLeftTuple original) {
        NotNodeLeftTuple peer = new NotNodeLeftTuple();
        peer.initPeer((AbstractLeftTuple) original, this);
        original.setPeer( peer );
        return peer;
    }

    public void retractRightTuple(final RightTuple rightTuple,
                                  final PropagationContext pctx,
                                  final ReteEvaluator reteEvaluator) {
        final BetaMemory memory = (BetaMemory) reteEvaluator.getNodeMemory( this );
        rightTuple.setPropagationContext( pctx );
        doDeleteRightTuple( rightTuple, reteEvaluator, memory );
    }

    @Override
    public void modifyRightTuple(RightTuple rightTuple, PropagationContext context, ReteEvaluator reteEvaluator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean doRemove(RuleRemovalContext context, ReteooBuilder builder) {
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
