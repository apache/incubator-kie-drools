/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.core.command.runtime.rule;

import org.drools.core.command.ExecuteCommand;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.runtime.ExecutableRunner;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.RequestContext;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.command.CommandFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ExecuteCommandDisconnectedTest {

    @Test
    public void executeDisconnected() {
        KieBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        KieSession ksession = kbase.newKieSession();
        ExecutionResultImpl localKresults = new ExecutionResultImpl();

        RequestContext context = RequestContext.create().with( ksession );

        ExecutableRunner runner = ExecutableRunner.create();

        List cmds = new ArrayList();
        cmds.add(new InsertObjectCommand(new String("Hi!"), "handle"));

        BatchExecutionCommand batchCmd = CommandFactory.newBatchExecution(cmds, "kresults");
        ExecuteCommand execCmd = new ExecuteCommand(batchCmd,true);

        ExecutionResults results = execCmd.execute( context );

        assertNotNull(results);

        assertNotNull(results.getFactHandle("handle"));

        assertTrue(((DefaultFactHandle)results.getFactHandle("handle")).isDisconnected());

        cmds = new ArrayList();
        cmds.add(new InsertObjectCommand(new String("Hi!"), "handle"));
        batchCmd = CommandFactory.newBatchExecution(cmds, "kresults");
        execCmd = new ExecuteCommand(batchCmd);

        results = execCmd.execute( context );

        assertNotNull(results);

        assertNotNull(results.getFactHandle("handle"));

        assertFalse(((DefaultFactHandle)results.getFactHandle("handle")).isDisconnected());

    }
}
