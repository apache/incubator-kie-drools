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
package org.drools.core.reteoo.builder;

import org.drools.base.common.NetworkNode;
import org.drools.base.common.RuleBasePartitionId;
import org.drools.base.reteoo.BaseTerminalNode;
import org.drools.core.common.*;
import org.drools.core.reteoo.*;
import org.drools.util.bitmask.BitMask;
import org.drools.util.bitmask.EmptyBitMask;
import org.kie.api.definition.rule.Rule;

import java.util.Objects;

public class BiLinearLeftInputWrapper implements BetaRightInput {

    private LeftTupleSource wrappedSecondLeftInput;
    private BiLinearJoinNode betaNode;
    public LeftTupleSource getWrappedSecondLeftInput() {
        return wrappedSecondLeftInput;
    }

    public void setWrappedSecondLeftInput(LeftTupleSource source) {
        this.wrappedSecondLeftInput = source;
    }

    @Override
    public void setBetaNode(BetaNode betaNode) {
        this.betaNode = (BiLinearJoinNode) betaNode;
    }

    @Override
    public void setPartitionId(BuildContext context, RuleBasePartitionId partitionId) {

    }

    @Override
    public void initInferredMask() {

    }

    @Override
    public boolean inputIsTupleToObjectNode() {
        return false;
    }

    @Override
    public ObjectSource getParent() {
        // BiLinear wraps LeftTupleSource, not ObjectSource
        // BiLinearJoinNode overrides setPartitionId() to handle this properly
        // Return null since LeftTupleSource cannot be cast to ObjectSource
        return null;
    }

    @Override
    public ObjectTypeNode getObjectTypeNode() {
        return null;
    }

    @Override
    public void doAttach(BuildContext context) {
        // Register BiLinearJoinNode as sink on second left input
        // Only register if it's different from first input to avoid duplicate registration
        if (wrappedSecondLeftInput != null && betaNode != null &&
            !wrappedSecondLeftInput.equals(betaNode.getLeftTupleSource())) {
            wrappedSecondLeftInput.addTupleSink(betaNode, context);
        }
    }

    @Override
    public void networkUpdated(UpdateContext updateContext) {

    }

    @Override
    public void initDeclaredMask(BuildContext context) {

    }

    @Override
    public void assertObject(InternalFactHandle factHandle, PropagationContext context, ReteEvaluator reteEvaluator) {

    }

    @Override
    public void modifyObject(InternalFactHandle factHandle, ModifyPreviousTuples modifyPreviousTuples, PropagationContext context, ReteEvaluator reteEvaluator) {

    }

    @Override
    public void byPassModifyToBetaNode(InternalFactHandle factHandle, ModifyPreviousTuples modifyPreviousTuples, PropagationContext context, ReteEvaluator reteEvaluator) {

    }

    @Override
    public void removeAssociatedTerminal(BaseTerminalNode terminalNode) {

    }

    @Override
    public int getAssociatedTerminalsSize() {
        return 0;
    }

    @Override
    public boolean hasAssociatedTerminal(BaseTerminalNode terminalNode) {
        return false;
    }

    @Override
    public NetworkNode[] getSinks() {
        return new NetworkNode[0];
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public RuleBasePartitionId getPartitionId() {
        return null;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public Rule[] getAssociatedRules() {
        return new Rule[0];
    }

    @Override
    public boolean isAssociatedWith(Rule rule) {
        return false;
    }

    @Override
    public void addAssociatedTerminal(BaseTerminalNode terminalNode) {

    }

    @Override
    public boolean removeAssociation(Rule rule, RuleRemovalContext context) {
        return false;
    }

    @Override
    public void addAssociation(Rule rule, BuildContext context) {

    }

    @Override
    public boolean isRightInputPassive() {
        return false;
    }

    @Override
    public BaseNode asBaseNode() {
        return null;
    }

    @Override
    public void updateSink(ObjectSink sink, PropagationContext context, InternalWorkingMemory workingMemory) {

    }

    @Override
    public BitMask getDeclaredMask() {
        return EmptyBitMask.get();
    }

    @Override
    public BitMask getInferredMask() {
        return EmptyBitMask.get();
    }

    @Override
    public BitMask getNegativeMask() {
        return EmptyBitMask.get();
    }

    @Override
    public ObjectSinkNode getNextObjectSinkNode() {
        return null;
    }

    @Override
    public void setNextObjectSinkNode(ObjectSinkNode next) {

    }

    @Override
    public ObjectSinkNode getPreviousObjectSinkNode() {
        return null;
    }

    @Override
    public void setPreviousObjectSinkNode(ObjectSinkNode previous) {

    }

    @Override
    public void setPartitionIdWithSinks(RuleBasePartitionId partitionId) {

    }

    @Override
    public void retractRightTuple(TupleImpl rightTuple, PropagationContext context, ReteEvaluator reteEvaluator) {

    }

    @Override
    public void modifyRightTuple(TupleImpl rightTuple, PropagationContext context, ReteEvaluator reteEvaluator) {

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BiLinearLeftInputWrapper)) return false;

        BiLinearLeftInputWrapper other = (BiLinearLeftInputWrapper) obj;
        return Objects.equals(this.wrappedSecondLeftInput, other.wrappedSecondLeftInput);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wrappedSecondLeftInput);
    }
}