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
package org.drools.ancompiler;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.conf.AlphaRangeIndexThresholdOption;

import static org.assertj.core.api.Assertions.assertThat;

public class RangeIndexANCTest extends BaseModelTest {

    public RangeIndexANCTest(RUN_TYPE testRunType) {
        super(testRunType);
    }

    @Test
    public void testInteger() {
        final String drl =
                "package org.drools.compiler.test\n" +
                           "import " + Person.class.getCanonicalName() + "\n" +
                           "rule test1\n when\n" +
                           "   Person( age >= 18 )\n" +
                           "then\n end\n" +
                           "rule test2\n when\n" +
                           "   Person( age < 25 )\n" +
                           "then\n end\n" +
                           "rule test3\n when\n" +
                           "   Person( age > 8 )\n" +
                           "then\n end\n";

        final KieBase kbase = createKieBaseWithRangeIndexThresholdValue(drl, 3);
        final KieSession ksession = kbase.newKieSession();

        ksession.insert(new Person("John", 18));
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(3);

        ksession.insert(new Person("Paul", 60));
        fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(2);
    }

    private KieBase createKieBaseWithRangeIndexThresholdValue(String drl, int rangeIndexThresholdValue) {
        final KieContainer kieContainer = getKieContainer(drl);
        final KieBaseConfiguration kieBaseConfiguration = KieServices.get().newKieBaseConfiguration();
        kieBaseConfiguration.setOption(AlphaRangeIndexThresholdOption.get(rangeIndexThresholdValue)); // for test convenience. Default value is AlphaRangeIndexThresholdOption.DEFAULT_VALUE
        return kieContainer.newKieBase(kieBaseConfiguration);
    }

    @Test
    public void testMixedRangeHashAndOther() {
        final String drl = "package org.drools.compiler.test\n" +
                           "import " + Person.class.getCanonicalName() + "\n" +
                           "global java.util.List results;\n" +
                           "rule test1\n when\n" +
                           "   Person( age >= 18 )\n" +
                           "then\n" +
                           "   results.add(drools.getRule().getName());" +
                           "end\n" +
                           "rule test2\n when\n" +
                           "   Person( age < 25 )\n" +
                           "then\n" +
                           "   results.add(drools.getRule().getName());" +
                           "end\n" +
                           "rule test3\n when\n" +
                           "   Person( age > 8 )\n" +
                           "then\n" +
                           "   results.add(drools.getRule().getName());" +
                           "end\n" +
                           "rule test4\n when\n" +
                           "   Person( age == 60 )\n" +
                           "then\n" +
                           "   results.add(drools.getRule().getName());" +
                           "end\n" +
                           "rule test5\n when\n" +
                           "   Person( age == 12 )\n" +
                           "then\n" +
                           "   results.add(drools.getRule().getName());" +
                           "end\n" +
                           "rule test6\n when\n" +
                           "   Person( age == 4 )\n" +
                           "then\n" +
                           "   results.add(drools.getRule().getName());" +
                           "end\n" +
                           "rule test7\n when\n" +
                           "   Person( age != 18 )\n" +
                           "then\n" +
                           "   results.add(drools.getRule().getName());" +
                           "end\n";

        final KieBase kbase = createKieBaseWithRangeIndexThresholdValue(drl, 3);
        final KieSession ksession = kbase.newKieSession();

        List<String> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        ksession.insert(new Person("John", 18));
        ksession.fireAllRules();
        assertThat(results).containsOnly("test1", "test2", "test3");
        results.clear();

        ksession.insert(new Person("Paul", 60));
        ksession.fireAllRules();
        assertThat(results).containsOnly("test1", "test3", "test4", "test7");
    }

    @Test
    public void testChainRange() {
        final String drl = "package com.sample\n" +
                           "import " + Person.class.getCanonicalName() + "\n" +
                           "global java.util.List results;\n" +
                           "rule test1\n" +
                           "  when\n" +
                           "    $p : Person(age > 10, age < 20)\n" +
                           "  then\n" +
                           "   results.add(drools.getRule().getName());" +
                           "end\n" +
                           "rule test2\n" +
                           "  when\n" +
                           "    $p : Person(age > 10, age < 30)\n" +
                           "  then\n" +
                           "   results.add(drools.getRule().getName());" +
                           "end\n" +
                           "rule test3\n" +
                           "  when\n" +
                           "    $p : Person(age > 10, age < 40)\n" +
                           "  then\n" +
                           "   results.add(drools.getRule().getName());" +
                           "end\n" +
                           "rule test4\n" +
                           "  when\n" +
                           "    $p : Person(age > 20, age < 30)\n" +
                           "  then\n" +
                           "   results.add(drools.getRule().getName());" +
                           "end\n" +
                           "rule test5\n" +
                           "  when\n" +
                           "    $p : Person(age > 30, age < 40)\n" +
                           "  then\n" +
                           "   results.add(drools.getRule().getName());" +
                           "end";

        final KieBase kbase = createKieBaseWithRangeIndexThresholdValue(drl, 3);
        final KieSession ksession = kbase.newKieSession();

        List<String> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        ksession.insert(new Person("John", 35));
        ksession.fireAllRules();
        assertThat(results).containsOnly("test3", "test5");
        results.clear();

        ksession.insert(new Person("Paul", 20));
        ksession.fireAllRules();
        assertThat(results).containsOnly("test2", "test3");
        results.clear();
    }

    @Test
    public void testRangeWithBeta() {
        final String drl = "package com.sample\n" +
                           "import " + Person.class.getCanonicalName() + "\n" +
                           "global java.util.List results;\n" +
                           "rule test1\n" +
                           "  when\n" +
                           "    $p1 : Person(age > 10)\n" +
                           "  then\n" +
                           "   results.add(drools.getRule().getName());" +
                           "end\n" +
                           "rule test2\n" +
                           "  when\n" +
                           "    $p1 : Person(age > 10)\n" +
                           "    $p2 : Person(this != $p1, age > $p1.age)\n" +
                           "  then\n" +
                           "   results.add(drools.getRule().getName());" +
                           "end\n" +
                           "rule test3\n" +
                           "  when\n" +
                           "    $p1 : Person(age > 20)\n" +
                           "    $p2 : Person(this != $p1, age > $p1.age)\n" +
                           "  then\n" +
                           "   results.add(drools.getRule().getName());" +
                           "end\n" +
                           "rule test4\n" +
                           "  when\n" +
                           "    $p1 : Person(age > 30)\n" +
                           "    $p2 : Person(this != $p1, age > $p1.age)\n" +
                           "  then\n" +
                           "   results.add(drools.getRule().getName());" +
                           "end";

        final KieBase kbase = createKieBaseWithRangeIndexThresholdValue(drl, 3);
        final KieSession ksession = kbase.newKieSession();

        List<String> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        ksession.insert(new Person("John", 35));
        ksession.fireAllRules();
        assertThat(results).containsOnly("test1");
        results.clear();

        ksession.insert(new Person("Paul", 25));
        ksession.fireAllRules();
        assertThat(results).containsOnly("test1", "test2", "test3");
        results.clear();
    }
}
