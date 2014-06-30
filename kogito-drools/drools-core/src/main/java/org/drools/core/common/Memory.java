package org.drools.core.common;

import org.drools.core.util.LinkedListNode;
import org.drools.core.reteoo.SegmentMemory;

/**
 * A super interface for node memories
 */
public interface Memory extends LinkedListNode<Memory> {
    
    short getNodeType();
    
    SegmentMemory getSegmentMemory();

    void setSegmentMemory(SegmentMemory segmentMemory);

    void reset();
}
