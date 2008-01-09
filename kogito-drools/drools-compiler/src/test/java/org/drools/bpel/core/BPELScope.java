package org.drools.bpel.core;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.CompositeNode;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class BPELScope extends CompositeNode implements BPELActivity, BPELFaultHandlerContainer {

    private static final long serialVersionUID = 400L;

    public void setActivity(BPELActivity activity) {
        addNode(activity);
        linkIncomingConnections(
            Node.CONNECTION_DEFAULT_TYPE,
            new CompositeNode.NodeAndType(
                activity, Node.CONNECTION_DEFAULT_TYPE));
        linkOutgoingConnections(
            new CompositeNode.NodeAndType(
                activity, Node.CONNECTION_DEFAULT_TYPE),
            Node.CONNECTION_DEFAULT_TYPE);
    }

    public String[] getSourceLinks() {
        throw new IllegalArgumentException("A scope does not support links!");
    }

    public String[] getTargetLinks() {
        throw new IllegalArgumentException("A scope does not support links!");
    }

    public void setSourceLinks(String[] sourceLinks) {
        throw new IllegalArgumentException("A scope does not support links!");
    }

    public void setTargetLinks(String[] targetLinks) {
        throw new IllegalArgumentException("A scope does not support links!");
    }
    
}
