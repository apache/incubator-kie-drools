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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.runtime.process.NodeInstance;
import org.kie.api.runtime.process.NodeInstanceContainer;

public class NodeInstanceContainerAdapter implements org.drools.runtime.process.NodeInstanceContainer {

	public NodeInstanceContainer delegate;
	
	public NodeInstanceContainerAdapter(NodeInstanceContainer delegate) {
		this.delegate = delegate;
	}

	public NodeInstanceContainer getDelegate() {
		return delegate;
	}
	
	public Collection<NodeInstance> getNodeInstances() {
		Collection<org.kie.api.runtime.process.NodeInstance> nodeInstances = delegate.getNodeInstances();
		if (nodeInstances == null) {
			return null;
		}
		List<NodeInstance> result = new ArrayList<NodeInstance>();
		for (org.kie.api.runtime.process.NodeInstance nodeInstance: nodeInstances) {
			result.add(new NodeInstanceAdapter(nodeInstance));
		}
		return result;
	}

	public NodeInstance getNodeInstance(long nodeInstanceId) {
		org.kie.api.runtime.process.NodeInstance nodeInstance = delegate.getNodeInstance(nodeInstanceId);
		if (nodeInstance == null) {
			return null;
		} else {
			return new NodeInstanceAdapter(nodeInstance);
		}
	}

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NodeInstanceContainerAdapter && delegate.equals(((NodeInstanceContainerAdapter)obj).delegate);
    }
}
