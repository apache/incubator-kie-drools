/**
 * 
 */
package org.drools.event.io;



public interface ResourceChangeListener {
    public void resourceAdded(ResourceModifiedEvent event);
    public void resourceModified(ResourceModifiedEvent event);
    public void resourceRemoved(ResourceModifiedEvent event);
}