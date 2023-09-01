package org.drools.core.common;

import org.drools.core.phreak.RuntimeSegmentUtilities;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.util.LinkedListNode;

/**
 * A super interface for node memories
 */
public interface Memory extends LinkedListNode<Memory> {
    
    short getNodeType();
    
    SegmentMemory getSegmentMemory();

    default SegmentMemory getOrCreateSegmentMemory( LeftTupleSource tupleSource, ReteEvaluator reteEvaluator ) {
        SegmentMemory smem = getSegmentMemory();
        if (smem == null) {
            smem = RuntimeSegmentUtilities.getOrCreateSegmentMemory(this, tupleSource, reteEvaluator);
        }
        return smem;
    }

    void setSegmentMemory(SegmentMemory segmentMemory);

    void reset();
}
