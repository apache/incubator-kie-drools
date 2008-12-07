package org.drools;

import java.util.Collection;

import org.drools.io.Resource;

/**
 * This class is used to provide a ChangeSet info to a ResourceChangeNotifier. It should be used when you implement the ResourceChangeMonitor interface.
 * Each method provides a Collection of removed, added and modified resources, and determined by the monitor. Drools currently only provides the
 * ResourceChangeScanner, which scans the local disk for changes. 
 * 
 * This interface, as well as ResourceChangeMonitor, ResourceChangeNotifier, ResourceChangeScanner and ResourceChangeListener are still considered subject to change. 
 * Use the XML format change-set, as
 * part of the ResourceType api when adding to KnowledgeBuilder, which is considered stable. KnowledgeBuilder currently ignored Added/Modified xml elements,
 * the KnowledgeAgent will use them, when rebuilding the KnowledgeBase.
 * 
 */
public interface ChangeSet {
    public Collection<Resource> getResourcesRemoved();

    public Collection<Resource> getResourcesAdded();
    
    public Collection<Resource> getResourcesModified();

}
