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

import org.drools.core.base.UndefinedCalendarExcption;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(Parameterized.class)
public class TimerAndCalendarExceptionTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public TimerAndCalendarExceptionTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseStreamConfigurations(true);
    }

    @Test(timeout = 10000)
    public void testUnknownProtocol() {
        wrongTimerExpression("xyz:30");
    }

    @Test(timeout = 10000)
    public void testMissingColon() {
        wrongTimerExpression("int 30");
    }

    @Test(timeout = 10000)
    public void testMalformedExpression() {
        wrongTimerExpression("30s s30");
    }

    @Test(timeout = 10000)
    public void testMalformedIntExpression() {
        wrongTimerExpression("int 30s");
    }

    @Test(timeout = 10000)
    public void testMalformedCronExpression() {
        wrongTimerExpression("cron: 0/30 * * * * *");
    }

    private void wrongTimerExpression(final String timer) {
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

    @Test
    public void testUndefinedCalendar() {
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
