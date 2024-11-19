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

import java.util.stream.Stream;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * Tests generics in RHS with modify - BZ 1142886.
 */
public class GenericsWithModifyTest {

    private static final String DRL =
            "package defaultKBase;\n" +
            "import java.util.Map;\n" +
            "import java.util.HashMap;\n" +
            "rule R no-loop when\n" +
            " $s : String( )\n" +
            "then\n" +
            " Map<String,String> a = new HashMap<String,String>();\n" +
            " modify( $s ) { };\n" +
            "end\n";

    private KieSession kieSession;

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseConfigurations().stream();
    }

    @AfterEach
    public void dispose() {
        if (this.kieSession != null) {
            this.kieSession.dispose();
        }
    }

    /**
     * Tests generics on rule's RHS with modify.
     */
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testModifyWithGenericsOnRHS(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final KieBuilder kbuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, true, DRL);

        final KieContainer kieContainer = KieServices.Factory.get().newKieContainer(kbuilder.getKieModule().getReleaseId());
        this.kieSession = kieContainer.newKieSession();
        this.kieSession.insert("1");
        this.kieSession.fireAllRules();
    }
}
