package org.drools.core.process.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.drools.core.process.WorkItem;

public class WorkItemImpl implements WorkItem, Serializable {

    private static final long serialVersionUID = 510l;
    
    private long id;
    private String name;
    private int state = 0;
    private Map<String, Object> parameters = new HashMap<>();
    private Map<String, Object> results = new HashMap<>();
    private String processInstanceId;
    private String deploymentId;
    private long nodeInstanceId;
    private long nodeId;
    
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
    
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
    
    public void setParameter(String name, Object value) {
        this.parameters.put(name, value);
    }
    
    public Object getParameter(String name) {
        return parameters.get(name);
    }
    
    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    public void setResults(Map<String, Object> results) {
        if (results != null) {
            this.results = results;
        }
    }
    
    public void setResult(String name, Object value) {
        results.put(name, value);
    }
    
    public Object getResult(String name) {
        return results.get(name);
    }
    
    public Map<String, Object> getResults() {
        return results;
    }
    
    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }
    
    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public long getNodeInstanceId() {
        return nodeInstanceId;
    }

    public void setNodeInstanceId(long nodeInstanceId) {
        this.nodeInstanceId = nodeInstanceId;
    }

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
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
        for (Iterator<Map.Entry<String, Object>> iterator = parameters.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Object> entry = iterator.next();
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
