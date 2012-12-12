package org.drools.reteoo;

import java.util.List;

import org.drools.common.InternalAgenda;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.Memory;
import org.drools.core.util.AbstractBaseLinkedListNode;
import org.drools.phreak.RuleNetworkEvaluatorActivation;
import org.drools.reteoo.LeftInputAdapterNode.LiaNodeMemory;

public class RuleMemory  extends AbstractBaseLinkedListNode<Memory>
        implements
        Memory {
    private long                           linkedSegmentMask;

    private long                           allLinkedMaskTest;

    private RuleTerminalNode               rtn;

    private RuleNetworkEvaluatorActivation agendaItem;
    
    private SegmentMemory[]                segmentMemories;

    public RuleMemory(RuleTerminalNode rtn) {
        this.rtn = rtn;
    }

    public RuleTerminalNode getRuleTerminalNode() {
        return rtn;
    }

    public void setRuleTerminalNode(RuleTerminalNode rtn) {
        this.rtn = rtn;
    }

    public RuleNetworkEvaluatorActivation getAgendaItem() {
        return agendaItem;
    }

    public void setAgendaItem(RuleNetworkEvaluatorActivation agendaItem) {
        this.agendaItem = agendaItem;
    }

    public long getLinkedSegmentMask() {
        return linkedSegmentMask;
    }

    public void setLinkedSegmentMask(long linkedSegmentMask) {
        this.linkedSegmentMask = linkedSegmentMask;
    }

    public long getAllLinkedMaskTest() {
        return allLinkedMaskTest;
    }

    public void setAllLinkedMaskTest(long allLinkedTestMask) {
        this.allLinkedMaskTest = allLinkedTestMask;
    }

    public void linkSegment(long mask,
                            InternalWorkingMemory wm) {
        linkedSegmentMask = linkedSegmentMask | mask;
        if ( isRuleLinked() ) {
            doLinkRule( wm );
        }
    }

    public void doLinkRule(InternalWorkingMemory wm) {
        if ( agendaItem == null ) {
            int salience = rtn.getRule().getSalience().getValue( null,
                                                                 rtn.getRule(),
                                                                 wm );
            agendaItem = ((InternalAgenda) wm.getAgenda()).createRuleNetworkEvaluatorActivation( salience, this, rtn );
        } else if ( !agendaItem.isActive() ) {
            ((InternalAgenda) wm.getAgenda()).addActivation( agendaItem );
        }
    }

    public void doUnlinkRule(InternalWorkingMemory wm) {
        ((InternalAgenda) wm.getAgenda()).removeActivation( agendaItem );
    }

    public void unlinkedSegment(long mask,
                                InternalWorkingMemory wm) {
        if ( isRuleLinked() ) {
            doUnlinkRule( wm );
        }
        linkedSegmentMask = linkedSegmentMask ^ mask;
    }

    public boolean isRuleLinked() {
        return (linkedSegmentMask & allLinkedMaskTest) == allLinkedMaskTest;
    }

    public short getNodeType() {
        return NodeTypeEnums.RuleTerminalNode;
    }

    public SegmentMemory[] getSegmentMemories() {
        return segmentMemories;
    }

    public void setSegmentMemories(SegmentMemory[] segmentMemories) {
        this.segmentMemories = segmentMemories;
    }

    public SegmentMemory getSegmentMemory() {
        throw new UnsupportedOperationException();
    }    

}
