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

import org.drools.kiesession.agenda.DefaultAgenda;
import org.drools.testcoverage.common.model.Alarm;
import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.TimedRuleExecutionOption;
import org.kie.api.runtime.rule.FactHandle;

import static java.util.Arrays.asList;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
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

        await().until(list::size, greaterThanOrEqualTo(1));

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

        await().until(list::size, equalTo(1));

        long end = System.currentTimeMillis();

        assertTrue(end - start >= 1000);
    }

    @Ignore("DROOLS-6479 - Fixing timing issues")
    @Test(timeout = 10000)
    public void testHaltWithTimer() throws Exception {
        final String drl = "// fire once, for a String, create an Integer, halt!\n" +
                           "rule x\n" +
                           "timer(int:0 1000)\n" +
                           "when\n" +
                           "    $s: String( this == \"halt\" )\n" +
                           "then\n" +
                           "    insert( new Integer(1) );\n" +
                           "    drools.halt();\n" +
                           "end";

        final KieBase kbase =
                KieBaseUtil.getKieBaseFromKieModuleFromDrl("timer-and-calendar-test", kieBaseTestConfiguration, drl);
        ksession = kbase.newKieSession();
        new Thread(ksession::fireUntilHalt).start();
        // Thread.sleep(1000);

        ksession.insert("halt");

        // replace with latches
        Thread.sleep(2000);

        // now check that rule "halt" fired once, creating one Integer
        assertEquals(2, ksession.getFactCount());
        //ksession.delete(handle);
    }

    @Ignore("DROOLS-6479 - Fixing timing issues")
    @Test(timeout = 10000)
    public void testHaltAfterSomeTimeThenRestart() throws Exception {
        final String drl = "package org.kie.test;" +
                           "global java.util.List list; \n" +
                           "\n" +
                           "\n" +
                           "rule FireAtWill\n" +
                           "timer(int:0 100)\n" +
                           "when  \n" +
                           "then \n" +
                           "  list.add( 0 );\n" +
                           "end\n" +
                           "\n" +
                           "rule ImDone\n" +
                           "when\n" +
                           "  String( this == \"halt\" )\n" +
                           "then\n" +
                           "  drools.halt();\n" +
                           "end\n" +
                           "\n" +
                           "rule Hi \n" +
                           "salience 10 \n" +
                           "when \n" +
                           "  String( this == \"trigger\" ) \n" +
                           "then \n " +
                           "  list.add( 5 ); \n" +
                           "end \n" +
                           "\n" +
                           "rule Lo \n" +
                           "salience -5 \n" +
                           "when \n" +
                           "  String( this == \"trigger\" ) \n" +
                           "then \n " +
                           "  list.add( -5 ); \n" +
                           "end \n";

        final KieBase kbase =
                KieBaseUtil.getKieBaseFromKieModuleFromDrl("timer-and-calendar-test", kieBaseTestConfiguration, drl);
        ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        new Thread(ksession::fireUntilHalt).start();

        Thread.sleep(250);

        assertEquals(asList(0, 0, 0), list);

        ksession.insert("halt");
        ksession.insert("trigger");
        Thread.sleep(300);
        assertEquals(asList(0, 0, 0), list);

        new Thread(ksession::fireUntilHalt).start();
        Thread.sleep(200);

        assertEquals(asList(0, 0, 0, 5, 0, -5, 0, 0), list);
    }

    @Ignore("DROOLS-6479 - Fixing timing issues")
    @Test(timeout = 10000)
    public void testHaltAfterSomeTimeThenRestartButNoLongerHolding() throws Exception {
        final String drl = "package org.kie.test;" +
                           "global java.util.List list; \n" +
                           "\n" +
                           "\n" +
                           "rule FireAtWill\n" +
                           "   timer(int:0 200)\n" +
                           "when  \n" +
                           "  eval(true)" +
                           "  String( this == \"trigger\" )" +
                           "then \n" +
                           "  list.add( 0 );\n" +
                           "end\n" +
                           "\n" +
                           "rule ImDone\n" +
                           "when\n" +
                           "  String( this == \"halt\" )\n" +
                           "then\n" +
                           "  drools.halt();\n" +
                           "end\n" +
                           "\n";

        final KieBase kbase =
                KieBaseUtil.getKieBaseFromKieModuleFromDrl("timer-and-calendar-test", kieBaseTestConfiguration, drl);
        ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final FactHandle handle = ksession.insert("trigger");

        new Thread(ksession::fireUntilHalt).start();

        Thread.sleep(350);
        assertEquals(2, list.size()); // delay 0, repeat after 100
        assertEquals(asList(0, 0), list);

        ksession.insert("halt");

        Thread.sleep(200);
        ksession.delete(handle);
        assertEquals(2, list.size()); // halted, no more rule firing

        new Thread(ksession::fireUntilHalt).start();
        Thread.sleep(200);

        assertEquals(2, list.size());
        assertEquals(asList(0, 0), list);
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
