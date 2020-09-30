/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.metric;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.drools.mvel.compiler.Address;
import org.drools.mvel.compiler.Person;
import org.drools.metric.util.MetricLogUtils;
import org.drools.mvel.CommonTestMethodBase;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

public class MetricLogUtilsTest extends CommonTestMethodBase {

    @Before
    public void setup() {
        System.setProperty(MetricLogUtils.METRIC_LOGGER_ENABLED, "true");
        System.setProperty(MetricLogUtils.METRIC_LOGGER_THRESHOLD, "-1");
    }

    @Test
    public void testJoin() {

        String str =
                "import " + Address.class.getCanonicalName() + "\n" +
                     "import " + Person.class.getCanonicalName() + "\n" +
                     "rule R1\n" +
                     "when\n" +
                     "  $p1 : Person(age > 5)\n" +
                     "  $p2 : Person(age > $p1.age)\n" +
                     "then\n" +
                     "end\n" +
                     "rule R2\n" +
                     "when\n" +
                     "  $p1 : Person(age > 5)\n" +
                     "  $p2 : Person(age < $p1.age)\n" +
                     "then\n" +
                     "end\n";

        KieBase kbase = loadKnowledgeBaseFromString(str);

        List<Person> personList = IntStream.range(0, 10)
                                           .mapToObj(i -> new Person("John" + i, i))
                                           .collect(Collectors.toList());

        KieSession ksession = kbase.newKieSession();
        personList.stream().forEach(ksession::insert);

        int fired = ksession.fireAllRules();
        ksession.dispose();
        assertEquals(36, fired);
    }

    @Test
    public void testFrom() {

        String str =
                "import " + Address.class.getCanonicalName() + "\n" +
                     "import " + Person.class.getCanonicalName() + "\n" +
                     "rule R1\n" +
                     "when\n" +
                     "  $p : Person()\n" +
                     "  $a1 : Address() from $p.addresses\n" +
                     "  $a2 : Address(suburb != \"XYZ\", zipCode == $a1.zipCode, this != $a1) from $p.addresses\n" +
                     "then\n" +
                     "end\n";

        KieBase kbase = loadKnowledgeBaseFromString(str);

        List<Person> personList = IntStream.range(0, 10)
                                           .mapToObj(i -> new Person("John" + i, i))
                                           .map(p -> {
                                               p.addAddress(new Address("StreetX" + p.getAge(), "ABC", "111"));
                                               p.addAddress(new Address("StreetY" + p.getAge(), "ABC", "111"));
                                               p.addAddress(new Address("StreetZ" + p.getAge(), "ABC", "999"));
                                               return p;
                                           })
                                           .collect(Collectors.toList());

        KieSession ksession = kbase.newKieSession();
        personList.stream().forEach(ksession::insert);

        int fired = ksession.fireAllRules();
        ksession.dispose();
        assertEquals(20, fired);
    }

    @Test
    public void testNot() {

        String str =
                "import " + Address.class.getCanonicalName() + "\n" +
                     "import " + Person.class.getCanonicalName() + "\n" +
                     "rule R1\n" +
                     "when\n" +
                     "  $p1 : Person()\n" +
                     "  $p2 : Person(this != $p1)\n" +
                     "  not Person(this != $p1, this != $p2, (age == $p1.age || age == $p2.age))\n" +
                     "then\n" +
                     "end\n";

        KieBase kbase = loadKnowledgeBaseFromString(str);

        List<Person> personList = IntStream.range(0, 10)
                                           .mapToObj(i -> new Person("John" + i, i))
                                           .collect(Collectors.toList());

        KieSession ksession = kbase.newKieSession();
        personList.stream().forEach(ksession::insert);

        int fired = ksession.fireAllRules();
        ksession.dispose();
        assertEquals(90, fired);
    }

