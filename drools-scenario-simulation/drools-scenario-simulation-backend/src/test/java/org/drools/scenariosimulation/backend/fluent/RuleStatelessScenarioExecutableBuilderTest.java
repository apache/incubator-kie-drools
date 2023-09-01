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
package org.drools.scenariosimulation.backend.fluent;

import java.util.List;
import java.util.Map;

import org.drools.commands.fluent.Batch;
import org.drools.commands.runtime.rule.AgendaGroupSetFocusCommand;
import org.drools.commands.runtime.rule.FireAllRulesCommand;
import org.drools.commands.runtime.rule.InsertElementsCommand;
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

import static org.assertj.core.api.Assertions.assertThat;
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
        when(kieBaseMock.getKiePackages()).thenReturn(List.of());
        String sessionName = "sessionName";
        RuleStatelessScenarioExecutableBuilder builder = new RuleStatelessScenarioExecutableBuilder(kieContainerMock, sessionName);

        Map<String, Object> result = builder.run();

        verify(kieContainerMock, times(1)).newStatelessKieSession(eq(sessionName));
        assertThat(result).containsKeys(RuleScenarioExecutableBuilder.COVERAGE_LISTENER, RuleScenarioExecutableBuilder.RULES_AVAILABLE);
    }

    @Test
    public void generateCommands_twoCommands() {
        RuleStatelessScenarioExecutableBuilder builder = new RuleStatelessScenarioExecutableBuilder(null, null);

        Command<ExecutionResults> batchCommand = builder.generateCommands(null);
        
        assertThat(((Batch) batchCommand).getCommands()).hasSize(2);
        assertThat(((Batch) batchCommand).getCommands().get(0)).isInstanceOf(AddCoverageListenerCommand.class);
        assertThat(((Batch) batchCommand).getCommands().get(1)).isInstanceOf(FireAllRulesCommand.class);
        
    }

    @Test
    public void generateCommands_threeCommands() {
        RuleStatelessScenarioExecutableBuilder builder = new RuleStatelessScenarioExecutableBuilder(null, null);
        builder.setActiveAgendaGroup("test");

        Command<ExecutionResults> batchCommand = builder.generateCommands(null);
        
        assertThat(((Batch) batchCommand).getCommands()).hasSize(3);
        assertThat(((Batch) batchCommand).getCommands().get(0)).isInstanceOf(AddCoverageListenerCommand.class);
        assertThat(((Batch) batchCommand).getCommands().get(1)).isInstanceOf(AgendaGroupSetFocusCommand.class);
        assertThat(((Batch) batchCommand).getCommands().get(2)).isInstanceOf(FireAllRulesCommand.class);
        
    }

    @Test
    public void generateCommands_fourCommands() {
        RuleStatelessScenarioExecutableBuilder builder = new RuleStatelessScenarioExecutableBuilder(null, null);
        builder.setActiveAgendaGroup("test");
        builder.insert(new Object());

        Command<ExecutionResults> batchCommand = builder.generateCommands(null);
        
        assertThat(((Batch) batchCommand).getCommands()).hasSize(4);
        assertThat(((Batch) batchCommand).getCommands().get(0)).isInstanceOf(AddCoverageListenerCommand.class);
        assertThat(((Batch) batchCommand).getCommands().get(1)).isInstanceOf(AgendaGroupSetFocusCommand.class);
        assertThat(((Batch) batchCommand).getCommands().get(2)).isInstanceOf(InsertElementsCommand.class);
        assertThat(((Batch) batchCommand).getCommands().get(3)).isInstanceOf(FireAllRulesCommand.class);
        
    }
    
    
    @Test
    public void generateCommands_fiveCommands() {
        RuleStatelessScenarioExecutableBuilder builder = new RuleStatelessScenarioExecutableBuilder(null, null);
        builder.setActiveAgendaGroup("test");
        builder.insert(new Object());
        FactMappingValue emptyFMV = new FactMappingValue(FactIdentifier.EMPTY, ExpressionIdentifier.DESCRIPTION, null);
        builder.addInternalCondition(String.class, obj -> null, new ScenarioResult(emptyFMV, null));

        Command<ExecutionResults> batchCommand = builder.generateCommands(null);

        assertThat(((Batch) batchCommand).getCommands()).hasSize(5);
        assertThat(((Batch) batchCommand).getCommands().get(0)).isInstanceOf(AddCoverageListenerCommand.class);
        assertThat(((Batch) batchCommand).getCommands().get(1)).isInstanceOf(AgendaGroupSetFocusCommand.class);
        assertThat(((Batch) batchCommand).getCommands().get(2)).isInstanceOf(InsertElementsCommand.class);
        assertThat(((Batch) batchCommand).getCommands().get(3)).isInstanceOf(FireAllRulesCommand.class);
        assertThat(((Batch) batchCommand).getCommands().get(4)).isInstanceOf(ValidateFactCommand.class);
    }
}