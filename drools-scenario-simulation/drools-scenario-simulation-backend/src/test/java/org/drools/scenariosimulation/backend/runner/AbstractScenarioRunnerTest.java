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

package org.drools.scenariosimulation.backend.runner;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.backend.expression.ExpressionEvaluatorFactory;
import org.drools.scenariosimulation.backend.runner.model.ScenarioRunnerDTO;
import org.drools.scenariosimulation.backend.runner.model.ScenarioRunnerData;
import org.drools.scenariosimulation.backend.util.ScenarioSimulationServerMessages;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.kie.api.runtime.KieContainer;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AbstractScenarioRunnerTest {

    private final static int SCENARIO_DATA = 5;

    @Mock
    protected KieContainer kieContainerMock;

    protected AbstractScenarioRunner abstractScenarioRunnerLocal;
    protected Settings settingsLocal;
    private ScenarioRunnerDTO scenarioRunnerDTOLocal;

    @Before
    public void setup() {
        settingsLocal = new Settings();
        scenarioRunnerDTOLocal = getScenarioRunnerDTO();
        abstractScenarioRunnerLocal = spy(
                new AbstractScenarioRunner(kieContainerMock,
                                           scenarioRunnerDTOLocal,
                                           ExpressionEvaluatorFactory.create(
                                                   this.getClass().getClassLoader(),
                                                   ScenarioSimulationModel.Type.RULE)) {
                    @Override
                    protected AbstractRunnerHelper newRunnerHelper() {
                        return null;
                    }
                });
    }

    @Test
    public void getDescriptionForSimulationByClassNameAndSimulation() {
        Description retrieved = AbstractScenarioRunner.getDescriptionForSimulation(Optional.empty(), scenarioRunnerDTOLocal.getScenarioWithIndices());
        commonVerifyDescriptionForSimulation(retrieved, AbstractScenarioRunner.class.getSimpleName());
        retrieved = AbstractScenarioRunner.getDescriptionForSimulation(Optional.of("src/test/Test.scesim"), scenarioRunnerDTOLocal.getScenarioWithIndices());
        commonVerifyDescriptionForSimulation(retrieved, "Test");
    }

    @Test
    public void getDescriptionForScenario() {
        final Scenario scenario = scenarioRunnerDTOLocal.getScenarioWithIndices().get(2).getScesimData();
        Description retrieved = AbstractScenarioRunner.getDescriptionForScenario(Optional.empty(), 1, scenario.getDescription());
        commonVerifyDescriptionForScenario(retrieved, 1, scenario.getDescription(), AbstractScenarioRunner.class.getSimpleName());
        retrieved = AbstractScenarioRunner.getDescriptionForScenario(Optional.of("src/test/Test.scesim"), 1, scenario.getDescription());
        commonVerifyDescriptionForScenario(retrieved, 1, scenario.getDescription(), "Test");
    }

    @Test
    public void getSpecificRunnerProvider() {
        // all existing types should have a dedicated runner
        for (ScenarioSimulationModel.Type value : ScenarioSimulationModel.Type.values()) {
            final ScenarioRunnerProvider retrieved = AbstractScenarioRunner.getSpecificRunnerProvider(value);
            assertNotNull(retrieved);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSpecificRunnerProviderNullType() {
        settingsLocal.setType(null);
        AbstractScenarioRunner.getSpecificRunnerProvider(null);
    }

    @Test
    public void testRun() {
        ArgumentCaptor<Failure> failureArgumentCaptor = ArgumentCaptor.forClass(Failure.class);

        doThrow(new ScenarioException("Failed assertion", true))
                .when(abstractScenarioRunnerLocal).internalRunScenario(eq(scenarioRunnerDTOLocal.getScenarioWithIndices().get(0)),
                                                                       isA(ScenarioRunnerData.class),
                                                                       any(), any());
        doThrow(new ScenarioException("Generic exception"))
                .when(abstractScenarioRunnerLocal).internalRunScenario(eq(scenarioRunnerDTOLocal.getScenarioWithIndices().get(1)),
                                                                       isA(ScenarioRunnerData.class),
                                                                       any(), any());
        doThrow(new ScenarioException(new IllegalArgumentException("Wrong argument")))
                .when(abstractScenarioRunnerLocal).internalRunScenario(eq(scenarioRunnerDTOLocal.getScenarioWithIndices().get(2)),
                                                                       isA(ScenarioRunnerData.class),
                                                                       any(), any());
        doThrow(new RuntimeException("Unknown exception"))
                .when(abstractScenarioRunnerLocal).internalRunScenario(eq(scenarioRunnerDTOLocal.getScenarioWithIndices().get(3)),
                                                                       isA(ScenarioRunnerData.class),
                                                                       any(), any());
        assertNull(abstractScenarioRunnerLocal.simulationRunMetadataBuilder);
        RunNotifier runNotifier = spy(new RunNotifier());

        abstractScenarioRunnerLocal.run(runNotifier);

        assertNotNull(abstractScenarioRunnerLocal.simulationRunMetadataBuilder);
        verify(runNotifier, times(SCENARIO_DATA + 2)).fireTestStarted(isA(Description.class));
        verify(runNotifier, times(SCENARIO_DATA)).fireTestFailure(failureArgumentCaptor.capture());
        verify(runNotifier, times(SCENARIO_DATA)).fireTestFinished(isA(Description.class));

        List<Failure> capturedFailures = failureArgumentCaptor.getAllValues();
        assertEquals(ScenarioSimulationServerMessages.getIndexedScenarioMessage("Failed assertion", 1, "INDEX-0", "test"),
                     capturedFailures.get(0).getException().getMessage());
        assertTrue(capturedFailures.get(0).getException() instanceof IndexedScenarioAssertionError);
        assertEquals(ScenarioSimulationServerMessages.getIndexedScenarioMessage("Generic exception", 2, "INDEX-1", "test"),
                     capturedFailures.get(1).getException().getMessage());
        assertTrue(capturedFailures.get(1).getException() instanceof IndexedScenarioException);
        assertEquals(ScenarioSimulationServerMessages.getIndexedScenarioMessage("Wrong argument", 3, "INDEX-2", "test"),
                     capturedFailures.get(2).getException().getMessage());
        assertTrue(capturedFailures.get(2).getException() instanceof IndexedScenarioException);
        assertEquals(ScenarioSimulationServerMessages.getIndexedScenarioMessage("Unknown exception", 4, "INDEX-3", "test"),
                     capturedFailures.get(3).getException().getMessage());
        assertTrue(capturedFailures.get(3).getException() instanceof IndexedScenarioException);
    }

    @Test
    public void getScesimFileName() {
        assertEquals("Test", AbstractScenarioRunner.getScesimFileName("src/test/Test.scesim"));
        assertEquals("Test", AbstractScenarioRunner.getScesimFileName("Test.scesim"));
        assertEquals("Test", AbstractScenarioRunner.getScesimFileName("Test"));
        assertEquals("Test.1", AbstractScenarioRunner.getScesimFileName("src/test/Test.1.scesim"));
        assertEquals(null, AbstractScenarioRunner.getScesimFileName(null));
    }

    private void commonVerifyDescriptionForSimulation(final Description retrieved, final String className) {
        assertNotNull(retrieved);
        assertEquals(className, retrieved.getDisplayName());
        assertEquals(SCENARIO_DATA, retrieved.getChildren().size());
        assertNull(retrieved.getTestClass());
        assertEquals(className, retrieved.getClassName());
        IntStream.range(0, SCENARIO_DATA).forEach(index -> {
            final Description description = retrieved.getChildren().get(index);
            commonVerifyDescriptionForScenario(description, index + 1, scenarioRunnerDTOLocal.getScenarioWithIndices().get(index).getScesimData().getDescription(), className);
        });
    }

    private void commonVerifyDescriptionForScenario(final Description description, int index, final String scenarioDescription, final String className) {
        String expected = String.format("#%1$d: %2$s(%3$s)", index, scenarioDescription, className);
        assertEquals(expected, description.getDisplayName());
    }

    private ScenarioRunnerDTO getScenarioRunnerDTO() {

        Simulation simulation = new Simulation();
        IntStream.range(0, SCENARIO_DATA).forEach(index -> {
            Scenario scenario = simulation.addData();
            scenario.setDescription("INDEX-" + index);
        });

        ScenarioSimulationModel model = new ScenarioSimulationModel();
        model.setSimulation(simulation);

        return new ScenarioRunnerDTO(model, "test.scesim");
    }
}