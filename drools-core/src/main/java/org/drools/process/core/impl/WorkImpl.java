package org.drools.process.core.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.process.core.Work;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class WorkImpl implements Work, Serializable {
    
    private static final long serialVersionUID = 400L;
    
    private String name;
    private Map<String, Object> parameters = new HashMap<String, Object>();
    
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
    
    public void setParameters(Map<String, Object> parameters) {
        if (parameters == null) {
            throw new NullPointerException();
        }
        this.parameters = new HashMap<String, Object>(parameters);
    }
    
    public Object getParameter(String name) {
        if (name == null) {
            throw new NullPointerException("Parameter name is null");
        }
        return parameters.get(name);
    }
    
    public Map<String, Object> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }
    
    public String toString() {
        return "Work " + name;
    }
}
