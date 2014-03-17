package org.drools.impl.adapters;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class WorkItemHandlerAdapter implements WorkItemHandler {

	private org.drools.runtime.process.WorkItemHandler delegate;
	
	public WorkItemHandlerAdapter(org.drools.runtime.process.WorkItemHandler delegate) {
		this.delegate = delegate;
	}
	
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		delegate.executeWorkItem(new WorkItemAdapter(workItem), new WorkItemManagerAdapter(manager));
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		delegate.abortWorkItem(new WorkItemAdapter(workItem), new WorkItemManagerAdapter(manager));
	}

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WorkItemHandlerAdapter && delegate.equals(((WorkItemHandlerAdapter)obj).delegate);
    }
}
