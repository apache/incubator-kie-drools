package org.drools.compiler.integrationtests;

import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PassivePatternTest {

    @Test
    public void testPassiveInsert() throws Exception {
        String str =
                "global java.util.List list\n" +
                "rule R when\n" +
                "    $i : Integer()\n" +
                "    ?String( this == $i.toString() )\n" +
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
        assertEquals(2, list.size());
        assertTrue(list.containsAll(asList(1, 2)));
    }

}
