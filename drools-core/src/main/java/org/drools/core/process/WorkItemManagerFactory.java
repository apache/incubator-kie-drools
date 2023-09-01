package org.drools.core.process;

import org.drools.core.common.InternalKnowledgeRuntime;

public interface WorkItemManagerFactory {

    WorkItemManager createWorkItemManager(InternalKnowledgeRuntime kruntime);

}
