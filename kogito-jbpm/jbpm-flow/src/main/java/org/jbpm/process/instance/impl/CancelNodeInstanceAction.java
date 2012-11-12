/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.instance.impl;

import java.io.Serializable;

import org.kie.runtime.process.NodeInstance;
import org.kie.runtime.process.ProcessContext;
import org.kie.runtime.process.WorkflowProcessInstance;

public class CancelNodeInstanceAction implements Action, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long attachedToNodeId;
	
	public CancelNodeInstanceAction(Long attachedToNodeId) {
		super();
		this.attachedToNodeId = attachedToNodeId;
	}
	
	public void execute(ProcessContext context) throws Exception {
		WorkflowProcessInstance pi = context.getNodeInstance().getProcessInstance();
		long nodeInstanceId = -1;
		for (NodeInstance nodeInstance : pi.getNodeInstances()) {
			if (attachedToNodeId == nodeInstance.getNodeId()) {
				nodeInstanceId = nodeInstance.getId();
				break;
			}
		}
		((org.jbpm.workflow.instance.NodeInstance)context.getNodeInstance().getProcessInstance().getNodeInstance(nodeInstanceId)).cancel();
	}

}
