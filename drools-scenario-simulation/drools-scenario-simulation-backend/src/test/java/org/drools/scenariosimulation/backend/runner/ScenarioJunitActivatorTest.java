package org.drools.scenariosimulation.backend.runner;

import java.util.stream.Stream;

import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.backend.runner.model.ScenarioRunnerDTO;
import org.drools.util.ResourceHelper;
import org.drools.scenariosimulation.backend.util.ScenarioSimulationXMLPersistence;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.kie.api.runtime.KieContainer;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
    private ScenarioRunnerDTO scenarioRunnerDTOMock;

    @Mock
    private ScenarioSimulationModel scenarioSimulationModelMock;

    @Mock
    private RunNotifier runNotifierMock;

    private Simulation simulationLocal;
    private Settings settingsLocal;

    @Before
    public void setup() throws Exception {
        simulationLocal = new Simulation();
        settingsLocal = new Settings();
        settingsLocal.setSkipFromBuild(true);
        when(xmlReaderMock.unmarshal(any())).thenReturn(scenarioSimulationModelMock);
        when(scenarioSimulationModelMock.getSimulation()).thenReturn(simulationLocal);
        when(scenarioSimulationModelMock.getSettings()).thenReturn(settingsLocal);
    }

    @Test
    public void getChildrenTest() throws InitializationError {
        assertThat(getScenarioJunitActivator().getChildren()).hasSize(0);

        settingsLocal.setSkipFromBuild(false);

        assertThat(getScenarioJunitActivator().getChildren()).hasSize(1);
    }

    @Test
    public void runChildTest() throws InitializationError {
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