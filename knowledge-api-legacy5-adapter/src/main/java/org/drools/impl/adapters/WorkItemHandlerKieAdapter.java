package org.drools.impl.adapters;

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemManager;
import org.kie.api.runtime.process.WorkItemHandler;

public class WorkItemHandlerKieAdapter implements org.drools.runtime.process.WorkItemHandler {

    private WorkItemHandler delegate;

    public WorkItemHandlerKieAdapter(WorkItemHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        delegate.executeWorkItem(new WorkItemKieAdapter(workItem), new WorkItemManagerKieAdapter(manager));
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        delegate.abortWorkItem(new WorkItemKieAdapter(workItem), new WorkItemManagerKieAdapter(manager));
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WorkItemHandlerKieAdapter && delegate.equals(((WorkItemHandlerKieAdapter)obj).delegate);
    }
}
