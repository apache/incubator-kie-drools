package org.drools.command.runtime.process;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.runtime.StatefulKnowledgeSession;

@XmlAccessorType(XmlAccessType.NONE)
public class CompleteWorkItemCommand implements GenericCommand<Object> {
	
	@XmlAttribute(name="id", required = true)
	private long workItemId;
	private Map<String, Object> results = new HashMap<String, Object>();
	
	public CompleteWorkItemCommand() {}
	
	public CompleteWorkItemCommand(long workItemId) {
		this.workItemId = workItemId;
	}
	
	public CompleteWorkItemCommand(long workItemId, Map<String, Object> results) {
		this(workItemId);
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

    public Void execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
		ksession.getWorkItemManager().completeWorkItem(workItemId, results);
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
