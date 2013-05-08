package org.drools.core.reteoo;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.LeftTupleSets;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.NetworkNode;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SegmentMemory extends LinkedList<SegmentMemory>
        implements
        LinkedListNode<SegmentMemory> {

    protected static transient Logger log = LoggerFactory.getLogger(SegmentMemory.class);
    private NetworkNode        rootNode;
    private NetworkNode        tipNode;
    private LinkedList<Memory> nodeMemories;
    private long               linkedNodeMask;
    private long               allLinkedMaskTest;
    private List<PathMemory>   pathMemories;
    private long               segmentPosMaskBit;
    private int                pos;
    private volatile LeftTupleSets      stagedLeftTuples;
    private boolean            active;
    private SegmentMemory      previous;
    private SegmentMemory      next;

    public SegmentMemory(NetworkNode rootNode) {
        this.rootNode = rootNode;
        this.pathMemories = new ArrayList<PathMemory>(1);
        this.nodeMemories = new LinkedList<Memory>();

        this.stagedLeftTuples = new LeftTupleSets();
    }

    public NetworkNode getRootNode() {
        return rootNode;
    }

    public NetworkNode getTipNode() {
        return tipNode;
    }

    public void setTipNode(NetworkNode tipNode) {
        this.tipNode = tipNode;
    }

    public LeftTupleSink getSinkFactory() {
        return (LeftTupleSink) rootNode;
    }

    public void setSinkFactory(LeftTupleSink sink) {
    }

    public Memory createNodeMemory(MemoryFactory memoryFactory,
                                   InternalWorkingMemory wm) {
        Memory memory = wm.getNodeMemory(memoryFactory);
        nodeMemories.add(memory);
        return memory;
    }

    public LinkedList<Memory> getNodeMemories() {
        return nodeMemories;
    }

    public long getLinkedNodeMask() {
        return linkedNodeMask;
    }

    public void setLinkedNodeMask(long linkedNodeMask) {
        this.linkedNodeMask = linkedNodeMask;
    }

    public String getRuleNames() {
        StringBuilder sbuilder = new StringBuilder();
        for (int i = 0; i < pathMemories.size(); i++) {
            if (i > 0) {
                sbuilder.append(", ");
            }
            sbuilder.append(pathMemories.get(i));
        }

        return sbuilder.toString();
    }

    public void linkNode(long mask,
                         InternalWorkingMemory wm) {
        linkedNodeMask = linkedNodeMask | mask;
        if (log.isTraceEnabled()) {
            log.trace("LinkNode notify=true nmask={} smask={} spos={} rules={}", mask, linkedNodeMask, pos, getRuleNames());
        }

        notifyRuleLinkSegment(wm);
    }

    public void linkNodeWithoutRuleNotify(long mask) {
        linkedNodeMask = linkedNodeMask | mask;

        if (log.isTraceEnabled()) {
            log.trace("LinkNode notify=false nmask={} smask={} spos={} rules={}", mask, linkedNodeMask, pos, getRuleNames());
        }

        if (isSegmentLinked()) {
            for (int i = 0, length = pathMemories.size(); i < length; i++) {
                // do not use foreach, don't want Iterator object creation
                pathMemories.get(i).linkNodeWithoutRuleNotify(segmentPosMaskBit);
            }
        }
    }

    public void notifyRuleLinkSegment(InternalWorkingMemory wm) {
        if (isSegmentLinked()) {
            for (int i = 0, length = pathMemories.size(); i < length; i++) {
                // do not use foreach, don't want Iterator object creation
                pathMemories.get(i).linkSegment(segmentPosMaskBit,
                                                wm);
            }
        }
    }

    public void unlinkNode(long mask,
                           InternalWorkingMemory wm) {
        if (log.isTraceEnabled()) {
            log.trace("UnlinkNode notify=true nmask={} smask={} spos={} rules={}", mask, linkedNodeMask, pos, getRuleNames());
        }

        // some node unlinking does not unlink the segment, such as nodes after a Branch CE
        boolean linked = isSegmentLinked();
        linkedNodeMask = linkedNodeMask ^ mask;
        if (linked && !isSegmentLinked()) {
            for (int i = 0, length = pathMemories.size(); i < length; i++) {
                // do not use foreach, don't want Iterator object creation
                pathMemories.get(i).unlinkedSegment(segmentPosMaskBit,
                                                    wm);
            }
        }
    }

    public void unlinkNodeWithoutRuleNotify(long mask) {
        if (log.isTraceEnabled()) {
            log.trace("UnlinkNode notify=false nmask={} smask={} spos={} rules={}", mask, linkedNodeMask, pos, getRuleNames());
        }
        linkedNodeMask = linkedNodeMask ^ mask;
    }

    public long getAllLinkedMaskTest() {
        return allLinkedMaskTest;
    }

    public void setAllLinkedMaskTest(long allLinkedTestMask) {
        this.allLinkedMaskTest = allLinkedTestMask;
    }

    public boolean isSegmentLinked() {
        return (linkedNodeMask & allLinkedMaskTest) == allLinkedMaskTest;
    }

    public List<PathMemory> getPathMemories() {
        return pathMemories;
    }

    public void setPathMemories(List<PathMemory> ruleSegments) {
        this.pathMemories = ruleSegments;
    }

    public long getSegmentPosMaskBit() {
        return segmentPosMaskBit;
    }

    public void setSegmentPosMaskBit(long nodeSegmenMask) {
        this.segmentPosMaskBit = nodeSegmenMask;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean evaluating) {
        this.active = evaluating;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public LeftTupleSets getStagedLeftTuples() {
        return stagedLeftTuples;
    }

    public void setStagedTuples(LeftTupleSets stagedTuples) {
        this.stagedLeftTuples = stagedTuples;
    }

    public SegmentMemory getNext() {
        return this.next;
    }

    public void setNext(SegmentMemory next) {
        this.next = next;
    }

    public SegmentMemory getPrevious() {
        return this.previous;
    }

    public void setPrevious(SegmentMemory previous) {
        this.previous = previous;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime * rootNode.getId();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (!super.equals(obj)) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        SegmentMemory other = (SegmentMemory) obj;
        if (rootNode == null) {
            if (other.rootNode != null) { return false; }
        } else if (rootNode.getId() != other.rootNode.getId()) { return false; }
        return true;
    }

}
