package org.drools.workflow.instance;

import java.util.Collection;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.NodeContainer;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface NodeInstanceContainer {

    Collection<NodeInstance> getNodeInstances();

    Collection<NodeInstance> getNodeInstances(boolean recursive);

    NodeInstance getFirstNodeInstance(long nodeId);

    NodeInstance getNodeInstance(Node node);

    void addNodeInstance(NodeInstance nodeInstance);

    void removeNodeInstance(NodeInstance nodeInstance);
    
    NodeContainer getNodeContainer();

}
