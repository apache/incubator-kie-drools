package org.drools.core.phreak;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.NetworkNode;
import org.drools.core.common.StreamTupleEntryQueue;
import org.drools.core.common.SynchronizedLeftTupleSets;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.reteoo.AccumulateNode;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.CompositeObjectSinkAdapter;
import org.drools.core.reteoo.ConditionalBranchNode;
import org.drools.core.reteoo.ConditionalBranchNode.ConditionalBranchMemory;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.EvalConditionNode;
import org.drools.core.reteoo.EvalConditionNode.EvalMemory;
import org.drools.core.reteoo.FromNode;
import org.drools.core.reteoo.FromNode.FromMemory;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.LeftTupleSinkNode;
import org.drools.core.reteoo.LeftTupleSinkPropagator;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.NotNode;
import org.drools.core.reteoo.ObjectSink;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.QueryElementNode.QueryElementNodeMemory;
import org.drools.core.reteoo.RiaPathMemory;
import org.drools.core.reteoo.RightInputAdapterNode;
import org.drools.core.reteoo.RightInputAdapterNode.RiaNodeMemory;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.reteoo.TimerNode;
import org.drools.core.reteoo.TimerNode.TimerNodeMemory;
import org.drools.core.rule.constraint.QueryNameConstraint;
import org.drools.core.util.Iterator;
import org.drools.core.util.ObjectHashMap.ObjectEntry;

public class SegmentUtilities {

    //    public static RightInputAdapterNode getOuterMostRiaNode(RightInputAdapterNode riaNode, LeftTupleSource startLTs) {
    //        if ( riaNode.getStartTupleSource() != startLTs ) {
    //            // This is a nested subnetwork, so we know there must be atleast one outer subnetwork
    //            LeftTupleSource lts = riaNode.getLeftTupleSource();
    //            while ( true ) {
    //                if ( NodeTypeEnums.isBetaNode(lts) && (( BetaNode )lts).isRightInputIsRiaNode() ) {
    //                    return getOuterMostRiaNode( ( RightInputAdapterNode ) ((BetaNode)lts).getRightInput(), startLTs );
    //                }
    //                lts = lts.getLeftTupleSource();
    //            }
    //        } else {
    //            return riaNode;
    //        }
    //    }

