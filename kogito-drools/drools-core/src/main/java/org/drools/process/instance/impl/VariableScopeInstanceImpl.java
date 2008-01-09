package org.drools.process.instance.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.process.instance.VariableScopeInstance;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class VariableScopeInstanceImpl implements VariableScopeInstance, Serializable {

    private static final long serialVersionUID = 400L;
    
    private Map<String, Object> variables = new HashMap<String, Object>();

    public Object getVariable(String name) {
        return variables.get(name);
    }

    public Map<String, Object> getVariables() {
        return Collections.unmodifiableMap(variables);
    }

    public void setVariable(String name, Object value) {
        if (name == null) {
            throw new IllegalArgumentException(
                "The name of a variable may not be null!");
        }
        variables.put(name, value);
    }
    
}
