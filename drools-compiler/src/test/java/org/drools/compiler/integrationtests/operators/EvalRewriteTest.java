/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests.operators;

import java.util.ArrayList;
import java.util.List;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Order;
import org.drools.compiler.OrderItem;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

public class EvalRewriteTest extends CommonTestMethodBase {

    @Test
    public void testEvalRewrite() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_EvalRewrite.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        final Order order1 = new Order(10, "Bob");
        final OrderItem item11 = new OrderItem(order1, 1);
        final OrderItem item12 = new OrderItem(order1, 2);
        order1.addItem(item11);
        order1.addItem(item12);

        final Order order2 = new Order(11, "Bob");
        final OrderItem item21 = new OrderItem(order2, 1);
        final OrderItem item22 = new OrderItem(order2, 2);
        order2.addItem(item21);
        order2.addItem(item22);

        final Order order3 = new Order(12, "Bob");
        final OrderItem item31 = new OrderItem(order3, 1);
        final OrderItem item32 = new OrderItem(order3, 2);
        order3.addItem(item31);
        order3.addItem(item32);

        final Order order4 = new Order(13, "Bob");
        final OrderItem item41 = new OrderItem(order4, 1);
        final OrderItem item42 = new OrderItem(order4, 2);
        order4.addItem(item41);
        order4.addItem(item42);

        ksession.insert(order1);
        ksession.insert(item11);
        ksession.insert(item12);
        ksession.insert(order2);
        ksession.insert(item21);
        ksession.insert(item22);
        ksession.insert(order3);
        ksession.insert(item31);
        ksession.insert(item32);
        ksession.insert(order4);
        ksession.insert(item41);
        ksession.insert(item42);

        ksession.fireAllRules();

        assertEquals(5, list.size());
        assertTrue(list.contains(item11));
        assertTrue(list.contains(item12));
        assertTrue(list.contains(item22));
        assertTrue(list.contains(order3));
        assertTrue(list.contains(order4));
    }

    @Test
    public void testEvalRewriteMatches() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_EvalRewriteMatches.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        final Order order1 = new Order(14, "Mark");
        final OrderItem item11 = new OrderItem(order1, 1);
        final OrderItem item12 = new OrderItem(order1, 2);
        order1.addItem(item11);
        order1.addItem(item12);

        ksession.insert(order1);
        ksession.insert(item11);
        ksession.insert(item12);

        ksession.fireAllRules();

        assertEquals(2, list.size());
        assertTrue(list.contains(item11));
        assertTrue(list.contains(item12));
    }

    @Test
    public void testEvalRewriteWithSpecialOperators() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_EvalRewriteWithSpecialOperators.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        final Order order1 = new Order(10, "Bob");
        final OrderItem item11 = new OrderItem(order1, 1);
        final OrderItem item12 = new OrderItem(order1, 2);
        order1.addItem(item11);
        order1.addItem(item12);

        final Order order2 = new Order(11, "Bob");
        final OrderItem item21 = new OrderItem(order2, 1);
        final OrderItem item22 = new OrderItem(order2, 2);
        order2.addItem(item21);
        order2.addItem(item22);

        final Order order3 = new Order(12, "Bob");
        final OrderItem item31 = new OrderItem(order3, 1);
        final OrderItem item32 = new OrderItem(order3, 2);
        final OrderItem item33 = new OrderItem(order3, 3);
        order3.addItem(item31);
        order3.addItem(item32);
        order3.addItem(item33);

        final Order order4 = new Order(13, "Bob");
        final OrderItem item41 = new OrderItem(order4, 1);
        final OrderItem item42 = new OrderItem(order4, 2);
        order4.addItem(item41);
        order4.addItem(item42);

        final Order order5 = new Order(14, "Mark");
        final OrderItem item51 = new OrderItem(order5, 1);
        final OrderItem item52 = new OrderItem(order5, 2);
        order5.addItem(item51);
        order5.addItem(item52);

        ksession.insert(order1);
        ksession.insert(item11);
        ksession.insert(item12);
        ksession.insert(order2);
        ksession.insert(item21);
        ksession.insert(item22);
        ksession.insert(order3);
        ksession.insert(item31);
        ksession.insert(item32);
        ksession.insert(item33);
        ksession.insert(order4);
        ksession.insert(item41);
        ksession.insert(item42);
        ksession.insert(order5);
        ksession.insert(item51);
        ksession.insert(item52);

        ksession.fireAllRules();

        assertEquals(9, list.size());
        int index = 0;
        assertEquals(item11, list.get(index++));
        assertEquals(item12, list.get(index++));
        assertEquals(item21, list.get(index++));
        assertEquals(item22, list.get(index++));
        assertEquals(item31, list.get(index++));
        assertEquals(item33, list.get(index++));
        assertEquals(item41, list.get(index++));
        assertEquals(order5, list.get(index++));
        assertEquals(order5, list.get(index));
    }
}
