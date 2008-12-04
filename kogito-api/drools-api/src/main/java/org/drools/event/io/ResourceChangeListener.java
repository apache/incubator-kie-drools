/**
 * 
 */
package org.drools.event.io;

import org.drools.KnowledgeBaseChangeSet;

public interface ResourceChangeListener {
    void resourceChanged(KnowledgeBaseChangeSet changeSet);
}