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

import org.drools.testcoverage.common.model.Order;
import org.drools.testcoverage.common.model.OrderItem;
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
public class EvalRewriteTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public EvalRewriteTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testEvalRewrite() {

        final String drl = "package org.drools.compiler.integrationtests.operators\n" +
                "import " + OrderItem.class.getCanonicalName() + " ;\n" +
                "import " + Order.class.getCanonicalName() + " ;\n" +
                "global java.util.List results;\n" +
                "\n" +
                "\n" +
                "rule \"eval rewrite\"\n" +
                "    when\n" +
                "        $o : OrderItem( order.number == 10 )\n" +
                "    then\n" +
                "        results.add( $o );\n" +
                "end\n" +
                "\n" +
                "rule \"eval rewrite2\"\n" +
                "    when\n" +
                "        $o1 : OrderItem( order.number == 11, $seq : seq == 1 )\n" +
                "        $o2 : OrderItem( order.number == $o1.order.number, seq != $seq )\n" +
                "    then\n" +
                "        results.add( $o2 );\n" +
                "end\n" +
                "\n" +
                "rule \"eval rewrite3\"\n" +
                "    when\n" +
                "        $o1 : OrderItem( order.number == 12, seq == 1 )\n" +
                "        $o : Order( items[(Integer) 1] == $o1 )\n" +
                "    then\n" +
                "        results.add( $o );\n" +
                "end\n" +
                "\n" +
                "rule \"eval rewrite4\"\n" +
                "    when\n" +
                "        OrderItem( $nbr : order.number == 13, seq == 1 )\n" +
                "        $o : Order( number == $nbr )\n" +
                "    then\n" +
                "        results.add( $o );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("eval-rewrite-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
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

            assertThat(list.size()).isEqualTo(5);
            assertThat(list.contains(item11)).isTrue();
            assertThat(list.contains(item12)).isTrue();
            assertThat(list.contains(item22)).isTrue();
            assertThat(list.contains(order3)).isTrue();
            assertThat(list.contains(order4)).isTrue();
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testEvalRewriteMatches() {

        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + OrderItem.class.getCanonicalName() + " ;\n" +
                "import " + Order.class.getCanonicalName() + " ;\n" +
                "global java.util.List results;\n" +
                "\n" +
                "rule \"eval rewrite with 'matches'\"\n" +
                "    salience 20\n" +
                "    when\n" +
                "        $oi : OrderItem( order.number == 14, seq == 1, order.customer matches \"M\\\\w*\" )\n" +
                "    then\n" +
                "        results.add( $oi );\n" +
                "end\n" +
                "\n" +
                "rule \"eval rewrite with 'not matches'\"\n" +
                "    salience 10\n" +
                "    when\n" +
                "        $oi : OrderItem( order.number == 14, seq == 2, order.customer not matches \"B\\\\w*\" )\n" +
                "    then\n" +
                "        results.add( $oi );\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("eval-rewrite-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
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

            assertThat(list.size()).isEqualTo(2);
            assertThat(list.contains(item11)).isTrue();
            assertThat(list.contains(item12)).isTrue();
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testEvalRewriteWithSpecialOperators() {

        final String drl = "package org.drools.compiler\n" +
                "import " + OrderItem.class.getCanonicalName() + " ;\n" +
                "import " + Order.class.getCanonicalName() + " ;\n" +
                "global java.util.List results;\n" +
                "\n" +
                "// [not] in\n" +
                "// [not] contains\n" +
                "// [not] memberOf\n" +
                "// excludes\n" +
                "// [not] matches\n" +
                "\n" +
                "rule \"eval rewrite with 'in'\"\n" +
                "    salience 100\n" +
                "    when\n" +
                "        Order( $id : number == 10 )\n" +
                "        $o : OrderItem( order.number in ( 1, (1+1), $id ), seq == 1 )\n" +
                "    then\n" +
                "        results.add( $o );\n" +
                "end\n" +
                "\n" +
                "rule \"eval rewrite with 'not in'\"\n" +
                "    salience 90\n" +
                "    when\n" +
                "        Order( $id : number == 10 )\n" +
                "        $o : OrderItem( order.number not in ( 1, (1+1), ( $id + 1 ) ), order.number == $id, seq == 2 )\n" +
                "    then\n" +
                "        results.add( $o );\n" +
                "end\n" +
                "\n" +
                "rule \"eval rewrite with 'contains'\"\n" +
                "    salience 80\n" +
                "    when\n" +
                "        $o : OrderItem( order.number == 11, seq == 1 )\n" +
                "        Order( this.itemsValues contains $o )\n" +
                "    then\n" +
                "        results.add( $o );\n" +
                "end\n" +
                "\n" +
                "rule \"eval rewrite with 'not contains'\"\n" +
                "    salience 70\n" +
                "    when\n" +
                "        $o : OrderItem( order.number == 11, seq == 2 )\n" +
                "        Order( number == 12, this.itemsValues not contains $o )\n" +
                "    then\n" +
                "        results.add( $o );\n" +
                "end\n" +
                "\n" +
                "rule \"eval rewrite with 'memberOf'\"\n" +
                "    salience 60\n" +
                "    when\n" +
                "        $order : Order( number == 12 )\n" +
                "        $o : OrderItem( seq == 1, order.number==12, this.seq memberOf $order.itemsKeys )\n" +
                "    then\n" +
                "        results.add( $o );\n" +
                "end\n" +
                "\n" +
                "rule \"eval rewrite with 'not memberOf'\"\n" +
                "    salience 50\n" +
                "    when\n" +
                "        $order : Order( number == 11 )\n" +
                "        $o : OrderItem( seq == 3, order.number==12, this.seq not memberOf $order.itemsKeys )\n" +
                "    then\n" +
                "        results.add( $o );\n" +
                "end\n" +
                "\n" +
                "rule \"eval rewrite with 'excludes'\"\n" +
                "    salience 30\n" +
                "    when\n" +
                "        $o : OrderItem( order.number == 13, seq == 1 )\n" +
                "        Order( number == 12, this.itemsValues excludes $o )\n" +
                "    then\n" +
                "        results.add( $o );\n" +
                "end\n" +
                "\n" +
                "rule \"eval rewrite with 'matches'\"\n" +
                "    salience 20\n" +
                "    when\n" +
                "        $o : Order( number == 14, this.customer matches \"Mark\" )\n" +
                "    then\n" +
                "        results.add( $o );\n" +
                "end\n" +
                "\n" +
                "rule \"eval rewrite with 'not matches'\"\n" +
                "    salience 10\n" +
                "    when\n" +
                "        $o : Order( number == 14, this.customer not matches \"Bob\" )\n" +
                "    then\n" +
                "        results.add( $o );\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("eval-rewrite-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
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

            assertThat(list.size()).isEqualTo(9);
            int index = 0;
            assertThat(list.get(index++)).isEqualTo(item11);
            assertThat(list.get(index++)).isEqualTo(item12);
            assertThat(list.get(index++)).isEqualTo(item21);
            assertThat(list.get(index++)).isEqualTo(item22);
            assertThat(list.get(index++)).isEqualTo(item31);
            assertThat(list.get(index++)).isEqualTo(item33);
            assertThat(list.get(index++)).isEqualTo(item41);
            assertThat(list.get(index++)).isEqualTo(order5);
            assertThat(list.get(index)).isEqualTo(order5);
        } finally {
            ksession.dispose();
        }
    }
}
