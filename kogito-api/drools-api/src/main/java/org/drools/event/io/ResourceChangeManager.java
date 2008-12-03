package org.drools.event.io;

import java.util.Collection;

import org.drools.definition.KnowledgeDefinition;
import org.drools.io.Resource;
import org.drools.io.ResourceChangeMonitor;

public interface ResourceChangeManager {
    void subscribeResourceChangeListener(ResourceChangeListener listener,
                                         Resource resource);

    void unsubscribeResourceChangeListener(ResourceChangeListener listener,
                                           Resource resource);

    void addResourceChangeMonitor(ResourceChangeMonitor monitor);

    void removeResourceChangeMonitor(ResourceChangeMonitor monitor);

    Collection<ResourceChangeMonitor> getResourceChangeMonitor();
}
