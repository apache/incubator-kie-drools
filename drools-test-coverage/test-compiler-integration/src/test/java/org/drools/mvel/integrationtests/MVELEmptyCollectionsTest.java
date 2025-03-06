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

import org.drools.mvel.integrationtests.facts.FactWithList;
import org.drools.mvel.integrationtests.facts.FactWithMap;
import org.drools.mvel.integrationtests.facts.FactWithObject;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class MVELEmptyCollectionsTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        // Some of these fail without executable model, so test only executable model.
        return TestParametersUtil2.getKieBaseCloudOnlyExecModelConfiguration().stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testEmptyListAsMethodParameter(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl =
                "import " + FactWithList.class.getCanonicalName() + "; \n" +
                "rule \"test\"\n" +
                "dialect \"mvel\" \n" +
                "when\n" +
                "    $p: FactWithList()\n" +
                "then\n" +
                "    $p.setItems([]); \n" +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();
        final FactWithList f = new FactWithList("testString");
        ksession.insert(f);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(f.getItems()).hasSize(0);
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testEmptyListAsConstructorParameter(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl =
                "import " + FactWithList.class.getCanonicalName() + "; \n" +
                        "import " + FactWithObject.class.getCanonicalName() + "; \n" +
                        "rule \"test\"\n" +
                        "dialect \"mvel\" \n" +
                        "when\n" +
                        "    $p: FactWithObject()\n" +
                        "then\n" +
                        "    $p.setObjectValue(new FactWithList([])); \n" +
                        "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();
        final FactWithObject f = new FactWithObject(null);
        ksession.insert(f);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(f.getObjectValue()).isInstanceOf(FactWithList.class);
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testEmptyMapAsMethodParameter(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl =
                "import " + FactWithMap.class.getCanonicalName() + "; \n" +
                        "rule \"test\"\n" +
                        "dialect \"mvel\" \n" +
                        "when\n" +
                        "    $p: FactWithMap()\n" +
                        "then\n" +
                        "    $p.setItemsMap([]); \n" +
                        "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();
        final FactWithMap f = new FactWithMap(1, "testString");
        ksession.insert(f);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(f.getItemsMap()).hasSize(0);
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testEmptyMapAsConstructorParameter(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl =
                "import " + FactWithMap.class.getCanonicalName() + "; \n" +
                        "import " + FactWithObject.class.getCanonicalName() + "; \n" +
                        "rule \"test\"\n" +
                        "dialect \"mvel\" \n" +
                        "when\n" +
                        "    $p: FactWithObject()\n" +
                        "then\n" +
                        "    $p.setObjectValue(new FactWithMap([])); \n" +
                        "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();
        final FactWithObject f = new FactWithObject(null);
        ksession.insert(f);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(f.getObjectValue()).isNotNull().isInstanceOf(FactWithMap.class);
    }
}
