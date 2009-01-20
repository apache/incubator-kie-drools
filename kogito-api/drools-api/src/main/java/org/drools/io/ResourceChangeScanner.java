package org.drools.io;

import java.util.Properties;

/**
 * <p>
 * Built in service, as provided by the ResourceFactory for monitor file changes on the local disk.
 * </p>
 *
 * <p>
 * This interface, as well as ChangeSet, ResourceChangeNotifier, ResourceChangeMonitor and ResourceChangeScanner are still considered subject to change. 
 * Use the XML format change-set, as
 * part of the ResourceType api when adding to KnowledgeBuilder, which is considered stable. KnowledgeBuilder currently ignored Added/Modified xml elements,
 * the KnowledgeAgent will use them, when rebuilding the KnowledgeBase.
 * </p>
 */
public interface ResourceChangeScanner
    extends
    ResourceChangeMonitor {

    /**
     * New ResourceChangeScannerConfiguration which can be used to update this service.
     * @return
     */
    public ResourceChangeScannerConfiguration newResourceChangeScannerConfiguration();

    /**
     * New ResourceChangeScannerConfiguration, using the given Properties, which can be used to update this service.
     * @return
     */
    public ResourceChangeScannerConfiguration newResourceChangeScannerConfiguration(Properties properties);

    /**
     * Reconfigure the Scanner now
     * @param configuration
     */
    public void configure(ResourceChangeScannerConfiguration configuration);

    /** 
     * Execute a disk scan of subscribed resources now.
     */
    public void scan();

    /**
     * Start the service, this creates a new Thread.
     */
    public void start();

    /**
     * Stop the service.
     */
    public void stop();

    /**
     * Set scan interval in seconds
     * @param interval
     */
    public void setInterval(int interval);

}
