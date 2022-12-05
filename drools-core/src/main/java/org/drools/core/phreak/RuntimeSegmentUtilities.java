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

import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.AccumulateNode;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.AsyncReceiveNode;
import org.drools.core.reteoo.AsyncSendNode;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.ConditionalBranchNode;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.EvalConditionNode;
import org.drools.core.reteoo.FromNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.core.reteoo.LeftTupleNode;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.LeftTupleSinkNode;
import org.drools.core.reteoo.LeftTupleSinkPropagator;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.ObjectSink;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.PathEndNode;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.RightInputAdapterNode;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.SegmentMemory.SegmentPrototype;
import org.drools.core.reteoo.TimerNode;
import org.drools.core.rule.constraint.QueryNameConstraint;

import static org.drools.core.phreak.BuildtimeSegmentUtilities.JOIN_NODE_BIT;
import static org.drools.core.phreak.BuildtimeSegmentUtilities.NOT_NODE_BIT;
import static org.drools.core.phreak.BuildtimeSegmentUtilities.REACTIVE_EXISTS_NODE_BIT;
import static org.drools.core.phreak.BuildtimeSegmentUtilities.canBeDisabled;
import static org.drools.core.phreak.BuildtimeSegmentUtilities.isNonTerminalTipNode;
import static org.drools.core.phreak.BuildtimeSegmentUtilities.isRootNode;
import static org.drools.core.phreak.BuildtimeSegmentUtilities.isSet;
import static org.drools.core.phreak.BuildtimeSegmentUtilities.nextNodePosMask;
import static org.drools.core.phreak.BuildtimeSegmentUtilities.updateNodeTypesMask;

public class RuntimeSegmentUtilities {

    /**
     * Initialises the NodeSegment memory for all nodes in the segment.
     */
    public static SegmentMemory getOrCreateSegmentMemory(LeftTupleNode node, ReteEvaluator reteEvaluator) {
        SegmentMemory smem = reteEvaluator.getNodeMemory((MemoryFactory) node).getSegmentMemory();
        if ( smem != null ) {
            return smem;
        }

        // find segment root
        LeftTupleNode segmentRoot = BuildtimeSegmentUtilities.findSegmentRoot(node);

        smem = restoreSegmentFromPrototype(reteEvaluator, segmentRoot);
        if ( smem != null ) {
            if (NodeTypeEnums.isBetaNode(segmentRoot) && ((BetaNode) segmentRoot).isRightInputIsRiaNode()) {
                createRiaSegmentMemory((BetaNode) segmentRoot, reteEvaluator);
            }
            return smem;
        }

        return lazyCreateSegmentMemory(reteEvaluator, segmentRoot);
    }

    private static SegmentMemory restoreSegmentFromPrototype(ReteEvaluator reteEvaluator, LeftTupleNode segmentRoot) {
        SegmentPrototype proto = reteEvaluator.getKnowledgeBase().getSegmentPrototype(segmentRoot);
        if (proto == null || proto.getNodesInSegment() == null) {
            return null;
        }

        LeftTupleNode lastNode = proto.getNodesInSegment()[proto.getNodesInSegment().length-1];
        SegmentMemory smem = null;


        if (NodeTypeEnums.isTerminalNode(lastNode)) {
            // If the last node is a tn and it's pmem does not exist, instantiate it separately to avoid a recursive smem/pmem creation.
            // As the smem will create the pmem and thus peek will no longer work.
            PathMemory pmem = (PathMemory) reteEvaluator.getNodeMemories().peekNodeMemory(lastNode);
            if (pmem == null) {
                pmem = initializePathMemory(reteEvaluator, (PathEndNode) lastNode);
            }

            smem = pmem.getSegmentMemories()[proto.getPos()];
        }


        if (smem == null) {
            // Note eager smems mean this smem may have been created by this initializePathMemory,
            // so check and use that to avoid duplicate creation.
            smem = reteEvaluator.getKnowledgeBase().createSegmentFromPrototype(reteEvaluator, proto);
        }

        updateRiaAndTerminalMemory(smem, proto, reteEvaluator);

        return smem;
    }

