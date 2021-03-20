/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.integrationtests;

import java.util.Collection;

import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class AccumulateConsistencyTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public AccumulateConsistencyTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testMinNoMatch() {
        final String drl =
                "package org.drools.compiler.integrationtests;\n" +
                           "import " + Person.class.getCanonicalName() + ";\n" +
                           "rule R\n" +
                           "when\n" +
                           "    accumulate( $p : Person( name == \"John\" ),\n" +
                           "                $min : min( $p.getAge() ) )\n" +
                           "then\n" +
                           "    System.out.println($min);\n" +
                           "end";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration, drl);
        final KieSession kieSession = kieBase.newKieSession();

        try {
            kieSession.insert(new Person("Paul", 20));
            assertEquals(1, kieSession.fireAllRules());
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testMaxNoMatch() {
        final String drl =
                "package org.drools.compiler.integrationtests;\n" +
                           "import " + Person.class.getCanonicalName() + ";\n" +
                           "rule R\n" +
                           "when\n" +
                           "    accumulate( $p : Person( name == \"John\" ),\n" +
                           "                $max : max( $p.getAge() ) )\n" +
                           "then\n" +
                           "    System.out.println($max);\n" +
                           "end";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration, drl);
        final KieSession kieSession = kieBase.newKieSession();

        try {
            kieSession.insert(new Person("Paul", 20));
            assertEquals(1, kieSession.fireAllRules());
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testAveNoMatch() {
        final String drl =
                "package org.drools.compiler.integrationtests;\n" +
                           "import " + Person.class.getCanonicalName() + ";\n" +
                           "rule R\n" +
                           "when\n" +
                           "    accumulate( $p : Person( name == \"John\" ),\n" +
                           "                $ave : average( $p.getAge() ) )\n" +
                           "then\n" +
                           "    System.out.println($ave);\n" +
                           "end";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration, drl);
        final KieSession kieSession = kieBase.newKieSession();

        try {
            kieSession.insert(new Person("Paul", 20));
            assertEquals(1, kieSession.fireAllRules());
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testSumNoMatch() {
        final String drl =
                "package org.drools.compiler.integrationtests;\n" +
                           "import " + Person.class.getCanonicalName() + ";\n" +
                           "rule R\n" +
                           "when\n" +
                           "    accumulate( $p : Person( name == \"John\" ),\n" +
                           "                $sum : sum( $p.getAge() ) )\n" +
                           "then\n" +
                           "    System.out.println($sum);\n" +
                           "end";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration, drl);
        final KieSession kieSession = kieBase.newKieSession();

        try {
            kieSession.insert(new Person("Paul", 20));
            assertEquals(1, kieSession.fireAllRules());
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testCountNoMatch() {
        final String drl =
                "package org.drools.compiler.integrationtests;\n" +
                           "import " + Person.class.getCanonicalName() + ";\n" +
                           "rule R\n" +
                           "when\n" +
                           "    accumulate( $p : Person( name == \"John\" ),\n" +
                           "                $count : count( $p.getAge() ) )\n" +
                           "then\n" +
                           "    System.out.println($count);\n" +
                           "end";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration, drl);
        final KieSession kieSession = kieBase.newKieSession();

        try {
            kieSession.insert(new Person("Paul", 20));
            assertEquals(1, kieSession.fireAllRules());
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testMinMaxNoMatch() {
        final String drl =
                "package org.drools.compiler.integrationtests;\n" +
                           "import " + Person.class.getCanonicalName() + ";\n" +
                           "rule R\n" +
                           "when\n" +
                           "    accumulate( $p : Person( name == \"John\" ),\n" +
                           "                $min : min( $p.getAge() ),\n" +
                           "                $max : max( $p.getAge() ))\n" +
                           "then\n" +
                           "    System.out.println($min + \", \" + $max);\n" +
                           "end";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration, drl);
        final KieSession kieSession = kieBase.newKieSession();

        try {
            kieSession.insert(new Person("Paul", 20));
            assertEquals(1, kieSession.fireAllRules());
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testMinNoMatchAccFrom() {
        final String drl =
                "package org.drools.compiler.integrationtests;\n" +
                           "import " + Person.class.getCanonicalName() + ";\n" +
                           "rule R\n" +
                           "when\n" +
                           "    $min : Number() from accumulate( $p : Person( name == \"John\" ),\n" +
                           "                min( $p.getAge() ) )\n" +
                           "then\n" +
                           "    System.out.println($min);\n" +
                           "end";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration, drl);
        final KieSession kieSession = kieBase.newKieSession();

        try {
            kieSession.insert(new Person("Paul", 20));
            assertEquals(0, kieSession.fireAllRules()); // not fired
        } finally {
            kieSession.dispose();
        }
    }
}
