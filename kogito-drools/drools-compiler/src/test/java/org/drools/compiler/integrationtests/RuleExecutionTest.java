/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.integrationtests;

import org.drools.compiler.Person;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
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

    @Test
    public void testOnDeleteMatchConsequence() throws Exception {
        String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R1 when\n" +
                "    $p : Person( age > 30 )\n" +
                "then\n" +
                "    $p.setStatus(\"in\");\n" +
                "then[$onDeleteMatch$]\n" +
                "    $p.setStatus(\"out\");\n" +
                "end\n";

        KieSession ksession = new KieHelper()
                .addContent(str, ResourceType.DRL)
                .build()
                .newKieSession();

        Person mario = new Person("Mario", 40);
        FactHandle fact = ksession.insert(mario);
        ksession.fireAllRules();

        assertEquals("in", mario.getStatus());

        ksession.delete(fact);
        ksession.fireAllRules();

        assertEquals("out", mario.getStatus());
    }
}