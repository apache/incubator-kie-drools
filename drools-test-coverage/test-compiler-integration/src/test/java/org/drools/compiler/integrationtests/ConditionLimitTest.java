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
package org.drools.compiler.integrationtests;

import java.util.Collection;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class ConditionLimitTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public ConditionLimitTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseStreamConfigurations(true);
    }

    public static class FieldObject {
        private String key;
        private String value;

        public FieldObject(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }
        public void setKey(String key) {
            this.key = key;
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
    }

    @Test
    public void testEvalErrorHandling() {
        // DROOLS-6856
        final int INPUT_COUNT = 65;

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("ConditionLimitTest", kieBaseTestConfiguration, generateRulesFile(INPUT_COUNT));
        final KieSession kSession = kbase.newKieSession();

        /*
         * Construct all objects needed for the test to run.
         */
        FieldObject[] inputs = new FieldObject[INPUT_COUNT];
        FactHandle[] fhs = new FactHandle[INPUT_COUNT];
        for (int i = 0; i < INPUT_COUNT; i++) {
            inputs[i] = new FieldObject("o" + i, "RUN");
            fhs[i] = kSession.insert(inputs[i]);
        }
        FieldObject result = new FieldObject("result", "");
        kSession.insert(result);

        assertThat(result.getValue()).isEqualTo("");		//make sure result is set right

        kSession.fireAllRules();
        assertThat(result.getValue()).isEqualTo("The rule has run.");		//rule has fired


        result.setValue("XXXXXX");
        /*
         * A rerun of the rules will not change the result since nothing triggers the RETE graph to detect a change.
         */
        kSession.fireAllRules();
        result.setValue("XXXXXX");

        /*
         * Loop through all inputs and mark them as updated.  This should trigger the rule to rerun.
         */
        for (int i = 0; i < INPUT_COUNT; i++) {
            kSession.update(fhs[i], inputs[i]);
            /*
             * The below will work on all rule executions except 65.  There is no error during compilation
             * and the input value is ignored.
             */
            kSession.fireAllRules();
            assertThat(result.getValue()).isEqualTo("The rule has run.");
            result.setValue("XXXXXX");
        }
    }

    private String generateRulesFile(int inputCount) {
        StringBuilder sb = new StringBuilder();
        sb.append(""
                + "package rules\n"
                + "import " + FieldObject.class.getCanonicalName() + "\n"
                + "\n"
                + "rule \"R1\"\n"
                + "	when\n");
        for (int i = 0; i < inputCount; i++) {
            sb.append("		o" + i + ": FieldObject(key == \"o" + i + "\")\n");
        }
        sb.append(""
                + "\n"
                + "		result: FieldObject(key == \"result\")\n"
                + "	then\n"
                + "		modify(result) {setValue(\"The rule has run.\")};\n"
                + "	end;");
        return sb.toString();
    }
}