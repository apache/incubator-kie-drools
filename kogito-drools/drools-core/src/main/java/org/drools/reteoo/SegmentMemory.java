package org.drools.reteoo;

import java.util.ArrayList;
import java.util.List;

import org.drools.common.InternalWorkingMemory;
import org.drools.common.Memory;
import org.drools.common.MemoryFactory;
import org.drools.common.NetworkNode;
import org.drools.common.StagedLeftTuples;
import org.drools.core.util.Entry;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListNode;
import org.drools.core.util.index.LeftTupleList;
import org.drools.core.util.index.RightTupleList;
import org.drools.phreak.SegmentPropagator;

public class SegmentMemory extends LinkedList<SegmentMemory> implements LinkedListNode<SegmentMemory>{
    private LeftTupleSource   rootNode;
    private NetworkNode       tipNode;
    
    private LinkedList<Memory> nodeMemories;
    
    private long              linkedNodeMask;

    private long              allLinkedMaskTest;

    private List<RuleMemory>  ruleMemories;

    private long              segmentPosMaskBit;
    
    private int               pos;

    private StagedLeftTuples         stagedLeftTuples;    

    private int               counter;

    private boolean           active;
    
    private SegmentMemory     previous;
    private SegmentMemory     next;
    

    public SegmentMemory(LeftTupleSource rootNode) {
        this.rootNode = rootNode;
        this.ruleMemories = new ArrayList<RuleMemory>( 1 );
        this.nodeMemories = new LinkedList<Memory>(); 
        
        this.stagedLeftTuples = new StagedLeftTuples();
    }
        
    public LeftTupleSource getRootNode() {
        return rootNode;
    }
    
    public NetworkNode getTipNode() {
        return tipNode;
    }
    
    public void setTipNode(NetworkNode tipNode) {
        this.tipNode = tipNode;
    }
    
    public Memory createNodeMemory(MemoryFactory memoryFactory, InternalWorkingMemory wm) {
        Memory memory = wm.getNodeMemory( memoryFactory );        
        nodeMemories.add( memory );
        return memory;
    }
    
    public LinkedList<Memory> getNodeMemories() {
        return nodeMemories;
    }    

    public long getLinkedNodeMask() {
        return linkedNodeMask;
    }

    public void setLinkedNodeMask(long linkedSegmentMask) {
        this.linkedNodeMask = linkedSegmentMask;
    }

    public void linkNode(long mask,
                         InternalWorkingMemory wm) {
        linkedNodeMask = linkedNodeMask | mask;
        if ( isSegmentLinked() ) {
            notifyRuleLinkSegment( wm );
        }
    }

    public void notifyRuleLinkSegment(InternalWorkingMemory wm) {
        for ( int i = 0, length = ruleMemories.size(); i < length; i++ ) {
            // do not use foreach, don't want Iterator object creation
            ruleMemories.get( i ).linkSegment( segmentPosMaskBit, wm );
        }
    }

    public void unlinkNode(long mask,
                           InternalWorkingMemory wm) {
        if ( isSegmentLinked() ) {
            for ( int i = 0, length = ruleMemories.size(); i < length; i++ ) {
                // do not use foreach, don't want Iterator object creation
                ruleMemories.get( i ).unlinkedSegment( segmentPosMaskBit, wm );
            }
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

    public List<RuleMemory> getRuleMemories() {
        return ruleMemories;
    }

    public void setRuleMemories(List<RuleMemory> ruleSegments) {
        this.ruleMemories = ruleSegments;
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

    public StagedLeftTuples getStagedLeftTuples() {
        return stagedLeftTuples;
    }

    public void setStagedTuples(StagedLeftTuples stagedTuples) {
        this.stagedLeftTuples = stagedTuples;
    }

    public void setNext(SegmentMemory next) {
        this.next = next;
    }

    public SegmentMemory getNext() {
        return this.next;
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
        if ( this == obj ) return true;
        if ( !super.equals( obj ) ) return false;
        if ( getClass() != obj.getClass() ) return false;
        SegmentMemory other = (SegmentMemory) obj;
        if ( rootNode == null ) {
            if ( other.rootNode != null ) return false;
        } else if ( rootNode.getId() != other.rootNode.getId()) return false;
        return true;
    } 
    
    
}
