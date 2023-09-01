package org.kie.api.runtime.process;

import java.util.Collection;

/**
 * A node instance container is a container that can contain
 * (zero or more) node instances.
 */
public interface NodeInstanceContainer {

    /**
     * Returns all node instances that are currently active
     * within this container.
     *
     * @return the list of node instances currently active
     */
    Collection<NodeInstance> getNodeInstances();

    /**
     * Returns the node instance with the given id, or <code>null</code>
     * if the node instance cannot be found.
     *
     * @param nodeInstanceId
     * @return the node instance with the given id
     */
    NodeInstance getNodeInstance(long nodeInstanceId);

}