    /**
     * Initialises the NodeSegment memory for all nodes in the segment.
     *
     * @param wm
     */
    public static SegmentMemory createSegmentMemory(LeftTupleSource tupleSource,
                                                    final InternalWorkingMemory wm) {
        synchronized (wm) {
            SegmentMemory smem = wm.getNodeMemory((MemoryFactory) tupleSource).getSegmentMemory();
            if ( smem != null ) {
                return smem; // this can happen when multiple threads are trying to initialize the segment
            }

            // find segment root
            while (tupleSource.getType() != NodeTypeEnums.LeftInputAdapterNode &&
                   SegmentUtilities.parentInSameSegment(tupleSource, null)) {
                tupleSource = tupleSource.getLeftTupleSource();
            }

            LeftTupleSource segmentRoot = tupleSource;
            int nodeTypesInSegment = 0;

            smem = restoreSegmentFromPrototype(wm, segmentRoot, nodeTypesInSegment);
            if ( smem != null ) {
                return smem;
            }

            smem = new SegmentMemory(segmentRoot);

            // Iterate all nodes on the same segment, assigning their position as a bit mask value
            // allLinkedTestMask is the resulting mask used to test if all nodes are linked in
            long nodePosMask = 1;
            long allLinkedTestMask = 0;
            boolean updateNodeBit = true;  // nodes after a branch CE can notify, but they cannot impact linking

            while (true) {
                nodeTypesInSegment = updateNodeTypesMask(tupleSource, nodeTypesInSegment);
                if ( tupleSource.isStreamMode() && smem.getStreamQueue() == null ) {
                    // need to make sure there is one Queue, for the rule, when a stream mode node is found.

                    StreamTupleEntryQueue queue = initAndGetTupleQueue(tupleSource, wm);
                    smem.setStreamQueue( queue );
                }
                if (NodeTypeEnums.isBetaNode(tupleSource)) {
                    allLinkedTestMask = processBetaNode(tupleSource, wm, smem, nodePosMask, allLinkedTestMask, updateNodeBit);
                } else {
                    switch (tupleSource.getType()) {
                        case NodeTypeEnums.LeftInputAdapterNode:
                            allLinkedTestMask = processLiaNode((LeftInputAdapterNode) tupleSource, wm, smem, nodePosMask, allLinkedTestMask);
                            break;
                        case NodeTypeEnums.EvalConditionNode:
                            processEvalNode((EvalConditionNode) tupleSource, wm, smem);
                            break;
                        case NodeTypeEnums.ConditionalBranchNode:
                            updateNodeBit = processBranchNode((ConditionalBranchNode) tupleSource, wm, smem);
                            break;
                        case NodeTypeEnums.FromNode:
                            processFromNode((FromNode) tupleSource, wm, smem);
                            break;
                        case NodeTypeEnums.TimerConditionNode:
                            processTimerNode((TimerNode) tupleSource, wm, smem, nodePosMask);
                            break;
                        case NodeTypeEnums.QueryElementNode:
                            updateNodeBit = processQueryNode((QueryElementNode) tupleSource, wm, segmentRoot, smem, nodePosMask);
                            break;
                    }
                }
                nodePosMask = nodePosMask << 1;

                if (tupleSource.getSinkPropagator().size() == 1) {
                    LeftTupleSinkNode sink = (LeftTupleSinkNode) tupleSource.getSinkPropagator().getFirstLeftTupleSink();
                    if (NodeTypeEnums.isLeftTupleSource(sink)) {
                        tupleSource = (LeftTupleSource) sink;
                    } else {
                        // rtn or rian
                        // While not technically in a segment, we want to be able to iterate easily from the last node memory to the ria/rtn memory
                        // we don't use createNodeMemory, as these may already have been created by, but not added, by the method updateRiaAndTerminalMemory
                        Memory memory = wm.getNodeMemory((MemoryFactory) sink);
                        if (sink.getType() == NodeTypeEnums.RightInputAdaterNode) {
                            PathMemory riaPmem = ((RiaNodeMemory)memory).getRiaPathMemory();
                            smem.getNodeMemories().add( riaPmem );

                            RightInputAdapterNode rian = ( RightInputAdapterNode ) sink;
                            ObjectSink[] nodes = rian.getSinkPropagator().getSinks();
                            for ( ObjectSink node : nodes ) {
                                if ( NodeTypeEnums.isLeftTupleSource(node) )  {
                                    SegmentMemory parentSmem = createSegmentMemory( (LeftTupleSource) node, wm );
                                }
                            }
                        } else if (NodeTypeEnums.isTerminalNode(sink)) {
                            smem.getNodeMemories().add((PathMemory)memory);
                        }
                        memory.setSegmentMemory(smem);
                        smem.setTipNode(sink);
                        break;
                    }
                } else {
                    // not in same segment
                    smem.setTipNode(tupleSource);
                    break;
                }
            }
            smem.setAllLinkedMaskTest(allLinkedTestMask);

            // iterate to find root and determine the SegmentNodes position in the RuleSegment
            LeftTupleSource pathRoot = segmentRoot;
            int ruleSegmentPosMask = 1;
            int counter = 0;
            while (pathRoot.getType() != NodeTypeEnums.LeftInputAdapterNode) {
                if (!SegmentUtilities.parentInSameSegment(pathRoot, null)) {
                    // for each new found segment, increase the mask bit position
                    ruleSegmentPosMask = ruleSegmentPosMask << 1;
                    counter++;
                }
                pathRoot = pathRoot.getLeftTupleSource();
            }
            smem.setSegmentPosMaskBit(ruleSegmentPosMask);
            smem.setPos(counter);

            if (smem.getRootNode().getType() != NodeTypeEnums.LeftInputAdapterNode &&
                ((LeftTupleSource)smem.getRootNode()).getLeftTupleSource().getType() == NodeTypeEnums.LeftInputAdapterNode ) {
                    // If LiaNode is in it's own segment, then the segment first after that must use SynchronizedLeftTupleSets
                    smem.setStagedTuples( new SynchronizedLeftTupleSets() );
            }

            nodeTypesInSegment = updateRiaAndTerminalMemory(tupleSource, tupleSource, smem, wm, false, nodeTypesInSegment);

            ((KnowledgeBaseImpl)wm.getKnowledgeBase()).registerSegmentPrototype(segmentRoot, smem);

            return smem;
        }
    }

