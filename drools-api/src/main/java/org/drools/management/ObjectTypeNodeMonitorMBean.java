package org.drools.management;

/**
 * An interface for OTN MBean
 * 
 * @author etirelli
 */
public interface ObjectTypeNodeMonitorMBean {

    /**
     * The ID of the node
     * 
     * @return
     */
    public int getId();

    /**
     * The entry point for the node
     * 
     * @return
     */
    public String getEntryPoint();

    /**
     * The Object Type of the node
     * 
     * @return
     */
    public String getObjectType();

    /**
     * The partition the node belongs to
     * 
     * @return
     */
    public String getPartitionId();

    /**
     * True if this node corresponds to an event type
     * 
     * @return
     */
    public boolean isEvent();

    /**
     * The calculated expiration offset for this node
     * in case it is an event. -1 means it does not
     * expires.
     * 
     * @return
     */
    public long getExpirationOffset();

}