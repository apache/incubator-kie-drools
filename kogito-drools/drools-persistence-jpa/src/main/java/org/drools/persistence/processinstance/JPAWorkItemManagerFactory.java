package org.drools.persistence.processinstance;

import org.drools.common.InternalKnowledgeRuntime;
import org.drools.process.instance.WorkItemManager;
import org.drools.process.instance.WorkItemManagerFactory;

public class JPAWorkItemManagerFactory implements WorkItemManagerFactory {

	public WorkItemManager createWorkItemManager(InternalKnowledgeRuntime kruntime) {
		return new JPAWorkItemManager(kruntime);
	}

}
