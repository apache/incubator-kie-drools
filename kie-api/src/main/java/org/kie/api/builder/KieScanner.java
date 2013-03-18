package org.kie.api.builder;

/**
 * A KieScanner is a scanner of the maven repositories (both local and remote)
 * used to automatically discover if there are new releases for a given KieModule and its dependencies
 * and eventually deploy them in the KieRepository
 */
public interface KieScanner {

    /**
     * Starts this KieScanner polling the maven repositories with the given interval expressed in milliseconds
     * @throws An IllegalStateException if this KieScanner has been already started
     */
    void start(long pollingInterval);

    /**
     * Stops this KieScanner
     */
    void stop();

    /**
     * Triggers a synchronous scan
     */
    void scanNow();
}
