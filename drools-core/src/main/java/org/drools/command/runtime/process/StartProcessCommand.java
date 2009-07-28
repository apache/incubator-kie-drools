package org.drools.command.runtime.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.process.instance.ProcessInstance;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;

public class StartProcessCommand implements GenericCommand<ProcessInstance> {
	
	private String processId;
	private Map<String, Object> parameters = new HashMap<String, Object>();
	private List<Object> data = null;
	
	public String getProcessId() {
		return processId;
	}
	
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	
	public Map<String, Object> getParameters() {
		return parameters;
	}
	
	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}
	
	public List<Object> getData() {
		return data;
	}
	
	public void setData(List<Object> data) {
		this.data = data;
	}

    public ProcessInstance execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        
		if (data != null) {
			for (Object o: data) {
				ksession.insert(o);
			}
		}
		ProcessInstance processInstance = (ProcessInstance) ksession.startProcess(processId, parameters);
		return processInstance;
	}

	public String toString() {
		String result = "session.startProcess(" + processId + ", [";
		if (parameters != null) {
			int i = 0;
			for (Map.Entry<String, Object> entry: parameters.entrySet()) {
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
