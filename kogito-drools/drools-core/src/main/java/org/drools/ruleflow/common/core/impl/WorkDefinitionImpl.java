package org.drools.ruleflow.common.core.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.drools.ruleflow.common.core.ParameterDefinition;
import org.drools.ruleflow.common.core.WorkDefinition;

public class WorkDefinitionImpl implements WorkDefinition {
    
    private static final long serialVersionUID = 3977861786923710777L;
    
    private String name;
    private Map parameters = new HashMap();
    private Map results = new HashMap();

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Set getParameters() {
    	return new HashSet(parameters.values());        
    }
    
    public void setParameters(Set parameters) {
        this.parameters.clear();
        Iterator iterator = parameters.iterator();
        while (iterator.hasNext()) {
        	addParameter((ParameterDefinition) iterator.next());
        }        
    }
    
    public void addParameter(ParameterDefinition parameter) {
    	parameters.put(parameter.getName(), parameter);
    }
    
    public void removeParameter(String name) {
        parameters.remove(name);
    }
    
    public String[] getParameterNames() {
        return (String[]) parameters.keySet().toArray(new String[parameters.size()]);
    }
    
    public ParameterDefinition getParameter(String name) {
        return (ParameterDefinition) parameters.get(name);
    }
    
    public Set getResults() {
    	return new HashSet(results.values());
    }
    
    public void setResults(Set results) {
    	this.results.clear();
        Iterator it = results.iterator();
        while (it.hasNext()) {
        	addResult((ParameterDefinition) it.next());
        }   
    }
    
    public void addResult(ParameterDefinition result) {
        results.put(result.getName(), result);
    }
    
    public void removeResult(String name) {
        results.remove(name);
    }
    
    public String[] getResultNames() {
        return (String[]) results.keySet().toArray(new String[results.size()]);
    }
    
    public ParameterDefinition getResult(String name) {
        return (ParameterDefinition) results.get(name);
    }
    
    public String toString() {
        return name;
    }
}
