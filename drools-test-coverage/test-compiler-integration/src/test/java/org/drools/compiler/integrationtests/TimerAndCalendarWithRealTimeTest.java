/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.drools.core.common.DefaultAgenda;
import org.drools.testcoverage.common.model.Alarm;
import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.TimedRuleExecutionOption;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class TimerAndCalendarWithRealTimeTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;
    private KieSession ksession;
    private KieBase kbase;

    public TimerAndCalendarWithRealTimeTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseStreamConfigurations(true);
    }

    @After
    public void after() throws Exception {
        if (ksession != null) {
            ksession.dispose();
        }
    }

    @Test(timeout = 15000)
    public void testDuration() throws Exception {
        final String drl = "package org.drools.compiler.test;\n" +
                           "\n" +
                           "import " + Cheese.class.getCanonicalName() + ";\n" +
                           "import " + Person.class.getCanonicalName() + ";\n" +
                           "\n" +
                           "global java.util.List list;\n" +
                           "\n" +
                           "rule delayed\n" +
                           "    duration 100\n" +
                           "    when\n" +
                           "        cheese : Cheese( )\n" +
                           "    then\n" +
                           "        list.add( cheese );\n" +
                           "end ";

        final KieBase kbase =
                KieBaseUtil.getKieBaseFromKieModuleFromDrl("timer-and-calendar-test", kieBaseTestConfiguration, drl);
        ksession = kbase.newKieSession();
        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final Cheese brie = new Cheese("brie", 12);
        ksession.insert(brie);

        ksession.fireAllRules();
        // now check for update
        assertEquals(0, list.size());

        awaitUntilRulesThatFiredAre(1);
        // now check for update
        assertEquals(1, list.size());
    }

    @Test(timeout = 10000)
    public void testDurationWithNoLoop() throws Exception {
        final String drl = "package org.drools.compiler.test;\n" +
                           "\n" +
                           "import " + Cheese.class.getCanonicalName() + ";\n" +
                           "import " + Person.class.getCanonicalName() + ";\n" +
                           "\n" +
                           "global java.util.List list;\n" +
                           "\n" +
                           "rule delayed\n" +
                           "    timer 100\n" +
                           "    no-loop true\n" +
                           "    when\n" +
                           "        cheese : Cheese( )\n" +
                           "    then\n" +
                           "        list.add( cheese );\n" +
                           "end";

        final KieBase kbase =
                KieBaseUtil.getKieBaseFromKieModuleFromDrl("timer-and-calendar-test", kieBaseTestConfiguration, drl);
        ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final Cheese brie = new Cheese("brie", 12);
        ksession.insert(brie);
        ksession.fireAllRules();

        // now check for update
        assertEquals(0, list.size());

        awaitUntilRulesThatFiredAre(1);

        // now check for update
        assertEquals(1, list.size());
    }

    @Test(timeout = 10000)
    public void testFireRuleAfterDuration() throws Exception {
        final String drl = "package org.drools.compiler.test;\n" +
                           "import " + Cheese.class.getCanonicalName() + ";\n" +
                           "global java.util.List list;\n" +
                           "\n" +
                           "rule delayed\n" +
                           "    duration 100\n" +
                           "    when\n" +
                           "        cheese : Cheese( $type:type == \"brie\" )\n" +
                           "    then\n" +
                           "        list.add( cheese );\n" +
                           "        insert(new Cheese(\"stilton\", 42));\n" +
                           "        delete(cheese);\n" +
                           "end\n" +
                           "\n" +
                           "rule after_delayed\n" +
                           "    when\n" +
                           "        cheese : Cheese( $type:type == \"stilton\" )\n" +
                           "    then\n" +
                           "        list.add( cheese );\n" +
                           "end";

        final KieBase kbase =
                KieBaseUtil.getKieBaseFromKieModuleFromDrl("timer-and-calendar-test", kieBaseTestConfiguration, drl);
        ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final Cheese brie = new Cheese("brie", 12);
        ksession.insert(brie);
        ksession.fireAllRules();

        // now check for update
        assertEquals(0, list.size());

        awaitUntilRulesThatFiredAre(1);

        // now check for update
        assertEquals(2, list.size());
    }

    @Test(timeout = 10000)
    public void testTimerWithNot() throws Exception {

        final KieBase kbase = KieBaseUtil
            .getKieBaseFromClasspathResources(
                                              "timer-and-calendar-test",
                                                  kieBaseTestConfiguration,
                                                  "org/drools/compiler/integrationtests/test_Timer_With_Not.drl");
        ksession = kbase.newKieSession();

        awaitUntilRulesThatFiredAre(2);
        // now check that rule "wrap A" fired once, creating one B
        assertEquals(2, ksession.getFactCount());
    }

    @Test(timeout = 10000)
    public void testTimerRemoval() {
        final String drl = "package org.drools.compiler.test\n" +
                           "import " + TimeUnit.class.getName() + "\n" +
                           "global java.util.List list \n" +
                           "rule TimerRule \n" +
                           "   timer (int:100 50) \n" +
                           "when \n" +
                           "then \n" +
                           "        //forces it to pause until main thread is ready\n" +
                           "        list.add(list.size()); \n" +
                           " end";

        kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("timer-and-calendar-test", kieBaseTestConfiguration, drl);
        ksession = kbase.newKieSession();

        final List<Integer> list = Collections.synchronizedList(new ArrayList<>());
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        await().until(agendaIsNotEmpty());
        ksession.fireAllRules();

        await().until(() -> list.size() >= 1);

        kbase.removeRule("org.drools.compiler.test", "TimerRule");
        ksession.fireAllRules();

        await().until(ruleIsRemoved());
    }

    @Test
    public void testIntervalRuleInsertion() throws Exception {
        // DROOLS-620
        // Does not fail when using pseudo clock due to the subsequent call to fireAllRules
        final String drl =
                "package org.simple\n" +
                           "global java.util.List list\n" +
                           "import " + Alarm.class.getCanonicalName() + "\n" +
                           "rule \"Interval Alarm\"\n" +
                           "timer(int: 1s 1s)\n" +
                           "when " +
                           "    not Alarm()\n" +
                           "then\n" +
                           "    insert(new Alarm());\n" +
                           "    list.add(\"fired\"); \n" +
                           "end\n";

        final KieBase kbase =
                KieBaseUtil.getKieBaseFromKieModuleFromDrl("timer-and-calendar-test", kieBaseTestConfiguration, drl);
        final KieSessionConfiguration kieSessionConfiguration =
                KieSessionTestConfiguration.STATEFUL_REALTIME.getKieSessionConfiguration();
        kieSessionConfiguration.setOption(TimedRuleExecutionOption.YES);
        ksession = kbase.newKieSession(kieSessionConfiguration, null);

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        // use a timestamp to check if the interval is large enough

        long start = System.currentTimeMillis();

        await().until(() -> list.size()  == 1);

        long end = System.currentTimeMillis();

        assertTrue(end - start >= 1000);
    }

    private void awaitUntilRulesThatFiredAre(int rulesToFire) throws InterruptedException {
        int count = 0;
        while (count < rulesToFire) {
            count += ksession.fireAllRules();
            Thread.sleep(10);
        }
    }

    private Callable<Boolean> ruleIsRemoved() {
        return () -> kbase.getRule("org.drools.compiler.test", "TimerRule") == null;
    }

    private Callable<Boolean> agendaIsNotEmpty() {
        return () -> !((DefaultAgenda) ksession.getAgenda()).getPropagationList().isEmpty();
    }

}
