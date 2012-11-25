package org.drools.common;

import org.drools.core.util.LinkedListNode;
import org.drools.reteoo.SegmentMemory;

/**
 * A super interface for node memories
 */
public interface Memory extends LinkedListNode<Memory> {
    
    public short getNodeType();
    
    public SegmentMemory getSegmentMemory();

}
