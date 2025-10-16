package org.drools.core.reteoo;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.SegmentMemory.SegmentPrototype;

public interface SegmentPrototypeRegistry {
    
    void invalidateSegmentPrototype(LeftTupleNode rootNode);
    
    void registerSegmentPrototype(LeftTupleNode tupleSource, SegmentPrototype smem);

    SegmentMemory createSegmentFromPrototype(ReteEvaluator reteEvaluator, LeftTupleSource tupleSource);

    SegmentMemory createSegmentFromPrototype(ReteEvaluator reteEvaluator, SegmentPrototype smem);

    SegmentPrototype getSegmentPrototype(SegmentMemory segment);

    SegmentPrototype getSegmentPrototype(LeftTupleNode node);

    boolean hasSegmentPrototypes();

}
