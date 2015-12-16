/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
import java.util.List;

import org.drools.definition.process.Node;
import org.kie.api.definition.process.NodeContainer;

public class NodeContainerAdapter implements org.drools.definition.process.NodeContainer {

	public NodeContainer delegate;
	
	public NodeContainerAdapter(NodeContainer delegate) {
		this.delegate = delegate;
	}

	public NodeContainer getDelegate() {
		return delegate;
	}
	
	public Node[] getNodes() {
		List<Node> result = new ArrayList<Node>();
		for (org.kie.api.definition.process.Node node: delegate.getNodes()) {
			result.add(new NodeAdapter(node));
		}
		return result.toArray(new Node[result.size()]);
	}

	public Node getNode(long id) {
		return new NodeAdapter(delegate.getNode(id));
	}

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NodeContainerAdapter && delegate.equals(((NodeContainerAdapter)obj).delegate);
    }
}
