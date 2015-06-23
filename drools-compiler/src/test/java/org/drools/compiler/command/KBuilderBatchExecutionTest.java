/*
 * Copyright 2015 JBoss Inc
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

package org.drools.compiler.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.integrationtests.DroolsTest;
import org.drools.core.command.builder.KnowledgeBuilderAddCommand;
import org.drools.core.command.builder.KnowledgeBuilderGetErrorsCommand;
import org.drools.core.command.builder.KnowledgeBuilderGetKnowledgePackagesCommand;
import org.drools.core.command.builder.KnowledgeBuilderHasErrorsCommand;
import org.drools.core.command.builder.NewKnowledgeBuilderCommand;
import org.drools.compiler.integrationtests.DroolsTest.Bar;
import org.drools.compiler.integrationtests.DroolsTest.Foo;
import org.junit.After;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.api.command.Command;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.ExecutionResults;

public class KBuilderBatchExecutionTest extends CommonTestMethodBase {

    private static final String source = "org/drools/compiler/lang/misplaced_parenthesis.drl";

    private StatefulKnowledgeSession ksession = null;
    
    @After
    public void disposeKSession() throws Exception {
        if( ksession != null ) { 
            ksession.dispose();
            ksession = null;
        }
    }
    
    @Test
    public void testKBuilderAdd() throws Exception {
        Resource res = ResourceFactory.newClassPathResource(source);
        assertNotNull(res);
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        ksession = createKnowledgeSession(kbase);
        
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(new NewKnowledgeBuilderCommand("kbuilder"));
        commands.add(new KnowledgeBuilderAddCommand(res, ResourceType.DRL, null));
        Command<?> cmds = CommandFactory.newBatchExecution( commands );
    
        ExecutionResults result = (ExecutionResults) ksession.execute( cmds );

        assertNotNull(result);
        Object kbuilder = result.getValue("kbuilder");
        assertNotNull(kbuilder);
        assertTrue(kbuilder instanceof KnowledgeBuilder);
        assertTrue(((KnowledgeBuilder) kbuilder).hasErrors());
    }
    
    @Test
    public void testHasErrors() throws Exception {
        Resource res = ResourceFactory.newClassPathResource(source);
        assertNotNull(res);
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        ksession = createKnowledgeSession(kbase);
        
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(new NewKnowledgeBuilderCommand());
        commands.add(new KnowledgeBuilderAddCommand(res, ResourceType.DRL, null));
        commands.add(new KnowledgeBuilderHasErrorsCommand("hasErrors"));
        Command<?> cmds = CommandFactory.newBatchExecution( commands );
    
        ExecutionResults result = (ExecutionResults) ksession.execute( cmds );
        
        assertNotNull(result);
        Object hasErrors = result.getValue("hasErrors");
        assertNotNull(hasErrors);
        assertEquals(Boolean.TRUE, hasErrors);
    }
    
    @Test
    public void testBatchGetErrors() throws Exception {
        Resource res = ResourceFactory.newClassPathResource(source);
        assertNotNull(res);
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        ksession = createKnowledgeSession(kbase);
        
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(new NewKnowledgeBuilderCommand());
        commands.add(new KnowledgeBuilderAddCommand(res, ResourceType.DRL, null));
        commands.add(new KnowledgeBuilderGetErrorsCommand("errors"));
        Command<?> cmds = CommandFactory.newBatchExecution( commands );
    
        ExecutionResults result = (ExecutionResults) ksession.execute( cmds );
        
        assertNotNull(result);
        Object errors = result.getValue("errors");
        assertNotNull(errors);
        assertTrue(errors instanceof KnowledgeBuilderErrors);
        KnowledgeBuilderErrors kbErrors = (KnowledgeBuilderErrors) errors;

        assertEquals( 2, kbErrors.size());
        KnowledgeBuilderError kbError = kbErrors.iterator().next();
        assertTrue( kbError.getMessage().startsWith("[ERR 102]"));
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testGetKnowledgePackages() { 
        int NUM_FACTS = 5;
        int startCounter = counter;
        
        String ruleString 
        = "package org.drools.compiler.integrationtests;\n"
        + "import " + KBuilderBatchExecutionTest.class.getCanonicalName() + ";\n"
        + "import " + DroolsTest.class.getName() + ".Foo;\n"
        + "import " + DroolsTest.class.getName() + ".Bar;\n"
        + "rule test\n"
        + "when\n"
        + "      Foo($p : id, id < " + Integer.toString( NUM_FACTS ) + ")\n"
        + "      Bar(id == $p)\n"
        + "then\n"
        + "   " + KBuilderBatchExecutionTest.class.getSimpleName() + ".incCounter();\n"
        + "end\n";
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        ksession = createKnowledgeSession(kbase);
        
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(new NewKnowledgeBuilderCommand());
        commands.add(new KnowledgeBuilderAddCommand( ResourceFactory.newByteArrayResource(ruleString.getBytes()), ResourceType.DRL, null));
        commands.add(new KnowledgeBuilderGetKnowledgePackagesCommand("pkgs"));
        Command<?> cmds = CommandFactory.newBatchExecution( commands );
    
        ExecutionResults result = (ExecutionResults) ksession.execute( cmds );
        assertNotNull(result);
        Object pkgsObject = result.getValue("pkgs");
        assertTrue(pkgsObject != null && pkgsObject instanceof Collection<?>);
        kbase.addKnowledgePackages( ((Collection<KnowledgePackage>) pkgsObject) );

        ksession.dispose();
        ksession = createKnowledgeSession(kbase);
        for ( int i = 0; i < NUM_FACTS; i++ ) {
            ksession.insert( new Foo( i ) );
            ksession.insert( new Bar( i ) );
        }
        ksession.fireAllRules();
        
        assertEquals(startCounter + NUM_FACTS, counter);
    }

    private static int counter = 0;
    public static void incCounter() { 
         ++counter;    
    }
    
}
