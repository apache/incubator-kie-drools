/**
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
package org.drools.scenariosimulation.api.model;

import java.util.Optional;

/**
 * Java representation of a single <b>audit</b> line
 */
public class AuditLogLine {

    private int scenarioIndex;
    private String scenario;
    private int executionIndex;
    private String decisionOrRuleName;
    private String result;
    private Optional<String> message;

    public AuditLogLine() {
        // CDI
    }

    public AuditLogLine(int scenarioIndex, String scenario, int executionIndex, String decisionOrRuleName, String result) {
        this.scenarioIndex = scenarioIndex;
        this.scenario = scenario;
        this.executionIndex = executionIndex;
        this.decisionOrRuleName = decisionOrRuleName;
        this.result = result;
        this.message = Optional.empty();
    }

    public AuditLogLine(int scenarioIndex, String scenario, int executionIndex, String decisionOrRuleName, String result, String message) {
        this.scenarioIndex = scenarioIndex;
        this.scenario = scenario;
        this.executionIndex = executionIndex;
        this.decisionOrRuleName = decisionOrRuleName;
        this.result = result;
        this.message = Optional.ofNullable(message);
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

    public String getDecisionOrRuleName() {
        return decisionOrRuleName;
    }

    public String getResult() {
        return result;
    }

    public Optional<String> getMessage() {
        return message;
    }
}
