package org.drools.ruleflow.common.core.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.ruleflow.common.core.Work;

public class WorkImpl implements Work {
    
    private String name;
    private Map parameters = new HashMap();
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setParameter(String name, Object value) {
    	if (name == null) {
    		throw new NullPointerException("Parameter name is null");
    	}
		parameters.put(name, value);
    }
    
    public void setParameters(Map parameters) {
        if (parameters == null) {
            throw new NullPointerException();
        }
        this.parameters = new HashMap(parameters);
    }
    
    public Object getParameter(String name) {
        if (name == null) {
            throw new NullPointerException("Parameter name is null");
        }
        return parameters.get(name);
    }
    
    public Map getParameters() {
        return Collections.unmodifiableMap(parameters);
    }
    
    public String toString() {
        return "Task " + name;
    }
}
