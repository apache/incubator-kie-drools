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
package org.drools.testcoverage.regression;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for BZ 1150308.
 */
public class FixedPatternTest {

    private KieSession ksession;

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseConfigurations().stream();
    }

    @AfterEach
    public void cleanup() {
        if (this.ksession != null) {
            this.ksession.dispose();
        }
    }

    /**
     * Tests fixed pattern without constraint in Decision table (BZ 1150308).
     */
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testFixedPattern(KieBaseTestConfiguration kieBaseTestConfiguration) {

        final Resource resource = KieServices.Factory.get().getResources().newClassPathResource("fixedPattern.drl.xls", getClass());
        final KieBuilder kbuilder = KieUtil.getKieBuilderFromResources(kieBaseTestConfiguration, true, resource);

        final KieSession ksession = KieBaseUtil.getDefaultKieBaseFromKieBuilder(kbuilder).newKieSession();

        final List<Long> list = new ArrayList<Long>();
        ksession.setGlobal("list", list);

        ksession.insert(1L);
        ksession.insert(2);
        ksession.fireAllRules();

        assertThat(list).hasSize(1);
        assertThat(list).first().isEqualTo(1L);
    }
}
