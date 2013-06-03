package org.drools.core.phreak;

import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.core.base.DroolsQuery;
import org.drools.core.common.BetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.LeftTupleSets;
import org.drools.core.common.LeftTupleSetsImpl;
import org.drools.core.common.Memory;
import org.drools.core.common.NetworkNode;
import org.drools.core.common.RightTupleSets;
import org.drools.core.reteoo.AccumulateNode;
import org.drools.core.reteoo.TimerNode;
import org.drools.core.reteoo.TimerNode.TimerNodeMemory;
import org.drools.core.util.FastIterator;
import org.drools.core.util.LinkedList;
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
import org.drools.core.reteoo.LeftTupleMemory;
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
import org.drools.core.reteoo.RiaPathMemory;
import org.drools.core.reteoo.RightInputAdapterNode;
import org.drools.core.reteoo.RightInputAdapterNode.RiaNodeMemory;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RightTupleMemory;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.ContextEntry;
import org.drools.core.spi.PropagationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static PhreakTimerNode        pTimerNode  = new PhreakTimerNode();
    private static PhreakRuleTerminalNode pRtNode     = new PhreakRuleTerminalNode();

    private static int cycle = 0;

    private static PhreakQueryTerminalNode pQtNode = new PhreakQueryTerminalNode();

    public RuleNetworkEvaluator() {

    }

    public int evaluateNetwork(PathMemory pmem, LinkedList<StackEntry> outerStack, RuleExecutor executor, InternalWorkingMemory wm) {
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
            node = liaNode.getSinkPropagator().getFirstLeftTupleSink();
            nodeMem = smem.getNodeMemories().getFirst().getNext(); // skip the liaNode memory
        }

        LeftTupleSets srcTuples = smem.getStagedLeftTuples().takeAll(); // need to takeAll, as this is taken alpha network

        if (log.isTraceEnabled()) {
            log.trace("Rule[name={}] segments={} {}", ((TerminalNode)pmem.getNetworkNode()).getRule().getName(), smems.length, srcTuples.toStringSizes());
        }

        Set<String> visitedRules;
        if (((TerminalNode)pmem.getNetworkNode()).getType() == NodeTypeEnums.QueryTerminalNode) {
            visitedRules = new HashSet<String>();
        } else {
            visitedRules = Collections.<String>emptySet();
        }

        LinkedList<StackEntry> stack = new LinkedList<StackEntry>();
        outerEval(liaNode, pmem, (LeftTupleSink) node, nodeMem, smems, smemIndex, srcTuples, wm, stack, outerStack, visitedRules, true, executor);

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

    public void outerEval(LeftInputAdapterNode liaNode,
                          PathMemory pmem,
                          NetworkNode node,
                          Memory nodeMem,
                          SegmentMemory[] smems,
                          int smemIndex,
                          LeftTupleSets trgTuples,
                          InternalWorkingMemory wm,
                          LinkedList<StackEntry> stack,
                          LinkedList<StackEntry> outerStack,
                          Set<String> visitedRules,
                          boolean processRian,
                          RuleExecutor executor) {
        innerEval(liaNode, pmem, node, nodeMem, smems, smemIndex, trgTuples, wm, stack, outerStack, visitedRules, processRian, executor);
        while (true) {
            // eval
            if (!stack.isEmpty()) {
                StackEntry entry = stack.removeLast();
                evalStackEntry(entry, stack, outerStack, executor, wm);
            } else {
                return; // stack is empty return;
            }
        }
    }

    public void evalStackEntry(StackEntry entry, LinkedList<StackEntry> stack, LinkedList<StackEntry> outerStack, RuleExecutor executor, InternalWorkingMemory wm) {
        NetworkNode node = entry.getNode();
        Memory nodeMem = entry.getNodeMem();
        LeftTupleSets trgTuples = entry.getTrgTuples();
        if (node.getType() == NodeTypeEnums.QueryElementNode) {
            // copy across the results, if any from the query node memory
            trgTuples.addAll(((QueryElementNodeMemory) nodeMem).getResultLeftTuples());
        }

        LeftTupleSinkNode sink = entry.getSink();
        PathMemory pmem = entry.getRmem();

        SegmentMemory[] smems = entry.getSmems();
        int smemIndex = entry.getSmemIndex();
        Set<String> visitedRules = entry.getVisitedRules();
        boolean processRian;
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
        innerEval(entry.getLiaNode(), pmem, node, nodeMem, smems, smemIndex, trgTuples, wm, stack, outerStack, visitedRules, processRian, executor);
    }

    public void innerEval(LeftInputAdapterNode liaNode,
                          PathMemory pmem,
                          NetworkNode node,
                          Memory nodeMem,
                          SegmentMemory[] smems,
                          int smemIndex,
                          LeftTupleSets trgTuples,
                          InternalWorkingMemory wm,
                          LinkedList<StackEntry> stack,
                          LinkedList<StackEntry> outerStack,
                          Set<String> visitedRules,
                          boolean processRian,
                          RuleExecutor executor) {
        LeftTupleSets srcTuples;
        SegmentMemory smem = smems[smemIndex];
        while (true) {
            srcTuples = trgTuples; // previous target, is now the source
            if (log.isTraceEnabled()) {
                int offset = getOffset(node);
                log.trace("{} {} {} {}", indent(offset), ++cycle, node.toString(), srcTuples.toStringSizes());
            }

            if (NodeTypeEnums.isTerminalNode(node)) {
                TerminalNode rtn = ( TerminalNode ) node;
                if (node.getType() == NodeTypeEnums.QueryTerminalNode) {
                    pQtNode.doNode((QueryTerminalNode) rtn,
                                   wm,
                                   srcTuples,
                                   stack);
                } else {
                    pRtNode.doNode(rtn,
                                   wm,
                                   srcTuples,
                                   executor);
                }
                return;
            } else if (NodeTypeEnums.RightInputAdaterNode == node.getType()) {
                doRiaNode2(wm, srcTuples, (RightInputAdapterNode) node, stack);
                return;
            }

            LeftTupleSets stagedLeftTuples;
            if (node == smem.getTipNode() ) {
                // we are about to process the segment tip, allow it to merge insert/update/delete clashes
                if ( smem.isEmpty() ) {
                    // Can happen if the next segments have not yet been initialized, only when there are no right inputs
                    synchronized ( smem ) {
                        if ( smem.isEmpty() ) {
                            SegmentUtilities.createChildSegments( wm,
                                                                  smem,
                                                                  ((LeftTupleSource) node).getSinkPropagator() );
                        }
                        stagedLeftTuples = smem.getFirst().getStagedLeftTuples();
                    }
                } else {
                    stagedLeftTuples = smem.getFirst().getStagedLeftTuples();
                }
            } else {
                stagedLeftTuples = null;
            }

            LeftTupleSinkNode sink = ((LeftTupleSource) node).getSinkPropagator().getFirstLeftTupleSink();

            trgTuples = new LeftTupleSetsImpl();


            if (NodeTypeEnums.isBetaNode(node)) {
                boolean exitInnerEval = evalBetaNode(liaNode, pmem, node, nodeMem, smems, smemIndex, trgTuples, wm, stack, outerStack, visitedRules, processRian, executor, srcTuples, stagedLeftTuples, sink);
                if ( exitInnerEval ) {
                    return; // RiaNode exists and has placed StackEntry on the Stack
                }
            } else {
                switch (node.getType()) {
                    case NodeTypeEnums.EvalConditionNode: {
                        if ( stagedLeftTuples != null ) {
                            synchronized ( stagedLeftTuples ) {
                                pEvalNode.doNode((EvalConditionNode) node, (EvalMemory) nodeMem, sink,
                                                 wm, srcTuples, trgTuples, stagedLeftTuples);
                            }
                        } else {
                            pEvalNode.doNode((EvalConditionNode) node, (EvalMemory) nodeMem, sink,
                                             wm, srcTuples, trgTuples, stagedLeftTuples);
                        }
                        break;

                    }
                    case NodeTypeEnums.FromNode: {
                        if ( stagedLeftTuples != null ) {
                            synchronized ( stagedLeftTuples ) {
                                pFromNode.doNode((FromNode) node, (FromMemory) nodeMem, sink,
                                                 wm, srcTuples, trgTuples, stagedLeftTuples);
                            }
                        } else {
                            pFromNode.doNode((FromNode) node, (FromMemory) nodeMem, sink,
                                             wm, srcTuples, trgTuples, stagedLeftTuples);
                        }
                        break;
                    }
                    case NodeTypeEnums.QueryElementNode: {
                        boolean exitInnerEval =  evalQueryNode(liaNode, pmem, node, nodeMem, smems, smemIndex, trgTuples, wm, stack, visitedRules, srcTuples, sink);
                        if ( exitInnerEval ) {
                            return; // Queries exists and has placed StackEntry on the Stack
                        }
                        break;
                    }
                    case NodeTypeEnums.TimerConditionNode: {
                        if ( stagedLeftTuples != null ) {
                            synchronized ( stagedLeftTuples ) {
                                pTimerNode.doNode( (TimerNode) node, (TimerNodeMemory) nodeMem, pmem, sink, wm, srcTuples, trgTuples, stagedLeftTuples);
                            }
                        } else {
                            pTimerNode.doNode( (TimerNode) node, (TimerNodeMemory) nodeMem, pmem, sink, wm, srcTuples, trgTuples, stagedLeftTuples);
                        }
                        break;
                    }
                    case NodeTypeEnums.ConditionalBranchNode: {
                        if ( stagedLeftTuples != null ) {
                            synchronized ( stagedLeftTuples ) {
                                pBranchNode.doNode((ConditionalBranchNode) node, (ConditionalBranchMemory) nodeMem, sink,
                                                   wm, srcTuples, trgTuples, stagedLeftTuples, executor);
                            }
                        } else {
                            pBranchNode.doNode((ConditionalBranchNode) node, (ConditionalBranchMemory) nodeMem, sink,
                                               wm, srcTuples, trgTuples, stagedLeftTuples, executor);
                        }
                        break;
                    }
                }
            }

            if (node != smem.getTipNode()) {
                // get next node and node memory in the segment
                node = sink;
                nodeMem = nodeMem.getNext();
            } else {
                // Reached end of segment, start on new segment.
                synchronized ( stagedLeftTuples ) {
                    // end of SegmentMemory, so we know that stagedLeftTuples is not null
                    SegmentPropagator.propagate(smem,
                                                trgTuples,
                                                wm);
                    smem = smems[++smemIndex];
                    trgTuples = smem.getStagedLeftTuples().takeAll();
                }

                if (log.isTraceEnabled()) {
                    log.trace("Segment {}", smemIndex);
                }
                node = (LeftTupleSink) smem.getRootNode();
                nodeMem = smem.getNodeMemories().getFirst();
            }
            processRian = true; //  make sure it's reset, so ria nodes are processed
        }
    }

    private boolean evalQueryNode(LeftInputAdapterNode liaNode, PathMemory pmem, NetworkNode node, Memory nodeMem, SegmentMemory[] smems, int smemIndex, LeftTupleSets trgTuples, InternalWorkingMemory wm, LinkedList<StackEntry> stack, Set<String> visitedRules, LeftTupleSets srcTuples, LeftTupleSinkNode sink) {
        QueryElementNodeMemory qmem = (QueryElementNodeMemory) nodeMem;

        if (srcTuples.isEmpty() && qmem.getResultLeftTuples().isEmpty()) {
            // no point in evaluating query element, and setting up stack, if there is nothing to process
            return false;
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
            StackEntry stackEntry = new StackEntry(liaNode,node, sink, pmem, nodeMem, smems,
                                                   smemIndex, trgTuples, visitedRules, true);

            stack.add(stackEntry);

            pQueryNode.doNode(qnode, (QueryElementNodeMemory) nodeMem, stackEntry, sink,
                              wm, srcTuples);

            SegmentMemory qsmem = ((QueryElementNodeMemory) nodeMem).getQuerySegmentMemory();
            List<PathMemory> qpmems = qsmem.getPathMemories();

            // Build the evaluation information for each 'or' branch
            for (int i = 0; i < qpmems.size() ; i++) {
                PathMemory qpmem = qpmems.get(i);

                pmem = qpmem;
                smems = qpmem.getSegmentMemories();
                smemIndex = 0;
                SegmentMemory smem = smems[smemIndex]; // 0
                liaNode = (LeftInputAdapterNode) smem.getRootNode();

                if (liaNode == smem.getTipNode()) {
                    // segment only has liaNode in it
                    // nothing is staged in the liaNode, so skip to next segment
                    smem = smems[++smemIndex]; // 1
                    node = smem.getRootNode();
                    nodeMem = smem.getNodeMemories().getFirst();
                } else {
                    // lia is in shared segment, so point to next node
                    node = liaNode.getSinkPropagator().getFirstLeftTupleSink();
                    nodeMem = smem.getNodeMemories().getFirst().getNext(); // skip the liaNode memory
                }

                trgTuples = smem.getStagedLeftTuples();
                stackEntry = new StackEntry(liaNode,node, null, pmem,
                                            nodeMem, smems, smemIndex,
                                            trgTuples, visitedRules, false);
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

    private boolean evalBetaNode(LeftInputAdapterNode liaNode, PathMemory pmem, NetworkNode node, Memory nodeMem, SegmentMemory[] smems, int smemIndex, LeftTupleSets trgTuples, InternalWorkingMemory wm, LinkedList<StackEntry> stack, LinkedList<StackEntry> outerStack, Set<String> visitedRules, boolean processRian, RuleExecutor executor,
                                 LeftTupleSets srcTuples, LeftTupleSets stagedLeftTuples, LeftTupleSinkNode sink) {BetaNode betaNode = (BetaNode) node;

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
            doRiaNode( wm, liaNode, pmem, srcTuples,
                      betaNode, sink, smems, smemIndex, nodeMem, bm, stack, outerStack, visitedRules, executor);
            return true; // return here, doRiaNode queues the evaluation on the stack, which is necessary to handled nested query nodes
        }

        if ( stagedLeftTuples != null ) {
            synchronized ( stagedLeftTuples ) {
                switchOnDoBetaNode(node, trgTuples, wm, srcTuples, stagedLeftTuples, sink, bm, am);
            }
        } else {
            switchOnDoBetaNode(node, trgTuples, wm, srcTuples, stagedLeftTuples, sink, bm, am);
        }


        return false;
    }

    private void switchOnDoBetaNode(NetworkNode node, LeftTupleSets trgTuples, InternalWorkingMemory wm, LeftTupleSets srcTuples, LeftTupleSets stagedLeftTuples, LeftTupleSinkNode sink, BetaMemory bm, AccumulateMemory am) {
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

    private void doRiaNode(InternalWorkingMemory wm,
                           LeftInputAdapterNode liaNode,
                           PathMemory pmem,
                           LeftTupleSets srcTuples,
                           BetaNode betaNode,
                           LeftTupleSinkNode sink,
                           SegmentMemory[] smems,
                           int smemIndex,
                           Memory nodeMem,
                           BetaMemory bm,
                           LinkedList<StackEntry> stack,
                           LinkedList<StackEntry> outerStack,
                           Set<String> visitedRules,
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
        StackEntry stackEntry = new StackEntry(liaNode, betaNode, sink, pmem, nodeMem, smems,
                                               smemIndex, srcTuples, visitedRules, false);
        stack.add(stackEntry);
        if (log.isTraceEnabled()) {
            int offset = getOffset(betaNode);
            log.trace("{} RiaQueue {} {}", indent(offset), betaNode.toString(), srcTuples.toStringSizes());
        }

        innerEval(liaNode, pathMem, subSmem.getRootNode(), subSmem.getNodeMemories().getFirst(),
                  subnetworkSmems, subSmem.getPos(),
                  subSmem.getStagedLeftTuples(), wm, stack, outerStack, visitedRules, true, executor);
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

        RiaNodeMemory rnm = (RiaNodeMemory) wm.getNodeMemory( riaNode );

        length--; // subtract one, as first is not in the array;
        for (LeftTuple leftTuple = srcTuples.getInsertFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            PropagationContext pctx = leftTuple.getPropagationContext();
            InternalFactHandle handle = riaNode.createFactHandle(leftTuple, pctx, wm);
        
            // this is required for serialization support
            rnm.getMap().put( leftTuple, handle );

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

            // this is required for serialization support
            rnm.getMap().remove( leftTuple );

            RightTuple rightTuple = (RightTuple) leftTuple.getObject();
            RightTupleSets rightTuples = bm.getStagedRightTuples();
            rightTuples.addDelete(rightTuple);

            if (bns != null) {
                // Add peered RightTuples, they are attached to FH - unlink LeftTuples that has a peer ref
                for (int i = 0; i < length; i++) {
                    rightTuple = rightTuple.getHandleNext();
                    rightTuples = bms[i].getStagedRightTuples();
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
            rightTuples.addUpdate(rightTuple);

            if (bns != null) {
                // Add peered RightTuples, they are attached to FH - unlink LeftTuples that has a peer ref
                for (int i = 0; i < length; i++) {
                    rightTuple = rightTuple.getHandleNext();
                    rightTuples = bms[i].getStagedRightTuples();
                    rightTuples.addUpdate(rightTuple);
                }
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }

        srcTuples.resetAll();
    }

    public boolean isRuleExecutor() {
        return true;
    }

    public static void findLeftTupleBlocker(BetaNode betaNode, RightTupleMemory rtm,
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
            ltm.remove(leftTuple);
            leftTuple = next;
        }

        for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();
            ltm.add(leftTuple);
            for (LeftTuple childLeftTuple = leftTuple.getFirstChild(); childLeftTuple != null; ) {
                LeftTuple childNext = childLeftTuple.getLeftParentNext();
                childLeftTuple.reAddRight();
                childLeftTuple = childNext;
            }
            leftTuple = next;
        }
    }

    public static void dpUpdatesExistentialReorderLeftMemory(BetaMemory bm,
                                                  LeftTupleSets srcLeftTuples) {
        LeftTupleMemory ltm = bm.getLeftTupleMemory();

        // sides must first be re-ordered, to ensure iteration integrity
        for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();
            if ( leftTuple.getMemory() != null ) {
                ltm.remove(leftTuple);
            }
            leftTuple = next;
        }

        for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();
            RightTuple blocker = leftTuple.getBlocker();
            if ( blocker == null ) {
                ltm.add(leftTuple);
                for (LeftTuple childLeftTuple = leftTuple.getFirstChild(); childLeftTuple != null; ) {
                    LeftTuple childNext = childLeftTuple.getLeftParentNext();
                    childLeftTuple.reAddRight();
                    childLeftTuple = childNext;
                }
            } else if ( blocker.getStagedType() != LeftTuple.NONE ) {
                // it's blocker is also being updated, so remove to force it to start from the beginning
                blocker.removeBlocked( leftTuple );
            }
            leftTuple = next;
        }
    }

    public static void dpUpdatesReorderRightMemory(BetaMemory bm,
                                                   RightTupleSets srcRightTuples) {
        RightTupleMemory rtm = bm.getRightTupleMemory();

        for (RightTuple rightTuple = srcRightTuples.getUpdateFirst(); rightTuple != null; ) {
            RightTuple next = rightTuple.getStagedNext();
            if ( rightTuple.getMemory() != null ) {
                rightTuple.setTempRightTupleMemory(rightTuple.getMemory());
                rtm.remove(rightTuple);
            }
            rightTuple = next;
        }

        for (RightTuple rightTuple = srcRightTuples.getUpdateFirst(); rightTuple != null; ) {
            RightTuple next = rightTuple.getStagedNext();
            if ( rightTuple.getTempRightTupleMemory() != null ) {
                rtm.add(rightTuple);
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

        boolean resumeFromCurrent = !(betaNode.isIndexedUnificationJoin() || rtm.getIndexType().isComparison());

        // remove all the staged rightTuples from the memory before to readd them all
        // this is to avoid split bucket when an updated rightTuple hasn't been moved yet
        // and so it is the first entry in the wrong bucket

        for (RightTuple rightTuple = srcRightTuples.getUpdateFirst(); rightTuple != null; ) {
            RightTuple next = rightTuple.getStagedNext();
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
                rightTuple.nullBlocked();
                rtm.remove(rightTuple);
            }
            rightTuple = next;
        }

        for (RightTuple rightTuple = srcRightTuples.getUpdateFirst(); rightTuple != null; ) {
            RightTuple next = rightTuple.getStagedNext();
            if ( rightTuple.getTempRightTupleMemory() != null ) {

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
