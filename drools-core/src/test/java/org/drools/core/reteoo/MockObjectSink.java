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

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.spi.PropagationContext;
import org.kie.api.definition.rule.Rule;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

public class MockObjectSink
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
                             final InternalWorkingMemory workingMemory) {
        new RightTupleImpl( factHandle, this );
        this.asserted.add( new Object[]{factHandle, context, workingMemory} );
    }

    public void retractRightTuple(final RightTuple rightTuple,
                              final PropagationContext context,
                              final InternalWorkingMemory workingMemory) {
        this.retracted.add( new Object[]{rightTuple.getFactHandle(), context, workingMemory} );
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

    public void writeExternal( ObjectOutput out ) throws IOException {
    }

    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
    }

    @Override
    public void assertRightTuple(RightTuple rightTuple, PropagationContext context, InternalWorkingMemory workingMemory) {
    }

    public short getType() {
        return NodeTypeEnums.JoinNode;
    }

    public void modifyObject(InternalFactHandle factHandle,
                             ModifyPreviousTuples modifyPreviousTuples,
                             PropagationContext context,
                             InternalWorkingMemory workingMemory) {
        RightTuple rightTuple = modifyPreviousTuples.peekRightTuple();
        while ( rightTuple != null ) {
            modifyPreviousTuples.removeRightTuple();
            rightTuple = modifyPreviousTuples.peekRightTuple();
        }
        this.updated.add( new Object[]{factHandle, context, workingMemory} );
        
    }

    public void modifyRightTuple(RightTuple rightTuple,
                                 PropagationContext context,
                                 InternalWorkingMemory workingMemory) {
        this.updated.add( new Object[]{rightTuple, context, workingMemory} );
        
    }

    public void byPassModifyToBetaNode(InternalFactHandle factHandle,
                                       ModifyPreviousTuples modifyPreviousTuples,
                                       PropagationContext context,
                                       InternalWorkingMemory workingMemory) {
    }

    public int getAssociationsSize() {
        return 0;
    }

    public int getAssociatedRuleSize() {
        return 0;
    }

    public int getAssociationsSize(Rule rule) {
        return 0;
    }

    public boolean isAssociatedWith( Rule rule ) {
        return false;
    }

    public ObjectTypeNode.Id getRightInputOtnId() {
        return null;
    }

    public boolean thisNodeEquals(final Object object) {
        return false;
    }

    public int nodeHashCode() {return this.hashCode();}

    public void setPartitionIdWithSinks( RuleBasePartitionId partitionId ) { }
}
