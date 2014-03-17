package org.drools.impl.adapters;

import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import java.util.Map;

public class WorkItemManagerKieAdapter implements WorkItemManager {

    public org.drools.runtime.process.WorkItemManager delegate;

    public WorkItemManagerKieAdapter(org.drools.runtime.process.WorkItemManager delegate) {
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
        delegate.registerWorkItemHandler(workItemName, new WorkItemHandlerKieAdapter(handler));
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WorkItemManagerKieAdapter && delegate.equals(((WorkItemManagerKieAdapter)obj).delegate);
    }
}
