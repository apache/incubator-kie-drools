/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.workflow.instance.impl;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.workflow.instance.NodeInstance;
import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.impl.ImmutableDefaultFactory;
import org.mvel2.integration.impl.SimpleValueResolver;

public class NodeInstanceResolverFactory extends ImmutableDefaultFactory {

	private static final long serialVersionUID = 510l;
	
	private NodeInstance nodeInstance;
	
	private Map<String, Object> extraParameters = new HashMap<String, Object>();
	
	public NodeInstanceResolverFactory(NodeInstance nodeInstance) {
		this.nodeInstance = nodeInstance;
		this.extraParameters.put("nodeInstance", nodeInstance);
		if (nodeInstance.getProcessInstance() != null) {
		    this.extraParameters.put("processInstance", nodeInstance.getProcessInstance());
		    this.extraParameters.put("processInstanceId", nodeInstance.getProcessInstance().getId());
		    this.extraParameters.put("parentProcessInstanceId", nodeInstance.getProcessInstance().getParentProcessInstanceId());
		}
	}

	public boolean isResolveable(String name) {
		boolean found = nodeInstance.resolveContextInstance(VariableScope.VARIABLE_SCOPE, name) != null;
		if (!found) {
		    return extraParameters.containsKey(name);
		}
		
		return found;
	}
	
	
	public VariableResolver getVariableResolver(String name) {
	    if (extraParameters.containsKey(name)) {
	        return new SimpleValueResolver(extraParameters.get(name));
	    }
	    
	    Object value = ((VariableScopeInstance)
			nodeInstance.resolveContextInstance(
					VariableScope.VARIABLE_SCOPE, name)).getVariable(name);
		return new SimpleValueResolver(value);
	}
	
}
