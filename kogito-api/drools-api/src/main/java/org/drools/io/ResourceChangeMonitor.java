package org.drools.io;

/**
 * 
 *  * This interface, as well as ResourceChangeNotifier are still considered subject to change.
 */
public interface ResourceChangeMonitor {
    void subscribeNotifier(ResourceChangeNotifier notifier, Resource resource);
    void unsubscribeNotifier(ResourceChangeNotifier notifier, Resource resource);
}
