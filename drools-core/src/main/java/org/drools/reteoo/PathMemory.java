package org.drools.reteoo;

import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.util.AbstractBaseLinkedListNode;
import org.drools.phreak.RuleNetworkEvaluatorActivation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathMemory extends AbstractBaseLinkedListNode<Memory>
        implements
        Memory {
    protected static transient Logger log = LoggerFactory.getLogger(SegmentMemory.class);

    private long                           linkedSegmentMask;

    private long                           allLinkedMaskTest;

    private TerminalNode                   rtn;

    private RuleNetworkEvaluatorActivation agendaItem;
    
    private SegmentMemory[]                segmentMemories;

    private SegmentMemory                  segmentMemory;

    public PathMemory(TerminalNode rtn) {
        this.rtn = rtn;
    }

    public TerminalNode getRuleTerminalNode() {
        return rtn;
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

    public long getAllLinkedMaskTest() {
        return allLinkedMaskTest;
    }

    public void setAllLinkedMaskTest(long allLinkedTestMask) {
        this.allLinkedMaskTest = allLinkedTestMask;
    }

    public void linkNodeWithoutRuleNotify(long mask) {
        linkedSegmentMask = linkedSegmentMask | mask;
    }
    
    public void linkSegment(long mask,
                            InternalWorkingMemory wm) {
        if ( log.isTraceEnabled() ) {
            if ( getRuleTerminalNode() != null ) {
                log.trace( "  LinkSegment smask={} rmask={} name={}", mask, linkedSegmentMask, getRuleTerminalNode().getRule().getName()  );
            }  else {
                log.trace( "  LinkSegment smask={} rmask={} name={}", mask, "RiaNode" );
            }

        }
        linkedSegmentMask = linkedSegmentMask | mask;
        if ( isRuleLinked() ) {
            doLinkRule( wm );
        }
    }

    public void doLinkRule(InternalWorkingMemory wm) {
        if ( log.isTraceEnabled() ) {
            log.trace( "    LinkRule name={}", getRuleTerminalNode().getRule().getName()  );
        }
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
        if ( log.isTraceEnabled() ) {
            log.trace( "    UnlinkRule name={}", getRuleTerminalNode().getRule().getName()  );
        }
        if ( agendaItem == null ) {
            int salience = rtn.getRule().getSalience().getValue( null,
                                                                 rtn.getRule(),
                                                                 wm );
            agendaItem = ((InternalAgenda) wm.getAgenda()).createRuleNetworkEvaluatorActivation( salience, this, rtn );
        } else if ( !agendaItem.isActive() ) {
            ((InternalAgenda) wm.getAgenda()).addActivation( agendaItem );
        }
        //((InternalAgenda) wm.getAgenda()).removeActivation( agendaItem );
    }

    public void unlinkedSegment(long mask,
                                InternalWorkingMemory wm) {
        if ( log.isTraceEnabled() ) {
            log.trace( "  UnlinkSegment smask={} rmask={} name={}", mask, linkedSegmentMask, this  );
        }
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

    public void setSegmentMemory(SegmentMemory sm) {
        this.segmentMemory = sm;
    }
    
    public SegmentMemory getSegmentMemory() {
        return this.segmentMemory;
    }

    public String toString() {
        return "[RuleMem " + getRuleTerminalNode().getRule().getName() + "]";
    }

}
