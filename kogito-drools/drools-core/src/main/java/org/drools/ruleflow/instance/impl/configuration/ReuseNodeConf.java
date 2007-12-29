package org.drools.ruleflow.instance.impl.configuration;

import org.drools.ruleflow.core.Node;
import org.drools.ruleflow.instance.RuleFlowNodeInstance;
import org.drools.ruleflow.instance.impl.PvmNodeConf;
import org.drools.ruleflow.instance.impl.RuleFlowProcessInstanceImpl;

public class ReuseNodeConf implements PvmNodeConf {
    public final Class<? extends RuleFlowNodeInstance> cls;
    
    public ReuseNodeConf(Class<? extends RuleFlowNodeInstance> cls){
        this.cls = cls;
//        if ( RuleFlowNodeInstance.class.isAssignableFrom( this.cls ) ) {
//            throw new IllegalArgumentException("Node must be of the type RuleFlowNodeInstance." );
//        }
    }

	public RuleFlowNodeInstance getNodeInstance(Node node, RuleFlowProcessInstanceImpl processInstance ) {    	
        RuleFlowNodeInstance result = processInstance.getFirstNodeInstance( node.getId() );
        if ( result == null ) {
            try {
                result = ( RuleFlowNodeInstance ) cls.newInstance();
            } catch ( Exception e ) {
                throw new RuntimeException("Unable  to instance RuleFlow Node: '" + this.cls.getName() );
            }
            result.setNodeId( node.getId() );
            processInstance.addNodeInstance( result );
        }
        return result;    	
	}

}
