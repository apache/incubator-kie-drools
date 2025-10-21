package org.drools.core.common;

import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.LeftTupleNode;
import org.drools.core.reteoo.LeftTupleSinkPropagator;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.PathEndNode;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.TupleToObjectNode;

public interface SegmentMemorySupport {
    
    public SegmentMemory getOrCreateSegmentMemory(LeftTupleNode node);
    
    public SegmentMemory getOrCreateSegmentMemory(LeftTupleNode node, Memory memory);
    
    public SegmentMemory createSegmentMemoryLazily(LeftTupleSource segmentRoot);
    
    public SegmentMemory getQuerySegmentMemory(QueryElementNode queryNode);
    
    public void createChildSegments(LeftTupleSinkPropagator sinkProp, SegmentMemory smem);
      
    public SegmentMemory createChildSegment(LeftTupleNode node);
   
    public SegmentMemory createChildSegmentLazily(LeftTupleNode node);
    
    public TupleToObjectNode createSubnetworkSegmentMemory(BetaNode betaNode);
    
    public PathMemory initializePathMemory(PathEndNode pathEndNode);
    
    public void checkEagerSegmentCreation(LeftTupleSource lt, int nodeTypesInSegment);
    
    public int checkSegmentBoundary(LeftTupleSource lt, int nodeTypesInSegment);
    
    public int updateSubnetworkAndTerminalMemoryLazily(LeftTupleSource lt,
                                                       LeftTupleSource originalLt,
                                                       SegmentMemory smem,
                                                       boolean fromPrototype,
                                                       int nodeTypesInSegment);
    

}
