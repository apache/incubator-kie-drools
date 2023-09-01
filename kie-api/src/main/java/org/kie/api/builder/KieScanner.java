package org.kie.api.builder;

import java.util.Collection;

import org.kie.api.event.kiescanner.KieScannerEventListener;

/**
 * A KieScanner is a scanner of the maven repositories (both local and remote)
 * used to automatically discover if there are new releases for a given KieModule and its dependencies
 * and eventually deploy them in the KieRepository
 */
public interface KieScanner {

    enum Status {
        STARTING, SCANNING, UPDATING, RUNNING, STOPPED, SHUTDOWN
    }
    
    /**
     * Starts this KieScanner polling the maven repositories with the given interval expressed in milliseconds
     * throws An IllegalStateException if this KieScanner has been already started
     */
    void start(long pollingInterval);

    /**
     * Stops this KieScanner, but does not release the resources. A call to {@link #start(long)} will
     * resume the scanner's work after a call to {@link #stop()}
     */
    void stop();

    /**
     * Shuts down the scanner and releases any resources held. After a shutdown call,
     * any call to start() will fail with an exception.
     */
    void shutdown();

    /**
     * Triggers a synchronous scan
     */
    void scanNow();

    /**
     * Add an event listener.
     *
     * @param listener
     *            The listener to add.
     */
    void addListener(KieScannerEventListener listener);

    /**
     * Remove an event listener.
     *
     * @param listener
     *            The listener to remove.
     */
    void removeListener(KieScannerEventListener listener);

    /**
     * Returns all event listeners.
     *
     * @return listeners The listeners.
     */
    Collection<KieScannerEventListener> getListeners();
}
