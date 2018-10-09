package org.drools.compiler.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.drools.compiler.Person;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.drools.core.marshalling.impl.ProtobufMarshaller;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.io.ResourceType;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.time.SessionClock;
import org.kie.internal.marshalling.MarshallerFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.utils.KieHelper;

import static org.drools.core.util.IoUtils.areByteArraysEqual;
import static org.junit.Assert.*;

public class MarshallerTest {

    @Test
    public void testAgendaDoNotSerializeObject() throws Exception {
        String str =
                "import java.util.Collection\n" +
                        "rule R1 when\n" +
                        "    String(this == \"x\" || this == \"y\" || this == \"z\")\n" +
                        "then\n" +
                        "end\n";

        KieBase kbase = new KieHelper().addContent(str, ResourceType.DRL ).build();
        KieSession ksession = kbase.newKieSession();

        ksession.insert("x");
        ksession.insert("y");
        ksession.insert("z");

        assertEquals( 3, ksession.fireAllRules() );

        ksession = getSerialisedStatefulKnowledgeSession(ksession, ksession.getKieBase(), true , true);

        assertEquals( 0, ksession.fireAllRules() );
    }

    public static StatefulKnowledgeSession getSerialisedStatefulKnowledgeSession(KieSession ksession,
                                                                                 KieBase kbase,
                                                                                 boolean dispose,
                                                                                 boolean testRoundTrip ) throws Exception {
        ProtobufMarshaller marshaller = (ProtobufMarshaller) MarshallerFactory.newMarshaller(kbase,
                                                                                             (ObjectMarshallingStrategy[])ksession.getEnvironment().get(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES) );
        long time = ksession.<SessionClock>getSessionClock().getCurrentTime();
        // make sure globas are in the environment of the session
        ksession.getEnvironment().set( EnvironmentName.GLOBALS, ksession.getGlobals() );

        // Serialize object
        final byte [] b1;
        {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            marshaller.marshall( bos,
                                 ksession,
                                 time );
            b1 = bos.toByteArray();
            bos.close();
        }

        // Deserialize object
        StatefulKnowledgeSession ksession2;
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(b1 );
            ksession2 = marshaller.unmarshall( bais,
                                               ksession.getSessionConfiguration(),
                                               ksession.getEnvironment());
            bais.close();
        }

        if( testRoundTrip ) {
            // for now, we can ensure the IDs will match because queries are creating untraceable fact handles at the moment
//            int previous_id = ((StatefulKnowledgeSessionImpl)ksession).session.getFactHandleFactory().getId();
//            long previous_recency = ((StatefulKnowledgeSessionImpl)ksession).session.getFactHandleFactory().getRecency();
//            int current_id = ((StatefulKnowledgeSessionImpl)ksession2).session.getFactHandleFactory().getId();
//            long current_recency = ((StatefulKnowledgeSessionImpl)ksession2).session.getFactHandleFactory().getRecency();
//            ((StatefulKnowledgeSessionImpl)ksession2).session.getFactHandleFactory().clear( previous_id, previous_recency );

            // Reserialize and check that byte arrays are the same
            final byte[] b2;
            {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                marshaller.marshall( bos,
                                     ksession2,
                                     time );
                b2 = bos.toByteArray();
                bos.close();
            }

            // bytes should be the same.
            if ( !areByteArraysEqual( b1,
                                      b2 ) ) {
//                throw new IllegalArgumentException( "byte streams for serialisation test are not equal" );
            }

//            ((StatefulKnowledgeSessionImpl) ksession2).session.getFactHandleFactory().clear( current_id, current_recency );
//            ((StatefulKnowledgeSessionImpl) ksession2).session.setGlobalResolver( ((StatefulKnowledgeSessionImpl) ksession).session.getGlobalResolver() );

        }

        if ( dispose ) {
            ksession.dispose();
        }

        return ksession2;
    }


    @Test
    public void testAgendaReconciliationFrom() throws Exception {
        String str =
                "import java.util.Collection\n" +
                        "rule R1 when\n" +
                        "    String() from [ \"x\", \"y\", \"z\" ]\n" +
                        "then\n" +
                        "end\n";

        KieBase kbase = new KieHelper().addContent(str, ResourceType.DRL ).build();
        KieSession ksession = kbase.newKieSession();
        assertEquals( 3, ksession.fireAllRules() );

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true );

        assertEquals( 0, ksession.fireAllRules() );
    }

    @Test
    public void testAgendaReconciliationFrom2() throws Exception {
        String str =
                "import java.util.Collection\n" +
                        "rule R1 when\n" +
                        "    String() from [ \"x\", \"y\", \"z\" ]\n" +
                        "then\n" +
                        "end\n";

        KieBase kbase = new KieHelper().addContent(str, ResourceType.DRL ).build();
        KieSession ksession = kbase.newKieSession();

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true );

        assertEquals( 3, ksession.fireAllRules() );
    }

    @Test
    public void testAgendaReconciliationAccumulate() throws Exception {

        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "rule X when\n" +
                        "  accumulate ( $p: Person ( getName().startsWith(\"M\")); \n" +
                        "                $sum : sum($p.getAge())  \n" +
                        "              )                          \n" +
                        "then\n" +
                        "  insert($sum);\n" +
                        "end";

        KieBase kbase = new KieHelper().addContent(str, ResourceType.DRL).build();
        KieSession ksession = kbase.newKieSession();

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        assertEquals(1, ksession.fireAllRules());

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);

        assertEquals(0, ksession.fireAllRules());
    }

    @Test
    public void testAgendaReconciliationAccumulate2() throws Exception {

        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "rule X when\n" +
                        "  accumulate ( $p: Person ( getName().startsWith(\"M\")); \n" +
                        "                $sum : sum($p.getAge())  \n" +
                        "              )                          \n" +
                        "then\n" +
                        "  insert($sum);\n" +
                        "end";

        KieBase kbase = new KieHelper().addContent(str, ResourceType.DRL).build();
        KieSession ksession = kbase.newKieSession();

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);

        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testSubnetwork() throws Exception {
        final String str =
                        "rule R1 when\n" +
                        "    String()\n" +
                        "    Long()\n" +
                        "    not( Long() and Integer() )\n" +
                        "then end\n";


        KieBase kbase = new KieHelper().addContent(str, ResourceType.DRL)
                .build(EqualityBehaviorOption.EQUALITY);
        KieSession ksession = kbase.newKieSession();

        try {
            ksession.insert("Luca");
            ksession.insert(2L);
            ksession.insert(10);

            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true );

            assertEquals( 0, ksession.fireAllRules() );

            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true );

            ksession.delete(ksession.getFactHandle(10));

            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true );

            assertEquals( 1, ksession.fireAllRules() );

        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testSubnetwork2() throws Exception {
        final String str =
                        "rule R1 when\n" +
                        "    String()\n" +
                        "    Long()\n" +
                        "    not( Long() and Integer() )\n" +
                        "then end\n";


        KieBase kbase = new KieHelper().addContent(str, ResourceType.DRL)
                .build(EqualityBehaviorOption.EQUALITY);
        KieSession ksession = kbase.newKieSession();

        try {
            ksession.insert("Luca");
            ksession.insert(2L);

            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true );

            assertEquals( 1, ksession.fireAllRules() );

            ksession.insert("Mario");
            ksession.insert(11);

            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true );

            assertEquals( 0, ksession.fireAllRules() );


        } finally {
            ksession.dispose();
        }
    }

}
