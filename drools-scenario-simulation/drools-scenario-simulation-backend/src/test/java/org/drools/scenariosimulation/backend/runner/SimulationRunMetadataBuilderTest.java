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
package org.drools.scenariosimulation.backend.runner;

import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.SimulationRunMetadata;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResultMetadata;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimulationRunMetadataBuilderTest {

    @Test
    public void build() {
        ScenarioWithIndex scenarioWithIndex1 = new ScenarioWithIndex(1, new Scenario());
        ScenarioResultMetadata result1 = new ScenarioResultMetadata(scenarioWithIndex1);
        result1.addExecuted("d1");
        result1.addExecuted("d2");
        result1.addAvailable("d1");
        result1.addAvailable("d2");
        result1.addAvailable("d3");

        ScenarioResultMetadata result2 = new ScenarioResultMetadata(new ScenarioWithIndex(2, new Scenario()));
        result2.addExecuted("d1");
        result2.addExecuted("d3");
        result2.addAvailable("d1");
        result2.addAvailable("d2");
        result2.addAvailable("d3");

        SimulationRunMetadataBuilder builder = SimulationRunMetadataBuilder.create();
        builder.addScenarioResultMetadata(result1);
        builder.addScenarioResultMetadata(result2);
        SimulationRunMetadata build = builder.build();

        assertEquals(3, build.getAvailable());
        assertEquals(3, build.getExecuted());
        assertEquals(100, build.getCoveragePercentage(), 0.1);
        assertEquals(2, build.getOutputCounter().get("d1"), 0.1);
        assertEquals(1, build.getOutputCounter().get("d2"), 0.1);
        assertEquals(2, build.getScenarioCounter().get(scenarioWithIndex1).size(), 0.1);
    }
}