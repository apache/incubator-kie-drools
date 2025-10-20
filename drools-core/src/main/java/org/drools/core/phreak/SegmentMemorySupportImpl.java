package org.drools.core.phreak;

import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.base.rule.constraint.QueryNameConstraint;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.NodeMemories;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.SegmentMemorySupport;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTupleNode;
import org.drools.core.reteoo.LeftTupleSinkNode;
import org.drools.core.reteoo.LeftTupleSinkPropagator;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.ObjectSink;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.PathEndNode;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.SegmentPrototypeRegistry;
import org.drools.core.reteoo.TupleToObjectNode;
import org.drools.core.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.core.reteoo.SegmentMemory.SegmentPrototype;

import static org.drools.core.phreak.EagerPhreakBuilder.isInsideSubnetwork;

public class SegmentMemorySupportImpl implements SegmentMemorySupport {

    private final ReteEvaluator reteEvaluator;
    private final NodeMemories nodeMemories;
    private final SegmentPrototypeRegistry segmentPrototypeRegistry;
    private final WorkingMemoryEntryPoint defaultEntryPoint;

    public SegmentMemorySupportImpl(ReteEvaluator reteEvaluator,
                                     NodeMemories nodeMemories,
                                     SegmentPrototypeRegistry segmentPrototypeRegistry,
                                     WorkingMemoryEntryPoint defaultEntryPoint) {
        this.reteEvaluator = reteEvaluator;
        this.nodeMemories = nodeMemories;
        this.segmentPrototypeRegistry = segmentPrototypeRegistry;
        this.defaultEntryPoint = defaultEntryPoint;
    }

    /**
     * Initialises the NodeSegment memory for all nodes in the segment.
     */
    public SegmentMemory getOrCreateSegmentMemory(LeftTupleNode node) {
        return getOrCreateSegmentMemory(node, nodeMemories.getNodeMemory(
                (MemoryFactory<? extends Memory>) node));
    }

    /**
     * Initialises the NodeSegment memory for all nodes in the segment.
     */
    public SegmentMemory getOrCreateSegmentMemory(LeftTupleNode node, Memory memory) {
        SegmentMemory smem = memory.getSegmentMemory();
        if (smem != null) {
            return smem;
        }

        // find segment root
        LeftTupleNode segmentRoot = BuildtimeSegmentUtilities.findSegmentRoot(node);

        smem = restoreSegmentFromPrototype(segmentRoot);
        if (smem != null) {
            if (NodeTypeEnums.isBetaNode(segmentRoot) && segmentRoot.inputIsTupleToObjectNode()) {
                createSubnetworkSegmentMemory((BetaNode) segmentRoot);
            }
            return smem;
        }

        // it should not be possible to reach here, for BuildTimeSegmentProtos
        return LazyPhreakBuilder.createSegmentMemory(reteEvaluator, segmentRoot);
    }

    private SegmentMemory restoreSegmentFromPrototype(LeftTupleNode segmentRoot) {
        SegmentPrototype proto = segmentPrototypeRegistry.getSegmentPrototype(segmentRoot);
        if (proto == null || proto.getNodesInSegment() == null) {
            return null;
        }

        LeftTupleNode lastNode = proto.getNodesInSegment()[proto.getNodesInSegment().length - 1];

        if (NodeTypeEnums.isTerminalNode(lastNode)) {
            // If the last node is a tn and it's pmem does not exist, instantiate it separately to avoid a recursive smem/pmem creation.
            // As the smem will create the pmem and thus peek will no longer work.
            PathMemory pmem = (PathMemory) nodeMemories.peekNodeMemory(lastNode);
            if (pmem == null) {
                pmem = initializePathMemory((PathEndNode) lastNode);
            }

            SegmentMemory smem = pmem.getSegmentMemories()[proto.getPos()];
            if (smem != null) {
                return smem;
            }
        }

        SegmentMemory smem = segmentPrototypeRegistry.createSegmentFromPrototype(reteEvaluator, proto);

        updateSubnetworkAndTerminalMemory(smem, proto);

        return smem;
    }

