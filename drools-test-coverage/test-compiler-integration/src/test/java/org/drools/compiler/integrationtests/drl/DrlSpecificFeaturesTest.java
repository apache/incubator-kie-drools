/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests.drl;

import java.util.Collection;

import org.assertj.core.api.Assertions;
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
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class DrlSpecificFeaturesTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public DrlSpecificFeaturesTest( final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    // following test depends on MVEL: http://jira.codehaus.org/browse/MVEL-212
    @Test
    public void testMVELConsequenceUsingFactConstructors() {
        final String drl =
                "package org.drools.compiler.integrationtests.drl;\n" +
                        "import " + Person.class.getCanonicalName() + ";\n" +
                        "global " + KieSession.class.getCanonicalName() + " ksession\n" +
                        "rule test dialect 'mvel'\n" +
                        "when\n" +
                        "    $person:Person( name == 'mark' )\n" +
                        "then\n" +
                        "    // below constructor for Person does not exist\n" +
                        "    Person p = new Person( 'bob', 30, 555 )\n" +
                        "    ksession.update(ksession.getFactHandle($person), new Person('bob', 30, 999, 453, 534, 534, 32))\n" +
                        "end\n";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration,
                false,
                drl);
        Assertions.assertThat(kieBuilder.getResults().getMessages()).isNotEmpty();
    }

    @Test
    public void testTypeUnsafe() throws Exception {
        final String drl = "import " + DrlSpecificFeaturesTest.class.getName() + ".*\n" +
                "declare\n" +
                "   Parent @typesafe(false)\n" +
                "end\n" +
                "rule R1\n" +
                "when\n" +
                "   $a : Parent( x == 1 )\n" +
                "then\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("declare-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            for (int i = 0; i < 20; i++) {
                ksession.insert(new ChildA(i % 10));
                ksession.insert(new ChildB(i % 10));
            }

            assertEquals(4, ksession.fireAllRules());

            // give time to async jitting to complete
            Thread.sleep(100);

            ksession.insert(new ChildA(1));
            ksession.insert(new ChildB(1));
            assertEquals(2, ksession.fireAllRules());
        } finally {
            ksession.dispose();
        }
    }

    public static class Parent {
    }

    public static class ChildA extends Parent {
        private final int x;

        public ChildA(final int x) {
            this.x = x;
        }

        public int getX() {
            return x;
        }
    }

    public static class ChildB extends Parent {
        private final int x;

        public ChildB(final int x) {
            this.x = x;
        }

        public int getX() {
            return x;
        }
    }
}
