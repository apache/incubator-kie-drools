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

/**
 * Java representation of a single <b>audit</b> line
 */
public class AuditLogLine {

    private int scenarioIndex;
    private String scenario;
    private int executionIndex;
    private String message;
    private String level;

    public AuditLogLine() {
        // CDI
    }

    public AuditLogLine(int scenarioIndex, String scenario, int executionIndex, String message, String level) {
        this.scenarioIndex = scenarioIndex;
        this.scenario = scenario;
        this.executionIndex = executionIndex;
        this.message = message;
        this.level = level;
    }

    public int getScenarioIndex() {
        return scenarioIndex;
    }

    public String getScenario() {
        return scenario;
    }

    public int getExecutionIndex() {
        return executionIndex;
    }

    public String getMessage() {
        return message;
    }

    public String getLevel() {
        return level;
    }
}
