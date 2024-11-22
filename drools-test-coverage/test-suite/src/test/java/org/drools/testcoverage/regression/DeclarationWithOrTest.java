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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.builder.KieBuilder;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests handling a variable binding in LHS with OR (BZ 1136424).
 */
public class DeclarationWithOrTest {

    private static final String FACT = "working";

    private static final String DRL =
        "global java.util.List list\n" +
        "\n" +
        "rule R\n" +
        "when\n" +
        " s: String( s.toString() == \"x\" || s.toString() == \"y\" )\n" +
        "then\n" +
        " list.add(\"" + FACT + "\");\n" +
        "end";

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseConfigurations().stream();
    }

    /**
     * Verifies that the rule with binding and OR in LHS compiles and works as expected.
     */
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testBindingWithOrInLHS(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final KieBuilder kbuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, true, DRL);
        final KieSession ksession = KieBaseUtil.getDefaultKieBaseFromKieBuilder(kbuilder).newKieSession();

        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        try {
            ksession.insert("y");
            ksession.fireAllRules();
        } finally {
            ksession.dispose();
        }

        assertThat(list).as("Unexpected element in result global").containsExactly(FACT);
    }

}