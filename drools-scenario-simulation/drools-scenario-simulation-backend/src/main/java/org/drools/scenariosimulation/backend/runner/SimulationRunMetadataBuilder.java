package org.drools.scenariosimulation.backend.runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.scenariosimulation.api.model.AuditLog;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.SimulationRunMetadata;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResultMetadata;

public class SimulationRunMetadataBuilder {

    protected List<ScenarioResultMetadata> scenarioResultMetadata = new ArrayList<>();

    private SimulationRunMetadataBuilder() {
    }

    public SimulationRunMetadataBuilder addScenarioResultMetadata(ScenarioResultMetadata scenarioResultMetadata) {
        this.scenarioResultMetadata.add(scenarioResultMetadata);
        return this;
    }

    public SimulationRunMetadata build() {
        int available = 0;
        Map<String, Integer> outputCounter = new HashMap<>();
        Map<ScenarioWithIndex, Map<String, Integer>> scenarioCounter = new HashMap<>();
        AuditLog auditLog = new AuditLog();
        for (ScenarioResultMetadata scenarioResultMetadatum : scenarioResultMetadata) {
            // this value is the same for all the scenarios
            available = scenarioResultMetadatum.getAvailable().size();
            scenarioResultMetadatum.getExecutedWithCounter()
                    .forEach((name, counter) -> outputCounter.compute(name,
                                                                      (key, number) -> number == null ? counter : number + counter));
        }

        for (ScenarioResultMetadata scenarioResultMetadatum : scenarioResultMetadata) {
            scenarioCounter.put(scenarioResultMetadatum.getScenarioWithIndex(),
                                scenarioResultMetadatum.getExecutedWithCounter());
            auditLog.addAuditLogLines(scenarioResultMetadatum.getAuditLogLines());
        }
        return new SimulationRunMetadata(available, outputCounter.keySet().size(), outputCounter, scenarioCounter, auditLog);
    }

    public static SimulationRunMetadataBuilder create() {
        return new SimulationRunMetadataBuilder();
    }
}
