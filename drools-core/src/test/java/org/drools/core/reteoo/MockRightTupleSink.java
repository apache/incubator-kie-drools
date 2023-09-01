package org.drools.core.reteoo;

import java.util.ArrayList;
import java.util.List;

import org.drools.base.common.NetworkNode;
import org.drools.base.reteoo.BaseTerminalNode;
import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.core.common.PropagationContext;
import org.drools.core.common.ReteEvaluator;
import org.drools.base.common.RuleBasePartitionId;
import org.kie.api.definition.rule.Rule;

public class MockRightTupleSink
    implements
    RightTupleSink {
    
    private final List        retracted        = new ArrayList();

    public void retractRightTuple(RightTuple rightTuple,
                                  PropagationContext context,
                                  ReteEvaluator reteEvaluator) {
        this.retracted.add( new Object[]{rightTuple, context, reteEvaluator} );

    }
    
    public List getRetracted() {
        return this.retracted;
    }

    public int getId() {
        return 0;
    }

    public RuleBasePartitionId getPartitionId() {
        return null;
    }

    public short getType() {
        return NodeTypeEnums.JoinNode;
    }


    public void modifyRightTuple(RightTuple rightTuple,
                                 PropagationContext context,
                                 ReteEvaluator reteEvaluator) {
        // TODO Auto-generated method stub
        
    }

    @Override public Rule[] getAssociatedRules() {
        return new Rule[0];
    }

    public boolean isAssociatedWith(Rule rule) {
        return false;
    }

    public ObjectTypeNode.Id getRightInputOtnId() {
        return null;
    }

    public boolean thisNodeEquals(final Object object) {
        return false;
    }

    @Override
    public void addAssociatedTerminal(BaseTerminalNode terminalNode) {
    }

    @Override
    public void removeAssociatedTerminal(BaseTerminalNode terminalNode) {
    }

    @Override
    public int getAssociatedTerminalsSize() {
        return 0;
    }

    @Override
    public boolean hasAssociatedTerminal(BaseTerminalNode terminalNode) {
        return false;
    }

    @Override
    public NetworkNode[] getSinks() {
        return new NetworkNode[0];
    }
}
