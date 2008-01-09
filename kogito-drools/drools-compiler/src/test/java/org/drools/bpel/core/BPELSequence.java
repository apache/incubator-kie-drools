package org.drools.bpel.core;

import java.util.List;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.impl.ConnectionImpl;
import org.drools.workflow.core.node.CompositeNode;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class BPELSequence extends CompositeNode implements BPELStructuredActivity {
    
    private static final long serialVersionUID = 400L;

    private String[] sourceLinks;
    private String[] targetLinks;
    
    public void setActivities(List<BPELActivity> activities) {
        if (activities == null || activities.size() < 2) {
            throw new IllegalArgumentException(
                "A BPEL sequence must contain at least two sub activities!");
        }
        BPELActivity previous = activities.get(0);
        addNode(previous);
        linkIncomingConnections(
            Node.CONNECTION_DEFAULT_TYPE,
            new CompositeNode.NodeAndType(
                    previous, Node.CONNECTION_DEFAULT_TYPE));
        for (int i = 1; i < activities.size(); i++ ) {
            BPELActivity next = activities.get(i);
            addNode(next);
            new ConnectionImpl(
                previous, Node.CONNECTION_DEFAULT_TYPE,
                next, Node.CONNECTION_DEFAULT_TYPE);
            previous = next;
        }
        linkOutgoingConnections(
            new CompositeNode.NodeAndType(
                previous, Node.CONNECTION_DEFAULT_TYPE),
            Node.CONNECTION_DEFAULT_TYPE);
    }
    
    public String[] getSourceLinks() {
        return sourceLinks;
    }

    public void setSourceLinks(String[] sourceLinks) {
        this.sourceLinks = sourceLinks;
    }

    public String[] getTargetLinks() {
        return targetLinks;
    }

    public void setTargetLinks(String[] targetLinks) {
        this.targetLinks = targetLinks;
    }

}
