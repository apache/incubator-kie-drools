/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler;

import org.junit.Ignore;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.runtime.StatefulKnowledgeSession;

public class NestedAccessorsTest extends CommonTestMethodBase {

    @Test
    public void testNestedAccessor() throws Exception {
        String str = "import org.drools.compiler.*;\n" +
                "rule R1 when\n" +
                "   Person( name == \"mark\", cheese.(type == \"gorgonzola\", price == 10) )\n" +
                "then\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Person mark1 = new Person("mark");
        mark1.setCheese(new Cheese("gorgonzola", 10));
        ksession.insert(mark1);

        assertEquals(1, ksession.fireAllRules());
        ksession.dispose();
    }

    @Test
    public void testNestedAccessorWithBinding() throws Exception {
        String str = "import org.drools.compiler.*;\n" +
                "global StringBuilder sb\n" +
                "rule R1 when\n" +
                "   Person( name == \"mark\", cheese.(price == 10, $type : type) )\n" +
                "then\n" +
                "   sb.append( $type );\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        StringBuilder sb = new StringBuilder();
        ksession.setGlobal("sb", sb);

        Person mark1 = new Person("mark");
        mark1.setCheese(new Cheese("gorgonzola", 10));
        ksession.insert(mark1);

        assertEquals(1, ksession.fireAllRules());
        assertEquals("gorgonzola", sb.toString());
        ksession.dispose();
    }

    @Test
    public void testDoubleNestedAccessor() throws Exception {
        String str = "import org.drools.compiler.*;\n" +
                "rule R1 when\n" +
                "   Person( name == \"mark\", cheese.(price == 10, type.(length == 10) ) )\n" +
                "then\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Person mark1 = new Person("mark");
        mark1.setCheese(new Cheese("gorgonzola", 10));
        ksession.insert(mark1);

        assertEquals(1, ksession.fireAllRules());
        ksession.dispose();
    }

    @Test
    public void testNestedAccessorWithInlineCast() throws Exception {
        String str = "import org.drools.compiler.*;\n" +
                "rule R1 when\n" +
                "   Person( name == \"mark\", address#LongAddress.(country == \"uk\", suburb == \"suburb\") )\n" +
                "then\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Person mark1 = new Person("mark");
        mark1.setAddress(new LongAddress("street", "suburb", "zipCode", "uk"));
        ksession.insert(mark1);

        Person mark2 = new Person("mark");
        ksession.insert(mark2);

        Person mark3 = new Person("mark");
        mark3.setAddress(new Address("street", "suburb", "zipCode"));
        ksession.insert(mark3);

        assertEquals(1, ksession.fireAllRules());
        ksession.dispose();
    }

}
