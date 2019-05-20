/*
 * Copyright 2011 Red Hat Inc.
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
package org.drools.compiler.command;

import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.kie.api.command.Command;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.io.ResourceFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class MoreBatchExecutionTest extends CommonTestMethodBase {

    private KieSession ksession = null;
    
    @AfterEach
    public void disposeKSession() {
        if( ksession != null ) { 
            ksession.dispose();
            ksession = null;
        }
    }
    
    @Test
    public void testFireAllRules() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("org/drools/compiler/integrationtests/drl/test_ImportFunctions.drl"), ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        ksession = createKnowledgeSession(kbase);

        final Cheese cheese = new Cheese("stilton", 15);
        ksession.insert(cheese);
        List<?> list = new ArrayList();
        ksession.setGlobal("list", list);

        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newFireAllRules("fired"));
        Command<?> cmds = CommandFactory.newBatchExecution(commands);
        ExecutionResults result = (ExecutionResults) ksession.execute(cmds);
        assertNotNull(result, "Batch execution result is null!");

        Object firedObject = result.getValue("fired");
        assertTrue( firedObject != null && firedObject instanceof Integer, "Retrieved object is null or incorrect!");
        assertEquals(4, firedObject);

        list = (List<?>) ksession.getGlobal("list");
        assertEquals(4, list.size());

        assertEquals("rule1", list.get(0));
        assertEquals("rule2", list.get(1));
        assertEquals("rule3", list.get(2));
        assertEquals("rule4", list.get(3));
    }
    
    @Test
    public void testQuery() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("org/drools/compiler/integrationtests/simple_query_test.drl"), ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        ksession = createKnowledgeSession(kbase);
        
        ksession.insert( new Cheese( "stinky", 5 ) );
        ksession.insert( new Cheese( "smelly", 7 ) );
        
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newQuery("numStinkyCheeses", "simple query"));
        Command<?> cmds = CommandFactory.newBatchExecution(commands);
        ExecutionResults result = (ExecutionResults) ksession.execute(cmds);
        assertNotNull(result, "Batch execution result is null!");

        Object queryResultsObject = result.getValue("numStinkyCheeses");
        assertTrue(queryResultsObject != null && queryResultsObject instanceof QueryResults, "Retrieved object is null or incorrect!");
        
        assertEquals( 1, ((QueryResults) queryResultsObject).size() );
    }
    
}
