package org.drools.core.common;

import org.drools.core.reteoo.LeftTupleNode;
import org.drools.core.reteoo.LeftTupleSinkPropagator;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.PathEndNode;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.SegmentMemory;

public interface SegmentMemorySupport {
    
    public SegmentMemory getOrCreateSegmentMemory(LeftTupleNode node);
    
    public SegmentMemory getOrCreateSegmentMemory(LeftTupleNode node, Memory memory);
    
    public SegmentMemory createSegmentMemoryLazily(LeftTupleSource segmentRoot);
    
    public SegmentMemory getQuerySegmentMemory(QueryElementNode queryNode);
    
    public void createChildSegments(LeftTupleSinkPropagator sinkProp, SegmentMemory smem);
      
    public SegmentMemory createChildSegment(LeftTupleNode node);
   
    public SegmentMemory createChildSegmentLazily(LeftTupleNode node);
    
    public PathMemory initializePathMemory(PathEndNode pathEndNode);
    
    public void checkEagerSegmentCreation(LeftTupleSource lt, int nodeTypesInSegment);
    
    

}
