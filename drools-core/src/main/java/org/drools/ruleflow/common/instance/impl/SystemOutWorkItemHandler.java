package org.drools.ruleflow.common.instance.impl;

import org.drools.ruleflow.common.instance.WorkItem;
import org.drools.ruleflow.common.instance.WorkItemHandler;
import org.drools.ruleflow.common.instance.WorkItemManager;

public class SystemOutWorkItemHandler implements WorkItemHandler {

    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        System.out.println("Executing work item " + workItem);
        manager.completeWorkItem(workItem.getId(), null);
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        System.out.println("Aborting work item " + workItem);
        manager.abortWorkItem(workItem.getId());
    }

}
