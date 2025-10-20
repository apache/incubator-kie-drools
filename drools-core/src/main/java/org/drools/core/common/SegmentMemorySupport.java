package org.drools.core.common;

import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.LeftTupleNode;
import org.drools.core.reteoo.LeftTupleSinkPropagator;
import org.drools.core.reteoo.PathEndNode;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.TupleToObjectNode;

public interface SegmentMemorySupport {
    
    public SegmentMemory getOrCreateSegmentMemory(LeftTupleNode node);
    
    public SegmentMemory getOrCreateSegmentMemory(LeftTupleNode node, Memory memory);
    
    public SegmentMemory getQuerySegmentMemory(QueryElementNode queryNode);
    
    public void createChildSegments(LeftTupleSinkPropagator sinkProp, SegmentMemory smem);
    
    public SegmentMemory createChildSegment(LeftTupleNode node);
    
    public TupleToObjectNode createSubnetworkSegmentMemory(BetaNode betaNode);
    
    public PathMemory initializePathMemory(PathEndNode pathEndNode);
    
    

}
