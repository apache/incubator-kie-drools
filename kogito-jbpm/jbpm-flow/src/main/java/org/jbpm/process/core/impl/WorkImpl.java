/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.core.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jbpm.process.core.ParameterDefinition;
import org.jbpm.process.core.Work;

public class WorkImpl implements Work, Serializable {
    
    private static final long serialVersionUID = 510l;
    
    private String name;
    private Map<String, Object> parameters = new HashMap<String, Object>();
    private Map<String, ParameterDefinition> parameterDefinitions = new HashMap<String, ParameterDefinition>();
    
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

    public void setParameterDefinitions(Set<ParameterDefinition> parameterDefinitions) {
        this.parameterDefinitions.clear();
        for (ParameterDefinition parameterDefinition: parameterDefinitions) {
            addParameterDefinition(parameterDefinition);
        }
    }

    public void addParameterDefinition(ParameterDefinition parameterDefinition) {
        this.parameterDefinitions.put(parameterDefinition.getName(), parameterDefinition);
    }

    public Set<ParameterDefinition> getParameterDefinitions() {
        return new HashSet<ParameterDefinition>(parameterDefinitions.values());
    }

    public String[] getParameterNames() {
        return parameterDefinitions.keySet().toArray(new String[parameterDefinitions.size()]);
    }

    public ParameterDefinition getParameterDefinition(String name) {
        return parameterDefinitions.get(name);
    }
}
