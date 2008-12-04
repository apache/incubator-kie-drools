package org.drools.io;


public interface ResourceChangeMonitor {
    void subscribeNotifier(ResourceChangeNotifier notifier, Resource resource);
    void unsubscribeNotifier(ResourceChangeNotifier notifier, Resource resource);
}
