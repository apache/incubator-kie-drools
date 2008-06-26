package org.drools.workflow.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DroolsAction implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
    private Map<String, Object> metaData = new HashMap<String, Object>();

    public void setMetaData(String name, Object value) {
        this.metaData.put(name, value);
    }
    
    public Object getMetaData(String name) {
        return this.metaData.get(name);
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
}
