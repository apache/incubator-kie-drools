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
package org.drools.mvel.compiler.oopath;

import java.util.Collection;
import java.util.stream.Stream;

import org.drools.mvel.compiler.oopath.model.Child;
import org.drools.mvel.compiler.oopath.model.Man;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class OOPathAccumulateTest {

	public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testAccumulateAverage(KieBaseTestConfiguration kieBaseTestConfiguration) {
        testAccumulate(kieBaseTestConfiguration, "average", 10);
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testAccumulateMin(KieBaseTestConfiguration kieBaseTestConfiguration) {
        testAccumulate(kieBaseTestConfiguration, "min", 8);
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testAccumulateMax(KieBaseTestConfiguration kieBaseTestConfiguration) {
        testAccumulate(kieBaseTestConfiguration, "max", 12);
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testAccumulateCount(KieBaseTestConfiguration kieBaseTestConfiguration) {
        testAccumulate(kieBaseTestConfiguration, "count", 2);
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testAccumulateSum(KieBaseTestConfiguration kieBaseTestConfiguration) {
        testAccumulate(kieBaseTestConfiguration, "sum", 20);
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testAccumulateCollectList(KieBaseTestConfiguration kieBaseTestConfiguration) {
        testAccumulateCollection(kieBaseTestConfiguration, "collectList", 12, 8);
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testAccumulateCollectSet(KieBaseTestConfiguration kieBaseTestConfiguration) {
        testAccumulateCollection(kieBaseTestConfiguration, "collectSet", 12, 8);
    }

    private void testAccumulate(KieBaseTestConfiguration kieBaseTestConfiguration, final String accumulateFunction, final Number expectedResult) {
        // DROOLS-1265
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                        "global java.lang.Object globalVar\n" +
                        "\n" +
                        "rule R when\n" +
                        "  accumulate ( Adult( $child: /children ) ; $accumulateResult: " + accumulateFunction + "($child.getAge()) )\n" +
                        "then\n" +
                        "  kcontext.getKieRuntime().setGlobal(\"globalVar\", $accumulateResult);\n" +
                        "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final Man bob = new Man( "Bob", 40 );
        bob.addChild( new Child( "Charles", 12 ) );
        bob.addChild( new Child( "Debbie", 8 ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        final Number result = (Number) ksession.getGlobal("globalVar");
        if (result instanceof Double) {
            assertThat(expectedResult.doubleValue()).isEqualTo(result.doubleValue());
        } else {
            assertThat(expectedResult.longValue()).isEqualTo(result.longValue());
        }
    }

    private void testAccumulateCollection(KieBaseTestConfiguration kieBaseTestConfiguration, String accumulateFunction, final Integer... expectedResults) {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                        "global java.util.Collection<Integer> globalVar\n" +
                        "\n" +
                        "rule R when\n" +
                        "  accumulate ( Adult( $child: /children ) ; $accumulateResult: " + accumulateFunction + "($child.getAge()) )\n" +
                        "then\n" +
                        "  kcontext.getKieRuntime().setGlobal(\"globalVar\", $accumulateResult);\n" +
                        "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final Man bob = new Man( "Bob", 40 );
        bob.addChild( new Child( "Charles", 12 ) );
        bob.addChild( new Child( "Debbie", 8 ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        final Collection<Integer> result = (Collection<Integer>) ksession.getGlobal("globalVar");
        assertThat(result).containsExactlyInAnyOrder(expectedResults);
    }
}
