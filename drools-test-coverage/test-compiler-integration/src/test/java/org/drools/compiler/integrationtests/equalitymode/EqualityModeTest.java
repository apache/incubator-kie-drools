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

import org.drools.testcoverage.common.util.EngineTestConfiguration;
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
public class EqualityModeTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public EqualityModeTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseConfigurations(EngineTestConfiguration.ALPHA_NETWORK_COMPILER_FALSE,
                                                           EngineTestConfiguration.EQUALITY_MODE,
                                                           EngineTestConfiguration.CLOUD_MODE,
                                                           EngineTestConfiguration.EXECUTABLE_MODEL_OFF,
                                                           EngineTestConfiguration.EXECUTABLE_MODEL_FLOW,
                                                           EngineTestConfiguration.EXECUTABLE_MODEL_PATTERN);
    }

    @Test
    public void testBasicFactEquality() {
        final String drl =
                "import " + FactWithEquals.class.getCanonicalName() + " \n"
                        + "rule R \n"
                        + "when \n"
                        + "    $a: FactWithEquals() \n"
                        + "then \n"
                        + "end \n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("equality-mode-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.insert(new FactWithEquals(10));
            ksession.insert(new FactWithEquals(10));
            assertThat(ksession.fireAllRules()).isEqualTo(1);

            ksession.insert(new FactWithEquals(10));
            ksession.insert(new FactWithEquals(11));
            ksession.insert(new FactWithEquals(12));
            assertThat(ksession.fireAllRules()).isEqualTo(2);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testAccumulate() {
        final String drl =
                "import " + FactWithEquals.class.getCanonicalName() + " \n"
                        + "global java.util.List result; \n"
                        + "rule R \n"
                        + "when \n"
                        + " accumulate( \n"
                        + "    $fact: FactWithEquals();\n"
                        + "    $factCount: count($fact))\n"
                        + "then \n"
                        + "    result.add($factCount); \n"
                        + "end \n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("equality-mode-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List<Long> resultList = new ArrayList<>();
            ksession.setGlobal("result", resultList);

            ksession.insert(new FactWithEquals(10));
            ksession.insert(new FactWithEquals(10));
            ksession.insert(new FactWithEquals(11));

            assertThat(ksession.fireAllRules()).isEqualTo(1);
            assertThat(resultList).hasSize(1);
            assertThat(resultList).containsExactly(2L);
        } finally {
            ksession.dispose();
        }
    }
}
