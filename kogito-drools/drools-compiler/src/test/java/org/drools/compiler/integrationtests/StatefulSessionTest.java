package org.drools.compiler.integrationtests;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.FactHandle;
import org.drools.core.RuleBase;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.RuleBaseFactory;
import org.drools.core.StatefulSession;
import org.drools.compiler.compiler.PackageBuilder;
import org.drools.core.rule.Package;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.command.Command;
import org.kie.command.CommandFactory;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;
import org.kie.runtime.ExecutionResults;
import org.kie.runtime.StatefulKnowledgeSession;

public class StatefulSessionTest extends CommonTestMethodBase {
    final List list = new ArrayList();

    @Test
    public void testInsertObject() throws Exception {
        String str = "";
        str += "package org.kie \n";
        str += "import org.drools.compiler.Cheese \n";
        str += "rule rule1 \n";
        str += "  when \n";
        str += "    $c : Cheese() \n";
        str += " \n";
        str += "  then \n";
        str += "    $c.setPrice( 30 ); \n";
        str += "end\n";
        
        Cheese stilton = new Cheese( "stilton", 5 );
        
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        
        Command insertCmd = CommandFactory.newInsert( stilton, "outStilton" );
        Command fireCmd = CommandFactory.newFireAllRules();
        
        Command cmds = CommandFactory.newBatchExecution( Arrays.asList( new Command[] { insertCmd,fireCmd} ) );
        
        ExecutionResults result = (ExecutionResults) ksession.execute( cmds );
        stilton = ( Cheese ) result.getValue( "outStilton" );
        assertEquals( 30,
                      stilton.getPrice() );
        
        Object o = ksession.getObject( (FactHandle) result.getFactHandle( "outStilton" ) );
        assertSame( o, stilton );
    }
    
    @Test
    public void testSequentialException() {
        RuleBaseConfiguration config = new RuleBaseConfiguration();
        config.setSequential( true );
        RuleBase ruleBase = RuleBaseFactory.newRuleBase( config );
        
        try {
            ruleBase.newStatefulSession();
            fail("cannot have a stateful session with sequential set to true" );
        } catch ( Exception e ) {
            
        }
    }
    
    private StatefulSession getExceptionSession() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ConsequenceException.drl" ) ) );
        
        if ( builder.hasErrors() ) {
            throw new RuntimeException( builder.getErrors().toString() );
        }
        
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase    = SerializationHelper.serializeObject(ruleBase);
        return ruleBase.newStatefulSession();
    }

    private StatefulSession getSession() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "literal_rule_test.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase    = SerializationHelper.serializeObject(ruleBase);
        StatefulSession session = ruleBase.newStatefulSession();

//        session    = SerializationHelper.serializeObject(session);
        session.setGlobal( "list",
                           this.list );
        return session;
    }
      
}
