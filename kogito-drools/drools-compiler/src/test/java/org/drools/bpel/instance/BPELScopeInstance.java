package org.drools.bpel.instance;

import org.drools.workflow.instance.NodeInstance;
import org.drools.workflow.instance.node.CompositeNodeInstance;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class BPELScopeInstance extends CompositeNodeInstance {

    private static final long serialVersionUID = 400L;

    public void internalTrigger(NodeInstance from, String type) {
        if (BPELLinkManager.checkActivityEnabled(this)) {
            super.internalTrigger(from, type);
        }
    }
    
    public void triggerCompleted(String outType) {
        super.triggerCompleted(outType);
        BPELLinkManager.activateTargetLinks(this);
    }
    
}
