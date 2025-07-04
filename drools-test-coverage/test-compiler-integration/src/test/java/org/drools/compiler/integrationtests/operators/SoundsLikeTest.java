/*
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
package org.drools.compiler.integrationtests.operators;

import java.util.stream.Stream;

import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class SoundsLikeTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testSoundsLike(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // JBRULES-2991: Operator soundslike is broken

        testFiredRules(kieBaseTestConfiguration, "package org.drools.compiler.integrationtests.operators;\n" +
                            "import " + Person.class.getCanonicalName() + ";\n" +
                               "rule SoundsLike\n" +
                               "when\n" +
                               "    Person( name soundslike \"Bob\" )\n" +
                               "then\n" +
                               "end",
                       1,
                       "Mark",
                       "Bob");
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testSoundsLikeNegativeCase(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // JBRULES-2991: Operator soundslike is broken

        testFiredRules(kieBaseTestConfiguration, "package org.drools.compiler.integrationtests.operators;\n" +
                               "import " + Person.class.getCanonicalName() + ";\n" +
                               "rule SoundsLike\n" +
                               "when\n" +
                               "    Person( name soundslike \"Bob\" )\n" +
                               "then\n" +
                               "end",
                       0,
                       "Mark");
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testNotSoundsLike(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // JBRULES-2991: Operator soundslike is broken

        testFiredRules(kieBaseTestConfiguration, "package org.drools.compiler.integrationtests.operators;\n" +
                               "import " + Person.class.getCanonicalName() + ";\n" +
                               "rule NotSoundsLike\n" +
                               "when\n" +
                               "    Person( name not soundslike \"Bob\" )\n" +
                               "then\n" +
                               "end",
                       1,
                       "John");
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testNotSoundsLikeNegativeCase(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // JBRULES-2991: Operator soundslike is broken

        testFiredRules(kieBaseTestConfiguration, "package org.drools.compiler.integrationtests.operators;\n" +
                               "import " + Person.class.getCanonicalName() + ";\n" +
                               "rule NotSoundsLike\n" +
                               "when\n" +
                               "    Person( name not soundslike \"Bob\" )\n" +
                               "then\n" +
                               "end",
                       0,
                       "Bob");
    }

    private void testFiredRules(KieBaseTestConfiguration kieBaseTestConfiguration, 
    							final String rule,
                                final int firedRulesCount,
                                final String... persons) {
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("sounds-like-test",
                                                                         kieBaseTestConfiguration,
                                                                         rule);
        final KieSession ksession = kbase.newKieSession();
        try {
            Stream.of(persons).forEach(person -> ksession.insert(new Person(person)));

            final int rules = ksession.fireAllRules();
            assertThat(rules).isEqualTo(firedRulesCount);
        } finally {
            ksession.dispose();
        }
    }
}
