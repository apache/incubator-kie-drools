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
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.core.reteoo.LeftTupleNode;
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
import org.drools.core.rule.constraint.QueryNameConstraint;

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
        LeftTupleNode segmentRoot  = BuildtimeSegmentUtilities.findSegmentRoot(node);

        smem = restoreSegmentFromPrototype(reteEvaluator, segmentRoot);
        if (NodeTypeEnums.isBetaNode(segmentRoot) && ( (BetaNode) segmentRoot ).isRightInputIsRiaNode()) {
            createRiaSegmentMemory( (BetaNode) segmentRoot, reteEvaluator );
        }
        return smem;
    }

    public static long nextNodePosMask(long nodePosMask) {
        // prevent overflow of segment and path memories masks when a segment has 64 or more nodes or a path has 64 or more segments
        // in this extreme case all the items after the 64th will be all mapped by the same bit and then the linking of one of them
        // will be enough to consider all those item linked
        long nextNodePosMask = nodePosMask << 1;
        return nextNodePosMask > 0 ? nextNodePosMask : nodePosMask;
    }

    private static SegmentMemory restoreSegmentFromPrototype(ReteEvaluator reteEvaluator, LeftTupleNode segmentRoot) {
        SegmentPrototype proto = reteEvaluator.getKnowledgeBase().getSegmentPrototype(segmentRoot);

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
            SegmentMemory childSmem = createChildSegment(reteEvaluator, sink);
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
}
