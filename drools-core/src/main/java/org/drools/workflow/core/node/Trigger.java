package org.drools.workflow.core.node;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.process.core.context.variable.Mappable;

public class Trigger implements Mappable, Serializable {
	
	private static final long serialVersionUID = 4L;
	
	private Map<String, String> inMapping = new HashMap<String, String>();

    public void addInMapping(String subVariableName, String variableName) {
        inMapping.put(subVariableName, variableName);
    }
    
    public void setInMappings(Map<String, String> inMapping) {
        this.inMapping = inMapping;
    }
    
    public String getInMapping(String parameterName) {
        return inMapping.get(parameterName);
    }

    public Map<String, String> getInMappings() {
        return Collections.unmodifiableMap(inMapping);
    }
    
    public void addOutMapping(String subVariableName, String variableName) {
        throw new IllegalArgumentException(
    		"A trigger does not support out mappings");
    }
    
    public void setOutMappings(Map<String, String> outMapping) {
        throw new IllegalArgumentException(
			"A trigger does not support out mappings");
    }
    
    public String getOutMapping(String parameterName) {
        throw new IllegalArgumentException(
			"A trigger does not support out mappings");
    }

    public Map<String, String> getOutMappings() {
        throw new IllegalArgumentException(
        	"A trigger does not support out mappings");
    }

}
