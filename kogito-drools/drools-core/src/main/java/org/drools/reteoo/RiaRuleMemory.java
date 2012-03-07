package org.drools.reteoo;

import org.drools.common.InternalWorkingMemory;

public class RiaRuleMemory extends RuleMemory {

    private RightInputAdapterNode riaNode;
    
    public RiaRuleMemory(RightInputAdapterNode riaNode) {
        super( null );
        this.riaNode = riaNode;
    }
    
    
    public void doLinkRule(InternalWorkingMemory wm) {
        riaNode.getSinkPropagator().doLinkRiaNode( wm );
    }
        
    public void doUnlinkRule(InternalWorkingMemory wm) {
        riaNode.getSinkPropagator().doUnlinkRiaNode( wm );
    }
    
    public short getNodeType() {
        return NodeTypeEnums.RightInputAdaterNode;
    }    
	
}
