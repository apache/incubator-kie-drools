package org.drools.bpel.core;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.impl.ConnectionImpl;
import org.drools.workflow.core.impl.WorkflowProcessImpl;
import org.drools.workflow.core.node.EndNode;


/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class BPELProcess extends WorkflowProcessImpl implements BPELFaultHandlerContainer {

    private static final long serialVersionUID = 400L;
    
    public static final String BPEL_TYPE = "BPEL";
    
    public BPELProcess() {
        setType(BPEL_TYPE);
    }
    
    public void setActivity(BPELActivity activity) {
        if (activity == null) {
            throw new IllegalArgumentException(
                "The activity of a BPEL process may not be null!");
        }
        if (getActivity() != null) {
            throw new IllegalArgumentException(
                "The activity of this BPEL process has already been set!");
        }
        addNode(activity);
        EndNode end = new EndNode();
        addNode(end);
        new ConnectionImpl(
            activity, Node.CONNECTION_DEFAULT_TYPE,
            end, Node.CONNECTION_DEFAULT_TYPE);
    }
    
    public BPELActivity getActivity() {
        Node[] nodes = getNodes();
        if (nodes == null || nodes.length == 0) {
            return null;
        }
        return (BPELActivity) nodes[0];
    }
    
}
