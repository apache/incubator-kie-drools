/**
 * 
 */
package org.drools.event.io;

import org.drools.ChangeSet;

public interface ResourceChangeListener {
    void resourcesChanged(ChangeSet changeSet);
}