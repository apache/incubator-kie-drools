/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;

import static org.drools.scenariosimulation.api.utils.ScenarioSimulationSharedUtils.toScenarioWithIndex;

/**
 * Envelop class that wrap the definition of the simulation and the values of the scenarios
 */
public class Simulation extends AbstractScesimModel<Scenario> {

    public List<ScenarioWithIndex> getScenarioWithIndex() {
        return toScenarioWithIndex(this);
    }

    @Override
    public Scenario addScesimData(int index) {
        if (index < 0 || index > scesimData.size()) {
            throw new IllegalArgumentException(new StringBuilder().append("Index out of range ").append(index).toString());
        }
        Scenario scenario = new Scenario();
        scesimData.add(index, scenario);
        return scenario;
    }

    @Override
    public Simulation cloneScesimModel() {
        Simulation toReturn = new Simulation();
        final List<FactMapping> originalFactMappings = this.simulationDescriptor.getUnmodifiableFactMappings();
        for (int i = 0; i < originalFactMappings.size(); i++) {
            final FactMapping originalFactMapping = originalFactMappings.get(i);
            toReturn.simulationDescriptor.addFactMapping(i, originalFactMapping);
        }
        this.scesimData.forEach(scenario -> toReturn.scesimData.add(scenario.cloneScesimData()));
        return toReturn;
    }
}