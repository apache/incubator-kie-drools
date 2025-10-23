package org.drools.core.phreak;

import java.util.List;

import org.drools.base.common.NetworkNode;
import org.drools.core.common.ActivationsManager;
import org.drools.core.common.Memory;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.TupleImpl;

public interface RuleNetworkEvaluator {

    void evaluateNetwork(RuleExecutor executor,
                         PathMemory pmem);

    void evaluateNetwork(ActivationsManager activationsManager,
                         RuleExecutor executor,
                         PathMemory pmem);

    void evaluate(PathMemory pmem,
                  ActivationsManager activationsManager,
                  NetworkNode sink,
                  Memory tm,
                  TupleSets trgLeftTuples);
    
    void forceFlushPath(PathMemory outPmem); 

    void forceFlushLeftTuple(PathMemory pmem,
                             SegmentMemory sm,
                             TupleSets leftTupleSets);
    
    void forceFlushWhenSubnetwork(PathMemory pmem);
    
    public boolean flushLeftTupleIfNecessary(SegmentMemory sm, boolean streamMode);
    
    public boolean flushLeftTupleIfNecessary(SegmentMemory sm,
                                             TupleImpl leftTuple,
                                             boolean streamMode,
                                             short stagedType);
    
    List<PathMemory> findPathsToFlushFromSubnetwork(PathMemory pmem);
    
    void propagate(SegmentMemory sourceSegment, TupleSets leftTuples);

}
