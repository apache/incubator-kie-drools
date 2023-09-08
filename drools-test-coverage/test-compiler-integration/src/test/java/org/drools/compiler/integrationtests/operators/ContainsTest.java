/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.integrationtests.operators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.Cheesery;
import org.drools.testcoverage.common.model.Order;
import org.drools.testcoverage.common.model.OrderItem;
import org.drools.testcoverage.common.model.Primitives;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class ContainsTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public ContainsTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testContainsCheese() {
        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "import " + Cheesery.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "rule \"Cheesery contains stilton\"\n" +
                "    salience 10\n" +
                "    when\n" +
                "        stilton : Cheese( type == \"stilton\" )\n" +
                "        Cheesery( cheeses contains stilton )\n" +
                "    then\n" +
                "        list.add( stilton );\n" +
                "end   \n" +
                "\n" +
                "rule \"Cheesery does not contain brie\"\n" +
                "    when\n" +
                "        brie : Cheese( type == \"brie\" )\n" +
                "        Cheesery( cheeses not contains brie )\n" +
                "    then\n" +
                "        list.add( brie );\n" +
                "end ";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("contains-test",
                                                                           kieBaseTestConfiguration,
                                                                           drl);
        final KieSession ksession = kieBase.newKieSession();
        try {
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

            assertThat(list.size()).isEqualTo(2);

            assertThat(list.get(0)).isEqualTo(stilton);
            assertThat(list.get(1)).isEqualTo(brie);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testContainsInArray() {

         final String drl = "package org.drools.compiler.integrationtests.operators\n" +
                 "import " + Primitives.class.getCanonicalName() + " ;\n" +
                 "global java.util.List list;\n" +
                 "\n" +
                 "rule \"contains in elements\"\n" +
                 "    salience 10\n" +
                 "    when\n" +
                 "        Primitives( stringArray contains \"test1\" )\n" +
                 "    then\n" +
                 "        list.add( \"ok1\" );\n" +
                 "end\n" +
                 "\n" +
                 "rule \"excludes in elements\"\n" +
                 "    when\n" +
                 "        Primitives( stringArray excludes \"test2\" )\n" +
                 "    then\n" +
                 "        list.add( \"ok2\" );\n" +
                 "end";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("contains-test",
                                                                           kieBaseTestConfiguration,
                                                                           drl);
        final KieSession ksession = kieBase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("list", list);

            final Primitives p = new Primitives();
            p.setStringArray(new String[]{"test1", "test3"});
            ksession.insert(p);

            ksession.fireAllRules();

            assertThat(list.size()).isEqualTo(2);

            assertThat(list.get(0)).isEqualTo("ok1");
            assertThat(list.get(1)).isEqualTo("ok2");
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testNotContainsOperator() {
        // JBRULES-2404: "not contains" operator doesn't work on nested fields
        final String str = "package org.drools.compiler.integrationtests.operators\n" +
                "import " + Order.class.getCanonicalName() + " ;\n" +
                "import " + OrderItem.class.getCanonicalName() + " ;\n" +
                "rule NotContains\n" +
                "when\n" +
                "    $oi : OrderItem( )\n" +
                "    $o  : Order( items.values() not contains $oi )" +
                "then\n" +
                "end";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("contains-test",
                                                                           kieBaseTestConfiguration,
                                                                           str);
        final KieSession ksession = kieBase.newKieSession();
        try {
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
            assertThat(rules).isEqualTo(0);

            // should fire as item21 is not contained in order1.items
            ksession.insert(item21);
            rules = ksession.fireAllRules();
            assertThat(rules).isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }
}
