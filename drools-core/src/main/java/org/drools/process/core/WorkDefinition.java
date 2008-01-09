package org.drools.process.core;

import java.util.Set;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface WorkDefinition {
    
    String getName();
    
    Set<ParameterDefinition> getParameters();
    String[] getParameterNames();
    ParameterDefinition getParameter(String name);
    
    Set<ParameterDefinition> getResults();
    String[] getResultNames();
    ParameterDefinition getResult(String name);
    
}
