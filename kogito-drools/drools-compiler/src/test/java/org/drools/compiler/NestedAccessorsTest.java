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

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.runtime.KieSession;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.mockito.ArgumentCaptor;

public class NestedAccessorsTest extends CommonTestMethodBase {

    @Test
    public void testNestedAccessor() throws Exception {
        final String str = "import org.drools.compiler.*;\n" +
                "rule R1 when\n" +
                "   Person( name == \"mark\", cheese.(type == \"gorgonzola\", price == 10) )\n" +
                "then\n" +
                "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = kbase.newKieSession();

        final Person mark1 = new Person("mark");
        mark1.setCheese(new Cheese("gorgonzola", 10));
        ksession.insert(mark1);

        assertEquals(1, ksession.fireAllRules());
        ksession.dispose();
    }

    @Test
    public void testNestedAccessorWithBinding() throws Exception {
        final String str = "import org.drools.compiler.*;\n" +
                "global StringBuilder sb\n" +
                "rule R1 when\n" +
                "   Person( name == \"mark\", cheese.(price == 10, $type : type) )\n" +
                "then\n" +
                "   sb.append( $type );\n" +
                "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = kbase.newKieSession();

        final StringBuilder sb = new StringBuilder();
        ksession.setGlobal("sb", sb);

        final Person mark1 = new Person("mark");
        mark1.setCheese(new Cheese("gorgonzola", 10));
        ksession.insert(mark1);

        assertEquals(1, ksession.fireAllRules());
        assertEquals("gorgonzola", sb.toString());
        ksession.dispose();
    }

    @Test
    public void testDoubleNestedAccessor() throws Exception {
        final String str = "import org.drools.compiler.*;\n" +
                "rule R1 when\n" +
                "   Person( name == \"mark\", cheese.(price == 10, type.(length == 10) ) )\n" +
                "then\n" +
                "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = kbase.newKieSession();

        final Person mark1 = new Person("mark");
        mark1.setCheese(new Cheese("gorgonzola", 10));
        ksession.insert(mark1);

        assertEquals(1, ksession.fireAllRules());
        ksession.dispose();
    }

    @Test
    public void testNestedAccessorWithInlineCast() throws Exception {
        final String str = "import org.drools.compiler.*;\n" +
                "rule R1 when\n" +
                "   Person( name == \"mark\", address#LongAddress.(country == \"uk\", suburb == \"suburb\") )\n" +
                "then\n" +
                "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = kbase.newKieSession();

        final Person mark1 = new Person("mark");
        mark1.setAddress(new LongAddress("street", "suburb", "zipCode", "uk"));
        ksession.insert(mark1);

        final Person mark2 = new Person("mark");
        ksession.insert(mark2);

        final Person mark3 = new Person("mark");
        mark3.setAddress(new Address("street", "suburb", "zipCode"));
        ksession.insert(mark3);

        assertEquals(1, ksession.fireAllRules());
        ksession.dispose();
    }

    @Test
    public void testNestedAccessors() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_NestedAccessors.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        final Order order1 = new Order(11, "Bob");
        final OrderItem item11 = new OrderItem(order1, 1);
        final OrderItem item12 = new OrderItem(order1, 2);
        order1.addItem(item11);
        order1.addItem(item12);

        ksession.insert(order1);
        ksession.insert(item11);
        ksession.insert(item12);

        ksession.fireAllRules();

        assertEquals(0, list.size());

        final Order order2 = new Order(12, "Mark");
        final Order.OrderStatus status = new Order.OrderStatus();
        status.setActive(true);
        order2.setStatus(status);
        final OrderItem item21 = new OrderItem(order2, 1);
        final OrderItem item22 = new OrderItem(order2, 2);
        order1.addItem(item21);
        order1.addItem(item22);

        ksession.insert(order2);
        ksession.insert(item21);
        ksession.insert(item22);

        ksession.fireAllRules();

        assertEquals(2, list.size());
        assertSame(item21, list.get(0));
        assertSame(item22, list.get(1));
    }

    @Test
    public void testNestedAccessors2() throws Exception {
        final String rule = "package org.drools.compiler\n" +
                "rule 'rule1'" +
                "    salience 10\n" +
                "when\n" +
                "    Cheesery( typedCheeses[0].type == 'stilton' );\n" +
                "then\n" +
                "end\n" +
                "rule 'rule2'\n" +
                "when\n" +
                "    Cheesery( typedCheeses[0].price == 10 );\n" +
                "then\n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString(rule);
        final KieSession ksession = createKnowledgeSession(kbase);
        final org.kie.api.event.rule.AgendaEventListener ael = mock(org.kie.api.event.rule.AgendaEventListener.class);
        ksession.addEventListener(ael);

        final Cheesery c1 = new Cheesery();
        c1.addCheese(new Cheese("stilton", 20));
        final Cheesery c2 = new Cheesery();
        c2.addCheese(new Cheese("brie", 10));
        final Cheesery c3 = new Cheesery();
        c3.addCheese(new Cheese("muzzarella", 30));

        ksession.insert(c1);
        ksession.insert(c2);
        ksession.insert(c3);
        ksession.fireAllRules();

        final ArgumentCaptor<AfterMatchFiredEvent> captor = ArgumentCaptor.forClass(org.kie.api.event.rule.AfterMatchFiredEvent.class);
        verify(ael, times(2)).afterMatchFired(captor.capture());

        final List<org.kie.api.event.rule.AfterMatchFiredEvent> values = captor.getAllValues();
        assertThat(values.get(0).getMatch().getObjects().get(0), is(c1));
        assertThat(values.get(1).getMatch().getObjects().get(0), is(c2));

        ksession.dispose();
    }
}
