package org.drools.bpel.instance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.bpel.core.BPELActivity;
import org.drools.bpel.core.BPELProcess;
import org.drools.bpel.core.BPELReceive;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.NodeContainer;
import org.drools.workflow.instance.NodeInstance;
import org.drools.workflow.instance.NodeInstanceContainer;
import org.drools.workflow.instance.impl.WorkflowProcessInstanceImpl;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class BPELProcessInstance extends WorkflowProcessInstanceImpl {
    
    private static final long serialVersionUID = 400L;

    public BPELProcess getBPELProcess() {
        return (BPELProcess) getProcess();
    }
    
    public void acceptMessage(String partnerLink, String portType, String operation, String message) {
        if (getState() == STATE_PENDING) {
            setState(STATE_ACTIVE);
        }
        BPELReceive receive = findBPELReceive(partnerLink, portType, operation);
        if (receive == null) {
            throw new IllegalArgumentException(
                "Could not find BPELReceive for " + partnerLink + ", " + portType + ", " + operation);
        }
        BPELActivity activity = receive;
        List<BPELActivity> parents = new ArrayList<BPELActivity>(); 
        while (!activity.getNodeContainer().equals(getBPELProcess())) {
            activity = (BPELActivity) activity.getNodeContainer();
            parents.add(0, activity);
        }
        NodeInstanceContainer nodeInstanceContainer = this;
        for (Iterator<BPELActivity> iterator = parents.iterator(); iterator.hasNext(); ) {
            BPELActivity parent = iterator.next();
            NodeInstance nodeInstance = nodeInstanceContainer.getFirstNodeInstance(parent.getId());
            if (nodeInstance != null) {
                nodeInstanceContainer = (NodeInstanceContainer) nodeInstance;
            } else if (receive.isCreateInstance()) {
                nodeInstanceContainer = (NodeInstanceContainer) nodeInstanceContainer.getNodeInstance(parent);
            } else {
                // TODO: store message in cache of accepted messages
                return;
            }
        }
        ((BPELReceiveInstance) nodeInstanceContainer.getNodeInstance(receive)).triggerCompleted(message);
    }
    
    private BPELReceive findBPELReceive(String partnerLink, String portType, String operation) {
        return findBPELReceive(partnerLink, portType, operation, getBPELProcess().getActivity());
    }
    
    private BPELReceive findBPELReceive(String partnerLink, String portType, String operation, Node node) {
        if (node instanceof BPELReceive) {
            BPELReceive receive = (BPELReceive) node;
            if (receive.getPartnerLink().equals(partnerLink)
                    && receive.getPortType().equals(portType)
                    && receive.getOperation().equals(operation)) {
                return receive;
            }
            return null;
        }
        if (node instanceof NodeContainer) {
            Node[] nodes = ((NodeContainer) node).getNodes();
            for (int i = 0; i < nodes.length; i++) {
                BPELReceive result = findBPELReceive(partnerLink, portType, operation, nodes[i]);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    @Override
    protected void internalStart() {
        // do nothing, BPEL Processes are started by receiving a message
    }

}
