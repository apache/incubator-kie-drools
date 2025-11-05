/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.phreak;

import org.drools.base.common.NetworkNode;
import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.core.common.Memory;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.LeftTupleNode;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.LeftTupleSinkNode;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.SegmentNodeMemory;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.reteoo.TupleToObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.core.phreak.BuildtimeSegmentUtilities.nextNodePosMask;

public class SegmentCursor {
    private static final Logger log = LoggerFactory.getLogger(SegmentCursor.class);
    
    private PathMemory pmem;
    private SegmentMemory[] smems;
    private int smemIndex;
    private long bit;
    private Memory nodeMem;
    private NetworkNode node;
    private TupleSets srcTuples;
    private TupleSets stagedLeftTuples;
    
    public SegmentCursor(PathMemory pmem,
                         SegmentMemory[] smems,
                         int smemIndex,
                         long bit,
                         Memory nodeMem,
                         NetworkNode node, 
                         TupleSets srcTuples) {
        this.pmem = pmem;
        this.smems = smems;
        this.smemIndex = smemIndex;
        this.bit = bit;
        this.nodeMem = nodeMem;
        this.node = node;
        this.srcTuples = srcTuples;
    }
    
    public PathMemory getPathMemory() {
        return pmem;
    }
    
    public int getSegmentMemorySize() {
        return smems.length;
    }
    
    public Memory getCurrentNodeMemory() {
        return nodeMem;
    }
    
    public int getSegmentMemoryIndex() {
        return smemIndex;
    }
    
    public NetworkNode getCurrentNode() {
        return node;
    }
    
    public TupleSets getSourceTuples() {
        return srcTuples;
    }
    
    public TupleSets getStagedLeftTuples() {
        return stagedLeftTuples;
    }
    
    public SegmentMemory getCurrentSegment() {
        return smems[smemIndex];
    }
    
    public void setSourceTuples(TupleSets srcTuples) {
        this.srcTuples = srcTuples;
    }
    
    
    public void moveToNextAvailableSegment() {
        while ((getCurrentSegment().getDirtyNodeMask() & bit) == 0 && node != getCurrentSegment().getTipNode() && !NodeTypeEnums.isBetaNodeWithSubnetwork(node)) {
        //while ((dirtyMask & bit) == 0 && node != smem.getTipNode() && NodeTypeEnums.isBetaNodeWithoutSubnetwork(node)) {
            if (log.isTraceEnabled()) {
                int offset = getOffset(node);
                log.trace("{} Skip Node {}", indent(offset), node);
            }
            bit = nextNodePosMask(bit); // shift to check the next node
            node = ((LeftTupleNode) node).getSinkPropagator().getFirstLeftTupleSink();
            nodeMem = nodeMem.getNext();
        }
    }

    public void moveToNextNodeInSegment() {
        node = ((LeftTupleNode) node).getSinkPropagator().getFirstLeftTupleSink();
        nodeMem = nodeMem.getNext();
        bit = nextNodePosMask(bit);
    }

    public void moveToNextSegment() {
        // Reached end of segment, start on new segment.
        bit = 1;
        smemIndex = smemIndex + 1;
    
        node = getCurrentSegment().getRootNode();
        nodeMem = getCurrentSegment().getNodeMemories()[0];
    }

    public void moveToNextNodeOrSegment() {
        if (node == getCurrentSegment().getTipNode()) {
            moveToNextSegment();
            srcTuples = getCurrentSegment().getStagedLeftTuples().takeAll();
            traceMoveToNextNodeOrSegment();
        } else {
            // get next node and node memory in the segment
            moveToNextNodeInSegment();
        }
    }

    public void moveToSegment(int i) {
        smemIndex = i;
        bit = 1;
        srcTuples = getCurrentSegment().getStagedLeftTuples().takeAll();
        node = getCurrentSegment().getRootNode();
        nodeMem = getCurrentSegment().getNodeMemories()[0];
    }

    public boolean isDirtySegmentOrIsSubnetwork() {
        return !srcTuples.isEmpty() ||
             getCurrentSegment().getDirtyNodeMask() != 0 ||
             (NodeTypeEnums.isBetaNode(node) && ((BetaNode)node).getRightInput().inputIsTupleToObjectNode());
    }

