/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.reteoo;

import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.core.common.BetaConstraints;
import org.drools.core.common.BiLinearBetaConstraints;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.util.FastIterator;

public class BiLinearJoinNode extends JoinNode {

    private static final long serialVersionUID = 510l;

    // Cross-network declaration context for variable resolution
    protected BiLinearDeclarationContext declarationContext;

    public BiLinearJoinNode() {
    }

    public BiLinearJoinNode(final int id,
                           final LeftTupleSource leftInput,
                           final BetaRightInput rightInput,
                           final BetaConstraints constraints,
                           final BuildContext context) {
        super(id, leftInput, rightInput, createBiLinearConstraints(constraints, leftInput), context);
    }

    @Override
    public void doAttach(BuildContext context) {
        super.doAttach(context);
    }

    /**
     * Override setPartitionId to handle LeftTupleSource as second input instead of ObjectSource.
     * BiLinear has LeftTupleSource as second input, not ObjectSource, so we need special handling.
     */
    @Override
    public void setPartitionId(BuildContext context, org.drools.base.common.RuleBasePartitionId partitionId) {
        LeftTupleSource secondInput = getSecondLeftInput();
        if (secondInput != null) {
            org.drools.base.common.RuleBasePartitionId parentId = secondInput.getPartitionId();
            if (parentId != org.drools.base.common.RuleBasePartitionId.MAIN_PARTITION && !parentId.equals(partitionId)) {
                this.partitionId = parentId;
                rightInput.setPartitionId(context, this.partitionId);
                context.setPartitionId(this.partitionId);
                leftInput.setSourcePartitionId(context, this.partitionId);
                return;
            }
        }
        this.partitionId = partitionId;
    }

    private static BetaConstraints createBiLinearConstraints(BetaConstraints originalConstraints,
                                                           LeftTupleSource leftInput) {
        return new BiLinearBetaConstraints(originalConstraints);
    }

    private BiLinearDeclarationContext createDeclarationContext(LeftTupleSource leftInput,
                                                              LeftTupleSource secondLeftInput) {
        int secondNetworkOffset = leftInput != null ? leftInput.getObjectCount() : 0;

        return new BiLinearDeclarationContext(
            leftInput,
            secondLeftInput,
            secondNetworkOffset
        );
    }

    public LeftTupleSource getSecondLeftInput() {
        return ((org.drools.core.reteoo.builder.BiLinearLeftInputWrapper) rightInput).getWrappedSecondLeftInput();
    }

    public int getFirstNetworkSize() {
        return getLeftTupleSource().getObjectCount();
    }

    public void linkOutsideLeftInput(LeftTupleSource secondLeftInput) {

        ((org.drools.core.reteoo.builder.BiLinearLeftInputWrapper) rightInput).setWrappedSecondLeftInput(secondLeftInput);

        if (secondLeftInput != null && !secondLeftInput.equals(getLeftTupleSource())) {
            secondLeftInput.addTupleSink(this);
        }

        this.setObjectCount(leftInput.getObjectCount() + secondLeftInput.getObjectCount());

        this.declarationContext = createDeclarationContext(getLeftTupleSource(), secondLeftInput);
               getBiLinearConstraints().setDeclarationContext(
                       declarationContext
               );
    }

    public BiLinearBetaConstraints getBiLinearConstraints() {
        return (BiLinearBetaConstraints) getRawConstraints();
    }

    @Override
    public int getType() {
        return NodeTypeEnums.BiLinearJoinNode;
    }

    /**
     * Override getRightIterator to properly handle second left input tuples stored in right memory
     */
    @Override
    public FastIterator<TupleImpl> getRightIterator(TupleMemory memory) {
        return memory.fastIterator();
    }

    /**
     * Override getFirstRightTuple to properly handle second left input tuples
     * Note: Returns TupleImpl cast to RightTuple for compatibility
     */
    @Override
    public RightTuple getFirstRightTuple(final TupleImpl leftTuple,
                                        final TupleMemory memory,
                                        final FastIterator<TupleImpl> it) {
        // For BiLinear, return first tuple from second left input memory
        // This is actually a LeftTuple but we cast it for compatibility
        return (RightTuple) memory.getFirst(leftTuple);
    }

    /**
     * BiLinear-specific method to get first tuple from second network as TupleImpl
     * This provides cleaner semantics than treating second network tuples as "right" tuples
     */
    public TupleImpl getFirstSecondNetworkTuple(final TupleImpl leftTuple,
                                               final TupleMemory memory,
                                               final FastIterator<TupleImpl> it) {
        return memory.getFirst(leftTuple);
    }

    public FastIterator<TupleImpl> getSecondNetworkIterator(TupleMemory memory) {
        return memory.fastIterator();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof BiLinearJoinNode) || this.hashCode() != object.hashCode()) {
            return false;
        }

        BiLinearJoinNode other = (BiLinearJoinNode) object;

        return this.getClass() == other.getClass() &&
               this.constraints.equals(other.constraints) &&
               this.rightInput.equals(other.rightInput) &&
               this.leftInput.getId() == other.leftInput.getId();
    }

    @Override
    public String toString() {
        return "[BiLinearJoinNode(" + this.getId() + ") - " +
               "FirstInput: " + (getLeftTupleSource() != null ? getLeftTupleSource().getId() : "null") +
               ", SecondInput: " + (getSecondLeftInput() != null ? getSecondLeftInput().getId() : "null") +
                "]";
    }
}