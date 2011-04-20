package org.drools.integrationtests;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RoutingMessage;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

public class StrEvaluatorTest {

    @Test
    public void testStrStartsWith() throws Exception {
        KnowledgeBase kbase = readKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

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
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

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
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        RoutingMessage m = new RoutingMessage();
        m.setRoutingValue("R1:messageBody:R2");

        ksession.insert(m);
        ksession.fireAllRules();
        assertEquals( 6, list.size() );
        assertEquals( "Message length is 17", list.get(2) );

    }

    @Test
    public void testStrNotStartsWith() throws Exception {
        KnowledgeBase kbase = readKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

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
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

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
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

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
