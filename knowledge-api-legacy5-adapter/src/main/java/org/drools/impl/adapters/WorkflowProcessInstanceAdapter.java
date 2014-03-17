package org.drools.impl.adapters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.runtime.process.NodeInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;

public class WorkflowProcessInstanceAdapter extends ProcessInstanceAdapter implements org.drools.runtime.process.WorkflowProcessInstance {

    public WorkflowProcessInstanceAdapter(WorkflowProcessInstance delegate) {
        super(delegate);
    }
    
    public WorkflowProcessInstance getDelegate() {
    	return (WorkflowProcessInstance) super.getDelegate();
    }

	public Collection<NodeInstance> getNodeInstances() {
		Collection<org.kie.api.runtime.process.NodeInstance> nodeInstances = getDelegate().getNodeInstances();
		if (nodeInstances == null) {
			return null;
		}
		List<NodeInstance> result = new ArrayList<NodeInstance>(nodeInstances.size());
		for (org.kie.api.runtime.process.NodeInstance nodeInstance: nodeInstances) {
			result.add(new NodeInstanceAdapter(nodeInstance));
		}
		return result;
	}

	public NodeInstance getNodeInstance(long nodeInstanceId) {
		org.kie.api.runtime.process.NodeInstance nodeInstance = getDelegate().getNodeInstance(nodeInstanceId);
		if (nodeInstance == null) {
			return null;
		} else {
			return new NodeInstanceAdapter(nodeInstance);
		}
	}

	public Object getVariable(String name) {
		return getDelegate().getVariable(name);
	}

	public void setVariable(String name, Object value) {
		getDelegate().setVariable(name, value);		
	}

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WorkflowProcessInstanceAdapter && delegate.equals(((WorkflowProcessInstanceAdapter)obj).delegate);
    }
}