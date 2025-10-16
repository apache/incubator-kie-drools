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

import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.base.rule.constraint.QueryNameConstraint;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.core.reteoo.LeftTupleNode;
import org.drools.core.reteoo.LeftTupleSinkNode;
import org.drools.core.reteoo.LeftTupleSinkPropagator;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.ObjectSink;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.PathEndNode;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.TupleToObjectNode;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.SegmentMemory.SegmentPrototype;

import static org.drools.core.phreak.EagerPhreakBuilder.isInsideSubnetwork;

public class RuntimeSegmentUtilities {

    /**
     * Initialises the NodeSegment memory for all nodes in the segment.
     */
    public static SegmentMemory getOrCreateSegmentMemory(ReteEvaluator reteEvaluator, LeftTupleNode node) {
        return getOrCreateSegmentMemory(reteEvaluator, node, reteEvaluator.getNodeMemory(
                (MemoryFactory<? extends Memory>) node));
    }

    /**
     * Initialises the NodeSegment memory for all nodes in the segment.
     */
    public static SegmentMemory getOrCreateSegmentMemory(ReteEvaluator reteEvaluator,
                                                         LeftTupleNode node,
                                                         Memory memory) {
        SegmentMemory smem = memory.getSegmentMemory();
        if (smem != null) {
            return smem;
        }

        // find segment root
        LeftTupleNode segmentRoot = BuildtimeSegmentUtilities.findSegmentRoot(node);

        smem = restoreSegmentFromPrototype(reteEvaluator, segmentRoot);
        if (smem != null) {
            if (NodeTypeEnums.isBetaNode(segmentRoot) && segmentRoot.inputIsTupleToObjectNode()) {
                createSubnetworkSegmentMemory(reteEvaluator, (BetaNode) segmentRoot);
            }
            return smem;
        }

        // it should not be possible to reach here, for BuildTimeSegmentProtos
        return LazyPhreakBuilder.createSegmentMemory(reteEvaluator, segmentRoot);
    }

    private static SegmentMemory restoreSegmentFromPrototype(ReteEvaluator reteEvaluator, LeftTupleNode segmentRoot) {
        SegmentPrototype proto = reteEvaluator.getKnowledgeBase().getSegmentPrototype(segmentRoot);
        if (proto == null || proto.getNodesInSegment() == null) {
            return null;
        }

        LeftTupleNode lastNode = proto.getNodesInSegment()[proto.getNodesInSegment().length - 1];

        if (NodeTypeEnums.isTerminalNode(lastNode)) {
            // If the last node is a tn and it's pmem does not exist, instantiate it separately to avoid a recursive smem/pmem creation.
            // As the smem will create the pmem and thus peek will no longer work.
            PathMemory pmem = (PathMemory) reteEvaluator.getNodeMemories().peekNodeMemory(lastNode);
            if (pmem == null) {
                pmem = initializePathMemory(reteEvaluator, (PathEndNode) lastNode);
            }

            SegmentMemory smem = pmem.getSegmentMemories()[proto.getPos()];
            if (smem != null) {
                return smem;
            }
        }

        SegmentMemory smem = reteEvaluator.getKnowledgeBase().createSegmentFromPrototype(reteEvaluator, proto);

        updateSubnetworkAndTerminalMemory(reteEvaluator, smem, proto);

        return smem;
    }

    public static SegmentMemory getQuerySegmentMemory(ReteEvaluator reteEvaluator, QueryElementNode queryNode) {
        ObjectTypeNode queryOtn = reteEvaluator.getDefaultEntryPoint().getEntryPointNode().getQueryNode();
        LeftInputAdapterNode liaNode = getQueryLiaNode(queryOtn, queryNode.getQueryElement().getQueryName());
        LiaNodeMemory liam = reteEvaluator.getNodeMemory(liaNode);
        SegmentMemory querySmem = liam.getSegmentMemory();
        if (querySmem == null) {
            querySmem = getOrCreateSegmentMemory(reteEvaluator, liaNode, liam);
        }
        return querySmem;
    }

    static TupleToObjectNode createSubnetworkSegmentMemory(ReteEvaluator reteEvaluator, BetaNode betaNode) {
        TupleToObjectNode tton = (TupleToObjectNode) betaNode.getRightInput().getParent();

        LeftTupleSource subnetworkLts = tton.getStartTupleSource();

        Memory rootSubNetwokrMem = reteEvaluator.getNodeMemory((MemoryFactory) subnetworkLts);
        SegmentMemory subNetworkSegmentMemory = rootSubNetwokrMem.getSegmentMemory();
        if (subNetworkSegmentMemory == null) {
            // we need to stop recursion here
            getOrCreateSegmentMemory(reteEvaluator, subnetworkLts, rootSubNetwokrMem);
        }
        return tton;
    }

