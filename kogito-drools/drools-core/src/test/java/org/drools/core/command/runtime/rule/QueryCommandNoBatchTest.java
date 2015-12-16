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

import org.drools.core.QueryResultsImpl;
import org.drools.core.QueryResultsRowImpl;
import org.junit.*;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.KieBase;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.drools.core.command.GetVariableCommand;
import org.drools.core.command.KnowledgeContextResolveFromContextCommand;
import org.drools.core.command.ResolvingKnowledgeCommandContext;
import org.drools.core.command.SetVariableCommandFromCommand;
import org.drools.core.command.impl.ContextImpl;
import org.drools.core.command.impl.DefaultCommandService;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.drools.core.runtime.rule.impl.FlatQueryResults;
import org.drools.core.world.impl.WorldImpl;
import static org.junit.Assert.*;

/**
 *
 * @author salaboy
 */
@Ignore("phreak")
public class QueryCommandNoBatchTest {

    private DefaultCommandService commandService;

    @Test
    public void executeQueryNoBatch() {

        KieBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        KieSession ksession = kbase.newKieSession();
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
        QueryResultsRowImpl queryResultsRow = (QueryResultsRowImpl) commandService.execute(resolveFromContextCommand);

        assertNotNull(queryResultsRow);

        assertEquals(0, queryResultsRow.size());
    }
}
