package org.jbpm.process.workitem.handler;

import java.util.Map;

import org.drools.spi.ProcessContext;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.node.WorkItemNodeInstance;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.NodeInstance;
import org.kie.runtime.process.NodeInstanceContainer;
import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemHandler;
import org.kie.runtime.process.WorkItemManager;

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
