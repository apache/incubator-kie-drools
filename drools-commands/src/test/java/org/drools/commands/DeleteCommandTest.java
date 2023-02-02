/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.commands;

import org.drools.commands.runtime.rule.DeleteCommand;
import org.drools.commands.runtime.rule.InsertObjectCommand;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutableRunner;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.RequestContext;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.RegistryContext;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteCommandTest {

    private KieSession ksession;
    private ExecutableRunner<RequestContext> runner;
    private Context context;

    @Before
    public void setup() {
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        ksession = kbase.newKieSession();
        runner = ExecutableRunner.create();
        context = ((RegistryContext) runner.createContext()).register(KieSession.class, ksession);
    }

    @After
    public void cleanUp() {
        ksession.dispose();
    }

    @Test
    public void testDeleteDisconnectedNonExistingFactHandle() {
        // DROOLS-7056
        String fact = "fact";
        InsertObjectCommand insertObjectCommand = new InsertObjectCommand(fact);
        FactHandle handle = runner.execute(insertObjectCommand, context);

        DeleteCommand deleteCommand1 = new DeleteCommand(handle);
        runner.execute(deleteCommand1, context);

        DeleteCommand deleteCommand2 = new DeleteCommand(handle); // delete twice
        runner.execute(deleteCommand2, context); // No Exception

        assertThat(ksession.getFactCount()).isEqualTo(0);
    }
}
