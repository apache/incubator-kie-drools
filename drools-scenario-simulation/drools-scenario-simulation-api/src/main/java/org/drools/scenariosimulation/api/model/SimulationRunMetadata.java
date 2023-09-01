package org.drools.scenariosimulation.api.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Aggregation of all metadata information about a simulation run
 */
public class SimulationRunMetadata {

    /**
     * <code>AuditLog</code>> representing the log to print in the CSV report
     */
    protected AuditLog auditLog;
    protected int available;
    protected int executed;
    protected double coveragePercentage;
    protected Map<String, Integer> outputCounter = new HashMap<>();
    protected Map<ScenarioWithIndex, Map<String, Integer>> scenarioCounter = new HashMap<>();

    public SimulationRunMetadata() {
        // CDI
    }

    public SimulationRunMetadata(int available,
                                 int executed,
                                 Map<String, Integer> outputCounter,
                                 Map<ScenarioWithIndex, Map<String, Integer>> scenarioCounter,
                                 AuditLog auditLog) {
        this.available = available;
        this.executed = executed;
        this.auditLog = auditLog;
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

    public AuditLog getAuditLog() {
        return auditLog;
    }
}
