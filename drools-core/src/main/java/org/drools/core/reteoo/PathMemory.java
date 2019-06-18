/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import java.io.Serializable;

import org.drools.core.common.ActivationsFilter;
import org.drools.core.common.CompositeDefaultAgenda;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.util.AbstractBaseLinkedListNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathMemory extends AbstractBaseLinkedListNode<Memory>
        implements
        Serializable, Memory {

    protected static final Logger log = LoggerFactory.getLogger(PathMemory.class);
    protected static final boolean isLogTraceEnabled = log.isTraceEnabled();

    private          long              linkedSegmentMask;
    private          long              allLinkedMaskTest;
    private final    PathEndNode       pathEndNode;
    private          RuleAgendaItem    agendaItem;
    private          SegmentMemory[]   segmentMemories;
    private          SegmentMemory     segmentMemory;

    public  final    boolean           dataDriven;

    public PathMemory(PathEndNode pathEndNode, InternalWorkingMemory wm) {
        this.pathEndNode = pathEndNode;
        this.linkedSegmentMask = 0L;
        this.dataDriven = initDataDriven( wm );
    }

    protected boolean initDataDriven( InternalWorkingMemory wm ) {
        return isRuleDataDriven( wm, getRule() );
    }

    protected boolean isRuleDataDriven( InternalWorkingMemory wm, RuleImpl rule ) {
        return rule != null &&
               ( rule.isDataDriven() ||
                 ( wm != null &&
                   wm.getSessionConfiguration().getForceEagerActivationFilter().accept(rule) ));
    }

    public PathEndNode getPathEndNode() {
        return pathEndNode;
    }

    public RuleImpl getRule() {
        return pathEndNode instanceof TerminalNode ? ((TerminalNode) pathEndNode).getRule() : null;
    }

    public RuleAgendaItem getRuleAgendaItem() {
        return agendaItem;
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
        linkedSegmentMask |= mask;
    }

    public void linkSegment(long mask, InternalWorkingMemory wm) {
        linkedSegmentMask |= mask;
        if (isLogTraceEnabled) {
            if (NodeTypeEnums.isTerminalNode(getPathEndNode())) {
                TerminalNode rtn = (TerminalNode) getPathEndNode();
                log.trace("  LinkSegment smask={} rmask={} name={}", mask, linkedSegmentMask, rtn.getRule().getName());
            } else {
                log.trace("  LinkSegment smask={} rmask={}", mask, "RiaNode");
            }
        }
        if (isRuleLinked()) {
            doLinkRule(wm);
        }
    }

    public RuleAgendaItem getOrCreateRuleAgendaItem(InternalAgenda agenda) {
        ensureAgendaItemCreated(agenda);
        return agendaItem;
    }

    private TerminalNode ensureAgendaItemCreated(InternalAgenda agenda) {
        TerminalNode rtn = (TerminalNode) getPathEndNode();
        if (agendaItem == null) {
            int salience = rtn.getRule().getSalience().isDynamic() ? 0 : rtn.getRule().getSalience().getValue();
            agendaItem = agenda.createRuleAgendaItem(salience, this, rtn);
        }
        return rtn;
    }

    public void doLinkRule(InternalWorkingMemory wm) {
        doLinkRule( getActualAgenda( wm ) );
    }

    public void doLinkRule(InternalAgenda agenda) {
        TerminalNode rtn = ensureAgendaItemCreated(agenda);
        if (isLogTraceEnabled) {
            log.trace(" LinkRule name={}", rtn.getRule().getName());
        }

        queueRuleAgendaItem(agenda);
    }

    public void doUnlinkRule(InternalWorkingMemory wm) {
        doUnlinkRule( getActualAgenda( wm ) );
    }

    public void doUnlinkRule(InternalAgenda agenda) {
        TerminalNode rtn = ensureAgendaItemCreated(agenda);
        if (isLogTraceEnabled) {
            log.trace("    UnlinkRule name={}", rtn.getRule().getName());
        }

        agendaItem.getRuleExecutor().setDirty(true);
        if ( !agendaItem.isQueued() ) {
            if ( isLogTraceEnabled ) {
                log.trace("Queue RuleAgendaItem {}", agendaItem);
            }
            InternalAgendaGroup ag = agendaItem.getAgendaGroup();
            ag.add( agendaItem );
        }
    }

    public void queueRuleAgendaItem(InternalAgenda agenda) {
        agendaItem.getRuleExecutor().setDirty(true);

        ActivationsFilter activationFilter = agenda.getActivationsFilter();
        if ( activationFilter != null && !activationFilter.accept( agendaItem,
                                                                   agenda.getWorkingMemory(),
                                                                   agendaItem.getTerminalNode() ) ) {
            return;
        }

        if ( !agendaItem.isQueued() ) {
            if ( isLogTraceEnabled ) {
                log.trace("Queue RuleAgendaItem {}", agendaItem);
            }
            InternalAgendaGroup ag = agendaItem.getAgendaGroup();
            ag.add( agendaItem );
        }

        if ( agendaItem.getRule().isQuery() ) {
            agenda.addQueryAgendaItem( agendaItem );
        } else if ( agendaItem.getRule().isEager() ) {
            agenda.addEagerRuleAgendaItem( agendaItem );
        }
    }

    public void unlinkedSegment(long mask, InternalWorkingMemory wm) {
        boolean linkedRule =  isRuleLinked();
        linkedSegmentMask ^= mask;
        if (isLogTraceEnabled) {
            log.trace("  UnlinkSegment smask={} rmask={} name={}", mask, linkedSegmentMask, this);
        }
        if (linkedRule && !isRuleLinked()) {
            doUnlinkRule(wm);
        }
    }

    public boolean isRuleLinked() {
        return (linkedSegmentMask & allLinkedMaskTest) == allLinkedMaskTest;
    }

    public boolean isDataDriven() {
        return dataDriven;
    }

    public short getNodeType() {
        return NodeTypeEnums.RuleTerminalNode;
    }

    public boolean isInitialized() {
        return agendaItem != null && segmentMemories[0] != null;
    }

    public SegmentMemory[] getSegmentMemories() {
        return segmentMemories;
    }

    public void setSegmentMemory(int index, SegmentMemory sm) {
        this.segmentMemories[index] = sm;
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
        return "[RuleMem " + getRule().getName() + "]";
    }

    public void reset() {
        this.linkedSegmentMask = 0L;
        // TODO we could reset the agandaItem instead of throwing it away
        this.agendaItem = null;
    }

    public InternalAgenda getActualAgenda(InternalWorkingMemory wm) {
        InternalAgenda agenda = wm.getAgenda();
        return agenda instanceof CompositeDefaultAgenda ?
               ( (CompositeDefaultAgenda) agenda ).getPartitionedAgendaForNode( getPathEndNode() ) :
               agenda;
    }
}
