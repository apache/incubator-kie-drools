package org.jbpm.process.workitem.esb;

import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemHandler;
import org.kie.runtime.process.WorkItemManager;

public class ESBServiceWorkItemHandler implements WorkItemHandler {

	@SuppressWarnings("unchecked")
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
//		Message message = MessageFactory.getInstance().getMessage();
//	    Body body = message.getBody();
//	    
//	    Map<String, Object> parameters = (Map<String, Object>) workItem.getParameter("Parameters");
//	    if (parameters != null) {
//	    	for (Map.Entry<String, Object> entry: parameters.entrySet()) {
//	    		body.add(entry.getKey(), entry.getValue());
//	    	}
//	    }
//	    String category = (String) workItem.getParameter("Category");
//	    String service = (String) workItem.getParameter("Service");
//	    
//	    ServiceInvoker invoker;
//		try {
//			invoker = new ServiceInvoker(category, service);
//		    invoker.deliverAsync(message);
//			manager.completeWorkItem(workItem.getId(), null);
//		} catch (MessageDeliverException e) {
//			e.printStackTrace();
//			manager.abortWorkItem(workItem.getId());
//		}
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// Do nothing
	}

}
