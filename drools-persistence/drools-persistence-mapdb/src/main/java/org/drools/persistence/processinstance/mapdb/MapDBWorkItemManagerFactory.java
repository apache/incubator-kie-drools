package org.drools.persistence.processinstance.mapdb;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.process.instance.WorkItemManager;
import org.drools.core.process.instance.WorkItemManagerFactory;

public class MapDBWorkItemManagerFactory implements WorkItemManagerFactory {

	@Override
	public WorkItemManager createWorkItemManager(
			InternalKnowledgeRuntime kruntime) {
		return new MapDBWorkItemManager(kruntime);
	}

}
