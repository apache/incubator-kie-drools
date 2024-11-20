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
import java.util.List;
import java.util.stream.Stream;

import org.drools.mvel.compiler.Message;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class KnowledgeContextTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testKnowledgeContextJava(KieBaseTestConfiguration kieBaseTestConfiguration) {
        testKnowledgeContext(kieBaseTestConfiguration, "test_KnowledgeContextJava.drl");
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testKnowledgeContextMVEL(KieBaseTestConfiguration kieBaseTestConfiguration) {
        testKnowledgeContext(kieBaseTestConfiguration, "test_KnowledgeContextMVEL.drl");
    }

    private void testKnowledgeContext(KieBaseTestConfiguration kieBaseTestConfiguration, final String drlResourceName) {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, drlResourceName);
        KieSession ksession = kbase.newKieSession();
        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.insert(new Message());
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("Hello World");
    }
}
