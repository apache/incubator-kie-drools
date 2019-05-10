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

import java.util.stream.Stream;

import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.backend.runner.model.SimulationWithFileName;
import org.drools.scenariosimulation.backend.util.ResourceHelper;
import org.drools.scenariosimulation.backend.util.ScenarioSimulationXMLPersistence;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.kie.api.runtime.KieContainer;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScenarioJunitActivatorTest {

    @Mock
    private ScenarioSimulationXMLPersistence xmlReaderMock;

    @Mock
    private KieContainer kieContainerMock;

    @Mock
    private AbstractScenarioRunner runnerMock;

    @Mock
    private SimulationWithFileName simulationWithFileNameMock;

    @Mock
    private ScenarioSimulationModel scenarioSimulationModelMock;

    @Mock
    private RunNotifier runNotifierMock;

    private Simulation simulationLocal;

    @Before
    public void setup() {
        simulationLocal = new Simulation();
        simulationLocal.getSimulationDescriptor().setSkipFromBuild(true);
        when(xmlReaderMock.unmarshal(any())).thenReturn(scenarioSimulationModelMock);
        when(scenarioSimulationModelMock.getSimulation()).thenReturn(simulationLocal);
    }

    @Test
    public void getChildrenTest() throws InitializationError {
        assertEquals(0, getScenarioJunitActivator().getChildren().size());

        simulationLocal.getSimulationDescriptor().setSkipFromBuild(false);

        assertEquals(1, getScenarioJunitActivator().getChildren().size());
    }

    @Test
    public void runChildTest() throws InitializationError {
        getScenarioJunitActivator().runChild(simulationWithFileNameMock, runNotifierMock);
        verify(runnerMock, times(1)).run(runNotifierMock);
    }

    private ScenarioJunitActivator getScenarioJunitActivator() throws InitializationError {
        return new ScenarioJunitActivator(ScenarioJunitActivator.class) {
            @Override
            ScenarioSimulationXMLPersistence getXmlReader() {
                return xmlReaderMock;
            }

            @Override
            Stream<String> getResources() {
                return ResourceHelper.getResourcesByExtension("txt");
            }

            @Override
            KieContainer getKieContainer() {
                return kieContainerMock;
            }

            @Override
            AbstractScenarioRunner newRunner(KieContainer kieContainer, Simulation simulation, String path) {
                return runnerMock;
            }
        };
    }
}