    public SegmentMemory getQuerySegmentMemory(QueryElementNode queryNode) {
        ObjectTypeNode queryOtn = defaultEntryPoint.getEntryPointNode().getQueryNode();
        LeftInputAdapterNode liaNode = getQueryLiaNode(queryOtn, queryNode.getQueryElement().getQueryName());
        LiaNodeMemory liam = nodeMemories.getNodeMemory(liaNode);
        SegmentMemory querySmem = liam.getSegmentMemory();
        if (querySmem == null) {
            querySmem = getOrCreateSegmentMemory(liaNode, liam);
        }
        return querySmem;
    }

    public TupleToObjectNode createSubnetworkSegmentMemory(BetaNode betaNode) {
        TupleToObjectNode tton = (TupleToObjectNode) betaNode.getRightInput().getParent();

        LeftTupleSource subnetworkLts = tton.getStartTupleSource();

        Memory rootSubNetwokrMem = nodeMemories.getNodeMemory((MemoryFactory) subnetworkLts);
        SegmentMemory subNetworkSegmentMemory = rootSubNetwokrMem.getSegmentMemory();
        if (subNetworkSegmentMemory == null) {
            // we need to stop recursion here
            getOrCreateSegmentMemory(subnetworkLts, rootSubNetwokrMem);
        }
        return tton;
    }

    public void createChildSegments(LeftTupleSinkPropagator sinkProp, SegmentMemory smem) {
        if (!smem.isEmpty()) {
            return; // this can happen when multiple threads are trying to initialize the segment
        }
        for (LeftTupleSinkNode sink = sinkProp.getFirstLeftTupleSink(); sink != null; sink = sink
                .getNextLeftTupleSinkNode()) {
            SegmentMemory childSmem = PhreakBuilder.isEagerSegmentCreation() ? createChildSegment(sink)
                    : LazyPhreakBuilder.createChildSegment(reteEvaluator, sink);
            smem.add(childSmem);
        }
    }

    public SegmentMemory createChildSegment(LeftTupleNode node) {
        Memory memory = nodeMemories.getNodeMemory((MemoryFactory) node);
        if (memory.getSegmentMemory() == null) {
            getOrCreateSegmentMemory(node, memory);
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
    private void updateSubnetworkAndTerminalMemory(SegmentMemory smem, SegmentPrototype proto) {
        for (PathEndNode endNode : proto.getPathEndNodes()) {
            if (!isInsideSubnetwork(endNode, proto)) {
                // While SegmentPrototypes are added for entire path, for traversal reasons.
                // SegmenrMemory's themselves are only added to the PathMemory for path or subpath the are part of.
                continue;
            }

            PathMemory pmem = (PathMemory) nodeMemories.peekNodeMemory(endNode);
            if (pmem != null) {
                pmem.addSegmentToPathMemory(smem);
            } else {
                pmem = nodeMemories.getNodeMemory((MemoryFactory<? extends PathMemory>) endNode);
                pmem.addSegmentToPathMemory(smem); // this needs to be set before init, to avoid recursion during eager segment initialisation
                pmem.setSegmentMemory(smem.getPos(), smem);
                initializePathMemory(endNode, pmem);
            }

            if (smem.getAllLinkedMaskTest() > 0 && smem.isSegmentLinked()) {
                // not's can cause segments to be linked, and the rules need to be notified for evaluation
                smem.notifyRuleLinkSegment();
            }
        }
    }

    public PathMemory initializePathMemory(PathEndNode pathEndNode) {
        PathMemory pmem = nodeMemories.getNodeMemory(pathEndNode);
        initializePathMemory(pathEndNode, pmem);
        return pmem;
    }

    private void initializePathMemory(PathEndNode pathEndNode, PathMemory pmem) {
        if (pathEndNode.getEagerSegmentPrototypes() != null) {
            for (SegmentPrototype eager : pathEndNode.getEagerSegmentPrototypes()) {
                if (pmem.getSegmentMemories()[eager.getPos()] == null) {
                    getOrCreateSegmentMemory(eager.getRootNode());
                }
            }
        }
    }

    private LeftInputAdapterNode getQueryLiaNode(ObjectTypeNode queryOtn, String queryName) {
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
