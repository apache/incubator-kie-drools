package org.drools.core.phreak;

import org.drools.base.common.NetworkNode;
import org.drools.core.common.ActivationsManager;
import org.drools.core.common.Memory;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.SegmentMemory;

public interface RuleNetworkEvaluator {

    void evaluateNetwork(RuleExecutor executor,
                         PathMemory pmem);

    void evaluateNetwork(ActivationsManager activationsManager,
                         RuleExecutor executor,
                         PathMemory pmem);
    
    public void forceFlushLeftTuple(PathMemory pmem,
                                    SegmentMemory sm, 
                                    TupleSets leftTupleSets); 

    void outerEval(ActivationsManager activationsManager,
                   RuleExecutor executor,
                   PathMemory pmem,
                   SegmentMemory[] smems,
                   int smemIndex,
                   long bit,
                   Memory nodeMem,
                   NetworkNode node,
                   TupleSets trgTuples,
                   boolean processSubnetwork);

}
