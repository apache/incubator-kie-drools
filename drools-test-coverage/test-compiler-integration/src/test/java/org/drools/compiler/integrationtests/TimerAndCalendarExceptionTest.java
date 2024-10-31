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

import java.util.stream.Stream;

import org.drools.core.base.UndefinedCalendarExcption;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class TimerAndCalendarExceptionTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseStreamConfigurations(true).stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
	@Timeout(10000)
    public void testUnknownProtocol(KieBaseTestConfiguration kieBaseTestConfiguration) {
        wrongTimerExpression(kieBaseTestConfiguration, "xyz:30");
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
	@Timeout(10000)
    public void testMissingColon(KieBaseTestConfiguration kieBaseTestConfiguration) {
        wrongTimerExpression(kieBaseTestConfiguration, "int 30");
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
	@Timeout(10000)
    public void testMalformedExpression(KieBaseTestConfiguration kieBaseTestConfiguration) {
        wrongTimerExpression(kieBaseTestConfiguration, "30s s30");
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
	@Timeout(10000)
    public void testMalformedIntExpression(KieBaseTestConfiguration kieBaseTestConfiguration) {
        wrongTimerExpression(kieBaseTestConfiguration, "int 30s");
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
	@Timeout(10000)
    public void testMalformedCronExpression(KieBaseTestConfiguration kieBaseTestConfiguration) {
        wrongTimerExpression(kieBaseTestConfiguration, "cron: 0/30 * * * * *");
    }

    private void wrongTimerExpression(KieBaseTestConfiguration kieBaseTestConfiguration, final String timer) {
        final String drl =
                "package org.simple \n" +
                           "rule xxx \n" +
                           "  timer (" + timer + ") " +
                           "when \n" +
                           "then \n" +
                           "end  \n";

        final KieBuilder kieBuilder = KieUtil
            .getKieBuilderFromDrls(
                                   kieBaseTestConfiguration,
                                       false,
                                       drl);
        assertThat(kieBuilder.getResults().getMessages()).isNotEmpty();
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testUndefinedCalendar(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl =
                "rule xxx \n" +
                           "  calendars \"cal1\"\n" +
                           "when \n" +
                           "then \n" +
                           "end  \n";

        final KieBase kbase =
                KieBaseUtil.getKieBaseFromKieModuleFromDrl("timer-and-calendar-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            try {
                ksession.fireAllRules();
                fail("should throw UndefinedCalendarExcption");
            } catch (final UndefinedCalendarExcption ignored) {
            }
        } finally {
            ksession.dispose();
        }
    }

}
