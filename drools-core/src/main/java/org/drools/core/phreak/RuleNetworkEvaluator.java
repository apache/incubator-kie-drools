package org.drools.core.phreak;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.core.base.DroolsQuery;
import org.drools.core.base.extractors.ArrayElementReader;
import org.drools.core.common.*;
import org.drools.core.util.AbstractBaseLinkedListNode;
import org.drools.core.util.FastIterator;
import org.drools.core.util.LinkedList;
import org.drools.core.util.index.RightTupleList;
import org.drools.core.reteoo.*;
import org.drools.core.reteoo.AccumulateNode.AccumulateContext;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.core.reteoo.ConditionalBranchEvaluator.ConditionalExecution;
import org.drools.core.reteoo.ConditionalBranchNode.ConditionalBranchMemory;
import org.drools.core.reteoo.EvalConditionNode.EvalMemory;
import org.drools.core.reteoo.FromNode.FromMemory;
import org.drools.core.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.core.reteoo.QueryElementNode.QueryElementNodeMemory;
import org.drools.core.reteoo.QueryElementNode.UnificationNodeViewChangedEventListener;
import org.drools.core.rule.Accumulate;
import org.drools.core.rule.ContextEntry;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.EvalCondition;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.DataProvider;
import org.drools.core.spi.PropagationContext;
import org.kie.api.runtime.rule.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.drools.core.util.index.LeftTupleList;

public class RuleNetworkEvaluator {

    private static final Logger log = LoggerFactory.getLogger(RuleNetworkEvaluator.class);

    private static PhreakJoinNode         pJoinNode   = new PhreakJoinNode();
    private static PhreakEvalNode         pEvalNode   = new PhreakEvalNode();
    private static PhreakFromNode         pFromNode   = new PhreakFromNode();
    private static PhreakNotNode          pNotNode    = new PhreakNotNode();
    private static PhreakExistsNode       pExistsNode = new PhreakExistsNode();
    private static PhreakAccumulateNode   pAccNode    = new PhreakAccumulateNode();
    private static PhreakBranchNode       pBranchNode = new PhreakBranchNode();
    private static PhreakQueryNode        pQueryNode  = new PhreakQueryNode();
    private static PhreakRuleTerminalNode pRtNode     = new PhreakRuleTerminalNode();

    private static PhreakQueryTerminalNode pQtNode = new PhreakQueryTerminalNode();

    public RuleNetworkEvaluator() {

    }

    public int evaluateNetwork(PathMemory pmem, InternalWorkingMemory wm, RuleNetworkEvaluatorActivation activation) {
        SegmentMemory[] smems = pmem.getSegmentMemories();

        int smemIndex = 0;
        SegmentMemory smem = smems[smemIndex]; // 0
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) smem.getRootNode();

        NetworkNode node;
        Memory nodeMem;
        if (liaNode == smem.getTipNode()) {
            // segment only has liaNode in it
            // nothing is staged in the liaNode, so skip to next segment           
            smem = smems[++smemIndex]; // 1
            node = smem.getRootNode();
            nodeMem = smem.getNodeMemories().getFirst();
        } else {
            // lia is in shared segment, so point to next node
//            LeftTupleSinkPropagator sink = liaNode.getSinkPropagator();
//            LeftTupleSinkNode firstSink = (LeftTupleSinkNode) sink.getFirstLeftTupleSink();
//            LeftTupleSinkNode secondSink = firstSink.getNextLeftTupleSinkNode();
//            if (sink.size() == 2) {
//                // As we check above for segment splits, if the sink size is 2, it must be a subnetwork.
//                // Always take the non riaNode path
//                node = secondSink;
//            } else {
//                node = firstSink;
//            }
            node = liaNode.getSinkPropagator().getFirstLeftTupleSink();
            nodeMem = smem.getNodeMemories().getFirst().getNext(); // skip the liaNode memory
        }

        LeftTupleSets srcTuples = smem.getStagedLeftTuples();

        if (log.isTraceEnabled()) {
            log.trace("Rule[name={}] segments={} {}", pmem.getRuleTerminalNode().getRule().getName(), smems.length, srcTuples.toStringSizes());
        }

        Set<String> visitedRules;
        if (pmem.getRuleTerminalNode().getType() == NodeTypeEnums.QueryTerminalNode) {
            visitedRules = new HashSet<String>();
        } else {
            visitedRules = Collections.<String>emptySet();
        }

        LinkedList<StackEntry> stack = new LinkedList<StackEntry>();
        eval1(liaNode, pmem, (LeftTupleSink) node, nodeMem, smems, smemIndex, srcTuples, wm, stack, visitedRules, true, activation);

