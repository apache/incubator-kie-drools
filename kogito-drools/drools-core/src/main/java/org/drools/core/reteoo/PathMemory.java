package org.drools.core.reteoo;

import org.drools.core.base.mvel.MVELSalienceExpression;
import org.drools.core.common.DefaultAgenda;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalRuleFlowGroup;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.NetworkNode;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.util.AbstractBaseLinkedListNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathMemory extends AbstractBaseLinkedListNode<Memory>
        implements
        Memory {
    protected static transient Logger log = LoggerFactory.getLogger(SegmentMemory.class);
    private          long            linkedSegmentMask;
    private          long            allLinkedMaskTest;
    private          NetworkNode     networkNode;
    private volatile RuleAgendaItem  agendaItem;
    private          SegmentMemory[] segmentMemories;
    private          SegmentMemory   segmentMemory;

    public PathMemory(NetworkNode networkNode) {
        this.networkNode = networkNode;
    }

    public NetworkNode getNetworkNode() {
        return networkNode;
    }

    public RuleAgendaItem getRuleAgendaItem() {
        return agendaItem;
    }

    public void setAgendaItem(RuleAgendaItem agendaItem) {
        this.agendaItem = agendaItem;
    }

    public void setlinkedSegmentMask(long mask) {
        linkedSegmentMask = mask;
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
        if (log.isTraceEnabled()) {
            if (NodeTypeEnums.isTerminalNode(getNetworkNode())) {
                TerminalNode rtn = (TerminalNode) getNetworkNode();
                log.trace("  LinkSegment smask={} rmask={} name={}", mask, linkedSegmentMask, rtn.getRule().getName());
            } else {
                log.trace("  LinkSegment smask={} rmask={} name={}", mask, "RiaNode");
            }
        }
        linkedSegmentMask = linkedSegmentMask | mask;
        if (isRuleLinked()) {
            doLinkRule(wm);
        }
    }

    public void doLinkRule(InternalWorkingMemory wm) {
        TerminalNode rtn = (TerminalNode) getNetworkNode();
        if (log.isTraceEnabled()) {
            log.trace("    LinkRule name={}", rtn.getRule().getName());
        }
        if (agendaItem == null) {
            int salience = ( rtn.getRule().getSalience() instanceof MVELSalienceExpression)
                           ? 0
                           : rtn.getRule().getSalience().getValue(null, rtn.getRule(), wm);
            agendaItem = ((InternalAgenda) wm.getAgenda()).createRuleAgendaItem(salience, this, rtn);
        }

        queueRuleAgendaItem(wm);
    }

    public void doUnlinkRule(InternalWorkingMemory wm) {
        TerminalNode rtn = (TerminalNode) getNetworkNode();
        if (log.isTraceEnabled()) {
            log.trace("    UnlinkRule name={}", rtn.getRule().getName());
        }
        if (agendaItem == null) {
            int salience = ( rtn.getRule().getSalience() instanceof MVELSalienceExpression)
                           ? 0
                           : rtn.getRule().getSalience().getValue(null, rtn.getRule(), wm);
            agendaItem = ((InternalAgenda) wm.getAgenda()).createRuleAgendaItem(salience, this, rtn);
        }

        queueRuleAgendaItem(wm);
    }

    public void queueRuleAgendaItem(InternalWorkingMemory wm) {
        agendaItem.getRuleExecutor().setDirty(true);
        if (!agendaItem.isQueued()) {
            InternalRuleFlowGroup rfg = agendaItem.getRuleFlowGroup();
            InternalAgendaGroup ag = agendaItem.getAgendaGroup();
            if ( rfg != null ) {
                rfg.addActivation( agendaItem );
            } else {
                ag.add( agendaItem );
            }
        }
        if ( agendaItem.getRule().isEager() ) {
            // will return if already added
            ((InternalAgenda)wm.getAgenda()).addEagerRuleAgendaItem( agendaItem );
        }
    }


    public void unlinkedSegment(long mask,
                                InternalWorkingMemory wm) {
        if (log.isTraceEnabled()) {
            log.trace("  UnlinkSegment smask={} rmask={} name={}", mask, linkedSegmentMask, this);
        }
        if (isRuleLinked()) {
            doUnlinkRule(wm);
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
        return this.segmentMemory;
    }

    public void setSegmentMemory(SegmentMemory sm) {
        this.segmentMemory = sm;
    }

    public String toString() {
        return "[RuleMem " + ((TerminalNode) getNetworkNode()).getRule().getName() + "]";
    }

}