    public boolean isOnEmptyOrNonDirtySegment() {
        return srcTuples.isEmpty() && getCurrentSegment().getDirtyNodeMask() == 0;
    }

    public boolean isAtEndOfSegmentMemory() {
        return node == getCurrentSegment().getTipNode();
    }

    public boolean hasNoSourceTuples() {
        return srcTuples.isEmpty();
    }

    public void restoreStagedLeftTuples() {
        getCurrentSegment().getFirst().getStagedLeftTuples().addAll(stagedLeftTuples);
    }

    public boolean stagedLeftTuplesAreNotEmpty() {
        return stagedLeftTuples != null && !stagedLeftTuples.isEmpty();
    }

    public void traceStartOfEvaluation(int cycle) {
        if (log.isTraceEnabled()) {
            int offset = getOffset(node);
            log.trace("{} {} {} {}", indent(offset), cycle, node.toString(), srcTuples.toStringSizes());
        }
    }

    public void traceMoveToDirtySegment(int cycle2) {
        if (log.isTraceEnabled()) {
            int offset = getOffset(node);
            log.trace("{} Segment {}", indent(offset), smemIndex);
            log.trace("{} {} {} {}", indent(offset), cycle2, node.toString(), srcTuples.toStringSizes());
        }
    }

    public void traceSkipNonDirtySegment(int i) {
        if (log.isTraceEnabled()) {
            int offset = getOffset(node);
            log.trace("{} Skip Segment {}", indent(offset), i-1);
        }
    }

    public void traceMoveToNextNodeOrSegment() {
        if (log.isTraceEnabled()) {
            int offset = getOffset(node);
            log.trace("{} Segment {}", indent(offset), smemIndex);
        }
    }
    
    public void traceResumeFromStack() {
        if (log.isTraceEnabled()) {
            int offset = getOffset(node);
            log.trace("{} Resume {} {}", indent(offset), node.toString(), srcTuples.toStringSizes());
        }
    }

    public void traceSubnetworkQueue() {
        if (log.isTraceEnabled()) {
            int offset = getOffset(node);
            log.trace("{} SubnetworkQueue {} {}", indent(offset), node.toString(), srcTuples.toStringSizes());
        }
    }

    public void traceSwitchOnBetaNodes(BetaMemory bm) {
        if (log.isTraceEnabled()) {
            int offset = getOffset(node);
            log.trace("{} rightTuples {}", indent(offset), bm.getStagedRightTuples().toStringSizes());
        }
    }

    public void traceQueryResultTuples() {
        if (log.isTraceEnabled()) {
            int offset = getOffset(node);
            log.trace("{} query result tuples {}", indent(offset), ((org.drools.core.reteoo.QueryElementNode.QueryElementNodeMemory) nodeMem).getResultLeftTuples().toStringSizes());
        }
    }

    public void traceOrQueryBranchQueued(int i) {
        if (log.isTraceEnabled()) {
            int offset = getOffset(node);
            log.trace("{} ORQueue branch={} {} {}", indent(offset), i, node.toString(), srcTuples.toStringSizes());
        }
    }

    public void traceNetworkEvaluation() {
        if (log.isTraceEnabled()) {
            log.trace("Rule[name={}] segments={} {}", ((TerminalNode)pmem.getPathEndNode()).getRule().getName(), pmem.getSegmentMemories().length, srcTuples.toStringSizes());
        }
    }

    public StackEntry saveForResumeAfterQueryExecution(TupleSets trgTuples) {
        LeftTupleSinkNode sink = getFirstLeftTupleSink();
        StackEntry stackEntry = new StackEntry(node, bit, sink, pmem, nodeMem, smems,
                                               smemIndex, trgTuples, true, true);
        return stackEntry;
    }

    public StackEntry saveForResumeAfterSubnetworkExecution() {
        LeftTupleSinkNode sink = getFirstLeftTupleSink();
        // Resume the node after the TupleToObjectNode segment has been processed and the right input memory populated
        return new StackEntry(node, ((SegmentNodeMemory) nodeMem).getNodePosMaskBit(), sink, pmem, nodeMem, smems,
                                               smemIndex, srcTuples, false, false);
    }

