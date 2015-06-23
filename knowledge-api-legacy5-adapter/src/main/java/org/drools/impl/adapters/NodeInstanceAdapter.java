/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.impl.adapters;

import org.drools.definition.process.Node;
import org.drools.runtime.process.NodeInstanceContainer;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.kie.api.runtime.process.NodeInstance;

public class NodeInstanceAdapter implements org.drools.runtime.process.NodeInstance {

	public NodeInstance delegate;
	
	public NodeInstanceAdapter(NodeInstance delegate) {
		this.delegate = delegate;
	}
	
	public NodeInstance getDelegate() {
		return delegate;
	}
	
	public long getId() {
		return delegate.getId();
	}

	public long getNodeId() {
		return delegate.getNodeId();
	}

	public Node getNode() {
		return new NodeAdapter(delegate.getNode());
	}

	public String getNodeName() {
		return delegate.getNodeName();
	}

	public WorkflowProcessInstance getProcessInstance() {
		return new WorkflowProcessInstanceAdapter(delegate.getProcessInstance());
	}

	public NodeInstanceContainer getNodeInstanceContainer() {
		return new NodeInstanceContainerAdapter(delegate.getNodeInstanceContainer());
	}

	public Object getVariable(String variableName) {
		return delegate.getVariable(variableName);
	}

	public void setVariable(String variableName, Object value) {
		delegate.setVariable(variableName, value);
	}

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NodeInstanceAdapter && delegate.equals(((NodeInstanceAdapter)obj).delegate);
    }
}
