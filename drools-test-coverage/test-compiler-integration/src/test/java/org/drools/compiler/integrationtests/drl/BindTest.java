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
package org.drools.compiler.integrationtests.drl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class BindTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public BindTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testFactBindings() {
        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                " \n" +
                "rule \"simple rule\"\n" +
                "    no-loop true\n" +
                "    when\n" +
                "        $person : Person( name == \"big cheese\", $cheese : cheese )\n" +
                "    then\n" +
                "        update( $cheese );\n" +
                "        update( $person );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("bind-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final RuleRuntimeEventListener wmel = mock(RuleRuntimeEventListener.class);
            ksession.addEventListener(wmel);

            final Person bigCheese = new Person("big cheese");
            final Cheese cheddar = new Cheese("cheddar", 15);
            bigCheese.setCheese(cheddar);

            final FactHandle bigCheeseHandle = ksession.insert(bigCheese);
            final FactHandle cheddarHandle = ksession.insert(cheddar);
            ksession.fireAllRules();

            final ArgumentCaptor<ObjectUpdatedEvent> arg = ArgumentCaptor.forClass(org.kie.api.event.rule.ObjectUpdatedEvent.class);
            verify(wmel, times(2)).objectUpdated(arg.capture());

            org.kie.api.event.rule.ObjectUpdatedEvent event = arg.getAllValues().get(0);
            assertThat(event.getFactHandle()).isSameAs(cheddarHandle);
            assertThat(cheddar).isSameAs(event.getOldObject());
            assertThat(cheddar).isSameAs(event.getObject());

            event = arg.getAllValues().get(1);
            assertThat(bigCheeseHandle).isSameAs(event.getFactHandle());
            assertThat(bigCheese).isSameAs(event.getOldObject());
            assertThat(bigCheese).isSameAs(event.getObject());
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testBindingToMissingField() {
        // JBRULES-3047
        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
            "rule rule1\n" +
            "when\n" +
            "    Integer( $i : noSuchField ) \n" +
            "    eval( $i > 0 )\n" +
            "then \n" +
            "end\n";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration,
                                                                    false,
                                                                    drl);
        assertThat(kieBuilder.getResults().getMessages()).isNotEmpty();
    }

    @Test
    public void testFieldBindingOnWrongFieldName() {
        //JBRULES-2527

        String drl =
            "package org.drools.compiler.integrationtests.drl;\n" +
            "import " + Person.class.getCanonicalName() + ";\n" +
            "global java.util.List mlist\n" +
            "rule rule1 \n" +
            "when\n" +
            "   Person( $f : invalidFieldName, eval( $f != null ) )\n" +
            "then\n" +
            "end\n";

        testBingWrongFieldName(drl);

        drl =
            "package org.drools.compiler.integrationtests.drl;\n" +
            "import " + Person.class.getCanonicalName() + ";\n" +
            "global java.util.List mlist\n" +
            "rule rule1 \n" +
            "when\n" +
            "   Person( $f : invalidFieldName, name == ( $f ) )\n" +
            "then\n" +
            "end\n";

        testBingWrongFieldName(drl);
    }

    private void testBingWrongFieldName(final String drl) {
        try {
            final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration,
                                                                        false,
                                                                        drl);
            assertThat(kieBuilder.getResults().getMessages()).isNotEmpty();
        } catch (final Exception e) {
            e.printStackTrace();
            fail("Exception should not be thrown ");
        }
    }

    @Test
    public void testBindingsOnConnectiveExpressions() {

        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "global java.util.List results;\n" +
                "rule \"bindings\"\n" +
                "when\n" +
                "    Cheese( $p : price, $t : type, type == \"stilton\" || price == 10 )\n" +
                "then\n" +
                "    results.add( $t );\n" +
                "    results.add( new Integer( $p ) );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("bind-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List results = new ArrayList();
            ksession.setGlobal("results", results);

            ksession.insert(new Cheese("stilton", 15));

            ksession.fireAllRules();

            assertThat(results.size()).isEqualTo(2);
            assertThat(results.get(0)).isEqualTo("stilton");
            assertThat(results.get(1)).isEqualTo(15);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testAutomaticBindings() {

        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List results;\n" +
                "rule \"test auto bindings 1\"\n" +
                "when\n" +
                "    $p : Person();\n" +
                "    $c : Cheese( type == $p.likes, price == $c.price  )\n" +
                "then\n" +
                "    results.add( $p );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("bind-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("results", list);

            final Person bob = new Person("bob");
            bob.setLikes("stilton");
            final Cheese stilton = new Cheese("stilton", 12);
            ksession.insert(bob);
            ksession.insert(stilton);

            ksession.fireAllRules();
            assertThat(list.size()).isEqualTo(1);

            assertThat(list.get(0)).isEqualTo(bob);
        } finally {
            ksession.dispose();
        }
    }
}
