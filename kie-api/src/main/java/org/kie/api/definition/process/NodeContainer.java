package org.kie.api.definition.process;

/**
 * A NodeContainer contains a set of Nodes
 * There are different types of NodeContainers and NodeContainers may be nested.
 */
public interface NodeContainer {

    /**
     * The Nodes of this NodeContainer.
     *
     * @return the nodes
     */
    Node[] getNodes();

    /**
     * The node in this NodeContainer with the given id.
     *
     * @return the node with the given id
     */
    Node getNode(long id);

    /** 
     * the node in this NodeContainer with the give unique id
     * @param nodeId
     * @return
     */
    Node getNodeByUniqueId(String nodeId);

}
