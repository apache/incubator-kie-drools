package org.jbpm.process.workitem.handler;

import java.util.Map;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.NodeInstanceContainer;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.drools.spi.ProcessContext;
import org.jbpm.workflow.instance.node.WorkItemNodeInstance;

public class JavaHandlerWorkItemHandler implements WorkItemHandler {

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
        } catch (ClassNotFoundException e) {
            System.err.println(e);
        } catch (InstantiationException e) {
            System.err.println(e);
        } catch (IllegalAccessException e) {
            System.err.println(e);
        }
        manager.abortWorkItem(workItem.getId());
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
