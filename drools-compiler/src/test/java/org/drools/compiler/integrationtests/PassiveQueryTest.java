package org.drools.compiler.integrationtests;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.Rete;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.utils.KieHelper;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class PassiveQueryTest {

    @Test
    public void testPassiveQuery() throws Exception {
        String str =
                "global java.util.List list\n" +
                "query Q (Integer i)\n" +
                "    String( this == i.toString() )\n" +
                "end\n" +
                "rule R when\n" +
                "    $i : Integer()\n" +
                "    ?Q( $i; )\n" +
                "then\n" +
                "    list.add( $i );\n" +
                "end\n";

        KieSession ksession = new KieHelper()
                .addContent(str, ResourceType.DRL)
                .build()
                .newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        ksession.insert(1);
        ksession.insert("2");
        ksession.fireAllRules();
        assertEquals(0, list.size());

        ksession.insert("1");
        ksession.fireAllRules();
        assertEquals(0, list.size());

        ksession.insert(2);
        ksession.fireAllRules();
        assertEquals(1, list.size());
        assertEquals(2, (int)list.get(0));
    }

    @Test
    public void testPassiveQueryNoDataDriven() throws Exception {
        String str =
                "global java.util.List list\n" +
                "query Q (Integer i)\n" +
                "    String( this == i.toString() )\n" +
                "end\n" +
                "rule R when\n" +
                "    $i : Integer()\n" +
                "    ?Q( $i; )\n" +
                "then\n" +
                "    list.add( $i );\n" +
                "end\n";

        KieSession ksession = new KieHelper()
                .addContent(str, ResourceType.DRL)
                .build()
                .newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        ksession.insert(1);
        ksession.insert("1");
        ksession.fireAllRules();
        assertEquals(1, list.size());
    }

    @Test
    public void testPassiveQueryDataDriven() throws Exception {
        String str =
                "global java.util.List list\n" +
                "query Q (Integer i)\n" +
                "    String( this == i.toString() )\n" +
                "end\n" +
                "rule R @Propagation(IMMEDIATE) when\n" +
                "    $i : Integer()\n" +
                "    ?Q( $i; )\n" +
                "then\n" +
                "    list.add( $i );\n" +
                "end\n";

        KieSession ksession = new KieHelper()
                .addContent(str, ResourceType.DRL)
                .build()
                .newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        ksession.insert(1);
        ksession.insert("1");
        ksession.fireAllRules();
        assertEquals(0, list.size());
    }

    @Test
    public void testReactiveQueryDataDriven() throws Exception {
        String str =
                "global java.util.List list\n" +
                "query Q (Integer i)\n" +
                "    String( this == i.toString() )\n" +
                "end\n" +
                "rule R @Propagation(IMMEDIATE) when\n" +
                "    $i : Integer()\n" +
                "    Q( $i; )\n" +
                "then\n" +
                "    list.add( $i );\n" +
                "end\n";

        KieSession ksession = new KieHelper()
                .addContent(str, ResourceType.DRL)
                .build()
                .newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        ksession.insert(1);
        ksession.insert("1");
        ksession.fireAllRules();
        assertEquals(1, list.size());
    }

    @Test
    public void testPassiveQueryDataDrivenWithBeta() throws Exception {
        String str =
                "global java.util.List list\n" +
                "query Q (Integer i)\n" +
                "    String( this == i.toString() )\n" +
                "end\n" +
                "rule R @Propagation(IMMEDIATE) when\n" +
                "    $i : Integer()\n" +
                "    $j : Integer( this == $i+1 )\n" +
                "    ?Q( $j; )\n" +
                "then\n" +
                "    list.add( $j );\n" +
                "end\n";

        KieSession ksession = new KieHelper()
                .addContent(str, ResourceType.DRL)
                .build()
                .newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        ksession.insert(1);
        ksession.insert(2);
        ksession.insert("2");
        ksession.fireAllRules();
        assertEquals(0, list.size());
    }

    @Test
    public void testPassiveQueryNodeSharing() throws Exception {
        String str =
                "global java.util.List list\n" +
                "query Q (Integer i)\n" +
                "    String( this == i.toString() )\n" +
                "end\n" +
                "rule R1 @Propagation(IMMEDIATE) when\n" +
                "    $i : Integer()\n" +
                "    ?Q( $i; )\n" +
                "then\n" +
                "    list.add( \"R1\" );\n" +
                "end\n" +
                "rule R2 when\n" +
                "    $i : Integer()\n" +
                "    ?Q( $i; )\n" +
                "then\n" +
                "    list.add( \"R2\" );\n" +
                "end\n";

        KieSession ksession = new KieHelper()
                .addContent(str, ResourceType.DRL)
                .build()
                .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.insert(1);
        ksession.insert("1");
        ksession.fireAllRules();
        assertEquals(1, list.size());
        assertEquals("R2", list.get(0));
    }

    @Test
    public void testPassiveQueryNodeSharing2() throws Exception {
        String str =
                "global java.util.List list\n" +
                "query Q (Integer i)\n" +
                "    String( this == i.toString() )\n" +
                "end\n" +
                "rule R1a @Propagation(IMMEDIATE) when\n" +
                "    $i : Integer()\n" +
                "    ?Q( $i; )\n" +
                "then\n" +
                "    list.add( \"R1a\" );\n" +
                "end\n" +
                "rule R1b @Propagation(IMMEDIATE) when\n" +
                "    Long( $i : intValue )\n" +
                "    ?Q( $i; )\n" +
                "then\n" +
                "    list.add( \"R1b\" );\n" +
                "end\n" +
                "rule R2a when\n" +
                "    $i : Integer()\n" +
                "    ?Q( $i; )\n" +
                "then\n" +
                "    list.add( \"R2a\" );\n" +
                "end\n" +
                "rule R2b when\n" +
                "    Long( $i : intValue )\n" +
                "    ?Q( $i; )\n" +
                "then\n" +
                "    list.add( \"R2b\" );\n" +
                "end\n";

        KieSession ksession = new KieHelper()
                .addContent(str, ResourceType.DRL)
                .build()
                .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.insert(1);
        ksession.insert(1L);
        ksession.insert("1");
        ksession.fireAllRules();
        assertEquals(2, list.size());
        assertTrue(list.containsAll(asList("R2a", "R2b")));
    }

    @Test
    public void testPassiveQueryUsingSegmentPropagator() throws Exception {
        String str =
                "global java.util.List list\n" +
                "query Q (Integer i)\n" +
                "    String( this == i.toString() )\n" +
                "end\n" +
                "rule R1a @Propagation(IMMEDIATE) when\n" +
                "    $i : Integer()\n" +
                "    ?Q( $i; )\n" +
                "then\n" +
                "    list.add( \"R1a\" );\n" +
                "end\n" +
                "rule R1b @Propagation(IMMEDIATE) when\n" +
                "    $i : Integer()\n" +
                "    ?Q( $i; )\n" +
                "    Long( intValue == $i )\n" +
                "then\n" +
                "    list.add( \"R1b\" );\n" +
                "end\n" +
                "rule R2 when\n" +
                "    $i : Integer()\n" +
                "    ?Q( $i; )\n" +
                "then\n" +
                "    list.add( \"R2\" );\n" +
                "end\n";

        KieBase kbase = new KieHelper()
                .addContent(str, ResourceType.DRL)
                .build();

        KieSession ksession = kbase.newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.insert(1L);
        FactHandle fh = ksession.insert(1);
        ksession.insert("1");

        Rete rete = ((KnowledgeBaseImpl)kbase).getRete();
        LeftInputAdapterNode lia = null;

        for (ObjectTypeNode otn : rete.getObjectTypeNodes()) {
            if ( Integer.class == otn.getObjectType().getValueType().getClassType() ) {
                lia = (LeftInputAdapterNode)otn.getSinkPropagator().getSinks()[0];
                break;
            }
        }

        LeftTupleSink[] sinks = lia.getSinkPropagator().getSinks();
        QueryElementNode q1 = (QueryElementNode)sinks[0];
        QueryElementNode q2 = (QueryElementNode)sinks[1];

        InternalWorkingMemory wm = (InternalWorkingMemory)ksession;
        wm.flushPropagations();

        Memory memory1 = wm.getNodeMemory(q1);
        assertTrue(memory1.getSegmentMemory().getStagedLeftTuples().isEmpty());

        Memory memory2 = wm.getNodeMemory(q2);
        assertFalse(memory2.getSegmentMemory().getStagedLeftTuples().isEmpty());
        assertNotNull(memory2.getSegmentMemory().getStagedLeftTuples().getInsertFirst());

        ksession.fireAllRules();
        assertEquals(1, list.size());
        assertEquals("R2", list.get(0));

        list.clear();

        ksession.delete(fh);
        ksession.insert(1);
        ksession.fireAllRules();
        assertEquals(3, list.size());
    }
}
