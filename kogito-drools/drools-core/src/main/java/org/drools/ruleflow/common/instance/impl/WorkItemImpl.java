package org.drools.ruleflow.common.instance.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.drools.ruleflow.common.instance.WorkItem;

public class WorkItemImpl implements WorkItem {

	private long id;
    private String name;
    private int state = 0;
    private Map parameters = new HashMap();
    private Map results = new HashMap();
    private long processInstanceId;
    
    public void setId(long id) {
        this.id = id;
    }
    
    public long getId() {
        return id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setState(int state) {
        this.state = state;
    }
    
    public int getState() {
        return state;
    }
    
    public void setParameters(Map parameters) {
        this.parameters = parameters;
    }
    
    public Object getParameter(String name) {
        return parameters.get(name);
    }
    
    public Map getParameters() {
        return parameters;
    }
    
    public void setResults(Map results) {
        this.results = results;
    }
    
    public void setResult(String name, Object value) {
        results.put(name, value);
    }
    
    public Object getResult(String name) {
        return results.get(name);
    }
    
    public Map getResults() {
        return results;
    }
    
    public void setProcessInstanceId(long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }
    
    public long getProcessInstanceId() {
        return processInstanceId;
    }
    
    public String toString() {
    	StringBuilder b = new StringBuilder("WorkItem ");
    	b.append(id);
    	b.append(" [name=");
    	b.append(name);
    	b.append(", state=");
    	b.append(state);
    	b.append(", processInstanceId=");
    	b.append(processInstanceId);
        b.append(", parameters{");
    	for (Iterator iterator = parameters.entrySet().iterator(); iterator.hasNext(); ) {
    	    Map.Entry entry = (Map.Entry) iterator.next();
    	    b.append(entry.getKey());
    	    b.append("=");
    	    b.append(entry.getValue());
    	    if (iterator.hasNext()) {
    	        b.append(", ");
    	    }
    	}
        b.append("}]");
    	return b.toString();
    }
}