    public static SegmentMemory getQuerySegmentMemory(ReteEvaluator reteEvaluator, LeftTupleSource segmentRoot, QueryElementNode queryNode) {
        LeftInputAdapterNode liaNode = getQueryLiaNode(queryNode.getQueryElement().getQueryName(), getQueryOtn(segmentRoot));
        LiaNodeMemory liam = reteEvaluator.getNodeMemory(liaNode);
        SegmentMemory querySmem = liam.getSegmentMemory();
        if (querySmem == null) {
            querySmem = getOrCreateSegmentMemory(liaNode, reteEvaluator);
        }
        return querySmem;
    }

    private static RightInputAdapterNode createRiaSegmentMemory( BetaNode betaNode, ReteEvaluator reteEvaluator ) {
        RightInputAdapterNode riaNode = (RightInputAdapterNode) betaNode.getRightInput();

        LeftTupleSource subnetworkLts = riaNode.getLeftTupleSource();
        while (subnetworkLts.getLeftTupleSource() != riaNode.getStartTupleSource()) {
            subnetworkLts = subnetworkLts.getLeftTupleSource();
        }

        Memory rootSubNetwokrMem = reteEvaluator.getNodeMemory( (MemoryFactory) subnetworkLts );
        SegmentMemory subNetworkSegmentMemory = rootSubNetwokrMem.getSegmentMemory();
        if (subNetworkSegmentMemory == null) {
            // we need to stop recursion here
            getOrCreateSegmentMemory(subnetworkLts, reteEvaluator);
        }
        return riaNode;
    }

    public static void createChildSegments(ReteEvaluator reteEvaluator, SegmentMemory smem, LeftTupleSinkPropagator sinkProp) {
        if ( !smem.isEmpty() ) {
              return; // this can happen when multiple threads are trying to initialize the segment
        }
        for (LeftTupleSinkNode sink = sinkProp.getFirstLeftTupleSink(); sink != null; sink = sink.getNextLeftTupleSinkNode()) {
            SegmentMemory childSmem = PhreakBuilder.isEagerSegmentCreation() ? createChildSegment(reteEvaluator, sink) : lazyCreateChildSegment(reteEvaluator, sink);
            smem.add(childSmem);
        }
    }

    public static SegmentMemory createChildSegment(ReteEvaluator reteEvaluator, LeftTupleNode node) {
        Memory memory = reteEvaluator.getNodeMemory((MemoryFactory) node);
        if (memory.getSegmentMemory() == null) {
            getOrCreateSegmentMemory(node, reteEvaluator);
        }
        return memory.getSegmentMemory();
    }

    /**
     * This adds the segment memory to the terminal node or ria node's list of memories.
     * In the case of the terminal node this allows it to know that all segments from
     * the tip to root are linked.
     * In the case of the ria node its all the segments up to the start of the subnetwork.
     * This is because the rianode only cares if all of it's segments are linked, then
     * it sets the bit of node it is the right input for.
     */
    public static void updateRiaAndTerminalMemory(SegmentMemory smem,
                                                 SegmentPrototype proto,
                                                 ReteEvaluator reteEvaluator) {
        for (PathEndNode pathEndNode : proto.getPathEndNodes()) {
            if (pathEndNode.getSegmentPrototypes()[proto.getPos()] == null) {
                // this is a rian path, and the smem is before the subnetwork, so skip.
                continue;
            }

            PathMemory pmem = (PathMemory) reteEvaluator.getNodeMemories().peekNodeMemory(pathEndNode);
            if (pmem != null) {
                pmem.setSegmentMemory( smem.getPos(), smem );
            } else {
                pmem = reteEvaluator.getNodeMemories().getNodeMemory((MemoryFactory<? extends PathMemory>) pathEndNode, reteEvaluator);
                pmem.setSegmentMemory( smem.getPos(), smem ); // this needs to be set before init, to avoid recursion during eager segment initialisation
                initializePathMemory(reteEvaluator, pathEndNode, pmem);
            }

            smem.addPathMemory( pmem );
            if (smem.isSegmentLinked()) {
                // not's can cause segments to be linked, and the rules need to be notified for evaluation
                smem.notifyRuleLinkSegment(reteEvaluator);
            }
        }
    }

    public static PathMemory initializePathMemory(ReteEvaluator reteEvaluator, PathEndNode pathEndNode) {
        PathMemory pmem = reteEvaluator.getNodeMemories().getNodeMemory((MemoryFactory<PathMemory>) pathEndNode, reteEvaluator);
        initializePathMemory(reteEvaluator, pathEndNode, pmem);
        return pmem;
    }

