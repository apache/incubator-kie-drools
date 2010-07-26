/**
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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.RuleBasePartitionId;
import org.drools.spi.PropagationContext;

public class MockObjectSink
    implements
    ObjectSinkNode,
    RightTupleSink {
    private final List     asserted  = new ArrayList();
    private final List     retracted = new ArrayList();

    private ObjectSinkNode previousObjectSinkNode;
    private ObjectSinkNode nextObjectSinkNode;

    public void assertObject(final InternalFactHandle factHandle,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {
        new RightTuple( factHandle, this );
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
        // TODO Auto-generated method stub
        return false;
    }

    public void setObjectMemoryEnabled(boolean objectMemoryOn) {
        // TODO Auto-generated method stub
        
    }

    public int getId() {
        // TODO Auto-generated method stub
        return 0;
    }

    public RuleBasePartitionId getPartitionId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void writeExternal( ObjectOutput out ) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public short getType() {
        // TODO Auto-generated method stub
        return NodeTypeEnums.JoinNode;
    }

    public void modifyObject(InternalFactHandle factHandle,
                             ModifyPreviousTuples modifyPreviousTuples,
                             PropagationContext context,
                             InternalWorkingMemory workingMemory) {
        // TODO Auto-generated method stub
        
    }

    public void modifyRightTuple(RightTuple rightTuple,
                                 PropagationContext context,
                                 InternalWorkingMemory workingMemory) {
        // TODO Auto-generated method stub
        
    }
}