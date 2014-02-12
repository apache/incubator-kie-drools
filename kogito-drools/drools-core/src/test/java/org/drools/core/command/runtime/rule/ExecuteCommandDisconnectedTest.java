/*
 * Copyright 2012 JBoss by Red Hat.
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

import java.util.ArrayList;
import java.util.List;

import org.drools.core.QueryResultsRowImpl;
import org.junit.*;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.api.command.*;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.runtime.ExecutionResults;
import org.drools.core.command.ExecuteCommand;
import org.drools.core.command.GetVariableCommand;
import org.drools.core.command.KnowledgeContextResolveFromContextCommand;
import org.drools.core.command.ResolvingKnowledgeCommandContext;
import org.drools.core.command.SetVariableCommandFromCommand;
import org.drools.core.command.impl.ContextImpl;
import org.drools.core.command.impl.DefaultCommandService;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.drools.core.world.impl.WorldImpl;
import static org.junit.Assert.*;

public class ExecuteCommandDisconnectedTest {

    private StatefulKnowledgeSession ksession;
    private DefaultCommandService commandService;
    
    public ExecuteCommandDisconnectedTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        
    }

    @After
    public void tearDown() {
    }

    @Test
    public void executeDisconnected() {
        
       

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        

        ksession = kbase.newStatefulKnowledgeSession();
        ExecutionResultImpl localKresults = new ExecutionResultImpl();
        
        ResolvingKnowledgeCommandContext kContext 
            = new ResolvingKnowledgeCommandContext( new ContextImpl( "ksession", null ) );
        kContext.set("localResults", localKresults);
        kContext.set("ksession", ksession);
        
        commandService = new DefaultCommandService(kContext);
        
        
        
        List cmds = new ArrayList();
        cmds.add(new InsertObjectCommand(new String("Hi!"), "handle"));
                                                                                                                 
        BatchExecutionCommand batchCmd = CommandFactory.newBatchExecution(cmds, "kresults");
        ExecuteCommand execCmd = new ExecuteCommand(batchCmd,true);
        KnowledgeContextResolveFromContextCommand resolveFromContextCommand = new KnowledgeContextResolveFromContextCommand(execCmd,   
                                                                                        null,null,"ksession","localResults");
        ExecutionResults results = (ExecutionResults)commandService.execute(resolveFromContextCommand);
        
        assertNotNull(results);
        
        assertNotNull(results.getFactHandle("handle"));
        
        assertTrue(((DefaultFactHandle)results.getFactHandle("handle")).isDisconnected());
        
        
        
        cmds = new ArrayList();
        cmds.add(new InsertObjectCommand(new String("Hi!"), "handle"));
        batchCmd = CommandFactory.newBatchExecution(cmds, "kresults");
        execCmd = new ExecuteCommand(batchCmd);
        resolveFromContextCommand = new KnowledgeContextResolveFromContextCommand(execCmd,   
                                                                                        null,null,"ksession","localResults");
        results = (ExecutionResults)commandService.execute(resolveFromContextCommand);
        
        assertNotNull(results);
        
        assertNotNull(results.getFactHandle("handle"));
        
        assertFalse(((DefaultFactHandle)results.getFactHandle("handle")).isDisconnected());
        
    }
    
    @Test @Ignore("phreak")
    public void executeCmdContextPropagationCastTest() {
         KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        ksession = kbase.newStatefulKnowledgeSession();
        ExecutionResultImpl localKresults = new ExecutionResultImpl();
        WorldImpl worldImpl = new WorldImpl();
        worldImpl.createContext("__TEMP__");
        worldImpl.getContext("__TEMP__").set("__TEMP__", new ContextImpl("__TEMP__", null));
        ResolvingKnowledgeCommandContext kContext = new ResolvingKnowledgeCommandContext(worldImpl);
        kContext.set("localResults", localKresults);
        kContext.set("ksession", ksession);

        commandService = new DefaultCommandService(kContext);
        List cmds = new ArrayList();
        
        QueryCommand queryCommand = new QueryCommand("out", "myQuery", new Object[]{});
        SetVariableCommandFromCommand setVariableCmd = new SetVariableCommandFromCommand("__TEMP__", "query123", queryCommand);
        cmds.add(setVariableCmd);
        
        BatchExecutionCommand batchCmd = CommandFactory.newBatchExecution(cmds, "kresults");
        ExecuteCommand execCmd = new ExecuteCommand(batchCmd,true);
        
        
        KnowledgeContextResolveFromContextCommand resolveFromContextCommand = new KnowledgeContextResolveFromContextCommand(execCmd,
                null, null, "ksession", "localResults");
        ExecutionResults results = (ExecutionResults) commandService.execute(resolveFromContextCommand);

        // I'm not expecting any results here
        assertNotNull(results);

        GetVariableCommand getVariableCmd = new GetVariableCommand("query123", "__TEMP__");
        resolveFromContextCommand = new KnowledgeContextResolveFromContextCommand(getVariableCmd,
                null, null, "ksession", "localResults");
        QueryResultsRowImpl queryResults = (QueryResultsRowImpl) commandService.execute(resolveFromContextCommand);

        assertNotNull(queryResults);

        assertEquals(0, queryResults.size());
        
        
    }
}
