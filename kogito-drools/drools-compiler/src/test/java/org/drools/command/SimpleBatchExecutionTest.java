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
package org.drools.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.drools.CommonTestMethodBase;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SimpleBatchExecutionTest extends CommonTestMethodBase {

    private StatefulKnowledgeSession ksession;
    protected final static String ruleString = ""
        + "package org.drools.persistence \n"
        + "global String globalCheeseCountry\n"
        + "\n"
        + "rule 'EmptyRule' \n" 
        + "    when\n"
        + "    then\n"
        + "end\n";

    @Before
    public void createKSession() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(ruleString.getBytes()), ResourceType.DRL );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        assertFalse( kbuilder.hasErrors() );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        ksession = createKnowledgeSession(kbase);
    }
    
    @After
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
        
        Collection<Object> factList = ksession.getObjects();
        assertTrue("Expected " + expectedList.size() + " objects but retrieved " + factList.size(), factList.size() == expectedList.size() );
        for( Object fact : factList ) { 
           if( expectedList.contains(fact) ) { 
               expectedList.remove(fact);
           }
        }
        assertTrue("Retrieved object list did not contain expected objects.", expectedList.isEmpty() );
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
        
        Collection<Object> factList = ksession.getObjects();
        assertTrue("Expected " + expectedList.size() + " objects but retrieved " + factList.size(), factList.size() == expectedList.size() );
        for( Object fact : factList ) { 
           if( expectedList.contains(fact) ) { 
               expectedList.remove(fact);
           }
        }
        assertTrue("Retrieved object list did not contain expected objects.", expectedList.isEmpty() );
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
        assertNotNull("GetGlobalCommand result is null!", result);
        Object global = result.getValue("cheeseCountry");
        assertNotNull("Retrieved global fact is null!", global);
        assertEquals("Retrieved global is not equal to 'France'.", "France", global );
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
        assertNotNull("GetObjectCommand result is null!", result);
        
        assertEquals( expected_1, result.getValue("out_1") );
        assertEquals( expected_2, result.getValue("out_2") );
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
        assertNotNull("GetObjectsCommand result is null!", result);
        
        List<Object> objectList = (List) result.getValue("out_list");
        assertTrue("Retrieved object list is null or empty!", objectList != null && ! objectList.isEmpty());
        
        Collection<Object> factList = ksession.getObjects();
        Object [] expectedArr = {expected_1, expected_2};
        List<Object> expectedList = new ArrayList<Object>(Arrays.asList(expectedArr));
        assertTrue("Expected " + expectedList.size() + " objects but retrieved " + factList.size(), factList.size() == expectedList.size() );
        for( Object fact : factList ) { 
           if( expectedList.contains(fact) ) { 
               expectedList.remove(fact);
           }
        }
        assertTrue("Retrieved object list did not contain expected objects.", expectedList.isEmpty() );
    }
    
}
