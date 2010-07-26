/**
 * Copyright 2010 JBoss Inc
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
