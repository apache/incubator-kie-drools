/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvel.integrationtests.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.drools.mvel.compiler.Cheese;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class LocaleTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public LocaleTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testLatinLocale() throws Exception {
        final Locale defaultLoc = Locale.getDefault();

        try {
            // setting a locale that uses COMMA as decimal separator
            Locale.setDefault(new Locale("pt", "BR"));

            KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_LatinLocale.drl");
            KieSession ksession = kbase.newKieSession();

            final List<String> results = new ArrayList<String>();
            ksession.setGlobal("results", results);

            final Cheese mycheese = new Cheese("cheddar", 4);
            final FactHandle handle = ksession.insert(mycheese);
            ksession.fireAllRules();

            assertEquals(1, results.size());
            assertEquals("1", results.get(0));

            mycheese.setPrice(8);
            mycheese.setDoublePrice(8.50);

            ksession.update(handle, mycheese);
            ksession.fireAllRules();
            assertEquals(2, results.size());
            assertEquals("3", results.get(1));
        } finally {
            Locale.setDefault(defaultLoc);
        }
    }

}
