package org.drools.examples.process.order;

import java.util.HashMap;
import java.util.Map;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

public class ShippingWorkItemHandler implements WorkItemHandler {

	private StatefulKnowledgeSession ksession;
	private int trackingCounter;
	
	public ShippingWorkItemHandler(StatefulKnowledgeSession ksession) {
		this.ksession = ksession;
	}
	
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		Order order = (Order) workItem.getParameter("order");
		System.out.println("Shipping order " + order.getOrderId());
		order.setTrackingId("Tracking-" + ++trackingCounter);
		ksession.update(ksession.getFactHandle(order), order);
		Map<String, Object> results = new HashMap<String, Object>();
		results.put("trackingId", order.getTrackingId());
		manager.completeWorkItem(workItem.getId(), results);
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
	}

}
