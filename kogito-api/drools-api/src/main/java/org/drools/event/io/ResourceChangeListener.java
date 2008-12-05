/**
 * 
 */
package org.drools.event.io;

import org.drools.ChangeSet;

public interface ResourceChangeListener {
    void resourceChanged(ChangeSet changeSet);
}