    public StackEntry saveForQueryBranchEvaluation() {
        return new StackEntry(node, bit, null, pmem,
                                    nodeMem, pmem.getSegmentMemories(), smemIndex,
                                    srcTuples, false, true);
    }

    public LeftTupleSinkNode getFirstLeftTupleSink() {
        return ((LeftTupleNode) node).getSinkPropagator().getFirstLeftTupleSink();
    }

    public void saveStagedLeftTuples() {
        stagedLeftTuples = getCurrentSegment().getFirst().getStagedLeftTuples().takeAll();
    }

    public void resetStagedLeftTuples() {
        stagedLeftTuples = null;
    }

    public static SegmentCursor createForSubNetwork(PathMemory pathMem) {
        SegmentMemory[]      subnetworkSmems = pathMem.getSegmentMemories();
        SegmentMemory subSmem = null;
        for (int i = 0; subSmem == null; i++) {
            // segment positions outside of the subnetwork, in the parent chain, are null
            // so we must iterate to find the first non null segment memory
            subSmem =  subnetworkSmems[i];
        }
        TupleSets subLts = subSmem.getStagedLeftTuples().takeAll();
        // node is first in the segment, so bit is 1
        
        return new SegmentCursor(pathMem, 
                subnetworkSmems, 
                subSmem.getPos(), 
                1, 
                subSmem.getNodeMemories()[0], 
                subSmem.getRootNode(), 
                subLts);
    }

    public static SegmentCursor createForResume(StackEntry entry) {
        NetworkNode node;
        SegmentMemory smem = entry.getSmems()[entry.getSmemIndex()];
        if (entry.getNode() == smem.getTipNode()) {
            // Reached end of segment, start on new segment.
            smem = entry.getSmems()[entry.getSmemIndex() + 1];
            return new SegmentCursor(entry.getRmem(), 
                    entry.getSmems(), 
                    entry.getSmemIndex() + 1, 
                    1, // update bit to start of new segment
                    smem.getNodeMemories()[0], 
                    smem.getRootNode(), 
                    smem.getStagedLeftTuples().takeAll());
            
    
        } else {
            // get next node and node memory in the segment
            LeftTupleSink nextSink = entry.getSink().getNextLeftTupleSinkNode();
            if (nextSink == null) {
                node = entry.getSink();
            } else {
                // there is a nested subnetwork, take out path
                node = nextSink;
            }
            return new SegmentCursor(entry.getRmem(), 
                    entry.getSmems(), 
                    entry.getSmemIndex(), 
                    nextNodePosMask(entry.getBit()), // update bit to new node, 
                    entry.getNodeMem().getNext(), 
                    node, 
                    entry.getTrgTuples());
        }
    }

    public static SegmentCursor createForNonResume(StackEntry entry) {
        SegmentCursor sc;
        sc = new SegmentCursor(entry.getRmem(), 
                entry.getSmems(), 
                entry.getSmemIndex(), 
                entry.getBit(), 
                entry.getNodeMem(), 
                entry.getNode(), 
                entry.getTrgTuples());
        return sc;
    }

    public static SegmentCursor createSegmentCursor(PathMemory pmem, SegmentMemory sm, TupleSets tupleSets) {
        if (NodeTypeEnums.isLeftInputAdapterNode(sm.getRootNode()) && !NodeTypeEnums.isLeftInputAdapterNode(sm.getTipNode())) {
            return new SegmentCursor(pmem, 
                    pmem.getSegmentMemories(), 
                    sm.getPos(),
                    2L,
                    sm.getNodeMemories()[1],
                    // The segment is the first and it has the lian shared with other nodes, the lian must be skipped, so adjust the bit and sink
                    sm.getRootNode().getSinkPropagator().getFirstLeftTupleSink(), 
                    tupleSets);
        } else {
            return new SegmentCursor(pmem, 
                    pmem.getSegmentMemories(), 
                    sm.getPos(),
                    1L,
                    sm.getNodeMemories()[0],
                    // The segment is the first and it has the lian shared with other nodes, the lian must be skipped, so adjust the bit and sink
                    sm.getRootNode(), 
                    tupleSets);
        }
    }

    