    private static SegmentMemory restoreSegmentFromPrototype(InternalWorkingMemory wm, LeftTupleSource segmentRoot, int nodeTypesInSegment) {
        SegmentMemory smem = ((KnowledgeBaseImpl)wm.getKnowledgeBase()).getSegmentFromPrototype(wm, segmentRoot);
        if ( smem != null ) {
            // there is a prototype for this segment memory
            for (NetworkNode node : smem.getNodesInSegment()) {
                wm.getNodeMemory((MemoryFactory) node).setSegmentMemory(smem);
            }
            nodeTypesInSegment = updateRiaAndTerminalMemory(segmentRoot, segmentRoot, smem, wm, true, nodeTypesInSegment);
        }
        return smem;
    }

    private static boolean processQueryNode(QueryElementNode tupleSource, InternalWorkingMemory wm, LeftTupleSource segmentRoot, SegmentMemory smem, long nodePosMask) {
        // Initialize the QueryElementNode and have it's memory reference the actual query SegmentMemory
        QueryElementNode queryNode = (QueryElementNode) tupleSource;
        SegmentMemory querySmem = getQuerySegmentMemory(wm, segmentRoot, queryNode);
        QueryElementNodeMemory queryNodeMem = (QueryElementNodeMemory) smem.createNodeMemory(queryNode, wm);
        queryNodeMem.setNodePosMaskBit(nodePosMask);
        queryNodeMem.setQuerySegmentMemory(querySmem);
        queryNodeMem.setSegmentMemory(smem);
        return ! queryNode.getQueryElement().isAbductive();
    }

    public static SegmentMemory getQuerySegmentMemory(InternalWorkingMemory wm, LeftTupleSource segmentRoot, QueryElementNode queryNode) {
        LeftInputAdapterNode liaNode = getQueryLiaNode(queryNode.getQueryElement().getQueryName(), getQueryOtn(segmentRoot));
        LiaNodeMemory liam = (LiaNodeMemory) wm.getNodeMemory((MemoryFactory) liaNode);
        SegmentMemory querySmem = liam.getSegmentMemory();
        if (querySmem == null) {
            querySmem = createSegmentMemory(liaNode, wm);
        }
        return querySmem;
    }

    private static void processFromNode(FromNode tupleSource, InternalWorkingMemory wm, SegmentMemory smem) {
        FromMemory fromMemory = (FromMemory) smem.createNodeMemory((FromNode) tupleSource, wm);
        fromMemory.getBetaMemory().setSegmentMemory(smem);
    }

    private static boolean processBranchNode(ConditionalBranchNode tupleSource, InternalWorkingMemory wm, SegmentMemory smem) {
        boolean updateNodeBit;
        ConditionalBranchMemory branchMem = (ConditionalBranchMemory) smem.createNodeMemory((ConditionalBranchNode) tupleSource, wm);
        branchMem.setSegmentMemory(smem);
        updateNodeBit = false; // nodes after a branch CE can notify, but they cannot impact linking
        return updateNodeBit;
    }

    private static void processEvalNode(EvalConditionNode tupleSource, InternalWorkingMemory wm, SegmentMemory smem) {
        EvalMemory evalMem = (EvalMemory) smem.createNodeMemory((EvalConditionNode) tupleSource, wm);
        evalMem.setSegmentMemory(smem);
    }

    private static void processTimerNode(TimerNode tupleSource, InternalWorkingMemory wm, SegmentMemory smem, long nodePosMask) {
        TimerNodeMemory tnMem = (TimerNodeMemory) smem.createNodeMemory( ( TimerNode ) tupleSource, wm );
        tnMem.setNodePosMaskBit(nodePosMask);
        tnMem.setSegmentMemory(smem);
    }

    private static long processLiaNode(LeftInputAdapterNode tupleSource, InternalWorkingMemory wm, SegmentMemory smem, long nodePosMask, long allLinkedTestMask) {
        LiaNodeMemory liaMemory = (LiaNodeMemory) smem.createNodeMemory((LeftInputAdapterNode) tupleSource, wm);
        smem.setStagedTuples( new SynchronizedLeftTupleSets() ); // LiaNode SegmentMemory must have Synchronized LeftTupleSets
        liaMemory.setSegmentMemory(smem);
        liaMemory.setNodePosMaskBit(nodePosMask);
        allLinkedTestMask = allLinkedTestMask | nodePosMask;
        return allLinkedTestMask;
    }

