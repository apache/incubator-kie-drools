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
package org.drools.mvel.compiler.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.mvel.CommonTestMethodBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.command.Command;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Before
    public void createKSession() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(ruleString.getBytes()), ResourceType.DRL );
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        assertThat(kbuilder.hasErrors()).isFalse();
        kbase.addPackages( kbuilder.getKnowledgePackages() );

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
        assertThat(fact_1).isNotNull();
        Object fact_2 = result.getValue("out_2");
        assertThat(fact_2).isNotNull();
        ksession.fireAllRules();

        Object [] expectedArr = {expected_1, expected_2};
        List<Object> expectedList = new ArrayList<Object>(Arrays.asList(expectedArr));
        
        Collection<? extends Object> factList = ksession.getObjects();
        assertThat(factList.size() == expectedList.size()).as("Expected " + expectedList.size() + " objects but retrieved " + factList.size()).isTrue();
        for( Object fact : factList ) {
            expectedList.remove(fact);
        }
        assertThat(expectedList.isEmpty()).as("Retrieved object list did not contain expected objects.").isTrue();
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
        assertThat(outList).isNotNull();
        ksession.fireAllRules();
    
        List<Object> expectedList = new ArrayList<Object>(Arrays.asList(expectedArr));
        
        Collection<? extends Object> factList = ksession.getObjects();
        assertThat(factList.size() == expectedList.size()).as("Expected " + expectedList.size() + " objects but retrieved " + factList.size()).isTrue();
        for( Object fact : factList ) {
            expectedList.remove(fact);
        }
        assertThat(expectedList.isEmpty()).as("Retrieved object list did not contain expected objects.").isTrue();
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
        assertThat(result).isNotNull();
        Object global = result.getValue("globalCheeseCountry");
        assertThat(global).isNotNull();
        assertThat(global).isEqualTo("France");
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
        assertThat(result).as("GetGlobalCommand result is null!").isNotNull();
        Object global = result.getValue("cheeseCountry");
        assertThat(global).as("Retrieved global fact is null!").isNotNull();
        assertThat(global).as("Retrieved global is not equal to 'France'.").isEqualTo("France");
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
        assertThat(fact).isNotNull();
        assertThat(fact).isEqualTo(expected_1);
        
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newGetObject(handle_1, "out_1"));
        commands.add(CommandFactory.newGetObject(handle_2, "out_2"));
        Command cmds = CommandFactory.newBatchExecution( commands );
        
        ExecutionResults result = (ExecutionResults) ksession.execute( cmds );
        assertThat(result).as("GetObjectCommand result is null!").isNotNull();

        assertThat(result.getValue("out_1")).isEqualTo(expected_1);
        assertThat(result.getValue("out_2")).isEqualTo(expected_2);
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
        assertThat(object).isNotNull();
        assertThat(object).isEqualTo(expected_1);
        object = ksession.getObject(handle_2);
        assertThat(object).isNotNull();
        assertThat(object).isEqualTo(expected_2);
        
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newGetObjects("out_list"));
        Command cmds = CommandFactory.newBatchExecution( commands );
        
        ExecutionResults result = (ExecutionResults) ksession.execute( cmds );
        assertThat(result).as("GetObjectsCommand result is null!").isNotNull();
        
        List<Object> objectList = (List) result.getValue("out_list");
        assertThat(objectList != null && !objectList.isEmpty()).as("Retrieved object list is null or empty!").isTrue();
        
        Collection<? extends Object> factList = ksession.getObjects();
        Object [] expectedArr = {expected_1, expected_2};
        List<Object> expectedList = new ArrayList<Object>(Arrays.asList(expectedArr));
        assertThat(factList.size() == expectedList.size()).as("Expected " + expectedList.size() + " objects but retrieved " + factList.size()).isTrue();
        for( Object fact : factList ) {
            expectedList.remove(fact);
        }
        assertThat(expectedList.isEmpty()).as("Retrieved object list did not contain expected objects.").isTrue();
    }
    
}
