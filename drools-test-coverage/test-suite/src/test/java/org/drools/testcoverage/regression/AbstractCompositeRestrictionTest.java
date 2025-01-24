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

import java.util.List;
import java.util.stream.Stream;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Bugfix test for bz#724655 'NPE in AbstractCompositionRestriction when using
 * unbound variables'
 */
public class AbstractCompositeRestrictionTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseConfigurations().stream();
    }

    @DisabledIfSystemProperty(named = "drools.drl.antlr4.parser.enabled", matches = "true")
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void test(KieBaseTestConfiguration kieBaseTestConfiguration) {

        final KieBuilder builder = KieUtil.getKieBuilderFromResources(kieBaseTestConfiguration, false,
                KieServices.Factory.get().getResources().newClassPathResource("abstractCompositeRestrictionTest.drl", getClass()));

        final List<Message> msgs = builder.getResults().getMessages();

        final String[] lines = msgs.get(0).getText().split("\n");
        final String unable = "Unable to Analyse Expression valueType == Field.INT || valueType == Field.DOUBLE:";
        assertThat(lines[0]).isEqualTo(unable);
    }
}
