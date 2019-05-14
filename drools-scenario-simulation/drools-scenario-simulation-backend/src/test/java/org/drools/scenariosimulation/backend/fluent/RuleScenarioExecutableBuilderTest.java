package org.drools.scenariosimulation.backend.fluent;

import org.drools.scenariosimulation.backend.runner.ScenarioException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.KieContainer;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RuleScenarioExecutableBuilderTest {

    @Test
    public void testPseudoClock() {
        KieContainer kieContainerMock = mock(KieContainer.class);
        when(kieContainerMock.getKieSessionModel(anyString())).thenReturn(null);
        assertThatThrownBy(() -> RuleScenarioExecutableBuilder.forcePseudoClock.apply(null, kieContainerMock))
                .isInstanceOf(ScenarioException.class);
    }

}