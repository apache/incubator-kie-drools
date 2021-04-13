/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.core.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.jbpm.process.core.ParameterDefinition;
import org.jbpm.process.core.Work;
import org.jbpm.process.instance.impl.humantask.DeadlineHelper;
import org.jbpm.process.instance.impl.humantask.DeadlineInfo;
import org.jbpm.process.instance.impl.humantask.Reassignment;

public class WorkImpl implements Work, Serializable {

    private static final long serialVersionUID = 510l;

    private String name;
    private Map<String, Object> parameters = new LinkedHashMap<String, Object>();
    private Map<String, ParameterDefinition> parameterDefinitions = new LinkedHashMap<String, ParameterDefinition>();

    private Collection<DeadlineInfo<Map<String, Object>>> startDeadlines;
    private Collection<DeadlineInfo<Map<String, Object>>> endDeadlines;
    private Collection<DeadlineInfo<Reassignment>> startReassigments;
    private Collection<DeadlineInfo<Reassignment>> endReassigments;

    private static final String NOT_STARTED = "NotStartedNotify";
    private static final String NOT_COMPLETED = "NotCompletedNotify";

    private static final String NOT_STARTED_ASSIGN = "NotStartedReassign";
    private static final String NOT_COMPLETED_ASSIGN = "NotCompletedReassign";

    private static final Set<String> META_NAMES = new HashSet<>();

    static {
        META_NAMES.add(NOT_STARTED);
        META_NAMES.add(NOT_COMPLETED);
        META_NAMES.add(NOT_STARTED_ASSIGN);
        META_NAMES.add(NOT_COMPLETED_ASSIGN);
    }

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
        this.parameters = new HashMap<String, Object>(parameters);
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
    public Collection<DeadlineInfo<Map<String, Object>>> getNotStartedDeadlines() {

        if (startDeadlines == null) {
            startDeadlines = DeadlineHelper.parseDeadlines(getParameter(NOT_STARTED));
        }
        return startDeadlines;

    }

    @Override
    public Collection<DeadlineInfo<Map<String, Object>>> getNotCompletedDeadlines() {
        if (endDeadlines == null) {
            endDeadlines = DeadlineHelper.parseDeadlines(getParameter(NOT_COMPLETED));
        }
        return endDeadlines;
    }

    @Override
    public Set<String> getMetaParameters() {
        return META_NAMES;
    }

    @Override
    public Collection<DeadlineInfo<Reassignment>> getNotStartedReassignments() {
        if (startReassigments == null) {
            startReassigments = DeadlineHelper.parseReassignments(getParameter(NOT_STARTED_ASSIGN));
        }
        return startReassigments;
    }

    @Override
    public Collection<DeadlineInfo<Reassignment>> getNotCompletedReassigments() {
        if (endReassigments == null) {
            endReassigments = DeadlineHelper.parseReassignments(getParameter(NOT_COMPLETED_ASSIGN));
        }
        return endReassigments;
    }
}