    public static void createChildSegments(ReteEvaluator reteEvaluator,
                                           LeftTupleSinkPropagator sinkProp,
                                           SegmentMemory smem) {
        if (!smem.isEmpty()) {
            return; // this can happen when multiple threads are trying to initialize the segment
        }
        for (LeftTupleSinkNode sink = sinkProp.getFirstLeftTupleSink(); sink != null; sink = sink
                .getNextLeftTupleSinkNode()) {
            SegmentMemory childSmem = PhreakBuilder.isEagerSegmentCreation() ? createChildSegment(reteEvaluator, sink)
                    : LazyPhreakBuilder.createChildSegment(reteEvaluator, sink);
            smem.add(childSmem);
        }
    }

    public static SegmentMemory createChildSegment(ReteEvaluator reteEvaluator, LeftTupleNode node) {
        Memory memory = reteEvaluator.getNodeMemory((MemoryFactory) node);
        if (memory.getSegmentMemory() == null) {
            getOrCreateSegmentMemory(reteEvaluator, node, memory);
        }
        return memory.getSegmentMemory();
    }

    /**
     * This adds the segment memory to the terminal node or TupleToObjectNode node's list of memories.
     * In the case of the terminal node this allows it to know that all segments from
     * the tip to root are linked.
     * In the case of the TupleToObjectNode node its all the segments up to the start of the subnetwork.
     * This is because the TupleToObjectNode only cares if all of it's segments are linked, then
     * it sets the bit of node it is the right input for.
     */
    private static void updateSubnetworkAndTerminalMemory(ReteEvaluator reteEvaluator,
                                                          SegmentMemory smem,
                                                          SegmentPrototype proto) {
        for (PathEndNode endNode : proto.getPathEndNodes()) {
            if (!isInsideSubnetwork(endNode, proto)) {
                // While SegmentPrototypes are added for entire path, for traversal reasons.
                // SegmenrMemory's themselves are only added to the PathMemory for path or subpath the are part of.
                continue;
            }

            PathMemory pmem = (PathMemory) reteEvaluator.getNodeMemories().peekNodeMemory(endNode);
            if (pmem != null) {
                RuntimeSegmentUtilities.addSegmentToPathMemory(pmem, smem);
            } else {
                pmem = reteEvaluator.getNodeMemories().getNodeMemory((MemoryFactory<? extends PathMemory>) endNode);
                RuntimeSegmentUtilities.addSegmentToPathMemory(pmem, smem); // this needs to be set before init, to avoid recursion during eager segment initialisation
                pmem.setSegmentMemory(smem.getPos(), smem);
                initializePathMemory(reteEvaluator, endNode, pmem);
            }

            if (smem.getAllLinkedMaskTest() > 0 && smem.isSegmentLinked()) {
                // not's can cause segments to be linked, and the rules need to be notified for evaluation
                smem.notifyRuleLinkSegment(reteEvaluator);
            }
        }
    }

    public static void addSegmentToPathMemory(PathMemory pmem, SegmentMemory smem) {
        if (smem.getRootNode().getPathIndex() >= pmem.getPathEndNode().getStartTupleSource().getPathIndex()) {
            smem.addPathMemory(pmem);
            pmem.setSegmentMemory(smem.getPos(), smem);
        }

    }

    public static PathMemory initializePathMemory(ReteEvaluator reteEvaluator, PathEndNode pathEndNode) {
        PathMemory pmem = reteEvaluator.getNodeMemories().getNodeMemory(pathEndNode);
        initializePathMemory(reteEvaluator, pathEndNode, pmem);
        return pmem;
    }

    public static void initializePathMemory(ReteEvaluator reteEvaluator, PathEndNode pathEndNode, PathMemory pmem) {
        if (pathEndNode.getEagerSegmentPrototypes() != null) {
            for (SegmentPrototype eager : pathEndNode.getEagerSegmentPrototypes()) {
                if (pmem.getSegmentMemories()[eager.getPos()] == null) {
                    getOrCreateSegmentMemory(reteEvaluator, eager.getRootNode());
                }
            }
        }
    }

    private static LeftInputAdapterNode getQueryLiaNode(ObjectTypeNode queryOtn, String queryName) {
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
