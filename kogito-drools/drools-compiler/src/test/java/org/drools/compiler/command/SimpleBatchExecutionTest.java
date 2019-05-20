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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.command.Command;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.io.ResourceFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimpleBatchExecutionTest extends CommonTestMethodBase {

    private KieSession ksession;
    protected final static String ruleString = ""
        + "package org.kie.api.persistence \n"
        + "global String globalCheeseCountry\n"
        + "\n"
        + "rule 'EmptyRule' \n" 
        + "    when\n"
        + "    then\n"
        + "end\n";

    @BeforeEach
    public void createKSession() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(ruleString.getBytes()), ResourceType.DRL );
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        assertFalse( kbuilder.hasErrors() );
        kbase.addPackages( kbuilder.getKnowledgePackages() );

        ksession = createKnowledgeSession(kbase);
    }
    
    @AfterEach
    public void disposeKSession() throws Exception {
        if( ksession != null ) { 
            ksession.dispose();
            ksession = null;
        }
    }
    
    @Test 
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testInsertObjectCommand() throws Exception {
        
        String expected_1 = "expected_1";
        String expected_2 = "expected_2";
        
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newInsert(expected_1, "out_1"));
        commands.add(CommandFactory.newInsert(expected_2, "out_2"));
        Command cmds = CommandFactory.newBatchExecution( commands );
        
        ExecutionResults result = (ExecutionResults) ksession.execute( cmds );
        
        Object fact_1 = result.getValue("out_1");
        assertNotNull(fact_1);
        Object fact_2 = result.getValue("out_2");
        assertNotNull(fact_2);
        ksession.fireAllRules();

        Object [] expectedArr = {expected_1, expected_2};
        List<Object> expectedList = new ArrayList<Object>(Arrays.asList(expectedArr));
        
        Collection<? extends Object> factList = ksession.getObjects();
        assertTrue(factList.size() == expectedList.size(),
                              "Expected " + expectedList.size() + " objects but retrieved " + factList.size());
        for( Object fact : factList ) {
           if( expectedList.contains(fact) ) { 
               expectedList.remove(fact);
           }
        }
        assertTrue(expectedList.isEmpty(), "Retrieved object list did not contain expected objects.");
    }
    
    @Test 
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testInsertElementsCommand() throws Exception {
        
        String expected_1 = "expected_1";
        String expected_2 = "expected_2";
        Object [] expectedArr = {expected_1, expected_2};
        Collection<Object> factCollection = Arrays.asList(expectedArr);
        
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newInsertElements(factCollection, "out_list", true, null));
        Command cmds = CommandFactory.newBatchExecution( commands );
        
        ExecutionResults result = (ExecutionResults) ksession.execute( cmds );
        
        Collection<? extends Object> outList = (Collection<? extends Object>) result.getValue("out_list");
        assertNotNull(outList);
        ksession.fireAllRules();
    
        List<Object> expectedList = new ArrayList<Object>(Arrays.asList(expectedArr));
        
        Collection<? extends Object> factList = ksession.getObjects();
        assertTrue(factList.size() == expectedList.size(),
                              "Expected " + expectedList.size() + " objects but retrieved " + factList.size());
        for( Object fact : factList ) {
           if( expectedList.contains(fact) ) { 
               expectedList.remove(fact);
           }
        }
        assertTrue(expectedList.isEmpty(), "Retrieved object list did not contain expected objects.");
    }

    @Test 
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testSetGlobalCommand() throws Exception {
        
        ksession.insert(new Integer(5));
        ksession.insert(new Integer(7));
        ksession.fireAllRules();
        
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newSetGlobal( "globalCheeseCountry", "France", true ));
    
        Command cmds = CommandFactory.newBatchExecution( commands );
    
        ExecutionResults result = (ExecutionResults) ksession.execute( cmds );
        assertNotNull(result);
        Object global = result.getValue("globalCheeseCountry");
        assertNotNull(global);
        assertEquals("France", global);
    }

    @Test 
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testGetGlobalCommand() throws Exception {
        
        ksession.insert(new Integer(5));
        ksession.insert(new Integer(7));
        ksession.fireAllRules();
        ksession.setGlobal("globalCheeseCountry", "France");
        
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newGetGlobal( "globalCheeseCountry", "cheeseCountry" ));
        Command cmds = CommandFactory.newBatchExecution( commands );

        ExecutionResults result = (ExecutionResults) ksession.execute( cmds );
        assertNotNull(result, "GetGlobalCommand result is null!");
        Object global = result.getValue("cheeseCountry");
        assertNotNull(global, "Retrieved global fact is null!");
        assertEquals("France", global, "Retrieved global is not equal to 'France'.");
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testGetObjectCommand() throws Exception {
        
        String expected_1 = "expected_1";
        String expected_2 = "expected_2";
        FactHandle handle_1 = ksession.insert( expected_1 );
        FactHandle handle_2 = ksession.insert( expected_2 );
        ksession.fireAllRules();
        
        Object fact = ksession.getObject(handle_1);
        assertNotNull(fact);
        assertEquals(expected_1, fact);

        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newGetObject(handle_1, "out_1"));
        commands.add(CommandFactory.newGetObject(handle_2, "out_2"));
        Command cmds = CommandFactory.newBatchExecution( commands );
        
        ExecutionResults result = (ExecutionResults) ksession.execute( cmds );
        assertNotNull(result, "GetObjectCommand result is null!");

        assertEquals(expected_1, result.getValue("out_1"));
        assertEquals(expected_2, result.getValue("out_2"));
    }
    
    @Test 
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testGetObjectsCommand() throws Exception {
        
        String expected_1 = "expected_1";
        String expected_2 = "expected_2";
        FactHandle handle_1 = ksession.insert( expected_1 );
        FactHandle handle_2 = ksession.insert( expected_2 );
        ksession.fireAllRules();
        
        Object object = ksession.getObject(handle_1);
        assertNotNull(object);
        assertEquals(expected_1, object);
        object = ksession.getObject(handle_2);
        assertNotNull(object);
        assertEquals(expected_2, object);

        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newGetObjects("out_list"));
        Command cmds = CommandFactory.newBatchExecution( commands );
        
        ExecutionResults result = (ExecutionResults) ksession.execute( cmds );
        assertNotNull(result, "GetObjectsCommand result is null!");

        List<Object> objectList = (List) result.getValue("out_list");
        boolean b = objectList != null && ! objectList.isEmpty();
        assertTrue(b, "Retrieved object list is null or empty!");

        Collection<? extends Object> factList = ksession.getObjects();
        Object [] expectedArr = {expected_1, expected_2};
        List<Object> expectedList = new ArrayList<Object>(Arrays.asList(expectedArr));
        assertTrue(factList.size() == expectedList.size(),
                              "Expected " + expectedList.size() + " objects but retrieved " + factList.size());
        for( Object fact : factList ) {
           if( expectedList.contains(fact) ) { 
               expectedList.remove(fact);
           }
        }
        assertTrue(expectedList.isEmpty(), "Retrieved object list did not contain expected objects.");
    }
}