    private static long processBetaNode(LeftTupleSource tupleSource, InternalWorkingMemory wm, SegmentMemory smem, long nodePosMask, long allLinkedTestMask, boolean updateNodeBit) {
        BetaMemory bm;
        BetaNode betaNode = (BetaNode) tupleSource;
        if (NodeTypeEnums.AccumulateNode == tupleSource.getType()) {
            bm = ((AccumulateMemory) smem.createNodeMemory((AccumulateNode) tupleSource, wm)).getBetaMemory();
        } else {
            bm = (BetaMemory) smem.createNodeMemory(betaNode, wm);

        }
        // this must be set first, to avoid recursion as sub networks can be initialised multiple ways
        // and bm.getSegmentMemory == null check can be used to avoid recursion.
        bm.setSegmentMemory(smem);

        if (betaNode.isRightInputIsRiaNode()) {
            // Iterate to find outermost rianode
            RightInputAdapterNode riaNode = (RightInputAdapterNode) betaNode.getRightInput();
            //riaNode = getOuterMostRiaNode(riaNode, betaNode.getLeftTupleSource());

            // Iterat
            LeftTupleSource subnetworkLts = riaNode.getLeftTupleSource();
            while (subnetworkLts.getLeftTupleSource() != riaNode.getStartTupleSource()) {
                subnetworkLts = subnetworkLts.getLeftTupleSource();
            }

            Memory rootSubNetwokrMem = wm.getNodeMemory((MemoryFactory) subnetworkLts);
            SegmentMemory subNetworkSegmentMemory = rootSubNetwokrMem.getSegmentMemory();
            if (subNetworkSegmentMemory == null) {
                // we need to stop recursion here
                createSegmentMemory((LeftTupleSource) subnetworkLts, wm);
            }

            RiaNodeMemory riaMem = (RiaNodeMemory) wm.getNodeMemory((MemoryFactory) riaNode);
            bm.setRiaRuleMemory(riaMem.getRiaPathMemory());
            if (updateNodeBit && riaMem.getRiaPathMemory().getAllLinkedMaskTest() > 0) {
                // only ria's with reactive subnetworks can be disabled and thus need checking
                allLinkedTestMask = allLinkedTestMask | nodePosMask;
            }
        } else if (updateNodeBit &&
                   (!(NodeTypeEnums.NotNode == tupleSource.getType() && !((NotNode) tupleSource).isEmptyBetaConstraints()) &&
                    NodeTypeEnums.AccumulateNode != tupleSource.getType())) {
            // non empty not nodes and accumulates can never be disabled and thus don't need checking
            allLinkedTestMask = allLinkedTestMask | nodePosMask;

        }
        bm.setNodePosMaskBit(nodePosMask);
        if (NodeTypeEnums.NotNode == tupleSource.getType()) {
            // not nodes start up linked in
            smem.linkNodeWithoutRuleNotify(bm.getNodePosMaskBit());
        }
        return allLinkedTestMask;
    }

    public static void createChildSegments(final InternalWorkingMemory wm,
                                           SegmentMemory smem,
                                           LeftTupleSinkPropagator sinkProp) {
        synchronized (smem) {
            if ( !smem.isEmpty() ) {
                  return; // this can happen when multiple threads are trying to initialize the segment
            }
            for (LeftTupleSinkNode sink = (LeftTupleSinkNode) sinkProp.getFirstLeftTupleSink(); sink != null; sink = sink.getNextLeftTupleSinkNode()) {
                Memory memory = wm.getNodeMemory((MemoryFactory) sink);

                SegmentMemory childSmem = createChildSegment(wm, sink, memory);
                smem.add(childSmem);
            }
        }
    }

