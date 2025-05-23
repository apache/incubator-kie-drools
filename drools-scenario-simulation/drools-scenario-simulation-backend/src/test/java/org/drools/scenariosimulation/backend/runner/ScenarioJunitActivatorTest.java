/*
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

import java.util.stream.Stream;

import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.backend.runner.model.ScenarioRunnerDTO;
import org.drools.util.ResourceHelper;
import org.drools.scenariosimulation.backend.util.ScenarioSimulationXMLPersistence;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.kie.api.runtime.KieContainer;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Deprecated
class ScenarioJunitActivatorTest {

    @Mock
    private ScenarioSimulationXMLPersistence xmlReaderMock;

    @Mock
    private KieContainer kieContainerMock;

    @Mock
    private AbstractScenarioRunner runnerMock;

    @Mock
    private ScenarioRunnerDTO scenarioRunnerDTOMock;

    @Mock
    private ScenarioSimulationModel scenarioSimulationModelMock;

    @Mock
    private RunNotifier runNotifierMock;

    @Test
    void getChildrenTest() throws Exception {
        Simulation simulationLocal = new Simulation();
        Settings settingsLocal = new Settings();
        settingsLocal.setSkipFromBuild(true);

        when(xmlReaderMock.unmarshal(any())).thenReturn(scenarioSimulationModelMock);
        when(scenarioSimulationModelMock.getSimulation()).thenReturn(simulationLocal);
        when(scenarioSimulationModelMock.getSettings()).thenReturn(settingsLocal);

        assertThat(getScenarioJunitActivator().getChildren()).isEmpty();

        settingsLocal.setSkipFromBuild(false);

        assertThat(getScenarioJunitActivator().getChildren()).hasSize(1);
    }

    @Test
    void runChildTest() throws InitializationError {
        getScenarioJunitActivator().runChild(scenarioRunnerDTOMock, runNotifierMock);
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
                return ResourceHelper.getResourcesByExtension("txt").stream();
            }

            @Override
            KieContainer getKieContainer() {
                return kieContainerMock;
            }

            @Override
            AbstractScenarioRunner newRunner(KieContainer kieContainer, ScenarioRunnerDTO scenarioRunnerDTO) {
                return runnerMock;
            }
        };
    }
}