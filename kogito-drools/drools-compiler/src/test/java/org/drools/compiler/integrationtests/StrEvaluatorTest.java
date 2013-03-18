package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.RoutingMessage;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderError;
import org.kie.builder.KnowledgeBuilderErrors;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;
import org.kie.runtime.StatefulKnowledgeSession;

public class StrEvaluatorTest extends CommonTestMethodBase {

    @Test
    public void testStrStartsWith() throws Exception {
        KnowledgeBase kbase = readKnowledgeBase();
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        RoutingMessage m = new RoutingMessage();
        m.setRoutingValue("R1:messageBody");

        ksession.insert(m);
        ksession.fireAllRules();
        assertTrue(list.size() == 4);
        assertTrue( ((String) list.get(3)).equals("Message starts with R1") );

    }

    @Test
    public void testStrEndsWith() throws Exception {
        KnowledgeBase kbase = readKnowledgeBase();
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        RoutingMessage m = new RoutingMessage();
        m.setRoutingValue("messageBody:R2");

        ksession.insert(m);
        ksession.fireAllRules();
        assertTrue(list.size() == 4);
        assertTrue( ((String) list.get(3)).equals("Message ends with R2") );

    }

    @Test
    public void testStrLengthEquals() throws Exception {
        KnowledgeBase kbase = readKnowledgeBase();
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        RoutingMessage m = new RoutingMessage();
        m.setRoutingValue("R1:messageBody:R2");

        ksession.insert(m);
        ksession.fireAllRules();
        assertEquals( 6, list.size() );
        assertTrue(list.contains("Message length is 17"));

    }

    @Test
    public void testStrNotStartsWith() throws Exception {
        KnowledgeBase kbase = readKnowledgeBase();
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        RoutingMessage m = new RoutingMessage();
        m.setRoutingValue("messageBody");

        ksession.insert(m);
        ksession.fireAllRules();
        assertTrue(list.size() == 3);
        assertTrue( ((String) list.get(1)).equals("Message does not start with R2") );
    }

    @Test
    public void testStrNotEndsWith() throws Exception {
        KnowledgeBase kbase = readKnowledgeBase();
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        RoutingMessage m = new RoutingMessage();
        m.setRoutingValue("messageBody");

        ksession.insert(m);
        ksession.fireAllRules();
        assertTrue(list.size() == 3);
        assertTrue( ((String) list.get(0)).equals("Message does not end with R1") );
    }

    @Test
    public void testStrLengthNoEquals() throws Exception {
        KnowledgeBase kbase = readKnowledgeBase();
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        RoutingMessage m = new RoutingMessage();
        m.setRoutingValue("messageBody");

        ksession.insert(m);
        ksession.fireAllRules();
        assertTrue(list.size() == 3);
        assertTrue( ((String) list.get(2)).equals("Message length is not 17") );
    }

    private KnowledgeBase readKnowledgeBase() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "strevaluator_test.drl" ) ),
                ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if (errors.size() > 0) {
            for (KnowledgeBuilderError error: errors) {
                System.err.println(error);
            }
            throw new IllegalArgumentException("Could not parse knowledge." + errors.toArray());
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        return kbase;
    }

}
