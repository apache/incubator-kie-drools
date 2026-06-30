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
import org.drools.core.reteoo.builder.BiLinearLeftInputWrapper;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.util.FastIterator;
import org.kie.api.definition.rule.Rule;

public class BiLinearJoinNode extends JoinNode {

    private static final long serialVersionUID = 510l;

    public BiLinearJoinNode() {
    }

    public BiLinearJoinNode(final int id,
                           final LeftTupleSource leftInput,
                           final BetaConstraints constraints,
                           final BuildContext context) {
        super(id, leftInput, new BiLinearLeftInputWrapper(), createBiLinearConstraints(constraints), context);
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

    private static BetaConstraints createBiLinearConstraints(BetaConstraints originalConstraints) {
        return new BiLinearBetaConstraints(originalConstraints);
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
    }

    public void unlinkSecondLeftInput() {
        LeftTupleSource secondInput = getSecondLeftInput();
        if (secondInput != null && !secondInput.equals(getLeftTupleSource())) {
            secondInput.removeTupleSink(this);
        }
        ((org.drools.core.reteoo.builder.BiLinearLeftInputWrapper) rightInput).setWrappedSecondLeftInput(null);
    }

    @Override
    public boolean doRemove(RuleRemovalContext context, ReteooBuilder builder) {
        if (!isInUse()) {
            // Remove from first left input (standard BetaNode behavior)
            getLeftTupleSource().removeTupleSink(this);

            // Also remove from second left input (BiLinear-specific)
            LeftTupleSource secondInput = getSecondLeftInput();
            if (secondInput != null && !secondInput.equals(getLeftTupleSource())) {
                secondInput.removeTupleSink(this);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAssociation(Rule rule, RuleRemovalContext context) {
        boolean result = super.removeAssociation(rule, context);

        // If this node is no longer in use after removing the association,
        // we need to clean up the second left input reference
        if (!isInUse()) {
            unlinkSecondLeftInput();
        }

        return result;
    }

    public BiLinearBetaConstraints getBiLinearConstraints() {
        return (BiLinearBetaConstraints) getRawConstraints();
    }

    @Override
    public int getType() {
        return NodeTypeEnums.BiLinearJoinNode;
    }

    /**
     * Get first tuple from second network.
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