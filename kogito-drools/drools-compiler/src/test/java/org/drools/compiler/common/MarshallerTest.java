package org.drools.compiler.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.stream.Stream;

import org.drools.compiler.Person;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.drools.core.marshalling.impl.JavaSerializableResolverStrategy;
import org.drools.core.marshalling.impl.ProtobufMessages;
import org.drools.core.marshalling.impl.ReadSessionResult;
import org.drools.core.marshalling.impl.SerializablePlaceholderResolverStrategy;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.io.ResourceType;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MarshallerTest {

    static Stream<Arguments> parameters() {
        return Stream.of(new JavaSerializableResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT),
                         new SerializablePlaceholderResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT))
                .map(s -> {
                    final Environment env = EnvironmentFactory.newEnvironment();
                    env.set(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, new ObjectMarshallingStrategy[]{s});
                    return env;
                }).map(Arguments::arguments);
    }

    @ParameterizedMarshallerTest
    public void testAgendaDoNotSerializeObject(Environment env) throws Exception {
        KieSession ksession = null;
        try {
            String str =
                    "import java.util.Collection\n" +
                            "rule R1 when\n" +
                            "    String(this == \"x\" || this == \"y\" || this == \"z\")\n" +
                            "then\n" +
                            "end\n";

            KieBase kbase = new KieHelper().addContent(str, ResourceType.DRL).build();
            ksession = kbase.newKieSession(null, env);

            ksession.insert("x");
            ksession.insert("y");
            ksession.insert("z");

            assertEquals(3, ksession.fireAllRules());

            ReadSessionResult serialisedStatefulKnowledgeSession =
                    SerializationHelper.getSerialisedStatefulKnowledgeSessionWithMessage(
                    ksession, ksession.getKieBase(), true);
            ksession = serialisedStatefulKnowledgeSession.getSession();

            ProtobufMessages.KnowledgeSession deserializedMessage =
                    serialisedStatefulKnowledgeSession.getDeserializedMessage();

            assertEquals(0, ksession.fireAllRules());
            assertFalse(deserializedMessage.getRuleData().getAgenda().getMatchList().stream().anyMatch(
                    ml -> ml.getTuple().getObjectList().size() > 0));
        } finally {
            if (ksession != null) {
                ksession.dispose();
            }
        }
    }

    @ParameterizedMarshallerTest
    public void testFromWithFireBeforeSerialization(Environment env) throws Exception {
        String str =
                "import java.util.Collection\n" +
                        "rule R1 when\n" +
                        "    String() from [ \"x\", \"y\", \"z\" ]\n" +
                        "then\n" +
                        "end\n";

        KieBase kbase = new KieHelper().addContent(str, ResourceType.DRL).build();
        KieSession ksession = null;
        try {
            ksession = kbase.newKieSession(null, env);
            assertEquals(3, ksession.fireAllRules());

            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);

            assertEquals(0, ksession.fireAllRules());
        } finally {
            if (ksession != null) {
                ksession.dispose();
            }
        }
    }

    @ParameterizedMarshallerTest
    public void testFromWithFireAfterSerialization(Environment env) throws Exception {
        String str =
                "import java.util.Collection\n" +
                        "rule R1 when\n" +
                        "    String() from [ \"x\", \"y\", \"z\" ]\n" +
                        "then\n" +
                        "end\n";

        KieBase kbase = new KieHelper().addContent(str, ResourceType.DRL).build();
        KieSession ksession = null;
        try {
            ksession = kbase.newKieSession(null, env);

            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);

            assertEquals(3, ksession.fireAllRules());
        } finally {
            if (ksession != null) {
                ksession.dispose();
            }
        }
    }

    @ParameterizedMarshallerTest
    public void testFromWithPartialFiring(Environment env) throws Exception {
        String str =
                "import java.util.Collection\n" +
                        "rule R1 when\n" +
                        "    String() from [ \"x\", \"y\", \"z\" ]\n" +
                        "then\n" +
                        "end\n";

        KieBase kbase = new KieHelper().addContent(str, ResourceType.DRL).build();
        KieSession ksession = null;
        try {
            ksession = kbase.newKieSession(null, env);
            assertEquals(2, ksession.fireAllRules(2));

            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);

            assertEquals(1, ksession.fireAllRules());
        } finally {
            if (ksession != null) {
                ksession.dispose();
            }
        }
    }

    @ParameterizedMarshallerTest
    public void test2FromsWithPartialFiring(Environment env) throws Exception {
        String str =
                "import java.util.Collection\n" +
                        "rule R1 when\n" +
                        "    String() from [ \"x\", \"y\", \"z\" ]\n" +
                        "    String() from [ \"a\", \"b\", \"c\" ]\n" +
                        "then\n" +
                        "end\n";

        KieBase kbase = new KieHelper().addContent(str, ResourceType.DRL).build();
        KieSession ksession = null;
        try {
            ksession = kbase.newKieSession(null, env);
            assertEquals(5, ksession.fireAllRules(5));

            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);

            assertEquals(4, ksession.fireAllRules());
        } finally {
            if (ksession != null) {
                ksession.dispose();
            }
        }
    }

    @ParameterizedMarshallerTest
    public void testFromAndJoinWithPartialFiring(Environment env) throws Exception {
        String str =
                "import java.util.Collection\n" +
                        "rule R1 when\n" +
                        "    String() from [ \"x\", \"y\", \"z\" ]\n" +
                        "    Integer()\n" +
                        "then\n" +
                        "end\n";

        KieBase kbase = new KieHelper().addContent(str, ResourceType.DRL).build();
        KieSession ksession = null;
        try {
            ksession = kbase.newKieSession(null, env);
            ksession.insert(42);
            assertEquals(2, ksession.fireAllRules(2));

            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);

            assertEquals(1, ksession.fireAllRules());
        } finally {
            if (ksession != null) {
                ksession.dispose();
            }
        }
    }

    @ParameterizedMarshallerTest
    public void testAgendaReconciliationAccumulate(Environment env) throws Exception {

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
        KieSession ksession = null;
        try {
            ksession = kbase.newKieSession(null, env);

            ksession.insert(new Person("Mark", 37));
            ksession.insert(new Person("Edson", 35));
            ksession.insert(new Person("Mario", 40));

            assertEquals(1, ksession.fireAllRules());

            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);

            assertEquals(0, ksession.fireAllRules());
        } finally {
            if (ksession != null) {
                ksession.dispose();
            }
        }
    }

    @ParameterizedMarshallerTest
    public void testAgendaReconciliationAccumulate2(Environment env) throws Exception {

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
        KieSession ksession = null;
        try {
            ksession = kbase.newKieSession(null, env);

            ksession.insert(new Person("Mark", 37));
            ksession.insert(new Person("Edson", 35));
            ksession.insert(new Person("Mario", 40));

            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);

            assertEquals(1, ksession.fireAllRules());
        } finally {
            if (ksession != null) {
                ksession.dispose();
            }
        }
    }

    @ParameterizedMarshallerTest
    public void testSubnetwork(Environment env) throws Exception {
        final String str =
                "rule R1 when\n" +
                        "    String()\n" +
                        "    Long()\n" +
                        "    not( Long() and Integer() )\n" +
                        "then end\n";

        KieBase kbase = new KieHelper().addContent(str, ResourceType.DRL)
                .build(EqualityBehaviorOption.EQUALITY);
        KieSession ksession = null;

        try {
            ksession = kbase.newKieSession(null, env);

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
            if (ksession != null) {
                ksession.dispose();
            }
        }
    }

    @ParameterizedMarshallerTest
    public void testSubnetwork2(Environment env) throws Exception {
        final String str =
                "rule R1 when\n" +
                        "    String()\n" +
                        "    Long()\n" +
                        "    not( Long() and Integer() )\n" +
                        "then end\n";

        KieBase kbase = new KieHelper().addContent(str, ResourceType.DRL)
                .build(EqualityBehaviorOption.EQUALITY);
        KieSession ksession = null;

        try {
            ksession = kbase.newKieSession(null, env);
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

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ParameterizedTest
    @MethodSource("parameters")
    public @interface ParameterizedMarshallerTest {

    }

    @ParameterizedMarshallerTest
    public void testFromJoinWithPartialFiring(Environment env) throws Exception {
        String str =
                "import java.util.Collection\n" +
                        "rule R1 when\n" +
                        "    Integer()\n" +
                        "    String() from [ \"x\", \"y\", \"z\" ]\n" +
                        "then\n" +
                        "end\n";

        KieBase kbase = new KieHelper().addContent(str, ResourceType.DRL).build();
        KieSession ksession = null;
        try {
            ksession = kbase.newKieSession(null, env);
            InternalFactHandle fh1 = ( InternalFactHandle ) ksession.insert( 1 );

            assertEquals(2, ksession.fireAllRules(2));

            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);

            assertEquals(1, ksession.fireAllRules());

            // old FH should keep its id
            InternalFactHandle intFH = ( InternalFactHandle ) ksession.getFactHandles().iterator().next();
            assertEquals( fh1.getId(), intFH.getId() );

            // serialization/deserialization of derived FHs shouldn't consume more FH ids
            assertEquals( fh1.getId() + 4, (( InternalFactHandle ) ksession.insert( 2 )).getId() );
        } finally {
            if (ksession != null) {
                ksession.dispose();
            }
        }
    }
}
