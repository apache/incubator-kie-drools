package org.drools.compiler.integrationtests;

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
import org.drools.core.reteoo.MethodCountingAlphaNode;
import org.drools.core.reteoo.MethodCountingLeftInputAdapterNode;
import org.drools.core.reteoo.MethodCountingObjectTypeNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.builder.MethodCountingNodeFactory;
import org.drools.core.reteoo.builder.NodeFactory;
import org.junit.Test;
import org.kie.api.definition.rule.Rule;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.utils.KieHelper;

import static org.drools.core.util.DroolsTestUtil.rulestoMap;

public class SharingTest extends CommonTestMethodBase {

    private KnowledgeBase kbase;
    private Map<String, Rule> rules;
    private AlphaNode alphaNode_1;
    private AlphaNode alphaNode_2;
    private LeftInputAdapterNode lian_1;
    private LeftInputAdapterNode lian_2;
    private ObjectTypeNode otn;
    private JoinNode joinNode;



    public void setupKnowledgeBase() throws Exception {
        NodeFactory nodeFactory = MethodCountingNodeFactory.getInstance();
        kbase = loadKnowledgeBaseFromString( nodeFactory, getRules());
        rules = rulestoMap(kbase);
        otn = getObjectTypeNode(kbase, Person.class );
        alphaNode_1 = ( AlphaNode ) otn.getObjectSinkPropagator().getSinks()[0]; //AlphaNode name == "Mark"
        alphaNode_2 = (AlphaNode) alphaNode_1.getObjectSinkPropagator().getSinks()[0]; // 2nd level (age = 50)

        lian_1 = (LeftInputAdapterNode) alphaNode_1.getObjectSinkPropagator().getSinks()[1];
        lian_2 = (LeftInputAdapterNode) alphaNode_2.getObjectSinkPropagator().getSinks()[0];

        AlphaNode an = ( AlphaNode ) otn.getObjectSinkPropagator().getSinks()[1]; // name = "John"
        LeftInputAdapterNode lian =(LeftInputAdapterNode) an.getObjectSinkPropagator().getSinks()[1];
        joinNode = (JoinNode) lian.getSinkPropagator().getSinks()[0]; //this == $personCheese
    }

    public static ObjectTypeNode getObjectTypeNode(KnowledgeBase kbase, Class<?> nodeClass) {
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

    @Test
    public void testOTNSharing() throws Exception {
        setupKnowledgeBase();
        assertEquals( 2, otn.getObjectSinkPropagator().size() );
        assertEquals(7, otn.getAssociationsSize());
    }

    @Test
    public void testAlphaNodeSharing() throws Exception {
        setupKnowledgeBase();

        assertEquals( 2, alphaNode_1.getObjectSinkPropagator().size());
        assertEquals( 4, alphaNode_1.getAssociationsSize() );
        assertTrue( alphaNode_1.isAssociatedWith(rules.get("r1")));
        assertTrue( alphaNode_1.isAssociatedWith(rules.get("r2")));
        assertTrue( alphaNode_1.isAssociatedWith(rules.get("r3")));
        assertTrue( alphaNode_1.isAssociatedWith(rules.get("r4")));
        Map<String, Integer> countingMap = ((MethodCountingAlphaNode)alphaNode_1).getMethodCountMap();
        assertNotNull(countingMap);
        assertEquals(6,countingMap.get("thisNodeEquals").intValue());

        //Check 2nd level of sharing (age = 50)
        assertEquals( 1, alphaNode_2.getObjectSinkPropagator().size() );
        assertEquals( 2, alphaNode_2.getAssociationsSize() );
        assertTrue( alphaNode_2.isAssociatedWith(rules.get("r3")));
        assertTrue( alphaNode_2.isAssociatedWith(rules.get("r4")));
        countingMap = ((MethodCountingAlphaNode)alphaNode_2).getMethodCountMap();
        assertNotNull(countingMap);
        assertEquals(1,countingMap.get("thisNodeEquals").intValue());
        assertNull (countingMap.get("equals")); //Make sure we are not using recursive "equals" method
    }

    @Test
    public void testLeftInputAdapterNodeSharing() throws Exception {
        setupKnowledgeBase();

        //Check first level of lian sharing
        assertEquals( 2, lian_1.getSinkPropagator().size() );
        assertTrue( lian_1.isAssociatedWith(rules.get("r1")));
        assertTrue( lian_1.isAssociatedWith(rules.get("r2")));
        RuleTerminalNode rtn1 = (RuleTerminalNode) lian_1.getSinkPropagator().getSinks()[0];
        assertTrue(rtn1.isAssociatedWith(rules.get("r1")));
        RuleTerminalNode rtn2 = (RuleTerminalNode) lian_1.getSinkPropagator().getSinks()[1];
        assertTrue(rtn2.isAssociatedWith(rules.get("r2")));

        Map<String, Integer> countingMap = ((MethodCountingLeftInputAdapterNode)lian_1).getMethodCountMap();
        assertNotNull(countingMap);
        assertEquals(3,countingMap.get("thisNodeEquals").intValue());

        //Check 2nd level of lian sharing
        assertEquals( 2, lian_2.getAssociationsSize());
        assertEquals( 2, lian_2.getSinkPropagator().size() );
        assertTrue( lian_2.isAssociatedWith(rules.get("r3")));
        assertTrue( lian_2.isAssociatedWith(rules.get("r4")));
        rtn1 = (RuleTerminalNode) lian_2.getSinkPropagator().getSinks()[0];
        assertTrue(rtn1.isAssociatedWith(rules.get("r3")));
        rtn2 = (RuleTerminalNode) lian_2.getSinkPropagator().getSinks()[1];
        assertTrue(rtn2.isAssociatedWith(rules.get("r4")));

        countingMap = ((MethodCountingLeftInputAdapterNode)lian_2).getMethodCountMap();
        assertNotNull(countingMap);
        assertEquals(1,countingMap.get("thisNodeEquals").intValue());
        assertNull (countingMap.get("equals")); //Make sure we are not using recursive "equals" method
    }

    @Test
    public void testJoinNodeSharing() throws Exception {
        setupKnowledgeBase();

        assertEquals( 2, joinNode.getSinkPropagator().size() );
        assertEquals( 2, joinNode.getAssociationsSize());
        assertTrue( joinNode.isAssociatedWith(rules.get("r6")));
        assertTrue( joinNode.isAssociatedWith(rules.get("r7")));

        MethodCountingObjectTypeNode betaOTN  = (MethodCountingObjectTypeNode) joinNode.getRightInput();
        Map<String, Integer> countingMap = betaOTN.getMethodCountMap();
        assertNull(countingMap);
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
}
