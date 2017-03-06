/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.testcoverage.functional;

import java.util.ArrayList;
import java.util.Collection;

import org.assertj.core.api.Assertions;
import org.drools.testcoverage.common.model.SimplePerson;
import org.drools.testcoverage.common.util.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

/**
 * Test which takes a collection in working memory and calls iserLogical on each
 * its elements, than changes the collection in program and checks the correct
 * changes.
 */
@RunWith(Parameterized.class)
public class LogicalInsertFromCollectionTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public LogicalInsertFromCollectionTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseConfigurations();
    }

    @Test
    public void testRemoveElement() {
        final KieSession ksession = getKieBaseForTest().newKieSession();
        final Collection<Integer> collection = new ArrayList<Integer>();

        for (int i = 0; i < 4; i++) {
            collection.add(i);
        }

        final FactHandle handle = ksession.insert(collection);
        ksession.fireAllRules();

        for (int i = 5; i > 1; i--) {

            // before remove 5,4,3,2,1 facts
            Assertions.assertThat(ksession.getFactCount()).isEqualTo((long) i);

            collection.remove(collection.iterator().next());
            ksession.update(handle, collection);
            ksession.fireAllRules();
            // after removing 4,3,2,1,0 facts
            Assertions.assertThat(ksession.getFactCount()).isEqualTo((long) (i - 1));
        }

    }

    @Test
    public void testAddElement() {
        final KieSession ksession = getKieBaseForTest().newKieSession();

        final Collection<Integer> collection = new ArrayList<Integer>();

        for (int i = 0; i < 4; i++) {
            collection.add(i);
        }

        FactHandle handle = ksession.insert(collection);
        ksession.fireAllRules();

        // before adding 5 facts
        Assertions.assertThat(ksession.getFactCount()).isEqualTo((long) 5);

        collection.add(42);
        ksession.update(handle, collection);
        ksession.fireAllRules();

        // after adding should be 6 facts
        Assertions.assertThat(ksession.getFactCount()).isEqualTo((long) 6);
    }

    @Test
    public void testChangeElement() {
        final KieSession ksession = getKieBaseForTest().newKieSession();

        final Collection<SimplePerson> collection = new ArrayList<>();

        for (int i = 1; i < 5; i++) {
            collection.add(new SimplePerson("Person " + i, 10 * i));
        }

        final FactHandle handle = ksession.insert(collection);
        ksession.fireAllRules();

        // before change - expecting 5 facts
        Assertions.assertThat(ksession.getFactCount()).isEqualTo((long) 5);

        collection.iterator().next().setAge(80);
        ksession.update(handle, collection);
        ksession.fireAllRules();

        // after change - expecting 4 facts
        Assertions.assertThat(ksession.getFactCount()).isEqualTo((long) 4);

        collection.iterator().next().setAge(30);
        ksession.update(handle, collection);
        ksession.fireAllRules();

        Assertions.assertThat(ksession.getFactCount()).isEqualTo((long) 5);

    }

    private KieBase getKieBaseForTest() {
        final Resource drlResource =
                KieServices.Factory.get().getResources().newClassPathResource("logicalInsertFromCollectionTest.drl", getClass());
        return KieBaseUtil.getKieBaseAndBuildInstallModule(TestConstants.PACKAGE_REGRESSION,
                kieBaseTestConfiguration, drlResource);
    }
}
