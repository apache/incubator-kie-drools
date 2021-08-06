/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.integrationtests.concurrency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.test.testcategory.TurtleTestCategory;

@RunWith(Parameterized.class)
@Category(TurtleTestCategory.class)
public class ConsequenceWithAndOrConcurrencyTest extends BaseConcurrencyTest {

    public ConsequenceWithAndOrConcurrencyTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        super(kieBaseTestConfiguration);
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(false); // fails with exec-model. file JIRA
    }

    protected String getDrl() {
        return "package com.example.reproducer\n" +
               "import " + Bus.class.getCanonicalName() + ";\n" +
               "import static " + StaticUtils.class.getCanonicalName() + ".TOSTRING;\n" +
               "dialect \"mvel\"\n" +
               "global java.util.List result;\n" +
               "rule \"rule_mt_1a\"\n" +
               "    when\n" +
               "        $bus : Bus( $title: \"POWER PLANT\" )\n" +
               "    then\n" +
               "        result.add(TOSTRING($bus.karaoke.dvd[$title].artist) == \"BBB\" || TOSTRING($bus.karaoke.dvd[$title].artist) >= \"01\" && TOSTRING($bus.karaoke.dvd[$title].artist) <= \"39\");\n" +
               "end";
    }

    protected void setGlobal(KieSession kSession) {
        List<String> result = new ArrayList<>();
        kSession.setGlobal("result", result);
    }

    protected void preprocess(KieBase kieBase) {
        KieSession kSession1 = kieBase.newKieSession();
        List<Boolean> result = new ArrayList<>();
        kSession1.setGlobal("result", result);
        Bus bus1 = new Bus("red", 30);
        bus1.getKaraoke().getDvd().put("POWER PLANT", new Album("POWER PLANT", "BBB")); // match the 1st condition -> short circuit
        bus1.getKaraoke().getDvd().put("Somewhere Out In Space", new Album("Somewhere Out In Space", "GAMMA RAY"));
        kSession1.insert(bus1);
        kSession1.fireAllRules();
        kSession1.dispose();
    }

}
