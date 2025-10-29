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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.drools.base.common.NetworkNode;
import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.core.common.ActivationsManager;
import org.drools.core.common.Memory;
import org.drools.core.common.NodeMemories;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.SegmentMemorySupport;
import org.drools.core.common.TupleSets;
import org.drools.core.common.TupleSetsImpl;
import org.drools.core.reteoo.AbstractTerminalNode;
import org.drools.core.reteoo.AccumulateNode;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.core.reteoo.AsyncReceiveNode;
import org.drools.core.reteoo.AsyncReceiveNode.AsyncReceiveMemory;
import org.drools.core.reteoo.AsyncSendNode;
import org.drools.core.reteoo.AsyncSendNode.AsyncSendMemory;
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
import org.drools.core.reteoo.NotNode;
import org.drools.core.reteoo.ObjectSink;
import org.drools.core.reteoo.PathEndNode;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.QueryElementNode.QueryElementNodeMemory;
import org.drools.core.reteoo.QueryTerminalNode;
import org.drools.core.reteoo.ReactiveFromNode;
import org.drools.core.reteoo.RightInputAdapterNode;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.SubnetworkTuple;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.reteoo.TimerNode;
import org.drools.core.reteoo.TimerNode.TimerNodeMemory;
import org.drools.core.reteoo.Tuple;
import org.drools.core.reteoo.TupleFactory;
import org.drools.core.reteoo.TupleImpl;
import org.drools.core.reteoo.TupleToObjectNode;
import org.drools.core.reteoo.TupleToObjectNode.SubnetworkPathMemory;
import org.drools.core.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.base.reteoo.NodeTypeEnums.AccumulateNode;
import static org.drools.core.common.TupleSetsImpl.createLeftTupleTupleSets;
import static org.drools.core.phreak.BuildtimeSegmentUtilities.nextNodePosMask;

public class RuleNetworkEvaluatorImpl implements RuleNetworkEvaluator {

    private static final Logger log = LoggerFactory.getLogger(RuleNetworkEvaluatorImpl.class);

    private final PhreakJoinNode         pJoinNode;
    private final PhreakEvalNode         pEvalNode;
    private final PhreakFromNode         pFromNode;
    private final PhreakReactiveFromNode pReactiveFromNode;
    private final PhreakNotNode          pNotNode;
    private final PhreakExistsNode       pExistsNode;
    private final PhreakAccumulateNode   pAccNode;
    private final PhreakAccumulateNode   pGroupByNode;
    private final PhreakBranchNode       pBranchNode;
    private final PhreakQueryNode        pQueryNode;
    private final PhreakTimerNode        pTimerNode;
    private final PhreakAsyncSendNode    pSendNode;
    private final PhreakAsyncReceiveNode pReceiveNode;
    private final PhreakRuleTerminalNode pRtNode;
    private final PhreakQueryTerminalNode pQtNode;

    private static int cycle = 0;

    private NodeMemories nodeMemories;

    private SegmentMemorySupport segmentMemorySupport;

    public RuleNetworkEvaluatorImpl(ReteEvaluator reteEvaluator, NodeMemories nodeMemories, SegmentMemorySupport segmentMemorySupport) {
        this.nodeMemories = nodeMemories;
        this.segmentMemorySupport = segmentMemorySupport;
        pJoinNode   = PhreakNetworkNodeFactory.Factory.get().createPhreakJoinNode(reteEvaluator);
        pEvalNode   = PhreakNetworkNodeFactory.Factory.get().createPhreakEvalNode(reteEvaluator);
        pFromNode   = PhreakNetworkNodeFactory.Factory.get().createPhreakFromNode(reteEvaluator);
        pReactiveFromNode = PhreakNetworkNodeFactory.Factory.get().createPhreakReactiveFromNode(reteEvaluator);
        pNotNode    = PhreakNetworkNodeFactory.Factory.get().createPhreakNotNode(reteEvaluator);
        pExistsNode = PhreakNetworkNodeFactory.Factory.get().createPhreakExistsNode(reteEvaluator);
        pAccNode    = PhreakNetworkNodeFactory.Factory.get().createPhreakAccumulateNode(reteEvaluator);
        pGroupByNode = PhreakNetworkNodeFactory.Factory.get().createPhreakGroupByNode(reteEvaluator);
        pBranchNode = PhreakNetworkNodeFactory.Factory.get().createPhreakBranchNode(reteEvaluator);
        pQueryNode  = PhreakNetworkNodeFactory.Factory.get().createPhreakQueryNode(reteEvaluator);
        pTimerNode  = PhreakNetworkNodeFactory.Factory.get().createPhreakTimerNode(reteEvaluator);
        pSendNode   = PhreakNetworkNodeFactory.Factory.get().createPhreakAsyncSendNode(reteEvaluator);
        pReceiveNode = PhreakNetworkNodeFactory.Factory.get().createPhreakAsyncReceiveNode(reteEvaluator);
        pRtNode     = PhreakNetworkNodeFactory.Factory.get().createPhreakRuleTerminalNode(reteEvaluator);
        pQtNode     = PhreakNetworkNodeFactory.Factory.get().createPhreakQueryTerminalNode(reteEvaluator);
    }

