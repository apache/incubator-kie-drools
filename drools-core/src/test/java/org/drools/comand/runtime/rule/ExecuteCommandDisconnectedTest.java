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
package org.drools.comand.runtime.rule;

import java.util.ArrayList;
import java.util.List;
import org.junit.*;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.command.*;
import org.drools.command.impl.ContextImpl;
import org.drools.command.impl.DefaultCommandService;
import org.drools.command.runtime.rule.InsertObjectCommand;
import org.drools.common.DefaultFactHandle;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.impl.ExecutionResultImpl;

/**
 *
 * @author salaboy
 */
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
        
        Assert.assertNotNull(results);
        
        Assert.assertNotNull(results.getFactHandle("handle"));
        
        Assert.assertTrue(((DefaultFactHandle)results.getFactHandle("handle")).isDisconnected());
        
        
        
        cmds = new ArrayList();
        cmds.add(new InsertObjectCommand(new String("Hi!"), "handle"));
        batchCmd = CommandFactory.newBatchExecution(cmds, "kresults");
        execCmd = new ExecuteCommand(batchCmd);
        resolveFromContextCommand = new KnowledgeContextResolveFromContextCommand(execCmd,   
                                                                                        null,null,"ksession","localResults");
        results = (ExecutionResults)commandService.execute(resolveFromContextCommand);
        
        Assert.assertNotNull(results);
        
        Assert.assertNotNull(results.getFactHandle("handle"));
        
        Assert.assertFalse(((DefaultFactHandle)results.getFactHandle("handle")).isDisconnected());
        
    }
}
