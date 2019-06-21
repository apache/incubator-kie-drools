/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.scenariosimulation.backend.fluent;

import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.backend.runner.ScenarioException;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.runtime.ExecutableRunner;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.RequestContext;
import org.kie.internal.builder.fluent.ExecutableBuilder;
import org.kie.internal.builder.fluent.KieContainerFluent;
import org.kie.internal.builder.fluent.KieSessionFluent;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RuleStatefulScenarioExecutableBuilderTest {

    @Mock
    private ExecutableBuilder executableBuilderMock;

    @Mock
    private ExecutableRunner<RequestContext> executableRunnerMock;

    @Mock
    private KieContainerFluent kieContainerFluent;

    @Mock
    private KieSessionFluent kieSessionFluentMock;

    @Test
    public void testPseudoClock() {
        KieContainer kieContainerMock = mock(KieContainer.class);
        when(kieContainerMock.getKieSessionModel(anyString())).thenReturn(null);
        assertThatThrownBy(() -> RuleStatefulScenarioExecutableBuilder.forcePseudoClock.apply(null, kieContainerMock))
                .isInstanceOf(ScenarioException.class);

        when(kieContainerMock.getKieSessionModel(anyString())).thenReturn(mock(KieSessionModel.class));
        RuleStatefulScenarioExecutableBuilder.forcePseudoClock.apply(null, kieContainerMock);
    }

    @Test
    public void testBuilder() {
        when(executableBuilderMock.newApplicationContext(anyString())).thenReturn(executableBuilderMock);
        when(executableBuilderMock.setKieContainer(any(KieContainer.class))).thenReturn(kieContainerFluent);
        when(kieContainerFluent.newSessionCustomized(anyString(), any())).thenReturn(kieSessionFluentMock);
        when(kieSessionFluentMock.dispose()).thenReturn(executableBuilderMock);

        RuleStatefulScenarioExecutableBuilder builder = new RuleStatefulScenarioExecutableBuilder(null, null) {
            @Override
            protected ExecutableBuilder createExecutableBuilder() {
                return executableBuilderMock;
            }

            @Override
            protected ExecutableRunner<RequestContext> createExecutableRunner() {
                return executableRunnerMock;
            }
        };

        Object toInsert = new Object();
        String agendaGroup = "agendaGroup";
        String ruleFlowGroup = "ruleFlowGroup";

        FactIdentifier indexFI = FactIdentifier.INDEX;

        builder.setActiveAgendaGroup(agendaGroup);
        verify(kieSessionFluentMock, times(1)).setActiveAgendaGroup(eq(agendaGroup));
        reset(kieContainerFluent);

        builder.setActiveRuleFlowGroup(ruleFlowGroup);
        verify(kieSessionFluentMock, times(1)).setActiveAgendaGroup(eq(agendaGroup));
        reset(kieContainerFluent);

        builder.insert(toInsert);
        verify(kieSessionFluentMock, times(1)).insert(eq(toInsert));
        reset(kieContainerFluent);

        builder.addInternalCondition(String.class, obj -> null, new ScenarioResult(indexFI, null));
        builder.run();

        verify(kieSessionFluentMock, times(1)).fireAllRules();
        verify(kieSessionFluentMock, times(1)).addCommand(any(ValidateFactCommand.class));
    }
}