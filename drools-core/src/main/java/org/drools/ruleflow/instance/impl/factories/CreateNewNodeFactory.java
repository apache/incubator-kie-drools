package org.drools.ruleflow.instance.impl.factories;

import org.drools.ruleflow.core.Node;
import org.drools.ruleflow.core.RuleSetNode;
import org.drools.ruleflow.instance.RuleFlowNodeInstance;
import org.drools.ruleflow.instance.impl.ActionNodeInstanceImpl;
import org.drools.ruleflow.instance.impl.MilestoneNodeInstanceImpl;
import org.drools.ruleflow.instance.impl.ProcessNodeInstanceFactory;
import org.drools.ruleflow.instance.impl.RuleFlowJoinInstanceImpl;
import org.drools.ruleflow.instance.impl.RuleFlowProcessInstanceImpl;
import org.drools.ruleflow.instance.impl.RuleFlowSplitInstanceImpl;
import org.drools.ruleflow.instance.impl.StartNodeInstanceImpl;
import org.drools.ruleflow.instance.impl.SubFlowNodeInstanceImpl;

public class CreateNewNodeFactory implements ProcessNodeInstanceFactory {
    public final Class<? extends RuleFlowNodeInstance> cls;
    
    public CreateNewNodeFactory(Class<? extends RuleFlowNodeInstance> cls){
        this.cls = cls;
    }
    
	public RuleFlowNodeInstance getNodeInstance(Node node, RuleFlowProcessInstanceImpl processInstance ) {    	  	
        RuleFlowNodeInstance result;
        try {
            result = this.cls.newInstance();
        } catch ( Exception e ) {
            throw new RuntimeException("Unable  to instance RuleFlow Node: '" + this.cls.getName() );
        }
        result.setNodeId( node.getId() );
        processInstance.addNodeInstance( result );
        return result;
	}

}
