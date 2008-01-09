package org.drools.bpel.instance;

import org.drools.bpel.core.BPELActivity;
import org.drools.util.ArrayUtils;
import org.drools.workflow.instance.NodeInstance;
import org.drools.workflow.instance.NodeInstanceContainer;
import org.drools.workflow.instance.impl.NodeInstanceImpl;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public final class BPELLinkManager {
    
    private BPELLinkManager() {
    }
    
    private static String[] getSourceLinks(NodeInstance activityInstance) {
        return ((BPELActivity) 
            ((NodeInstanceImpl) activityInstance).getNode())
                .getSourceLinks();
    }
    
    private static String[] getTargetLinks(NodeInstance activityInstance) {
        return ((BPELActivity) 
            ((NodeInstanceImpl) activityInstance).getNode())
                .getTargetLinks();
    }
    
    public static boolean checkActivityEnabled(NodeInstance activityInstance) {
        boolean enabled = true;
        String[] incomingLinks = getTargetLinks(activityInstance);
        if (incomingLinks != null) {
            for (int i = 0; i < incomingLinks.length; i++) {
                BPELFlowInstance flowInstance = getFlowInstance(activityInstance, incomingLinks[i]);
                if (!flowInstance.isLinkActive(incomingLinks[i])) {
                    enabled = false;
                    flowInstance.addWaitingActivityInstance(activityInstance, incomingLinks[i]);
                }
            }
        }
        return enabled;
    }
    
    public static void activateTargetLinks(NodeInstance activityInstance) {
        String[] outgoingLinks = getSourceLinks(activityInstance);
        if (outgoingLinks != null) {
            for (int i = 0; i < outgoingLinks.length; i++) {
                BPELFlowInstance flowInstance = getFlowInstance(activityInstance, outgoingLinks[i]);
                flowInstance.activateLink(outgoingLinks[i]);
            }
        }
    }

    private static BPELFlowInstance getFlowInstance(NodeInstance activityInstance, String linkName) {
        NodeInstanceContainer parent = activityInstance.getNodeInstanceContainer();
        while (!(parent instanceof BPELFlowInstance)
                || !ArrayUtils.contains(((BPELFlowInstance) parent).getBPELFlow().getLinks(), linkName)) {
             parent = ((NodeInstance) parent).getNodeInstanceContainer();     
        }
        return (BPELFlowInstance) parent;
    }

}
