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

import java.util.List;
import java.util.stream.Stream;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message;

import static org.assertj.core.api.Assertions.assertThat;

public class SwitchOverStringTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        // TODO: EM failed with some tests. File JIRAs
        return TestParametersUtil2.getKieBaseCloudConfigurations(false).stream();
    }

    private static final String FUNCTION_WITH_SWITCH_OVER_STRING = "function void theTest(String input) {\n" +
            "  switch(input) {\n" +
            "    case \"Hello World\" :" +
            "      System.out.println(\"yep\");\n" +
            "      break;\n" +
            "    default :\n" +
            "      System.out.println(\"uh\");\n" +
            "      break;\n" +
            "  }\n" +
            "}";

    @AfterEach
    public void cleanUp() {
        System.clearProperty("drools.dialect.java.compiler.lnglevel");
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testCompileSwitchOverStringWithLngLevel17(KieBaseTestConfiguration kieBaseTestConfiguration) {
        double javaVersion = Double.valueOf(System.getProperty("java.specification.version"));
        Assumptions.assumeTrue(javaVersion >= 1.7, "Test only makes sense on Java 7+.");
        System.setProperty("drools.dialect.java.compiler.lnglevel", "1.7");
        try {
            KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, FUNCTION_WITH_SWITCH_OVER_STRING);
            List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
            assertThat(errors.isEmpty()).as(errors.toString()).isTrue();
        } finally {
            System.clearProperty("drools.dialect.java.compiler.lnglevel");
        }
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testShouldFailToCompileSwitchOverStringWithLngLevel16(KieBaseTestConfiguration kieBaseTestConfiguration) {
        System.setProperty("drools.dialect.java.compiler.lnglevel", "1.6");
        try {
            KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, FUNCTION_WITH_SWITCH_OVER_STRING);
            List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
            assertThat(errors.isEmpty()).as("Should have an error").isFalse();
            
        } finally {
            System.clearProperty("drools.dialect.java.compiler.lnglevel");
        }
    }
}
