package org.drools.persistence.processinstance;

import java.util.Set;

import org.drools.core.process.instance.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;

public interface InternalWorkItemManager extends WorkItemManager {

	void clearWorkItems();

	Set<WorkItem> getWorkItems();
}
