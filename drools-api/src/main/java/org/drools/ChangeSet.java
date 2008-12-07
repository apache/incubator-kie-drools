package org.drools;

import java.util.Collection;

import org.drools.io.Resource;

/**
 * This class is used to provide a ChangeSet info to a ResourceChangeNotifier. It should be used when you implement the ResourceChangeMonitor interface.
 * Each method provides a Collection of removed, added and modified resources, and determined by the monitor. Drools currently only provides the
 * ResourceChangeScanner, which scans the local disk for changes. 
 * 
 * This interface, as well as ResourceChangeMonitor and ResourceChangeNotifier are still considered subject to change.
 */
public interface ChangeSet {
    public Collection<Resource> getResourcesRemoved();


    public Collection<Resource> getResourcesAdded();
    
    public Collection<Resource> getResourcesModified();

}
