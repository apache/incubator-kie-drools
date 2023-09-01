package org.kie.api.management;

/**
 * An interface for OTN MBean
 */
public interface ObjectTypeNodeMonitorMBean {

    /**
     * @return ID of the node
     */
    int getId();

    /**
     * @return entry point for the node
     */
    String getEntryPoint();

    /**
     * @return Object Type of the node
     */
    String getObjectType();

    /**
     * @return partition the node belongs to
     */
    String getPartitionId();

    /**
     * @return true if this node corresponds to an event type
     */
    boolean isEvent();

    /**
     * @return calculated expiration offset for this node in case it is an event.
     * -1 means it does not expire.
     */
    long getExpirationOffset();

}
