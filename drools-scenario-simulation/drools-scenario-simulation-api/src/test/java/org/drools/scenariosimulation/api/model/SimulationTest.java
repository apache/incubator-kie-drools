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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

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
        assertNotNull(cloned);
        final ScesimModelDescriptor originalDescriptor = simulation.getScesimModelDescriptor();
        final ScesimModelDescriptor clonedDescriptor = cloned.getScesimModelDescriptor();
        assertEquals(originalDescriptor.getUnmodifiableFactMappings().size(), clonedDescriptor.getUnmodifiableFactMappings().size());
        IntStream.range(0, originalDescriptor.getUnmodifiableFactMappings().size()).forEach(index -> {
            assertEquals(originalDescriptor.getUnmodifiableFactMappings().get(index), clonedDescriptor.getUnmodifiableFactMappings().get(index));
        });
        assertEquals(simulation.getUnmodifiableData().size(), cloned.getUnmodifiableData().size());
        IntStream.range(0, simulation.getUnmodifiableData().size()).forEach(index -> {
            assertEquals(simulation.getUnmodifiableData().get(index).getDescription(), cloned.getUnmodifiableData().get(index).getDescription());
        });
    }

    @Test
    public void cloneData() {
        Scenario clonedScenario = simulation.cloneData(0, 1);

        assertEquals(originalScenario.getDescription(), clonedScenario.getDescription());
        assertEquals(originalScenario.getUnmodifiableFactMappingValues().size(), clonedScenario.getUnmodifiableFactMappingValues().size());
        assertEquals(originalScenario, simulation.getDataByIndex(0));
        assertEquals(clonedScenario, simulation.getDataByIndex(1));

        assertNotEquals(originalScenario, clonedScenario);
        assertNotEquals(originalScenario.getUnmodifiableFactMappingValues().get(0), clonedScenario.getUnmodifiableFactMappingValues().get(0));
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
        assertEquals(2, simulation.getUnmodifiableData().get(0).getUnmodifiableFactMappingValues().size());
        assertEquals(1, simulation.getScesimModelDescriptor().getUnmodifiableFactMappings().size());
        simulation.removeFactMappingByIndex(0);
        assertEquals(1, simulation.getUnmodifiableData().get(0).getUnmodifiableFactMappingValues().size());
        assertEquals(0, simulation.getScesimModelDescriptor().getUnmodifiableFactMappings().size());
    }

    @Test
    public void removeFactMapping() {
        assertEquals(2, simulation.getUnmodifiableData().get(0).getUnmodifiableFactMappingValues().size());
        assertEquals(1, simulation.getScesimModelDescriptor().getUnmodifiableFactMappings().size());
        simulation.removeFactMapping(simulation.getScesimModelDescriptor().getFactMappingByIndex(0));
        assertEquals(1, simulation.getUnmodifiableData().get(0).getUnmodifiableFactMappingValues().size());
        assertEquals(0, simulation.getScesimModelDescriptor().getUnmodifiableFactMappings().size());
    }

    @Test
    public void getScenarioWithIndex() {
        List<ScenarioWithIndex> scenarioWithIndex = simulation.getScenarioWithIndex();
        assertEquals(simulation.getUnmodifiableData().size(), scenarioWithIndex.size());
        ScenarioWithIndex scenario = scenarioWithIndex.get(0);
        int index = scenario.getIndex();
        assertEquals(simulation.getDataByIndex(index - 1), scenario.getScesimData());
    }
}