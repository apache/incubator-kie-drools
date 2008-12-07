/**
 * 
 */
package org.drools.event.io;

import org.drools.ChangeSet;

/**
 * Interface that provides informed on changes resources, via the ChangeSet interface.
 *
 *
 * This interface, as well as ChangeSet, ResourceChangeNotifier, ResourceChangeMonitor and ResourceChangeScanner are still considered subject to change. 
 * Use the XML format change-set, as
 * part of the ResourceType api when adding to KnowledgeBuilder, which is considered stable. KnowledgeBuilder currently ignored Added/Modified xml elements,
 * the KnowledgeAgent will use them, when rebuilding the KnowledgeBase.
 */
public interface ResourceChangeListener {
    
    /**
     * The Resource has changed, the ResourceChangeNotifier will call this method and execute the user implemented code.
     * 
     * @param changeSet
     */
    void resourcesChanged(ChangeSet changeSet);
}