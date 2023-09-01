package org.drools.persistence.processinstance;

import java.util.Set;

import org.drools.core.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;

/**
 * Exposes the work items outside of the manager.
 */
public interface InternalWorkItemManager extends WorkItemManager {

	void clearWorkItems();

	Set<WorkItem> getWorkItems();
}
