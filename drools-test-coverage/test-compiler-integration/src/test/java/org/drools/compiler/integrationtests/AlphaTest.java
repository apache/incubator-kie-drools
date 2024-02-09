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
package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.Cheesery;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.conf.ShareAlphaNodesOption;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class AlphaTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public AlphaTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testAlphaExpression() {
        final String drl = "package org.drools.compiler\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule \"alpha\"\n" +
                "when\n" +
                "    Person( 5 < 6 )\n" +
                "then\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("alpha-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.insert(new Person("mark", 50));
            final int rules = ksession.fireAllRules();
            assertThat(rules).isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testAlphaNodeSharing() {

        final String drl = "package org.drools.compiler\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List results;\n" +
                "\n" +
                "rule \"First\"\n" +
                "salience 10\n" +
                " when\n" +
                "  c: Person(age <= 10)\n" +
                " then\n" +
                "  results.add(\"1\");\n" +
                "end\n" +
                " \n" +
                "rule \"Second\"\n" +
                "salience 5\n" +
                " when\n" +
                "     c: Person(age <= 10)\n" +
                " then\n" +
                "  results.add(\"2\");\n" +
                "end ";

        kieBaseTestConfiguration.setAdditionalKieBaseOptions(ShareAlphaNodesOption.YES);
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("alpha-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List results = new ArrayList();
            ksession.setGlobal("results", results);

            final Person p1 = new Person("bob", 5);
            ksession.insert(p1);

            ksession.fireAllRules();

            assertThat(results.size()).isEqualTo(2);
            assertThat(results.get(0)).isEqualTo("1");
            assertThat(results.get(1)).isEqualTo("2");
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testAlphaCompositeConstraints() {

        final String drl = "package org.drools.compiler\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List results;\n" +
                "\n" +
                "rule \"test alpha composite constraints\"\n" +
                "when\n" +
                "    Person( eval( age == 25 ) || ( eval( name.equals( \"bob\" ) ) && eval( age == 30 ) ) )\n" +
                "then\n" +
                "    results.add( \"OK\" );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("alpha-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("results", list);

            final Person bob = new Person("bob", 30);

            ksession.insert(bob);
            ksession.fireAllRules();

            assertThat(list.size()).isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testAlphaHashingWithConstants() {
        // JBRULES-3658
        final String drl = "import " + Person.class.getName() + ";\n" +
                "rule R1 when\n" +
                "   $p : Person( age == 38 )\n" +
                "then end\n" +
                "rule R2 when\n" +
                "   $p : Person( age == 37+1 )\n" +
                "then end\n" +
                "rule R3 when\n" +
                "   $p : Person( age == 36+2 )\n" +
                "then end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("alpha-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.insert( new Person( "Mario", 38 ) );
            assertThat(ksession.fireAllRules()).isEqualTo(3);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testNPEOnMVELAlphaPredicates() {

        final String drl = "package org.drools.compiler\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List results;\n" +
                "\n" +
                "rule \"test NPE on mvel predicate\"\n" +
                "when\n" +
                "    $p : Person( cheese.type != null )\n" +
                "    $q : Cheese( ) from $p.cheese\n" +
                "then\n" +
                "    results.add( $q );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("alpha-test", kieBaseTestConfiguration, drl);
        final KieSession session = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            session.setGlobal("results", list);

            final Cheese cheese = new Cheese("stilton", 10);
            final Cheesery cheesery = new Cheesery();
            cheesery.addCheese(cheese);
            final Person bob = new Person("bob", "stilton");
            final Cheese cheese2 = new Cheese();
            bob.setCheese(cheese2);

            final FactHandle p = session.insert(bob);
            session.insert(cheesery);

            session.fireAllRules();

            assertThat(list.size()).as("should not have fired").isEqualTo(0);

            cheese2.setType("stilton");
            session.update(p, bob);
            session.fireAllRules();

            assertThat(list.size()).isEqualTo(1);
        } finally {
            session.dispose();
        }
    }
}
