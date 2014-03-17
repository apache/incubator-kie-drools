package org.drools.impl.adapters;

import java.util.Map;

import org.drools.runtime.process.WorkItem;

public class WorkItemAdapter implements WorkItem {

	public org.kie.api.runtime.process.WorkItem delegate;
	
	public WorkItemAdapter(org.kie.api.runtime.process.WorkItem delegate) {
		this.delegate = delegate;
	}
	
	public long getId() {
		return delegate.getId();
	}

	public String getName() {
		return delegate.getName();
	}

	public int getState() {
		return delegate.getState();
	}

	public Object getParameter(String name) {
		return delegate.getParameter(name);
	}

	public Map<String, Object> getParameters() {
		return delegate.getParameters();
	}

	public Object getResult(String name) {
		return delegate.getResult(name);
	}

	public Map<String, Object> getResults() {
		return delegate.getResults();
	}

	public long getProcessInstanceId() {
		return delegate.getProcessInstanceId();
	}

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WorkItemAdapter && delegate.equals(((WorkItemAdapter)obj).delegate);
    }
}