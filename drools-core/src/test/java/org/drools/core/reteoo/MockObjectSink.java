/**
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

import java.util.ArrayList;
import java.util.List;

import org.drools.base.base.ObjectType;
import org.drools.base.common.NetworkNode;
import org.drools.base.common.RuleBasePartitionId;
import org.drools.base.reteoo.BaseTerminalNode;
import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.base.rule.Pattern;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PropagationContext;
import org.drools.core.common.ReteEvaluator;
import org.drools.util.bitmask.BitMask;
import org.kie.api.definition.rule.Rule;

public class MockObjectSink extends ObjectSource
    implements
    ObjectSinkNode,
    RightTupleSink {
    private final List     asserted  = new ArrayList();
    private final List     retracted = new ArrayList();
    private final List     updated   = new ArrayList();

    private ObjectSinkNode previousObjectSinkNode;
    private ObjectSinkNode nextObjectSinkNode;

    public void assertObject(final InternalFactHandle factHandle,
                             final PropagationContext context,
                             final ReteEvaluator reteEvaluator) {
        new RightTuple(factHandle, this );
        this.asserted.add( new Object[]{factHandle, context, reteEvaluator} );
    }

    public void retractRightTuple(final TupleImpl rightTuple,
                              final PropagationContext context,
                              final ReteEvaluator reteEvaluator) {
        this.retracted.add( new Object[]{rightTuple.getFactHandle(), context, reteEvaluator} );
    }

    @Override
    public BitMask calculateDeclaredMask(Pattern pattern, ObjectType modifiedType, List<String> settableProperties) {
        return null;
    }

    @Override
    public void updateSink(ObjectSink sink, PropagationContext context, InternalWorkingMemory workingMemory) {

    }

    public List getAsserted() {
        return this.asserted;
    }

    public List getRetracted() {
        return this.retracted;
    }
    
    public List getUpdated() {
        return this.updated;
    }

    /**
     * Returns the next node
     * @return
     *      The next ObjectSinkNode
     */
    public ObjectSinkNode getNextObjectSinkNode() {
        return this.nextObjectSinkNode;
    }

    /**
     * Sets the next node 
     * @param next
     *      The next ObjectSinkNode
     */
    public void setNextObjectSinkNode(final ObjectSinkNode next) {
        this.nextObjectSinkNode = next;
    }

    /**
     * Returns the previous node
     * @return
     *      The previous ObjectSinkNode
     */
    public ObjectSinkNode getPreviousObjectSinkNode() {
        return this.previousObjectSinkNode;
    }

    /**
     * Sets the previous node 
     * @param previous
     *      The previous ObjectSinkNode
     */
    public void setPreviousObjectSinkNode(final ObjectSinkNode previous) {
        this.previousObjectSinkNode = previous;
    }

    public boolean isObjectMemoryEnabled() {
        return false;
    }

    public void setObjectMemoryEnabled(boolean objectMemoryOn) {
    }

    public int getId() {
        return 0;
    }

    public RuleBasePartitionId getPartitionId() {
        return null;
    }

    public int getType() {
        return NodeTypeEnums.MockAlphaNode;
    }

    public void modifyObject(InternalFactHandle factHandle,
                             ModifyPreviousTuples modifyPreviousTuples,
                             PropagationContext context,
                             ReteEvaluator reteEvaluator) {
        TupleImpl rightTuple = modifyPreviousTuples.peekRightTuple(RuleBasePartitionId.MAIN_PARTITION);
        while ( rightTuple != null ) {
            modifyPreviousTuples.removeRightTuple(RuleBasePartitionId.MAIN_PARTITION);
            rightTuple = modifyPreviousTuples.peekRightTuple(RuleBasePartitionId.MAIN_PARTITION);
        }
        this.updated.add( new Object[]{factHandle, context, reteEvaluator} );
        
    }

    public void modifyRightTuple(TupleImpl rightTuple,
                                 PropagationContext context,
                                 ReteEvaluator reteEvaluator) {
        this.updated.add( new Object[]{rightTuple, context, reteEvaluator} );
        
    }

    public void byPassModifyToBetaNode(InternalFactHandle factHandle,
                                       ModifyPreviousTuples modifyPreviousTuples,
                                       PropagationContext context,
                                       ReteEvaluator reteEvaluator) {
    }

    public boolean isAssociatedWith( Rule rule ) {
        return false;
    }

    @Override public Rule[] getAssociatedRules() {
        return new Rule[0];
    }

    @Override
    public void addAssociatedTerminal(BaseTerminalNode terminalNode) {
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

    public boolean thisNodeEquals(final Object object) {
        return false;
    }

    public int nodeHashCode() {return this.hashCode();}

    @Override
    public NetworkNode[] getSinks() {
        return new NetworkNode[0];
    }
}
