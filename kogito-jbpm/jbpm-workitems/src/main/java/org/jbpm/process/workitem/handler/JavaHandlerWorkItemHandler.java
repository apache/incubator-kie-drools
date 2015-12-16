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

package org.jbpm.process.workitem.handler;

import java.util.Map;

import org.drools.core.spi.ProcessContext;
import org.jbpm.process.workitem.AbstractLogOrThrowWorkItemHandler;
import org.jbpm.workflow.instance.node.WorkItemNodeInstance;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.NodeInstanceContainer;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.process.WorkflowProcessInstance;

public class JavaHandlerWorkItemHandler extends AbstractLogOrThrowWorkItemHandler {

	private StatefulKnowledgeSession ksession;
	
	public JavaHandlerWorkItemHandler(StatefulKnowledgeSession ksession) {
		this.ksession = ksession;
	}
	
	@SuppressWarnings("unchecked")
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		String className = (String) workItem.getParameter("Class");
		try {
			Class<JavaHandler> c = (Class<JavaHandler>) Class.forName(className);
			JavaHandler handler = c.newInstance();
			ProcessContext kcontext = new ProcessContext(ksession);
			WorkflowProcessInstance processInstance = (WorkflowProcessInstance) 
				ksession.getProcessInstance(workItem.getProcessInstanceId());
			kcontext.setProcessInstance(processInstance);
			WorkItemNodeInstance nodeInstance = findNodeInstance(workItem.getId(), processInstance);
			kcontext.setNodeInstance(nodeInstance);
			Map<String, Object> results = handler.execute(kcontext);
			
            manager.completeWorkItem(workItem.getId(), results);
    		return;
        } catch (ClassNotFoundException cnfe) {
            handleException(cnfe);
        } catch (InstantiationException ie) {
            handleException(ie);
        } catch (IllegalAccessException iae) {
            handleException(iae);
        }
	}
	
	private WorkItemNodeInstance findNodeInstance(long workItemId, NodeInstanceContainer container) {
		for (NodeInstance nodeInstance: container.getNodeInstances()) {
			if (nodeInstance instanceof WorkItemNodeInstance) {
				WorkItemNodeInstance workItemNodeInstance = (WorkItemNodeInstance) nodeInstance;
				if (workItemNodeInstance.getWorkItem().getId() == workItemId) {
					return workItemNodeInstance;
				}
			}
			if (nodeInstance instanceof NodeInstanceContainer) {
				WorkItemNodeInstance result = findNodeInstance(workItemId, ((NodeInstanceContainer) nodeInstance));
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		// Do nothing
	}

}
