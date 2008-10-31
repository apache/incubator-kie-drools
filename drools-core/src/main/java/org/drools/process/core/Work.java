package org.drools.process.core;

import java.util.Map;
import java.util.Set;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface Work {

    void setName(String name);
    String getName();
    
    void setParameter(String name, Object value);
    void setParameters(Map<String, Object> parameters);
    Object getParameter(String name);
    Map<String, Object> getParameters();
    
    void addParameterDefinition(ParameterDefinition parameterDefinition);
    void setParameterDefinitions(Set<ParameterDefinition> parameterDefinitions);
    Set<ParameterDefinition> getParameterDefinitions();
    String[] getParameterNames();
    ParameterDefinition getParameterDefinition(String name);
    
}
