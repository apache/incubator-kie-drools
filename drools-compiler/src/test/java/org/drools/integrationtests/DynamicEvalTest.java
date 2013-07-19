package org.drools.integrationtests;

import java.util.Collection;
        
import junit.framework.TestCase;
        
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.event.rule.DebugAgendaEventListener;
import org.drools.event.rule.DebugWorkingMemoryEventListener;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.time.SessionPseudoClock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
        
public class DynamicEvalTest extends TestCase {
    KnowledgeBase kbase;
    StatefulKnowledgeSession session;
    SessionPseudoClock clock;
    Collection<Object> effects;
    KnowledgeBuilder kbuilder;
    KnowledgeBaseConfiguration baseConfig;
    KnowledgeSessionConfiguration sessionConfig;
            
    @Before
    public void setUp() throws Exception {

        baseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        // use stream mode to enable proper event processing (see Drools Fusion 5.5.0 Doc "Event Processing Modes")
        baseConfig.setOption( EventProcessingOption.STREAM );       
        kbase = KnowledgeBaseFactory.newKnowledgeBase(baseConfig);
        
        // config
        sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        // use a pseudo clock, which starts at 0 and can be advanced manually
        sessionConfig.setOption( ClockTypeOption.get( "pseudo" ) );
        
        // create and return session
        session = kbase.newStatefulKnowledgeSession( sessionConfig, null );
        clock = session.getSessionClock();

    }

    public void loadPackages( Resource res, ResourceType type ) {
        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( res, type );

        KnowledgeBuilderErrors errors = kbuilder.getErrors();

        if (errors.size() > 0) {
            for (KnowledgeBuilderError error : errors) {
                System.err.println( error );
            }
            throw new IllegalArgumentException( "Could not parse knowledge." );
        }

    }
            
    @Override @After
    public void tearDown() {
        if (session != null) {
            session.dispose();
        }
        kbase = null;
        effects = null;
        clock = null;
        kbuilder = null;
        baseConfig = null;
        sessionConfig = null;
    }
            
    @Test
    public void testDynamicAdd() {
        String test =
        "\nrule id3" +
        "\nwhen" +
        "\neval(0 < 1)" + // this eval works
        "\nthen" +
        "\ninsertLogical( \"done\" );" +
        "\nend";

        loadPackages( ResourceFactory.newByteArrayResource( test.getBytes() ), ResourceType.DRL );
        session.getKnowledgeBase().addKnowledgePackages( kbuilder.getKnowledgePackages() );
        session.addEventListener( new DebugWorkingMemoryEventListener(  ) );
        
        int fired = session.fireAllRules(); // 1
        System.out.println(fired);
        effects = session.getObjects();
        assertTrue("fired", effects.contains("done"));
        
        // so the above works, let's try it again
        String test2 =
        "\nrule id4" +
        "\nwhen" +
        "\neval(0 == 0 )" + // this eval doesn't
        "\nthen" +
        "\ninsertLogical( \"done2\" );" +
        "\nend";

        loadPackages(ResourceFactory.newByteArrayResource(test2.getBytes()), ResourceType.DRL);
        session.getKnowledgeBase().addKnowledgePackages(kbuilder.getKnowledgePackages());


        fired = session.fireAllRules(); // 0
        System.out.println(fired);
        effects = session.getObjects();
        assertTrue("fired", effects.contains("done2")); // fails
    }

    @Test
    public void testDynamicAdd2() {
        String test =
                "rule id3\n" +
                "when\n" +
                "eval(0 == 0)\n" +
                "String( this == \"go\" )\n" + // this eval works
                "then\n" +
                "insertLogical( \"done\" );\n" +
                "end\n" +
                "rule id5\n" +
                "when\n" +
                "eval(0 == 0)\n" +
                "Integer( this == 7 )\n" + // this eval works
                "then\n" +
                "insertLogical( \"done3\" );\n" +
                "end\n";


        loadPackages( ResourceFactory.newByteArrayResource( test.getBytes() ), ResourceType.DRL );
        session.getKnowledgeBase().addKnowledgePackages( kbuilder.getKnowledgePackages() );
        session.addEventListener( new DebugWorkingMemoryEventListener(  ) );

        session.insert( "go" );
        session.insert( 5 );
        session.insert( 7 );

        int fired = session.fireAllRules(); // 1
        System.out.println(fired);
        effects = session.getObjects();
        assertTrue("fired", effects.contains("done"));

        // so the above works, let's try it again
        String test2 =
                "\nrule id4" +
                "\nwhen" +
                "\neval(0 == 0 )" + // this eval doesn't
                "\nInteger( this == 5 )" +
                "\nthen" +
                "\ninsertLogical( \"done2\" );" +
                "\nend";

        loadPackages(ResourceFactory.newByteArrayResource(test2.getBytes()), ResourceType.DRL);
        session.getKnowledgeBase().addKnowledgePackages(kbuilder.getKnowledgePackages());


        fired = session.fireAllRules(); // 0
        System.out.println(fired);
        effects = session.getObjects();
        assertTrue("fired", effects.contains("done2")); // fails

        for ( Object o : session.getObjects() ) {
            System.out.println( o );
        }
    }


}