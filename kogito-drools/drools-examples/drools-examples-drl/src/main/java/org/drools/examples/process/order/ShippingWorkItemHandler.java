package org.drools.examples.process.order;

import java.util.HashMap;
import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.process.instance.WorkItemHandler;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemManager;

public class ShippingWorkItemHandler implements WorkItemHandler {

	private WorkingMemory workingMemory;
	private int trackingCounter;
	
	public ShippingWorkItemHandler(WorkingMemory workingMemory) {
		this.workingMemory = workingMemory;
	}
	
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		Order order = (Order) workItem.getParameter("order");
		System.out.println("Shipping order " + order.getOrderId());
		order.setTrackingId("Tracking-" + ++trackingCounter);
		workingMemory.update(workingMemory.getFactHandle(order), order);
		Map<String, Object> results = new HashMap<String, Object>();
		results.put("trackingId", order.getTrackingId());
		manager.completeWorkItem(workItem.getId(), results);
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
	}

}