    public static void initializePathMemory(ReteEvaluator reteEvaluator, PathEndNode pathEndNode, PathMemory pmem) {
        if (pathEndNode.getEagerSegmentPrototypes() != null) {
            for (SegmentPrototype eager : pathEndNode.getEagerSegmentPrototypes()) {
                if ( pmem.getSegmentMemories()[eager.getPos()] == null) {
                    getOrCreateSegmentMemory(eager.getRootNode(), reteEvaluator);
                }
            }
        }
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

    public static void checkEagerSegmentCreation(LeftTupleSource lt, ReteEvaluator reteEvaluator, int nodeTypesInSegment) {
        // A Not node has to be eagerly initialized unless in its segment there is at least a join node
        if ( isSet(nodeTypesInSegment, NOT_NODE_BIT) &&
                !isSet(nodeTypesInSegment, JOIN_NODE_BIT) &&
                !isSet(nodeTypesInSegment, REACTIVE_EXISTS_NODE_BIT) ) {
            getOrCreateSegmentMemory(lt, reteEvaluator);
        }
    }

    // --- LAZY SEGMENT CREATION

    public static SegmentMemory lazyCreateChildSegment(ReteEvaluator reteEvaluator, LeftTupleNode node) {
        Memory memory = reteEvaluator.getNodeMemory((MemoryFactory) node);
        if (memory.getSegmentMemory() == null) {
            if (NodeTypeEnums.isEndNode(node)) {
                // RTNS and RiaNode's have their own segment, if they are the child of a split.
                createChildSegmentForTerminalNode( node, memory );
            } else {
                lazyCreateSegmentMemory((LeftTupleSource) node, reteEvaluator);
            }
        }
        return memory.getSegmentMemory();
    }

    private static SegmentMemory lazyCreateSegmentMemory(ReteEvaluator reteEvaluator, LeftTupleNode segmentRoot) {
        if (NodeTypeEnums.isTerminalNode(segmentRoot)) {
            Memory memory = reteEvaluator.getNodeMemory((MemoryFactory) segmentRoot);
            return createChildSegmentForTerminalNode(segmentRoot, memory );
        }
        return lazyCreateSegmentMemory((LeftTupleSource) segmentRoot, reteEvaluator);
    }

    private static SegmentMemory createChildSegmentForTerminalNode( LeftTupleNode node, Memory memory ) {
        SegmentMemory childSmem = new SegmentMemory( node ); // rtns or riatns don't need a queue
        PathMemory pmem = NodeTypeEnums.isTerminalNode( node ) ? (PathMemory) memory : (RightInputAdapterNode.RiaPathMemory) memory;

        childSmem.setPos( pmem.getSegmentMemories().length - 1 );
        pmem.setSegmentMemory(childSmem.getPos(), childSmem);
        pmem.setSegmentMemory(childSmem);
        childSmem.addPathMemory( pmem );

        childSmem.setTipNode(node);
        childSmem.addNodeMemory(memory);
        return childSmem;
    }

    private static SegmentMemory lazyCreateSegmentMemory(LeftTupleSource segmentRoot, ReteEvaluator reteEvaluator) {
        LeftTupleSource tupleSource = segmentRoot;
        SegmentMemory smem = new SegmentMemory(segmentRoot);

        // Iterate all nodes on the same segment, assigning their position as a bit mask value
        // allLinkedTestMask is the resulting mask used to test if all nodes are linked in
        long nodePosMask = 1;
        long allLinkedTestMask = 0;
        boolean updateNodeBit = true;  // nodes after a branch CE can notify, but they cannot impact linking

        int nodeTypesInSegment = 0;
        while (true) {
            nodeTypesInSegment = updateNodeTypesMask(tupleSource, nodeTypesInSegment);
            if (NodeTypeEnums.isBetaNode(tupleSource)) {
                allLinkedTestMask = processBetaNode((BetaNode)tupleSource, reteEvaluator, smem, nodePosMask, allLinkedTestMask, updateNodeBit);
            } else {
                switch (tupleSource.getType()) {
                    case NodeTypeEnums.LeftInputAdapterNode:
                        allLinkedTestMask = processLiaNode((LeftInputAdapterNode) tupleSource, reteEvaluator, smem, nodePosMask, allLinkedTestMask);
                        break;
                    case NodeTypeEnums.EvalConditionNode:
                        processEvalNode((EvalConditionNode) tupleSource, reteEvaluator, smem);
                        break;
                    case NodeTypeEnums.ConditionalBranchNode:
                        updateNodeBit = processBranchNode((ConditionalBranchNode) tupleSource, reteEvaluator, smem);
                        break;
                    case NodeTypeEnums.FromNode:
                        processFromNode((FromNode) tupleSource, reteEvaluator, smem);
                        break;
                    case NodeTypeEnums.ReactiveFromNode:
                        processReactiveFromNode((MemoryFactory) tupleSource, reteEvaluator, smem, nodePosMask);
                        break;
                    case NodeTypeEnums.TimerConditionNode:
                        processTimerNode((TimerNode) tupleSource, reteEvaluator, smem, nodePosMask);
                        break;
                    case NodeTypeEnums.AsyncSendNode:
                        processAsyncSendNode((AsyncSendNode) tupleSource, reteEvaluator, smem);
                        break;
                    case NodeTypeEnums.AsyncReceiveNode:
                        processAsyncReceiveNode((AsyncReceiveNode) tupleSource, reteEvaluator, smem, nodePosMask);
                        break;
                    case NodeTypeEnums.QueryElementNode:
                        updateNodeBit = processQueryNode((QueryElementNode) tupleSource, reteEvaluator, segmentRoot, smem, nodePosMask);
                        break;
                }
            }

            nodePosMask = nextNodePosMask(nodePosMask);

            if (tupleSource.getSinkPropagator().size() == 1) {
                LeftTupleSinkNode sink = tupleSource.getSinkPropagator().getFirstLeftTupleSink();
                if (NodeTypeEnums.isLeftTupleSource(sink)) {
                    tupleSource = (LeftTupleSource) sink;
                } else {
                    // rtn or rian
                    // While not technically in a segment, we want to be able to iterate easily from the last node memory to the ria/rtn memory
                    // we don't use createNodeMemory, as these may already have been created by, but not added, by the method updateRiaAndTerminalMemory
                    Memory memory = reteEvaluator.getNodeMemory((MemoryFactory) sink);
                    if (sink.getType() == NodeTypeEnums.RightInputAdapterNode) {
                        PathMemory riaPmem = (RightInputAdapterNode.RiaPathMemory)memory;
                        smem.addNodeMemory( riaPmem );

                        RightInputAdapterNode rian = ( RightInputAdapterNode ) sink;
                        ObjectSink[] nodes = rian.getObjectSinkPropagator().getSinks();
                        for ( ObjectSink node : nodes ) {
                            if ( NodeTypeEnums.isLeftTupleSource(node) )  {
                                getOrCreateSegmentMemory( (LeftTupleSource) node, reteEvaluator );
                            }
                        }
                    } else if (NodeTypeEnums.isTerminalNode(sink)) {
                        smem.addNodeMemory( memory );
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
            LeftTupleSource leftTupleSource = pathRoot.getLeftTupleSource();
            if (isNonTerminalTipNode(leftTupleSource, null)) {
                // for each new found segment, increase the mask bit position
                ruleSegmentPosMask = ruleSegmentPosMask << 1;
                counter++;
            }
            pathRoot = leftTupleSource;
        }
        smem.setSegmentPosMaskBit(ruleSegmentPosMask);
        smem.setPos(counter);

        updateRiaAndTerminalMemory(tupleSource, tupleSource, smem, reteEvaluator, false, nodeTypesInSegment);

        reteEvaluator.getKnowledgeBase().registerSegmentPrototype(segmentRoot, smem.getSegmentPrototype().initFromSegmentMemory(smem));

        return smem;
    }

    private static boolean processQueryNode(QueryElementNode queryNode, ReteEvaluator reteEvaluator, LeftTupleSource segmentRoot, SegmentMemory smem, long nodePosMask) {
        // Initialize the QueryElementNode and have it's memory reference the actual query SegmentMemory
        SegmentMemory querySmem = getQuerySegmentMemory(reteEvaluator, segmentRoot, queryNode);
        QueryElementNode.QueryElementNodeMemory queryNodeMem = smem.createNodeMemory(queryNode, reteEvaluator);
        queryNodeMem.setNodePosMaskBit(nodePosMask);
        queryNodeMem.setQuerySegmentMemory(querySmem);
        queryNodeMem.setSegmentMemory(smem);
        return ! queryNode.getQueryElement().isAbductive();
    }

    private static void processFromNode(MemoryFactory tupleSource, ReteEvaluator reteEvaluator, SegmentMemory smem) {
        smem.createNodeMemory(tupleSource, reteEvaluator).setSegmentMemory(smem);
    }

    private static void processAsyncSendNode(MemoryFactory tupleSource, ReteEvaluator reteEvaluator, SegmentMemory smem) {
        smem.createNodeMemory(tupleSource, reteEvaluator).setSegmentMemory(smem);
    }

    private static void processAsyncReceiveNode(AsyncReceiveNode tupleSource, ReteEvaluator reteEvaluator, SegmentMemory smem, long nodePosMask) {
        AsyncReceiveNode.AsyncReceiveMemory tnMem = smem.createNodeMemory( tupleSource, reteEvaluator );
        tnMem.setNodePosMaskBit(nodePosMask);
        tnMem.setSegmentMemory(smem);
    }

    private static void processReactiveFromNode(MemoryFactory tupleSource, ReteEvaluator reteEvaluator, SegmentMemory smem, long nodePosMask) {
        FromNode.FromMemory mem = ((FromNode.FromMemory) smem.createNodeMemory(tupleSource, reteEvaluator));
        mem.setSegmentMemory(smem);
        mem.setNodePosMaskBit(nodePosMask);
    }

    private static boolean processBranchNode(ConditionalBranchNode tupleSource, ReteEvaluator reteEvaluator, SegmentMemory smem) {
        ConditionalBranchNode.ConditionalBranchMemory branchMem = smem.createNodeMemory(tupleSource, reteEvaluator);
        branchMem.setSegmentMemory(smem);
        // nodes after a branch CE can notify, but they cannot impact linking
        return false;
    }

    private static void processEvalNode(EvalConditionNode tupleSource, ReteEvaluator reteEvaluator, SegmentMemory smem) {
        EvalConditionNode.EvalMemory evalMem = smem.createNodeMemory(tupleSource, reteEvaluator);
        evalMem.setSegmentMemory(smem);
    }

    private static void processTimerNode(TimerNode tupleSource, ReteEvaluator reteEvaluator, SegmentMemory smem, long nodePosMask) {
        TimerNode.TimerNodeMemory tnMem = smem.createNodeMemory( tupleSource, reteEvaluator );
        tnMem.setNodePosMaskBit(nodePosMask);
        tnMem.setSegmentMemory(smem);
    }

    private static long processLiaNode(LeftInputAdapterNode tupleSource, ReteEvaluator reteEvaluator, SegmentMemory smem, long nodePosMask, long allLinkedTestMask) {
        LiaNodeMemory liaMemory = smem.createNodeMemory(tupleSource, reteEvaluator);
        liaMemory.setSegmentMemory(smem);
        liaMemory.setNodePosMaskBit(nodePosMask);
        allLinkedTestMask = allLinkedTestMask | nodePosMask;
        return allLinkedTestMask;
    }

    private static long processBetaNode(BetaNode betaNode, ReteEvaluator reteEvaluator, SegmentMemory smem, long nodePosMask, long allLinkedTestMask, boolean updateNodeBit) {
        BetaMemory bm = NodeTypeEnums.AccumulateNode == betaNode.getType() ?
                ((AccumulateNode.AccumulateMemory) smem.createNodeMemory(betaNode, reteEvaluator)).getBetaMemory() :
                (BetaMemory) smem.createNodeMemory(betaNode, reteEvaluator);

        // this must be set first, to avoid recursion as sub networks can be initialised multiple ways
        // and bm.getSegmentMemory == null check can be used to avoid recursion.
        bm.setSegmentMemory(smem);

        if (betaNode.isRightInputIsRiaNode()) {
            RightInputAdapterNode riaNode = createRiaSegmentMemory( betaNode, reteEvaluator );

            RightInputAdapterNode.RiaPathMemory riaMem = reteEvaluator.getNodeMemory(riaNode);
            bm.setRiaRuleMemory(riaMem);
            if (updateNodeBit && canBeDisabled(betaNode) && riaMem.getAllLinkedMaskTest() > 0) {
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
                                                   ReteEvaluator reteEvaluator,
                                                   boolean fromPrototype,
                                                   int nodeTypesInSegment ) {

        nodeTypesInSegment = checkSegmentBoundary(lt, reteEvaluator, nodeTypesInSegment);

        PathMemory pmem = null;
        for (LeftTupleSink sink : lt.getSinkPropagator().getSinks()) {
            if (NodeTypeEnums.isLeftTupleSource(sink)) {
                nodeTypesInSegment = updateRiaAndTerminalMemory((LeftTupleSource) sink, originalLt, smem, reteEvaluator, fromPrototype, nodeTypesInSegment);
            } else if (sink.getType() == NodeTypeEnums.RightInputAdapterNode) {
                // Even though we don't add the pmem and smem together, all pmem's for all pathend nodes must be initialized
                RightInputAdapterNode.RiaPathMemory riaMem = (RightInputAdapterNode.RiaPathMemory) reteEvaluator.getNodeMemory((MemoryFactory) sink);
                // Only add the RIANode, if the LeftTupleSource is part of the RIANode subnetwork
                if (inSubNetwork((RightInputAdapterNode) sink, originalLt)) {
                    pmem = riaMem;

                    if (fromPrototype) {
                        ObjectSink[] nodes = ((RightInputAdapterNode) sink).getObjectSinkPropagator().getSinks();
                        for ( ObjectSink node : nodes ) {
                            // check if the SegmentMemory has been already created by the BetaNode and if so avoid to build it twice
                            if ( NodeTypeEnums.isLeftTupleSource(node) && reteEvaluator.getNodeMemory((MemoryFactory) node).getSegmentMemory() == null )  {
                                restoreSegmentFromPrototype(reteEvaluator, (LeftTupleSource) node, nodeTypesInSegment);
                            }
                        }
                    } else if ( ( pmem.getAllLinkedMaskTest() & ( 1L << pmem.getSegmentMemories().length ) ) == 0 ) {
                        // must eagerly initialize child segment memories
                        ObjectSink[] nodes = ((RightInputAdapterNode) sink).getObjectSinkPropagator().getSinks();
                        for ( ObjectSink node : nodes ) {
                            if ( NodeTypeEnums.isLeftTupleSource(node) )  {
                                getOrCreateSegmentMemory( (LeftTupleSource) node, reteEvaluator );
                            }
                        }
                    }
                }

            } else if (NodeTypeEnums.isTerminalNode(sink)) {
                pmem = (PathMemory) reteEvaluator.getNodeMemory((MemoryFactory) sink);
            }

            if (pmem != null && smem.getPos() < pmem.getSegmentMemories().length) {
                smem.addPathMemory( pmem );
                pmem.setSegmentMemory( smem.getPos(), smem );
                if (smem.isSegmentLinked()) {
                    // not's can cause segments to be linked, and the rules need to be notified for evaluation
                    smem.notifyRuleLinkSegment(reteEvaluator);
                }
                checkEagerSegmentCreation(sink.getLeftTupleSource(), reteEvaluator, nodeTypesInSegment);
                pmem = null;
            }
        }
        return nodeTypesInSegment;
    }

    private static void restoreSegmentFromPrototype(ReteEvaluator reteEvaluator, LeftTupleSource segmentRoot, int nodeTypesInSegment) {
        SegmentMemory smem = reteEvaluator.getKnowledgeBase().createSegmentFromPrototype(reteEvaluator, segmentRoot);
        if ( smem != null ) {
            updateRiaAndTerminalMemory(segmentRoot, segmentRoot, smem, reteEvaluator, true, nodeTypesInSegment);
        }
    }

    private static int checkSegmentBoundary(LeftTupleSource lt, ReteEvaluator reteEvaluator, int nodeTypesInSegment) {
        if ( isRootNode( lt, null ) )  {
            // we are in a new child segment
            checkEagerSegmentCreation(lt.getLeftTupleSource(), reteEvaluator, nodeTypesInSegment);
            nodeTypesInSegment = 0;
        }
        return updateNodeTypesMask(lt, nodeTypesInSegment);
    }


    /**
     * Is the LeftTupleSource a node in the sub network for the RightInputAdapterNode
     * To be in the same network, it must be a node is after the two output of the parent
     * and before the rianode.
     */
    private static boolean inSubNetwork(RightInputAdapterNode riaNode, LeftTupleSource leftTupleSource) {
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
}
