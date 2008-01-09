package org.drools.bpel.instance;

import org.drools.process.instance.WorkItem;
import org.drools.workflow.instance.NodeInstance;
import org.drools.workflow.instance.node.WorkItemNodeInstance;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class BPELReplyInstance extends WorkItemNodeInstance {

    private static final long serialVersionUID = 400L;

    public void internalTrigger(NodeInstance from, String type) {
        if (BPELLinkManager.checkActivityEnabled(this)) {
            super.internalTrigger(from, type);
        }
    }
    
    public void triggerCompleted(WorkItem workItem) {
        super.triggerCompleted(workItem);
        BPELLinkManager.activateTargetLinks(this);
    }
    
}
