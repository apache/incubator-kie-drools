package org.drools.compiler.integrationtests;

import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.*;
import org.drools.core.reteoo.builder.MethodCountingNodeFactory;
import org.drools.core.reteoo.builder.NodeFactory;
import org.junit.Test;
import org.kie.api.definition.rule.Rule;
import org.kie.internal.KnowledgeBase;

import java.util.List;
import java.util.Map;

import static org.drools.compiler.integrationtests.IncrementalCompilationTest.rulestoMap;

public class SharingTest extends CommonTestMethodBase {

    private KnowledgeBase kbase;
    private Map<String, Rule> rules;


    public void setupKnowledgeBase() throws Exception {
        NodeFactory nodeFactory = MethodCountingNodeFactory.getInstance();
        kbase = loadKnowledgeBaseFromString( nodeFactory, getRules());
        rules = rulestoMap(kbase);
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
        drl += "$cheddar : Cheese(type == \"cheddar\" )";
        drl += "   Person(name == \"Smith\",$cheddar == cheese)\n";
        drl += "then\n";
        drl += "end\n";
        drl += "rule r7\n";
        drl += "when\n";
        drl += "$cheddar : Cheese(type == \"cheddar\" )";
        drl += "   Person(name == \"Smith\",$cheddar == cheese)\n";
        drl += "then\n";
        drl += "end\n";
        return drl;
    }

    //@Test(timeout=10000)
    @Test()
    public void testNodeSharing() throws Exception {
        setupKnowledgeBase();
        ObjectTypeNode otn = getObjectTypeNode(kbase, Person.class );

        assertEquals( 3, otn.getSinkPropagator().size() );

        testAlphaNodeSharing(rules, otn);
        testLeftInputAdapterNodeSharing(rules, otn);
        testJoinNodeSharing(rules, otn);
    }

    @Test()
    public void testMethodCount() throws Exception {
        setupKnowledgeBase();
        ObjectTypeNode otn = getObjectTypeNode(kbase, Person.class );

        MethodCountingAlphaNode mcan = (MethodCountingAlphaNode) otn.getSinkPropagator().getSinks()[2]; // name = "Mark"
        assertNotNull(mcan.getMethodCountMap());
        assertEquals(2, mcan.getMethodCountMap().get("equals").intValue());
    }

    private void testAlphaNodeSharing(Map<String, Rule> rules, ObjectTypeNode otn) {
        //AlphaNode name == "Mark"
        AlphaNode an = ( AlphaNode ) otn.getSinkPropagator().getSinks()[2];
        testFirstLevelAlphaSharing(rules, an);
        testSecondLevelAlphaSharing(rules, (AlphaNode) an.getSinkPropagator().getSinks()[0]);
    }

    private void testFirstLevelAlphaSharing(Map<String, Rule> rules, AlphaNode an) {
        //name == "Mark"
        assertEquals( 2, an.getSinkPropagator().size());
        assertEquals( 4, an.getAssociationsSize() );
        assertTrue( an.isAssociatedWith(rules.get("r1")));
        assertTrue( an.isAssociatedWith(rules.get("r2")));
        assertTrue( an.isAssociatedWith(rules.get("r3")));
        assertTrue( an.isAssociatedWith(rules.get("r4")));
    }

    private void testSecondLevelAlphaSharing(Map<String, Rule> rules, AlphaNode an) {
        //Check 2nd level of sharing (age = 50)
        assertEquals( 1, an.getSinkPropagator().size() );
        assertEquals( 2, an.getAssociationsSize() );
        assertTrue( an.isAssociatedWith(rules.get("r3")));
        assertTrue( an.isAssociatedWith(rules.get("r4")));
    }


    private void testLeftInputAdapterNodeSharing(Map<String, Rule> rules, ObjectTypeNode otn) {
        //Check first level of lian sharing
        AlphaNode a1 = ( AlphaNode ) otn.getSinkPropagator().getSinks()[2]; //AlphaNode name == "Mark"
        LeftInputAdapterNode a1_lian = (LeftInputAdapterNode) a1.getSinkPropagator().getSinks()[1];
        assertEquals( 2, a1_lian.getSinkPropagator().size() );
        assertTrue( a1_lian.isAssociatedWith(rules.get("r1")));
        assertTrue( a1_lian.isAssociatedWith(rules.get("r2")));
        testRuleTerminalNodeSharing(rules, a1_lian);

        //Check 2nd level of lian sharing
        AlphaNode a1_1 = (AlphaNode) a1.getSinkPropagator().getSinks()[0];
        LeftInputAdapterNode a1_1_lian = (LeftInputAdapterNode) a1_1.getSinkPropagator().getSinks()[0];
        assertEquals( 2, a1_1_lian.getAssociationsSize());
        assertEquals( 2, a1_1_lian.getSinkPropagator().size() );
        assertTrue( a1_1_lian.isAssociatedWith(rules.get("r3")));
        assertTrue( a1_1_lian.isAssociatedWith(rules.get("r4")));
        testSeconLevelRuleTerminalNodeSharing(rules, a1_1_lian);
    }

    private void testJoinNodeSharing(Map<String, Rule> rules,ObjectTypeNode otn) {
        AlphaNode an = ( AlphaNode ) otn.getSinkPropagator().getSinks()[1]; // name = "Smith"
        JoinNode jn = (JoinNode) an.getSinkPropagator().getSinks()[0]; //cheese == $cheddar
        assertEquals( 2, jn.getSinkPropagator().size() );
        assertEquals( 2, jn.getAssociationsSize());
        assertTrue( jn.isAssociatedWith(rules.get("r6")));
        assertTrue( jn.isAssociatedWith(rules.get("r7")));
    }

    private void testRuleTerminalNodeSharing(Map<String, Rule> rules,LeftInputAdapterNode lian) {
        RuleTerminalNode rtn1 = (RuleTerminalNode) lian.getSinkPropagator().getSinks()[0];
        assertTrue(rtn1.isAssociatedWith(rules.get("r1")));
        RuleTerminalNode rtn2 = (RuleTerminalNode) lian.getSinkPropagator().getSinks()[1];
        assertTrue(rtn2.isAssociatedWith(rules.get("r2")));
    }

    private void testSeconLevelRuleTerminalNodeSharing(Map<String, Rule> rules,LeftInputAdapterNode lian) {
        RuleTerminalNode rtn1 = (RuleTerminalNode) lian.getSinkPropagator().getSinks()[0];
        assertTrue(rtn1.isAssociatedWith(rules.get("r3")));
        RuleTerminalNode rtn2 = (RuleTerminalNode) lian.getSinkPropagator().getSinks()[1];
        assertTrue(rtn2.isAssociatedWith(rules.get("r4")));
    }
}