    public static SegmentCursor createSegmentCursor(PathMemory pmem, NetworkNode sink, Memory tm, TupleSets tupleSets) {
        SegmentMemory[] smems = pmem.getSegmentMemories();
        SegmentMemory sm = tm.getSegmentMemory();
        int smemIndex = 0;
        for (SegmentMemory smem : smems) {
            if (smem == sm) {
                break;
            }
            smemIndex++;
        }

        long bit = 1;
        for (NetworkNode node = sm.getRootNode(); node != sink; node = ((LeftTupleNode) node).getSinkPropagator()
                .getFirstLeftTupleSink()) {
            //update the bit to the correct node position.
            bit = nextNodePosMask(bit);
        }

        return new SegmentCursor(pmem, 
                smems,
                smemIndex,
                bit,
                tm,
                sink, tupleSets);
    }

    public static SegmentCursor createSegmentCursorForQueryExecution(PathMemory pmem) {
        if (pmem.getPathEndNode().getPathNodes()[0] == pmem.getSegmentMemories()[0].getTipNode()) {
            // segment only has liaNode in it
            // nothing is staged in the liaNode, so skip to next segment
            return new SegmentCursor(
                    pmem, 
                    pmem.getSegmentMemories(), 
                    1, 
                    1L,  
                    pmem.getSegmentMemories()[1].getNodeMemories()[0], 
                    pmem.getSegmentMemories()[1].getRootNode(), 
                    pmem.getSegmentMemories()[1].getStagedLeftTuples().takeAll());
        } else {
            // lia is in shared segment, so point to next node
            return new SegmentCursor(
                    pmem, 
                    pmem.getSegmentMemories(), 
                    0, 
                    2L,  
                    pmem.getSegmentMemories()[0].getNodeMemories()[1], 
                    pmem.getPathEndNode().getPathNodes()[0].getSinkPropagator().getFirstLeftTupleSink(), 
                    pmem.getSegmentMemories()[0].getStagedLeftTuples().takeAll());
        }
    }

    public static SegmentCursor createSegmentCursor(PathMemory pmem) {
        if (pmem.getSegmentMemories()[0].isOnlyLiaSegment()) {
            return new SegmentCursor(
                    pmem,
                    pmem.getSegmentMemories(),
                    1,
                    1L,
                    // segment only has liaNode in it
                    // nothing is staged in the liaNode, so skip to next segment
                    pmem.getSegmentMemories()[1].getNodeMemories()[0],
                    pmem.getSegmentMemories()[1].getRootNode(), 
                    pmem.getSegmentMemories()[1].getStagedLeftTuples());
            
        } else {
            // lia is in shared segment, so point to next node
            return new SegmentCursor(
                    pmem, 
                    pmem.getSegmentMemories(),
                    0,
                    2L,
                    pmem.getSegmentMemories()[0].getNodeMemories()[1],
                    pmem.getSegmentMemories()[0].getRootNode().getSinkPropagator().getFirstLeftTupleSink(),
                    pmem.getSegmentMemories()[0].getStagedLeftTuples());
        }
    }

    private static String indent(int size) {
        StringBuilder sbuilder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sbuilder.append("  ");
        }

        return sbuilder.toString();
    }

    private static int getOffset(NetworkNode node) {
        LeftTupleNode lt;
        int offset = 1;
        if (NodeTypeEnums.isTerminalNode(node)) {
            lt = ((TerminalNode) node).getLeftTupleSource();
            offset++;
        } else if (node.getType() == NodeTypeEnums.TupleToObjectNode) {
            lt = ((TupleToObjectNode) node).getLeftTupleSource();
        } else {
            lt = (LeftTupleNode) node;
        }
        while (!NodeTypeEnums.isLeftInputAdapterNode(lt)) {
            offset++;
            lt = lt.getLeftTupleSource();
        }

        return offset;
    }
    
}