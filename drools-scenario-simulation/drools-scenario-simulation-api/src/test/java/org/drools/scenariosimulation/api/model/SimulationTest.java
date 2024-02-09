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

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    public void addData_failsOutsideBoundaries() {
        simulation.addData(1);

        assertThatThrownBy(() -> simulation.addData(-1)).isInstanceOf(IndexOutOfBoundsException.class);

        assertThatThrownBy(() -> simulation.addData(3)).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void cloneModel() {
        final Simulation cloned = simulation.cloneModel();
        
        assertThat(cloned).isNotNull();
        final ScesimModelDescriptor originalDescriptor = simulation.getScesimModelDescriptor();
        final ScesimModelDescriptor clonedDescriptor = cloned.getScesimModelDescriptor();
        assertThat(clonedDescriptor.getUnmodifiableFactMappings()).hasSameSizeAs(originalDescriptor.getUnmodifiableFactMappings());
        
        assertThat(clonedDescriptor.getUnmodifiableFactMappings()).isEqualTo(originalDescriptor.getUnmodifiableFactMappings());
        
        assertThat(cloned.getUnmodifiableData()).hasSameSizeAs(simulation.getUnmodifiableData());
        assertThat(cloned.getUnmodifiableData()).usingElementComparator((x, y) -> x.getDescription().compareTo(y.getDescription())).isEqualTo(simulation.getUnmodifiableData());
    }

    @Test
    public void cloneData() {
        Scenario clonedScenario = simulation.cloneData(0, 1);

        assertThat(clonedScenario.getDescription()).isEqualTo(originalScenario.getDescription());
        assertThat(clonedScenario.getUnmodifiableFactMappingValues()).hasSameSizeAs(originalScenario.getUnmodifiableFactMappingValues());
        assertThat(simulation.getDataByIndex(0)).isEqualTo(originalScenario);
        assertThat(simulation.getDataByIndex(1)).isEqualTo(clonedScenario);

        assertThat(clonedScenario).isNotEqualTo(originalScenario);
        assertThat(clonedScenario.getUnmodifiableFactMappingValues().get(0)).isNotEqualTo(originalScenario.getUnmodifiableFactMappingValues().get(0));
    }

    @Test
    public void cloneData_failOutsideBoundaries() {

        assertThatThrownBy(() -> simulation.cloneData(-1, 1)).isInstanceOf(IndexOutOfBoundsException.class);

        assertThatThrownBy(() -> simulation.cloneData(2, 1)).isInstanceOf(IndexOutOfBoundsException.class);

        assertThatThrownBy(() -> simulation.cloneData(0, -1)).isInstanceOf(IndexOutOfBoundsException.class);

        assertThatThrownBy(() -> simulation.cloneData(0, 2)).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void removeFactMappingByIndex() {
        simulation.removeFactMappingByIndex(0);
        
        assertThat(simulation.getUnmodifiableData().get(0).getUnmodifiableFactMappingValues()).hasSize(1);
        assertThat(simulation.getScesimModelDescriptor().getUnmodifiableFactMappings()).hasSize(0);
    }

    @Test
    public void removeFactMapping() {
        simulation.removeFactMapping(simulation.getScesimModelDescriptor().getFactMappingByIndex(0));
        
        assertThat(simulation.getUnmodifiableData().get(0).getUnmodifiableFactMappingValues()).hasSize(1);
        assertThat(simulation.getScesimModelDescriptor().getUnmodifiableFactMappings()).hasSize(0);
    }

    @Test
    public void getScenarioWithIndex() {
        List<ScenarioWithIndex> scenarios = simulation.getScenarioWithIndex();
        
        assertThat(scenarios).hasSameSizeAs(simulation.getUnmodifiableData());
        
        ScenarioWithIndex scenario = scenarios.get(0);
        int index = scenario.getIndex();
        
        assertThat(scenario.getScesimData()).isEqualTo(simulation.getDataByIndex(index - 1));
    }
}