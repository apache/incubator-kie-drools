package org.drools.compiler.integrationtests;

import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class RuleExecutionTest {

    @Test
    public void testNoAll() throws Exception {
        String str =
                "global java.util.List list\n" +
                "rule R1 when\n" +
                "    $i : Integer()\n" +
                "    $s : String( this == $i.toString() )\n" +
                "then\n" +
                "    list.add( $i );\n" +
                "end\n" +
                "\n" +
                "rule R2 when\n" +
                "    $i : Integer()\n" +
                "    $s : Long( intValue == $i )\n" +
                "then\n" +
                "    insert( \"\" + $i );\n" +
                "    list.add( -$i );\n" +
                "end\n";

        KieSession ksession = new KieHelper()
                .addContent(str, ResourceType.DRL)
                .build()
                .newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        ksession.insert(1);
        ksession.insert(2);
        ksession.insert(3);
        ksession.insert(1L);
        ksession.insert(2L);
        ksession.insert(3L);
        ksession.fireAllRules();

        assertEquals(asList(-1, 1, -2, 2, -3, 3), list);
    }

    @Test
    public void testAll() throws Exception {
        String str =
                "global java.util.List list\n" +
                "rule R1 when\n" +
                "    $i : Integer()\n" +
                "    $s : String( this == $i.toString() )\n" +
                "then\n" +
                "    list.add( $i );\n" +
                "end\n" +
                "\n" +
                "rule R2 @All when\n" +
                "    $i : Integer()\n" +
                "    $s : Long( intValue == $i )\n" +
                "then\n" +
                "    insert( \"\" + $i );\n" +
                "    list.add( -$i );\n" +
                "end\n";

        KieSession ksession = new KieHelper()
                .addContent(str, ResourceType.DRL)
                .build()
                .newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        ksession.insert(1);
        ksession.insert(2);
        ksession.insert(3);
        ksession.insert(1L);
        ksession.insert(2L);
        ksession.insert(3L);
        ksession.fireAllRules();

        assertEquals(asList(-1, -2, -3, 1, 2, 3), list);
    }

    @Test
    public void testAllWithBeforeAndAfter() throws Exception {
        String str =
                "global java.util.List list\n" +
                "rule R1 when\n" +
                "    $i : Integer()\n" +
                "    $s : String( this == $i.toString() )\n" +
                "then\n" +
                "    list.add( $i );\n" +
                "end\n" +
                "\n" +
                "rule R2 @All when\n" +
                "    $i : Integer()\n" +
                "    $s : Long( intValue == $i )\n" +
                "then\n" +
                "    insert( \"\" + $i );\n" +
                "    list.add( -$i );\n" +
                "then[$onBeforeAllFire$]\n" +
                "    list.add( -$i * 5 );\n" +
                "then[$onAfterAllFire$]\n" +
                "    list.add( -$i * 4 );\n" +
                "end\n";

        KieSession ksession = new KieHelper()
                .addContent(str, ResourceType.DRL)
                .build()
                .newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        ksession.insert(1);
        ksession.insert(2);
        ksession.insert(3);
        ksession.insert(1L);
        ksession.insert(2L);
        ksession.insert(3L);
        ksession.fireAllRules();

        assertEquals(asList(-5,         // onBeforeAllFire
                            -1, -2, -3, // all R2
                            -12,        // onAfterAllFire
                            1, 2, 3     // R1
                           ), list);
    }

    public static void print(String s) {
        System.out.println(s);
    }
}
