package org.drools.compiler.integrationtests;

import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.core.base.ClassObjectType;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.reteoo.*;
import org.drools.core.reteoo.builder.MethodCountingNodeFactory;
import org.drools.core.reteoo.builder.NodeFactory;
import org.junit.Test;
import org.kie.api.definition.rule.Rule;
import org.kie.internal.KnowledgeBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.drools.compiler.integrationtests.IncrementalCompilationTest.rulestoMap;

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
        alphaNode_1 = ( AlphaNode ) otn.getSinkPropagator().getSinks()[0]; //AlphaNode name == "Mark"
        alphaNode_2 = (AlphaNode) alphaNode_1.getSinkPropagator().getSinks()[0]; // 2nd level (age = 50)

        lian_1 = (LeftInputAdapterNode) alphaNode_1.getSinkPropagator().getSinks()[1];
        lian_2 = (LeftInputAdapterNode) alphaNode_2.getSinkPropagator().getSinks()[0];

        AlphaNode an = ( AlphaNode ) otn.getSinkPropagator().getSinks()[1]; // name = "John"
        LeftInputAdapterNode lian =(LeftInputAdapterNode) an.getSinkPropagator().getSinks()[1];
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

    //@Test(timeout=10000)
    @Test()
    public void testOTNSharing() throws Exception {
        setupKnowledgeBase();
        assertEquals( 2, otn.getSinkPropagator().size() );
        assertEquals(7, otn.getAssociationsSize());
    }

    @Test
    public void testAlphaNodeSharing() throws Exception {
        setupKnowledgeBase();

        assertEquals( 2, alphaNode_1.getSinkPropagator().size());
        assertEquals( 4, alphaNode_1.getAssociationsSize() );
        assertTrue( alphaNode_1.isAssociatedWith(rules.get("r1")));
        assertTrue( alphaNode_1.isAssociatedWith(rules.get("r2")));
        assertTrue( alphaNode_1.isAssociatedWith(rules.get("r3")));
        assertTrue( alphaNode_1.isAssociatedWith(rules.get("r4")));
        Map<String, Integer> countingMap = ((MethodCountingAlphaNode)alphaNode_1).getMethodCountMap();
        assertNotNull(countingMap);
        assertEquals(6,countingMap.get("thisNodeEquals").intValue());

        //Check 2nd level of sharing (age = 50)
        assertEquals( 1, alphaNode_2.getSinkPropagator().size() );
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
        assertNotNull(countingMap);
        assertEquals(1,countingMap.get("thisNodeEquals").intValue());
        assertNull (countingMap.get("equals")); //Make sure we are not using recursive "equals" method
    }
}