    public static SegmentMemory createChildSegment(InternalWorkingMemory wm, LeftTupleSink sink, Memory memory) {
        if (!(NodeTypeEnums.isTerminalNode(sink) || sink.getType() == NodeTypeEnums.RightInputAdaterNode)) {
            if (memory.getSegmentMemory() == null) {
                SegmentUtilities.createSegmentMemory((LeftTupleSource) sink, wm);
            }
        } else {
            // RTNS and RiaNode's have their own segment, if they are the child of a split.
            if (memory.getSegmentMemory() == null) {
                SegmentMemory childSmem = new SegmentMemory(sink); // rtns or riatns don't need a queue
                if ( sink.getLeftTupleSource().getType() == NodeTypeEnums.LeftInputAdapterNode ) {
                    // If LiaNode is in it's own segment, then the segment first after that must use SynchronizedLeftTupleSets
                    childSmem.setStagedTuples( new SynchronizedLeftTupleSets() );
                }

                PathMemory pmem;
                if (NodeTypeEnums.isTerminalNode(sink)) {
                    pmem = (PathMemory) memory;
                } else {
                    pmem = ((RiaNodeMemory) memory).getRiaPathMemory();
                }
                pmem.getSegmentMemories()[pmem.getSegmentMemories().length - 1] = childSmem;
                pmem.setSegmentMemory(childSmem);
                childSmem.getPathMemories().add(pmem);

                childSmem.setTipNode(sink);
                childSmem.setSinkFactory(sink);
            }
        }
        return memory.getSegmentMemory();
    }

    /**
     * Is the LeftTupleSource a node in the sub network for the RightInputAdapterNode
     * To be in the same network, it must be a node is after the two output of the parent
     * and before the rianode.
     *
     * @param riaNode
     * @param leftTupleSource
     * @return
     */
    public static boolean inSubNetwork(RightInputAdapterNode riaNode, LeftTupleSource leftTupleSource) {
        LeftTupleSource startTupleSource = riaNode.getStartTupleSource();
        LeftTupleSource parent = riaNode.getLeftTupleSource();

        while (parent != startTupleSource) {
            if (parent == leftTupleSource) {
                return true;
            }
            parent = parent.getLeftTupleSource();
        }

        return false;
    }

    /**
     * This adds the segment memory to the terminal node or ria node's list of memories.
     * In the case of the terminal node this allows it to know that all segments from
     * the tip to root are linked.
     * In the case of the ria node its all the segments up to the start of the subnetwork.
     * This is because the rianode only cares if all of it's segments are linked, then
     * it sets the bit of node it is the right input for.
     *
     * @param lt
     * @param originalLt
     * @param smem
     * @param wm
     */
    private static int updateRiaAndTerminalMemory( LeftTupleSource lt,
                                                    LeftTupleSource originalLt,
                                                    SegmentMemory smem,
                                                    InternalWorkingMemory wm,
                                                    boolean fromPrototype,
                                                    int nodeTypesInSegment ) {

        nodeTypesInSegment = checkSegmentBoundary(lt, wm, nodeTypesInSegment);

        for (LeftTupleSink sink : lt.getSinkPropagator().getSinks()) {
            if (NodeTypeEnums.isLeftTupleSource(sink)) {
                nodeTypesInSegment = updateRiaAndTerminalMemory((LeftTupleSource) sink, originalLt, smem, wm, fromPrototype, nodeTypesInSegment);
            } else if (sink.getType() == NodeTypeEnums.RightInputAdaterNode) {
                // Only add the RIANode, if the LeftTupleSource is part of the RIANode subnetwork.
                if (inSubNetwork((RightInputAdapterNode) sink, originalLt)) {
                    RiaNodeMemory riaMem = (RiaNodeMemory) wm.getNodeMemory((MemoryFactory) sink);
                    PathMemory pmem = (PathMemory) riaMem.getRiaPathMemory();
                    smem.getPathMemories().add(pmem);
                    pmem.getSegmentMemories()[smem.getPos()] = smem;

                    if (fromPrototype) {
                        ObjectSink[] nodes = ((RightInputAdapterNode) sink).getSinkPropagator().getSinks();
                        for ( ObjectSink node : nodes ) {
                            // check if the SegmentMemory has been already created by the BetaNode and if so avoid to build it twice
                            if ( NodeTypeEnums.isLeftTupleSource(node) && wm.getNodeMemory((MemoryFactory) node).getSegmentMemory() == null )  {
                                restoreSegmentFromPrototype(wm, (LeftTupleSource) node, nodeTypesInSegment);
                            }
                        }
                    } else if ( ( pmem.getAllLinkedMaskTest() & ( 1L << pmem.getSegmentMemories().length ) ) == 0 ) {
                        // must eagerly initialize child segment memories
                        ObjectSink[] nodes = ((RightInputAdapterNode) sink).getSinkPropagator().getSinks();
                        for ( ObjectSink node : nodes ) {
                            if ( NodeTypeEnums.isLeftTupleSource(node) )  {
                                createSegmentMemory( (LeftTupleSource) node, wm );
                            }
                        }
                    }
                }
            } else if (NodeTypeEnums.isTerminalNode(sink)) {
                PathMemory pmem = (PathMemory) wm.getNodeMemory((MemoryFactory) sink);
                smem.getPathMemories().add(pmem);
                pmem.getSegmentMemories()[smem.getPos()] = smem;
                smem.setStreamQueue( pmem.getStreamQueue() );
                if (smem.isSegmentLinked()) {
                    // not's can cause segments to be linked, and the rules need to be notified for evaluation
                    smem.notifyRuleLinkSegment(wm);
                }
                checkEagerSegmentCreation(((TerminalNode) sink).getLeftTupleSource(), wm, nodeTypesInSegment);
            }
        }
        return nodeTypesInSegment;
    }

