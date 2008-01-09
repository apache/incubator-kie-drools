package org.drools.process.instance;

import java.util.Map;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface VariableScopeInstance {

    Map<String, Object> getVariables();
    
    Object getVariable(String name);
    
    void setVariable(String name, Object value);

}
