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
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

public class SimulationTest {

    private Simulation simulation;
    private Scenario originalScenario;

    @Before
    public void setup() {
        simulation = new Simulation();
        FactIdentifier factIdentifier = FactIdentifier.create("Test", String.class.getCanonicalName());
        ExpressionIdentifier expressionIdentifier = ExpressionIdentifier.create("Test", FactMappingType.GIVEN);
        simulation.getScesimModelDescriptor().addFactMapping(factIdentifier, expressionIdentifier);

        originalScenario = simulation.addData();
        originalScenario.setDescription("Test Description");
        originalScenario.addMappingValue(factIdentifier, expressionIdentifier, "TEST");
    }

    @Test
    public void addData() {
        simulation.addData(1);

        assertThatThrownBy(() -> simulation.addData(-1))
                .isInstanceOf(IndexOutOfBoundsException.class);

        assertThatThrownBy(() -> simulation.addData(3))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void cloneModel() {
        final Simulation cloned = this.simulation.cloneModel();
        assertThat(cloned).isNotNull();
        final ScesimModelDescriptor originalDescriptor = simulation.getScesimModelDescriptor();
        final ScesimModelDescriptor clonedDescriptor = cloned.getScesimModelDescriptor();
        assertThat(clonedDescriptor.getUnmodifiableFactMappings().size()).isEqualTo(originalDescriptor.getUnmodifiableFactMappings().size());
        IntStream.range(0, originalDescriptor.getUnmodifiableFactMappings().size()).forEach(index -> {
            assertThat(clonedDescriptor.getUnmodifiableFactMappings().get(index)).isEqualTo(originalDescriptor.getUnmodifiableFactMappings().get(index));
        });
        assertThat(cloned.getUnmodifiableData().size()).isEqualTo(simulation.getUnmodifiableData().size());
        IntStream.range(0, simulation.getUnmodifiableData().size()).forEach(index -> {
            assertThat(cloned.getUnmodifiableData().get(index).getDescription()).isEqualTo(simulation.getUnmodifiableData().get(index).getDescription());
        });
    }

    @Test
    public void cloneData() {
        Scenario clonedScenario = simulation.cloneData(0, 1);

        assertThat(clonedScenario.getDescription()).isEqualTo(originalScenario.getDescription());
        assertThat(clonedScenario.getUnmodifiableFactMappingValues().size()).isEqualTo(originalScenario.getUnmodifiableFactMappingValues().size());
        assertThat(simulation.getDataByIndex(0)).isEqualTo(originalScenario);
        assertThat(simulation.getDataByIndex(1)).isEqualTo(clonedScenario);

        assertThat(clonedScenario).isNotEqualTo(originalScenario);
        assertThat(clonedScenario.getUnmodifiableFactMappingValues().get(0)).isNotEqualTo(originalScenario.getUnmodifiableFactMappingValues().get(0));
    }

    @Test
    public void cloneScenarioFail() {

        assertThatThrownBy(() -> simulation.cloneData(-1, 1))
                .isInstanceOf(IndexOutOfBoundsException.class);

        assertThatThrownBy(() -> simulation.cloneData(2, 1))
                .isInstanceOf(IndexOutOfBoundsException.class);

        assertThatThrownBy(() -> simulation.cloneData(0, -1))
                .isInstanceOf(IndexOutOfBoundsException.class);

        assertThatThrownBy(() -> simulation.cloneData(0, 2))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void removeFactMappingByIndex() {
        assertThat(simulation.getUnmodifiableData().get(0).getUnmodifiableFactMappingValues().size()).isEqualTo(2);
        assertThat(simulation.getScesimModelDescriptor().getUnmodifiableFactMappings().size()).isEqualTo(1);
        simulation.removeFactMappingByIndex(0);
        assertThat(simulation.getUnmodifiableData().get(0).getUnmodifiableFactMappingValues().size()).isEqualTo(1);
        assertThat(simulation.getScesimModelDescriptor().getUnmodifiableFactMappings().size()).isEqualTo(0);
    }

    @Test
    public void removeFactMapping() {
        assertThat(simulation.getUnmodifiableData().get(0).getUnmodifiableFactMappingValues().size()).isEqualTo(2);
        assertThat(simulation.getScesimModelDescriptor().getUnmodifiableFactMappings().size()).isEqualTo(1);
        simulation.removeFactMapping(simulation.getScesimModelDescriptor().getFactMappingByIndex(0));
        assertThat(simulation.getUnmodifiableData().get(0).getUnmodifiableFactMappingValues().size()).isEqualTo(1);
        assertThat(simulation.getScesimModelDescriptor().getUnmodifiableFactMappings().size()).isEqualTo(0);
    }

    @Test
    public void getScenarioWithIndex() {
        List<ScenarioWithIndex> scenarioWithIndex = simulation.getScenarioWithIndex();
        assertThat(scenarioWithIndex.size()).isEqualTo(simulation.getUnmodifiableData().size());
        ScenarioWithIndex scenario = scenarioWithIndex.get(0);
        int index = scenario.getIndex();
        assertThat(scenario.getScesimData()).isEqualTo(simulation.getDataByIndex(index - 1));
    }
}