    private static int checkSegmentBoundary(LeftTupleSource lt, InternalWorkingMemory wm, int nodeTypesInSegment) {
        if ( !parentInSameSegment( lt, null ) )  {
            // we are in a new child segment
            checkEagerSegmentCreation(lt.getLeftTupleSource(), wm, nodeTypesInSegment);
            nodeTypesInSegment = 0;
        }
        return updateNodeTypesMask(lt, nodeTypesInSegment);
    }

    private static void checkEagerSegmentCreation(LeftTupleSource lt, InternalWorkingMemory wm, int nodeTypesInSegment) {
        // A Not node has to be eagerly initialized unless in its segment there is at least a join or exists node
        if ( isSet(nodeTypesInSegment, NOT_NODE_BIT) &&
             !isSet(nodeTypesInSegment, JOIN_NODE_BIT) &&
             !isSet(nodeTypesInSegment, EXISTS_NODE_BIT) ) {
            createSegmentMemory(lt, wm);
        }
    }

    public static StreamTupleEntryQueue initAndGetTupleQueue(NetworkNode node, InternalWorkingMemory wm) {
        // get's or initializes the queue, if it does not exist. It recurse to the outer most PathMemory
        // and then trickle the Queue back up to the inner PathMememories
        LeftTupleSink sink = null;
        if ( !(NodeTypeEnums.isTerminalNode(node) || NodeTypeEnums.RightInputAdaterNode == node.getType()) ) {
            // iterate to the terminal of this segment - either rian or rtn
            sink = ((LeftTupleSource)node).getSinkPropagator().getLastLeftTupleSink();
            while ( sink.getType() != NodeTypeEnums.RightInputAdaterNode && !NodeTypeEnums.isTerminalNode( sink) ) {
                sink = ((LeftTupleSource)sink).getSinkPropagator().getLastLeftTupleSink();
            }
        }  else {
            sink = (LeftTupleSink)node;
        }

        StreamTupleEntryQueue queue = null;
        if (NodeTypeEnums.RightInputAdaterNode == sink.getType()) {
            RightInputAdapterNode rian = (RightInputAdapterNode) sink;
            RiaNodeMemory riaMem =  (RiaNodeMemory) wm.getNodeMemory((MemoryFactory)sink);
            RiaPathMemory pmem = riaMem.getRiaPathMemory();

            queue = pmem.getStreamQueue();
            if ( queue == null ) {
                ObjectSink[] nodes = rian.getSinkPropagator().getSinks();
                // iterate the first child sink, we only need the first, as all reach the same outer rtn
                queue = initAndGetTupleQueue((LeftTupleSource) nodes[0], wm);
            }
        } else if (NodeTypeEnums.isTerminalNode(sink)) {
            PathMemory pmem =  (PathMemory) wm.getNodeMemory((MemoryFactory) sink);
            queue =  pmem.getStreamQueue();
            if ( queue == null ) {
                pmem.initQueue();
                queue = pmem.getStreamQueue();
            }
        }
        return queue;

    }

