/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.process.impl;

import java.util.Map;

import org.kie.kogito.process.WorkItem;

public class BaseWorkItem implements WorkItem {

    private final String id;
    private final String nodeInstanceId;
    private final String name;

    private final int state;
    private String phase;
    private String phaseStatus;

    private Map<String, Object> parameters;
    private Map<String, Object> results;

    public BaseWorkItem(String nodeInstanceId, String id, String name, int state, String phase, String phaseStatus, Map<String, Object> results) {
        this.id = id;
        this.nodeInstanceId = nodeInstanceId;
        this.name = name;
        this.state = state;
        this.phase = phase;
        this.phaseStatus = phaseStatus;
        this.results = results;
    }

    public BaseWorkItem(String nodeInstanceId, String id, String name, int state, String phase, String phaseStatus, Map<String, Object> parameters, Map<String, Object> results) {
        this.id = id;
        this.nodeInstanceId = nodeInstanceId;
        this.name = name;
        this.state = state;
        this.phase = phase;
        this.phaseStatus = phaseStatus;
        this.parameters = parameters;
        this.results = results;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getState() {
        return state;
    }

    @Override
    public Map<String, Object> getParameters() {
        return parameters;
    }

    @Override
    public String getPhase() {
        return phase;
    }

    @Override
    public String getPhaseStatus() {
        return phaseStatus;
    }

    @Override
    public Map<String, Object> getResults() {
        return results;
    }

    @Override
    public String getNodeInstanceId() {
        return nodeInstanceId;
    }

    @Override
    public String toString() {
        return "WorkItem [id=" + id + ", name=" + name + ", state=" + state + ", phase=" + phase + ", phaseStatus=" + phaseStatus + "]";
    }

}
