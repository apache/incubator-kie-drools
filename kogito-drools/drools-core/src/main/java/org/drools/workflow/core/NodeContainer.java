package org.drools.workflow.core;

import org.drools.process.core.Context;


/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface NodeContainer {

    /**
     * Returns the nodes of this node container.
     * 
     * @return the nodes of this node container
     */
    Node[] getNodes();

    /**
     * Returns the node with the given id
     * 
     * @param id
     *            the node id
     * @return the node with the given id
     * @throws IllegalArgumentException
     *             if an unknown id is passed
     */
    Node getNode(long id);

    /**
     * Method for adding a node to this node container. 
     * Note that the node will get an id unique for this node container.
     * 
     * @param node  the node to be added
     * @throws IllegalArgumentException if <code>node</code> is null 
     */
    void addNode(Node node);

    /**
     * Method for removing a node from this node container
     * 
     * @param node  the node to be removed
     * @throws IllegalArgumentException if <code>node</code> is null or unknown
     */
    void removeNode(Node node);
    
    Context resolveContext(String contextId, Object param);
    
    Node internalGetNode(long id);
    
}
