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
