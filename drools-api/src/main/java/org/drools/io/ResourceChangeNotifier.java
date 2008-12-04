package org.drools.io;

import java.util.Collection;

import org.drools.KnowledgeBaseChangeSet;
import org.drools.event.io.ResourceChangeListener;

public interface ResourceChangeNotifier {
    void subscribeResourceChangeListener(ResourceChangeListener listener,
                                         Resource resource);

    void unsubscribeResourceChangeListener(ResourceChangeListener listener,
                                           Resource resource);
    
    void subscribeChildResource(Resource directory, Resource child);    

    void addResourceChangeMonitor(ResourceChangeMonitor monitor);

    void removeResourceChangeMonitor(ResourceChangeMonitor monitor);

    Collection<ResourceChangeMonitor> getResourceChangeMonitor();
    
    public void publishKnowledgeBaseChangeSet(KnowledgeBaseChangeSet changeSet);
}
