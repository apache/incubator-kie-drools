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

import java.util.List;

import org.drools.core.command.runtime.rule.AgendaGroupSetFocusCommand;
import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.drools.core.command.runtime.rule.InsertElementsCommand;
import org.drools.core.fluent.impl.Batch;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResult;
import org.junit.Test;
import org.kie.api.command.Command;
import org.kie.api.runtime.ExecutionResults;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class RuleStatelessScenarioExecutableBuilderTest {

    @Test
    public void generateCommands() {
        RuleStatelessScenarioExecutableBuilder builder = new RuleStatelessScenarioExecutableBuilder(null, null);

        Command<ExecutionResults> batchCommand = builder.generateCommands();
        assertTrue(verifyCommand(batchCommand, FireAllRulesCommand.class));
        assertFalse(verifyCommand(batchCommand, AgendaGroupSetFocusCommand.class));
        assertFalse(verifyCommand(batchCommand, InsertElementsCommand.class));
        assertFalse(verifyCommand(batchCommand, ValidateFactCommand.class));

        builder.setActiveAgendaGroup("test");
        batchCommand = builder.generateCommands();

        assertTrue(verifyCommand(batchCommand, FireAllRulesCommand.class));
        assertTrue(verifyCommand(batchCommand, AgendaGroupSetFocusCommand.class));
        assertFalse(verifyCommand(batchCommand, InsertElementsCommand.class));
        assertFalse(verifyCommand(batchCommand, ValidateFactCommand.class));

        builder.insert(new Object());
        batchCommand = builder.generateCommands();
        assertTrue(verifyCommand(batchCommand, FireAllRulesCommand.class));
        assertTrue(verifyCommand(batchCommand, AgendaGroupSetFocusCommand.class));
        assertTrue(verifyCommand(batchCommand, InsertElementsCommand.class));
        assertFalse(verifyCommand(batchCommand, ValidateFactCommand.class));

        builder.addInternalCondition(String.class, obj -> null, new ScenarioResult(FactIdentifier.EMPTY, null));
        batchCommand = builder.generateCommands();
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