package org.drools.persistence.jpa.processinstance;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.process.WorkItemManager;
import org.drools.core.process.WorkItemManagerFactory;

public class JPAWorkItemManagerFactory implements WorkItemManagerFactory {

    public WorkItemManager createWorkItemManager(InternalKnowledgeRuntime kruntime) {
        return new JPAWorkItemManager(kruntime);
    }

}
