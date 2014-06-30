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

package org.drools.core.reteoo;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.RightTupleSets;
import org.drools.core.common.SynchronizedRightTupleSets;
import org.drools.core.rule.ContextEntry;
import org.drools.core.util.AbstractBaseLinkedListNode;

public class BetaMemory extends AbstractBaseLinkedListNode<Memory>
        implements
        Memory {

    private static final long serialVersionUID = 510l;
    private LeftTupleMemory            leftTupleMemory;
    private RightTupleMemory           rightTupleMemory;
    private RightTupleSets             stagedRightTuples;
    private ContextEntry[]             context;
    // the node type this memory belongs to
    private short                      nodeType;
    private SegmentMemory              segmentMemory;
    private long                       nodePosMaskBit;
    private int                        counter;
    private RiaPathMemory              riaRuleMemory;

    public BetaMemory() {
    }

    public BetaMemory(final LeftTupleMemory tupleMemory,
                      final RightTupleMemory objectMemory,
                      final ContextEntry[] context,
                      final short nodeType) {
        this.leftTupleMemory = tupleMemory;
        this.rightTupleMemory = objectMemory;
        this.stagedRightTuples = new SynchronizedRightTupleSets(this);
        this.context = context;
        this.nodeType = nodeType;
    }

    public RightTupleSets getStagedRightTuples() {
        return stagedRightTuples;
    }

    public void setStagedRightTuples(RightTupleSets stagedRightTuples) {
        this.stagedRightTuples = stagedRightTuples;
    }

    public RightTupleMemory getRightTupleMemory() {
        return this.rightTupleMemory;
    }

    public LeftTupleMemory getLeftTupleMemory() {
        return this.leftTupleMemory;
    }

    public RiaPathMemory getRiaRuleMemory() {
        return riaRuleMemory;
    }

    public void setRiaRuleMemory(RiaPathMemory riaRuleMemory) {
        this.riaRuleMemory = riaRuleMemory;
    }

    /**
     * @return the context
     */
    public ContextEntry[] getContext() {
        return context;
    }

    public void linkNode(InternalWorkingMemory wm) {
        segmentMemory.linkNode(nodePosMaskBit, wm);
    }

    public void unlinkNode(InternalWorkingMemory wm) {
        segmentMemory.unlinkNode(nodePosMaskBit, wm);
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
        return counter--;
    }

    public void setNodeDirty(InternalWorkingMemory wm) {
        segmentMemory.notifyRuleLinkSegment(wm, nodePosMaskBit);
    }

    public void setNodeDirtyWithoutNotify() {
        segmentMemory.updateDirtyNodeMask( nodePosMaskBit );
    }

    public void setNodeCleanWithoutNotify() {
        segmentMemory.updateCleanNodeMask( nodePosMaskBit );
    }

    public void reset() {
        if (leftTupleMemory != null) {
            leftTupleMemory.clear();
        }
        if (rightTupleMemory != null) {
            rightTupleMemory.clear();
        }
        stagedRightTuples.resetAll();
        counter = 0;
    }
}
