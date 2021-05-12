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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.testcoverage.common.model.MyFact;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.AccumulateNullPropagationOption;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.Variable;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class AccumulateConsistencyTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;
    private final boolean accumulateNullPropagation;

    public AccumulateConsistencyTest(final KieBaseTestConfiguration kieBaseTestConfiguration, boolean accumulateNullPropagation) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
        this.accumulateNullPropagation = accumulateNullPropagation;
    }

    // accumulateNullPropagation is false by default in drools 7.x
    @Parameterized.Parameters(name = "KieBase type={0}, accumulateNullPropagation= {1}")
    public static Collection<Object[]> getParameters() {
        Collection<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY, false});
        parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_PATTERN, false});
        parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY, true});
        parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_PATTERN, true});
        return parameters;
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
        KieSessionConfiguration kieSessionConfiguration = KieServices.get().newKieSessionConfiguration();
        kieSessionConfiguration.setProperty(AccumulateNullPropagationOption.PROPERTY_NAME, Boolean.toString(accumulateNullPropagation));
        final KieSession kieSession = kieBase.newKieSession(kieSessionConfiguration, null);

        try {
            kieSession.insert(new Person("Paul", 20));
            if (accumulateNullPropagation) {
                assertEquals(1, kieSession.fireAllRules());
            } else {
                assertEquals(0, kieSession.fireAllRules()); // don't propagate null
            }
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
        KieSessionConfiguration kieSessionConfiguration = KieServices.get().newKieSessionConfiguration();
        kieSessionConfiguration.setProperty(AccumulateNullPropagationOption.PROPERTY_NAME, Boolean.toString(accumulateNullPropagation));
        final KieSession kieSession = kieBase.newKieSession(kieSessionConfiguration, null);

        try {
            kieSession.insert(new Person("Paul", 20));
            if (accumulateNullPropagation) {
                assertEquals(1, kieSession.fireAllRules());
            } else {
                assertEquals(0, kieSession.fireAllRules()); // don't propagate null
            }
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
        KieSessionConfiguration kieSessionConfiguration = KieServices.get().newKieSessionConfiguration();
        kieSessionConfiguration.setProperty(AccumulateNullPropagationOption.PROPERTY_NAME, Boolean.toString(accumulateNullPropagation));
        final KieSession kieSession = kieBase.newKieSession(kieSessionConfiguration, null);

        try {
            kieSession.insert(new Person("Paul", 20));
            if (accumulateNullPropagation) {
                assertEquals(1, kieSession.fireAllRules());
            } else {
                assertEquals(0, kieSession.fireAllRules()); // don't propagate null
            }
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
        KieSessionConfiguration kieSessionConfiguration = KieServices.get().newKieSessionConfiguration();
        kieSessionConfiguration.setProperty(AccumulateNullPropagationOption.PROPERTY_NAME, Boolean.toString(accumulateNullPropagation));
        final KieSession kieSession = kieBase.newKieSession(kieSessionConfiguration, null);

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
        KieSessionConfiguration kieSessionConfiguration = KieServices.get().newKieSessionConfiguration();
        kieSessionConfiguration.setProperty(AccumulateNullPropagationOption.PROPERTY_NAME, Boolean.toString(accumulateNullPropagation));
        final KieSession kieSession = kieBase.newKieSession(kieSessionConfiguration, null);

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
        KieSessionConfiguration kieSessionConfiguration = KieServices.get().newKieSessionConfiguration();
        kieSessionConfiguration.setProperty(AccumulateNullPropagationOption.PROPERTY_NAME, Boolean.toString(accumulateNullPropagation));
        final KieSession kieSession = kieBase.newKieSession(kieSessionConfiguration, null);

        try {
            kieSession.insert(new Person("Paul", 20));
            assertEquals(1, kieSession.fireAllRules());
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testMinMaxMatch() {
        final String drl =
                "package org.drools.compiler.integrationtests;\n" +
                           "import " + Person.class.getCanonicalName() + ";\n" +
                           "global java.util.Map result;\n" +
                           "rule R\n" +
                           "when\n" +
                           "    accumulate( $p : Person( name == \"John\" ),\n" +
                           "                $min : min( $p.getAge() ),\n" +
                           "                $max : max( $p.getAge() ))\n" +
                           "then\n" +
                           "    result.put(\"min\", $min);\n" +
                           "    result.put(\"max\", $max);\n" +
                           "    System.out.println($min + \", \" + $max);\n" +
                           "end";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration, drl);
        KieSessionConfiguration kieSessionConfiguration = KieServices.get().newKieSessionConfiguration();
        kieSessionConfiguration.setProperty(AccumulateNullPropagationOption.PROPERTY_NAME, Boolean.toString(accumulateNullPropagation));
        final KieSession kieSession = kieBase.newKieSession(kieSessionConfiguration, null);

        Map<String, Integer> result = new HashMap<>();
        kieSession.setGlobal("result", result);

        try {
            kieSession.insert(new Person(0, "John", 20));
            kieSession.insert(new Person(1, "John", 60));

            assertEquals(1, kieSession.fireAllRules());
            assertEquals(20, result.get("min").intValue());
            assertEquals(60, result.get("max").intValue());
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
        KieSessionConfiguration kieSessionConfiguration = KieServices.get().newKieSessionConfiguration();
        kieSessionConfiguration.setProperty(AccumulateNullPropagationOption.PROPERTY_NAME, Boolean.toString(accumulateNullPropagation));
        final KieSession kieSession = kieBase.newKieSession(kieSessionConfiguration, null);

        try {
            kieSession.insert(new Person("Paul", 20));
            if (accumulateNullPropagation) {
                assertEquals(1, kieSession.fireAllRules());
            } else {
                assertEquals(0, kieSession.fireAllRules()); // don't propagate null
            }
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testMinMatchUnification() {

        final String drl =
                "package org.drools.compiler.integrationtests;\n" +
                           "import " + Person.class.getCanonicalName() + ";\n" +
                           "import " + MyFact.class.getCanonicalName() + ";\n" +
                           "rule R\n" +
                           "when\n" +
                           "    MyFact($i : currentValue)\n" +
                           "    accumulate( $p : Person( name == \"John\" ),\n" +
                           "                $i := min( $p.getAge() ) )\n" +
                           "then\n" +
                           "    System.out.println($i);\n" +
                           "end";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration, drl);
        KieSessionConfiguration kieSessionConfiguration = KieServices.get().newKieSessionConfiguration();
        kieSessionConfiguration.setProperty(AccumulateNullPropagationOption.PROPERTY_NAME, Boolean.toString(accumulateNullPropagation));
        final KieSession kieSession = kieBase.newKieSession(kieSessionConfiguration, null);

        try {
            kieSession.insert(new Person("John", 20));
            kieSession.insert(new MyFact("A", 20));
            assertEquals(1, kieSession.fireAllRules());
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testMinNoMatchUnification() {
        final String drl =
                "package org.drools.compiler.integrationtests;\n" +
                           "import " + Person.class.getCanonicalName() + ";\n" +
                           "import " + MyFact.class.getCanonicalName() + ";\n" +
                           "rule R\n" +
                           "when\n" +
                           "    MyFact($i : currentValue)\n" +
                           "    accumulate( $p : Person( name == \"John\" ),\n" +
                           "                $i := min( $p.getAge() ) )\n" +
                           "then\n" +
                           "    System.out.println($i);\n" +
                           "end";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration, drl);
        KieSessionConfiguration kieSessionConfiguration = KieServices.get().newKieSessionConfiguration();
        kieSessionConfiguration.setProperty(AccumulateNullPropagationOption.PROPERTY_NAME, Boolean.toString(accumulateNullPropagation));
        final KieSession kieSession = kieBase.newKieSession(kieSessionConfiguration, null);

        try {
            kieSession.insert(new Person("Paul", 20));
            kieSession.insert(new MyFact("A", null));
            if (accumulateNullPropagation) {
                assertEquals(1, kieSession.fireAllRules());
            } else {
                assertEquals(0, kieSession.fireAllRules()); // don't propagate null
            }
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testMinMatchUnificationQuery() {

        final String drl =
                "package org.drools.compiler.integrationtests;\n" +
                           "import " + Person.class.getCanonicalName() + ";\n" +
                           "import java.util.List;\n" +
                           "query getResults( String $name, List $persons )\n" +
                           "  accumulate(  \n" +
                           "    $p : Person( name == $name),\n" +
                           "    $persons := collectList( $p )\n" +
                           "  ) \n" +
                           "end\n";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration, drl);
        KieSessionConfiguration kieSessionConfiguration = KieServices.get().newKieSessionConfiguration();
        kieSessionConfiguration.setProperty(AccumulateNullPropagationOption.PROPERTY_NAME, Boolean.toString(accumulateNullPropagation));
        final KieSession kieSession = kieBase.newKieSession(kieSessionConfiguration, null);

        try {
            kieSession.insert(new Person(0, "John", 20));
            kieSession.insert(new Person(1, "John", 19));
            final QueryResults results = kieSession.getQueryResults("getResults", "John", Variable.v);
            List<Person> persons = (List<Person>)results.iterator().next().get("$persons");
            assertEquals(2, persons.size());
        } finally {
            kieSession.dispose();
        }
    }
}
