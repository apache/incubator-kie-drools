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
package org.drools.mvel.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.mvel.CommonTestMethodBase;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.runtime.KieSession;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class NestedAccessorsTest extends CommonTestMethodBase {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public NestedAccessorsTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testNestedAccessor() throws Exception {
        final String str = "import org.drools.mvel.compiler.*;\n" +
                "rule R1 when\n" +
                "   Person( name == \"mark\", cheese.(type == \"gorgonzola\", price == 10) )\n" +
                "then\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        final KieSession ksession = kbase.newKieSession();

        final Person mark1 = new Person("mark");
        mark1.setCheese(new Cheese("gorgonzola", 10));
        ksession.insert(mark1);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        ksession.dispose();
    }

    @Test
    public void testNestedAccessorWithBinding() throws Exception {
        final String str = "import org.drools.mvel.compiler.*;\n" +
                "global StringBuilder sb\n" +
                "rule R1 when\n" +
                "   Person( name == \"mark\", cheese.(price == 10, $type : type) )\n" +
                "then\n" +
                "   sb.append( $type );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        final KieSession ksession = kbase.newKieSession();

        final StringBuilder sb = new StringBuilder();
        ksession.setGlobal("sb", sb);

        final Person mark1 = new Person("mark");
        mark1.setCheese(new Cheese("gorgonzola", 10));
        ksession.insert(mark1);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(sb.toString()).isEqualTo("gorgonzola");
        ksession.dispose();
    }

    @Test
    public void testDoubleNestedAccessor() throws Exception {
        final String str = "import org.drools.mvel.compiler.*;\n" +
                "rule R1 when\n" +
                "   Person( name == \"mark\", cheese.(price == 10, type.(length == 10) ) )\n" +
                "then\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        final KieSession ksession = kbase.newKieSession();

        final Person mark1 = new Person("mark");
        mark1.setCheese(new Cheese("gorgonzola", 10));
        ksession.insert(mark1);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        ksession.dispose();
    }

    @Test
    public void testNestedAccessorWithInlineCast() throws Exception {
        final String str = "import org.drools.mvel.compiler.*;\n" +
                "rule R1 when\n" +
                "   Person( name == \"mark\", address#LongAddress.(country == \"uk\", suburb == \"suburb\") )\n" +
                "then\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        final KieSession ksession = kbase.newKieSession();

        final Person mark1 = new Person("mark");
        mark1.setAddress(new LongAddress("street", "suburb", "zipCode", "uk"));
        ksession.insert(mark1);

        final Person mark2 = new Person("mark");
        ksession.insert(mark2);

        final Person mark3 = new Person("mark");
        mark3.setAddress(new Address("street", "suburb", "zipCode"));
        ksession.insert(mark3);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        ksession.dispose();
    }

    @Test
    public void testNestedAccessors() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "test_NestedAccessors.drl");
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

        assertThat(list.size()).isEqualTo(0);

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

        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isSameAs(item21);
        assertThat(list.get(1)).isSameAs(item22);
    }

    @Test
    public void testNestedAccessors2() throws Exception {
        final String rule = "package org.drools.mvel.compiler\n" +
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

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, rule);
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
        assertThat(values.get(0).getMatch().getObjects().get(0)).isEqualTo(c1);
        assertThat(values.get(1).getMatch().getObjects().get(0)).isEqualTo(c2);

        ksession.dispose();
    }
}
