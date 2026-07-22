/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jbpm.process.core.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.jbpm.process.core.ParameterDefinition;
import org.jbpm.process.core.Work;
import org.kie.kogito.process.workitems.WorkParametersFactory;

public class WorkImpl implements Work, Serializable {

    private static final long serialVersionUID = 510l;

    private String name;
    private Map<String, Object> parameters = new LinkedHashMap<>();
    private Map<String, ParameterDefinition> parameterDefinitions = new LinkedHashMap<>();

    private WorkParametersFactory factory;

    private Set<String> metaParameters = new HashSet<>();

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setParameter(String name, Object value) {
        if (name == null) {
            throw new NullPointerException("Parameter name is null");
        }
        parameters.put(name, value);
    }

    @Override
    public void setParameters(Map<String, Object> parameters) {
        if (parameters == null) {
            throw new NullPointerException();
        }
        this.parameters = new HashMap<>(parameters);
    }

    @Override
    public Object getParameter(String name) {
        if (name == null) {
            throw new NullPointerException("Parameter name is null");
        }
        return parameters.get(name);
    }

    @Override
    public Map<String, Object> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    public void setMetaParameters(Set<String> metaParameters) {
        this.metaParameters = metaParameters;
    }

    @Override
    public Set<String> getMetaParameters() {
        return metaParameters;
    }

    @Override
    public String toString() {
        return "Work " + name;
    }

    @Override
    public void setParameterDefinitions(Set<ParameterDefinition> parameterDefinitions) {
        this.parameterDefinitions.clear();
        for (ParameterDefinition parameterDefinition : parameterDefinitions) {
            addParameterDefinition(parameterDefinition);
        }
    }

    @Override
    public void addParameterDefinition(ParameterDefinition parameterDefinition) {
        this.parameterDefinitions.put(parameterDefinition.getName(), parameterDefinition);
    }

    @Override
    public Set<ParameterDefinition> getParameterDefinitions() {
        return new LinkedHashSet<>(parameterDefinitions.values());
    }

    @Override
    public String[] getParameterNames() {
        return parameterDefinitions.keySet().toArray(new String[parameterDefinitions.size()]);
    }

    @Override
    public ParameterDefinition getParameterDefinition(String name) {
        return parameterDefinitions.get(name);
    }

    @Override
    public void setWorkParametersFactory(WorkParametersFactory factory) {
        this.factory = factory;

    }

    @Override
    public WorkParametersFactory getWorkParametersFactory() {
        return factory;
    }

}
