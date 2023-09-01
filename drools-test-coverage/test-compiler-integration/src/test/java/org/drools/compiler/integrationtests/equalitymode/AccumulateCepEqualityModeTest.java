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
package org.drools.compiler.integrationtests.equalitymode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.compiler.integrationtests.AccumulateCepTest;
import org.drools.testcoverage.common.util.EngineTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class AccumulateCepEqualityModeTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public AccumulateCepEqualityModeTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseConfigurations(EngineTestConfiguration.EQUALITY_MODE,
                                                           EngineTestConfiguration.STREAM_MODE,
                                                           EngineTestConfiguration.ALPHA_NETWORK_COMPILER_FALSE,
                                                           EngineTestConfiguration.EXECUTABLE_MODEL_OFF,
                                                           EngineTestConfiguration.EXECUTABLE_MODEL_FLOW,
                                                           EngineTestConfiguration.EXECUTABLE_MODEL_PATTERN);
    }

    @Test
    public void testManySlidingWindows() {

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                         AccumulateCepTest.TEST_MANY_SLIDING_WINDOWS_DRL);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("list", list);

            // Intentionally new Integer() here, we need different instances, but some of them equal
            // - if using direct value or Integer.valueOf, we get JVM cached instances, so we will get the same one for
            // the same number
            ksession.insert(new Integer(20));
            ksession.fireAllRules();
            assertThat(list).isEqualTo(asList(1, 1));

            ksession.insert(new Integer(20));
            ksession.fireAllRules();
            assertThat(list).isEqualTo(asList(1, 1));

            ksession.insert(new Integer(20));
            ksession.fireAllRules();
            assertThat(list).isEqualTo(asList(1, 1));

            ksession.insert(new Integer(2));
            ksession.fireAllRules();
            assertThat(list).isEqualTo(asList(1, 2));

            ksession.insert(new Integer(2));
            ksession.fireAllRules();
            assertThat(list).isEqualTo(asList(1, 2));

            ksession.insert(new Integer(2));
            ksession.fireAllRules();
            assertThat(list).isEqualTo(asList(1, 2));
        } finally {
            ksession.dispose();
        }
    }
}
