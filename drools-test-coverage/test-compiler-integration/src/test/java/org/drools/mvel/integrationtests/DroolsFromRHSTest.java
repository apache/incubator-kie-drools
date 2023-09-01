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
package org.drools.mvel.integrationtests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class DroolsFromRHSTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public DroolsFromRHSTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testHalt() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "test_halt.drl");
        KieSession ksession = kbase.newKieSession();

        final List results = new ArrayList();
        ksession.setGlobal( "results",
                results );

        ksession.insert(0);
        ksession.fireAllRules();

        assertThat(results.size()).isEqualTo(10);
        for ( int i = 0; i < 10; i++ ) {
            assertThat(results.get(i)).isEqualTo(i);
        }
    }

    @Test
    public void testFireLimit() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "test_fireLimit.drl");
        KieSession ksession = kbase.newKieSession();

        final List results = new ArrayList();
        ksession.setGlobal("results", results);

        ksession.insert(0);
        int count = ksession.fireAllRules();
        assertThat(count).isEqualTo(21);

        assertThat(results.size()).isEqualTo(20);
        for (int i = 0; i < 20; i++) {
            assertThat(results.get(i)).isEqualTo(i);
        }
        results.clear();

        ksession.insert(0);
        count = ksession.fireAllRules(10);
        assertThat(count).isEqualTo(10);

        assertThat(results.size()).isEqualTo(10);
        for (int i = 0; i < 10; i++) {
            assertThat(results.get(i)).isEqualTo(i);
        }

        count = ksession.fireAllRules(); //should finish the rest
        assertThat(count).isEqualTo(11);
        assertThat(results.size()).isEqualTo(20);
        for (int i = 0; i < 20; i++) {
            assertThat(results.get(i)).isEqualTo(i);
        }
        results.clear();

        ksession.insert(0);
        count = ksession.fireAllRules();

        assertThat(count).isEqualTo(21);

        assertThat(results.size()).isEqualTo(20);
        for (int i = 0; i < 20; i++) {
            assertThat(results.get(i)).isEqualTo(i);
        }
        results.clear();
    }
}
