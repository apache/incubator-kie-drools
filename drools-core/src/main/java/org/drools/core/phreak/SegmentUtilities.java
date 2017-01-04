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

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.NetworkNode;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.ConditionalBranchNode;
import org.drools.core.reteoo.ConditionalBranchNode.ConditionalBranchMemory;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.EvalConditionNode;
import org.drools.core.reteoo.EvalConditionNode.EvalMemory;
import org.drools.core.reteoo.ExistsNode;
import org.drools.core.reteoo.FromNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.core.reteoo.LeftTupleNode;
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
import org.drools.core.reteoo.RightInputAdapterNode;
import org.drools.core.reteoo.RightInputAdapterNode.RiaNodeMemory;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.reteoo.TimerNode;
import org.drools.core.reteoo.TimerNode.TimerNodeMemory;
import org.drools.core.rule.constraint.QueryNameConstraint;

public class SegmentUtilities {

    /**
     * Initialises the NodeSegment memory for all nodes in the segment.
     */
    public static SegmentMemory createSegmentMemory(LeftTupleSource tupleSource,
                                                    final InternalWorkingMemory wm) {
        SegmentMemory smem = wm.getNodeMemory((MemoryFactory) tupleSource).getSegmentMemory();
        if ( smem != null ) {
            return smem; // this can happen when multiple threads are trying to initialize the segment
        }

        // find segment root
        while (!SegmentUtilities.isRootNode(tupleSource, null)) {
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
            if (NodeTypeEnums.isBetaNode(tupleSource)) {
                allLinkedTestMask = processBetaNode((BetaNode)tupleSource, wm, smem, nodePosMask, allLinkedTestMask, updateNodeBit);
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
                    case NodeTypeEnums.ReactiveFromNode:
                        processReactiveFromNode((MemoryFactory) tupleSource, wm, smem, nodePosMask);
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
                LeftTupleSinkNode sink = tupleSource.getSinkPropagator().getFirstLeftTupleSink();
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
                        ObjectSink[] nodes = rian.getObjectSinkPropagator().getSinks();
                        for ( ObjectSink node : nodes ) {
                            if ( NodeTypeEnums.isLeftTupleSource(node) )  {
                                createSegmentMemory( (LeftTupleSource) node, wm );
                            }
                        }
                    } else if (NodeTypeEnums.isTerminalNode(sink)) {
                        smem.getNodeMemories().add(memory);
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
            if (SegmentUtilities.isRootNode(pathRoot, null)) {
                // for each new found segment, increase the mask bit position
                ruleSegmentPosMask = ruleSegmentPosMask << 1;
                counter++;
            }
            pathRoot = pathRoot.getLeftTupleSource();
        }
        smem.setSegmentPosMaskBit(ruleSegmentPosMask);
        smem.setPos(counter);

        nodeTypesInSegment = updateRiaAndTerminalMemory(tupleSource, tupleSource, smem, wm, false, nodeTypesInSegment);

        ((KnowledgeBaseImpl)wm.getKnowledgeBase()).registerSegmentPrototype(segmentRoot, smem);

        return smem;
    }

    private static SegmentMemory restoreSegmentFromPrototype(InternalWorkingMemory wm, LeftTupleSource segmentRoot, int nodeTypesInSegment) {
        SegmentMemory smem = wm.getKnowledgeBase().createSegmentFromPrototype(wm, segmentRoot);
        if ( smem != null ) {
            // there is a prototype for this segment memory
            for (NetworkNode node : smem.getNodesInSegment()) {
                wm.getNodeMemory((MemoryFactory) node).setSegmentMemory(smem);
            }
            nodeTypesInSegment = updateRiaAndTerminalMemory(segmentRoot, segmentRoot, smem, wm, true, nodeTypesInSegment);
        }
        return smem;
    }

    private static boolean processQueryNode(QueryElementNode queryNode, InternalWorkingMemory wm, LeftTupleSource segmentRoot, SegmentMemory smem, long nodePosMask) {
        // Initialize the QueryElementNode and have it's memory reference the actual query SegmentMemory
        SegmentMemory querySmem = getQuerySegmentMemory(wm, segmentRoot, queryNode);
        QueryElementNodeMemory queryNodeMem = smem.createNodeMemory(queryNode, wm);
        queryNodeMem.setNodePosMaskBit(nodePosMask);
        queryNodeMem.setQuerySegmentMemory(querySmem);
        queryNodeMem.setSegmentMemory(smem);
        return ! queryNode.getQueryElement().isAbductive();
    }

    public static SegmentMemory getQuerySegmentMemory(InternalWorkingMemory wm, LeftTupleSource segmentRoot, QueryElementNode queryNode) {
        LeftInputAdapterNode liaNode = getQueryLiaNode(queryNode.getQueryElement().getQueryName(), getQueryOtn(segmentRoot));
        LiaNodeMemory liam = wm.getNodeMemory(liaNode);
        SegmentMemory querySmem = liam.getSegmentMemory();
        if (querySmem == null) {
            querySmem = createSegmentMemory(liaNode, wm);
        }
        return querySmem;
    }

    private static void processFromNode(MemoryFactory tupleSource, InternalWorkingMemory wm, SegmentMemory smem) {
        smem.createNodeMemory(tupleSource, wm).setSegmentMemory(smem);
    }

    private static void processReactiveFromNode(MemoryFactory tupleSource, InternalWorkingMemory wm, SegmentMemory smem, long nodePosMask) {
        FromNode.FromMemory mem = ((FromNode.FromMemory) smem.createNodeMemory(tupleSource, wm));
        mem.setSegmentMemory(smem);
        mem.setNodePosMaskBit(nodePosMask);
    }

    private static boolean processBranchNode(ConditionalBranchNode tupleSource, InternalWorkingMemory wm, SegmentMemory smem) {
        ConditionalBranchMemory branchMem = smem.createNodeMemory(tupleSource, wm);
        branchMem.setSegmentMemory(smem);
        // nodes after a branch CE can notify, but they cannot impact linking
        return false;
    }

    private static void processEvalNode(EvalConditionNode tupleSource, InternalWorkingMemory wm, SegmentMemory smem) {
        EvalMemory evalMem = smem.createNodeMemory(tupleSource, wm);
        evalMem.setSegmentMemory(smem);
    }

    private static void processTimerNode(TimerNode tupleSource, InternalWorkingMemory wm, SegmentMemory smem, long nodePosMask) {
        TimerNodeMemory tnMem = smem.createNodeMemory( tupleSource, wm );
        tnMem.setNodePosMaskBit(nodePosMask);
        tnMem.setSegmentMemory(smem);
    }

    private static long processLiaNode(LeftInputAdapterNode tupleSource, InternalWorkingMemory wm, SegmentMemory smem, long nodePosMask, long allLinkedTestMask) {
        LiaNodeMemory liaMemory = smem.createNodeMemory(tupleSource, wm);
        liaMemory.setSegmentMemory(smem);
        liaMemory.setNodePosMaskBit(nodePosMask);
        allLinkedTestMask = allLinkedTestMask | nodePosMask;
        return allLinkedTestMask;
    }

    private static long processBetaNode(BetaNode betaNode, InternalWorkingMemory wm, SegmentMemory smem, long nodePosMask, long allLinkedTestMask, boolean updateNodeBit) {
        BetaMemory bm = NodeTypeEnums.AccumulateNode == betaNode.getType() ?
                        ((AccumulateMemory) smem.createNodeMemory(betaNode, wm)).getBetaMemory() :
                        (BetaMemory) smem.createNodeMemory(betaNode, wm);

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
                createSegmentMemory(subnetworkLts, wm);
            }

            RiaNodeMemory riaMem = wm.getNodeMemory(riaNode);
            bm.setRiaRuleMemory(riaMem.getRiaPathMemory());
            if (updateNodeBit && canBeDisabled(betaNode) && riaMem.getRiaPathMemory().getAllLinkedMaskTest() > 0) {
                // only ria's with reactive subnetworks can be disabled and thus need checking
                allLinkedTestMask = allLinkedTestMask | nodePosMask;
            }
        } else if (updateNodeBit && canBeDisabled(betaNode)) {
            allLinkedTestMask = allLinkedTestMask | nodePosMask;

        }
        bm.setNodePosMaskBit(nodePosMask);
        if (NodeTypeEnums.NotNode == betaNode.getType()) {
            // not nodes start up linked in
            smem.linkNodeWithoutRuleNotify(bm.getNodePosMaskBit());
        }
        return allLinkedTestMask;
    }

    private static boolean canBeDisabled(BetaNode betaNode) {
        // non empty not nodes and accumulates can never be disabled and thus don't need checking
        return (!(NodeTypeEnums.NotNode == betaNode.getType() && !((NotNode) betaNode).isEmptyBetaConstraints()) &&
                NodeTypeEnums.AccumulateNode != betaNode.getType() && !betaNode.isRightInputPassive());
    }

    public static void createChildSegments(final InternalWorkingMemory wm,
                                           SegmentMemory smem,
                                           LeftTupleSinkPropagator sinkProp) {
        if ( !smem.isEmpty() ) {
              return; // this can happen when multiple threads are trying to initialize the segment
        }
        for (LeftTupleSinkNode sink = sinkProp.getFirstLeftTupleSink(); sink != null; sink = sink.getNextLeftTupleSinkNode()) {
            SegmentMemory childSmem = createChildSegment(wm, sink);
            childSmem.setPos( smem.getPos()+1 );
            smem.add(childSmem);
        }
    }

    public static SegmentMemory createChildSegment(InternalWorkingMemory wm, LeftTupleNode node) {
        Memory memory = wm.getNodeMemory((MemoryFactory) node);
        if (memory.getSegmentMemory() == null) {
            if (NodeTypeEnums.isEndNode(node)) {
                // RTNS and RiaNode's have their own segment, if they are the child of a split.
                createChildSegmentForTerminalNode( node, memory );
            } else {
                createSegmentMemory((LeftTupleSource) node, wm);
            }
        }
        return memory.getSegmentMemory();
    }

    public static SegmentMemory createChildSegmentForTerminalNode( LeftTupleNode node, Memory memory ) {
        SegmentMemory childSmem = new SegmentMemory( node ); // rtns or riatns don't need a queue
        PathMemory pmem = NodeTypeEnums.isTerminalNode( node ) ? (PathMemory) memory : ((RiaNodeMemory) memory).getRiaPathMemory();

        childSmem.setPos( pmem.getSegmentMemories().length - 1 );
        pmem.setSegmentMemory(childSmem.getPos(), childSmem);
        pmem.setSegmentMemory(childSmem);
        childSmem.addPathMemory( pmem );

        childSmem.setTipNode(node);
        return childSmem;
    }

    /**
     * Is the LeftTupleSource a node in the sub network for the RightInputAdapterNode
     * To be in the same network, it must be a node is after the two output of the parent
     * and before the rianode.
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
                // Even though we don't add the pmem and smem together, all pmem's for all pathend nodes must be initialized
                RiaNodeMemory riaMem = (RiaNodeMemory) wm.getNodeMemory((MemoryFactory) sink);
                // Only add the RIANode, if the LeftTupleSource is part of the RIANode subnetwork
                if (inSubNetwork((RightInputAdapterNode) sink, originalLt)) {
                    PathMemory pmem = riaMem.getRiaPathMemory();
                    smem.addPathMemory( pmem );
                    if (smem.getPos() < pmem.getSegmentMemories().length) {
                        pmem.setSegmentMemory( smem.getPos(), smem );
                    }

                    if (fromPrototype) {
                        ObjectSink[] nodes = ((RightInputAdapterNode) sink).getObjectSinkPropagator().getSinks();
                        for ( ObjectSink node : nodes ) {
                            // check if the SegmentMemory has been already created by the BetaNode and if so avoid to build it twice
                            if ( NodeTypeEnums.isLeftTupleSource(node) && wm.getNodeMemory((MemoryFactory) node).getSegmentMemory() == null )  {
                                restoreSegmentFromPrototype(wm, (LeftTupleSource) node, nodeTypesInSegment);
                            }
                        }
                    } else if ( ( pmem.getAllLinkedMaskTest() & ( 1L << pmem.getSegmentMemories().length ) ) == 0 ) {
                        // must eagerly initialize child segment memories
                        ObjectSink[] nodes = ((RightInputAdapterNode) sink).getObjectSinkPropagator().getSinks();
                        for ( ObjectSink node : nodes ) {
                            if ( NodeTypeEnums.isLeftTupleSource(node) )  {
                                createSegmentMemory( (LeftTupleSource) node, wm );
                            }
                        }
                    }
                }
            } else if (NodeTypeEnums.isTerminalNode(sink)) {
                PathMemory pmem = (PathMemory) wm.getNodeMemory((MemoryFactory) sink);
                smem.addPathMemory(pmem);
                // this terminal segment could have been created during a rule removal with the only purpose to be merged
                // with the former one and in this case doesn't have to be added to the the path memory
                if (smem.getPos() < pmem.getSegmentMemories().length) {
                    pmem.setSegmentMemory( smem.getPos(), smem );
                    if (smem.isSegmentLinked()) {
                        // not's can cause segments to be linked, and the rules need to be notified for evaluation
                        smem.notifyRuleLinkSegment(wm);
                    }
                    checkEagerSegmentCreation(sink.getLeftTupleSource(), wm, nodeTypesInSegment);
                }
            }
        }
        return nodeTypesInSegment;
    }

    private static int checkSegmentBoundary(LeftTupleSource lt, InternalWorkingMemory wm, int nodeTypesInSegment) {
        if ( isRootNode( lt, null ) )  {
            // we are in a new child segment
            checkEagerSegmentCreation(lt.getLeftTupleSource(), wm, nodeTypesInSegment);
            nodeTypesInSegment = 0;
        }
        return updateNodeTypesMask(lt, nodeTypesInSegment);
    }

    public static SegmentMemory checkEagerSegmentCreation(LeftTupleSource lt, InternalWorkingMemory wm, int nodeTypesInSegment) {
        // A Not node has to be eagerly initialized unless in its segment there is at least a join node
        if ( isSet(nodeTypesInSegment, NOT_NODE_BIT) &&
             !isSet(nodeTypesInSegment, JOIN_NODE_BIT) &&
             !isSet(nodeTypesInSegment, REACTIVE_EXISTS_NODE_BIT) ) {
            return createSegmentMemory(lt, wm);
        }
        return null;
    }

    /**
     * Returns whether the node is the root of a segment.
     * Lians are always the root of a segment.
     *
     * node cannot be null.
     *
     * The result should discount any removingRule. That means it gives you the result as
     * if the rule had already been removed from the network.
     */
    public static boolean isRootNode(LeftTupleNode node, TerminalNode removingTN) {
        return node.getType() == NodeTypeEnums.LeftInputAdapterNode || isNonTerminalTipNode( node.getLeftTupleSource(), removingTN );
    }

    /**
     * Returns whether the node is the tip of a segment.
     * EndNodes (rtn and rian) are always the tip of a segment.
     *
     * node cannot be null.
     *
     * The result should discount any removingRule. That means it gives you the result as
     * if the rule had already been removed from the network.
     */
    public static boolean isTipNode( LeftTupleNode node, TerminalNode removingTN ) {
        return NodeTypeEnums.isEndNode(node) || isNonTerminalTipNode( node, removingTN );
    }

    private static boolean isNonTerminalTipNode( LeftTupleNode node, TerminalNode removingTN ) {
        LeftTupleSinkPropagator sinkPropagator = node.getSinkPropagator();

        if (removingTN == null) {
            return sinkPropagator.size() > 1;
        }

        if (sinkPropagator.size() == 1) {
            return false;
        }

        // we know the sink size is creater than 1 and that there is a removingRule that needs to be ignored.
        int count = 0;
        for ( LeftTupleSinkNode sink = sinkPropagator.getFirstLeftTupleSink(); sink != null; sink = sink.getNextLeftTupleSinkNode() ) {
            if ( sinkNotExclusivelyAssociatedWithTerminal( removingTN, sink ) ) {
                count++;
                if ( count > 1 ) {
                    // There is more than one sink that is not for the removing rule
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean sinkNotExclusivelyAssociatedWithTerminal( TerminalNode removingTN, LeftTupleSinkNode sink ) {
        return sink.getAssociatedRuleSize() > 1 || !sink.isAssociatedWith( removingTN.getRule() ) ||
               !removingTN.isTerminalNodeOf( sink ) || hasTerminalNodesDifferentThan( sink, removingTN );
    }

    private static boolean hasTerminalNodesDifferentThan(LeftTupleSinkNode node, TerminalNode tn) {
        LeftTupleSinkPropagator sinkPropagator = node.getSinkPropagator();
        for ( LeftTupleSinkNode sink = sinkPropagator.getFirstLeftTupleSink(); sink != null; sink = sink.getNextLeftTupleSinkNode() )  {
            if (sink instanceof TerminalNode) {
                if (tn.getId() != sink.getId()) {
                    return true;
                }
            } else if (hasTerminalNodesDifferentThan(sink, tn)) {
                return true;
            }
        }
        return false;
    }

    private static ObjectTypeNode getQueryOtn(LeftTupleSource lts) {
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

    private static LeftInputAdapterNode getQueryLiaNode(String queryName, ObjectTypeNode queryOtn) {
        for (ObjectSink sink : queryOtn.getObjectSinkPropagator().getSinks()) {
            AlphaNode alphaNode = (AlphaNode) sink;
            QueryNameConstraint nameConstraint = (QueryNameConstraint) alphaNode.getConstraint();
            if (queryName.equals(nameConstraint.getQueryName())) {
                return (LeftInputAdapterNode) alphaNode.getObjectSinkPropagator().getSinks()[0];
            }
        }

        throw new RuntimeException("Unable to find query '" + queryName + "'");
    }

    private static final int NOT_NODE_BIT               = 1 << 0;
    private static final int JOIN_NODE_BIT              = 1 << 1;
    private static final int REACTIVE_EXISTS_NODE_BIT   = 1 << 2;
    private static final int PASSIVE_EXISTS_NODE_BIT    = 1 << 3;

    public static int updateNodeTypesMask(NetworkNode node, int mask) {
        if (node != null) {
            switch ( node.getType() ) {
                case NodeTypeEnums.JoinNode:
                    mask |= JOIN_NODE_BIT;
                    break;
                case NodeTypeEnums.ExistsNode:
                    if ( ( (ExistsNode) node ).isRightInputPassive() ) {
                        mask |= PASSIVE_EXISTS_NODE_BIT;
                    } else {
                        mask |= REACTIVE_EXISTS_NODE_BIT;
                    }
                    break;
                case NodeTypeEnums.NotNode:
                    mask |= NOT_NODE_BIT;
                    break;
            }
        }
        return mask;
    }

    public static boolean isSet(int mask, int bit) {
        return (mask & bit) == bit;
    }
}
