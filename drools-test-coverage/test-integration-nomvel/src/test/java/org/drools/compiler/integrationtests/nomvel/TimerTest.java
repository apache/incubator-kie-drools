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
package org.drools.compiler.integrationtests.nomvel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.core.ClockType;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.time.Calendar;
import org.kie.api.time.SessionClock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.compiler.integrationtests.nomvel.TestUtil.getKieSession;
import static org.drools.modelcompiler.util.EvaluationUtil.convertDate;

public class TimerTest {

    @Test
    public void testIntervalTimer() throws Exception {
        String drl =
            "global java.util.List list \n" +
            "rule xxx \n" +
            "  timer (int:30s 10s) " +
            "when \n" +
            "then \n" +
            "  list.add(\"fired\"); \n" +
            "end  \n";

        final KieSession ksession = getKieSession(getCepKieModuleModel(), drl);

        List list = new ArrayList();

        PseudoClockScheduler timeService = ( PseudoClockScheduler ) ksession.getSessionClock();
        timeService.advanceTime(new Date().getTime(), TimeUnit.MILLISECONDS);

        ksession.setGlobal("list", list);

        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(0);

        timeService.advanceTime(20, TimeUnit.SECONDS);
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(0);

        timeService.advanceTime(15, TimeUnit.SECONDS);
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);

        timeService.advanceTime(3, TimeUnit.SECONDS);
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);

        timeService.advanceTime(2, TimeUnit.SECONDS);
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(2);

        timeService.advanceTime(10, TimeUnit.SECONDS);
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(3);
    }

    @Test
    public void testTimerWithDeclaration() {
        final String drl =
                "import " + MyEvent.class.getCanonicalName() + "\n" +
                "import " + AtomicInteger.class.getCanonicalName() + "\n" +
                "declare MyEvent\n" +
                "    @role( event )\n" +
                "    @timestamp( timestamp )\n" +
                "    @expires( 10ms )\n" +
                "end\n" +
                "\n" +
                "rule R\n" +
                "    timer (int: 0 1; start=$startTime, repeat-limit=0 )\n" +
                "    when\n" +
                "       $event: MyEvent ($startTime : timestamp)\n" +
                "       $counter : AtomicInteger(get() > 0)\n" +
                "    then\n" +
                "        System.out.println(\"RG_TEST_TIMER WITH \" + $event + \" AND \" + $counter);\n" +
                "        modify($counter){\n" +
                "            decrementAndGet()\n" +
                "        }\n" +
                "end";

        KieSession ksession = getKieSession(getCepKieModuleModel(), drl);

        try {
            final long now = 1000;
            final PseudoClockScheduler sessionClock = ksession.getSessionClock();
            sessionClock.setStartupTime(now - 10);

            final AtomicInteger counter = new AtomicInteger(1);
            final MyEvent event1 = new MyEvent(now - 8);
            final MyEvent event2 = new MyEvent(now - 7);
            final MyEvent event3 = new MyEvent(now - 6);

            ksession.insert(counter);
            ksession.insert(event1);
            ksession.insert(event2);
            ksession.insert(event3);

            ksession.fireAllRules(); // Nothing Happens
            assertThat(counter.get()).isEqualTo(1);

            sessionClock.advanceTime(10, TimeUnit.MILLISECONDS);
            ksession.fireAllRules();

            assertThat(counter.get()).isEqualTo(0);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testExpressionTimer() {
        testExpressionTimer("timer (expr: $i, $i)");
    }

    @Test
    public void testExpressionTimerWithConstant() {
        testExpressionTimer("timer (expr: $i, $i; start=3-JAN-2010)");
    }

    private void testExpressionTimer(String timerExpr) {
        final String drl =
                "package org.simple \n" +
                "global java.util.List list \n" +
                "rule xxx \n" +
                "  " + timerExpr + " \n" +
                "when \n" +
                "   $i : Long() \n" +
                "then \n" +
                "  list.add(\"fired\"); \n" +
                "end  \n";

        KieSession ksession = getKieSession(getCepKieModuleModel(), drl);

        try {
            final List list = new ArrayList();

            final PseudoClockScheduler timeService = ksession.getSessionClock();
            timeService.advanceTime(new Date().getTime(), TimeUnit.MILLISECONDS);

            ksession.setGlobal("list", list);
            final FactHandle fh = ksession.insert(10000L);

            ksession.fireAllRules();
            assertThat(list.size()).isEqualTo(0);

            timeService.advanceTime(10, TimeUnit.SECONDS);
            ksession.fireAllRules();
            assertThat(list.size()).isEqualTo(1);

            timeService.advanceTime(17, TimeUnit.SECONDS);
            ksession.update(fh, 5000L);
            ksession.fireAllRules();
            assertThat(list.size()).isEqualTo(2);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testCromTimer() throws Exception {
        final Locale defaultLoc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK); // Because of the date strings in the DRL, fixable with JBRULES-3444
            final String str =
                    "package org.simple \n" +
                    "global java.util.List list \n" +
                    "rule xxx \n" +
                    "  date-effective \"02-Jan-2010\"\n" +
                    "  date-expires \"06-Jan-2010\"\n" +
                    "  calendars \"cal1\"\n" +
                    "  timer (cron: 0 0 0 * * ?) " +
                    "when \n" +
                    "then \n" +
                    "  list.add(\"fired\"); \n" +
                    "end  \n";

            KieSession ksession = getKieSession(getCepKieModuleModel(), str);
            try {
                final List list = new ArrayList();
                final PseudoClockScheduler timeService = ksession.getSessionClock();
                final Date date = convertDate("01-Jan-2010");

                final Calendar cal1 = timestamp -> true;

                final long oneDay = 60 * 60 * 24;
                ksession.getCalendars().set("cal1", cal1);
                ksession.setGlobal("list", list);

                timeService.advanceTime(date.getTime(), TimeUnit.MILLISECONDS);
                ksession.fireAllRules();
                assertThat(list.size()).isEqualTo(0);

                timeService.advanceTime(oneDay, TimeUnit.SECONDS);
                ksession.fireAllRules();
                assertThat(list.size()).isEqualTo(0);

                timeService.advanceTime(oneDay, TimeUnit.SECONDS); // day 3
                ksession.fireAllRules();
                assertThat(list.size()).isEqualTo(1);

                timeService.advanceTime(oneDay, TimeUnit.SECONDS);
                ksession.fireAllRules();
                assertThat(list.size()).isEqualTo(2);

                timeService.advanceTime(oneDay, TimeUnit.SECONDS);   // day 5
                ksession.fireAllRules();
                assertThat(list.size()).isEqualTo(3);

                timeService.advanceTime(oneDay, TimeUnit.SECONDS);
                ksession.fireAllRules();
                assertThat(list.size()).isEqualTo(3);
            } finally {
                ksession.dispose();
            }
        } finally {
            Locale.setDefault(defaultLoc);
        }
    }

    public static class MyEvent {
        private long timestamp;
        public MyEvent(final long timestamp ) { this.timestamp = timestamp; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(final long timestamp ) { this.timestamp = timestamp; }
        public String toString() { return "MyEvent{" + "timestamp=" + timestamp + '}';  }
    }

    public static KieModuleModel getCepKieModuleModel() {
        KieModuleModel kproj = KieServices.get().newKieModuleModel();
        kproj.newKieBaseModel( "kb" )
                .setDefault( true )
                .setEventProcessingMode( EventProcessingOption.STREAM )
                .newKieSessionModel( "ks" )
                .setDefault( true ).setClockType( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        return kproj;
    }
}
