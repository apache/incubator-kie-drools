package org.drools.io;

import org.drools.event.io.ResourceChangeNotifier;

public interface ResourceChangeMonitor {
    void subscribeNotifier(ResourceChangeNotifier notifier, Resource resource);
    void unsubscribeNotifier(ResourceChangeNotifier notifier, Resource resource);
}
