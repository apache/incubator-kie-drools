package org.jbpm.test.wih;

import java.util.HashMap;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class FirstErrorWorkItemHandler implements WorkItemHandler {

    private boolean first = true;

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        if (first) {
            first = false;
            throw new RuntimeException("Error");
        }
        manager.completeWorkItem(workItem.getId(), new HashMap<String, Object>());
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        manager.abortWorkItem(workItem.getId());
    }

}