    public static boolean parentInSameSegment(LeftTupleSource lt, RuleImpl removingRule) {
        LeftTupleSource parentLt = lt.getLeftTupleSource();
        if (parentLt == null) {
            return false;
        }
        int size = parentLt.getSinkPropagator().size();

        if (removingRule != null && size == 2 && parentLt.getAssociations().containsKey(removingRule)) {
            // looks like a split, but one of the branches may be removed.

            LeftTupleSink first = parentLt.getSinkPropagator().getFirstLeftTupleSink();
            if (first.getAssociations().size() == 1 && first.getAssociations().containsKey(removingRule)) {
                return true;
            }

            LeftTupleSink last = parentLt.getSinkPropagator().getLastLeftTupleSink();
            return last.getAssociations().size() == 1 && last.getAssociations().containsKey(removingRule);
        } else {
            return size == 1;
        }

        // comments out for now, as the optimization to preserve subnetwork segments down one side is troublesome.
        //        LeftTupleSource parent = lt.getLeftTupleSource();
        //        if ( parent != null && ( parent.getSinkPropagator().size() == 1 ||
        //               // same segment, if it's a subnetwork split and we are on the non subnetwork side of the split
        //             ( parent.getSinkPropagator().size() == 2 &&
        //               NodeTypeEnums.isBetaNode( lt ) &&
        //               ((BetaNode)lt).isRightInputIsRiaNode() ) ) ) {
        //            return true;
        //        } else {
        //            return false;
        //        }
    }

    public static ObjectTypeNode getQueryOtn(LeftTupleSource lts) {
        while (!(lts instanceof LeftInputAdapterNode)) {
            lts = lts.getLeftTupleSource();
        }

        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) lts;
        ObjectSource os = liaNode.getObjectSource();
        while (!(os instanceof EntryPointNode)) {
            os = os.getParentObjectSource();
        }

        return ((EntryPointNode) os).getQueryNode();
    }

    public static LeftInputAdapterNode getQueryLiaNode(String queryName, ObjectTypeNode queryOtn) {
        if (queryOtn.getSinkPropagator() instanceof CompositeObjectSinkAdapter) {
            CompositeObjectSinkAdapter sink = (CompositeObjectSinkAdapter) queryOtn.getSinkPropagator();
            if (sink.getHashableSinks() != null) {
                for (AlphaNode alphaNode = (AlphaNode) sink.getHashableSinks().getFirst(); alphaNode != null; alphaNode = (AlphaNode) alphaNode.getNextObjectSinkNode()) {
                    QueryNameConstraint nameConstraint = (QueryNameConstraint) alphaNode.getConstraint();
                    if (queryName.equals(nameConstraint.getQueryName())) {
                        return (LeftInputAdapterNode) alphaNode.getSinkPropagator().getSinks()[0];
                    }
                }
            }

            Iterator it = sink.getHashedSinkMap().iterator();
            for (ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next()) {
                AlphaNode alphaNode = (AlphaNode) entry.getValue();
                QueryNameConstraint nameConstraint = (QueryNameConstraint) alphaNode.getConstraint();
                if (queryName.equals(nameConstraint.getQueryName())) {
                    return (LeftInputAdapterNode) alphaNode.getSinkPropagator().getSinks()[0];
                }
            }
        } else {
            AlphaNode alphaNode = (AlphaNode) queryOtn.getSinkPropagator().getSinks()[0];
            QueryNameConstraint nameConstraint = (QueryNameConstraint) alphaNode.getConstraint();
            if (queryName.equals(nameConstraint.getQueryName())) {
                return (LeftInputAdapterNode) alphaNode.getSinkPropagator().getSinks()[0];
            }
            return (LeftInputAdapterNode) queryOtn.getSinkPropagator().getSinks()[0];
        }

        throw new RuntimeException("Unable to find query '" + queryName + "'");
    }

    private static final int NOT_NODE_BIT       = 1 << 0;
    private static final int JOIN_NODE_BIT      = 1 << 1;
    private static final int EXISTS_NODE_BIT    = 1 << 2;

    private static int updateNodeTypesMask(NetworkNode node, int mask) {
        switch (node.getType()) {
            case NodeTypeEnums.JoinNode:
                mask |= JOIN_NODE_BIT;
                break;
            case NodeTypeEnums.ExistsNode:
                mask |= EXISTS_NODE_BIT;
                break;
            case NodeTypeEnums.NotNode:
                mask |= NOT_NODE_BIT;
                break;
        }
        return mask;
    }

    public static boolean isSet(int mask, int bit) {
        return (mask & bit) == bit;
    }
}
