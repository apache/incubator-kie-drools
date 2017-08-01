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
package org.jbpm.process.instance.impl;

import java.io.Serializable;
import java.util.Collection;

import org.jbpm.workflow.instance.node.CompositeNodeInstance;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessContext;
import org.kie.api.runtime.process.WorkflowProcessInstance;

public class CancelNodeInstanceAction implements Action, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String attachedToNodeId;
	
	public CancelNodeInstanceAction(String attachedToNodeId) {
		super();
		this.attachedToNodeId = attachedToNodeId;
	}
	
	public void execute(ProcessContext context) throws Exception {
		WorkflowProcessInstance pi = context.getNodeInstance().getProcessInstance();
		NodeInstance nodeInstance = findNodeByUniqueId(pi.getNodeInstances(), attachedToNodeId);
		if (nodeInstance != null) {
		    ((org.jbpm.workflow.instance.NodeInstance)nodeInstance).cancel();
		}
	}
	
	private NodeInstance findNodeByUniqueId(Collection<NodeInstance> nodeInstances, String uniqueId) {

        if (nodeInstances != null && !nodeInstances.isEmpty()) {
            for (NodeInstance nInstance : nodeInstances) {
                String nodeUniqueId = (String) nInstance.getNode().getMetaData().get("UniqueId");
                if (uniqueId.equals(nodeUniqueId)) {
                    return nInstance;
                }
                if (nInstance instanceof CompositeNodeInstance) {
                    NodeInstance nodeInstance = findNodeByUniqueId(((CompositeNodeInstance) nInstance).getNodeInstances(), uniqueId);
                    if (nodeInstance != null) {
                        return nodeInstance;
                    }
                }
            }
        }
        return null;
    }

}
