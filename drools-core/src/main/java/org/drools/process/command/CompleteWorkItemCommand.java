package org.drools.process.command;

import java.util.HashMap;
import java.util.Map;

import org.drools.reteoo.ReteooWorkingMemory;

public class CompleteWorkItemCommand implements Command<Object> {
	
	private long workItemId;
	private Map<String, Object> results = new HashMap<String, Object>();
	
	
	public CompleteWorkItemCommand() {
	    
	}
	
	public CompleteWorkItemCommand(long workItemId,
                                   Map<String, Object> results) {
        this.workItemId = workItemId;
        this.results = results;
    }

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

	public Object execute(ReteooWorkingMemory session) {
		session.getWorkItemManager().completeWorkItem(workItemId, results);
		return null;
	}

	public String toString() {
		String result = "session.getWorkItemManager().completeWorkItem(" + workItemId + ", [";
		if (results != null) {
			int i = 0;
			for (Map.Entry<String, Object> entry: results.entrySet()) {
				if (i++ > 0) {
					result += ", ";
				}
				result += entry.getKey() + "=" + entry.getValue();
			}
		}
		result += "]);";
		return result;
	}

}
