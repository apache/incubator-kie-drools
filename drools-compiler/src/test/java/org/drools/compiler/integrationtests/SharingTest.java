package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.compiler.integrationtests.facts.FactWithList;
import org.drools.core.base.ClassObjectType;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.definition.rule.Rule;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.Agenda;
import org.kie.internal.utils.KieHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SharingTest extends CommonTestMethodBase {

    private KieBase kbase;
    private Map<String, Rule> rules;
    private AlphaNode alphaNode_1;
    private AlphaNode alphaNode_2;
    private LeftInputAdapterNode lian_1;
    private LeftInputAdapterNode lian_2;
    private ObjectTypeNode otn;
    private JoinNode joinNode;



    public static ObjectTypeNode getObjectTypeNode(KieBase kbase, Class<?> nodeClass) {
        List<ObjectTypeNode> nodes = ((KnowledgeBaseImpl)kbase).getRete().getObjectTypeNodes();
        for ( ObjectTypeNode n : nodes ) {
            if ( ((ClassObjectType)n.getObjectType()).getClassType() == nodeClass ) {
                return n;
            }
        }
        return null;
    }

    private String getRules() {
        String drl = "";
        drl += "package org.drools.compiler.test\n";
        drl += "import " + Person.class.getCanonicalName() + "\n";
        drl += "import " + Cheese.class.getCanonicalName() + "\n";
        drl += "rule r1\n";
        drl += "when\n";
        drl += "   Person(name == \"Mark\")\n";
        drl += "then\n";
        drl += "end\n";
        drl += "rule r2\n";
        drl += "when\n";
        drl += "   Person(name == \"Mark\")\n";
        drl += "then\n";
        drl += "end\n";
        drl += "rule r3\n";
        drl += "when\n";
        drl += "   Person(name == \"Mark\", age == 50)\n";
        drl += "then\n";
        drl += "end\n";
        drl += "rule r4\n";
        drl += "when\n";
        drl += "   Person(name == \"Mark\", age == 50)\n";
        drl += "then\n";
        drl += "end\n";
        drl += "rule r5\n";
        drl += "when\n";
        drl += "   Person(name == \"John\", age == 50)\n";
        drl += "then\n";
        drl += "end\n";
        drl += "rule r6\n";
        drl += "when\n";
        drl += " Person(name == \"John\",$personCheese: cheese)\n";
        drl += "Cheese(this == $personCheese )";
        drl += "then\n";
        drl += "end\n";
        drl += "rule r7\n";
        drl += "when\n";
        drl += " Person(name == \"John\",$personCheese: cheese)\n";
        drl += "Cheese(this == $personCheese )";
        drl += "then\n";
        drl += "end\n";
        return drl;
    }

    public static class TestStaticUtils {
        public static int return1() {
            return 1;
        }
    }

    @Test
    public void testShouldAlphaShareBecauseSameConstantDespiteDifferentSyntax() {
        // DROOLS-1404
        String drl1 = "package c;\n" +
                      "import " + Misc2Test.TestObject.class.getCanonicalName() + "\n" +
                      "rule fileArule1 when\n" +
                      "  TestObject(value == 1)\n" +
                      "then\n" +
                      "end\n" +
                      "";
        String drl2 = "package iTzXzx;\n" + // <<- keep the different package
                      "import " + Misc2Test.TestObject.class.getCanonicalName() + "\n" +
                      "import " + TestStaticUtils.class.getCanonicalName() + "\n" +
                      "rule fileBrule1 when\n" +
                      "  TestObject(value == TestStaticUtils.return1() )\n" +
                      "then\n" +
                      "end\n" +
                      "rule fileBrule2 when\n" + // <<- keep this rule
                      "  TestObject(value == 0 )\n" +
                      "then\n" +
                      "end\n" +
                      "";

        KieSession kieSession = new KieHelper()
                .addContent( drl1, ResourceType.DRL )
                .addContent(drl2, ResourceType.DRL)
                .build().newKieSession();

        kieSession.insert(new Misc2Test.TestObject( 1) );

        assertEquals(2, kieSession.fireAllRules() );
    }

    @Test
    public void testShouldAlphaShareNotEqualsInDifferentPackages() {
        // DROOLS-1404
        String drl1 = "package c;\n" +
                      "import " + Misc2Test.TestObject.class.getCanonicalName() + "\n" +
                      "rule fileArule1 when\n" +
                      "  TestObject(value >= 1 )\n" +
                      "then\n" +
                      "end\n" +
                      "";
        String drl2 = "package iTzXzx;\n" + // <<- keep the different package
                      "import " + Misc2Test.TestObject.class.getCanonicalName() + "\n" +
                      "rule fileBrule1 when\n" +
                      "  TestObject(value >= 1 )\n" +
                      "then\n" +
                      "end\n" +
                      "rule fileBrule2 when\n" + // <<- keep this rule
                      "  TestObject(value >= 2 )\n" +
                      "then\n" +
                      "end\n" +
                      "";

        KieSession kieSession = new KieHelper()
                .addContent(drl1, ResourceType.DRL)
                .addContent(drl2, ResourceType.DRL)
                .build().newKieSession();

        kieSession.insert(new Misc2Test.TestObject( 1) );

        assertEquals(2, kieSession.fireAllRules() );
    }

    @Test
    public void testShouldAlphaShareNotEqualsInDifferentPackages2() {
        // DROOLS-1404
        String drl1 = "package c;\n" +
                "import " + FactWithList.class.getCanonicalName() + "\n" +
                "\n" +
                "rule fileArule1 when\n" +
                "  FactWithList(items contains \"test\")\n" +
                "then\n" +
                "end\n" +
                "";
        String drl2 = "package iTzXzx;\n" + // <<- keep the different package
                "import " + FactWithList.class.getCanonicalName() + "\n" +
                "rule fileBrule1 when\n" +
                "  FactWithList(items contains \"test\")\n" +
                "then\n" +
                "end\n" +
                "rule fileBrule2 when\n" + // <<- keep this rule
                "  FactWithList(items contains \"testtest\")\n" +
                "then\n" +
                "end\n" +
                "";

        KieSession kieSession = new KieHelper()
                .addContent(drl1, ResourceType.DRL)
                .addContent(drl2, ResourceType.DRL)
                .build().newKieSession();

        final FactWithList factWithList = new FactWithList("test");
        kieSession.insert(factWithList);

        assertEquals(2, kieSession.fireAllRules() );
    }

    @Test
    public void testSubnetworkSharing() {
        // DROOLS-
        String drl1 =
                "import " + A.class.getCanonicalName() + "\n" +
                "import " + B.class.getCanonicalName() + "\n" +
                "global java.util.List list" +
                "\n" +
                "rule R1 agenda-group \"G2\" when\n" +
                "    Number( intValue < 1 ) from accumulate (\n" +
                "        A( $id : id )\n" +
                "        and $b : B( parentId == $id )\n" +
                "    ;count($b))\n" +
                "then\n" +
                "    list.add(\"R1\");\n" +
                "end\n" +
                "\n" +
                "rule R2 agenda-group \"G1\" when\n" +
                "    Number( intValue < 1 ) from accumulate (\n" +
                "        A( $id : id )\n" +
                "        and $b : B( parentId == $id )\n" +
                "\n" +
//                "        and eval(true)\n" +
                "\n" +
                "    ;count($b))\n" +
                "then\n" +
                "    list.add(\"R2\");\n" +
                "end\n" +
                "\n" +
                "rule R3 agenda-group \"G1\" no-loop when\n" +
                "    $a : A( $id : id )\n" +
                "then\n" +
                "    modify($a) { setId($id + 1) };\n" +
                "end\n";

        KieSession kieSession = new KieHelper()
                .addContent(drl1, ResourceType.DRL)
                .build().newKieSession();

        List<String> list = new ArrayList<>();
        kieSession.setGlobal( "list", list );

        kieSession.insert( new A(1) );
        kieSession.insert( new B(1) );

        final Agenda agenda = kieSession.getAgenda();
        agenda.getAgendaGroup("G2").setFocus();
        agenda.getAgendaGroup("G1").setFocus();

        kieSession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.contains( "R1" ) );
        assertTrue( list.contains( "R2" ) );
    }

    public static class A {
        private int id;

        public A( int id ) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId( int id ) {
            this.id = id;
        }
    }

    public static class B {
        private final int parentId;

        public B( int parentId ) {
            this.parentId = parentId;
        }

        public int getParentId() {
            return parentId;
        }
    }
}
