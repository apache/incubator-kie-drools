/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.process.core.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.drools.process.core.ParameterDefinition;
import org.drools.process.core.WorkDefinition;

public class WorkDefinitionImpl implements WorkDefinition, Serializable {
    
    private static final long serialVersionUID = 510l;
    
    private String name;
    private Map<String, ParameterDefinition> parameters = new HashMap<String, ParameterDefinition>();
    private Map<String, ParameterDefinition> results = new HashMap<String, ParameterDefinition>();

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Set<ParameterDefinition> getParameters() {
        return new HashSet<ParameterDefinition>(parameters.values());
    }
    
    public void setParameters(Set<ParameterDefinition> parameters) {
        this.parameters.clear();
        Iterator<ParameterDefinition> iterator = parameters.iterator();
        while (iterator.hasNext()) {
            addParameter(iterator.next());
        }
    }
    
    public void addParameter(ParameterDefinition parameter) {
        parameters.put(parameter.getName(), parameter);
    }
    
    public void removeParameter(String name) {
        parameters.remove(name);
    }
    
    public String[] getParameterNames() {
        return parameters.keySet().toArray(new String[parameters.size()]);
    }
    
    public ParameterDefinition getParameter(String name) {
        return parameters.get(name);
    }
    
    public Set<ParameterDefinition> getResults() {
        return new HashSet<ParameterDefinition>(results.values());
    }
    
    public void setResults(Set<ParameterDefinition> results) {
        this.results.clear();
        Iterator<ParameterDefinition> it = results.iterator();
        while (it.hasNext()) {
            addResult(it.next());
        }
    }
    
    public void addResult(ParameterDefinition result) {
        results.put(result.getName(), result);
    }
    
    public void removeResult(String name) {
        results.remove(name);
    }
    
    public String[] getResultNames() {
        return results.keySet().toArray(new String[results.size()]);
    }
    
    public ParameterDefinition getResult(String name) {
        return results.get(name);
    }
    
    public String toString() {
        return name;
    }
}
