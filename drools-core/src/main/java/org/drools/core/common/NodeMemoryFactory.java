package org.drools.core.common;

import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.RuleSessionConfiguration;
import org.drools.core.reteoo.AccumulateNode;
import org.drools.core.reteoo.AsyncReceiveNode;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.AsyncReceiveNode.AsyncReceiveMemory;
import org.drools.core.reteoo.PathEndNode;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.core.reteoo.AccumulateNode.MultiAccumulateMemory;
import org.drools.core.reteoo.AccumulateNode.SingleAccumulateMemory;
import org.drools.core.reteoo.PathEndNode.PathMemSpec;
import org.drools.core.reteoo.TupleToObjectNode.SubnetworkPathMemory;

public class NodeMemoryFactory {
    
    
    private ActivationsManager activationsManager;
    
    private RuleSessionConfiguration ruleSessionConfiguration;
    
    private SegmentMemorySupport segmentMemorySupport;
    
    private ReteEvaluator reteEvaluator;

    private RuleBaseConfiguration ruleBaseConfiguration;
    
    public NodeMemoryFactory(RuleBaseConfiguration ruleBaseConfiguration, ReteEvaluator reteEvaluator) {
        this.ruleBaseConfiguration = ruleBaseConfiguration;
        this.reteEvaluator = reteEvaluator;
    }
    
    public PathMemory createPathMemory(PathEndNode node) {
        return initPathMemory(node, new PathMemory(node, reteEvaluator) );
    }

    public SubnetworkPathMemory createSubnetworkPathMemory(PathEndNode node) {
        return (SubnetworkPathMemory) NodeMemoryFactory.initPathMemory(node, new SubnetworkPathMemory(node,
                reteEvaluator));
    }

    public static PathMemory initPathMemory( PathEndNode pathEndNode, PathMemory pmem ) {
        PathMemSpec pathMemSpec = pathEndNode.getPathMemSpec();
        pmem.setAllLinkedMaskTest(pathMemSpec.allLinkedTestMask );
        pmem.setSegmentMemories( new SegmentMemory[pathEndNode.getPathMemSpec().smemCount()] );
        return pmem;
    }

    public AsyncReceiveMemory createAsyncReceiveMemory(AsyncReceiveNode asyncReceiveNode) {
        return new AsyncReceiveMemory(asyncReceiveNode, reteEvaluator);
    }
    
    public BetaMemory createBetaMemory(BetaConstraints constraints, BetaNode betaNode) {
        return constraints.createBetaMemory(ruleBaseConfiguration, betaNode.getType());
    }
    
    public AccumulateMemory createAccumulateMemory(BetaConstraints constraint, AccumulateNode betaNode) {
        BetaMemory betaMemory = constraint.createBetaMemory(ruleBaseConfiguration, NodeTypeEnums.AccumulateNode);
        AccumulateMemory memory = betaNode.getAccumulate().isMultiFunction() ?
                                  new MultiAccumulateMemory(betaMemory, betaNode.getAccumulate().getAccumulators()) :
                                  new SingleAccumulateMemory(betaMemory, betaNode.getAccumulate().getAccumulators()[0]);

        memory.workingMemoryContext = betaNode.getAccumulate().createWorkingMemoryContext();
        memory.resultsContext = betaNode.getResultBinder().createContext();
        return memory;
    }
    

}
