package org.jbpm.kie.services.test.objects;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class KieConteinerSystemOutWorkItemHandler implements WorkItemHandler {

	private KieContainer kieContainer;
	
	public KieConteinerSystemOutWorkItemHandler(KieContainer kieContainer) {
		super();
		this.kieContainer = kieContainer;
	}

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		System.out.println("Executing work item " + workItem + " with handler with injected " + kieContainer);
		if (kieContainer == null) {
			throw new IllegalArgumentException("No kieContainer found");
		}
		manager.completeWorkItem(workItem.getId(), null);
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