    @Override
    public void evaluateNetwork(RuleExecutor executor,
                                PathMemory pmem) {
        evaluateNetwork(pmem.getActualActivationsManager(), executor, pmem);
    }

    @Override
    public void evaluateNetwork(ActivationsManager activationsManager,
                                RuleExecutor executor,
                                PathMemory pmem) {
        SegmentMemory[] smems = pmem.getSegmentMemories();


        SegmentMemory smem = smems[0];
        if (smem == null) {
            // if there's no first smem it's a pure alpha firing and then doesn't require any furthe evaluation
            return;
        }

        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) smem.getRootNode();

        NetworkNode node;
        Memory nodeMem;
        boolean firstSegmentIsOnlyLia = liaNode == smem.getTipNode();
        if (firstSegmentIsOnlyLia) {
            // segment only has liaNode in it
            // nothing is staged in the liaNode, so skip to next segment
            smem = smems[1];
            node = smem.getRootNode();
            nodeMem = smem.getNodeMemories()[0];
        } else {
            // lia is in shared segment, so point to next node
            node = liaNode.getSinkPropagator().getFirstLeftTupleSink();
            nodeMem = smem.getNodeMemories()[1]; // skip the liaNode memory
        }

        TupleSets srcTuples = smem.getStagedLeftTuples();
        if (log.isTraceEnabled()) {
            log.trace("Rule[name={}] segments={} {}", ((TerminalNode)pmem.getPathEndNode()).getRule().getName(), smems.length, srcTuples.toStringSizes());
        }
        outerEval(activationsManager, executor, pmem, smems, firstSegmentIsOnlyLia ? 1 : 0, firstSegmentIsOnlyLia ? 1L : 2L, nodeMem, node, srcTuples, true);
    }
    
    @Override
    public void evaluate(PathMemory pmem,
                          ActivationsManager activationsManager,
                          NetworkNode sink,
                          Memory tm,
                          TupleSets trgLeftTuples) {
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
        for (NetworkNode node = sm.getRootNode(); node != sink; node = ((LeftTupleSource) node).getSinkPropagator()
                .getFirstLeftTupleSink()) {
            //update the bit to the correct node position.
            bit = nextNodePosMask(bit);
        }

        outerEval(activationsManager, pmem.getRuleAgendaItem().getRuleExecutor(), pmem, smems,
                smemIndex, bit, tm, sink, trgLeftTuples, true);
    }
    
    
    @Override
    public void forceFlushWhenSubnetwork(PathMemory pmem) {
        for (PathMemory outPmem : findPathsToFlushFromSubnetwork(pmem)) {
            forceFlushPath(outPmem);
        }
    }
    
    @Override
    public List<PathMemory> findPathsToFlushFromSubnetwork(PathMemory pmem) {
        List<PathMemory> paths = null;
        if (pmem.isDataDriven() && pmem.getNodeType() == NodeTypeEnums.TupleToObjectNode) {
            for (PathEndNode pnode : pmem.getPathEndNode().getPathEndNodes()) {
                if (NodeTypeEnums.isTerminalNode(pnode)) {
                    PathMemory outPmem = nodeMemories.getNodeMemory(pnode);
                    if (outPmem.isDataDriven()) {
                        if (paths == null) {
                            paths = new ArrayList<>();
                        }
                        paths.add(outPmem);
                    }
                }
            }
        }
        return paths == null ? Collections.emptyList() : paths;
    }
    
    
    @Override
    public void forceFlushPath(PathMemory outPmem) {
        SegmentMemory outSmem = outPmem.getSegmentMemories()[0];
        if (outSmem != null) {
            forceFlushLeftTuple(outPmem, outSmem, new TupleSetsImpl());
        }
    }
    
    
    @Override
    public void forceFlushLeftTuple(PathMemory pmem,
                                    SegmentMemory sm, 
                                    TupleSets leftTupleSets) {
        SegmentMemory[] smems = pmem.getSegmentMemories();

        LeftTupleNode node;
        Memory mem;
        long bit = 1;
        if (NodeTypeEnums.isLeftInputAdapterNode(sm.getRootNode()) && !NodeTypeEnums.isLeftInputAdapterNode(sm
                .getTipNode())) {
            // The segment is the first and it has the lian shared with other nodes, the lian must be skipped, so adjust the bit and sink
            node = sm.getRootNode().getSinkPropagator().getFirstLeftTupleSink();
            mem = sm.getNodeMemories()[1];
            bit = 2; // adjust bit to point to next node
        } else {
            node = sm.getRootNode();
            mem = sm.getNodeMemories()[0];
        }

        PathMemory rtnPmem = NodeTypeEnums.isTerminalNode(pmem.getPathEndNode()) ? pmem : nodeMemories.getNodeMemory(
                (AbstractTerminalNode) pmem.getPathEndNode().getPathEndNodes()[0]);

        ActivationsManager activationsManager = pmem.getActualActivationsManager();
        outerEval(activationsManager, rtnPmem.getOrCreateRuleAgendaItem().getRuleExecutor(), pmem, smems, sm.getPos(), bit, mem, node,
                leftTupleSets,
                true);
    }
    
    
    @Override
    public boolean flushLeftTupleIfNecessary(SegmentMemory sm, boolean streamMode) {
        return flushLeftTupleIfNecessary(sm, null, streamMode, Tuple.NONE);
    }

    @Override
    public boolean flushLeftTupleIfNecessary(SegmentMemory sm,
                                                    TupleImpl leftTuple,
                                                    boolean streamMode,
                                                    short stagedType) {
        PathMemory pmem = findPathToFlush(sm, leftTuple, streamMode);

        if (pmem == null) {
            return false;
        }

        forceFlushLeftTuple(pmem, sm, createLeftTupleTupleSets(leftTuple, stagedType));
        forceFlushWhenSubnetwork(pmem);
        return true;
    }

    public static PathMemory findPathToFlush(SegmentMemory sm, TupleImpl leftTuple, boolean streamMode) {
        boolean forceFlush = streamMode || (leftTuple != null && leftTuple.getFactHandle() != null && leftTuple
                .getFactHandle().isEvent());
        return forceFlush ? sm.getPathMemories().get(0) : sm.getFirstDataDrivenPathMemory();
    }

    private void outerEval(ActivationsManager activationsManager,
                          RuleExecutor executor,
                          PathMemory pmem,
                          SegmentMemory[] smems,
                          int smemIndex,
                          long bit,
                          Memory nodeMem,
                          NetworkNode node,
                          TupleSets trgTuples,
                          boolean processSubnetwork) {

        LinkedList<StackEntry> stack = new LinkedList<>();
        innerEval(activationsManager, executor, stack, pmem, smems, smemIndex, bit, nodeMem, node, trgTuples, processSubnetwork);
        while (!stack.isEmpty()) {
            // eval
            StackEntry entry = stack.removeLast();
            evalStackEntry(activationsManager, executor, stack, entry);
        }
    }

    private void evalStackEntry(ActivationsManager activationsManager,
                               RuleExecutor executor,
                               LinkedList<StackEntry> stack,
                               StackEntry entry) {
        NetworkNode node = entry.getNode();
        Memory nodeMem = entry.getNodeMem();
        TupleSets trgTuples = entry.getTrgTuples();
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
        boolean processSubnetwork = entry.isProcessSubnetwork();

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
                bit = nextNodePosMask(bit); // update bit to new node
            } else {
                // Reached end of segment, start on new segment.
                propagate(smem, trgTuples);
                smem = smems[++smemIndex];
                trgTuples = smem.getStagedLeftTuples().takeAll();
                node = smem.getRootNode();
                nodeMem = smem.getNodeMemories()[0];
                bit = 1; // update bit to start of new segment
            }
        }

        if (log.isTraceEnabled()) {
            int offset = getOffset(node);
            log.trace("{} Resume {} {}", indent(offset), node.toString(), trgTuples.toStringSizes());
        }
        innerEval(activationsManager, executor, stack, pmem, smems, smemIndex, bit, nodeMem, node, trgTuples, processSubnetwork);
    }

    private void innerEval(ActivationsManager activationsManager,
                          RuleExecutor executor,
                          LinkedList<StackEntry> stack,
                          PathMemory pmem,
                          final SegmentMemory[] smems,
                          int smemIndex,
                          long bit,
                          Memory nodeMem,
                          NetworkNode node,
                          TupleSets trgTuples,
                          boolean processSubnetwork) {
        SegmentMemory smem = smems[smemIndex];
        TupleSets stagedLeftTuples = null;
        while (true) {
            TupleSets srcTuples = trgTuples; // previous target, is now the source
            if (log.isTraceEnabled()) {
                int offset = getOffset(node);
                log.trace("{} {} {} {}", indent(offset), ++cycle, node.toString(), srcTuples.toStringSizes());
            }

            if (!(NodeTypeEnums.isBetaNodeWithSubnetwork(node))) {
                // The engine cannot skip a TupleToObjectNode node, as the dirty might be several levels deep
                if (srcTuples.isEmpty() && smem.getDirtyNodeMask() == 0) {
                    // empty sources and segment is not dirty, skip to non empty src tuples or dirty segment.
                    boolean foundDirty = false;
                    for (int i = ++smemIndex, length = smems.length; i < length; i++) {
                        if (log.isTraceEnabled()) {
                            int offset = getOffset(node);
                            log.trace("{} Skip Segment {}", indent(offset), i-1);
                        }

                        // this is needed for subnetworks that feed into a parent network that has no right inputs,
                        // and may not yet be initialized
                        if (smem.isEmpty() && !NodeTypeEnums.isTerminalNode(smem.getTipNode())) {
                            segmentMemorySupport.createChildSegments(smem.getTipNode().getSinkPropagator(), smem);
                        }
                        
                        smem = smems[i];
                        bit = 1;
                        srcTuples = smem.getStagedLeftTuples().takeAll();
                        node = smem.getRootNode();
                        nodeMem = smem.getNodeMemories()[0];
                        if (!srcTuples.isEmpty() ||
                             smem.getDirtyNodeMask() != 0 ||
                             (NodeTypeEnums.isBetaNode(node) && ((BetaNode)node).getRightInput().inputIsTupleToObjectNode())) {
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

            if (srcTuples.isEmpty()) {
                while ((smem.getDirtyNodeMask() & bit) == 0 && node != smem.getTipNode() && !NodeTypeEnums.isBetaNodeWithSubnetwork(node)) {
                //while ((dirtyMask & bit) == 0 && node != smem.getTipNode() && NodeTypeEnums.isBetaNodeWithoutSubnetwork(node)) {
                    if (log.isTraceEnabled()) {
                        int offset = getOffset(node);
                        log.trace("{} Skip Node {}", indent(offset), node);
                    }
                    bit = nextNodePosMask(bit); // shift to check the next node
                    node = ((LeftTupleSource) node).getSinkPropagator().getFirstLeftTupleSink();
                    nodeMem = nodeMem.getNext();
                }
            }

            boolean terminalNode = true;
            switch (node.getType()) {
                case NodeTypeEnums.RuleTerminalNode:
                    pRtNode.doNode(activationsManager, executor, (AbstractTerminalNode) node, srcTuples);
                    break;
                case NodeTypeEnums.QueryTerminalNode:
                    pQtNode.doNode(stack, (QueryTerminalNode) node, srcTuples);
                    break;
                case NodeTypeEnums.TupleToObjectNode:
                    doSubnetwork2((TupleToObjectNode) node, srcTuples);
                    break;
                default:
                    terminalNode = false;
            }
            if (terminalNode) {
                break;
            }

            stagedLeftTuples = getTargetStagedLeftTuples(smem, node);
            LeftTupleSinkNode sink = ((LeftTupleSource) node).getSinkPropagator().getFirstLeftTupleSink();

            trgTuples = evalNode(activationsManager, executor, stack, pmem, smems, smem, smemIndex, bit, nodeMem, node, sink, srcTuples, stagedLeftTuples, processSubnetwork);
            if (trgTuples == null) {
                break; // Queries exists and has been placed StackEntry, and there are no current trgTuples to process
            }

            if (node != smem.getTipNode()) {
                // get next node and node memory in the segment
                node = sink;
                nodeMem = nodeMem.getNext();
                bit = nextNodePosMask(bit);
            } else {
                // Reached end of segment, start on new segment.
                smem.getFirst().getStagedLeftTuples().addAll(stagedLeftTuples); // must put back all the LTs
                // end of SegmentMemory, so we know that stagedLeftTuples is not null
                propagate(smem, trgTuples);
                bit = 1;
                smem = smems[++smemIndex];
                trgTuples = smem.getStagedLeftTuples().takeAll();

                if (log.isTraceEnabled()) {
                    int offset = getOffset(node);
                    log.trace("{} Segment {}", indent(offset), smemIndex);
                }
                node = smem.getRootNode();
                nodeMem = smem.getNodeMemories()[0];
            }
            processSubnetwork = true; //  make sure it's reset, so ria nodes are processed
        }

        if (stagedLeftTuples != null && !stagedLeftTuples.isEmpty()) {
            smem.getFirst().getStagedLeftTuples().addAll(stagedLeftTuples); // must put back all the LTs
        }
    }

    private TupleSets evalNode(ActivationsManager activationsManager,
                              RuleExecutor executor,
                              LinkedList<StackEntry> stack,
                              PathMemory pmem,
                              SegmentMemory[] smems,
                              SegmentMemory smem,
                              int smemIndex,
                              long bit,
                              Memory nodeMem,
                              NetworkNode node,
                              LeftTupleSinkNode sink,
                              TupleSets srcTuples,
                              TupleSets stagedLeftTuples,
                              boolean processSubnetwork) {
        TupleSets trgTuples = new TupleSetsImpl();
        if (NodeTypeEnums.isBetaNode(node)) {
            boolean exitInnerEval = evalBetaNode(activationsManager, executor, stack, pmem, smems, smemIndex, nodeMem, node, sink, trgTuples, srcTuples, stagedLeftTuples, processSubnetwork);
            if (exitInnerEval) {
                return null;
            }
        } else {
            boolean exitInnerEval = false;
            switch (node.getType()) {
                case NodeTypeEnums.EvalConditionNode: {
                    pEvalNode.doNode((EvalConditionNode) node, (EvalMemory) nodeMem, sink,
                            srcTuples, trgTuples, stagedLeftTuples);
                    break;

                }
                case NodeTypeEnums.FromNode: {
                    pFromNode.doNode((FromNode) node, (FromMemory) nodeMem, sink,
                            srcTuples, trgTuples, stagedLeftTuples);
                    break;
                }
                case NodeTypeEnums.ReactiveFromNode: {
                    pReactiveFromNode.doNode((ReactiveFromNode) node, (ReactiveFromNode.ReactiveFromMemory) nodeMem, sink,
                                             srcTuples, trgTuples, stagedLeftTuples);
                    break;
                }
                case NodeTypeEnums.QueryElementNode: {
                    exitInnerEval =  evalQueryNode(stack, pmem, smems, smemIndex, bit, nodeMem, node,
                                                   sink, trgTuples, srcTuples, stagedLeftTuples);
                    break;
                }
                case NodeTypeEnums.TimerConditionNode: {
                    pTimerNode.doNode((TimerNode) node, (TimerNodeMemory) nodeMem, pmem, smem, sink, activationsManager, srcTuples, trgTuples, stagedLeftTuples);
                    break;
                }
                case NodeTypeEnums.ConditionalBranchNode: {
                    pBranchNode.doNode(activationsManager, (ConditionalBranchNode) node, (ConditionalBranchMemory) nodeMem,
                            sink, srcTuples, trgTuples, stagedLeftTuples, executor);
                    break;
                }
                case NodeTypeEnums.AsyncSendNode: {
                    pSendNode.doNode((AsyncSendNode) node, (AsyncSendMemory) nodeMem, srcTuples);
                    break;
                }
                case NodeTypeEnums.AsyncReceiveNode: {
                    pReceiveNode.doNode((AsyncReceiveNode) node, (AsyncReceiveMemory) nodeMem, sink, srcTuples, trgTuples);
                    break;
                }
            }
            if (exitInnerEval && trgTuples.isEmpty()) {
                return null;
            }
        }
        return trgTuples;
    }

    private TupleSets getTargetStagedLeftTuples(SegmentMemory smem,
                                                       NetworkNode node) {
        if (node == smem.getTipNode()) {
            // we are about to process the segment tip, allow it to merge insert/update/delete clashes
            if (smem.isEmpty()) {
                segmentMemorySupport.createChildSegments(((LeftTupleSource) node).getSinkPropagator(), smem);
            }
            return smem.getFirst().getStagedLeftTuples().takeAll();
        } else {
            return null;
        }
    }

    private boolean evalQueryNode(LinkedList<StackEntry> stack,
                                  PathMemory pmem,
                                  SegmentMemory[] smems,
                                  int smemIndex,
                                  long bit,
                                  Memory nodeMem,
                                  NetworkNode node,
                                  LeftTupleSinkNode sink,
                                  TupleSets trgTuples,
                                  TupleSets srcTuples,
                                  TupleSets stagedLeftTuples) {
        QueryElementNodeMemory qmem = (QueryElementNodeMemory) nodeMem;

        if (srcTuples.isEmpty() && qmem.getResultLeftTuples().isEmpty()) {
            // no point in evaluating query element, and setting up stack, if there is nothing to process
            return false;
        }

        QueryElementNode qnode = (QueryElementNode) node;

        if (log.isTraceEnabled()) {
            int offset = getOffset(node);
            log.trace("{} query result tuples {}", indent(offset), qmem.getResultLeftTuples().toStringSizes());
        }

        // result tuples can happen when reactivity occurs inside of the query, prior to evaluation
        // we will need special behaviour to add the results again, when this query result resumes
        qmem.getResultLeftTuples().addTo(trgTuples);
        qmem.setNodeCleanWithoutNotify();

        if (srcTuples.isEmpty()) {
            return false;
        } 
        // only process the Query Node if there are src tuples
        StackEntry stackEntry = new StackEntry(node, bit, sink, pmem, nodeMem, smems,
                                               smemIndex, trgTuples, true, true);

        stack.add(stackEntry);

        pQueryNode.doNode(qnode, (QueryElementNodeMemory) nodeMem, stackEntry,
                srcTuples, trgTuples, stagedLeftTuples);

        SegmentMemory qsmem = ((QueryElementNodeMemory) nodeMem).getQuerySegmentMemory();
        List<PathMemory> qpmems = qsmem.getPathMemories();

        // Build the evaluation information for each 'or' branch
        for (int i = 0; i < qpmems.size() ; i++) {
            PathMemory qpmem = qpmems.get(i);

            pmem = qpmem;
            smems = qpmem.getSegmentMemories();
            smemIndex = 0;
            SegmentMemory smem = smems[smemIndex]; // 0

            LeftTupleNode liaNode = qpmem.getPathEndNode().getPathNodes()[0];

            if (liaNode == smem.getTipNode()) {
                // segment only has liaNode in it
                // nothing is staged in the liaNode, so skip to next segment
                smem = smems[++smemIndex]; // 1
                node = smem.getRootNode();
                nodeMem = smem.getNodeMemories()[0];
                bit = 1;
            } else {
                // lia is in shared segment, so point to next node
                node = liaNode.getSinkPropagator().getFirstLeftTupleSink();
                nodeMem = smem.getNodeMemories()[1]; // skip the liaNode memory
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

    }

    private boolean evalBetaNode(ActivationsManager activationsManager,
                                 RuleExecutor executor,
                                 LinkedList<StackEntry> stack,
                                 PathMemory pmem,
                                 SegmentMemory[] smems,
                                 int smemIndex,
                                 Memory nodeMem,
                                 NetworkNode node,
                                 LeftTupleSinkNode sink,
                                 TupleSets trgTuples,
                                 TupleSets srcTuples,
                                 TupleSets stagedLeftTuples,
                                 boolean processSubnetwork) {
        BetaNode betaNode = (BetaNode) node;
        BetaMemory bm;
        AccumulateMemory am = null;
        if (NodeTypeEnums.AccumulateNode == node.getType()) {
            am = (AccumulateMemory) nodeMem;
            bm = am.getBetaMemory();
        } else {
            bm = (BetaMemory) nodeMem;
        }

        if (processSubnetwork && betaNode.getRightInput().inputIsTupleToObjectNode()) {
            // if the subnetwork is nested in this segment, it will create srcTuples containing
            // peer LeftTuples, suitable for the node in the main path.
            doSubnetwork(activationsManager, executor, stack,
                         pmem, smems, smemIndex, nodeMem, srcTuples, bm, betaNode, sink);
            return true; // return here, TupleToObjectNode queues the evaluation on the stack, which is necessary to handled nested query nodes
        }

        switchOnDoBetaNode(bm, am, node, sink, trgTuples, srcTuples, stagedLeftTuples);

        return false;
    }

    private void switchOnDoBetaNode(BetaMemory bm,
                                    AccumulateMemory am,
                                    NetworkNode node,
                                    LeftTupleSinkNode sink,
                                    TupleSets trgTuples,
                                    TupleSets srcTuples,
                                    TupleSets stagedLeftTuples) {
        if (log.isTraceEnabled()) {
            int offset = getOffset(node);
            log.trace("{} rightTuples {}", indent(offset), bm.getStagedRightTuples().toStringSizes());
        }

        switch (node.getType()) {
            case NodeTypeEnums.JoinNode: {
                pJoinNode.doNode((JoinNode) node, sink, bm,
                        srcTuples, trgTuples, stagedLeftTuples);
                break;
            }
            case NodeTypeEnums.NotNode: {
                pNotNode.doNode((NotNode) node, sink, bm,
                        srcTuples, trgTuples, stagedLeftTuples);
                break;
            }
            case NodeTypeEnums.ExistsNode: {
                pExistsNode.doNode((ExistsNode) node, sink, bm,
                        srcTuples, trgTuples, stagedLeftTuples);
                break;
            }
            case NodeTypeEnums.AccumulateNode: {
                AccumulateNode accumulateNode = (AccumulateNode) node;
                if (accumulateNode.getAccumulate().isGroupBy()) {
                    pGroupByNode.doNode(accumulateNode, sink, am, srcTuples, trgTuples, stagedLeftTuples);
                } else {
                    pAccNode.doNode(accumulateNode, sink, am, srcTuples, trgTuples, stagedLeftTuples);
                }
                break;
            }
        }
    }

    private void doSubnetwork(ActivationsManager activationsManager,
                              RuleExecutor executor,
                              LinkedList<StackEntry> stack,
                              PathMemory pmem,
                              SegmentMemory[] smems,
                              int smemIndex,
                              Memory nodeMem,
                              TupleSets srcTuples,
                              BetaMemory bm,
                              BetaNode betaNode,
                              LeftTupleSinkNode sink) {
        SubnetworkPathMemory pathMem         = bm.getSubnetworkPathMemory();
        SegmentMemory[]      subnetworkSmems = pathMem.getSegmentMemories();
        SegmentMemory subSmem = null;
        for (int i = 0; subSmem == null; i++) {
            // segment positions outside of the subnetwork, in the parent chain, are null
            // so we must iterate to find the first non null segment memory
            subSmem =  subnetworkSmems[i];
        }

        // Resume the node after the TupleToObjectNode segment has been processed and the right input memory populated
        StackEntry stackEntry = new StackEntry(betaNode, bm.getNodePosMaskBit(), sink, pmem, nodeMem, smems,
                                               smemIndex, srcTuples, false, false);
        stack.add(stackEntry);
        if (log.isTraceEnabled()) {
            int offset = getOffset(betaNode);
            log.trace("{} SubnetworkQueue {} {}", indent(offset), betaNode.toString(), srcTuples.toStringSizes());
        }


        TupleSets subLts = subSmem.getStagedLeftTuples().takeAll();
        // node is first in the segment, so bit is 1
        innerEval(activationsManager, executor, stack,
                   pathMem,
                   subnetworkSmems, subSmem.getPos(),
                   1, subSmem.getNodeMemories()[0], subSmem.getRootNode(), subLts, true);
    }

    private void doSubnetwork2(TupleToObjectNode tton,
                               TupleSets srcTuples) {

        ObjectSink[] sinks = tton.getObjectSinkPropagator().getSinks();

        BetaNode betaNode = ((RightInputAdapterNode)sinks[0]).getBetaNode();
        BetaMemory bm;
        Memory nodeMem = nodeMemories.getNodeMemory(betaNode);
        if (NodeTypeEnums.AccumulateNode == betaNode.getType()) {
            bm = ((AccumulateMemory) nodeMem).getBetaMemory();
        } else {
            bm = (BetaMemory) nodeMem;
        }
        TupleSets rightTuples = bm.getStagedRightTuples();

        // Build up iteration array for other sinks
        BetaNode[] bns = null;
        BetaMemory[] bms = null;
        int length = sinks.length;
        if (length > 1) {
            bns = new BetaNode[sinks.length - 1];
            bms = new BetaMemory[sinks.length - 1];
            for (int i = 1; i < length; i++) {
                bns[i - 1] = ((RightInputAdapterNode)sinks[i]).getBetaNode();
                Memory nodeMem2 = nodeMemories.getNodeMemory(bns[i - 1]);
                if (NodeTypeEnums.AccumulateNode == betaNode.getType()) {
                    bms[i - 1] = ((AccumulateMemory) nodeMem2).getBetaMemory();
                } else {
                    bms[i - 1] = (BetaMemory) nodeMem2;
                }
            }
        }

        length--; // subtract one, as first is not in the array;
        for (SubnetworkTuple subnetworkTuple = (SubnetworkTuple) srcTuples.getInsertFirst(); subnetworkTuple != null;) {
            SubnetworkTuple next = (SubnetworkTuple) subnetworkTuple.getStagedNext();

            if (bm.getStagedRightTuples().isEmpty()) {
                bm.setNodeDirtyWithoutNotify();
            }

            subnetworkTuple.prepareStagingOnRight();
            rightTuples.addInsert(subnetworkTuple);

            if (bns != null) {
                for (int i = 0; i < length; i++) {
                    if (bms[i].getStagedRightTuples().isEmpty()) {
                        bms[i].setNodeDirtyWithoutNotify();
                    }
                    subnetworkTuple = (SubnetworkTuple) TupleFactory.createPeer(tton,
                                                                                subnetworkTuple);
                    bms[i].getStagedRightTuples().addInsert(subnetworkTuple);
                }
            }
            subnetworkTuple = next;
        }

        for (SubnetworkTuple subnetworkTuple = (SubnetworkTuple) srcTuples.getDeleteFirst(); subnetworkTuple != null;) {
            SubnetworkTuple next = (SubnetworkTuple) subnetworkTuple.getStagedNext();

            if (rightTuples.isEmpty()) {
                bm.setNodeDirtyWithoutNotify();
            }

            switch (subnetworkTuple.getStagedTypeOnRight()) {
                // handle clash with already staged entries
                case Tuple.INSERT:
                    rightTuples.removeInsert(subnetworkTuple.moveStagingFromLeftToRight());
                    break;
                case Tuple.UPDATE:
                    rightTuples.removeUpdate(subnetworkTuple.moveStagingFromLeftToRight());
                    break;
            }

            subnetworkTuple.prepareStagingOnRight();
            rightTuples.addDelete(subnetworkTuple);

            if (bns != null) {
                for (int i = 0; i < length; i++) {
                    subnetworkTuple = (SubnetworkTuple) subnetworkTuple.getPeer();
                    if (bms[i].getStagedRightTuples().isEmpty()) {
                        bms[i].setNodeDirtyWithoutNotify();
                    }
                    bms[i].getStagedRightTuples().addDelete(subnetworkTuple);
                    subnetworkTuple.setStagedOnRight();
                }
            }
            subnetworkTuple = next;
        }

        for (SubnetworkTuple subnetworkTuple = (SubnetworkTuple) srcTuples.getUpdateFirst(); subnetworkTuple != null;) {
            SubnetworkTuple next = (SubnetworkTuple) subnetworkTuple.getStagedNext();

            if (rightTuples.isEmpty()) {
                bm.setNodeDirtyWithoutNotify();
            }

            subnetworkTuple.prepareStagingOnRight();
            rightTuples.addUpdate(subnetworkTuple);

            if (bns != null) {
                for (int i = 0; i < length; i++) {
                    subnetworkTuple = (SubnetworkTuple) subnetworkTuple.getPeer();

                    if (bms[i].getStagedRightTuples().isEmpty()) {
                        bms[i].setNodeDirtyWithoutNotify();
                    }
                    bms[i].getStagedRightTuples().addUpdate(subnetworkTuple);
                    subnetworkTuple.setStagedOnRight();
                }
            }
            subnetworkTuple = next;
        }
        srcTuples.resetAll();
    }
    
    public void propagate(SegmentMemory sourceSegment, TupleSets leftTuples) {
        if (leftTuples.isEmpty()) {
            return;
        }

        LeftTupleSource source = ( LeftTupleSource )  sourceSegment.getTipNode();
        
        if ( sourceSegment.isEmpty() ) {
            segmentMemorySupport.createChildSegments(source.getSinkPropagator(), sourceSegment);
        }
                
        processPeers(sourceSegment, leftTuples);

        Iterator<SegmentMemory> peersIterator = sourceSegment.getPeersWithDataDrivenPathMemoriesIterator();
        while (peersIterator.hasNext()) {
            SegmentMemory smem = peersIterator.next();
            for (PathMemory dataDrivenPmem : smem.getDataDrivenPathMemories()) {
                if (smem.getStagedLeftTuples().getDeleteFirst() == null &&
                    smem.getStagedLeftTuples().getUpdateFirst() == null &&
                    !dataDrivenPmem.isRuleLinked()) {
                    // skip flushing segments that have only inserts staged and the path is not linked
                    continue;
                }
                forceFlushLeftTuple(dataDrivenPmem, smem, smem.getStagedLeftTuples());
                forceFlushWhenSubnetwork(dataDrivenPmem);
            }
        }
    }

    private static void processPeers(SegmentMemory sourceSegment, TupleSets leftTuples) {
        SegmentMemory firstSmem = sourceSegment.getFirst();

        processPeerDeletes( leftTuples.getDeleteFirst(), firstSmem );
        processPeerDeletes( leftTuples.getNormalizedDeleteFirst(), firstSmem );
        processPeerUpdates( leftTuples, firstSmem );
        processPeerInserts( leftTuples, firstSmem );

        firstSmem.getStagedLeftTuples().addAll( leftTuples );
        leftTuples.resetAll();
    }

    private static void processPeerInserts(TupleSets leftTuples, SegmentMemory firstSmem) {
        for (TupleImpl leftTuple = leftTuples.getInsertFirst(); leftTuple != null; leftTuple =  leftTuple.getStagedNext()) {
            SegmentMemory smem = firstSmem.getNext();
            if ( smem != null ) {
                // It's possible for a deleted tuple and set of peers, to be cached on a delete (such as with accumulates).
                // So we should check if the instances already exist and use them if they do.
                if (leftTuple.getPeer() == null) {
                    TupleImpl peer = leftTuple;
                    // peers do not exist, so create and add them.
                    for (; smem != null; smem = smem.getNext()) {
                        LeftTupleSink sink = smem.getSinkFactory();
                        peer = TupleFactory.createPeer(sink, peer); // pctx is set during peer cloning
                        smem.getStagedLeftTuples().addInsert( peer );
                    }
                } else {
                    TupleImpl peer = leftTuple.getPeer();
                    // peers exist, so update them as an insert, which also handles staged clashing.
                    for (; smem != null; smem = smem.getNext()) {
                        peer.setPropagationContext( leftTuple.getPropagationContext() );
                        // ... and update the staged LeftTupleSets according to its current staged state
                        updateChildLeftTupleDuringInsert(peer, smem.getStagedLeftTuples(), smem.getStagedLeftTuples());
                        peer = peer.getPeer();
                    }
                }
            }
        }
    }

    private static void processPeerUpdates(TupleSets leftTuples, SegmentMemory firstSmem) {
        for (TupleImpl leftTuple = leftTuples.getUpdateFirst(); leftTuple != null; leftTuple = leftTuple.getStagedNext()) {
            SegmentMemory smem = firstSmem.getNext();
            if ( smem != null ) {
                for ( TupleImpl peer = leftTuple.getPeer(); peer != null; peer = peer.getPeer() ) {
                    // only stage, if not already staged, if insert, leave as insert
                    if ( peer.getStagedType() == LeftTuple.NONE ) {
                        peer.setPropagationContext( leftTuple.getPropagationContext() );
                        smem.getStagedLeftTuples().addUpdate( peer );
                    }

                    smem = smem.getNext();
                }
            }
        }
    }

    private static void updateChildLeftTupleDuringInsert(TupleImpl childLeftTuple, TupleSets stagedLeftTuples, TupleSets trgLeftTuples) {
        switch ( childLeftTuple.getStagedType() ) {
            // handle clash with already staged entries
            case LeftTuple.INSERT:
                // Was insert before, should continue as insert
                stagedLeftTuples.removeInsert( childLeftTuple );
                trgLeftTuples.addInsert( childLeftTuple );
                break;
            case LeftTuple.UPDATE:
                stagedLeftTuples.removeUpdate( childLeftTuple );
                trgLeftTuples.addUpdate( childLeftTuple );
                break;
            default:
                // no clash, so just add
                if (childLeftTuple.getSink().getType() == AccumulateNode ) {
                    trgLeftTuples.addInsert(childLeftTuple);
                } else {
                    trgLeftTuples.addUpdate(childLeftTuple);
                }
        }
    }

    private static void processPeerDeletes(TupleImpl leftTuple, SegmentMemory firstSmem) {
        for (; leftTuple != null; leftTuple = leftTuple.getStagedNext()) {
            SegmentMemory smem = firstSmem.getNext();
            if ( smem != null ) {
                for ( TupleImpl peer = leftTuple.getPeer(); peer != null; peer = peer.getPeer() ) {
                    peer.setPropagationContext( leftTuple.getPropagationContext() );
                    TupleSets stagedLeftTuples = smem.getStagedLeftTuples();
                    // if the peer is already staged as insert or update the LeftTupleSets will reconcile it internally
                    stagedLeftTuples.addDelete( peer );
                    smem = smem.getNext();
                }
            }
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
        LeftTupleSource lt;
        int offset = 1;
        if (NodeTypeEnums.isTerminalNode(node)) {
            lt = ((TerminalNode) node).getLeftTupleSource();
            offset++;
        } else if (node.getType() == NodeTypeEnums.TupleToObjectNode) {
            lt = ((TupleToObjectNode) node).getLeftTupleSource();
        } else {
            lt = (LeftTupleSource) node;
        }
        while (!NodeTypeEnums.isLeftInputAdapterNode(lt)) {
            offset++;
            lt = lt.getLeftTupleSource();
        }

        return offset;
    }
}
