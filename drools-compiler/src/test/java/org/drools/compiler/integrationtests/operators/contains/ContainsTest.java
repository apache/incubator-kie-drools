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

package org.drools.compiler.integrationtests.operators.contains;

import java.util.ArrayList;
import java.util.List;
import org.drools.compiler.Cheese;
import org.drools.compiler.Cheesery;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Order;
import org.drools.compiler.OrderItem;
import org.drools.compiler.Primitives;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

public class ContainsTest extends CommonTestMethodBase {

    @Test
    public void testContainsCheese() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_ContainsCheese.drl"));
        final KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final Cheese stilton = new Cheese("stilton", 12);
        ksession.insert(stilton);
        final Cheese brie = new Cheese("brie", 10);
        ksession.insert(brie);

        final Cheesery cheesery = new Cheesery();
        cheesery.getCheeses().add(stilton);
        ksession.insert(cheesery);

        ksession.fireAllRules();

        assertEquals(2, list.size());

        assertEquals(stilton, list.get(0));
        assertEquals(brie, list.get(1));
    }

    @Test
    public void testContainsInArray() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_contains_in_array.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final Primitives p = new Primitives();
        p.setStringArray(new String[]{"test1", "test3"});
        ksession.insert(p);

        ksession.fireAllRules();

        assertEquals(2, list.size());

        assertEquals("ok1", list.get(0));
        assertEquals("ok2", list.get(1));
    }

    @Test
    public void testNotContainsOperator() {
        // JBRULES-2404: "not contains" operator doesn't work on nested fields

        final String str = "package org.drools.compiler\n" +
                "rule NotContains\n" +
                "when\n" +
                "    $oi : OrderItem( )\n" +
                "    $o  : Order( items.values() not contains $oi )" +
                "then\n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = createKnowledgeSession(kbase);

        final Order order1 = new Order(1, "XYZ");
        final Order order2 = new Order(2, "ABC");
        final OrderItem item11 = new OrderItem(order1, 1);
        order1.addItem(item11);
        final OrderItem item21 = new OrderItem(order2, 1);
        order2.addItem(item21);

        ksession.insert(order1);
        ksession.insert(item11);

        // should not fire, as item11 is contained in order1.items
        int rules = ksession.fireAllRules();
        assertEquals(0, rules);

        // should fire as item21 is not contained in order1.items
        ksession.insert(item21);
        rules = ksession.fireAllRules();
        assertEquals(1, rules);
    }
}
