package org.drools.process.command;

import java.util.HashMap;
import java.util.Map;

import org.drools.WorkingMemory;

public class CompleteWorkItemCommand implements Command<Object> {
	
	private long workItemId;
	private Map<String, Object> results = new HashMap<String, Object>();
	
	public long getWorkItemId() {
		return workItemId;
	}

	public void setWorkItemId(long workItemId) {
		this.workItemId = workItemId;
	}

	public Map<String, Object> getResults() {
		return results;
	}

	public void setResults(Map<String, Object> results) {
		this.results = results;
	}

	public Object execute(WorkingMemory workingMemory) {
		workingMemory.getWorkItemManager().completeWorkItem(workItemId, results);
		return null;
	}

}