    @Test
    public void testExists() {

        String str =
                "import " + Address.class.getCanonicalName() + "\n" +
                     "import " + Person.class.getCanonicalName() + "\n" +
                     "rule R1\n" +
                     "when\n" +
                     "  $p1 : Person()\n" +
                     "  $p2 : Person(this != $p1)\n" +
                     "  exists Person(this != $p1, this != $p2, age != $p1.age, age != $p2.age)\n" +
                     "then\n" +
                     "end\n";

        KieBase kbase = loadKnowledgeBaseFromString(str);

        List<Person> personList = IntStream.range(0, 10)
                                           .mapToObj(i -> new Person("John" + i, i))
                                           .collect(Collectors.toList());

        KieSession ksession = kbase.newKieSession();
        personList.stream().forEach(ksession::insert);

        int fired = ksession.fireAllRules();
        ksession.dispose();
        assertEquals(90, fired);
    }

    @Test
    public void testAccumulate() {

        String str =
                "import " + Address.class.getCanonicalName() + "\n" +
                     "import " + Person.class.getCanonicalName() + "\n" +
                     "rule R1\n" +
                     "when\n" +
                     "  $p1 : Person()\n" +
                     "  accumulate ( $p2: Person ( getName().startsWith(\"J\"), this != $p1);\n" +
                     "                $average : average($p2.getAge());\n" +
                     "                $average > $p1.age, $average > 3\n" +
                     "             )\n" +
                     "then\n" +
                     //                     "  System.out.println(\"$p1.name = \" + $p1.getName() + \", other's $average = \" + $average);\n" +
                     "end\n";

        KieBase kbase = loadKnowledgeBaseFromString(str);

        List<Person> personList = IntStream.range(0, 10)
                                           .mapToObj(i -> new Person("John" + i, i))
                                           .collect(Collectors.toList());

        KieSession ksession = kbase.newKieSession();
        personList.stream().forEach(ksession::insert);

        int fired = ksession.fireAllRules();
        ksession.dispose();
        assertEquals(5, fired);
    }

    @Test
    public void testFromAccumulate() {

        String str =
                "import " + Address.class.getCanonicalName() + "\n" +
                     "import " + Person.class.getCanonicalName() + "\n" +
                     "rule R1\n" +
                     "when\n" +
                     "  $p1 : Person()\n" +
                     "  $average  : Double(this > $p1.age, this > 3) from accumulate ( $p2: Person ( getName().startsWith(\"J\"), this != $p1);\n" +
                     "                average($p2.getAge())\n" +
                     "             )\n" +
                     "then\n" +
                     //                     "  System.out.println(\"$p1.name = \" + $p1.getName() + \", other's $average = \" + $average);\n" +
                     "end\n";

        KieBase kbase = loadKnowledgeBaseFromString(str);

        List<Person> personList = IntStream.range(0, 10)
                                           .mapToObj(i -> new Person("John" + i, i))
                                           .collect(Collectors.toList());

        KieSession ksession = kbase.newKieSession();
        personList.stream().forEach(ksession::insert);

        int fired = ksession.fireAllRules();
        ksession.dispose();
        assertEquals(5, fired);
    }

    @Test
    public void testEval() {

        String str =
                "import " + Address.class.getCanonicalName() + "\n" +
                     "import " + Person.class.getCanonicalName() + "\n" +
                     "rule R1\n" +
                     "dialect \"mvel\"\n" +
                     "when\n" +
                     "  $p1 : Person()\n" +
                     "  eval($p1.age > 6)" +
                     "then\n" +
                     //                                          "  System.out.println(\"$p1.name = \" + $p1.getName());\n" +
                     "end\n";

        KieBase kbase = loadKnowledgeBaseFromString(str);

        List<Person> personList = IntStream.range(0, 10)
                                           .mapToObj(i -> new Person("John" + i, i))
                                           .collect(Collectors.toList());

        KieSession ksession = kbase.newKieSession();
        personList.stream().forEach(ksession::insert);

        int fired = ksession.fireAllRules();
        ksession.dispose();
        assertEquals(3, fired);
    }
}
