/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.scenariosimulation.backend.fluent;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.drools.core.command.runtime.rule.AgendaGroupSetFocusCommand;
import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.drools.core.command.runtime.rule.InsertElementsCommand;
import org.drools.core.fluent.impl.Batch;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMappingValue;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieBase;
import org.kie.api.command.Command;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RuleStatelessScenarioExecutableBuilderTest {

    @Mock
    private KieContainer kieContainerMock;

    @Mock
    private StatelessKieSession statelessKieSessionMock;

    @Mock
    private KieBase kieBaseMock;

    @Test
    public void testBuilder() {
        when(kieContainerMock.newStatelessKieSession(anyString())).thenReturn(statelessKieSessionMock);
        when(statelessKieSessionMock.getKieBase()).thenReturn(kieBaseMock);
        when(kieBaseMock.getKiePackages()).thenReturn(Collections.emptyList());
        String sessionName = "sessionName";
        RuleStatelessScenarioExecutableBuilder builder = new RuleStatelessScenarioExecutableBuilder(kieContainerMock, sessionName);

        Map<String, Object> result = builder.run();
        verify(kieContainerMock, times(1)).newStatelessKieSession(eq(sessionName));
        assertTrue(result.containsKey(RuleScenarioExecutableBuilder.COVERAGE_LISTENER));
        assertTrue(result.containsKey(RuleScenarioExecutableBuilder.RULES_AVAILABLE));
    }

    @Test
    public void generateCommands() {
        FactMappingValue emptyFMV = new FactMappingValue(FactIdentifier.EMPTY, ExpressionIdentifier.DESCRIPTION, null);

        RuleStatelessScenarioExecutableBuilder builder = new RuleStatelessScenarioExecutableBuilder(null, null);

        Command<ExecutionResults> batchCommand = builder.generateCommands(null);
        assertTrue(verifyCommand(batchCommand, AddCoverageListenerCommand.class));
        assertTrue(verifyCommand(batchCommand, FireAllRulesCommand.class));
        assertFalse(verifyCommand(batchCommand, AgendaGroupSetFocusCommand.class));
        assertFalse(verifyCommand(batchCommand, InsertElementsCommand.class));
        assertFalse(verifyCommand(batchCommand, ValidateFactCommand.class));

        builder.setActiveAgendaGroup("test");
        batchCommand = builder.generateCommands(null);

        assertTrue(verifyCommand(batchCommand, AddCoverageListenerCommand.class));
        assertTrue(verifyCommand(batchCommand, FireAllRulesCommand.class));
        assertTrue(verifyCommand(batchCommand, AgendaGroupSetFocusCommand.class));
        assertFalse(verifyCommand(batchCommand, InsertElementsCommand.class));
        assertFalse(verifyCommand(batchCommand, ValidateFactCommand.class));

        builder.insert(new Object());
        batchCommand = builder.generateCommands(null);
        assertTrue(verifyCommand(batchCommand, AddCoverageListenerCommand.class));
        assertTrue(verifyCommand(batchCommand, FireAllRulesCommand.class));
        assertTrue(verifyCommand(batchCommand, AgendaGroupSetFocusCommand.class));
        assertTrue(verifyCommand(batchCommand, InsertElementsCommand.class));
        assertFalse(verifyCommand(batchCommand, ValidateFactCommand.class));

        builder.addInternalCondition(String.class, obj -> null, new ScenarioResult(emptyFMV, null));
        batchCommand = builder.generateCommands(null);
        assertTrue(verifyCommand(batchCommand, AddCoverageListenerCommand.class));
        assertTrue(verifyCommand(batchCommand, FireAllRulesCommand.class));
        assertTrue(verifyCommand(batchCommand, AgendaGroupSetFocusCommand.class));
        assertTrue(verifyCommand(batchCommand, InsertElementsCommand.class));
        assertTrue(verifyCommand(batchCommand, ValidateFactCommand.class));
    }

    private boolean verifyCommand(Command<ExecutionResults> batchCommand, Class<?> classToFind) {
        if (!(batchCommand instanceof Batch)) {
            fail();
        }

        List<Command> commands = ((Batch) batchCommand).getCommands();

        return commands.stream().anyMatch(classToFind::isInstance);
    }
}