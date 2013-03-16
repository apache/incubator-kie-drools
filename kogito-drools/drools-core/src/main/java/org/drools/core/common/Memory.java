package org.drools.core.common;

import org.drools.core.util.LinkedListNode;
import org.drools.core.reteoo.SegmentMemory;

/**
 * A super interface for node memories
 */
public interface Memory extends LinkedListNode<Memory> {
    
    public short getNodeType();
    
    public SegmentMemory getSegmentMemory();

}
