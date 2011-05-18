package org.drools.integrationtests;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.drools.ClockType;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.SessionConfiguration;
import org.drools.WorkingMemory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.InternalRuleBase;
import org.drools.conf.EventProcessingOption;
import org.drools.definition.KnowledgePackage;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.io.ResourceFactory;
import org.drools.marshalling.Marshaller;
import org.drools.marshalling.MarshallerFactory;
import org.drools.reteoo.MockTupleSource;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.FixedDuration;
import org.drools.rule.Rule;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.drools.time.impl.DurationTimer;
import org.drools.time.impl.PseudoClockScheduler;
import org.junit.Ignore;
import org.junit.Test;

public class EventMarshallFailTest {

    public static class A
        implements
        Serializable {
    }

    public static class B
        implements
        Serializable {
    }

    public static class C
        implements
        Serializable {
    }

    @Test
    @Ignore
    public void testMarshall() throws Exception {
        String str =
                "import org.drools.integrationtests.EventMarshallFailTest.*\n" +
                        "declare A\n" +
                        " @role( event )\n" +
                        " @expires( 10m )\n" +
                        "end\n" +
                        "declare B\n" +
                        " @role( event )\n" +
                        " @expires( 10m )\n" +
                        "end\n" +
                        "rule one\n" +
                        "when\n" +
                        "   $a : A()\n" +
                        "   B(this after $a)\n" +
                        "then\n" +
                        "insert(new C());" +
                        "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( str ) ),
                      ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            throw new RuntimeException( kbuilder.getErrors().toString() );
        }
        KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption( EventProcessingOption.STREAM );
        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase( config );
        knowledgeBase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = knowledgeBase.newStatefulKnowledgeSession();
        ksession.insert( new A() );
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MarshallerFactory.newMarshaller( knowledgeBase ).marshall( out,
                                                                   ksession );

        ksession = MarshallerFactory.newMarshaller( knowledgeBase ).unmarshall( new ByteArrayInputStream( out.toByteArray() ) );
        ksession.insert( new B() );
        ksession.fireAllRules();
        assertEquals( 3,
                      ksession.getObjects().size() );
    }
    
    @Test @Ignore
    public void testScheduledActivation() {
        KnowledgeBaseImpl knowledgeBase = (KnowledgeBaseImpl) KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgePackageImp impl = new KnowledgePackageImp();
        impl.pkg = new org.drools.rule.Package("test");

        BuildContext buildContext = new BuildContext((InternalRuleBase) knowledgeBase.getRuleBase(), ((ReteooRuleBase) knowledgeBase.getRuleBase())
                .getReteooBuilder().getIdGenerator());
        //simple rule that fires after 10 seconds
        final Rule rule = new Rule("test-rule");
        new RuleTerminalNode(1,new MockTupleSource(2), rule, rule.getLhs(), buildContext);
        
        final List<String> fired = new ArrayList<String>();
        
        rule.setConsequence( new Consequence() {
            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) throws Exception {
                fired.add("a");
            }

            public String getName() {
                return "default";
            }
        } );
                
        rule.setTimer( new DurationTimer( 10000 ) );
        rule.setPackage("test");
        impl.pkg.addRule(rule);
        
        knowledgeBase.addKnowledgePackages(Collections.singleton((KnowledgePackage) impl));
        SessionConfiguration config = new SessionConfiguration();
        config.setClockType(ClockType.PSEUDO_CLOCK);
        StatefulKnowledgeSession ksession = knowledgeBase.newStatefulKnowledgeSession(config, KnowledgeBaseFactory.newEnvironment());
        PseudoClockScheduler scheduler = ksession.getSessionClock();
        Marshaller marshaller = MarshallerFactory.newMarshaller(knowledgeBase);
        


        ksession.insert("cheese");
        assertTrue(fired.isEmpty());
        //marshall, then unmarshall session
        readWrite(knowledgeBase, ksession, config);
        //the activations should fire after 10 seconds
        assertTrue(fired.isEmpty());
        scheduler.advanceTime(12, TimeUnit.SECONDS);
        assertFalse(fired.isEmpty());

    }
    
    private void readWrite(KnowledgeBase knowledgeBase, StatefulKnowledgeSession ksession, KnowledgeSessionConfiguration config) {
        try {
            Marshaller marshaller = MarshallerFactory.newMarshaller(knowledgeBase);
            ByteArrayOutputStream o = new ByteArrayOutputStream();
            marshaller.marshall(o, ksession);
            ksession = marshaller.unmarshall(new ByteArrayInputStream(o.toByteArray()), config, KnowledgeBaseFactory.newEnvironment());
            ksession.fireAllRules();
            //scheduler = ksession.getSessionClock();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
     

}
