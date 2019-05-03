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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.drools.scenariosimulation.api.utils.ScenarioSimulationSharedUtils.toScenarioWithIndex;

/**
 * Envelop class that wrap the definition of the simulation and the values of the scenarios
 */
public class Simulation {

    /**
     * Describes structure of the simulation
     */
    private final SimulationDescriptor simulationDescriptor = new SimulationDescriptor();
    /**
     * Contains list of scenarios to test
     */
    private final List<Scenario> scenarios = new LinkedList<>();

    /**
     * Returns an <b>unmodifiable</b> list wrapping the backed one
     * @return
     */
    public List<Scenario> getUnmodifiableScenarios() {
        return Collections.unmodifiableList(scenarios);
    }

    public List<ScenarioWithIndex> getScenarioWithIndex() {
        return toScenarioWithIndex(this);
    }

    public void removeScenarioByIndex(int index) {
        scenarios.remove(index);
    }

    public void removeScenario(Scenario toRemove) {
        scenarios.remove(toRemove);
    }

    public SimulationDescriptor getSimulationDescriptor() {
        return simulationDescriptor;
    }

    public Scenario getScenarioByIndex(int index) {
        return scenarios.get(index);
    }

    public Scenario addScenario() {
        return addScenario(scenarios.size());
    }

    public Scenario addScenario(int index) {
        if (index < 0 || index > scenarios.size()) {
            throw new IllegalArgumentException(new StringBuilder().append("Index out of range ").append(index).toString());
        }
        Scenario scenario = new Scenario(simulationDescriptor);
        scenarios.add(index, scenario);
        return scenario;
    }

    public void replaceScenario(int index, Scenario newScenario) {
        scenarios.set(index, newScenario);
    }

    public void removeFactMappingByIndex(int index) {
        clearScenarios(simulationDescriptor.getFactMappingByIndex(index));
        simulationDescriptor.removeFactMappingByIndex(index);
    }

    public void removeFactMapping(FactMapping toRemove) {
        clearScenarios(toRemove);
        simulationDescriptor.removeFactMapping(toRemove);
    }

    public Scenario cloneScenario(int sourceIndex, int targetIndex) {
        if (sourceIndex < 0 || sourceIndex >= scenarios.size()) {
            throw new IllegalArgumentException(new StringBuilder().append("SourceIndex out of range ").append(sourceIndex).toString());
        }
        if (targetIndex < 0 || targetIndex > scenarios.size()) {
            throw new IllegalArgumentException(new StringBuilder().append("TargetIndex out of range ").append(targetIndex).toString());
        }
        Scenario scenarioByIndex = getScenarioByIndex(sourceIndex);
        Scenario clonedScenario = scenarioByIndex.cloneScenario();
        scenarios.add(targetIndex, clonedScenario);
        return clonedScenario;
    }

    public void clear() {
        simulationDescriptor.clear();
        clearScenarios();
    }

    public void clearScenarios() {
        scenarios.clear();
    }

    public void sort() {
        scenarios.forEach(Scenario::sort);
    }

    public void resetErrors() {
        scenarios.forEach(Scenario::resetErrors);
    }

    public Simulation cloneSimulation() {
        Simulation toReturn = new Simulation();
        toReturn.getSimulationDescriptor().setType(simulationDescriptor.getType());
        toReturn.getSimulationDescriptor().setDmnFilePath(simulationDescriptor.getDmnFilePath());
        toReturn.getSimulationDescriptor().setDmoSession(simulationDescriptor.getDmoSession());
        final List<FactMapping> originalFactMappings = this.simulationDescriptor.getUnmodifiableFactMappings();
        for (int i = 0; i < originalFactMappings.size(); i++) {
            final FactMapping originalFactMapping = originalFactMappings.get(i);
            toReturn.simulationDescriptor.addFactMapping(i, originalFactMapping);
        }
        this.scenarios.forEach(scenario -> toReturn.scenarios.add(scenario.cloneScenario()));
        return toReturn;
    }

    private void clearScenarios(FactMapping toRemove) {
        scenarios.forEach(e -> e.removeFactMappingValueByIdentifiers(toRemove.getFactIdentifier(), toRemove.getExpressionIdentifier()));
    }
}