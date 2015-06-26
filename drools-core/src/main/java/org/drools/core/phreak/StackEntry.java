package org.drools.core.phreak;

import org.drools.core.common.LeftTupleSets;
import org.drools.core.common.Memory;
import org.drools.core.common.NetworkNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTupleSinkNode;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.util.AbstractBaseLinkedListNode;

/**
* Created with IntelliJ IDEA.
* User: mdproctor
* Date: 03/05/2013
* Time: 15:47
* To change this template use File | Settings | File Templates.
*/
public class StackEntry extends AbstractBaseLinkedListNode<StackEntry> {
    private LeftInputAdapterNode liaNode;
    private long                 bit;
    private NetworkNode          node;
    private LeftTupleSinkNode    sink;
    private PathMemory           pmem;
    private Memory               nodeMem;
    private SegmentMemory[]      smems;
    private int                  smemIndex;
    private LeftTupleSets        trgTuples;
    private boolean              resumeFromNextNode;
    private boolean              processRian;


    public StackEntry(LeftInputAdapterNode liaNode,
                      NetworkNode node,
                      long bit,
                      LeftTupleSinkNode sink,
                      PathMemory pmem,
                      Memory nodeMem,
                      SegmentMemory[] smems,
                      int smemIndex,
                      LeftTupleSets trgTuples,
                      boolean resumeFromNextNode,
                      boolean processRian) {
        this.liaNode = liaNode;
        this.bit = bit;
        this.node = node;
        this.sink = sink;
        this.pmem = pmem;
        this.nodeMem = nodeMem;
        this.smems = smems;
        this.smemIndex = smemIndex;
        this.trgTuples = trgTuples;
        this.resumeFromNextNode = resumeFromNextNode;
        this.processRian = processRian;
    }



    public LeftInputAdapterNode getLiaNode() {
        return this.liaNode;
    }

    public long getBit() {
        return bit;
    }

    public NetworkNode getNode() {
        return node;
    }

    public PathMemory getRmem() {
        return pmem;
    }

    public Memory getNodeMem() {
        return nodeMem;
    }

    public SegmentMemory[] getSmems() {
        return smems;
    }

    public int getSmemIndex() {
        return smemIndex;
    }

    public LeftTupleSets getTrgTuples() {
        return trgTuples;
    }

    public LeftTupleSinkNode getSink() {
        return sink;
    }

    public boolean isResumeFromNextNode() {
        return resumeFromNextNode;
    }


    public boolean isProcessRian() {
        return processRian;
    }
}
