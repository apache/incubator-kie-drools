package org.drools.process.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.process.instance.ProcessInstance;
import org.drools.WorkingMemory;

public class StartProcessCommand implements Command<ProcessInstance> {
	
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

	public ProcessInstance execute(WorkingMemory workingMemory) {
		if (data != null) {
			for (Object o: data) {
				workingMemory.insert(o);
			}
		}
		ProcessInstance processInstance = ( ProcessInstance ) workingMemory.startProcess(processId, parameters);
		workingMemory.fireAllRules();
		return processInstance;
	}

}
