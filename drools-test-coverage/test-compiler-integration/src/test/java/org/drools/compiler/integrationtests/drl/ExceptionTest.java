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
package org.drools.compiler.integrationtests.drl;

import java.util.stream.Stream;

import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ExceptionTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    public static class TestException extends RuntimeException {
        public TestException(String message) {
            super(message);
        }
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testReturnValueException(KieBaseTestConfiguration kieBaseTestConfiguration) {

        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "import " + TestException.class.getCanonicalName() + ";\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "function String throwException( ) {\n" +
                "    throw new TestException( \"this should throw an exception\" );\n" +
                "}\n" +
                "\n" +
                "rule \"Throw ReturnValue Exception\"\n" +
                "    when\n" +
                "        Cheese( type == ( throwException( ) ) )\n" +
                "    then\n" +
                "\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("exception-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final Cheese brie = new Cheese("brie", 12);

            assertThatThrownBy(() -> {
                ksession.insert(brie);
                ksession.fireAllRules();
            }).hasRootCauseInstanceOf(TestException.class);
        } finally {
            ksession.dispose();
        }
    }

}
