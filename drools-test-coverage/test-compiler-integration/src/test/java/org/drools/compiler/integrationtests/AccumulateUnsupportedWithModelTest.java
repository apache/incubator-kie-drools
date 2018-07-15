/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
import java.util.List;

import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class AccumulateUnsupportedWithModelTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public AccumulateUnsupportedWithModelTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    // See https://issues.jboss.org/browse/DROOLS-2733
    @Test(timeout = 10000)
    public void testMVELAccumulate() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources("accumulate-test", kieBaseTestConfiguration,
                                                                           "org/drools/compiler/integrationtests/test_AccumulateMVEL.drl");
        final KieSession wm = kbase.newKieSession();
        try {
            final List<?> results = new ArrayList<>();
            wm.setGlobal("results",
                         results);

            wm.insert(new Person("Bob",
                                 "stilton",
                                 20));
            wm.insert(new Person("Mark",
                                 "provolone"));
            wm.insert(new Cheese("stilton",
                                 10));
            wm.insert(new Cheese("brie",
                                 5));
            wm.insert(new Cheese("provolone",
                                 150));

            wm.fireAllRules();

            assertEquals(165, results.get(0));
            assertEquals(10, results.get(1));
            assertEquals(150, results.get(2));
            assertEquals(10, results.get(3));
            assertEquals(210, results.get(4));
        } finally {
            wm.dispose();
        }
    }

    // See https://issues.jboss.org/browse/DROOLS-2733
    @Test(timeout = 10000)
    public void testMVELAccumulate2WM() {

        final KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources("accumulate-test", kieBaseTestConfiguration,
                                                                           "org/drools/compiler/integrationtests/test_AccumulateMVEL.drl");
        final KieSession wm1 = kbase.newKieSession();
        try {
            final List<?> results1 = new ArrayList<>();

            wm1.setGlobal("results",
                          results1);

            final List<?> results2 = new ArrayList<>();
            final KieSession wm2 = kbase.newKieSession();
            try {
                wm2.setGlobal("results",
                              results2);

                wm1.insert(new Person("Bob",
                                      "stilton",
                                      20));
                wm1.insert(new Person("Mark",
                                      "provolone"));

                wm2.insert(new Person("Bob",
                                      "stilton",
                                      20));
                wm2.insert(new Person("Mark",
                                      "provolone"));

                wm1.insert(new Cheese("stilton",
                                      10));
                wm1.insert(new Cheese("brie",
                                      5));
                wm2.insert(new Cheese("stilton",
                                      10));
                wm1.insert(new Cheese("provolone",
                                      150));
                wm2.insert(new Cheese("brie",
                                      5));
                wm2.insert(new Cheese("provolone",
                                      150));
                wm1.fireAllRules();

                wm2.fireAllRules();
            } finally {
                wm2.dispose();
            }

            assertEquals(165, results1.get(0));
            assertEquals(10, results1.get(1));
            assertEquals(150, results1.get(2));
            assertEquals(10, results1.get(3));
            assertEquals(210, results1.get(4));

            assertEquals(165, results2.get(0));
            assertEquals(10, results2.get(1));
            assertEquals(150, results2.get(2));
            assertEquals(10, results2.get(3));
            assertEquals(210, results2.get(4));
        } finally {
            wm1.dispose();
        }
    }
}
