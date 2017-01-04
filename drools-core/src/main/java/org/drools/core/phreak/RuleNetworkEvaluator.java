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

package org.drools.core.phreak;

import org.drools.core.base.DroolsQuery;
import org.drools.core.common.BetaConstraints;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.NetworkNode;
import org.drools.core.common.TupleSets;
import org.drools.core.common.TupleSetsImpl;
import org.drools.core.reteoo.AccumulateNode;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.ConditionalBranchNode;
import org.drools.core.reteoo.ConditionalBranchNode.ConditionalBranchMemory;
import org.drools.core.reteoo.EvalConditionNode;
import org.drools.core.reteoo.EvalConditionNode.EvalMemory;
import org.drools.core.reteoo.ExistsNode;
import org.drools.core.reteoo.FromNode;
import org.drools.core.reteoo.FromNode.FromMemory;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleNode;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.LeftTupleSinkNode;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.NotNode;
import org.drools.core.reteoo.ObjectSink;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.QueryElementNode.QueryElementNodeMemory;
import org.drools.core.reteoo.QueryTerminalNode;
import org.drools.core.reteoo.ReactiveFromNode;
import org.drools.core.reteoo.RiaPathMemory;
import org.drools.core.reteoo.RightInputAdapterNode;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.SubnetworkTuple;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.reteoo.TimerNode;
import org.drools.core.reteoo.TimerNode.TimerNodeMemory;
import org.drools.core.reteoo.TupleMemory;
import org.drools.core.rule.ContextEntry;
import org.drools.core.spi.Tuple;
import org.drools.core.util.FastIterator;
import org.drools.core.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RuleNetworkEvaluator {

    private static final Logger log = LoggerFactory.getLogger(RuleNetworkEvaluator.class);

    private static final PhreakJoinNode         pJoinNode   = new PhreakJoinNode();
    private static final PhreakEvalNode         pEvalNode   = new PhreakEvalNode();
    private static final PhreakFromNode         pFromNode   = new PhreakFromNode();
    private static final PhreakReactiveFromNode pReactiveFromNode = new PhreakReactiveFromNode();
    private static final PhreakNotNode          pNotNode    = new PhreakNotNode();
    private static final PhreakExistsNode       pExistsNode = new PhreakExistsNode();
    private static final PhreakAccumulateNode   pAccNode    = new PhreakAccumulateNode();
    private static final PhreakBranchNode       pBranchNode = new PhreakBranchNode();
    private static final PhreakQueryNode        pQueryNode  = new PhreakQueryNode();
    private static final PhreakTimerNode        pTimerNode  = new PhreakTimerNode();
    private static final PhreakRuleTerminalNode pRtNode     = new PhreakRuleTerminalNode();

    private static int cycle = 0;

    private static PhreakQueryTerminalNode pQtNode = new PhreakQueryTerminalNode();

    public RuleNetworkEvaluator() {

    }

    public void evaluateNetwork(PathMemory pmem, RuleExecutor executor, InternalWorkingMemory wm) {
        evaluateNetwork( pmem, executor, pmem.getActualAgenda(wm) );
    }

    public void evaluateNetwork(PathMemory pmem, RuleExecutor executor, InternalAgenda agenda) {
        SegmentMemory[] smems = pmem.getSegmentMemories();

        int smemIndex = 0;
        SegmentMemory smem = smems[smemIndex]; // 0
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) smem.getRootNode();

        LinkedList<StackEntry> stack = new LinkedList<StackEntry>();

        NetworkNode node;
        Memory nodeMem;
        long bit = 1;
        if (liaNode == smem.getTipNode()) {
            // segment only has liaNode in it
            // nothing is staged in the liaNode, so skip to next segment
            smem = smems[++smemIndex]; // 1
            node = smem.getRootNode();
            nodeMem = smem.getNodeMemories().getFirst();
        } else {
            // lia is in shared segment, so point to next node
            bit = 2;
            node = liaNode.getSinkPropagator().getFirstLeftTupleSink();
            nodeMem = smem.getNodeMemories().getFirst().getNext(); // skip the liaNode memory
        }

        TupleSets<LeftTuple> srcTuples = smem.getStagedLeftTuples();
        if (log.isTraceEnabled()) {
            log.trace("Rule[name={}] segments={} {}", ((TerminalNode)pmem.getPathEndNode()).getRule().getName(), smems.length, srcTuples.toStringSizes());
        }
        outerEval(pmem, node, bit, nodeMem, smems, smemIndex, srcTuples, agenda, stack, true, executor);
    }

    public static String indent(int size) {
        StringBuilder sbuilder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sbuilder.append("  ");
        }

        return sbuilder.toString();
    }

    public static int getOffset(NetworkNode node) {
        LeftTupleSource lt;
        int offset = 1;
        if (NodeTypeEnums.isTerminalNode(node)) {
            lt = ((TerminalNode) node).getLeftTupleSource();
            offset++;
        } else if (node.getType() == NodeTypeEnums.RightInputAdaterNode) {
            lt = ((RightInputAdapterNode) node).getLeftTupleSource();
        } else {
            lt = (LeftTupleSource) node;
        }
        while (lt.getType() != NodeTypeEnums.LeftInputAdapterNode) {
            offset++;
            lt = lt.getLeftTupleSource();
        }

        return offset;
    }

    public void outerEval(PathMemory pmem,
                          NetworkNode node,
                          long bit,
                          Memory nodeMem,
                          SegmentMemory[] smems,
                          int smemIndex,
                          TupleSets<LeftTuple> trgTuples,
                          InternalAgenda agenda,
                          LinkedList<StackEntry> stack,
                          boolean processRian,
                          RuleExecutor executor) {
        innerEval(pmem, node, bit, nodeMem, smems, smemIndex, trgTuples, agenda, stack, processRian, executor);
        while (true) {
            // eval
            if (!stack.isEmpty()) {
                StackEntry entry = stack.removeLast();
                evalStackEntry(entry, stack, executor, agenda);
            } else {
                return; // stack is empty return;
            }
        }
    }

    public void evalStackEntry(StackEntry entry, LinkedList<StackEntry> stack, RuleExecutor executor, InternalAgenda agenda) {
        NetworkNode node = entry.getNode();
        Memory nodeMem = entry.getNodeMem();
        TupleSets<LeftTuple> trgTuples = entry.getTrgTuples();
        if (node.getType() == NodeTypeEnums.QueryElementNode) {
            // copy across the results, if any from the query node memory
            QueryElementNodeMemory qmem = (QueryElementNodeMemory) nodeMem;
            qmem.setNodeCleanWithoutNotify();
            qmem.getResultLeftTuples().addTo(trgTuples);
        }

        LeftTupleSinkNode sink = entry.getSink();
        PathMemory pmem = entry.getRmem();

        SegmentMemory[] smems = entry.getSmems();
        int smemIndex = entry.getSmemIndex();
        boolean processRian = entry.isProcessRian();

        long bit = entry.getBit();
        if (entry.isResumeFromNextNode()) {
            SegmentMemory smem = smems[smemIndex];
            if (node != smem.getTipNode()) {
                // get next node and node memory in the segment
                LeftTupleSink nextSink = sink.getNextLeftTupleSinkNode();
                if (nextSink == null) {
                    node = sink;
                } else {
                    // there is a nested subnetwork, take out path
                    node = nextSink;
                }

                nodeMem = nodeMem.getNext();
                bit = bit << 1; // update bit to new node
            } else {
                // Reached end of segment, start on new segment.
                SegmentPropagator.propagate(smem,
                                            trgTuples,
                                            agenda.getWorkingMemory());
                smem = smems[++smemIndex];
                trgTuples = smem.getStagedLeftTuples().takeAll();
                node = smem.getRootNode();
                nodeMem = smem.getNodeMemories().getFirst();
                bit = 1; // update bit to start of new segment
            }
        }

        if (log.isTraceEnabled()) {
            int offset = getOffset(node);
            log.trace("{} Resume {} {}", indent(offset), node.toString(), trgTuples.toStringSizes());
        }
        innerEval(pmem, node, bit, nodeMem, smems, smemIndex, trgTuples, agenda, stack, processRian, executor);
    }

    public void innerEval(PathMemory pmem,
                          NetworkNode node,
                          long bit,
                          Memory nodeMem,
                          SegmentMemory[] smems,
                          int smemIndex,
                          TupleSets<LeftTuple> trgTuples,
                          InternalAgenda agenda,
                          LinkedList<StackEntry> stack,
                          boolean processRian,
                          RuleExecutor executor) {
        TupleSets<LeftTuple> srcTuples;
        SegmentMemory smem = smems[smemIndex];
        TupleSets<LeftTuple> stagedLeftTuples = null;
        while (true) {
            srcTuples = trgTuples; // previous target, is now the source
            if (log.isTraceEnabled()) {
                int offset = getOffset(node);
                log.trace("{} {} {} {}", indent(offset), ++cycle, node.toString(), srcTuples.toStringSizes());
            }

            boolean emptySrcTuples = srcTuples.isEmpty();
            if ( !(NodeTypeEnums.isBetaNode(node) && ((BetaNode)node).isRightInputIsRiaNode() ) ) {
                // The engine cannot skip a ria node, as the dirty might be several levels deep
                if ( emptySrcTuples && smem.getDirtyNodeMask() == 0) {
                    // empty sources and segment is not dirty, skip to non empty src tuples or dirty segment.
                    boolean foundDirty = false;
                    for ( int i = ++smemIndex, length = smems.length; i < length; i++ ) {
                        if (log.isTraceEnabled()) {
                            int offset = getOffset(node);
                            log.trace("{} Skip Segment {}", indent(offset), i-1);
                        }

                        // this is needed for subnetworks that feed into a parent network that has no right inputs,
                        // and may not yet be initialized
                        if ( smem.isEmpty() && !NodeTypeEnums.isTerminalNode(smem.getTipNode()) ) {
                            SegmentUtilities.createChildSegments( agenda.getWorkingMemory(), smem, ((LeftTupleSource)smem.getTipNode()).getSinkPropagator() );
                        }
                        
                        smem = smems[i];
                        bit = 1;
                        srcTuples = smem.getStagedLeftTuples().takeAll();
                        emptySrcTuples = srcTuples.isEmpty();
                        node = smem.getRootNode();
                        nodeMem = smem.getNodeMemories().getFirst();
                        if ( !emptySrcTuples ||
                             smem.getDirtyNodeMask() != 0 ||
                             (NodeTypeEnums.isBetaNode(node) && ((BetaNode)node).isRightInputIsRiaNode() )) {
                            // break if dirty or if we reach a subnetwork. It must break for subnetworks, so they can be searched.
                            foundDirty = true;
                            smemIndex = i;
                            break;
                        }
                    }
                    if (!foundDirty) {
                        break;
                    }
                }
                if (log.isTraceEnabled()) {
                    int offset = getOffset(node);
                    log.trace("{} Segment {}", indent(offset), smemIndex);
                    log.trace("{} {} {} {}", indent(offset), cycle, node.toString(), srcTuples.toStringSizes());
                }
            }

            long dirtyMask = smem.getDirtyNodeMask();
            if ( emptySrcTuples ) {
                while ((dirtyMask & bit) == 0 && node != smem.getTipNode() && !(NodeTypeEnums.isBetaNode(node) && ((BetaNode)node).isRightInputIsRiaNode() ) ) {
                    if (log.isTraceEnabled()) {
                        int offset = getOffset(node);
                        log.trace("{} Skip Node {}", indent(offset), node);
                    }
                    bit = bit << 1; // shift to check the next node
                    node = ((LeftTupleSource) node).getSinkPropagator().getFirstLeftTupleSink();
                    nodeMem = nodeMem.getNext();
                }
            }

            boolean terminalNode = true;
            switch (node.getType()) {
                case NodeTypeEnums.RuleTerminalNode:
                    pRtNode.doNode(( TerminalNode ) node, agenda, srcTuples, executor);
                    break;
                case NodeTypeEnums.QueryTerminalNode:
                    pQtNode.doNode((QueryTerminalNode) node, agenda, srcTuples, stack);
                    break;
                case NodeTypeEnums.RightInputAdaterNode:
                    doRiaNode2(agenda.getWorkingMemory(), srcTuples, (RightInputAdapterNode) node);
                    break;
                default:
                    terminalNode = false;
            }
            if (terminalNode) {
                break;
            }

            stagedLeftTuples = getTargetStagedLeftTuples(node, agenda.getWorkingMemory(), smem);
            LeftTupleSinkNode sink = ((LeftTupleSource) node).getSinkPropagator().getFirstLeftTupleSink();

            trgTuples = evalNode( pmem, node, bit, nodeMem, smems, smemIndex, agenda, stack, processRian, executor, srcTuples, smem, stagedLeftTuples, sink );
            if ( trgTuples == null ) {
                break; // Queries exists and has been placed StackEntry, and there are no current trgTuples to process
            }

            if (node != smem.getTipNode()) {
                // get next node and node memory in the segment
                node = sink;
                nodeMem = nodeMem.getNext();
                bit = bit << 1;
            } else {
                // Reached end of segment, start on new segment.
                smem.getFirst().getStagedLeftTuples().addAll( stagedLeftTuples ); // must put back all the LTs
                // end of SegmentMemory, so we know that stagedLeftTuples is not null
                SegmentPropagator.propagate(smem, trgTuples, agenda.getWorkingMemory());
                bit = 1;
                smem = smems[++smemIndex];
                trgTuples = smem.getStagedLeftTuples().takeAll();

                if (log.isTraceEnabled()) {
                    int offset = getOffset(node);
                    log.trace("{} Segment {}", indent(offset), smemIndex);
                }
                node = smem.getRootNode();
                nodeMem = smem.getNodeMemories().getFirst();
            }
            processRian = true; //  make sure it's reset, so ria nodes are processed
        }

        if ( stagedLeftTuples != null && !stagedLeftTuples.isEmpty() ) {
            smem.getFirst().getStagedLeftTuples().addAll( stagedLeftTuples ); // must put back all the LTs
        }
    }

    public TupleSets<LeftTuple> evalNode( PathMemory pmem, NetworkNode node, long bit, Memory nodeMem,
                                   SegmentMemory[] smems, int smemIndex, InternalAgenda agenda, LinkedList<StackEntry> stack,
                                   boolean processRian, RuleExecutor executor, TupleSets<LeftTuple> srcTuples, SegmentMemory smem,
                                   TupleSets<LeftTuple> stagedLeftTuples, LeftTupleSinkNode sink ) {
        TupleSets<LeftTuple> trgTuples = new TupleSetsImpl<LeftTuple>();
        if ( NodeTypeEnums.isBetaNode( node )) {
            boolean exitInnerEval = evalBetaNode(pmem, node, nodeMem, smems, smemIndex, trgTuples, agenda, stack, processRian, executor, srcTuples, stagedLeftTuples, sink);
            if ( exitInnerEval ) {
                return null;
            }
        } else {
            boolean exitInnerEval = false;
            switch (node.getType()) {
                case NodeTypeEnums.EvalConditionNode: {
                    pEvalNode.doNode((EvalConditionNode) node, (EvalMemory) nodeMem, sink,
                                     agenda.getWorkingMemory(), srcTuples, trgTuples, stagedLeftTuples);
                    break;

                }
                case NodeTypeEnums.FromNode: {
                    pFromNode.doNode((FromNode) node, (FromMemory) nodeMem, sink,
                                     agenda.getWorkingMemory(), srcTuples, trgTuples, stagedLeftTuples);
                    break;
                }
                case NodeTypeEnums.ReactiveFromNode: {
                    pReactiveFromNode.doNode((ReactiveFromNode) node, (ReactiveFromNode.ReactiveFromMemory) nodeMem, sink,
                                             agenda.getWorkingMemory(), srcTuples, trgTuples, stagedLeftTuples);
                    break;
                }
                case NodeTypeEnums.QueryElementNode: {
                    exitInnerEval =  evalQueryNode(pmem, node, bit, nodeMem, smems, smemIndex, trgTuples,
                                                   agenda.getWorkingMemory(), stack, srcTuples, sink, stagedLeftTuples);
                    break;
                }
                case NodeTypeEnums.TimerConditionNode: {
                    pTimerNode.doNode( (TimerNode) node, (TimerNodeMemory) nodeMem, pmem, smem, sink, agenda, srcTuples, trgTuples, stagedLeftTuples);
                    break;
                }
                case NodeTypeEnums.ConditionalBranchNode: {
                    pBranchNode.doNode((ConditionalBranchNode) node, (ConditionalBranchMemory) nodeMem, sink,
                                       agenda, srcTuples, trgTuples, stagedLeftTuples, executor);
                    break;
                }
            }
            if ( exitInnerEval && trgTuples.isEmpty() ) {
                return null;
            }
        }
        return trgTuples;
    }

    private static TupleSets<LeftTuple> getTargetStagedLeftTuples(NetworkNode node, InternalWorkingMemory wm, SegmentMemory smem) {
        if (node == smem.getTipNode()) {
            // we are about to process the segment tip, allow it to merge insert/update/delete clashes
            if ( smem.isEmpty() ) {
                SegmentUtilities.createChildSegments(wm, smem, ((LeftTupleSource) node).getSinkPropagator() );
            }
            return smem.getFirst().getStagedLeftTuples().takeAll();
        } else {
            return null;
        }
    }

    private boolean evalQueryNode(PathMemory pmem,
                                  NetworkNode node,
                                  long bit,
                                  Memory nodeMem,
                                  SegmentMemory[] smems,
                                  int smemIndex,
                                  TupleSets<LeftTuple> trgTuples,
                                  InternalWorkingMemory wm,
                                  LinkedList<StackEntry> stack,
                                  TupleSets<LeftTuple> srcTuples,
                                  LeftTupleSinkNode sink,
                                  TupleSets<LeftTuple> stagedLeftTuples) {
        QueryElementNodeMemory qmem = (QueryElementNodeMemory) nodeMem;

        if (srcTuples.isEmpty() && qmem.getResultLeftTuples().isEmpty()) {
            // no point in evaluating query element, and setting up stack, if there is nothing to process
            return false;
        }

        QueryElementNode qnode = (QueryElementNode) node;

        if (log.isTraceEnabled()) {
            int offset = getOffset(node);
            log.trace("{} query result tuples {}", indent(offset), qmem.getResultLeftTuples().toStringSizes() );
        }

        // result tuples can happen when reactivity occurs inside of the query, prior to evaluation
        // we will need special behaviour to add the results again, when this query result resumes
        qmem.getResultLeftTuples().addTo( trgTuples );
        qmem.setNodeCleanWithoutNotify();

        if (!srcTuples.isEmpty()) {
            // only process the Query Node if there are src tuples
            StackEntry stackEntry = new StackEntry(node, bit, sink, pmem, nodeMem, smems,
                                                   smemIndex, trgTuples, true, true);

            stack.add(stackEntry);

            pQueryNode.doNode(qnode, (QueryElementNodeMemory) nodeMem, stackEntry,
                              wm, srcTuples, trgTuples, stagedLeftTuples);

            SegmentMemory qsmem = ((QueryElementNodeMemory) nodeMem).getQuerySegmentMemory();
            List<PathMemory> qpmems = qsmem.getPathMemories();

            // Build the evaluation information for each 'or' branch
            for (int i = 0; i < qpmems.size() ; i++) {
                PathMemory qpmem = qpmems.get(i);

                pmem = qpmem;
                smems = qpmem.getSegmentMemories();
                smemIndex = 0;
                SegmentMemory smem = smems[smemIndex]; // 0

                LeftTupleNode liaNode = (LeftInputAdapterNode) qpmem.getPathEndNode().getPathNodes()[0];

                if (liaNode == smem.getTipNode()) {
                    // segment only has liaNode in it
                    // nothing is staged in the liaNode, so skip to next segment
                    smem = smems[++smemIndex]; // 1
                    node = smem.getRootNode();
                    nodeMem = smem.getNodeMemories().getFirst();
                    bit = 1;
                } else {
                    // lia is in shared segment, so point to next node
                    node = liaNode.getSinkPropagator().getFirstLeftTupleSink();
                    nodeMem = smem.getNodeMemories().getFirst().getNext(); // skip the liaNode memory
                    bit = 2;
                }

                trgTuples = smem.getStagedLeftTuples().takeAll();
                stackEntry = new StackEntry(node, bit, null, pmem,
                                            nodeMem, smems, smemIndex,
                                            trgTuples, false, true);
                if (log.isTraceEnabled()) {
                    int offset = getOffset(stackEntry.getNode());
                    log.trace("{} ORQueue branch={} {} {}", indent(offset), i, stackEntry.getNode().toString(), trgTuples.toStringSizes());
                }
                stack.add(stackEntry);
            }
            return true;
        } else {
            return false;
        }

    }

    private boolean evalBetaNode(PathMemory pmem, NetworkNode node, Memory nodeMem,
                                 SegmentMemory[] smems, int smemIndex, TupleSets<LeftTuple> trgTuples, InternalAgenda agenda,
                                 LinkedList<StackEntry> stack, boolean processRian, RuleExecutor executor,
                                 TupleSets<LeftTuple> srcTuples, TupleSets<LeftTuple> stagedLeftTuples, LeftTupleSinkNode sink) {
        BetaNode betaNode = (BetaNode) node;
        BetaMemory bm;
        AccumulateMemory am = null;
        if (NodeTypeEnums.AccumulateNode == node.getType()) {
            am = (AccumulateMemory) nodeMem;
            bm = am.getBetaMemory();
        } else {
            bm = (BetaMemory) nodeMem;
        }

        if (processRian && betaNode.isRightInputIsRiaNode()) {
            // if the subnetwork is nested in this segment, it will create srcTuples containing
            // peer LeftTuples, suitable for the node in the main path.
            doRiaNode( agenda, pmem, srcTuples,
                       betaNode, sink, smems, smemIndex, nodeMem, bm, stack, executor );
            return true; // return here, doRiaNode queues the evaluation on the stack, which is necessary to handled nested query nodes
        }

        switchOnDoBetaNode(node, trgTuples, agenda.getWorkingMemory(), srcTuples, stagedLeftTuples, sink, bm, am);

        return false;
    }

    private void switchOnDoBetaNode(NetworkNode node, TupleSets<LeftTuple> trgTuples, InternalWorkingMemory wm, TupleSets<LeftTuple> srcTuples,
                                    TupleSets<LeftTuple> stagedLeftTuples, LeftTupleSinkNode sink, BetaMemory bm, AccumulateMemory am) {
        if (log.isTraceEnabled()) {
            int offset = getOffset(node);
            log.trace("{} rightTuples {}", indent(offset), bm.getStagedRightTuples().toStringSizes());
        }

        switch (node.getType()) {
            case NodeTypeEnums.JoinNode: {
                pJoinNode.doNode((JoinNode) node, sink, bm,
                                 wm, srcTuples, trgTuples, stagedLeftTuples);
                break;
            }
            case NodeTypeEnums.NotNode: {
                pNotNode.doNode((NotNode) node, sink, bm,
                                wm, srcTuples, trgTuples, stagedLeftTuples);
                break;
            }
            case NodeTypeEnums.ExistsNode: {
                pExistsNode.doNode((ExistsNode) node, sink, bm,
                                   wm, srcTuples, trgTuples, stagedLeftTuples);
                break;
            }
            case NodeTypeEnums.AccumulateNode: {
                pAccNode.doNode((AccumulateNode) node, sink, am, wm,
                                srcTuples, trgTuples, stagedLeftTuples);
                break;
            }
        }
    }

    private void doRiaNode(InternalAgenda agenda,
                           PathMemory pmem,
                           TupleSets<LeftTuple> srcTuples,
                           BetaNode betaNode,
                           LeftTupleSinkNode sink,
                           SegmentMemory[] smems,
                           int smemIndex,
                           Memory nodeMem,
                           BetaMemory bm,
                           LinkedList<StackEntry> stack,
                           RuleExecutor executor) {
        RiaPathMemory pathMem = bm.getRiaRuleMemory();
        SegmentMemory[] subnetworkSmems = pathMem.getSegmentMemories();
        SegmentMemory subSmem = null;
        for ( int i = 0; subSmem == null; i++) {
            // segment positions outside of the subnetwork, in the parent chain, are null
            // so we must iterate to find the first non null segment memory
            subSmem =  subnetworkSmems[i];
        }

        // Resume the node after the riaNode segment has been processed and the right input memory populated
        StackEntry stackEntry = new StackEntry(betaNode, bm.getNodePosMaskBit(), sink, pmem, nodeMem, smems,
                                               smemIndex, srcTuples, false, false);
        stack.add(stackEntry);
        if (log.isTraceEnabled()) {
            int offset = getOffset(betaNode);
            log.trace("{} RiaQueue {} {}", indent(offset), betaNode.toString(), srcTuples.toStringSizes());
        }


        TupleSets<LeftTuple> subLts = subSmem.getStagedLeftTuples().takeAll();
        // node is first in the segment, so bit is 1
        innerEval( pathMem, subSmem.getRootNode(), 1,
                   subSmem.getNodeMemories().getFirst(),
                   subnetworkSmems, subSmem.getPos(),
                   subLts, agenda, stack, true, executor );
    }

    private void doRiaNode2(InternalWorkingMemory wm,
                            TupleSets<LeftTuple> srcTuples,
                            RightInputAdapterNode riaNode) {

        ObjectSink[] sinks = riaNode.getObjectSinkPropagator().getSinks();

        BetaNode betaNode = (BetaNode) sinks[0];
        BetaMemory bm;
        Memory nodeMem = wm.getNodeMemory(betaNode);
        if (NodeTypeEnums.AccumulateNode == betaNode.getType()) {
            bm = ((AccumulateMemory) nodeMem).getBetaMemory();
        } else {
            bm = (BetaMemory) nodeMem;
        }
        TupleSets<RightTuple> rightTuples = bm.getStagedRightTuples();

        // Build up iteration array for other sinks
        BetaNode[] bns = null;
        BetaMemory[] bms = null;
        int length = sinks.length;
        if (length > 1) {
            bns = new BetaNode[sinks.length - 1];
            bms = new BetaMemory[sinks.length - 1];
            for (int i = 1; i < length; i++) {
                bns[i - 1] = (BetaNode) sinks[i];
                Memory nodeMem2 = wm.getNodeMemory(bns[i - 1]);
                if (NodeTypeEnums.AccumulateNode == betaNode.getType()) {
                    bms[i - 1] = ((AccumulateMemory) nodeMem2).getBetaMemory();
                } else {
                    bms[i - 1] = (BetaMemory) nodeMem2;
                }
            }
        }

        length--; // subtract one, as first is not in the array;
        for (LeftTuple leftTuple = srcTuples.getInsertFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            if ( bm.getStagedRightTuples().isEmpty() ) {
                bm.setNodeDirtyWithoutNotify();
            }
            leftTuple.clearStaged();
            rightTuples.addInsert( (RightTuple) leftTuple );
            ( (SubnetworkTuple) leftTuple ).setStagedOnRight( true );

            if (bns != null) {
                for (int i = 0; i < length; i++) {
                    if ( bms[i].getStagedRightTuples().isEmpty() ) {
                        bms[i].setNodeDirtyWithoutNotify();
                    }
                    leftTuple = riaNode.createPeer( leftTuple );
                    bms[i].getStagedRightTuples().addInsert((RightTuple)leftTuple);
                }
            }

            leftTuple = next;
        }

        for (LeftTuple leftTuple = srcTuples.getDeleteFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            if ( rightTuples.isEmpty() ) {
                bm.setNodeDirtyWithoutNotify();
            }
            leftTuple.clearStaged();
            rightTuples.addDelete((RightTuple)leftTuple);
            ( (SubnetworkTuple) leftTuple ).setStagedOnRight( true );

            if (bns != null) {
                for (int i = 0; i < length; i++) {
                    leftTuple = leftTuple.getPeer();
                    if ( bms[i].getStagedRightTuples().isEmpty() ) {
                        bms[i].setNodeDirtyWithoutNotify();
                    }
                    bms[i].getStagedRightTuples().addDelete((RightTuple)leftTuple);
                    ( (SubnetworkTuple) leftTuple ).setStagedOnRight( true );
                }
            }

            leftTuple = next;
        }

        for (LeftTuple leftTuple = srcTuples.getUpdateFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            if ( rightTuples.isEmpty() ) {
                bm.setNodeDirtyWithoutNotify();
            }
            leftTuple.clearStaged();
            rightTuples.addUpdate((RightTuple)leftTuple);
            ( (SubnetworkTuple) leftTuple ).setStagedOnRight( true );

            if (bns != null) {
                for (int i = 0; i < length; i++) {
                    leftTuple = leftTuple.getPeer();

                    if ( bms[i].getStagedRightTuples().isEmpty() ) {
                        bms[i].setNodeDirtyWithoutNotify();
                    }
                    bms[i].getStagedRightTuples().addUpdate((RightTuple)leftTuple);
                    ( (SubnetworkTuple) leftTuple ).setStagedOnRight( true );
                }
            }

            leftTuple = next;
        }

        srcTuples.resetAll();
    }

    public static void findLeftTupleBlocker(BetaNode betaNode, TupleMemory rtm,
                                             ContextEntry[] contextEntry, BetaConstraints constraints,
                                             LeftTuple leftTuple, boolean useLeftMemory) {
        // This method will also remove rightTuples that are from subnetwork where no leftmemory use used
        FastIterator it = betaNode.getRightIterator(rtm);
        for (RightTuple rightTuple = betaNode.getFirstRightTuple(leftTuple, rtm, null, it); rightTuple != null; ) {
            RightTuple nextRight = (RightTuple) it.next(rightTuple);
            if (constraints.isAllowedCachedLeft(contextEntry,
                                                rightTuple.getFactHandleForEvaluation())) {
                leftTuple.setBlocker(rightTuple);

                if (useLeftMemory) {
                    rightTuple.addBlocked(leftTuple);
                    break;
                } else if (betaNode.isRightInputIsRiaNode()) {
                    // If we aren't using leftMemory and the right input is a RIAN, then we must iterate and find all subetwork right tuples and remove them
                    // so we don't break
                    rtm.remove(rightTuple);
                } else {
                    break;
                }
            }
            rightTuple = nextRight;
        }
    }


    public static void unlinkAndDeleteChildLeftTuple( LeftTuple childLeftTuple,
                                                      TupleSets<LeftTuple> trgLeftTuples,
                                                      TupleSets<LeftTuple> stagedLeftTuples ) {
        childLeftTuple.unlinkFromRightParent();
        childLeftTuple.unlinkFromLeftParent();
        deleteChildLeftTuple( childLeftTuple, trgLeftTuples, stagedLeftTuples );
    }

    public static void deleteChildLeftTuple( LeftTuple childLeftTuple, TupleSets<LeftTuple> trgLeftTuples, TupleSets<LeftTuple> stagedLeftTuples ) {
        if (!childLeftTuple.isStagedOnRight()) {
            switch ( childLeftTuple.getStagedType() ) {
                // handle clash with already staged entries
                case LeftTuple.INSERT:
                    stagedLeftTuples.removeInsert( childLeftTuple );
                    trgLeftTuples.addNormalizedDelete( childLeftTuple );
                    return;
                case LeftTuple.UPDATE:
                    stagedLeftTuples.removeUpdate( childLeftTuple );
                    break;
            }
            trgLeftTuples.addDelete(childLeftTuple);
        }
    }

    public static void doUpdatesReorderLeftMemory(BetaMemory bm,
                                                  TupleSets<LeftTuple> srcLeftTuples) {
        TupleMemory ltm = bm.getLeftTupleMemory();

        // sides must first be re-ordered, to ensure iteration integrity
        for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; leftTuple = leftTuple.getStagedNext()) {
            ltm.remove(leftTuple);
        }

        for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; leftTuple = leftTuple.getStagedNext()) {
            ltm.add(leftTuple);
            for (LeftTuple childLeftTuple = leftTuple.getFirstChild(); childLeftTuple != null; ) {
                LeftTuple childNext = childLeftTuple.getHandleNext();
                childLeftTuple.reAddRight();
                childLeftTuple = childNext;
            }
        }
    }

    public static void doUpdatesExistentialReorderLeftMemory(BetaMemory bm,
                                                             TupleSets<LeftTuple> srcLeftTuples) {
        TupleMemory ltm = bm.getLeftTupleMemory();

        // sides must first be re-ordered, to ensure iteration integrity
        for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; leftTuple = leftTuple.getStagedNext()) {
            if ( leftTuple.getMemory() != null ) {
                ltm.remove(leftTuple);
            }
        }

        for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; leftTuple = leftTuple.getStagedNext()) {
            RightTuple blocker = leftTuple.getBlocker();
            if ( blocker == null ) {
                ltm.add(leftTuple);
                for (LeftTuple childLeftTuple = leftTuple.getFirstChild(); childLeftTuple != null; ) {
                    LeftTuple childNext = childLeftTuple.getHandleNext();
                    childLeftTuple.reAddRight();
                    childLeftTuple = childNext;
                }
            } else if ( blocker.getStagedType() != LeftTuple.NONE ) {
                // it's blocker is also being updated, so remove to force it to start from the beginning
                blocker.removeBlocked( leftTuple );
            }
        }
    }

    public static void doUpdatesReorderRightMemory(BetaMemory bm,
                                                   TupleSets<RightTuple> srcRightTuples) {
        TupleMemory rtm = bm.getRightTupleMemory();

        for (RightTuple rightTuple = srcRightTuples.getUpdateFirst(); rightTuple != null; rightTuple = rightTuple.getStagedNext()) {
            if ( rightTuple.getMemory() != null ) {
                rightTuple.setTempRightTupleMemory(rightTuple.getMemory());
                rtm.remove(rightTuple);
            }
        }

        for (RightTuple rightTuple = srcRightTuples.getUpdateFirst(); rightTuple != null; rightTuple = rightTuple.getStagedNext()) {
            if ( rightTuple.getTempRightTupleMemory() != null ) {
                rtm.add(rightTuple);
                for (LeftTuple childLeftTuple = rightTuple.getFirstChild(); childLeftTuple != null; ) {
                    LeftTuple childNext = childLeftTuple.getRightParentNext();
                    childLeftTuple.reAddLeft();
                    childLeftTuple = childNext;
                }
            }
        }
    }

    public static void doUpdatesExistentialReorderRightMemory(BetaMemory bm,
                                                              BetaNode betaNode,
                                                              TupleSets<RightTuple> srcRightTuples) {
        TupleMemory rtm = bm.getRightTupleMemory();

        boolean resumeFromCurrent = !(betaNode.isIndexedUnificationJoin() || rtm.getIndexType().isComparison());

        // remove all the staged rightTuples from the memory before to readd them all
        // this is to avoid split bucket when an updated rightTuple hasn't been moved yet
        // and so it is the first entry in the wrong bucket

        if ( rtm.getIndexType() != TupleMemory.IndexType.NONE) {
            for ( RightTuple rightTuple = srcRightTuples.getDeleteFirst(); rightTuple != null; rightTuple = rightTuple.getStagedNext() ) {
                rtm.remove( rightTuple );
            }
        }

        for (RightTuple rightTuple = srcRightTuples.getUpdateFirst(); rightTuple != null; rightTuple = rightTuple.getStagedNext()) {
            if (rightTuple.getMemory() != null) {
                rightTuple.setTempRightTupleMemory(rightTuple.getMemory());

                if (resumeFromCurrent) {
                    if (rightTuple.getBlocked() != null) {
                        // look for a non-staged right tuple first forward ...
                        RightTuple tempRightTuple = ( RightTuple ) rightTuple.getNext();
                        while ( tempRightTuple != null && tempRightTuple.getStagedType() != LeftTuple.NONE ) {
                            // next cannot be an updated or deleted rightTuple
                            tempRightTuple = (RightTuple) tempRightTuple.getNext();
                        }

                        // ... and if cannot find one try backward
                        if ( tempRightTuple == null ) {
                            tempRightTuple = ( RightTuple ) rightTuple.getPrevious();
                            while ( tempRightTuple != null && tempRightTuple.getStagedType() != LeftTuple.NONE ) {
                                // next cannot be an updated or deleted rightTuple
                                tempRightTuple = (RightTuple) tempRightTuple.getPrevious();
                            }
                        }

                        rightTuple.setTempNextRightTuple( tempRightTuple );
                    }
                }

                rightTuple.setTempBlocked(rightTuple.getBlocked());
                rightTuple.setBlocked(null);
                rtm.remove(rightTuple);
            }
        }

        for (RightTuple rightTuple = srcRightTuples.getUpdateFirst(); rightTuple != null; rightTuple = rightTuple.getStagedNext()) {
            rtm.add( rightTuple );

            if (resumeFromCurrent) {
                RightTuple tempRightTuple = rightTuple.getTempNextRightTuple();
                if ( rightTuple.getBlocked() != null && tempRightTuple == null && rightTuple.getMemory() == rightTuple.getTempRightTupleMemory()  ) {
                    // the next RightTuple was null, but current RightTuple was added back into the same bucket, so reset as root blocker to re-match can be attempted
                    rightTuple.setTempNextRightTuple( rightTuple );
                }
            }

            for (LeftTuple childLeftTuple = rightTuple.getFirstChild(); childLeftTuple != null; ) {
                LeftTuple childNext = childLeftTuple.getRightParentNext();
                childLeftTuple.reAddLeft();
                childLeftTuple = childNext;
            }
        }

        if ( rtm.getIndexType() != TupleMemory.IndexType.NONE) {
            for ( RightTuple rightTuple = srcRightTuples.getDeleteFirst(); rightTuple != null; rightTuple = rightTuple.getStagedNext() ) {
                rtm.add( rightTuple );
            }
        }
    }

    public static boolean useLeftMemory(LeftTupleSource tupleSource, Tuple leftTuple) {
        boolean useLeftMemory = true;
        if (!tupleSource.isLeftTupleMemoryEnabled()) {
            // This is a hack, to not add closed DroolsQuery objects
            Object object = leftTuple.getRootTuple().getFactHandle().getObject();
            if (!(object instanceof DroolsQuery) || !((DroolsQuery) object).isOpen()) {
                useLeftMemory = false;
            }
        }
        return useLeftMemory;
    }

    public static void normalizeStagedTuples( TupleSets<LeftTuple> stagedLeftTuples, LeftTuple childLeftTuple ) {
        if (!childLeftTuple.isStagedOnRight()) {
            switch ( childLeftTuple.getStagedType() ) {
                // handle clash with already staged entries
                case LeftTuple.INSERT:
                    stagedLeftTuples.removeInsert( childLeftTuple );
                    break;
                case LeftTuple.UPDATE:
                    stagedLeftTuples.removeUpdate( childLeftTuple );
                    break;
            }
        }
    }
}
