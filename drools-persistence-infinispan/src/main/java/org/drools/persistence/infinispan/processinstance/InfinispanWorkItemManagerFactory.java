package org.drools.persistence.infinispan.processinstance;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.process.instance.WorkItemManager;
import org.drools.core.process.instance.WorkItemManagerFactory;

public class InfinispanWorkItemManagerFactory implements WorkItemManagerFactory {

    public WorkItemManager createWorkItemManager(InternalKnowledgeRuntime kruntime) {
        return new InfinispanWorkItemManager(kruntime);
    }

}
