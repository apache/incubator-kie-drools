package org.drools.workflow.instance;

import java.util.Collection;

import org.drools.definition.process.Node;
import org.drools.definition.process.NodeContainer;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface NodeInstanceContainer extends org.drools.runtime.process.NodeInstanceContainer {

    Collection<NodeInstance> getNodeInstances(boolean recursive);

    NodeInstance getFirstNodeInstance(long nodeId);

    NodeInstance getNodeInstance(Node node);

    void addNodeInstance(NodeInstance nodeInstance);

    void removeNodeInstance(NodeInstance nodeInstance);
    
    NodeContainer getNodeContainer();
    
    void nodeInstanceCompleted(NodeInstance nodeInstance, String outType);

}