        return 0;
    }

    public static String indent(int size) {
        StringBuilder sbuilder = new StringBuilder();
        for (int i = 0; i < size; i++) {

            sbuilder.append("  ");
        }

        return sbuilder.toString();
    }

    public static int getOffset(NetworkNode node) {
        LeftTupleSource lt = null;
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

    public void eval1(LeftInputAdapterNode liaNode,
                      PathMemory rmem,
                      NetworkNode node,
                      Memory nodeMem,
                      SegmentMemory[] smems,
                      int smemIndex,
                      LeftTupleSets trgTuples,
                      InternalWorkingMemory wm,
                      LinkedList<StackEntry> stack,
                      Set<String> visitedRules,
                      boolean processRian,
                      RuleNetworkEvaluatorActivation activation) {
        while (true) {
            eval2(liaNode, rmem, node, nodeMem, smems, smemIndex, trgTuples, wm, stack, visitedRules, processRian, activation);

            // eval
            if (!stack.isEmpty()) {
                StackEntry entry = stack.removeLast();

                node = entry.getNode();
                nodeMem = entry.getNodeMem();
                trgTuples = entry.getTrgTuples();
                if (node.getType() == NodeTypeEnums.QueryElementNode) {
                    // copy across the results, if any from the query node memory
                    trgTuples.addAll(((QueryElementNodeMemory) nodeMem).getResultLeftTuples());
                }

//                if (!stack.isEmpty() && trgTuples.isEmpty()) {
//                    // The root stack entry must always be fully evaluated, as it may have later tuples
//                    // nested rules are only evaluated if they have tuples . This typically only
//                    // happens for 'or' braches, as results lazy add the parent to the queue
//                    continue;
//                }


                LeftTupleSinkNode sink = entry.getSink();
                rmem = entry.getRmem();

                smems = entry.getSmems();
                smemIndex = entry.getSmemIndex();
                visitedRules = entry.getVisitedRules();
                if (NodeTypeEnums.isBetaNode(node)) {
                    // queued beta nodes do not want their ria node evaluated, otherwise there is recursion
                    processRian = false;
                } else {
                    processRian = true;
                }

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
                    } else {
                        // Reached end of segment, start on new segment.
                        SegmentPropagator.propagate(smem,
                                                    trgTuples,
                                                    wm);
                        smem = smems[++smemIndex];
                        trgTuples = smem.getStagedLeftTuples();
                        node = (LeftTupleSink) smem.getRootNode();
                        nodeMem = smem.getNodeMemories().getFirst();
                    }
                }

                if (log.isTraceEnabled()) {
                    int offset = getOffset(node);
                    log.trace("{} Resume {} {}", indent(offset), node.toString(), trgTuples.toStringSizes());
                }
            } else {
                return; // stack is empty return;
            }
        }
    }

    public void eval2(LeftInputAdapterNode liaNode,
                      PathMemory rmem,
                      NetworkNode node,
                      Memory nodeMem,
                      SegmentMemory[] smems,
                      int smemIndex,
                      LeftTupleSets trgTuples,
                      InternalWorkingMemory wm,
                      LinkedList<StackEntry> stack,
                      Set<String> visitedRules,
                      boolean processRian,
                      RuleNetworkEvaluatorActivation activation) {
        LeftTupleSets srcTuples;
        SegmentMemory smem = smems[smemIndex];
        while (true) {
            srcTuples = trgTuples; // previous target, is now the source
            if (log.isTraceEnabled()) {
                int offset = getOffset(node);
                log.trace("{} {} {}", indent(offset), node.toString(), srcTuples.toStringSizes());
            }

            if (NodeTypeEnums.isTerminalNode(node)) {
                TerminalNode rtn = rmem.getRuleTerminalNode();
                if (node.getType() == NodeTypeEnums.QueryTerminalNode) {
                    pQtNode.doNode((QueryTerminalNode) rtn,
                                   wm,
                                   srcTuples,
                                   stack);
                } else {
                    pRtNode.doNode(rtn,
                                   wm,
                                   srcTuples,
                                   activation);
                }
                return;
            } else if (NodeTypeEnums.RightInputAdaterNode == node.getType()) {
                doRiaNode2(wm, srcTuples, (RightInputAdapterNode) node, stack);
                return;
            }

            LeftTupleSets stagedLeftTuples;
            if (node == smem.getTipNode() && smem.getFirst() != null) {
                // we are about to process the segment tip, allow it to merge insert/update/delete clashes
                // Can happen if the next segments have not yet been initialized
                stagedLeftTuples = smem.getFirst().getStagedLeftTuples();
            } else {
                stagedLeftTuples = null;
            }

            LeftTupleSinkNode sink = ((LeftTupleSource) node).getSinkPropagator().getFirstLeftTupleSink();

            trgTuples = new LeftTupleSets();

            if (NodeTypeEnums.isBetaNode(node)) {
                BetaNode betaNode = (BetaNode) node;

                BetaMemory bm = null;
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
                    doRiaNode( wm, liaNode, rmem, srcTuples,
                              betaNode, sink, smems, smemIndex, nodeMem, bm, stack, visitedRules, activation);
                    return; // return here is doRiaNode queues the evaluation on the stack, which is necessary to handled nested query nodes
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
            } else {
                switch (node.getType()) {
                    case NodeTypeEnums.EvalConditionNode: {
                        pEvalNode.doNode((EvalConditionNode) node, (EvalMemory) nodeMem, sink,
                                         wm, srcTuples, trgTuples, stagedLeftTuples);
                        break;

                    }
                    case NodeTypeEnums.FromNode: {
                        pFromNode.doNode((FromNode) node, (FromMemory) nodeMem, sink,
                                         wm, srcTuples, trgTuples, stagedLeftTuples);
                        break;
                    }
                    case NodeTypeEnums.QueryElementNode: {
                        QueryElementNodeMemory qmem = (QueryElementNodeMemory) nodeMem;

                        if (srcTuples.isEmpty() && qmem.getResultLeftTuples().isEmpty()) {
                            // no point in evaluating query element, and setting up stack, if there is nothing to process
                            break;
                        }

                        QueryElementNode qnode = (QueryElementNode) node;
                        if (visitedRules == Collections.<String>emptySet()) {
                            visitedRules = new HashSet<String>();
                        }
                        visitedRules.add(qnode.getQueryElement().getQueryName());


                        // result tuples can happen when reactivity occurs inside of the query, prior to evaluation
                        // we will need special behaviour to add the results again, when this query result resumes
                        trgTuples.addAll(qmem.getResultLeftTuples());

                        if (!srcTuples.isEmpty()) {
                            // only process the Query Node if there are src tuples
                            StackEntry stackEntry = new StackEntry(liaNode,node, sink, rmem, nodeMem, smems,
                                                                   smemIndex, trgTuples, visitedRules, true);

                            stack.add(stackEntry);

                            pQueryNode.doNode(qnode, (QueryElementNodeMemory) nodeMem, stackEntry, sink,
                                              wm, srcTuples);

                            SegmentMemory qsmem = ((QueryElementNodeMemory) nodeMem).getQuerySegmentMemory();
                            List<PathMemory> qrmems = qsmem.getPathMemories();

                            // Build the evaluation information for each 'or' branch
                            // Exception fo the last, place each entry on the stack, the last one evaluate now.
                            for (int i = qrmems.size() - 1; i >= 0; i--) {
                                PathMemory qrmem = qrmems.get(i);

                                rmem = qrmem;
                                smems = qrmem.getSegmentMemories();
                                smemIndex = 0;
                                smem = smems[smemIndex]; // 0
                                liaNode = (LeftInputAdapterNode) smem.getRootNode();

                                if (liaNode == smem.getTipNode()) {
                                    // segment only has liaNode in it
                                    // nothing is staged in the liaNode, so skip to next segment
                                    smem = smems[++smemIndex]; // 1
                                    node = smem.getRootNode();
                                    nodeMem = smem.getNodeMemories().getFirst();
                                } else {
                                    // lia is in shared segment, so point to next node
                                    //            LeftTupleSinkPropagator sink = liaNode.getSinkPropagator();
                                    //            LeftTupleSinkNode firstSink = (LeftTupleSinkNode) sink.getFirstLeftTupleSink();
                                    //            LeftTupleSinkNode secondSink = firstSink.getNextLeftTupleSinkNode();
                                    //            if (sink.size() == 2) {
                                    //                // As we check above for segment splits, if the sink size is 2, it must be a subnetwork.
                                    //                // Always take the non riaNode path
                                    //                node = secondSink;
                                    //            } else {
                                    //                node = firstSink;
                                    //            }
                                    node = liaNode.getSinkPropagator().getFirstLeftTupleSink();
                                    nodeMem = smem.getNodeMemories().getFirst().getNext(); // skip the liaNode memory
                                }

                                trgTuples = smem.getStagedLeftTuples();

                                if (i != 0 && !trgTuples.isEmpty()) {
                                    // All entries except the last should be placed on the stack for evaluation later.
                                    stackEntry = new StackEntry(liaNode,node, null, rmem,
                                                                nodeMem, smems, smemIndex,
                                                                trgTuples, visitedRules, false);
                                    if (log.isTraceEnabled()) {
                                        int offset = getOffset(stackEntry.getNode());
                                        log.trace("{} ORQueue branch={} {} {}", indent(offset), i, stackEntry.getNode().toString(), trgTuples.toStringSizes());
                                    }
                                    stack.add(stackEntry);
                                }
                            }
                            processRian = true; //  make sure it's reset, so ria nodes are processed
                            continue;
                        }
                        break;
                    }
                    case NodeTypeEnums.ConditionalBranchNode: {
                        pBranchNode.doNode((ConditionalBranchNode) node, (ConditionalBranchMemory) nodeMem, sink,
                                           wm, srcTuples, trgTuples, stagedLeftTuples, activation);
                        break;
                    }
                }
            }

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
            } else {
                // Reached end of segment, start on new segment.
                SegmentPropagator.propagate(smem,
                                            trgTuples,
                                            wm);
                smem = smems[++smemIndex];
                trgTuples = smem.getStagedLeftTuples();
                if (log.isTraceEnabled()) {
                    log.trace("Segment {}", smemIndex);
                }
                node = (LeftTupleSink) smem.getRootNode();
                nodeMem = smem.getNodeMemories().getFirst();
            }
            processRian = true; //  make sure it's reset, so ria nodes are processed
        }
    }

    private void doRiaNode(InternalWorkingMemory wm,
                           LeftInputAdapterNode liaNode,
                           PathMemory rmem,
                           LeftTupleSets srcTuples,
                           BetaNode betaNode,
                           LeftTupleSinkNode sink,
                           SegmentMemory[] smems,
                           int smemIndex,
                           Memory nodeMem,
                           BetaMemory bm,
                           LinkedList<StackEntry> stack,
                           Set<String> visitedRules,
                           RuleNetworkEvaluatorActivation activation) {
        RiaPathMemory pathMem = bm.getRiaRuleMemory();
        SegmentMemory[] subnetworkSmems = pathMem.getSegmentMemories();
        SegmentMemory subSmem = null;
        for ( int i = 0; subSmem == null; i++) {
            // segment positions outside of the subnetwork, in the parent chain, are null
            // so we must iterate to find the first non null segment memory
            subSmem =  subnetworkSmems[i];
        }


//        if (betaNode.getLeftTupleSource().getSinkPropagator().size() == 2) {
//            // sub network is not part of  share split, so need to handle propagation
//            // this ensures the first LeftTuple is actually the subnetwork node
//            // and the main outer network now receives the peer, notice the swap at the end "srcTuples == peerTuples"
//            LeftTupleSets peerTuples = new LeftTupleSets();
//            SegmentPropagator.processPeers(srcTuples, peerTuples, betaNode);
//            // Make sure subnetwork Segment has tuples to process
//            LeftTupleSets subnetworkStaged = subSmem.getStagedLeftTuples();
//            subnetworkStaged.addAll(srcTuples);
//
//            srcTuples.resetAll();
//
//            srcTuples = peerTuples;
//        }

        // Resume the node after the riaNode segment has been processed and the right input memory populated
        StackEntry stackEntry = new StackEntry(liaNode, betaNode, sink, rmem, nodeMem, smems,
                                               smemIndex, srcTuples, visitedRules, false);
        stack.add(stackEntry);
        if (log.isTraceEnabled()) {
            int offset = getOffset(betaNode);
            log.trace("{} RiaQueue {} {}", indent(offset), betaNode.toString(), srcTuples.toStringSizes());
        }

        //        RightInputAdapterNode riaNode = ( RightInputAdapterNode ) betaNode.getRightInput();
        //RiaNodeMemory riaNodeMemory = (RiaNodeMemory) wm.getNodeMemory((MemoryFactory) betaNode.getRightInput());
        //LeftTupleSets riaStagedTuples =
        eval2( liaNode, pathMem, (LeftTupleSink) subSmem.getRootNode(), subSmem.getNodeMemories().getFirst(),
               subnetworkSmems, subSmem.getPos(),
               subSmem.getStagedLeftTuples(), wm, stack, visitedRules, true, activation);
    }

    private void doRiaNode2(InternalWorkingMemory wm,
                            LeftTupleSets srcTuples,
                            RightInputAdapterNode riaNode,
                            LinkedList<StackEntry> stack) {

        ObjectSink[] sinks = riaNode.getSinkPropagator().getSinks();

        BetaNode betaNode = (BetaNode) sinks[0];
        BetaMemory bm;
        Memory nodeMem = wm.getNodeMemory(betaNode);
        if (NodeTypeEnums.AccumulateNode == betaNode.getType()) {
            bm = ((AccumulateMemory) nodeMem).getBetaMemory();
        } else {
            bm = (BetaMemory) nodeMem;
        }


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

            PropagationContext pctx = leftTuple.getPropagationContext();
            InternalFactHandle handle = riaNode.createFactHandle(leftTuple, pctx, wm);

            RightTuple rightTuple = new RightTuple(handle, betaNode);
            leftTuple.setObject(rightTuple);
            rightTuple.setPropagationContext(pctx);
            bm.getStagedRightTuples().addInsert(rightTuple);

            if (bns != null) {
                // Add peered RightTuples, they are attached to FH - unlink LeftTuples that has a peer ref
                for (int i = 0; i < length; i++) {
                    rightTuple = new RightTuple(handle, bns[i]);
                    rightTuple.setPropagationContext(pctx);
                    bms[i].getStagedRightTuples().addInsert(rightTuple);
                }
            }


            leftTuple.clearStaged();
            leftTuple = next;
        }

        for (LeftTuple leftTuple = srcTuples.getDeleteFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            RightTuple rightTuple = (RightTuple) leftTuple.getObject();
            RightTupleSets rightTuples = bm.getStagedRightTuples();
            switch (rightTuple.getStagedType()) {
                case LeftTuple.INSERT: {
                    rightTuples.removeInsert(rightTuple);
                    break;
                }
                case LeftTuple.UPDATE: {
                    rightTuples.removeUpdate(rightTuple);
                    break;
                }
            }
            rightTuples.addDelete(rightTuple);

            if (bns != null) {
                // Add peered RightTuples, they are attached to FH - unlink LeftTuples that has a peer ref
                for (int i = 0; i < length; i++) {
                    rightTuple = rightTuple.getHandleNext();
                    rightTuples = bms[i].getStagedRightTuples();
                    switch (rightTuple.getStagedType()) {
                        case LeftTuple.INSERT: {
                            rightTuples.removeInsert(rightTuple);
                            break;
                        }
                        case LeftTuple.UPDATE: {
                            rightTuples.removeUpdate(rightTuple);
                            break;
                        }
                    }
                    rightTuples.addDelete(rightTuple);
                }
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }

        for (LeftTuple leftTuple = srcTuples.getUpdateFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            RightTuple rightTuple = (RightTuple) leftTuple.getObject();
            RightTupleSets rightTuples = bm.getStagedRightTuples();
            switch (rightTuple.getStagedType()) {
                case LeftTuple.INSERT: {
                    rightTuples.removeInsert(rightTuple);
                    break;
                }
                case LeftTuple.UPDATE: {
                    rightTuples.removeUpdate(rightTuple);
                    break;
                }
            }
            rightTuples.addUpdate(rightTuple);

            if (bns != null) {
                // Add peered RightTuples, they are attached to FH - unlink LeftTuples that has a peer ref
                for (int i = 0; i < length; i++) {
                    rightTuple = rightTuple.getHandleNext();
                    rightTuples = bms[i].getStagedRightTuples();
                    switch (rightTuple.getStagedType()) {
                        case LeftTuple.INSERT: {
                            rightTuples.removeInsert(rightTuple);
                            break;
                        }
                        case LeftTuple.UPDATE: {
                            rightTuples.removeUpdate(rightTuple);
                            break;
                        }
                    }
                    rightTuples.addUpdate(rightTuple);
                }
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }

        srcTuples.resetAll();
    }

    public boolean isRuleNetworkEvaluatorActivation() {
        return true;
    }

    public static class StackEntry extends AbstractBaseLinkedListNode<StackEntry> {
        private LeftInputAdapterNode liaNode;
        private NetworkNode          node;
        private LeftTupleSinkNode    sink;
        private PathMemory           rmem;
        private Memory               nodeMem;
        private SegmentMemory[]      smems;
        private int                  smemIndex;
        private LeftTupleSets        trgTuples;
        private Set<String>          visitedRules;
        private boolean              resumeFromNextNode;


        public StackEntry(LeftInputAdapterNode liaNode,
                          NetworkNode node,
                          LeftTupleSinkNode sink,
                          PathMemory rmem,
                          Memory nodeMem,
                          SegmentMemory[] smems,
                          int smemIndex,
                          LeftTupleSets trgTuples,
                          Set<String> visitedRules,
                          boolean resumeFromNextNode) {
            this.liaNode = liaNode;
            this.node = node;
            this.sink = sink;
            this.rmem = rmem;
            this.nodeMem = nodeMem;
            this.smems = smems;
            this.smemIndex = smemIndex;
            this.trgTuples = trgTuples;
            this.visitedRules = visitedRules;
            this.resumeFromNextNode = resumeFromNextNode;
        }

        public LeftInputAdapterNode getLiaNode() {
            return this.liaNode;
        }

        public NetworkNode getNode() {
            return node;
        }

        public PathMemory getRmem() {
            return rmem;
        }

        public Memory getNodeMem() {
            return nodeMem;
        }

        public SegmentMemory[] getSmems() {
            return smems;
        }

        public int getSmemIndex() {
            return smemIndex;
        }

        public LeftTupleSets getTrgTuples() {
            return trgTuples;
        }

        public LeftTupleSinkNode getSink() {
            return sink;
        }

        public Set<String> getVisitedRules() {
            return visitedRules;
        }

        public boolean isResumeFromNextNode() {
            return resumeFromNextNode;
        }
    }

    public static class PhreakJoinNode {
        public void doNode(JoinNode joinNode,
                           LeftTupleSink sink,
                           BetaMemory bm,
                           InternalWorkingMemory wm,
                           LeftTupleSets srcLeftTuples,
                           LeftTupleSets trgLeftTuples,
                           LeftTupleSets stagedLeftTuples) {

            RightTupleSets srcRightTuples = bm.getStagedRightTuples();

            if (srcRightTuples.getDeleteFirst() != null) {
                doRightDeletes(joinNode, bm, wm, srcRightTuples, trgLeftTuples, stagedLeftTuples);
            }

            if (srcLeftTuples.getDeleteFirst() != null) {
                doLeftDeletes(joinNode, bm, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
            }

            if (srcLeftTuples.getUpdateFirst() != null ) {
                dpUpdatesReorderLeftMemory(bm,
                                           srcLeftTuples);
            }

            if (srcRightTuples.getUpdateFirst() != null) {
                dpUpdatesReorderRightMemory(bm,
                                            srcRightTuples);
            }

            if (srcRightTuples.getUpdateFirst() != null) {
                doRightUpdates(joinNode, sink, bm, wm, srcRightTuples, trgLeftTuples, stagedLeftTuples);
            }

            if (srcLeftTuples.getUpdateFirst() != null) {
                doLeftUpdates(joinNode, sink, bm, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
            }

            if (srcRightTuples.getInsertFirst() != null) {
                doRightInserts(joinNode, sink, bm, wm, srcRightTuples, trgLeftTuples);
            }

            if (srcLeftTuples.getInsertFirst() != null) {
                doLeftInserts(joinNode, sink, bm, wm, srcLeftTuples, trgLeftTuples);
            }

            srcRightTuples.resetAll();
            srcLeftTuples.resetAll();
        }

        public void doLeftInserts(JoinNode joinNode,
                                  LeftTupleSink sink,
                                  BetaMemory bm,
                                  InternalWorkingMemory wm,
                                  LeftTupleSets srcLeftTuples,
                                  LeftTupleSets trgLeftTuples) {
            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            RightTupleMemory rtm = bm.getRightTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = joinNode.getRawConstraints();

            for (LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();

                boolean useLeftMemory = useLeftMemory(joinNode, leftTuple);

                if (useLeftMemory) {
                    ltm.add(leftTuple);
                }

                FastIterator it = joinNode.getRightIterator(rtm);
                PropagationContext context = leftTuple.getPropagationContext();

                constraints.updateFromTuple(contextEntry,
                                            wm,
                                            leftTuple);

                for (RightTuple rightTuple = joinNode.getFirstRightTuple(leftTuple,
                                                                         rtm,
                                                                         null,
                                                                         it); rightTuple != null; rightTuple = (RightTuple) it.next(rightTuple)) {
                    if (constraints.isAllowedCachedLeft(contextEntry,
                                                        rightTuple.getFactHandle())) {
                        trgLeftTuples.addInsert(sink.createLeftTuple(leftTuple,
                                                                     rightTuple,
                                                                     null,
                                                                     null,
                                                                     sink,
                                                                     useLeftMemory));
                    }

                }
                leftTuple.clearStaged();
                leftTuple = next;
            }
            constraints.resetTuple(contextEntry);
        }

        public void doRightInserts(JoinNode joinNode,
                                   LeftTupleSink sink,
                                   BetaMemory bm,
                                   InternalWorkingMemory wm,
                                   RightTupleSets srcRightTuples,
                                   LeftTupleSets trgLeftTuples) {
            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            RightTupleMemory rtm = bm.getRightTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = joinNode.getRawConstraints();

            for (RightTuple rightTuple = srcRightTuples.getInsertFirst(); rightTuple != null; ) {
                RightTuple next = rightTuple.getStagedNext();

                rtm.add(rightTuple);

                FastIterator it = joinNode.getLeftIterator(ltm);
                PropagationContext context = rightTuple.getPropagationContext();

                constraints.updateFromFactHandle(contextEntry,
                                                 wm,
                                                 rightTuple.getFactHandle());

                for (LeftTuple leftTuple = joinNode.getFirstLeftTuple(rightTuple, ltm, context, it); leftTuple != null; leftTuple = (LeftTuple) it.next(leftTuple)) {
                    if (leftTuple.getStagedType() == LeftTuple.UPDATE) {
                        // ignore, as it will get processed via left iteration. Children cannot be processed twice
                        continue;
                    }

                    if (constraints.isAllowedCachedRight(contextEntry,
                                                         leftTuple)) {
                        trgLeftTuples.addInsert(sink.createLeftTuple(leftTuple,
                                                                     rightTuple,
                                                                     null,
                                                                     null,
                                                                     sink,
                                                                     true));
                    }
                }
                rightTuple.clearStaged();
                rightTuple = next;
            }
            constraints.resetFactHandle(contextEntry);
        }

        public void doLeftUpdates(JoinNode joinNode,
                                  LeftTupleSink sink,
                                  BetaMemory bm,
                                  InternalWorkingMemory wm,
                                  LeftTupleSets srcLeftTuples,
                                  LeftTupleSets trgLeftTuples,
                                  LeftTupleSets stagedLeftTuples) {
            RightTupleMemory rtm = bm.getRightTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = joinNode.getRawConstraints();

            for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();

                PropagationContext context = leftTuple.getPropagationContext();

                constraints.updateFromTuple(contextEntry,
                                            wm,
                                            leftTuple);

                FastIterator it = joinNode.getRightIterator(rtm);
                RightTuple rightTuple = joinNode.getFirstRightTuple(leftTuple,
                                                                    rtm,
                                                                    null,
                                                                    it);

                LeftTuple childLeftTuple = leftTuple.getFirstChild();

                // first check our index (for indexed nodes only) hasn't changed and we are returning the same bucket
                // if rightTuple is null, we assume there was a bucket change and that bucket is empty        
                if (childLeftTuple != null && rtm.isIndexed() && !it.isFullIterator() && (rightTuple == null || (rightTuple.getMemory() != childLeftTuple.getRightParent().getMemory()))) {
                    // our index has changed, so delete all the previous propagations
                    while (childLeftTuple != null) {
                        childLeftTuple = deleteLeftChild(childLeftTuple, trgLeftTuples, stagedLeftTuples);
                    }
                    // childLeftTuple is now null, so the next check will attempt matches for new bucket
                }

                // we can't do anything if RightTupleMemory is empty
                if (rightTuple != null) {
                    doLeftUpdatesProcessChildren(childLeftTuple, leftTuple, rightTuple, stagedLeftTuples, contextEntry, constraints, sink, it, trgLeftTuples);
                }
                leftTuple.clearStaged();
                leftTuple = next;
            }
            constraints.resetTuple(contextEntry);
        }

        public LeftTuple doLeftUpdatesProcessChildren(LeftTuple childLeftTuple,
                                                      LeftTuple leftTuple,
                                                      RightTuple rightTuple,
                                                      LeftTupleSets stagedLeftTuples,
                                                      ContextEntry[] contextEntry,
                                                      BetaConstraints constraints,
                                                      LeftTupleSink sink,
                                                      FastIterator it,
                                                      LeftTupleSets trgLeftTuples) {
            if (childLeftTuple == null) {
                // either we are indexed and changed buckets or
                // we had no children before, but there is a bucket to potentially match, so try as normal assert
                for (; rightTuple != null; rightTuple = (RightTuple) it.next(rightTuple)) {
                    if (constraints.isAllowedCachedLeft(contextEntry,
                                                        rightTuple.getFactHandle())) {
                        trgLeftTuples.addInsert(sink.createLeftTuple(leftTuple,
                                                                     rightTuple,
                                                                     null,
                                                                     null,
                                                                     sink,
                                                                     true));
                    }
                }
            } else {
                // in the same bucket, so iterate and compare
                for (; rightTuple != null; rightTuple = (RightTuple) it.next(rightTuple)) {
                    if (constraints.isAllowedCachedLeft(contextEntry,
                                                        rightTuple.getFactHandle())) {
                        // insert, childLeftTuple is not updated
                        if (childLeftTuple == null || childLeftTuple.getRightParent() != rightTuple) {
                            trgLeftTuples.addInsert(sink.createLeftTuple(leftTuple,
                                                                         rightTuple,
                                                                         null,
                                                                         null,
                                                                         sink,
                                                                         true));
                        } else {
                            switch (childLeftTuple.getStagedType()) {
                                // handle clash with already staged entries
                                case LeftTuple.INSERT:
                                    stagedLeftTuples.removeInsert(childLeftTuple);
                                    break;
                                case LeftTuple.UPDATE:
                                    stagedLeftTuples.removeUpdate(childLeftTuple);
                                    break;
                            }

                            // update, childLeftTuple is updated
                            trgLeftTuples.addUpdate(childLeftTuple);

                            childLeftTuple.reAddRight();
                            childLeftTuple = childLeftTuple.getLeftParentNext();
                        }
                    } else if (childLeftTuple != null && childLeftTuple.getRightParent() == rightTuple) {
                        // delete, childLeftTuple is updated
                        childLeftTuple = deleteLeftChild(childLeftTuple, trgLeftTuples, stagedLeftTuples);
                    }
                }
            }

            return childLeftTuple;
        }

        public void doRightUpdates(JoinNode joinNode,
                                   LeftTupleSink sink,
                                   BetaMemory bm,
                                   InternalWorkingMemory wm,
                                   RightTupleSets srcRightTuples,
                                   LeftTupleSets trgLeftTuples,
                                   LeftTupleSets stagedLeftTuples) {
            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = joinNode.getRawConstraints();

            for (RightTuple rightTuple = srcRightTuples.getUpdateFirst(); rightTuple != null; ) {
                RightTuple next = rightTuple.getStagedNext();

                PropagationContext context = rightTuple.getPropagationContext();

                LeftTuple childLeftTuple = rightTuple.getFirstChild();

                FastIterator it = joinNode.getLeftIterator(ltm);
                LeftTuple leftTuple = joinNode.getFirstLeftTuple(rightTuple, ltm, context, it);

                constraints.updateFromFactHandle(contextEntry,
                                                 wm,
                                                 rightTuple.getFactHandle());

                // first check our index (for indexed nodes only) hasn't changed and we are returning the same bucket
                // We assume a bucket change if leftTuple == null        
                if (childLeftTuple != null && ltm.isIndexed() && !it.isFullIterator() && (leftTuple == null || (leftTuple.getMemory() != childLeftTuple.getLeftParent().getMemory()))) {
                    // our index has changed, so delete all the previous propagations
                    while (childLeftTuple != null) {
                        childLeftTuple = deleteRightChild(childLeftTuple, trgLeftTuples, stagedLeftTuples);
                    }
                    // childLeftTuple is now null, so the next check will attempt matches for new bucket                    
                }

                // we can't do anything if LeftTupleMemory is empty
                if (leftTuple != null) {
                    doRightUpdatesProcessChildren(childLeftTuple, leftTuple, rightTuple, stagedLeftTuples, contextEntry, constraints, sink, it, trgLeftTuples);
                }
                rightTuple.clearStaged();
                rightTuple = next;
            }
            constraints.resetFactHandle(contextEntry);
        }

        public LeftTuple doRightUpdatesProcessChildren(LeftTuple childLeftTuple,
                                                       LeftTuple leftTuple,
                                                       RightTuple rightTuple,
                                                       LeftTupleSets stagedLeftTuples,
                                                       ContextEntry[] contextEntry,
                                                       BetaConstraints constraints,
                                                       LeftTupleSink sink,
                                                       FastIterator it,
                                                       LeftTupleSets trgLeftTuples) {
            if (childLeftTuple == null) {
                // either we are indexed and changed buckets or
                // we had no children before, but there is a bucket to potentially match, so try as normal assert
                for (; leftTuple != null; leftTuple = (LeftTuple) it.next(leftTuple)) {
                    if (leftTuple.getStagedType() == LeftTuple.UPDATE) {
                        // ignore, as it will get processed via left iteration. Children cannot be processed twice
                        continue;
                    }

                    if (constraints.isAllowedCachedRight(contextEntry,
                                                         leftTuple)) {
                        trgLeftTuples.addInsert(sink.createLeftTuple(leftTuple,
                                                                     rightTuple,
                                                                     null,
                                                                     null,
                                                                     sink,
                                                                     true));
                    }
                }
            } else {
                // in the same bucket, so iterate and compare
                for (; leftTuple != null; leftTuple = (LeftTuple) it.next(leftTuple)) {
                    if (leftTuple.getStagedType() == LeftTuple.UPDATE) {
                        // ignore, as it will get processed via left iteration. Children cannot be processed twice
                        continue;
                    }

                    if (constraints.isAllowedCachedRight(contextEntry,
                                                         leftTuple)) {
                        // insert, childLeftTuple is not updated
                        if (childLeftTuple == null || childLeftTuple.getLeftParent() != leftTuple) {
                            trgLeftTuples.addInsert(sink.createLeftTuple(leftTuple,
                                                                         rightTuple,
                                                                         null,
                                                                         null,
                                                                         sink,
                                                                         true));
                        } else {
                            switch (childLeftTuple.getStagedType()) {
                                // handle clash with already staged entries
                                case LeftTuple.INSERT:
                                    stagedLeftTuples.removeInsert(childLeftTuple);
                                    break;
                                case LeftTuple.UPDATE:
                                    stagedLeftTuples.removeUpdate(childLeftTuple);
                                    break;
                            }

                            // update, childLeftTuple is updated
                            trgLeftTuples.addUpdate(childLeftTuple);

                            childLeftTuple.reAddLeft();
                            childLeftTuple = childLeftTuple.getRightParentNext();
                        }
                    } else if (childLeftTuple != null && childLeftTuple.getLeftParent() == leftTuple) {
                        // delete, childLeftTuple is updated
                        childLeftTuple = deleteRightChild(childLeftTuple, trgLeftTuples, stagedLeftTuples);
                    }
                }
            }

            return childLeftTuple;
        }

        public void doLeftDeletes(JoinNode joinNode,
                                  BetaMemory bm,
                                  InternalWorkingMemory wm,
                                  LeftTupleSets srcLeftTuples,
                                  LeftTupleSets trgLeftTuples,
                                  LeftTupleSets stagedLeftTuples) {
            LeftTupleMemory ltm = bm.getLeftTupleMemory();

            for (LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();
                if (leftTuple.getMemory() != null) {
                    // it may have been staged and never actually added
                    ltm.remove(leftTuple);
                }

                if (leftTuple.getFirstChild() != null) {
                    LeftTuple childLeftTuple = leftTuple.getFirstChild();

                    while (childLeftTuple != null) {
                        childLeftTuple = deleteLeftChild(childLeftTuple, trgLeftTuples, stagedLeftTuples);
                    }
                }
                leftTuple.clearStaged();
                leftTuple = next;
            }
        }

        public void doRightDeletes(JoinNode joinNode,
                                   BetaMemory bm,
                                   InternalWorkingMemory wm,
                                   RightTupleSets srcRightTuples,
                                   LeftTupleSets trgLeftTuples,
                                   LeftTupleSets stagedLeftTuples) {
            RightTupleMemory rtm = bm.getRightTupleMemory();

            for (RightTuple rightTuple = srcRightTuples.getDeleteFirst(); rightTuple != null; ) {
                RightTuple next = rightTuple.getStagedNext();
                if (rightTuple.getMemory() != null) {
                    // it may have been staged and never actually added
                    rtm.remove(rightTuple);
                }
                ;

                if (rightTuple.getFirstChild() != null) {
                    LeftTuple childLeftTuple = rightTuple.getFirstChild();

                    while (childLeftTuple != null) {
                        childLeftTuple = deleteRightChild(childLeftTuple, trgLeftTuples, stagedLeftTuples);
                    }
                }
                rightTuple.clearStaged();
                rightTuple = next;
            }
        }
    }

    public static class PhreakNotNode {
        public void doNode(NotNode notNode,
                           LeftTupleSink sink,
                           BetaMemory bm,
                           InternalWorkingMemory wm,
                           LeftTupleSets srcLeftTuples,
                           LeftTupleSets trgLeftTuples,
                           LeftTupleSets stagedLeftTuples) {
            RightTupleSets srcRightTuples = bm.getStagedRightTuples();

            if (srcLeftTuples.getDeleteFirst() != null) {
                // left deletes must come before right deletes. Otherwise right deletes could
                // stage an insertion, that is later deleted in the rightDelete, causing potential problems
                doLeftDeletes(notNode, sink, bm, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
            }


            if (srcLeftTuples.getUpdateFirst() != null) {
                // must happen before right inserts, so it can find left tuples to block.
                dpUpdatesReorderLeftMemory(bm,
                                           srcLeftTuples);
            }

            if ( srcRightTuples.getUpdateFirst() != null) {
                dpUpdatesExistentialReorderRightMemory(bm,
                                                       notNode,
                                                       srcRightTuples); // this also preserves the next rightTuple
            }

            if (srcRightTuples.getInsertFirst() != null) {
                // must come before right updates and inserts, as they might cause insert propagation, while this causes delete propagations, resulting in staging clash.
                doRightInserts(notNode, sink, bm, wm, srcRightTuples, trgLeftTuples);
            }



            if (srcRightTuples.getUpdateFirst() != null) {
                // must come after rightInserts and before rightDeletes, to avoid staging clash
                doRightUpdates(notNode, sink, bm, wm, srcRightTuples, trgLeftTuples, stagedLeftTuples);
            }

            if (srcRightTuples.getDeleteFirst() != null) {
                // must come after rightUpdates, to avoid staging clash
                doRightDeletes(notNode, sink, bm, wm, srcRightTuples, trgLeftTuples);
            }

            if (srcLeftTuples.getUpdateFirst() != null) {
                doLeftUpdates(notNode, sink, bm, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
            }

            if (srcLeftTuples.getInsertFirst() != null) {
                doLeftInserts(notNode, sink, bm, wm, srcLeftTuples, trgLeftTuples);
            }

            srcRightTuples.resetAll();
            srcLeftTuples.resetAll();
        }

        public void doLeftInserts(NotNode notNode,
                                  LeftTupleSink sink,
                                  BetaMemory bm,
                                  InternalWorkingMemory wm,
                                  LeftTupleSets srcLeftTuples,
                                  LeftTupleSets trgLeftTuples) {
            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            RightTupleMemory rtm = bm.getRightTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = notNode.getRawConstraints();

            for (LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();

                FastIterator it = notNode.getRightIterator(rtm);
                PropagationContext context = leftTuple.getPropagationContext();

                boolean useLeftMemory = useLeftMemory(notNode, leftTuple);

                constraints.updateFromTuple(contextEntry,
                                            wm,
                                            leftTuple);

                // This method will also remove rightTuples that are from subnetwork where no leftmemory use used
                findLeftTupleBlocker(notNode, rtm, contextEntry, constraints, leftTuple, it, context, useLeftMemory);

                if (leftTuple.getBlocker() == null) {
                    // tuple is not blocked, so add to memory so other fact handles can attempt to match
                    if (useLeftMemory) {
                        ltm.add(leftTuple);
                    }

                    trgLeftTuples.addInsert(sink.createLeftTuple(leftTuple,
                                                                 sink,
                                                                 leftTuple.getPropagationContext(), useLeftMemory)); // use leftTuple pctx here, as no right input caused the trigger anway
                }
                leftTuple.clearStaged();
                leftTuple = next;
            }
            constraints.resetTuple(contextEntry);
        }

        public void doRightInserts(NotNode notNode,
                                   LeftTupleSink sink,
                                   BetaMemory bm,
                                   InternalWorkingMemory wm,
                                   RightTupleSets srcRightTuples,
                                   LeftTupleSets trgLeftTuples) {

            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            RightTupleMemory rtm = bm.getRightTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = notNode.getRawConstraints();

            LeftTupleSets stagedLeftTuples = null;
            if (!bm.getSegmentMemory().isEmpty()) {
                stagedLeftTuples = bm.getSegmentMemory().getFirst().getStagedLeftTuples();
            }

            // this must be processed here, rather than initial insert, as we need to link the blocker
            unlinkNotNodeOnRightInsert(notNode,
                                       bm,
                                       wm);

            for (RightTuple rightTuple = srcRightTuples.getInsertFirst(); rightTuple != null; ) {
                RightTuple next = rightTuple.getStagedNext();

                rtm.add(rightTuple);

                FastIterator it = notNode.getLeftIterator(ltm);
                PropagationContext context = rightTuple.getPropagationContext();

                constraints.updateFromFactHandle(contextEntry,
                                                 wm,
                                                 rightTuple.getFactHandle());
                for (LeftTuple leftTuple = notNode.getFirstLeftTuple(rightTuple, ltm, context, it); leftTuple != null; ) {
                    // preserve next now, in case we remove this leftTuple 
                    LeftTuple temp = (LeftTuple) it.next(leftTuple);

                    if (leftTuple.getStagedType() == LeftTuple.UPDATE) {
                        // ignore, as it will get processed via left iteration. Children cannot be processed twice
                        leftTuple = temp;
                        continue;
                    }

                    // we know that only unblocked LeftTuples are  still in the memory
                    if (constraints.isAllowedCachedRight(contextEntry,
                                                         leftTuple)) {
                        leftTuple.setBlocker(rightTuple);
                        rightTuple.addBlocked(leftTuple);

                        // this is now blocked so remove from memory
                        ltm.remove(leftTuple);

                        // subclasses like ForallNotNode might override this propagation
                        // ** @TODO (mdp) need to not break forall
                        LeftTuple childLeftTuple = leftTuple.getFirstChild();

                        if (childLeftTuple != null) { // NotNode only has one child
                            childLeftTuple.setPropagationContext(rightTuple.getPropagationContext());
                            childLeftTuple = deleteLeftChild(childLeftTuple, trgLeftTuples, stagedLeftTuples);
                        }
                    }

                    leftTuple = temp;
                }
                rightTuple.clearStaged();
                rightTuple = next;
            }
            constraints.resetFactHandle(contextEntry);
        }

        public static void unlinkNotNodeOnRightInsert(NotNode notNode,
                                                      BetaMemory bm,
                                                      InternalWorkingMemory wm) {
            if (bm.getSegmentMemory().isSegmentLinked() && !notNode.isRightInputIsRiaNode() && notNode.isEmptyBetaConstraints()) {
                // this must be processed here, rather than initial insert, as we need to link the blocker
                // @TODO this could be more efficient, as it means the entire StagedLeftTuples for all previous nodes where evaluated, needlessly.
                bm.unlinkNode(wm);
            }
        }

        public void doLeftUpdates(NotNode notNode,
                                  LeftTupleSink sink,
                                  BetaMemory bm,
                                  InternalWorkingMemory wm,
                                  LeftTupleSets srcLeftTuples,
                                  LeftTupleSets trgLeftTuples,
                                  LeftTupleSets stagedLeftTuples) {
            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            RightTupleMemory rtm = bm.getRightTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = notNode.getRawConstraints();

            for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();

                PropagationContext context = leftTuple.getPropagationContext();

                FastIterator rightIt = notNode.getRightIterator(rtm);
                RightTuple firstRightTuple = notNode.getFirstRightTuple(leftTuple, rtm, null, rightIt);

                // If in memory, remove it, because we'll need to add it anyway if it's not blocked, to ensure iteration order
                RightTuple blocker = leftTuple.getBlocker();
                if (blocker == null) {
                    ltm.remove(leftTuple);
                } else {
                    // check if we changed bucket
                    if (rtm.isIndexed() && !rightIt.isFullIterator()) {
                        // if newRightTuple is null, we assume there was a bucket change and that bucket is empty                
                        if (firstRightTuple == null || firstRightTuple.getMemory() != blocker.getMemory()) {
                            blocker.removeBlocked(leftTuple);
                            blocker = null;
                        }
                    }
                }

                constraints.updateFromTuple(contextEntry,
                                            wm,
                                            leftTuple);

                // if we where not blocked before (or changed buckets), or the previous blocker no longer blocks, then find the next blocker
                if (blocker == null || !constraints.isAllowedCachedLeft(contextEntry,
                                                                        blocker.getFactHandle())) {
                    if (blocker != null) {
                        // remove previous blocker if it exists, as we know it doesn't block any more
                        blocker.removeBlocked(leftTuple);
                    }

                    // find first blocker, because it's a modify, we need to start from the beginning again        
                    for (RightTuple newBlocker = firstRightTuple; newBlocker != null; newBlocker = (RightTuple) rightIt.next(newBlocker)) {
                        if (constraints.isAllowedCachedLeft(contextEntry,
                                                            newBlocker.getFactHandle())) {
                            leftTuple.setBlocker(newBlocker);
                            newBlocker.addBlocked(leftTuple);

                            break;
                        }
                    }

                    LeftTuple childLeftTuple = leftTuple.getFirstChild();

                    if (leftTuple.getBlocker() != null) {
                        // blocked
                        if (childLeftTuple != null) {
                            // blocked, with previous children, so must have not been previously blocked, so retract
                            // no need to remove, as we removed at the start
                            // to be matched against, as it's now blocked
                            childLeftTuple.setPropagationContext(leftTuple.getBlocker().getPropagationContext()); // we have the righttuple, so use it for the pctx
                            deleteLeftChild(childLeftTuple, trgLeftTuples, stagedLeftTuples);
                        } // else: it's blocked now and no children so blocked before, thus do nothing             
                    } else if (childLeftTuple == null) {
                        // not blocked, with no children, must have been previously blocked so assert
                        ltm.add(leftTuple); // add to memory so other fact handles can attempt to match
                        trgLeftTuples.addInsert(sink.createLeftTuple(leftTuple,
                                                                     sink,
                                                                     leftTuple.getPropagationContext(), true)); // use leftTuple for the pctx here, as the right one is not available
                                                                                                                // this won't cause a problem, as the trigger tuple (to the left) will be more recent anwyay
                    } else {
                        switch (childLeftTuple.getStagedType()) {
                            // handle clash with already staged entries
                            case LeftTuple.INSERT:
                                stagedLeftTuples.removeInsert(childLeftTuple);
                                break;
                            case LeftTuple.UPDATE:
                                stagedLeftTuples.removeUpdate(childLeftTuple);
                                break;
                        }
                        // not blocked, with children, so wasn't previous blocked and still isn't so modify                
                        ltm.add(leftTuple); // add to memory so other fact handles can attempt to match
                        trgLeftTuples.addUpdate(childLeftTuple); // no need to update pctx, as no right available, and pctx will exist on a parent LeftTuple anyway
                        childLeftTuple.reAddLeft();
                    }
                }
                leftTuple.clearStaged();
                leftTuple = next;
            }
            constraints.resetTuple(contextEntry);
        }


        public void doRightUpdates(NotNode notNode,
                                   LeftTupleSink sink,
                                   BetaMemory bm,
                                   InternalWorkingMemory wm,
                                   RightTupleSets srcRightTuples,
                                   LeftTupleSets trgLeftTuples,
                                   LeftTupleSets stagedLeftTuples) {
            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            RightTupleMemory rtm = bm.getRightTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = notNode.getRawConstraints();

            boolean resumeFromCurrent =  !(notNode.isIndexedUnificationJoin() || rtm.getIndexType().isComparison());

            for (RightTuple rightTuple = srcRightTuples.getUpdateFirst(); rightTuple != null; ) {
                RightTuple next = rightTuple.getStagedNext();
                if (ltm == null || (ltm.size() == 0 && rightTuple.getBlocked() == null)) {
                    // do nothing here, as we know there are no left tuples

                    //normally do this at the end, but as we are exiting early, make sure the buckets are still correct.
                    rtm.removeAdd(rightTuple);
                    rightTuple.clearStaged();
                    rightTuple = next;
                    continue;
                }

                PropagationContext context = rightTuple.getPropagationContext();

                constraints.updateFromFactHandle(contextEntry,
                                                 wm,
                                                 rightTuple.getFactHandle());

                FastIterator leftIt = notNode.getLeftIterator(ltm);
                LeftTuple firstLeftTuple = notNode.getFirstLeftTuple(rightTuple, ltm, context, leftIt);

                LeftTuple firstBlocked = rightTuple.getBlocked();
                // we now have  reference to the first Blocked, so null it in the rightTuple itself, so we can rebuild
                rightTuple.nullBlocked();

                // first process non-blocked tuples, as we know only those ones are in the left memory.
                for (LeftTuple leftTuple = firstLeftTuple; leftTuple != null; ) {
                    // preserve next now, in case we remove this leftTuple 
                    LeftTuple temp = (LeftTuple) leftIt.next(leftTuple);

                    if (leftTuple.getStagedType() == LeftTuple.UPDATE) {
                        // ignore, as it will get processed via left iteration. Children cannot be processed twice
                        leftTuple = temp;
                        continue;
                    }

                    // we know that only unblocked LeftTuples are  still in the memory
                    if (constraints.isAllowedCachedRight(contextEntry,
                                                         leftTuple)) {
                        leftTuple.setBlocker(rightTuple);
                        rightTuple.addBlocked(leftTuple);

                        // this is now blocked so remove from memory
                        ltm.remove(leftTuple);

                        LeftTuple childLeftTuple = leftTuple.getFirstChild();
                        if ( childLeftTuple != null) {
                            childLeftTuple.setPropagationContext(rightTuple.getPropagationContext());
                            deleteRightChild(childLeftTuple, trgLeftTuples, stagedLeftTuples);
                        }
                    }

                    leftTuple = temp;
                }

                if (firstBlocked != null) {
                    RightTuple rootBlocker = rightTuple.getTempNextRightTuple();
                    if (rootBlocker != null ) {
                        if ( rootBlocker != rightTuple ) {
                            rootBlocker = ( RightTuple ) rootBlocker.getNext();
                        }
                    } else{
                        resumeFromCurrent = false;
                    }


                    FastIterator rightIt = notNode.getRightIterator(rtm);

                    // iterate all the existing previous blocked LeftTuples
                    for (LeftTuple leftTuple = firstBlocked; leftTuple != null; ) {
                        LeftTuple temp = leftTuple.getBlockedNext();

                        leftTuple.clearBlocker();

                        if (leftTuple.getStagedType() == LeftTuple.UPDATE) {
                            // ignore, as it will get processed via left iteration. Children cannot be processed twice
                            // but need to add it back into list first
                            leftTuple.setBlocker(rightTuple);
                            rightTuple.addBlocked(leftTuple);

                            leftTuple = temp;
                            continue;
                        }

                        constraints.updateFromTuple(contextEntry,
                                                    wm,
                                                    leftTuple);

                        if (!resumeFromCurrent) {
                            rootBlocker = notNode.getFirstRightTuple(leftTuple, rtm, null, rightIt);
                        }

                        // we know that older tuples have been checked so continue next
                        for (RightTuple newBlocker = rootBlocker; newBlocker != null; newBlocker = (RightTuple) rightIt.next(newBlocker)) {
                            // cannot select a RightTuple queued in the delete list
                            // There may be UPDATE RightTuples too, but that's ok. They've already been re-added to the correct bucket, safe to be reprocessed.
                            if (leftTuple.getStagedType() != LeftTuple.DELETE && constraints.isAllowedCachedLeft(contextEntry,
                                                                newBlocker.getFactHandle())) {
                                leftTuple.setBlocker(newBlocker);
                                newBlocker.addBlocked(leftTuple);

                                break;
                            }
                        }

                        if (leftTuple.getBlocker() == null) {
                            // was previous blocked and not in memory, so add
                            ltm.add(leftTuple);

                            // subclasses like ForallNotNode might override this propagation
                            trgLeftTuples.addInsert(sink.createLeftTuple(leftTuple,
                                                                         sink,
                                                                         rightTuple.getPropagationContext(), true));
                        }

                        leftTuple = temp;
                    }
                } else {
                    // we had to do this at the end, rather than beginning as this 'if' block needs the next memory tuple
                    rtm.removeAdd(rightTuple);
                }
                rightTuple.clearStaged();
                rightTuple = next;
            }

            constraints.resetFactHandle(contextEntry);
            constraints.resetTuple(contextEntry);
        }

        public void doLeftDeletes(NotNode notNode,
                                  LeftTupleSink sink,
                                  BetaMemory bm,
                                  InternalWorkingMemory wm,
                                  LeftTupleSets srcLeftTuples,
                                  LeftTupleSets trgLeftTuples,
                                  LeftTupleSets stagedLeftTuples) {
            LeftTupleMemory ltm = bm.getLeftTupleMemory();

            for (LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();
                RightTuple blocker = leftTuple.getBlocker();
                if (blocker == null) {
                    if (leftTuple.getMemory() != null) {
                        // it may have been staged and never actually added
                        ltm.remove(leftTuple);
                    }

                    LeftTuple childLeftTuple = leftTuple.getFirstChild();

                    if (childLeftTuple != null) { // NotNode only has one child
                        deleteLeftChild(childLeftTuple, trgLeftTuples, stagedLeftTuples); // no need to update pctx, as no right available, and pctx will exist on a parent LeftTuple anyway
                    }
                } else {
                    blocker.removeBlocked(leftTuple);
                }
                leftTuple.clearStaged();
                leftTuple = next;
            }
        }

        public void doRightDeletes(NotNode notNode,
                                   LeftTupleSink sink,
                                   BetaMemory bm,
                                   InternalWorkingMemory wm,
                                   RightTupleSets srcRightTuples,
                                   LeftTupleSets trgLeftTuples) {
            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            RightTupleMemory rtm = bm.getRightTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = notNode.getRawConstraints();

            for (RightTuple rightTuple = srcRightTuples.getDeleteFirst(); rightTuple != null; ) {
                RightTuple next = rightTuple.getStagedNext();

                FastIterator it = notNode.getRightIterator(rtm);

                // assign now, so we can remove from memory before doing any possible propagations
                boolean useComparisonIndex = rtm.getIndexType().isComparison();
                RightTuple rootBlocker = useComparisonIndex ? null : (RightTuple) it.next(rightTuple);

                if (rightTuple.getMemory() != null) {
                    // it may have been staged and never actually added
                    rtm.remove(rightTuple);
                }

                if (rightTuple.getBlocked() != null) {
                    PropagationContext context = rightTuple.getPropagationContext();

                    for (LeftTuple leftTuple = rightTuple.getBlocked(); leftTuple != null; ) {
                        LeftTuple temp = leftTuple.getBlockedNext();

                        leftTuple.clearBlocker();

                        if (leftTuple.getStagedType() == LeftTuple.UPDATE) {
                            // ignore, as it will get processed via left iteration. Children cannot be processed twice
                            leftTuple = temp;
                            continue;
                        }

                        constraints.updateFromTuple(contextEntry,
                                                    wm,
                                                    leftTuple);

                        if (useComparisonIndex) {
                            rootBlocker = rtm.getFirst(leftTuple, null, it);
                        }

                        // we know that older tuples have been checked so continue next
                        for (RightTuple newBlocker = rootBlocker; newBlocker != null; newBlocker = (RightTuple) it.next(newBlocker)) {
                            if (constraints.isAllowedCachedLeft(contextEntry,
                                                                newBlocker.getFactHandle())) {
                                leftTuple.setBlocker(newBlocker);
                                newBlocker.addBlocked(leftTuple);

                                break;
                            }
                        }

                        if (leftTuple.getBlocker() == null) {
                            // was previous blocked and not in memory, so add
                            ltm.add(leftTuple);

                            trgLeftTuples.addInsert(sink.createLeftTuple(leftTuple,
                                                                         sink,
                                                                         rightTuple.getPropagationContext(), true));
                        }

                        leftTuple = temp;
                    }
                }

                rightTuple.nullBlocked();
                rightTuple.clearStaged();
                rightTuple = next;
            }

            constraints.resetTuple(contextEntry);
        }
    }

    public static class PhreakExistsNode {
        public void doNode(ExistsNode existsNode,
                           LeftTupleSink sink,
                           BetaMemory bm,
                           InternalWorkingMemory wm,
                           LeftTupleSets srcLeftTuples,
                           LeftTupleSets trgLeftTuples,
                           LeftTupleSets stagedLeftTuples) {
            RightTupleSets srcRightTuples = bm.getStagedRightTuples();


            if (srcLeftTuples.getUpdateFirst() != null )  {
                dpUpdatesReorderLeftMemory(bm,
                                           srcLeftTuples);
            }

            if ( srcRightTuples.getUpdateFirst() != null ) {
                dpUpdatesExistentialReorderRightMemory(bm,
                                                       existsNode,
                                                       srcRightTuples); // this also preserves the next rightTuple
            }

            if (srcLeftTuples.getDeleteFirst() != null) {
                doLeftDeletes(existsNode, bm, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
            }

            if (srcRightTuples.getInsertFirst() != null) {
                // left deletes must come before right deletes. Otherwise right deletes could
                // stage a deletion, that is later deleted in the rightDelete, causing potential problems
                doRightInserts(existsNode, sink, bm, wm, srcRightTuples, trgLeftTuples);
            }

            if (srcRightTuples.getUpdateFirst() != null) {
                // must come after rightInserts and before rightDeletes, to avoid staging clash
                doRightUpdates(existsNode, sink, bm, wm, srcRightTuples, trgLeftTuples, stagedLeftTuples);
            }

            if (srcRightTuples.getDeleteFirst() != null) {
                // must come after rightUpdetes, to avoid staging clash
                doRightDeletes(existsNode, bm, wm, srcRightTuples, trgLeftTuples, stagedLeftTuples);
            }


            if (srcLeftTuples.getUpdateFirst() != null) {
                doLeftUpdates(existsNode, sink, bm, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
            }

            if (srcLeftTuples.getInsertFirst() != null) {
                doLeftInserts(existsNode, sink, bm, wm, srcLeftTuples, trgLeftTuples);
            }

            srcRightTuples.resetAll();
            srcLeftTuples.resetAll();
        }

        public void doLeftInserts(ExistsNode existsNode,
                                  LeftTupleSink sink,
                                  BetaMemory bm,
                                  InternalWorkingMemory wm,
                                  LeftTupleSets srcLeftTuples,
                                  LeftTupleSets trgLeftTuples) {
            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            RightTupleMemory rtm = bm.getRightTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = existsNode.getRawConstraints();

            for (LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();

                FastIterator it = existsNode.getRightIterator(rtm);
                PropagationContext context = leftTuple.getPropagationContext();

                boolean useLeftMemory = useLeftMemory(existsNode, leftTuple);

                constraints.updateFromTuple(contextEntry,
                                            wm,
                                            leftTuple);

                // This method will also remove rightTuples that are from subnetwork where no leftmemory use used
                findLeftTupleBlocker(existsNode, rtm, contextEntry, constraints, leftTuple, it, context, useLeftMemory);

                if (leftTuple.getBlocker() != null) {
                    // tuple is not blocked to propagate
                    trgLeftTuples.addInsert(sink.createLeftTuple(leftTuple,
                                                                 sink,
                                                                 leftTuple.getBlocker().getPropagationContext(), useLeftMemory));
                } else if (useLeftMemory) {
                    // LeftTuple is not blocked, so add to memory so other RightTuples can match
                    ltm.add(leftTuple);
                }
                leftTuple.clearStaged();
                leftTuple = next;
            }
            constraints.resetTuple(contextEntry);
        }

        public void doRightInserts(ExistsNode existsNode,
                                   LeftTupleSink sink,
                                   BetaMemory bm,
                                   InternalWorkingMemory wm,
                                   RightTupleSets srcRightTuples,
                                   LeftTupleSets trgLeftTuples) {
            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            RightTupleMemory rtm = bm.getRightTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = existsNode.getRawConstraints();

            for (RightTuple rightTuple = srcRightTuples.getInsertFirst(); rightTuple != null; ) {
                RightTuple next = rightTuple.getStagedNext();
                rtm.add(rightTuple);

                FastIterator it = existsNode.getLeftIterator(ltm);
                PropagationContext context = rightTuple.getPropagationContext();

                constraints.updateFromFactHandle(contextEntry,
                                                 wm,
                                                 rightTuple.getFactHandle());

                for (LeftTuple leftTuple = existsNode.getFirstLeftTuple(rightTuple, ltm, context, it); leftTuple != null; ) {
                    // preserve next now, in case we remove this leftTuple 
                    LeftTuple temp = (LeftTuple) it.next(leftTuple);

                    if (leftTuple.getStagedType() == LeftTuple.UPDATE) {
                        // ignore, as it will get processed via left iteration. Children cannot be processed twice
                        leftTuple = temp;
                        continue;
                    }

                    // we know that only unblocked LeftTuples are  still in the memory
                    if (constraints.isAllowedCachedRight(contextEntry,
                                                         leftTuple)) {
                        leftTuple.setBlocker(rightTuple);
                        rightTuple.addBlocked(leftTuple);

                        ltm.remove(leftTuple);

                        trgLeftTuples.addInsert(sink.createLeftTuple(leftTuple,
                                                                     sink,
                                                                     rightTuple.getPropagationContext(), true));
                    }

                    leftTuple = temp;
                }
                rightTuple.clearStaged();
                rightTuple = next;
            }
            constraints.resetFactHandle(contextEntry);
        }

        public void doLeftUpdates(ExistsNode existsNode,
                                  LeftTupleSink sink,
                                  BetaMemory bm,
                                  InternalWorkingMemory wm,
                                  LeftTupleSets srcLeftTuples,
                                  LeftTupleSets trgLeftTuples,
                                  LeftTupleSets stagedLeftTuples) {
            boolean tupleMemory = true;
            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            RightTupleMemory rtm = bm.getRightTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = existsNode.getRawConstraints();

            for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();
                PropagationContext context = leftTuple.getPropagationContext();

                FastIterator rightIt = existsNode.getRightIterator(rtm);

                RightTuple firstRightTuple = existsNode.getFirstRightTuple(leftTuple, rtm, null, rightIt);

                // If in memory, remove it, because we'll need to add it anyway if it's not blocked, to ensure iteration order
                RightTuple blocker = leftTuple.getBlocker();
                if (blocker == null) {
                    ltm.remove(leftTuple);
                } else {
                    // check if we changed bucket
                    if (rtm.isIndexed() && !rightIt.isFullIterator()) {
                        // if newRightTuple is null, we assume there was a bucket change and that bucket is empty                
                        if (firstRightTuple == null || firstRightTuple.getMemory() != blocker.getMemory()) {
                            // we changed bucket, so blocker no longer blocks
                            blocker.removeBlocked(leftTuple);
                            blocker = null;
                        }
                    }
                }

                constraints.updateFromTuple(contextEntry,
                                            wm,
                                            leftTuple);

                // if we where not blocked before (or changed buckets), or the previous blocker no longer blocks, then find the next blocker
                if (blocker == null || !constraints.isAllowedCachedLeft(contextEntry,
                                                                        blocker.getFactHandle())) {

                    if (blocker != null) {
                        // remove previous blocker if it exists, as we know it doesn't block any more
                        blocker.removeBlocked(leftTuple);
                    }

                    // find first blocker, because it's a modify, we need to start from the beginning again        
                    for (RightTuple newBlocker = firstRightTuple; newBlocker != null; newBlocker = (RightTuple) rightIt.next(newBlocker)) {
                        if (constraints.isAllowedCachedLeft(contextEntry,
                                                            newBlocker.getFactHandle())) {
                            leftTuple.setBlocker(newBlocker);
                            newBlocker.addBlocked(leftTuple);

                            break;
                        }
                    }
                }

                if (leftTuple.getBlocker() == null) {
                    // not blocked
                    ltm.add(leftTuple); // add to memory so other fact handles can attempt to match

                    if (leftTuple.getFirstChild() != null) {
                        // with previous children, delete
                        if (leftTuple.getFirstChild() != null) {
                            LeftTuple childLeftTuple = leftTuple.getFirstChild();

                            if (childLeftTuple != null) {
                                // no need to update pctx, as no right available, and pctx will exist on a parent LeftTuple anyway
                                childLeftTuple = deleteLeftChild(childLeftTuple, trgLeftTuples, stagedLeftTuples);
                            }
                        }
                    }
                    // with no previous children. do nothing.
                } else if (leftTuple.getFirstChild() == null) {
                    // blocked, with no previous children, insert
                    trgLeftTuples.addInsert(sink.createLeftTuple(leftTuple,
                                                                 sink,
                                                                 leftTuple.getBlocker().getPropagationContext(), tupleMemory));
                } else {
                    // blocked, with previous children, modify
                    if (leftTuple.getFirstChild() != null) {
                        LeftTuple childLeftTuple = leftTuple.getFirstChild();

                        while (childLeftTuple != null) {
                            switch (childLeftTuple.getStagedType()) {
                                // handle clash with already staged entries
                                case LeftTuple.INSERT:
                                    stagedLeftTuples.removeInsert(childLeftTuple);
                                    break;
                                case LeftTuple.UPDATE:
                                    stagedLeftTuples.removeUpdate(childLeftTuple);
                                    break;
                            }

                            // update, childLeftTuple is updated
                            childLeftTuple.setPropagationContext( leftTuple.getBlocker().getPropagationContext() );
                            trgLeftTuples.addUpdate(childLeftTuple);
                            childLeftTuple.reAddRight();
                            childLeftTuple = childLeftTuple.getLeftParentNext();
                        }
                    }
                }

                leftTuple.clearStaged();
                leftTuple = next;
            }
            constraints.resetTuple(contextEntry);
        }

        public void doRightUpdates(ExistsNode existsNode,
                                   LeftTupleSink sink,
                                   BetaMemory bm,
                                   InternalWorkingMemory wm,
                                   RightTupleSets srcRightTuples,
                                   LeftTupleSets trgLeftTuples,
                                   LeftTupleSets stagedLeftTuples) {
            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            RightTupleMemory rtm = bm.getRightTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = existsNode.getRawConstraints();

            boolean resumeFromCurrent =  !(existsNode.isIndexedUnificationJoin() || rtm.getIndexType().isComparison());

            for (RightTuple rightTuple = srcRightTuples.getUpdateFirst(); rightTuple != null; ) {
                RightTuple next = rightTuple.getStagedNext();

                FastIterator leftIt = existsNode.getLeftIterator(ltm);
                PropagationContext context = rightTuple.getPropagationContext();

                LeftTuple firstLeftTuple = existsNode.getFirstLeftTuple(rightTuple, ltm, context, leftIt);

                constraints.updateFromFactHandle( contextEntry,
                                                  wm,
                                                  rightTuple.getFactHandle() );

                LeftTuple firstBlocked = rightTuple.getBlocked();
                // we now have  reference to the first Blocked, so null it in the rightTuple itself, so we can rebuild
                rightTuple.nullBlocked();

                // first process non-blocked tuples, as we know only those ones are in the left memory.
                for (LeftTuple leftTuple = firstLeftTuple; leftTuple != null; ) {
                    // preserve next now, in case we remove this leftTuple 
                    LeftTuple temp = (LeftTuple) leftIt.next(leftTuple);

                    if (leftTuple.getStagedType() == LeftTuple.UPDATE) {
                        // ignore, as it will get processed via left iteration. Children cannot be processed twice
                        leftTuple = temp;
                        continue;
                    }

                    // we know that only unblocked LeftTuples are  still in the memory
                    if (constraints.isAllowedCachedRight(contextEntry,
                                                         leftTuple)) {
                        leftTuple.setBlocker(rightTuple);
                        rightTuple.addBlocked(leftTuple);

                        // this is now blocked so remove from memory
                        ltm.remove(leftTuple);

                        // subclasses like ForallNotNode might override this propagation
                        trgLeftTuples.addInsert(sink.createLeftTuple(leftTuple,
                                                                     sink,
                                                                     rightTuple.getPropagationContext(), true));
                    }

                    leftTuple = temp;
                }

                if (firstBlocked != null) {
                    RightTuple rootBlocker = rightTuple.getTempNextRightTuple();
                    if (rootBlocker != null ) {
                        if ( rootBlocker != rightTuple ) {
                            rootBlocker = ( RightTuple ) rootBlocker.getNext();
                        }
                    } else{
                        resumeFromCurrent = false;
                    }


                    FastIterator rightIt = existsNode.getRightIterator(rtm);

                    // iterate all the existing previous blocked LeftTuples
                    for (LeftTuple leftTuple = (LeftTuple) firstBlocked; leftTuple != null; ) {
                        LeftTuple temp = leftTuple.getBlockedNext();

                        leftTuple.clearBlocker(); // must null these as we are re-adding them to the list

                        if (leftTuple.getStagedType() == LeftTuple.UPDATE) {
                            // ignore, as it will get processed via left iteration. Children cannot be processed twice
                            // but need to add it back into list first
                            leftTuple.setBlocker(rightTuple);
                            rightTuple.addBlocked(leftTuple);

                            leftTuple = temp;
                            continue;
                        }

                        constraints.updateFromTuple(contextEntry,
                                                    wm,
                                                    leftTuple);

                        if (resumeFromCurrent) {
                            rootBlocker = existsNode.getFirstRightTuple(leftTuple, rtm, null, rightIt);
                        }

                        // we know that older tuples have been checked so continue next
                        for (RightTuple newBlocker = rootBlocker; newBlocker != null; newBlocker = (RightTuple) rightIt.next(newBlocker)) {
                            // cannot select a RightTuple queued in the delete list
                            // There may be UPDATE RightTuples too, but that's ok. They've already been re-added to the correct bucket, safe to be reprocessed.
                            if (leftTuple.getStagedType() != LeftTuple.DELETE && constraints.isAllowedCachedLeft(contextEntry,
                                                                                                                 newBlocker.getFactHandle())) {
                                leftTuple.setBlocker(newBlocker);
                                newBlocker.addBlocked(leftTuple);

                                break;
                            }
                        }

                        if (leftTuple.getBlocker() == null) {
                            // was previous blocked and not in memory, so add
                            ltm.add(leftTuple);

                            LeftTuple childLeftTuple = leftTuple.getFirstChild();
                            if (childLeftTuple != null) {
                                childLeftTuple.setPropagationContext(rightTuple.getPropagationContext());
                                childLeftTuple = deleteLeftChild(childLeftTuple, trgLeftTuples, stagedLeftTuples);
                            }
                        }

                        leftTuple = temp;
                    }
                } else {
                    // we had to do this at the end, rather than beginning as this 'if' block needs the next memory tuple
                    rtm.removeAdd(rightTuple);
                }

                rightTuple.clearStaged();
                rightTuple = next;
            }
            constraints.resetFactHandle(contextEntry);
        }

        public void doLeftDeletes(ExistsNode existsNode,
                                  BetaMemory bm,
                                  InternalWorkingMemory wm,
                                  LeftTupleSets srcLeftTuples,
                                  LeftTupleSets trgLeftTuples,
                                  LeftTupleSets stagedLeftTuples) {
            LeftTupleMemory ltm = bm.getLeftTupleMemory();

            for (LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();
                RightTuple blocker = leftTuple.getBlocker();
                if (blocker == null) {
                    if (leftTuple.getMemory() != null) {
                        // it may have been staged and never actually added
                        ltm.remove(leftTuple);
                    }
                } else {
                    if (leftTuple.getFirstChild() != null) {
                        LeftTuple childLeftTuple = leftTuple.getFirstChild();

                        if (childLeftTuple != null) {
                            // no need to update pctx, as no right available, and pctx will exist on a parent LeftTuple anyway
                            childLeftTuple = deleteLeftChild(childLeftTuple, trgLeftTuples, stagedLeftTuples);
                        }
                    }
                    blocker.removeBlocked(leftTuple);
                }

                leftTuple.clearStaged();
                leftTuple = next;
            }
        }

        public void doRightDeletes(ExistsNode existsNode,
                                   BetaMemory bm,
                                   InternalWorkingMemory wm,
                                   RightTupleSets srcRightTuples,
                                   LeftTupleSets trgLeftTuples,
                                   LeftTupleSets stagedLeftTuples) {
            RightTupleMemory rtm = bm.getRightTupleMemory();
            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = existsNode.getRawConstraints();

            for (RightTuple rightTuple = srcRightTuples.getDeleteFirst(); rightTuple != null; ) {
                RightTuple next = rightTuple.getStagedNext();

                FastIterator it = existsNode.getRightIterator(rtm);

                boolean useComparisonIndex = rtm.getIndexType().isComparison();
                RightTuple rootBlocker = useComparisonIndex ? null : (RightTuple) it.next(rightTuple);

                if (rightTuple.getMemory() != null) {
                    // it may have been staged and never actually added
                    rtm.remove(rightTuple);
                }

                if (rightTuple.getBlocked() != null) {

                    PropagationContext context = rightTuple.getPropagationContext();

                    for (LeftTuple leftTuple = rightTuple.getBlocked(); leftTuple != null; ) {
                        LeftTuple temp = leftTuple.getBlockedNext();

                        leftTuple.clearBlocker();

                        if (leftTuple.getStagedType() == LeftTuple.UPDATE) {
                            // ignore, as it will get processed via left iteration. Children cannot be processed twice
                            leftTuple = temp;
                            continue;
                        }

                        constraints.updateFromTuple(contextEntry,
                                                    wm,
                                                    leftTuple);

                        if (useComparisonIndex) {
                            rootBlocker = rtm.getFirst(leftTuple, null, it);
                        }

                        // we know that older tuples have been checked so continue previously
                        for (RightTuple newBlocker = rootBlocker; newBlocker != null; newBlocker = (RightTuple) it.next(newBlocker)) {
                            if (constraints.isAllowedCachedLeft(contextEntry,
                                                                newBlocker.getFactHandle())) {
                                leftTuple.setBlocker(newBlocker);
                                newBlocker.addBlocked(leftTuple);

                                break;
                            }
                        }

                        if (leftTuple.getBlocker() == null) {
                            // was previous blocked and not in memory, so add
                            ltm.add(leftTuple);

                            LeftTuple childLeftTuple = leftTuple.getFirstChild();
                            if (childLeftTuple != null) {
                                childLeftTuple.setPropagationContext(rightTuple.getPropagationContext());
                                childLeftTuple = deleteLeftChild(childLeftTuple, trgLeftTuples, stagedLeftTuples);
                            }
                        }

                        leftTuple = temp;
                    }
                }
                rightTuple.nullBlocked();
                rightTuple.clearStaged();
                rightTuple = next;
            }
        }
    }

    private static void findLeftTupleBlocker(BetaNode betaNode, RightTupleMemory rtm,
                                             ContextEntry[] contextEntry, BetaConstraints constraints,
                                             LeftTuple leftTuple, FastIterator it,
                                             PropagationContext context, boolean useLeftMemory) {
        // This method will also remove rightTuples that are from subnetwork where no leftmemory use used

        for (RightTuple rightTuple = betaNode.getFirstRightTuple(leftTuple, rtm, null, it); rightTuple != null; ) {
            RightTuple nextRight = (RightTuple) it.next(rightTuple);
            if (constraints.isAllowedCachedLeft(contextEntry,
                                                rightTuple.getFactHandle())) {
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

    public static class PhreakAccumulateNode {
        public void doNode(AccumulateNode accNode,
                           LeftTupleSink sink,
                           AccumulateMemory am,
                           InternalWorkingMemory wm,
                           LeftTupleSets srcLeftTuples,
                           LeftTupleSets trgLeftTuples,
                           LeftTupleSets stagedLeftTuples) {
            boolean useLeftMemory = true;
            RightTupleSets srcRightTuples = am.getBetaMemory().getStagedRightTuples();

            // order of left and right operations is to minimise wasted of innefficient joins.

            // We need to collect which leftTuple where updated, so that we can
            // add their result tuple to the real target tuples later
            LeftTupleSets tempLeftTuples = new LeftTupleSets();

            if (srcLeftTuples.getDeleteFirst() != null) {
                // use the real target here, as dealing direct with left tuples
                doLeftDeletes(accNode, am, wm, srcLeftTuples, trgLeftTuples);
            }

            if (srcRightTuples.getDeleteFirst() != null) {
                doRightDeletes(accNode, am, wm, srcRightTuples, tempLeftTuples);
            }

            if (srcLeftTuples.getUpdateFirst() != null ) {
                dpUpdatesReorderLeftMemory(am.getBetaMemory(),
                                           srcLeftTuples);
            }

            if (srcRightTuples.getUpdateFirst() != null) {
                dpUpdatesReorderRightMemory(am.getBetaMemory(),
                                            srcRightTuples);
            }

            if (srcLeftTuples.getUpdateFirst() != null) {
                doLeftUpdates(accNode, sink, am, wm, srcLeftTuples, tempLeftTuples);
            }

            if (srcRightTuples.getUpdateFirst() != null) {
                doRightUpdates(accNode, sink, am, wm, srcRightTuples, tempLeftTuples);
            }

            if (srcRightTuples.getInsertFirst() != null) {
                doRightInserts(accNode, sink, am, wm, srcRightTuples, tempLeftTuples);
            }

            if (srcLeftTuples.getInsertFirst() != null) {
                doLeftInserts(accNode, sink, am, wm, srcLeftTuples, tempLeftTuples);
            }

            Accumulate accumulate = accNode.getAccumulate();
            // we do not need collect retracts. RightTuple retracts end up as updates for lefttuples. 
            // LeftTuple retracts are already on the trgLeftTuples 
            for (LeftTuple leftTuple = tempLeftTuples.getInsertFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();
                evaluateResultConstraints(accNode, sink, accumulate, leftTuple, leftTuple.getPropagationContext(),
                                          wm, am, (AccumulateContext) leftTuple.getObject(), useLeftMemory,
                                          trgLeftTuples, stagedLeftTuples);
                leftTuple.clearStaged();
                leftTuple = next;
            }

            for (LeftTuple leftTuple = tempLeftTuples.getUpdateFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();
                evaluateResultConstraints(accNode, sink, accumulate, leftTuple, leftTuple.getPropagationContext(),
                                          wm, am, (AccumulateContext) leftTuple.getObject(), useLeftMemory,
                                          trgLeftTuples, stagedLeftTuples);
                leftTuple.clearStaged();
                leftTuple = next;
            }

            srcRightTuples.resetAll();

            srcLeftTuples.resetAll();
        }

        public void doLeftInserts(AccumulateNode accNode,
                                  LeftTupleSink sink,
                                  AccumulateMemory am,
                                  InternalWorkingMemory wm,
                                  LeftTupleSets srcLeftTuples,
                                  LeftTupleSets trgLeftTuples) {

            Accumulate accumulate = accNode.getAccumulate();
            BetaMemory bm = am.getBetaMemory();
            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            RightTupleMemory rtm = bm.getRightTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = accNode.getRawConstraints();


            for (LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();

                boolean useLeftMemory = useLeftMemory(accNode, leftTuple);

                if (useLeftMemory) {
                    ltm.add(leftTuple);
                }

                PropagationContext context = leftTuple.getPropagationContext();

                AccumulateContext accresult = new AccumulateContext();


                leftTuple.setObject(accresult);

                accresult.context = accumulate.createContext();

                accumulate.init(am.workingMemoryContext,
                                accresult.context,
                                leftTuple,
                                wm);

                constraints.updateFromTuple(contextEntry,
                                            wm,
                                            leftTuple);

                FastIterator rightIt = accNode.getRightIterator(rtm);

                for (RightTuple rightTuple = accNode.getFirstRightTuple(leftTuple,
                                                                        rtm,
                                                                        null,
                                                                        rightIt); rightTuple != null; ) {
                    RightTuple nextRightTuple = (RightTuple) rightIt.next(rightTuple);

                    InternalFactHandle handle = rightTuple.getFactHandle();
                    if (constraints.isAllowedCachedLeft(contextEntry,
                                                        handle)) {
                        // add a match
                        addMatch(accNode,
                                 accumulate,
                                 leftTuple,
                                 rightTuple,
                                 null,
                                 null,
                                 wm,
                                 am,
                                 accresult,
                                 useLeftMemory);

                        if (!useLeftMemory && accNode.isRightInputIsRiaNode()) {
                            // RIAN with no left memory must have their right tuples removed
                            rtm.remove(rightTuple);
                        }
                    }

                    rightTuple = nextRightTuple;
                }

                leftTuple.clearStaged();
                trgLeftTuples.addInsert(leftTuple);

                constraints.resetTuple(contextEntry);

                leftTuple = next;
            }
            constraints.resetTuple(contextEntry);
        }

        public void doRightInserts(AccumulateNode accNode,
                                   LeftTupleSink sink,
                                   AccumulateMemory am,
                                   InternalWorkingMemory wm,
                                   RightTupleSets srcRightTuples,
                                   LeftTupleSets trgLeftTuples) {
            Accumulate accumulate = accNode.getAccumulate();

            BetaMemory bm = am.getBetaMemory();
            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            RightTupleMemory rtm = bm.getRightTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = accNode.getRawConstraints();

            for (RightTuple rightTuple = srcRightTuples.getInsertFirst(); rightTuple != null; ) {
                RightTuple next = rightTuple.getStagedNext();

                rtm.add(rightTuple);
                PropagationContext context = rightTuple.getPropagationContext();

                constraints.updateFromFactHandle(contextEntry,
                                                 wm,
                                                 rightTuple.getFactHandle());

                FastIterator leftIt = accNode.getLeftIterator(ltm);

                for (LeftTuple leftTuple = accNode.getFirstLeftTuple(rightTuple, ltm, context, leftIt); leftTuple != null; leftTuple = (LeftTuple) leftIt.next(leftTuple)) {
                    if (constraints.isAllowedCachedRight(contextEntry,
                                                         leftTuple)) {
                        final AccumulateContext accctx = (AccumulateContext) leftTuple.getObject();
                        addMatch(accNode,
                                 accumulate,
                                 leftTuple,
                                 rightTuple,
                                 null,
                                 null,
                                 wm,
                                 am,
                                 accctx,
                                 true);

                        // right inserts and updates are done first
                        // so any existing leftTuples we know are updates, but only add if not already added
                        if (leftTuple.getStagedType() == LeftTuple.NONE) {
                            trgLeftTuples.addUpdate(leftTuple);
                        }

                    }
                }

                rightTuple.clearStaged();
                rightTuple = next;
            }
            constraints.resetFactHandle(contextEntry);
        }

        public void doLeftUpdates(AccumulateNode accNode,
                                  LeftTupleSink sink,
                                  AccumulateMemory am,
                                  InternalWorkingMemory wm,
                                  LeftTupleSets srcLeftTuples,
                                  LeftTupleSets trgLeftTuples) {
            BetaMemory bm = am.getBetaMemory();
            RightTupleMemory rtm = bm.getRightTupleMemory();
            Accumulate accumulate = accNode.getAccumulate();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = accNode.getRawConstraints();

            for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();
                final AccumulateContext accctx = (AccumulateContext) leftTuple.getObject();

                PropagationContext context = leftTuple.getPropagationContext();

                constraints.updateFromTuple(contextEntry,
                                            wm,
                                            leftTuple);

                FastIterator rightIt = accNode.getRightIterator(rtm);
                RightTuple rightTuple = accNode.getFirstRightTuple(leftTuple,
                                                                   rtm,
                                                                   null,
                                                                   rightIt);

                LeftTuple childLeftTuple = leftTuple.getFirstChild();

                // first check our index (for indexed nodes only) hasn't changed and we are returning the same bucket
                // if rightTuple is null, we assume there was a bucket change and that bucket is empty
                if (childLeftTuple != null && rtm.isIndexed() && !rightIt.isFullIterator() && (rightTuple == null || (rightTuple.getMemory() != childLeftTuple.getRightParent().getMemory()))) {
                    // our index has changed, so delete all the previous matchings
                    removePreviousMatchesForLeftTuple(accNode,
                                                      accumulate,
                                                      leftTuple,
                                                      wm,
                                                      am,
                                                      accctx,
                                                      true);

                    childLeftTuple = null; // null so the next check will attempt matches for new bucket
                }

                // we can't do anything if RightTupleMemory is empty
                if (rightTuple != null) {
                    doLeftUpdatesProcessChildren(accNode,
                                                 am,
                                                 wm,
                                                 bm,
                                                 accumulate,
                                                 constraints,
                                                 rightIt,
                                                 leftTuple,
                                                 accctx,
                                                 rightTuple,
                                                 childLeftTuple);
                }

                leftTuple.clearStaged();
                trgLeftTuples.addUpdate(leftTuple);

                leftTuple = next;
            }
            constraints.resetTuple(contextEntry);
        }

        private void doLeftUpdatesProcessChildren(AccumulateNode accNode,
                                                  AccumulateMemory am,
                                                  InternalWorkingMemory wm,
                                                  BetaMemory bm,
                                                  Accumulate accumulate,
                                                  BetaConstraints constraints,
                                                  FastIterator rightIt,
                                                  LeftTuple leftTuple,
                                                  final AccumulateContext accctx,
                                                  RightTuple rightTuple,
                                                  LeftTuple childLeftTuple) {
            if (childLeftTuple == null) {
                // either we are indexed and changed buckets or
                // we had no children before, but there is a bucket to potentially match, so try as normal assert
                for (; rightTuple != null; rightTuple = (RightTuple) rightIt.next(rightTuple)) {
                    final InternalFactHandle handle = rightTuple.getFactHandle();
                    if (constraints.isAllowedCachedLeft(bm.getContext(),
                                                        handle)) {
                        // add a new match
                        addMatch(accNode,
                                 accumulate,
                                 leftTuple,
                                 rightTuple,
                                 null,
                                 null,
                                 wm,
                                 am,
                                 accctx,
                                 true);
                    }
                }
            } else {
                boolean isDirty = false;
                // in the same bucket, so iterate and compare
                for (; rightTuple != null; rightTuple = (RightTuple) rightIt.next(rightTuple)) {
                    final InternalFactHandle handle = rightTuple.getFactHandle();

                    if (constraints.isAllowedCachedLeft(bm.getContext(),
                                                        handle)) {
                        if (childLeftTuple == null || childLeftTuple.getRightParent() != rightTuple) {
                            // add a new match
                            addMatch(accNode,
                                     accumulate,
                                     leftTuple,
                                     rightTuple,
                                     childLeftTuple,
                                     null,
                                     wm,
                                     am,
                                     accctx,
                                     true);
                        } else {
                            // we must re-add this to ensure deterministic iteration
                            LeftTuple temp = childLeftTuple.getLeftParentNext();
                            childLeftTuple.reAddRight();
                            childLeftTuple = temp;
                        }
                    } else if (childLeftTuple != null && childLeftTuple.getRightParent() == rightTuple) {
                        LeftTuple temp = childLeftTuple.getLeftParentNext();
                        // remove the match
                        removeMatch(accNode,
                                    accumulate,
                                    rightTuple,
                                    childLeftTuple,
                                    wm,
                                    am,
                                    accctx,
                                    false);
                        childLeftTuple = temp;
                        // the next line means that when a match is removed from the current leftTuple
                        // and the accumulate does not support the reverse operation, then the whole
                        // result is dirty (since removeMatch above is not recalculating the total)
                        // and we need to do this later
                        isDirty = !accumulate.supportsReverse();
                    }
                    // else do nothing, was false before and false now.
                }
                if (isDirty) {
                    reaccumulateForLeftTuple(accNode,
                                             accumulate,
                                             leftTuple,
                                             wm,
                                             am,
                                             accctx);
                }
            }
        }

        public void doRightUpdates(AccumulateNode accNode,
                                   LeftTupleSink sink,
                                   AccumulateMemory am,
                                   InternalWorkingMemory wm,
                                   RightTupleSets srcRightTuples,
                                   LeftTupleSets trgLeftTuples) {
            BetaMemory bm = am.getBetaMemory();
            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = accNode.getRawConstraints();
            Accumulate accumulate = accNode.getAccumulate();

            for (RightTuple rightTuple = srcRightTuples.getUpdateFirst(); rightTuple != null; ) {
                RightTuple next = rightTuple.getStagedNext();
                PropagationContext context = rightTuple.getPropagationContext();

                LeftTuple childLeftTuple = rightTuple.getFirstChild();

                FastIterator leftIt = accNode.getLeftIterator(ltm);
                LeftTuple leftTuple = accNode.getFirstLeftTuple(rightTuple, ltm, context, leftIt);

                constraints.updateFromFactHandle(contextEntry,
                                                 wm,
                                                 rightTuple.getFactHandle());

                // first check our index (for indexed nodes only) hasn't changed and we are returning the same bucket
                // We assume a bucket change if leftTuple == null
                if (childLeftTuple != null && ltm.isIndexed() && !leftIt.isFullIterator() && (leftTuple == null || (leftTuple.getMemory() != childLeftTuple.getLeftParent().getMemory()))) {
                    // our index has changed, so delete all the previous matches
                    removePreviousMatchesForRightTuple(accNode,
                                                       accumulate,
                                                       rightTuple,
                                                       context,
                                                       wm,
                                                       am,
                                                       childLeftTuple,
                                                       trgLeftTuples);
                    childLeftTuple = null; // null so the next check will attempt matches for new bucket
                }

                // if LeftTupleMemory is empty, there are no matches to modify
                if (leftTuple != null) {
                    if (leftTuple.getStagedType() == LeftTuple.NONE) {
                        trgLeftTuples.addUpdate(leftTuple);
                    }

                    doRightUpdatesProcessChildren(accNode,
                                                  am,
                                                  wm,
                                                  bm,
                                                  constraints,
                                                  accumulate,
                                                  leftIt,
                                                  rightTuple,
                                                  childLeftTuple,
                                                  leftTuple,
                                                  trgLeftTuples);
                }

                rightTuple.clearStaged();
                rightTuple = next;
            }
            constraints.resetFactHandle(contextEntry);
        }

        private void doRightUpdatesProcessChildren(AccumulateNode accNode,
                                                   AccumulateMemory am,
                                                   InternalWorkingMemory wm,
                                                   BetaMemory bm,
                                                   BetaConstraints constraints,
                                                   Accumulate accumulate,
                                                   FastIterator leftIt,
                                                   RightTuple rightTuple,
                                                   LeftTuple childLeftTuple,
                                                   LeftTuple leftTuple,
                                                   LeftTupleSets trgLeftTuples) {
            if (childLeftTuple == null) {
                // either we are indexed and changed buckets or
                // we had no children before, but there is a bucket to potentially match, so try as normal assert
                for (; leftTuple != null; leftTuple = (LeftTuple) leftIt.next(leftTuple)) {
                    if (constraints.isAllowedCachedRight(bm.getContext(),
                                                         leftTuple)) {
                        if (leftTuple.getStagedType() == LeftTuple.NONE) {
                            trgLeftTuples.addUpdate(leftTuple);
                        }
                        final AccumulateContext accctx = (AccumulateContext) leftTuple.getObject();
                        // add a new match
                        addMatch(accNode,
                                 accumulate,
                                 leftTuple,
                                 rightTuple,
                                 null,
                                 null,
                                 wm,
                                 am,
                                 accctx,
                                 true);
                    }
                }
            } else {
                // in the same bucket, so iterate and compare
                for (; leftTuple != null; leftTuple = (LeftTuple) leftIt.next(leftTuple)) {
                    if (constraints.isAllowedCachedRight(bm.getContext(),
                                                         leftTuple)) {
                        if (leftTuple.getStagedType() == LeftTuple.NONE) {
                            trgLeftTuples.addUpdate(leftTuple);
                        }
                        final AccumulateContext accctx = (AccumulateContext) leftTuple.getObject();
                        LeftTuple temp = null;
                        if (childLeftTuple != null && childLeftTuple.getLeftParent() == leftTuple) {
                            temp = childLeftTuple.getRightParentNext();
                            // we must re-add this to ensure deterministic iteration                            
                            removeMatch(accNode,
                                        accumulate,
                                        rightTuple,
                                        childLeftTuple,
                                        wm,
                                        am,
                                        accctx,
                                        true);
                            childLeftTuple = temp;
                        }
                        // add a new match
                        addMatch(accNode,
                                 accumulate,
                                 leftTuple,
                                 rightTuple,
                                 null,
                                 childLeftTuple,
                                 wm,
                                 am,
                                 accctx,
                                 true);
                        if (temp != null) {
                            childLeftTuple = temp;
                        }
                    } else if (childLeftTuple != null && childLeftTuple.getLeftParent() == leftTuple) {
                        if (leftTuple.getStagedType() == LeftTuple.NONE) {
                            trgLeftTuples.addUpdate(leftTuple);
                        }

                        LeftTuple temp = childLeftTuple.getRightParentNext();
                        final AccumulateContext accctx = (AccumulateContext) leftTuple.getObject();
                        // remove the match
                        removeMatch(accNode,
                                    accumulate,
                                    rightTuple,
                                    childLeftTuple,
                                    wm,
                                    am,
                                    accctx,
                                    true);

                        childLeftTuple = temp;
                    }
                    // else do nothing, was false before and false now.
                }
            }
        }


        public void doLeftDeletes(AccumulateNode accNode,
                                  AccumulateMemory am,
                                  InternalWorkingMemory wm,
                                  LeftTupleSets srcLeftTuples,
                                  LeftTupleSets trgLeftTuples) {
            BetaMemory bm = am.getBetaMemory();
            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            //ContextEntry[] contextEntry = bm.getContext();
            Accumulate accumulate = accNode.getAccumulate();

            for (LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();
                if (leftTuple.getMemory() != null) {
                    // it may have been staged and never actually added
                    ltm.remove(leftTuple);


                    final AccumulateContext accctx = (AccumulateContext) leftTuple.getObject();
                    leftTuple.setObject(null);

                    removePreviousMatchesForLeftTuple(accNode,
                                                      accumulate,
                                                      leftTuple,
                                                      wm,
                                                      am,
                                                      accctx,
                                                      false);

                    if (accctx.propagated) {
                        trgLeftTuples.addDelete(accctx.resultLeftTuple);
                    } else {
                        // if not propagated, just destroy the result fact handle
                        // workingMemory.getFactHandleFactory().destroyFactHandle( accctx.result.getFactHandle() );
                    }
                }

                leftTuple.clearStaged();
                leftTuple = next;
            }
        }

        public void doRightDeletes(AccumulateNode accNode,
                                   AccumulateMemory am,
                                   InternalWorkingMemory wm,
                                   RightTupleSets srcRightTuples,
                                   LeftTupleSets trgLeftTuples) {
            RightTupleMemory rtm = am.getBetaMemory().getRightTupleMemory();
            Accumulate accumulate = accNode.getAccumulate();

            for (RightTuple rightTuple = srcRightTuples.getDeleteFirst(); rightTuple != null; ) {
                RightTuple next = rightTuple.getStagedNext();
                if (rightTuple.getMemory() != null) {
                    // it may have been staged and never actually added
                    rtm.remove(rightTuple);

                    if (rightTuple.getFirstChild() != null) {
                        LeftTuple match = rightTuple.getFirstChild();

                        while (match != null) {
                            LeftTuple nextLeft = match.getRightParentNext();
                            ;

                            LeftTuple leftTuple = match.getLeftParent();
                            final AccumulateContext accctx = (AccumulateContext) leftTuple.getObject();
                            removeMatch(accNode, accumulate, rightTuple, match, wm, am, accctx, true);

                            if (leftTuple.getStagedType() == LeftTuple.NONE) {
                                trgLeftTuples.addUpdate(leftTuple);
                            }

                            match.unlinkFromLeftParent();

                            match = nextLeft;
                        }
                    }
                }
                rightTuple.clearStaged();
                rightTuple = next;
            }
        }

        public void evaluateResultConstraints(final AccumulateNode accNode,
                                              final LeftTupleSink sink,
                                              final Accumulate accumulate,
                                              final LeftTuple leftTuple,
                                              final PropagationContext context,
                                              final InternalWorkingMemory workingMemory,
                                              final AccumulateMemory memory,
                                              final AccumulateContext accctx,
                                              final boolean useLeftMemory,
                                              final LeftTupleSets trgLeftTuples,
                                              final LeftTupleSets stagedLeftTuples) {
            // get the actual result
            final Object[] resultArray = accumulate.getResult(memory.workingMemoryContext,
                                                              accctx.context,
                                                              leftTuple,
                                                              workingMemory);
            Object result = accumulate.isMultiFunction() ? resultArray : resultArray[0];
            if (result == null) {
                return;
            }

            if (accctx.getResultFactHandle() == null) {
                final InternalFactHandle handle = accNode.createResultFactHandle(context,
                                                                                 workingMemory,
                                                                                 leftTuple,
                                                                                 result);

                accctx.setResultFactHandle(handle);

                accctx.setResultLeftTuple(sink.createLeftTuple(handle, leftTuple, sink));
            } else {
                accctx.getResultFactHandle().setObject(result);
            }

            // First alpha node filters
            AlphaNodeFieldConstraint[] resultConstraints = accNode.getResultConstraints();
            BetaConstraints resultBinder = accNode.getResultBinder();
            boolean isAllowed = result != null;
            for (int i = 0, length = resultConstraints.length; isAllowed && i < length; i++) {
                if (!resultConstraints[i].isAllowed(accctx.resultFactHandle,
                                                    workingMemory,
                                                    memory.alphaContexts[i])) {
                    isAllowed = false;
                }
            }
            if (isAllowed) {
                resultBinder.updateFromTuple(memory.resultsContext,
                                             workingMemory,
                                             leftTuple);
                if (!resultBinder.isAllowedCachedLeft(memory.resultsContext,
                                                      accctx.getResultFactHandle())) {
                    isAllowed = false;
                }
                resultBinder.resetTuple(memory.resultsContext);
            }


            LeftTuple childLeftTuple = (LeftTuple) accctx.getResultLeftTuple();
            childLeftTuple.setPropagationContext(leftTuple.getPropagationContext());
            if (accctx.propagated == true) {
                switch (childLeftTuple.getStagedType()) {
                    // handle clash with already staged entries
                    case LeftTuple.INSERT:
                        stagedLeftTuples.removeInsert(childLeftTuple);
                        break;
                    case LeftTuple.UPDATE:
                        stagedLeftTuples.removeUpdate(childLeftTuple);
                        break;
                }

                if (isAllowed) {
                    // modify 
                    trgLeftTuples.addUpdate(childLeftTuple);
                } else {
                    // retract                 
                    trgLeftTuples.addDelete(childLeftTuple);
                    accctx.propagated = false;
                }
            } else if (isAllowed) {
                // assert
                trgLeftTuples.addInsert(childLeftTuple);
                accctx.propagated = true;
            }

        }

        public static void addMatch(final AccumulateNode accNode,
                                    final Accumulate accumulate,
                                    final LeftTuple leftTuple,
                                    final RightTuple rightTuple,
                                    final LeftTuple currentLeftChild,
                                    final LeftTuple currentRightChild,
                                    final InternalWorkingMemory wm,
                                    final AccumulateMemory am,
                                    final AccumulateContext accresult,
                                    final boolean useLeftMemory) {
            LeftTuple tuple = leftTuple;
            InternalFactHandle handle = rightTuple.getFactHandle();
            if (accNode.isUnwrapRightObject()) {
                // if there is a subnetwork, handle must be unwrapped
                tuple = (LeftTuple) handle.getObject();
                //handle = tuple.getLastHandle();
            }
            accumulate.accumulate(am.workingMemoryContext,
                                  accresult.context,
                                  tuple,
                                  handle,
                                  wm);

            // in sequential mode, we don't need to keep record of matched tuples
            if (useLeftMemory) {
                // linking left and right by creating a new left tuple
                accNode.createLeftTuple(leftTuple,
                                        rightTuple,
                                        currentLeftChild,
                                        currentRightChild,
                                        accNode,
                                        true);
            }
        }

        /**
         * Removes a match between left and right tuple
         */
        public static void removeMatch(final AccumulateNode accNode,
                                       final Accumulate accumulate,
                                       final RightTuple rightTuple,
                                       final LeftTuple match,
                                       final InternalWorkingMemory wm,
                                       final AccumulateMemory am,
                                       final AccumulateContext accctx,
                                       final boolean reaccumulate) {
            // save the matching tuple
            LeftTuple leftTuple = match.getLeftParent();

            // removing link between left and right
            match.unlinkFromLeftParent();
            match.unlinkFromRightParent();

            // if there is a subnetwork, we need to unwrap the object from inside the tuple
            InternalFactHandle handle = rightTuple.getFactHandle();
            LeftTuple tuple = leftTuple;
            if (accNode.isUnwrapRightObject()) {
                tuple = (LeftTuple) handle.getObject();
            }

            if (accumulate.supportsReverse()) {
                // just reverse this single match
                accumulate.reverse(am.workingMemoryContext,
                                   accctx.context,
                                   tuple,
                                   handle,
                                   wm);
            } else {
                // otherwise need to recalculate all matches for the given leftTuple
                if (reaccumulate) {
                    reaccumulateForLeftTuple(accNode,
                                             accumulate,
                                             leftTuple,
                                             wm,
                                             am,
                                             accctx);

                }
            }
        }


        public static void reaccumulateForLeftTuple(final AccumulateNode accNode,
                                                    final Accumulate accumulate,
                                                    final LeftTuple leftTuple,
                                                    final InternalWorkingMemory wm,
                                                    final AccumulateMemory am,
                                                    final AccumulateContext accctx) {
            accumulate.init(am.workingMemoryContext,
                            accctx.context,
                            leftTuple,
                            wm);
            for (LeftTuple childMatch = leftTuple.getFirstChild(); childMatch != null; childMatch = childMatch.getLeftParentNext()) {
                InternalFactHandle childHandle = childMatch.getRightParent().getFactHandle();
                LeftTuple tuple = leftTuple;
                if (accNode.isUnwrapRightObject()) {
                    tuple = (LeftTuple) childHandle.getObject();
                    childHandle = tuple.getLastHandle();
                }
                accumulate.accumulate(am.workingMemoryContext,
                                      accctx.context,
                                      tuple,
                                      childHandle,
                                      wm);
            }
        }

        public static void removePreviousMatchesForRightTuple(final AccumulateNode accNode,
                                                              final Accumulate accumulate,
                                                              final RightTuple rightTuple,
                                                              final PropagationContext context,
                                                              final InternalWorkingMemory workingMemory,
                                                              final AccumulateMemory memory,
                                                              final LeftTuple firstChild,
                                                              final LeftTupleSets trgLeftTuples) {
            for (LeftTuple match = firstChild; match != null; ) {
                final LeftTuple next = match.getRightParentNext();

                final LeftTuple leftTuple = match.getLeftParent();
                final AccumulateContext accctx = (AccumulateContext) leftTuple.getObject();
                removeMatch(accNode,
                            accumulate,
                            rightTuple,
                            match,
                            workingMemory,
                            memory,
                            accctx,
                            true);

                if (leftTuple.getStagedType() == LeftTuple.NONE) {
                    trgLeftTuples.addUpdate(leftTuple);
                }

                match = next;
            }
        }

        public static void removePreviousMatchesForLeftTuple(final AccumulateNode accNode,
                                                             final Accumulate accumulate,
                                                             final LeftTuple leftTuple,
                                                             final InternalWorkingMemory workingMemory,
                                                             final AccumulateMemory memory,
                                                             final AccumulateContext accctx,
                                                             boolean reInit) {
            for (LeftTuple match = leftTuple.getFirstChild(); match != null; ) {
                LeftTuple next = match.getLeftParentNext();
                match.unlinkFromRightParent();
                match.unlinkFromLeftParent();
                match = next;
            }

            if (reInit) {
                // since there are no more matches, the following call will just re-initialize the accumulation
                accumulate.init(memory.workingMemoryContext,
                                accctx.context,
                                leftTuple,
                                workingMemory);
            }
        }

    }

    public static class PhreakEvalNode {
        public void doNode(EvalConditionNode evalNode,
                           EvalMemory em,
                           LeftTupleSink sink,
                           InternalWorkingMemory wm,
                           LeftTupleSets srcLeftTuples,
                           LeftTupleSets trgLeftTuples,
                           LeftTupleSets stagedLeftTuples) {

            if (srcLeftTuples.getDeleteFirst() != null) {
                doLeftDeletes(evalNode, em, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
            }

            if (srcLeftTuples.getUpdateFirst() != null) {
                doLeftUpdates(evalNode, em, sink, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
            }

            if (srcLeftTuples.getInsertFirst() != null) {
                doLeftInserts(evalNode, em, sink, wm, srcLeftTuples, trgLeftTuples);
            }

            srcLeftTuples.resetAll();
        }

        public void doLeftInserts(EvalConditionNode evalNode,
                                  EvalMemory em,
                                  LeftTupleSink sink,
                                  InternalWorkingMemory wm,
                                  LeftTupleSets srcLeftTuples,
                                  LeftTupleSets trgLeftTuples) {
            EvalCondition condition = evalNode.getCondition();
            for (LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();

                final boolean allowed = condition.isAllowed(leftTuple,
                                                            wm,
                                                            em.context);

                if (allowed) {
                    boolean useLeftMemory = useLeftMemory(evalNode, leftTuple);

                    trgLeftTuples.addInsert(sink.createLeftTuple(leftTuple,
                                                                 sink,
                                                                 leftTuple.getPropagationContext(), useLeftMemory));
                }

                leftTuple.clearStaged();
                leftTuple = next;
            }
        }

        public void doLeftUpdates(EvalConditionNode evalNode,
                                  EvalMemory em,
                                  LeftTupleSink sink,
                                  InternalWorkingMemory wm,
                                  LeftTupleSets srcLeftTuples,
                                  LeftTupleSets trgLeftTuples,
                                  LeftTupleSets stagedLeftTuples) {
            EvalCondition condition = evalNode.getCondition();
            for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();

                boolean wasPropagated = leftTuple.getFirstChild() != null;

                boolean allowed = condition.isAllowed(leftTuple,
                                                      wm,
                                                      em.context);
                if (allowed) {
                    if (wasPropagated) {
                        // update
                        LeftTuple childLeftTuple = leftTuple.getFirstChild();

                        switch (childLeftTuple.getStagedType()) {
                            // handle clash with already staged entries
                            case LeftTuple.INSERT:
                                stagedLeftTuples.removeInsert(childLeftTuple);
                                break;
                            case LeftTuple.UPDATE:
                                stagedLeftTuples.removeUpdate(childLeftTuple);
                                break;
                        }

                        trgLeftTuples.addUpdate(childLeftTuple);
                    } else {
                        // assert
                        trgLeftTuples.addInsert(sink.createLeftTuple(leftTuple,
                                                                     sink,
                                                                     leftTuple.getPropagationContext(), true));
                    }
                } else {
                    if (wasPropagated) {
                        // retract

                        LeftTuple childLeftTuple = leftTuple.getFirstChild();
                        switch (childLeftTuple.getStagedType()) {
                            // handle clash with already staged entries
                            case LeftTuple.INSERT:
                                stagedLeftTuples.removeInsert(childLeftTuple);
                                break;
                            case LeftTuple.UPDATE:
                                stagedLeftTuples.removeUpdate(childLeftTuple);
                                break;
                        }

                        trgLeftTuples.addDelete(childLeftTuple);
                    }
                    // else do nothing
                }

                leftTuple.clearStaged();
                leftTuple = next;
            }
        }

        public void doLeftDeletes(EvalConditionNode evalNode,
                                  EvalMemory em,
                                  InternalWorkingMemory wm,
                                  LeftTupleSets srcLeftTuples,
                                  LeftTupleSets trgLeftTuples,
                                  LeftTupleSets stagedLeftTuples) {
            for (LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();


                LeftTuple childLeftTuple = leftTuple.getFirstChild();
                if (childLeftTuple != null) {
                    switch (childLeftTuple.getStagedType()) {
                        // handle clash with already staged entries
                        case LeftTuple.INSERT:
                            stagedLeftTuples.removeInsert(childLeftTuple);
                            break;
                        case LeftTuple.UPDATE:
                            stagedLeftTuples.removeUpdate(childLeftTuple);
                            break;
                    }
                    trgLeftTuples.addDelete(childLeftTuple);
                }

                leftTuple.clearStaged();
                leftTuple = next;
            }
        }
    }

    public static class PhreakQueryNode {
        public void doNode(QueryElementNode queryNode,
                           QueryElementNodeMemory qmem,
                           StackEntry stackEntry,
                           LeftTupleSink sink,
                           InternalWorkingMemory wm,
                           LeftTupleSets srcLeftTuples) {

            if (srcLeftTuples.getDeleteFirst() != null) {
                doLeftDeletes(qmem, wm, srcLeftTuples);
            }

            if (srcLeftTuples.getUpdateFirst() != null) {
                doLeftUpdates(queryNode, qmem, sink, wm, srcLeftTuples);
            }

            if (srcLeftTuples.getInsertFirst() != null) {
                doLeftInserts(queryNode, qmem, stackEntry, wm, srcLeftTuples);
            }

            srcLeftTuples.resetAll();
        }

        public void doLeftInserts(QueryElementNode queryNode,
                                  QueryElementNodeMemory qmem,
                                  StackEntry stackEntry,
                                  InternalWorkingMemory wm,
                                  LeftTupleSets srcLeftTuples) {
            for (LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();

                PropagationContext pCtx = (PropagationContext) leftTuple.getPropagationContext();

                InternalFactHandle handle = queryNode.createFactHandle(pCtx,
                                                                       wm,
                                                                       leftTuple);

                DroolsQuery dquery = queryNode.createDroolsQuery(leftTuple, handle, stackEntry,
                                                                 qmem.getSegmentMemory().getPathMemories(),
                                                                 qmem.getResultLeftTuples(),
                                                                 stackEntry.getSink(), wm);

                LeftInputAdapterNode lian = (LeftInputAdapterNode) qmem.getQuerySegmentMemory().getRootNode();
                LiaNodeMemory lm = (LiaNodeMemory) qmem.getQuerySegmentMemory().getNodeMemories().get(0);
                LeftInputAdapterNode.doInsertObject(handle, pCtx, lian, wm, lm, false, dquery.isOpen());

                leftTuple.clearStaged();
                leftTuple = next;
            }
        }

        public void doLeftUpdates(QueryElementNode queryNode,
                                  QueryElementNodeMemory qmem,
                                  LeftTupleSink sink,
                                  InternalWorkingMemory wm,
                                  LeftTupleSets srcLeftTuples) {
            for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();

                InternalFactHandle fh = (InternalFactHandle) leftTuple.getObject();
                DroolsQuery dquery = (DroolsQuery) fh.getObject();

                Object[] argTemplate = queryNode.getQueryElement().getArgTemplate(); // an array of declr, variable and literals
                Object[] args = new Object[argTemplate.length]; // the actual args, to be created from the  template

                // first copy everything, so that we get the literals. We will rewrite the declarations and variables next
                System.arraycopy(argTemplate,
                                 0,
                                 args,
                                 0,
                                 args.length);

                int[] declIndexes = queryNode.getQueryElement().getDeclIndexes();

                for (int i = 0, length = declIndexes.length; i < length; i++) {
                    Declaration declr = (Declaration) argTemplate[declIndexes[i]];

                    Object tupleObject = leftTuple.get(declr).getObject();

                    Object o;

                    if (tupleObject instanceof DroolsQuery) {
                        // If the query passed in a Variable, we need to use it
                        ArrayElementReader arrayReader = (ArrayElementReader) declr.getExtractor();
                        if (((DroolsQuery) tupleObject).getVariables()[arrayReader.getIndex()] != null) {
                            o = Variable.v;
                        } else {
                            o = declr.getValue(wm,
                                               tupleObject);
                        }
                    } else {
                        o = declr.getValue(wm,
                                           tupleObject);
                    }

                    args[declIndexes[i]] = o;
                }

                int[] varIndexes = queryNode.getQueryElement().getVariableIndexes();
                for (int i = 0, length = varIndexes.length; i < length; i++) {
                    if (argTemplate[varIndexes[i]] == Variable.v) {
                        // Need to check against the arg template, as the varIndexes also includes re-declared declarations
                        args[varIndexes[i]] = Variable.v;
                    }
                }

                dquery.setParameters(args);
                ((UnificationNodeViewChangedEventListener) dquery.getQueryResultCollector()).setVariables(varIndexes);

                LeftInputAdapterNode lian = (LeftInputAdapterNode) qmem.getQuerySegmentMemory().getRootNode();
                if (dquery.isOpen()) {
                    LeftTuple childLeftTuple = fh.getFirstLeftTuple(); // there is only one, all other LTs are peers
                    LeftInputAdapterNode.doUpdateObject(childLeftTuple, childLeftTuple.getPropagationContext(), wm, lian, false, qmem.getQuerySegmentMemory());
                } else {
                    if (fh.getFirstLeftTuple() != null) {
                        throw new RuntimeException("defensive programming while testing"); // @TODO remove later (mdp)
                    }
                    LiaNodeMemory lm = (LiaNodeMemory) qmem.getQuerySegmentMemory().getNodeMemories().get(0);
                    LeftInputAdapterNode.doInsertObject(fh, leftTuple.getPropagationContext(), lian, wm, lm, false, dquery.isOpen());
                }


                leftTuple.clearStaged();
                leftTuple = next;
            }
        }

        public void doLeftDeletes(QueryElementNodeMemory qmem,
                                  InternalWorkingMemory wm,
                                  LeftTupleSets srcLeftTuples) {
            for (LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();

                InternalFactHandle fh = (InternalFactHandle) leftTuple.getObject();
                DroolsQuery dquery = (DroolsQuery) fh.getObject();
                if (dquery.isOpen()) {
                    LeftInputAdapterNode lian = (LeftInputAdapterNode) qmem.getQuerySegmentMemory().getRootNode();
                    LiaNodeMemory lm = (LiaNodeMemory) qmem.getQuerySegmentMemory().getNodeMemories().get(0);
                    LeftTuple childLeftTuple = fh.getFirstLeftTuple(); // there is only one, all other LTs are peers
                    LeftInputAdapterNode.doDeleteObject(childLeftTuple, childLeftTuple.getPropagationContext(), qmem.getQuerySegmentMemory(), wm, lian, false, lm);
                } // else do nothing, no state is maintained

                leftTuple.clearStaged();
                leftTuple = next;
            }
        }
    }

    public static class PhreakBranchNode {
        public void doNode(ConditionalBranchNode branchNode,
                           ConditionalBranchMemory cbm,
                           LeftTupleSink sink,
                           InternalWorkingMemory wm,
                           LeftTupleSets srcLeftTuples,
                           LeftTupleSets trgLeftTuples,
                           LeftTupleSets stagedLeftTuples, RuleNetworkEvaluatorActivation activation) {

            if (srcLeftTuples.getDeleteFirst() != null) {
                doLeftDeletes(branchNode, cbm, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples, activation);
            }

            if (srcLeftTuples.getUpdateFirst() != null) {
                doLeftUpdates(branchNode, cbm, sink, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples, activation);
            }

            if (srcLeftTuples.getInsertFirst() != null) {
                doLeftInserts(branchNode, cbm, sink, wm, srcLeftTuples, trgLeftTuples, activation);
            }

            srcLeftTuples.resetAll();
        }

        public void doLeftInserts(ConditionalBranchNode branchNode,
                                  ConditionalBranchMemory cbm,
                                  LeftTupleSink sink,
                                  InternalWorkingMemory wm,
                                  LeftTupleSets srcLeftTuples,
                                  LeftTupleSets trgLeftTuples, RuleNetworkEvaluatorActivation activation) {
            ConditionalBranchEvaluator branchEvaluator = branchNode.getBranchEvaluator();
            LeftTupleList tupleList = activation.getLeftTupleList();

            for (LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();

                boolean breaking = false;
                ConditionalExecution conditionalExecution = branchEvaluator.evaluate(leftTuple, wm, cbm.context);

                boolean useLeftMemory = useLeftMemory(branchNode, leftTuple);

                if (conditionalExecution != null) {
                    RuleTerminalNode rtn = (RuleTerminalNode) conditionalExecution.getSink().getFirstLeftTupleSink();
                    LeftTuple branchedLeftTuple = rtn.createLeftTuple(leftTuple,
                                                                      rtn,
                                                                      leftTuple.getPropagationContext(), useLeftMemory);

                    leftTuple.setObject(branchedLeftTuple);

                    //rtn.assertLeftTuple(branchedLeftTuple, leftTuple.getPropagationContext(), wm);
                    tupleList.add(branchedLeftTuple);

                    breaking = conditionalExecution.isBreaking();
                }

                if (!breaking) {
                    trgLeftTuples.addInsert(sink.createLeftTuple(leftTuple,
                                                                 sink,
                                                                 leftTuple.getPropagationContext(), useLeftMemory));
                }

                leftTuple.clearStaged();
                leftTuple = next;
            }
        }

        public void doLeftUpdates(ConditionalBranchNode branchNode,
                                  ConditionalBranchMemory cbm,
                                  LeftTupleSink sink,
                                  InternalWorkingMemory wm,
                                  LeftTupleSets srcLeftTuples,
                                  LeftTupleSets trgLeftTuples,
                                  LeftTupleSets stagedLeftTuples, RuleNetworkEvaluatorActivation activation) {
            ConditionalBranchEvaluator branchEvaluator = branchNode.getBranchEvaluator();
            LeftTupleList tupleList = activation.getLeftTupleList();

            for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();

                LeftTuple rtnLeftTuple = (LeftTuple) leftTuple.getObject();
                LeftTuple mainLeftTuple = leftTuple.getFirstChild();

                RuleTerminalNode oldRtn = null;
                if (rtnLeftTuple != null) {
                    oldRtn = (RuleTerminalNode) rtnLeftTuple.getSink();
                }

                ConditionalExecution conditionalExecution = branchEvaluator.evaluate(leftTuple, wm, cbm.context);

                RuleTerminalNode newRtn = null;
                boolean breaking = false;
                if (conditionalExecution != null) {
                    newRtn = (RuleTerminalNode) conditionalExecution.getSink().getFirstLeftTupleSink();
                    breaking = conditionalExecution.isBreaking();
                }

                // Handle conditional branches
                if (oldRtn != null) {
                    if (newRtn == null) {
                        // old exits, new does not, so delete
                        if ( rtnLeftTuple.getMemory() != null ) {
                            tupleList.remove(rtnLeftTuple);
                        }
                        oldRtn.retractLeftTuple(rtnLeftTuple, rtnLeftTuple.getPropagationContext(), wm);

                    } else if (newRtn == oldRtn) {
                        // old and new on same branch, so update
                        if ( rtnLeftTuple.getMemory() != null ) {
                            tupleList.remove(rtnLeftTuple); // must be removed before it can bereadded
                        }
                        tupleList.add(rtnLeftTuple);
                        //oldRtn.modifyLeftTuple(rtnLeftTuple, rtnLeftTuple.getPropagationContext(), wm);

                    } else {
                        // old and new on different branches, delete one and insert the other
                        if ( rtnLeftTuple.getMemory() != null ) {
                            tupleList.remove(rtnLeftTuple);
                        }
                        oldRtn.retractLeftTuple(rtnLeftTuple, rtnLeftTuple.getPropagationContext(), wm);

                        rtnLeftTuple = newRtn.createLeftTuple(leftTuple,
                                                              newRtn,
                                                              leftTuple.getPropagationContext(), true);

                        leftTuple.setObject(rtnLeftTuple);
                        tupleList.add(rtnLeftTuple);
                        //newRtn.assertLeftTuple(rtnLeftTuple, rtnLeftTuple.getPropagationContext(), wm);
                    }

                } else if (newRtn != null) {
                    // old does not exist, new exists, so insert
                    rtnLeftTuple = newRtn.createLeftTuple(leftTuple,
                                                          newRtn,
                                                          leftTuple.getPropagationContext(), true);

                    leftTuple.setObject(rtnLeftTuple);
                    //newRtn.assertLeftTuple(rtnLeftTuple, rtnLeftTuple.getPropagationContext(), wm);
                    tupleList.add(rtnLeftTuple);
                }

                // Handle main branch
                if (mainLeftTuple != null) {
                    switch (mainLeftTuple.getStagedType()) {
                        // handle clash with already staged entries
                        case LeftTuple.INSERT:
                            stagedLeftTuples.removeInsert(mainLeftTuple);
                            break;
                        case LeftTuple.UPDATE:
                            stagedLeftTuples.removeUpdate(mainLeftTuple);
                            break;
                    }

                    if (!breaking) {
                        // child exist, new one does, so update
                        trgLeftTuples.addUpdate(mainLeftTuple);
                    } else {
                        // child exist, new one does not, so delete
                        trgLeftTuples.addDelete(mainLeftTuple);
                    }
                } else if (!breaking) {
                    // child didn't exist, new one does, so insert
                    trgLeftTuples.addInsert(sink.createLeftTuple(leftTuple,
                                                                 sink,
                                                                 leftTuple.getPropagationContext(), true));
                }

                leftTuple.clearStaged();
                leftTuple = next;
            }
        }

        public void doLeftDeletes(ConditionalBranchNode branchNode,
                                  ConditionalBranchMemory cbm,
                                  InternalWorkingMemory wm,
                                  LeftTupleSets srcLeftTuples,
                                  LeftTupleSets trgLeftTuples,
                                  LeftTupleSets stagedLeftTuples, RuleNetworkEvaluatorActivation activation) {
            LeftTupleList tupleList = activation.getLeftTupleList();

            for (LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();

                LeftTuple rtnLeftTuple = (LeftTuple) leftTuple.getObject();
                LeftTuple mainLeftTuple = leftTuple.getFirstChild();

                if (rtnLeftTuple != null) {
                    RuleTerminalNode rtn = (RuleTerminalNode) rtnLeftTuple.getSink();
                    if ( rtnLeftTuple.getMemory() != null ) {
                        tupleList.remove(rtnLeftTuple);
                    }
                    rtn.retractLeftTuple(rtnLeftTuple,
                                         rtnLeftTuple.getPropagationContext(),
                                         wm);
                }

                if (mainLeftTuple != null) {
                    switch (mainLeftTuple.getStagedType()) {
                        // handle clash with already staged entries
                        case LeftTuple.INSERT:
                            stagedLeftTuples.removeInsert(mainLeftTuple);
                            break;
                        case LeftTuple.UPDATE:
                            stagedLeftTuples.removeUpdate(mainLeftTuple);
                            break;
                    }
                    trgLeftTuples.addDelete(mainLeftTuple);
                }

                leftTuple.clearStaged();
                leftTuple = next;
            }
        }
    }


    public static class PhreakFromNode {
        public void doNode(FromNode fromNode,
                           FromMemory fm,
                           LeftTupleSink sink,
                           InternalWorkingMemory wm,
                           LeftTupleSets srcLeftTuples,
                           LeftTupleSets trgLeftTuples,
                           LeftTupleSets stagedLeftTuples) {

            if (srcLeftTuples.getDeleteFirst() != null) {
                doLeftDeletes(fromNode, fm, sink, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
            }

            if (srcLeftTuples.getUpdateFirst() != null) {
                doLeftUpdates(fromNode, fm, sink, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
            }

            if (srcLeftTuples.getInsertFirst() != null) {
                doLeftInserts(fromNode, fm, sink, wm, srcLeftTuples, trgLeftTuples);
            }

            srcLeftTuples.resetAll();
        }

        public void doLeftInserts(FromNode fromNode,
                                  FromMemory fm,
                                  LeftTupleSink sink,
                                  InternalWorkingMemory wm,
                                  LeftTupleSets srcLeftTuples,
                                  LeftTupleSets trgLeftTuples) {

            BetaMemory bm = fm.getBetaMemory();
            ContextEntry[] context = bm.getContext();
            BetaConstraints betaConstraints = fromNode.getBetaConstraints();
            AlphaNodeFieldConstraint[] alphaConstraints = fromNode.getAlphaConstraints();
            DataProvider dataProvider = fromNode.getDataProvider();
            Class resultClass = fromNode.getResultClass();

            for (LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();

                PropagationContext propagationContext = leftTuple.getPropagationContext();

                Map<Object, RightTuple> matches = null;
                boolean useLeftMemory = useLeftMemory(fromNode, leftTuple);

                if (useLeftMemory) {
                    fm.betaMemory.getLeftTupleMemory().add(leftTuple);
                    matches = new LinkedHashMap<Object, RightTuple>();
                    leftTuple.setObject(matches);
                }

                betaConstraints.updateFromTuple(context,
                                                wm,
                                                leftTuple);

                for (final java.util.Iterator<?> it = dataProvider.getResults(leftTuple,
                                                                              wm,
                                                                              propagationContext,
                                                                              fm.providerContext); it.hasNext(); ) {
                    final Object object = it.next();
                    if ( (object == null) || !resultClass.isAssignableFrom( object.getClass() ) ) {
                        continue; // skip anything if it not assignable
                    }

                    RightTuple rightTuple = fromNode.createRightTuple(leftTuple,
                                                                      propagationContext,
                                                                      wm,
                                                                      object);

                    checkConstraintsAndPropagate(sink,
                                                 leftTuple,
                                                 rightTuple,
                                                 alphaConstraints,
                                                 betaConstraints,
                                                 propagationContext,
                                                 wm,
                                                 fm,
                                                 bm,
                                                 context,
                                                 useLeftMemory,
                                                 trgLeftTuples,
                                                 null);
                    if (useLeftMemory) {
                        fromNode.addToCreatedHandlesMap(matches,
                                                        rightTuple);
                    }
                }

                leftTuple.clearStaged();
                leftTuple = next;
            }
            betaConstraints.resetTuple(context);
        }

        public void doLeftUpdates(FromNode fromNode,
                                  FromMemory fm,
                                  LeftTupleSink sink,
                                  InternalWorkingMemory wm,
                                  LeftTupleSets srcLeftTuples,
                                  LeftTupleSets trgLeftTuples,
                                  LeftTupleSets stagedLeftTuples) {
            BetaMemory bm = fm.getBetaMemory();
            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            ContextEntry[] context = bm.getContext();
            BetaConstraints betaConstraints = fromNode.getBetaConstraints();
            AlphaNodeFieldConstraint[] alphaConstraints = fromNode.getAlphaConstraints();
            DataProvider dataProvider = fromNode.getDataProvider();
            Class resultClass = fromNode.getResultClass();

            for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();

                PropagationContext propagationContext = leftTuple.getPropagationContext();

                ltm.removeAdd(leftTuple);

                final Map<Object, RightTuple> previousMatches = (Map<Object, RightTuple>) leftTuple.getObject();
                final Map<Object, RightTuple> newMatches = new HashMap<Object, RightTuple>();
                leftTuple.setObject(newMatches);

                betaConstraints.updateFromTuple(context,
                                                wm,
                                                leftTuple);

                FastIterator rightIt = LinkedList.fastIterator;
                for (final java.util.Iterator<?> it = dataProvider.getResults(leftTuple,
                                                                              wm,
                                                                              propagationContext,
                                                                              fm.providerContext); it.hasNext(); ) {
                    final Object object = it.next();
                    if ( (object == null) || !resultClass.isAssignableFrom( object.getClass() ) ) {
                        continue; // skip anything if it not assignable
                    }

                    RightTuple rightTuple = previousMatches.remove(object);

                    if (rightTuple == null) {
                        // new match, propagate assert
                        rightTuple = fromNode.createRightTuple(leftTuple,
                                                               propagationContext,
                                                               wm,
                                                               object);
                    } else {
                        // previous match, so reevaluate and propagate modify
                        if (rightIt.next(rightTuple) != null) {
                            // handle the odd case where more than one object has the same hashcode/equals value
                            previousMatches.put(object,
                                                (RightTuple) rightIt.next(rightTuple));
                            rightTuple.setNext(null);
                        }
                    }

                    checkConstraintsAndPropagate(sink,
                                                 leftTuple,
                                                 rightTuple,
                                                 alphaConstraints,
                                                 betaConstraints,
                                                 propagationContext,
                                                 wm,
                                                 fm,
                                                 bm,
                                                 context,
                                                 true,
                                                 trgLeftTuples,
                                                 null);

                    fromNode.addToCreatedHandlesMap(newMatches,
                                                    rightTuple);
                }

                for (RightTuple rightTuple : previousMatches.values()) {
                    for (RightTuple current = rightTuple; current != null; current = (RightTuple) rightIt.next(current)) {
                        LeftTuple childLeftTuple = current.getFirstChild();
                        childLeftTuple.unlinkFromLeftParent();
                        childLeftTuple.unlinkFromRightParent();

                        switch (childLeftTuple.getStagedType()) {
                            // handle clash with already staged entries
                            case LeftTuple.INSERT:
                                stagedLeftTuples.removeInsert(childLeftTuple);
                                break;
                            case LeftTuple.UPDATE:
                                stagedLeftTuples.removeUpdate(childLeftTuple);
                                break;
                        }

                        childLeftTuple.setPropagationContext(propagationContext);
                        trgLeftTuples.addDelete(childLeftTuple);
                    }
                }

                leftTuple.clearStaged();
                leftTuple = next;
            }
            betaConstraints.resetTuple(context);
        }

        public void doLeftDeletes(FromNode fromNode,
                                  FromMemory fm,
                                  LeftTupleSink sink,
                                  InternalWorkingMemory wm,
                                  LeftTupleSets srcLeftTuples,
                                  LeftTupleSets trgLeftTuples,
                                  LeftTupleSets stagedLeftTuples) {
            BetaMemory bm = fm.getBetaMemory();
            LeftTupleMemory ltm = bm.getLeftTupleMemory();

            for (LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();

                ltm.remove(leftTuple);

                Map<Object, RightTuple> matches = (Map<Object, RightTuple>) leftTuple.getObject();

                if (leftTuple.getFirstChild() != null) {
                    LeftTuple childLeftTuple = leftTuple.getFirstChild();

                    while (childLeftTuple != null) {
                        childLeftTuple = deleteLeftChild(childLeftTuple, trgLeftTuples, stagedLeftTuples);
                    }
                }


                // @TODO (mdp) is this really necessary? won't the entire FH and RightTuple chaines just et GC'd?
                unlinkCreatedHandles(leftTuple);

                leftTuple.clearStaged();
                leftTuple = next;
            }
        }

        public static void unlinkCreatedHandles(final LeftTuple leftTuple) {
            Map<Object, RightTuple> matches = (Map<Object, RightTuple>) leftTuple.getObject();
            FastIterator rightIt = LinkedList.fastIterator;
            for (RightTuple rightTuple : matches.values()) {
                for (RightTuple current = rightTuple; current != null; ) {
                    RightTuple next = (RightTuple) rightIt.next(current);
                    current.unlinkFromRightParent();
                    current = next;
                }
            }
        }

        protected void checkConstraintsAndPropagate(final LeftTupleSink sink,
                                                    final LeftTuple leftTuple,
                                                    final RightTuple rightTuple,
                                                    final AlphaNodeFieldConstraint[] alphaConstraints,
                                                    final BetaConstraints betaConstraints,
                                                    final PropagationContext propagationContext,
                                                    final InternalWorkingMemory wm,
                                                    final FromMemory fm,
                                                    final BetaMemory bm,
                                                    final ContextEntry[] context,
                                                    final boolean useLeftMemory,
                                                    LeftTupleSets trgLeftTuples,
                                                    LeftTupleSets stagedLeftTuples) {
            boolean isAllowed = true;
            if (alphaConstraints != null) {
                // First alpha node filters
                for (int i = 0, length = alphaConstraints.length; i < length; i++) {
                    if (!alphaConstraints[i].isAllowed(rightTuple.getFactHandle(),
                                                       wm,
                                                       fm.alphaContexts[i])) {
                        // next iteration
                        isAllowed = false;
                        break;
                    }
                }
            }

            if (isAllowed && betaConstraints.isAllowedCachedLeft(context,
                                                                 rightTuple.getFactHandle())) {

                if (rightTuple.firstChild == null) {
                    // this is a new match, so propagate as assert
                    LeftTuple childLeftTuple = sink.createLeftTuple(leftTuple,
                                                                    rightTuple,
                                                                    null,
                                                                    null,
                                                                    sink,
                                                                    useLeftMemory);
                    childLeftTuple.setPropagationContext(propagationContext);
                    trgLeftTuples.addInsert(childLeftTuple);
                } else {
                    LeftTuple childLeftTuple = rightTuple.firstChild;

                    switch (childLeftTuple.getStagedType()) {
                        // handle clash with already staged entries
                        case LeftTuple.INSERT:
                            stagedLeftTuples.removeInsert(childLeftTuple);
                            break;
                        case LeftTuple.UPDATE:
                            stagedLeftTuples.removeUpdate(childLeftTuple);
                            break;
                    }

                    childLeftTuple.setPropagationContext(propagationContext);
                    trgLeftTuples.addUpdate(childLeftTuple);
                }
            } else {
                LeftTuple childLeftTuple = rightTuple.firstChild;
                if (childLeftTuple != null) {
                    switch (childLeftTuple.getStagedType()) {
                        // handle clash with already staged entries
                        case LeftTuple.INSERT:
                            stagedLeftTuples.removeInsert(childLeftTuple);
                            break;
                        case LeftTuple.UPDATE:
                            stagedLeftTuples.removeUpdate(childLeftTuple);
                            break;
                    }
                    childLeftTuple.setPropagationContext(propagationContext);
                    trgLeftTuples.addDelete(childLeftTuple);
                }
            }
        }
    }

    public static class PhreakRuleTerminalNode {
        public void doNode(TerminalNode rtnNode,
                           InternalWorkingMemory wm,
                           LeftTupleSets srcLeftTuples, RuleNetworkEvaluatorActivation activation) {
            if (srcLeftTuples.getDeleteFirst() != null) {
                doLeftDeletes(rtnNode, wm, srcLeftTuples, activation);
            }

            if (srcLeftTuples.getUpdateFirst() != null) {
                doLeftUpdates(rtnNode, wm, srcLeftTuples, activation);
            }

            if (srcLeftTuples.getInsertFirst() != null) {
                doLeftInserts(rtnNode, wm, srcLeftTuples, activation);
            }

            srcLeftTuples.resetAll();
        }

        public void doLeftInserts(TerminalNode rtnNode,
                                  InternalWorkingMemory wm,
                                  LeftTupleSets srcLeftTuples, RuleNetworkEvaluatorActivation ruleNetworkEvaluatorActivation) {
            boolean declarativeAgendaEnabled = ruleNetworkEvaluatorActivation.isDeclarativeAgendaEnabled();
            InternalAgenda agenda = ( InternalAgenda ) wm.getAgenda();
            int salience = 0;
            if( declarativeAgendaEnabled && rtnNode.getType() == NodeTypeEnums.RuleTerminalNode ) {
                salience = rtnNode.getRule().getSalience().getValue(null, null, null); // currently all branches have the same salience for the same rule
            }

            LeftTupleList tupleList = ruleNetworkEvaluatorActivation.getLeftTupleList();
            for (LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();
                tupleList.add(leftTuple);
                leftTuple.increaseActivationCountForEvents(); // increased here, decreased in Agenda's cancelActivation and fireActivation
                if( declarativeAgendaEnabled ) {
                    PropagationContext pctx = leftTuple.getPropagationContext();
                    AgendaItem item = agenda.createAgendaItem(leftTuple, salience, pctx,
                                                              rtnNode, ruleNetworkEvaluatorActivation );
                    item.setActivated(true);
                    leftTuple.setObject(item);
                    agenda.insertAndStageActivation(item);
                }
                leftTuple.clearStaged();
                leftTuple = next;
            }
        }

        public void doLeftUpdates(TerminalNode rtnNode,
                                  InternalWorkingMemory wm,
                                  LeftTupleSets srcLeftTuples, RuleNetworkEvaluatorActivation ruleNetworkEvaluatorActivation) {
            boolean declarativeAgendaEnabled = ruleNetworkEvaluatorActivation.isDeclarativeAgendaEnabled();
            InternalAgenda agenda = ( InternalAgenda ) wm.getAgenda();

            LeftTupleList tupleList = ruleNetworkEvaluatorActivation.getLeftTupleList();
            for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();
                boolean reAdd = true;
                AgendaItem item = null;
                if( declarativeAgendaEnabled && leftTuple.getObject() != null ) {
                   item = ( AgendaItem )leftTuple.getObject();
                   if ( item.getBlockers() != null && !item.getBlockers().isEmpty() ) {
                       reAdd = false; // declarativeAgenda still blocking LeftTuple, so don't add back ot list
                   }
                }
                if ( reAdd && leftTuple.getMemory() == null ) {
                    tupleList.add(leftTuple);
                }

                if( declarativeAgendaEnabled) {
                    agenda.modifyActivation(item, item.isActive());
                }
                leftTuple.clearStaged();
                leftTuple = next;
            }
        }

        public void doLeftDeletes(TerminalNode rtnNode,
                                  InternalWorkingMemory wm,
                                  LeftTupleSets srcLeftTuples, RuleNetworkEvaluatorActivation activation) {
            LeftTupleList tupleList = activation.getLeftTupleList();
            for (LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();
                if ( leftTuple.getMemory() != null ) {
                    tupleList.remove(leftTuple);
                }
                rtnNode.retractLeftTuple(leftTuple, leftTuple.getPropagationContext(), wm);
                leftTuple.clearStaged();
                leftTuple = next;
            }
        }
    }

    public static class PhreakQueryTerminalNode {
        public void doNode(QueryTerminalNode qtnNode,
                           InternalWorkingMemory wm,
                           LeftTupleSets srcLeftTuples,
                           LinkedList<StackEntry> stack) {
            if (srcLeftTuples.getDeleteFirst() != null) {
                doLeftDeletes(qtnNode, wm, srcLeftTuples, stack);
            }

            if (srcLeftTuples.getUpdateFirst() != null) {
                doLeftUpdates(qtnNode, wm, srcLeftTuples, stack);
            }

            if (srcLeftTuples.getInsertFirst() != null) {
                doLeftInserts(qtnNode, wm, srcLeftTuples, stack);
            }

            srcLeftTuples.resetAll();
        }

        public void doLeftInserts(QueryTerminalNode qtnNode,
                                  InternalWorkingMemory wm,
                                  LeftTupleSets srcLeftTuples,
                                  LinkedList<StackEntry> stack) {

            for (LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();
                //qtnNode.assertLeftTuple( leftTuple, leftTuple.getPropagationContext(), wm );

                PropagationContext pCtx = RuleTerminalNode.findMostRecentPropagationContext(leftTuple,
                                                                                            leftTuple.getPropagationContext());

                // find the DroolsQuery object
                LeftTuple rootEntry = leftTuple.getRootLeftTuple();

                DroolsQuery dquery = (DroolsQuery) rootEntry.getLastHandle().getObject();
                dquery.setQuery(qtnNode.getQuery());
                if (dquery.getStackEntry() != null) {
                    checkAndTriggerQueryReevaluation(wm, stack, rootEntry, dquery);
                }

                // Add results to the adapter
                dquery.getQueryResultCollector().rowAdded(qtnNode.getQuery(),
                                                          leftTuple,
                                                          pCtx,
                                                          wm);

                leftTuple.clearStaged();
                leftTuple = next;
            }
        }

        public void doLeftUpdates(QueryTerminalNode qtnNode,
                                  InternalWorkingMemory wm,
                                  LeftTupleSets srcLeftTuples,
                                  LinkedList<StackEntry> stack) {

            for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();

                PropagationContext pCtx = RuleTerminalNode.findMostRecentPropagationContext(leftTuple,
                                                                                            leftTuple.getPropagationContext());

                // qtnNode.modifyLeftTuple( leftTuple, leftTuple.getPropagationContext(), wm );
                LeftTuple rootEntry = leftTuple;

                // find the DroolsQuery object
                while (rootEntry.getParent() != null) {
                    rootEntry = rootEntry.getParent();
                }
                DroolsQuery dquery = (DroolsQuery) rootEntry.getLastHandle().getObject();
                dquery.setQuery(qtnNode.getQuery());
                if (dquery.getStackEntry() != null) {
                    checkAndTriggerQueryReevaluation(wm, stack, rootEntry, dquery);
                }

                // Add results to the adapter
                dquery.getQueryResultCollector().rowUpdated(qtnNode.getQuery(),
                                                            leftTuple,
                                                            pCtx,
                                                            wm);

                leftTuple.clearStaged();
                leftTuple = next;
            }
        }

        public void doLeftDeletes(QueryTerminalNode qtnNode,
                                  InternalWorkingMemory wm,
                                  LeftTupleSets srcLeftTuples,
                                  LinkedList<StackEntry> stack) {

            for (LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();

                //qtnNode.retractLeftTuple( leftTuple, leftTuple.getPropagationContext(), wm );

                PropagationContext pCtx = RuleTerminalNode.findMostRecentPropagationContext(leftTuple,
                                                                                            leftTuple.getPropagationContext());

                LeftTuple rootEntry = leftTuple;

                // find the DroolsQuery object
                while (rootEntry.getParent() != null) {
                    rootEntry = rootEntry.getParent();
                }
                DroolsQuery dquery = (DroolsQuery) rootEntry.getLastHandle().getObject();
                dquery.setQuery(qtnNode.getQuery());

                if (dquery.getStackEntry() != null) {
                    checkAndTriggerQueryReevaluation(wm, stack, rootEntry, dquery);
                }

                // Add results to the adapter
                dquery.getQueryResultCollector().rowRemoved(qtnNode.getQuery(),
                                                            leftTuple,
                                                            pCtx,
                                                            wm);

                leftTuple.clearStaged();
                leftTuple = next;
            }
        }


        public static void checkAndTriggerQueryReevaluation(InternalWorkingMemory wm, LinkedList<StackEntry> stack, LeftTuple rootEntry, DroolsQuery dquery) {
            StackEntry stackEntry = dquery.getStackEntry();
            if (!isAdded(stack, stackEntry)) {
                // Ignore unless stackEntry is not added to stack

                if (stackEntry.getLiaNode()== rootEntry.getLeftTupleSink().getLeftTupleSource()) {
                    // query is recursive, so just re-add the stack entry to the current stack. This happens for reactive queries, triggered by a beta node right input
                    stack.add(stackEntry);
                } else {
                    // parents is anther rule/query need to notify for agenda to schedule. query is reactive, triggered by right input,
                    List<PathMemory> rmems = dquery.getRuleMemories();
                    if (rmems != null) {
                        // StackEntry is null, when query is called directly from java

                        // reactivity comes form within the query, so need to notify parent rules to evaluate the results
                        for (int i = 0, length = rmems.size(); i < length; i++) {
                            PathMemory rmem = rmems.get(i);
                            rmem.doLinkRule(wm); // method already ignores is rule is activated and on agenda
                        }
                    }
                }
            }
        }

        public static boolean isAdded(LinkedList<StackEntry> stack, StackEntry stackEntry) {
            if (stackEntry == null || stackEntry.getPrevious() != null || stackEntry.getNext() != null || stack.getFirst() == stackEntry) {
                return true;
            }

            return false;
        }
    }

    public static LeftTuple deleteLeftChild(LeftTuple childLeftTuple,
                                            LeftTupleSets trgLeftTuples,
                                            LeftTupleSets stagedLeftTuples) {
        switch (childLeftTuple.getStagedType()) {
            // handle clash with already staged entries
            case LeftTuple.INSERT:
                stagedLeftTuples.removeInsert(childLeftTuple);
                break;
            case LeftTuple.UPDATE:
                stagedLeftTuples.removeUpdate(childLeftTuple);
                break;
        }

        LeftTuple next = childLeftTuple.getLeftParentNext();

        trgLeftTuples.addDelete(childLeftTuple);
        childLeftTuple.unlinkFromRightParent();
        childLeftTuple.unlinkFromLeftParent();

        return next;
    }

    public static LeftTuple deleteRightChild(LeftTuple childLeftTuple,
                                             LeftTupleSets trgLeftTuples,
                                             LeftTupleSets stagedLeftTuples) {
        switch (childLeftTuple.getStagedType()) {
            // handle clash with already staged entries
            case LeftTuple.INSERT:
                stagedLeftTuples.removeInsert(childLeftTuple);
                break;
            case LeftTuple.UPDATE:
                stagedLeftTuples.removeUpdate(childLeftTuple);
                break;
        }

        LeftTuple next = childLeftTuple.getRightParentNext();

        trgLeftTuples.addDelete(childLeftTuple);
        childLeftTuple.unlinkFromRightParent();
        childLeftTuple.unlinkFromLeftParent();

        return next;
    }

    public static void dpUpdatesReorderLeftMemory(BetaMemory bm,
                                                  LeftTupleSets srcLeftTuples) {
        LeftTupleMemory ltm = bm.getLeftTupleMemory();

        // sides must first be re-ordered, to ensure iteration integrity
        for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();
            if (leftTuple.getMemory() != null) {
                ltm.removeAdd(leftTuple);
                for (LeftTuple childLeftTuple = leftTuple.getFirstChild(); childLeftTuple != null; ) {
                    LeftTuple childNext = childLeftTuple.getLeftParentNext();
                    childLeftTuple.reAddRight();
                    childLeftTuple = childNext;
                }
            }
            leftTuple = next;
        }
    }

    public static void dpUpdatesReorderRightMemory(BetaMemory bm,
                                                   RightTupleSets srcRightTuples) {
        RightTupleMemory rtm = bm.getRightTupleMemory();

        for (RightTuple rightTuple = srcRightTuples.getUpdateFirst(); rightTuple != null; ) {
            RightTuple next = rightTuple.getStagedNext();
            if (rightTuple.getMemory() != null) {
                rtm.removeAdd(rightTuple);
                for (LeftTuple childLeftTuple = rightTuple.getFirstChild(); childLeftTuple != null; ) {
                    LeftTuple childNext = childLeftTuple.getRightParentNext();
                    childLeftTuple.reAddLeft();
                    childLeftTuple = childNext;
                }
            }
            rightTuple = next;
        }
    }

    public static void dpUpdatesExistentialReorderRightMemory(BetaMemory bm,
                                                              BetaNode betaNode,
                                                              RightTupleSets srcRightTuples) {
        RightTupleMemory rtm = bm.getRightTupleMemory();
//        boolean useComparisonIndex = rtm.getIndexType().isComparison();
//        boolean indexedUnificationJoin = betaNode.isIndexedUnificationJoin();

        boolean resumeFromCurrent =  !(betaNode.isIndexedUnificationJoin() || rtm.getIndexType().isComparison());


        for (RightTuple rightTuple = srcRightTuples.getUpdateFirst(); rightTuple != null; ) {
            RightTuple next = rightTuple.getStagedNext();
            if (rightTuple.getMemory() != null) {

                if (resumeFromCurrent) {
                    RightTupleMemory currentRtm = rightTuple.getMemory();
                    RightTuple tempRightTuple = ( RightTuple ) rightTuple.getPrevious();

                    while ( tempRightTuple != null && tempRightTuple.getStagedType() != LeftTuple.NONE ) {
                        // next cannot be an updated or deleted rightTuple
                        tempRightTuple =(RightTuple) rightTuple.getPrevious();
                    };

                    rtm.removeAdd( rightTuple );

                    if ( tempRightTuple == null && rightTuple.getMemory() == currentRtm  ) {
                        // the next RightTuple was null, but current RightTuple was added back into the same bucket, so reset as root blocker to re-match can be attempted
                        tempRightTuple = rightTuple;
                    }
                    rightTuple.setTempNextRightTuple( tempRightTuple );
                }  else {
                    rtm.removeAdd( rightTuple );
                }

//                // range indexing already iterates from the start
//                if ( !useComparisonIndex && rightTuple.getBlocked() != null ) {
//                    FastIterator rightIt = betaNode.getRightIterator(rtm, rightTuple);
//
//                    // if rightTuple blocks anything, preserve next, as this is lost in re-add.
//                    rightTuple.setTempRightTupleList(rightTuple.getMemory()); // also preserve current RightTupleList
//                    RightTuple tempRightTuple = (RightTuple) rightIt.next(rightTuple);
//                    while ( tempRightTuple != null && tempRightTuple.getStagedType() != LeftTuple.NONE ) {
//                        // next cannot be an updated or deleted rightTuple
//                        tempRightTuple =  (RightTuple) rightIt.next(tempRightTuple);
//                    };
//                    rightTuple.setTempNextRightTuple(tempRightTuple);
//                }


                for (LeftTuple childLeftTuple = rightTuple.getFirstChild(); childLeftTuple != null; ) {
                    LeftTuple childNext = childLeftTuple.getRightParentNext();
                    childLeftTuple.reAddLeft();
                    childLeftTuple = childNext;
                }
            }
            rightTuple = next;
        }

    }

    public static boolean useLeftMemory(LeftTupleSource tupleSource, LeftTuple leftTuple) {
        boolean useLeftMemory = true;
        if (!tupleSource.isLeftTupleMemoryEnabled()) {
            // This is a hack, to not add closed DroolsQuery objects
            Object object = leftTuple.getRootLeftTuple().getLastHandle().getObject();
            if (!(object instanceof DroolsQuery) || !((DroolsQuery) object).isOpen()) {
                useLeftMemory = false;
            }
        }
        return useLeftMemory;
    }

}
