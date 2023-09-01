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
package org.drools.model.codegen.execmodel;

import java.util.ArrayList;
import java.util.List;

import org.drools.commands.runtime.rule.ClearActivationGroupCommand;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class InternalMatchGroupTest extends OnlyPatternTest {

    private static final String LIST_NAME = "list";
    private static final String LIST_OUTPUT_NAME = "output-list";
    private static final String KIE_SESSION = "ksession1";
    private static final String ACTIVATION_GROUP = "first-group";

    public InternalMatchGroupTest(RUN_TYPE testRunType) {
        super(testRunType);
    }

    @Test
    public void testClearActivationGroup() {
        // DROOLS-5685 ClearActivationGroup command changed its behaviour
        String str = "import java.util.List;\n" +
                     "\n" +
                     "global List list\n" +
                     "\n" +
                     "rule \"First rule in first activation group\" @Propagation(IMMEDIATE)\n" +
                     "activation-group \"first-group\"\n" +
                     "    salience 10\n" +
                     "when\n" +
                     "then\n" +
                     "    list.add(\"First rule in first activation group executed\");\n" +
                     "end\n" +
                     "\n" +
                     "rule \"Second rule in first activation group\" @Propagation(IMMEDIATE)\n" +
                     "activation-group \"first-group\"\n" +
                     "    salience 5\n" +
                     "when\n" +
                     "then\n" +
                     "    list.add(\"Second rule in first activation group executed\");\n" +
                     "end\n" +
                     "\n" +
                     "rule \"Rule without activation group\" @Propagation(IMMEDIATE)\n" +
                     "    salience 2\n" +
                     "when\n" +
                     "then\n" +
                     "    list.add(\"Rule without activation group executed\");\n" +
                     "end";

        List<Command<?>> commands = new ArrayList<Command<?>>();

        KieSession ksession = getKieSession(str);

        KieCommands commandsFactory = KieServices.get().getCommands();
        BatchExecutionCommand batchExecution = commandsFactory.newBatchExecution(commands, KIE_SESSION);

        commands.add(commandsFactory.newSetGlobal(LIST_NAME, new ArrayList<String>(), LIST_OUTPUT_NAME));
        // Replace if/after Clear command is added to command factory.
        // commands.add(commandsFactory.newClearActivationGroup(ACTIVATION_GROUP));
        commands.add(new ClearActivationGroupCommand(ACTIVATION_GROUP));
        commands.add(commandsFactory.newFireAllRules());
        commands.add(commandsFactory.newGetGlobal(LIST_NAME, LIST_OUTPUT_NAME));

        ExecutionResults result = ksession.execute(batchExecution);

        List<?> outcome = (List<?>) result.getValue(LIST_OUTPUT_NAME);
        assertThat(outcome).isNotNull();
        assertThat(outcome.size()).isEqualTo(1);

        assertThat(outcome.get(0)).isEqualTo("Rule without activation group executed");
    }
}
