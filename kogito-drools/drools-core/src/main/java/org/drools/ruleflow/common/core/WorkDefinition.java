package org.drools.ruleflow.common.core;

import java.util.Set;

public interface WorkDefinition {
    
    String getName();
//    void setName(String name);
    
    Set getParameters();
//    void setParameters(Set parameters);
//    void addParameter(ParameterDefinition parameter);
//    void removeParameter(String name);
    String[] getParameterNames();
    ParameterDefinition getParameter(String name);
    
    Set getResults();
//    void setResults(Set results);
//    void addResult(ParameterDefinition result);
//    void removeResult(String name);
    String[] getResultNames();
    ParameterDefinition getResult(String name);
    
}
