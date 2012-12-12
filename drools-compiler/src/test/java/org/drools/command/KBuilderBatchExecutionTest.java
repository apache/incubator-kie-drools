package org.drools.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.CommonTestMethodBase;
import org.drools.command.builder.KnowledgeBuilderAddCommand;
import org.drools.command.builder.KnowledgeBuilderGetErrorsCommand;
import org.drools.command.builder.KnowledgeBuilderGetKnowledgePackagesCommand;
import org.drools.command.builder.KnowledgeBuilderHasErrorsCommand;
import org.drools.command.builder.NewKnowledgeBuilderCommand;
import org.drools.integrationtests.DroolsTest.Bar;
import org.drools.integrationtests.DroolsTest.Foo;
import org.junit.After;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderError;
import org.kie.builder.KnowledgeBuilderErrors;
import org.kie.command.Command;
import org.kie.command.CommandFactory;
import org.kie.definition.KnowledgePackage;
import org.kie.io.Resource;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;
import org.kie.runtime.ExecutionResults;
import org.kie.runtime.StatefulKnowledgeSession;

public class KBuilderBatchExecutionTest extends CommonTestMethodBase {

    private static final String source = "org/drools/lang/misplaced_parenthesis.drl";

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
        
        String ruleString 
        = "package org.drools.integrationtests;\n"
        + "import " + KBuilderBatchExecutionTest.class.getCanonicalName() + ";\n"
        + "import org.drools.integrationtests.DroolsTest.Foo;\n"
        + "import org.drools.integrationtests.DroolsTest.Bar;\n"
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
        
        assertEquals(NUM_FACTS, counter);
    }

    private static int counter = 0;
    public static void incCounter() { 
         ++counter;    
    }
    
}
