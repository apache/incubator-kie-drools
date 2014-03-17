package org.drools.impl.adapters;

import java.util.Map;

import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

public class WorkItemManagerAdapter implements WorkItemManager {
	
	public org.kie.api.runtime.process.WorkItemManager delegate;
	
	public WorkItemManagerAdapter(org.kie.api.runtime.process.WorkItemManager delegate) {
		this.delegate = delegate;
	}

	public void completeWorkItem(long id, Map<String, Object> results) {
		delegate.completeWorkItem(id, results);
	}

	public void abortWorkItem(long id) {
		delegate.abortWorkItem(id);
	}

	public void registerWorkItemHandler(String workItemName,
			WorkItemHandler handler) {
		delegate.registerWorkItemHandler(workItemName, new WorkItemHandlerAdapter(handler));
	}

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WorkItemManagerAdapter && delegate.equals(((WorkItemManagerAdapter)obj).delegate);
    }
}
