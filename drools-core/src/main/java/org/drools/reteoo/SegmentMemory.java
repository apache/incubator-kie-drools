package org.drools.reteoo;

import java.util.ArrayList;
import java.util.List;

import org.drools.common.InternalWorkingMemory;
import org.drools.core.util.index.LeftTupleList;
import org.drools.core.util.index.RightTupleList;

public class SegmentMemory {
    private long             linkedNodeMask;

    private long             allLinkedMaskTest;

    private List<RuleMemory> ruleMemories;

    private long             segmentPosMaskBit;
    
    private int              pos;

    private LeftTupleList    stagedAssertLeftTuple;
    private LeftTupleList    stagedRetractLeftTuple;
    private LeftTupleList    stagedModifyLeftTuple;

    private int              counter;

    private boolean          active;

    public SegmentMemory() {
        ruleMemories = new ArrayList<RuleMemory>( 1 );

        stagedAssertLeftTuple = new LeftTupleList();
        stagedAssertLeftTuple.setStagingMemory( true );

        stagedModifyLeftTuple = new LeftTupleList();
        stagedModifyLeftTuple.setStagingMemory( true );
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
        for ( RuleMemory rs : ruleMemories ) {
            rs.linkSegment( segmentPosMaskBit, wm );
        }
    }

    public void unlinkNode(long mask,
                           InternalWorkingMemory wm) {
        if ( isSegmentLinked() ) {
            for ( RuleMemory rs : ruleMemories ) {
                rs.unlinkedSegment( segmentPosMaskBit, wm );
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

    public void addAssertLeftTuple(LeftTuple leftTuple,
                                   InternalWorkingMemory wm) {
        stagedAssertLeftTuple.add( leftTuple );
        if ( counter == 0 && isSegmentLinked() ) {
            notifyRuleLinkSegment( wm );
        }
        counter++;
    }

    public void removeAssertLeftTuple(LeftTuple leftTuple,
                                      InternalWorkingMemory wm) {
        stagedAssertLeftTuple.remove( leftTuple );
        counter--;
    }

    public LeftTupleList getStagedAssertLeftTuple() {
        return stagedAssertLeftTuple;
    }

    public void setStagedAssertLeftTuple(LeftTupleList stagedAssertLeftTuple) {
        this.stagedAssertLeftTuple = stagedAssertLeftTuple;
    }

    public void addModifyLeftTuple(LeftTuple leftTuple,
                                   InternalWorkingMemory wm) {
        stagedModifyLeftTuple.add( leftTuple );
        if ( counter == 0 && isSegmentLinked() ) {
            notifyRuleLinkSegment( wm );
        }
        counter++;
    }

    public void removeModifyLeftTuple(LeftTuple leftTuple,
                                      InternalWorkingMemory wm) {
        stagedModifyLeftTuple.remove( leftTuple );
        counter--;
    }

    public LeftTupleList getStagedModifyLeftTuple() {
        return stagedModifyLeftTuple;
    }

    public void setStagedModifyLeftTuple(LeftTupleList stagedModifyLeftTuple) {
        this.stagedModifyLeftTuple = stagedModifyLeftTuple;
    }

    //    public void addAssertRightTuple(RightTuple rightTuple,
    //                                    InternalWorkingMemory wm) {
    //        stagedAssertRightTuple.add(  rightTuple );
    //        if ( counter == 0 && isSegmentLinked() ) {
    //            notifyRuleLinkSegment( wm );
    //        }        
    //        counter++;
    //    }
    //    
    //    public void removeAssertRightTuple(RightTuple rightTuple,
    //                                       InternalWorkingMemory wm) {
    //        stagedAssertRightTuple.remove(  rightTuple );
    //        counter--;
    //    } 
    //    
    //    public RightTupleList getStagedAssertRightTuple() {
    //        return stagedAssertRightTuple;
    //    }
    //
    //    public void setStagedAssertRightTuple(RightTupleList stagedAssertRightTuple) {
    //        this.stagedAssertRightTuple = stagedAssertRightTuple;
    //    }

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

}
