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
