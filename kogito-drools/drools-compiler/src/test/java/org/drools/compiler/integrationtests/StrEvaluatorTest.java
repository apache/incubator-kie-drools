package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.compiler.RoutingMessage;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.io.ResourceType;

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

        assertTrue( ((String) list.get(0)).equals("Message starts with R1") );
        assertTrue( ((String) list.get(1)).equals("Message length is not 17") );
        assertTrue( ((String) list.get(2)).equals("Message does not start with R2") );
        assertTrue( ((String) list.get(3)).equals("Message does not end with R1") );

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

        assertTrue( ((String) list.get(0)).equals("Message ends with R2") );
        assertTrue( ((String) list.get(1)).equals("Message length is not 17") );
        assertTrue( ((String) list.get(2)).equals("Message does not start with R2") );
        assertTrue( ((String) list.get(3)).equals("Message does not end with R1") );

    }

    @Test
    public void testStrLengthEquals() throws Exception {
        KnowledgeBase kbase = readKnowledgeBase();
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        RoutingMessage m = new RoutingMessage();
        m.setRoutingValue( "R1:messageBody:R2" );

        ksession.insert( m );
        ksession.fireAllRules();
        assertEquals( 6, list.size() );
        assertTrue(list.contains( "Message length is 17" ));

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
        assertTrue( list.size() == 3 );
        assertTrue( ((String) list.get(1)).equals( "Message does not start with R2" ) );
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
        assertTrue( list.size() == 3 );
        assertTrue( ( (String) list.get( 0 ) ).equals( "Message length is not 17" ) );
        assertTrue( ((String) list.get(1)).equals("Message does not start with R2") );
        assertTrue(((String) list.get(2)).equals("Message does not end with R1"));
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

        assertTrue( ((String) list.get(0)).equals("Message length is not 17") );
        assertTrue( ((String) list.get(1)).equals("Message does not start with R2") );
        assertTrue( ((String) list.get(2)).equals("Message does not end with R1") );
    }

    @Test
    public void testStrWithLogicalOr() {
        String drl = "package org.drools.compiler.integrationtests\n"
                     + "import org.drools.compiler.RoutingMessage\n"
                     + "rule R1\n"
                     + " when\n"
                     + " RoutingMessage( routingValue == \"R2\" || routingValue str[startsWith] \"R1\" )\n"
                     + " then\n"
                     + "end\n";
        KnowledgeBase kbase = loadKnowledgeBaseFromString( drl );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        try {
            for (String msgValue : new String[]{ "R1something", "R2something", "R2" }) {
                RoutingMessage msg = new RoutingMessage();
                msg.setRoutingValue(msgValue);
                ksession.insert(msg);
            }

            assertEquals("Wrong number of rules fired", 2, ksession.fireAllRules());
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testStrWithInlineCastAndFieldOnThis() {
        String drl = "package org.drools.compiler.integrationtests " +
                     "import " + Person.class.getName() + "; " +
                     "rule R1 " +
                     " when " +
                     " Object( this#" + Person.class.getName() + ".name str[startsWith] \"M\" ) " +
                     " then " +
                     "end ";
        KnowledgeBase kbase = loadKnowledgeBaseFromString(drl);

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        try {
            ksession.insert( new Person( "Mark" ) );

            assertEquals("Wrong number of rules fired", 1, ksession.fireAllRules());
        } finally {
            ksession.dispose();
        }
    }

    @Test
    @Ignore
    public void testStrWithInlineCastOnThis() {
        String drl = "package org.drools.compiler.integrationtests " +
                     "rule R1 " +
                     " when " +
                     " Object( this#String str[startsWith] \"M\" ) " +
                     " then " +
                     "end ";
        KnowledgeBase kbase = loadKnowledgeBaseFromString(drl);

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        try {
            ksession.insert( "Mark" );

            assertEquals( "Wrong number of rules fired", 1, ksession.fireAllRules() );
        } finally {
            ksession.dispose();
        }
    }

    private KnowledgeBase readKnowledgeBase() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource(getClass().getResourceAsStream("strevaluator_test.drl")),
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
