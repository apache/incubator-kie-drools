package org.drools.compiler.common;

import org.drools.compiler.Person;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.drools.core.marshalling.impl.ProtobufMessages;
import org.drools.core.marshalling.impl.ReadSessionResult;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

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

        KieBase kbase = new KieHelper().addContent(str, ResourceType.DRL).build();
        KieSession ksession = kbase.newKieSession();

        ksession.insert("x");
        ksession.insert("y");
        ksession.insert("z");

        assertEquals(3, ksession.fireAllRules());

        ReadSessionResult serialisedStatefulKnowledgeSession = ProtobufTestMarshaller.getSerialisedStatefulKnowledgeSession(ksession, ksession.getKieBase(), true, true);
        ksession = serialisedStatefulKnowledgeSession.getSession();

        ProtobufMessages.KnowledgeSession deserializedMessage = serialisedStatefulKnowledgeSession.getDeserializedMessage();

        assertFalse(deserializedMessage.getRuleData().getEntryPointList().stream().flatMap(ep -> ep.getHandleList().stream()).anyMatch(ml -> ml.getObject() != null));
        assertFalse(deserializedMessage.getRuleData().getAgenda().getMatchList().stream().anyMatch(ml -> ml.getObject() != null));

        assertEquals(0, ksession.fireAllRules());
    }

    @Test
    public void testAgendaReconciliationFrom() throws Exception {
        String str =
                "import java.util.Collection\n" +
                        "rule R1 when\n" +
                        "    String() from [ \"x\", \"y\", \"z\" ]\n" +
                        "then\n" +
                        "end\n";

        KieBase kbase = new KieHelper().addContent(str, ResourceType.DRL).build();
        KieSession ksession = kbase.newKieSession();
        assertEquals(3, ksession.fireAllRules());

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);

        assertEquals(0, ksession.fireAllRules());
    }

    @Test
    public void testAgendaReconciliationFrom2() throws Exception {
        String str =
                "import java.util.Collection\n" +
                        "rule R1 when\n" +
                        "    String() from [ \"x\", \"y\", \"z\" ]\n" +
                        "then\n" +
                        "end\n";

        KieBase kbase = new KieHelper().addContent(str, ResourceType.DRL).build();
        KieSession ksession = kbase.newKieSession();

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);

        assertEquals(3, ksession.fireAllRules());
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

            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);

            assertEquals(0, ksession.fireAllRules());

            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);

            ksession.delete(ksession.getFactHandle(10));

            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);

            assertEquals(1, ksession.fireAllRules());
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

            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);

            assertEquals(1, ksession.fireAllRules());

            ksession.insert("Mario");
            ksession.insert(11);

            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);

            assertEquals(0, ksession.fireAllRules());
        } finally {
            ksession.dispose();
        }
    }
}
