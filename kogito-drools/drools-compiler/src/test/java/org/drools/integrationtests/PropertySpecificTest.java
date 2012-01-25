package org.drools.integrationtests;

import org.drools.CommonTestMethodBase;
import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.type.FactType;
import org.drools.definition.type.Modifies;
import org.drools.definition.type.PropertySpecific;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class PropertySpecificTest extends CommonTestMethodBase {

    @Test(timeout = 5000)
    public void testPropertySpecific() throws Exception {
        String rule = "package org.drools\n" +
                "global java.util.List list;\n" +
                "declare A\n" +
                "    a : int\n" +
                "    b : int\n" +
                "    s : String\n" +
                "    i : int\n" +
                "end\n" +
                "declare B\n" +
                "    @propertySpecific\n" +
                "    s : String\n" +
                "    i : int\n" +
                "end\n" +
                "rule R1\n" +
                "when\n" +
                "    $a : A(s == \"start\");\n" +
                "    $b : B(s == $a.s);\n" +
                "then\n" +
                "    modify($a) { setS(\"running\") };\n" +
                "    list.add(\"R1\");\n" +
                "end\n" +
                "rule R2\n" +
                "when\n" +
                "    A($s : s);\n" +
                "    $b : B(s != $s);\n" +
                "then\n" +
                "    modify($b) { setS($s) };\n" +
                "    list.add(\"R2\");\n" +
                "end\n" +
                "rule R3\n" +
                "when\n" +
                "    $a : A(s != \"end\");\n" +
                "    $b : B(i > $a.i);\n" +
                "then\n" +
                "    modify($a) { setS(\"end\") };\n" +
                "    list.add(\"R3\");\n" +
                "end\n" +
                "rule R4\n" +
                "when\n" +
                "    $a : A(s == \"running\");\n" +
                "    $b : B(s != $a.s);\n" + // Slot specific allows to avoid an infinite loop even without the constraint i!=2
                // "    $b : B(i != 2, != $a.s);\n" + // add this constraint if you disable slot specific
                "then\n" +
                "    modify($b) { setI(2) };\n" +
                "    list.add(\"R4\");\n" +
                "end\n" +
                "rule R5\n" +
                "when\n" +
                "    $b : B(i == 2, s == \"running\");\n" +
                "then\n" +
                "    modify($b) { setI(4) };\n" +
                "    list.add(\"R5\");\n" +
                "end";
        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        FactType factTypeA = kbase.getFactType( "org.drools", "A" );
        Object factA = factTypeA.newInstance();
        factTypeA.set( factA, "s", "start" );
        factTypeA.set( factA, "i", 3 );
        ksession.insert( factA );

        FactType factTypeB = kbase.getFactType( "org.drools", "B" );
        Object factB = factTypeB.newInstance();
        factTypeB.set( factB, "s", "start" );
        factTypeB.set( factB, "i", 1 );
        ksession.insert( factB );

        List list = new ArrayList();
        ksession.setGlobal("list", list);

        int rules = ksession.fireAllRules();

        list = (List)ksession.getGlobal( "list" );
        System.out.println(list);

        assertEquals(6, rules);
        assertEquals("end", factTypeB.get(factB, "s"));
        assertEquals(4, factTypeB.get(factB, "i"));
        ksession.dispose();
    }

    @Test
    public void testPropertySpecificSimplified() throws Exception {
        String rule = "package org.drools\n" +
                "dialect \"mvel\"\n" +
                "declare A\n" +
                "    s : String\n" +
                "end\n" +
                "declare B\n" +
                "    @propertySpecific\n" +
                "    on : boolean\n" +
                "    s : String\n" +
                "end\n" +
                "rule R1\n" +
                "when\n" +
                "    A($s : s)\n" +
                "    $b : B(s != $s) @watch( ! s, on )\n" +
                "then\n" +
                "    modify($b) { setS($s) }\n" +
                "end\n" +
                "rule R2\n" +
                "when\n" +
                "    $b : B(on == false)\n" +
                "then\n" +
                "    modify($b) { setOn(true) }\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        FactType factTypeA = kbase.getFactType( "org.drools", "A" );
        Object factA = factTypeA.newInstance();
        factTypeA.set( factA, "s", "y" );
        ksession.insert( factA );

        FactType factTypeB = kbase.getFactType( "org.drools", "B" );
        Object factB = factTypeB.newInstance();
        factTypeB.set( factB, "on", false );
        factTypeB.set( factB, "s", "x" );
        ksession.insert( factB );

        int rules = ksession.fireAllRules();
        assertEquals(2, rules);

        assertEquals(true, factTypeB.get(factB, "on"));
        assertEquals("y", factTypeB.get(factB, "s"));
        ksession.dispose();
    }

    @Test
    public void testWatchNothing() throws Exception {
        String rule = "package org.drools\n" +
                "dialect \"mvel\"\n" +
                "declare A\n" +
                "    s : String\n" +
                "end\n" +
                "declare B\n" +
                "    @propertySpecific\n" +
                "    on : boolean\n" +
                "    s : String\n" +
                "end\n" +
                "rule R1\n" +
                "when\n" +
                "    A($s : s)\n" +
                "    $b : B(s != $s) @watch( !* )\n" +
                "then\n" +
                "    modify($b) { setS($s) }\n" +
                "end\n" +
                "rule R2\n" +
                "when\n" +
                "    $b : B(on == false)\n" +
                "then\n" +
                "    modify($b) { setOn(true) }\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        FactType factTypeA = kbase.getFactType( "org.drools", "A" );
        Object factA = factTypeA.newInstance();
        factTypeA.set( factA, "s", "y" );
        ksession.insert( factA );

        FactType factTypeB = kbase.getFactType( "org.drools", "B" );
        Object factB = factTypeB.newInstance();
        factTypeB.set( factB, "on", false );
        factTypeB.set( factB, "s", "x" );
        ksession.insert( factB );

        int rules = ksession.fireAllRules();
        assertEquals(2, rules);

        assertEquals(true, factTypeB.get(factB, "on"));
        assertEquals("y", factTypeB.get(factB, "s"));
        ksession.dispose();
    }

    @Test
    public void testWrongPropertyNameInWatchAnnotation() throws Exception {
        String rule = "package org.drools\n" +
                "dialect \"mvel\"\n" +
                "declare A\n" +
                "    s : String\n" +
                "end\n" +
                "declare B\n" +
                "    @propertySpecific\n" +
                "    on : boolean\n" +
                "    s : String\n" +
                "end\n" +
                "rule R1\n" +
                "when\n" +
                "    A($s : s)\n" +
                "    $b : B(s != $s) @watch( !s1, on )\n" +
                "then\n" +
                "    modify($b) { setS($s) }\n" +
                "end\n" +
                "rule R2\n" +
                "when\n" +
                "    $b : B(on == false)\n" +
                "then\n" +
                "    modify($b) { setOn(true) }\n" +
                "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(rule.getBytes()), ResourceType.DRL );
        assertEquals(1, kbuilder.getErrors().size());
    }

    @Test
    public void testDuplicatePropertyNamesInWatchAnnotation() throws Exception {
        String rule = "package org.drools\n" +
                "dialect \"mvel\"\n" +
                "declare A\n" +
                "    s : String\n" +
                "end\n" +
                "declare B\n" +
                "    @propertySpecific\n" +
                "    on : boolean\n" +
                "    s : String\n" +
                "end\n" +
                "rule R1\n" +
                "when\n" +
                "    A($s : s)\n" +
                "    $b : B(s != $s) @watch( s, !s )\n" +
                "then\n" +
                "    modify($b) { setS($s) }\n" +
                "end\n" +
                "rule R2\n" +
                "when\n" +
                "    $b : B(on == false)\n" +
                "then\n" +
                "    modify($b) { setOn(true) }\n" +
                "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(rule.getBytes()), ResourceType.DRL );
        assertEquals(1, kbuilder.getErrors().size());
    }

    @Test
    public void testWrongUasgeOfWatchAnnotationOnNonPropertySpecificClass() throws Exception {
        String rule = "package org.drools\n" +
                "dialect \"mvel\"\n" +
                "declare A\n" +
                "    s : String\n" +
                "end\n" +
                "declare B\n" +
                "    on : boolean\n" +
                "    s : String\n" +
                "end\n" +
                "rule R1\n" +
                "when\n" +
                "    A($s : s)\n" +
                "    $b : B(s != $s) @watch( !s, on )\n" +
                "then\n" +
                "    modify($b) { setS($s) }\n" +
                "end\n" +
                "rule R2\n" +
                "when\n" +
                "    $b : B(on == false)\n" +
                "then\n" +
                "    modify($b) { setOn(true) }\n" +
                "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(rule.getBytes()), ResourceType.DRL );
        assertEquals(1, kbuilder.getErrors().size());
    }

    @Test
    public void testPropertySpecificJavaBean() throws Exception {
        String rule = "package org.drools\n" +
                "import org.drools.integrationtests.PropertySpecificTest.C\n" +
                "declare A\n" +
                "    s : String\n" +
                "end\n" +
                "rule R1\n" +
                "when\n" +
                "    A($s : s)\n" +
                "    $c : C(s != $s)\n" +
                "then\n" +
                "    modify($c) { setS($s) }\n" +
                "end\n" +
                "rule R2\n" +
                "when\n" +
                "    $c : C(on == false)\n" +
                "then\n" +
                "    modify($c) { turnOn() }\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        FactType factTypeA = kbase.getFactType( "org.drools", "A" );
        Object factA = factTypeA.newInstance();
        factTypeA.set( factA, "s", "y" );
        ksession.insert( factA );

        C c = new C();
        c.setOn(false);
        c.setS("x");
        ksession.insert( c );

        int rules = ksession.fireAllRules();
        assertEquals(2, rules);

        assertEquals(true, c.isOn());
        assertEquals("y", c.getS());
        ksession.dispose();
    }

    @Test(timeout = 5000) @Ignore
    public void testInfiniteLoop() throws Exception {
        String rule = "package org.drools\n" +
                "import org.drools.integrationtests.PropertySpecificTest.C\n" +
                "rule R1\n" +
                "when\n" +
                "    $c : C(s == \"test\")\n" +
                "then\n" +
                "    modify($c) { turnOn() }\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        C c = new C();
        c.setOn(false);
        c.setS("test");
        ksession.insert( c );

        ksession.fireAllRules();
        ksession.dispose();
    }

    @PropertySpecific
    public static class C {
        private boolean on;
        private String s;

        public boolean isOn() {
            return on;
        }

        public void setOn(boolean on) {
            this.on = on;
        }

        @Modifies( { "on" } )
        public void turnOn() {
            setOn(true);
        }

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }
    }
}
