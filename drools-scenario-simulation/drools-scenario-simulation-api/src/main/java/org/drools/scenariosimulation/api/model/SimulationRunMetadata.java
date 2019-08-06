/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.scenariosimulation.api.model;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

/**
 * Aggregation of all metadata information about a simulation run
 */
public class SimulationRunMetadata {

    protected int available;
    protected int executed;
    protected double coveragePercentage;

    protected Map<String, Integer> outputCounter = new HashMap<>();

    protected Map<ScenarioWithIndex, Map<String, Integer>> scenarioCounter = new HashMap<>();

    /**
     * Map of the messages to print in the CSV report: inside the <b>value</b> Map, <b>key</b> is the message, <b>value</b> is the severity level
     */
    protected final Map<ScenarioWithIndex, Map<String, String>> auditMessagesMap = new HashMap<>();

    public SimulationRunMetadata() {
        // CDI
    }

    public SimulationRunMetadata(int available,
                                 int executed,
                                 Map<String, Integer> outputCounter,
                                 Map<ScenarioWithIndex, Map<String, Integer>> scenarioCounter,
                                 Map<ScenarioWithIndex, Map<String, String>> auditMessages) {
        this.available = available;
        this.executed = executed;
        this.auditMessagesMap.putAll(auditMessages);
        this.outputCounter.putAll(outputCounter);
        this.scenarioCounter.putAll(scenarioCounter);
        this.coveragePercentage = (double) executed / available;
    }

    public int getAvailable() {
        return available;
    }

    public int getExecuted() {
        return executed;
    }

    public double getCoveragePercentage() {
        return (double) executed / available * 100;
    }

    public Map<String, Integer> getOutputCounter() {
        return outputCounter;
    }

    public Map<ScenarioWithIndex, Map<String, Integer>> getScenarioCounter() {
        return scenarioCounter;
    }

    /**
     * Return an <b>unmodifiable</b> representation of <code>auditMessagesMap</code>
     * @return
     */
    public Map<ScenarioWithIndex, Map<String, String>> getAuditMessagesMap() {
        return unmodifiableMap(auditMessagesMap);
    }
}
