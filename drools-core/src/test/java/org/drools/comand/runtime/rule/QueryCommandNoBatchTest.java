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

import org.junit.*;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.command.*;
import org.drools.command.impl.ContextImpl;
import org.drools.command.impl.DefaultCommandService;
import org.drools.command.runtime.rule.QueryCommand;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.impl.ExecutionResultImpl;
import org.drools.runtime.rule.impl.NativeQueryResults;
import org.drools.world.impl.WorldImpl;
import static org.junit.Assert.*;

/**
 *
 * @author salaboy
 */
public class QueryCommandNoBatchTest {

    private StatefulKnowledgeSession ksession;
    private DefaultCommandService commandService;

    public QueryCommandNoBatchTest() {
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
    public void executeQueryNoBatch() {

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
        
        QueryCommand queryCommand = new QueryCommand("out", "myQuery", new Object[]{});
        SetVariableCommandFromCommand setVariableCmd = new SetVariableCommandFromCommand("__TEMP__", "query123", queryCommand);
        
        KnowledgeContextResolveFromContextCommand resolveFromContextCommand = new KnowledgeContextResolveFromContextCommand(setVariableCmd,
                null, null, "ksession", "localResults");
        ExecutionResults results = (ExecutionResults) commandService.execute(resolveFromContextCommand);

        // I'm not expecting any results here
        assertNull(results);

        GetVariableCommand getVariableCmd = new GetVariableCommand("query123", "__TEMP__");
        resolveFromContextCommand = new KnowledgeContextResolveFromContextCommand(getVariableCmd,
                null, null, "ksession", "localResults");
        NativeQueryResults queryResults = (NativeQueryResults) commandService.execute(resolveFromContextCommand);

        assertNotNull(queryResults);

        assertEquals(0, queryResults.size());

    }
}
