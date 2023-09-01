package org.drools.mvel.compiler.reteoo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import org.drools.base.common.NetworkNode;
import org.drools.core.common.PropagationContext;
import org.drools.core.common.ReteEvaluator;
import org.drools.base.common.RuleBasePartitionId;
import org.drools.base.reteoo.BaseTerminalNode;
import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RightTupleSink;
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

    public void writeExternal( ObjectOutput out ) throws IOException {
    }

    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
    }

    public short getType() {
        return NodeTypeEnums.JoinNode;
    }

    public void modifyRightTuple(RightTuple rightTuple,
                                 PropagationContext context,
                                 ReteEvaluator reteEvaluator) {
    }

    public boolean isAssociatedWith( Rule rule ) {
        return false;
    }

    @Override public Rule[] getAssociatedRules() {
        return new Rule[0];
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

    public ObjectTypeNode.Id getRightInputOtnId() {
        return null;
    }

    public boolean thisNodeEquals(final Object object) {
        return false;
    }

    public int nodeHashCode() {return this.hashCode();}

    @Override
    public NetworkNode[] getSinks() {
        return new NetworkNode[0];
    }
}
