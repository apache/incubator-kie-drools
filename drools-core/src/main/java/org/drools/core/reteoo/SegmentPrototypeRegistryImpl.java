package org.drools.core.reteoo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.SegmentMemory.SegmentPrototype;


public class SegmentPrototypeRegistryImpl implements SegmentPrototypeRegistry {
    
    private final transient Map<Integer, SegmentPrototype> segmentProtos;

    public SegmentPrototypeRegistryImpl(boolean isEagerCreation) {
        segmentProtos =  isEagerCreation ? new HashMap<>() : new ConcurrentHashMap<>();
    }
    
    public boolean hasSegmentPrototypes() {
        return !segmentProtos.isEmpty();
    }

    public void registerSegmentPrototype(LeftTupleNode tupleSource, SegmentPrototype smem) {
        segmentProtos.put(tupleSource.getId(), smem);
    }

    public void invalidateSegmentPrototype(LeftTupleNode rootNode) {
        segmentProtos.remove(rootNode.getId());
    }

    @Override
    public SegmentPrototype getSegmentPrototype(LeftTupleNode node) {
        return segmentProtos.get(node.getId());
    }

    @Override
    public SegmentMemory createSegmentFromPrototype(ReteEvaluator reteEvaluator, LeftTupleSource tupleSource) {
        SegmentPrototype proto = segmentProtos.get(tupleSource.getId());
        return createSegmentFromPrototype(reteEvaluator, proto);
    }

    public SegmentMemory createSegmentFromPrototype(ReteEvaluator reteEvaluator, SegmentPrototype proto) {
        return proto.newSegmentMemory(reteEvaluator);
    }

    public SegmentPrototype getSegmentPrototype(SegmentMemory segment) {
        return segmentProtos.get(segment.getRootNode().getId());
    }

}
