package org.jbpm.integrationtests.handler;

import org.kie.runtime.process.*;

public class TestWorkItemHandler implements WorkItemHandler {
    private WorkItem workItem;
    private boolean aborted = false; 
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        this.workItem = workItem;
    }
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        aborted = true;
    }
    public WorkItem getWorkItem() {
        return workItem;
    }
    public boolean isAborted() {
        return aborted;
    }
    public void reset() {
        workItem = null;
    }
}