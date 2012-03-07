/*
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

import org.drools.common.Memory;
import org.drools.common.InternalWorkingMemory;
import org.drools.core.util.index.RightTupleList;
import org.drools.rule.ContextEntry;

public class BetaMemory
        implements
        Memory {

    private static final long serialVersionUID = 510l;

    private LeftTupleMemory   leftTupleMemory;
    private RightTupleMemory  rightTupleMemory;

    private RightTupleList    stagedAssertRightTupleMemory;
    private RightTupleList    stagedRetractRightTupleMemory;
    private RightTupleList    stagedModifyRightTupleMemory;
    private ContextEntry[]    context;

    // the node type this memory belongs to
    private short             nodeType;

    private SegmentMemory     segmentMemory;

    private long              nodePosMaskBit;

    private int               counter;

    public BetaMemory() {
    }

    public BetaMemory(final LeftTupleMemory tupleMemory,
                      final RightTupleMemory objectMemory,
                      final ContextEntry[] context,
                      final short nodeType) {
        this.leftTupleMemory = tupleMemory;
        this.rightTupleMemory = objectMemory;
        this.stagedAssertRightTupleMemory = new RightTupleList();
        this.stagedRetractRightTupleMemory = new RightTupleList();
        this.stagedModifyRightTupleMemory = new RightTupleList();
        this.context = context;
        this.nodeType = nodeType;
    }

    public void addStagedAssertRightTuple(RightTuple rightTuple,
                                          InternalWorkingMemory wm) {
        stagedAssertRightTupleMemory.add( rightTuple );
    }

    public void removeStagedAssertRightTuple(RightTuple rightTuple,
                                             InternalWorkingMemory wm) {
        stagedAssertRightTupleMemory.remove( rightTuple );
    }

    public RightTupleList getStagedAssertRightTupleList() {
        return stagedAssertRightTupleMemory;
    }
    
    public void addStagedRetractRightTuple(RightTuple rightTuple,
                                           InternalWorkingMemory wm) {
        stagedAssertRightTupleMemory.add( rightTuple );
    }

    public void removeStagedRetractRightTuple(RightTuple rightTuple,
                                              InternalWorkingMemory wm) {
        stagedAssertRightTupleMemory.remove( rightTuple );
    }

    public RightTupleList getStagedRetractRightTupleList() {
        return stagedAssertRightTupleMemory;
    }
    
    public void addStagedModifyRightTuple(RightTuple rightTuple,
                                          InternalWorkingMemory wm) {
        stagedAssertRightTupleMemory.add( rightTuple );
    }

    public void removeStagedModifyRightTuple(RightTuple rightTuple,
                                             InternalWorkingMemory wm) {
        stagedAssertRightTupleMemory.remove( rightTuple );
    }

    public RightTupleList getStagedModifyRightTupleList() {
        return stagedAssertRightTupleMemory;
    }

    public RightTupleMemory getRightTupleMemory() {
        return this.rightTupleMemory;
    }

    public LeftTupleMemory getLeftTupleMemory() {
        return this.leftTupleMemory;
    }

    /**
     * @return the context
     */
    public ContextEntry[] getContext() {
        return context;
    }

    public void linkNode(InternalWorkingMemory wm) {
        segmentMemory.linkNode( nodePosMaskBit, wm );
    }

    public void unlinkNode(InternalWorkingMemory wm) {
        segmentMemory.unlinkNode( nodePosMaskBit, wm );
    }

    public short getNodeType() {
        return this.nodeType;
    }

    public SegmentMemory getSegmentMemory() {
        return segmentMemory;
    }

    public void setSegmentMemory(SegmentMemory segmentMemory) {
        this.segmentMemory = segmentMemory;
    }

    public long getNodePosMaskBit() {
        return nodePosMaskBit;
    }

    public void setNodePosMaskBit(long segmentPos) {
        this.nodePosMaskBit = segmentPos;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getAndIncCounter() {
        return counter++;
    }

    public int getAndDecCounter() {
        return --counter;
    }

    public void clearStagingMemory() {
        stagedAssertRightTupleMemory = null;
        stagedModifyRightTupleMemory = null;
        stagedRetractRightTupleMemory = null;
    }
    
    public void createStagingMemory() {
        stagedAssertRightTupleMemory = new RightTupleList();
        stagedModifyRightTupleMemory = new RightTupleList();
        stagedRetractRightTupleMemory = new RightTupleList();
        stagedAssertRightTupleMemory.setStagingMemory( true );
        stagedModifyRightTupleMemory.setStagingMemory( true );
        stagedRetractRightTupleMemory.setStagingMemory( true );
    }
}
