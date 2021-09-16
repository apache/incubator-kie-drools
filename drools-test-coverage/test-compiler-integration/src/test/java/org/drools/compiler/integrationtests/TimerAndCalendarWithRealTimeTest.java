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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
import org.kie.api.runtime.rule.FactHandle;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TimerAndCalendarWithRealTimeTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;
    private KieSession ksession;

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

        waitUntilRuleFires();
        // now check for update
        assertEquals(1, list.size());
    }

    private void waitUntilRuleFires() throws InterruptedException {
        while (ksession.fireAllRules() == 0) {
            Thread.sleep(10);
        }
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

        waitUntilRuleFires();

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

        waitUntilRuleFires();

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

        ksession.fireAllRules();
        Thread.sleep(200);
        ksession.fireAllRules();
        Thread.sleep(200);
        ksession.fireAllRules();
        // now check that rule "wrap A" fired once, creating one B
        assertEquals(2, ksession.getFactCount());
    }

    @Test(timeout = 10000)
    public void testTimerRemoval() throws InterruptedException {
        final String drl = "package org.drools.compiler.test\n" +
                           "import " + TimeUnit.class.getName() + "\n" +
                           "global java.util.List list \n" +
                           "global " + CountDownLatch.class.getName() + " latch\n" +
                           "rule TimerRule \n" +
                           "   timer (int:100 50) \n" +
                           "when \n" +
                           "then \n" +
                           "        //forces it to pause until main thread is ready\n" +
                           "        latch.await(10, TimeUnit.MINUTES); \n" +
                           "        list.add(list.size()); \n" +
                           " end";

        final KieBase kbase =
                KieBaseUtil.getKieBaseFromKieModuleFromDrl("timer-and-calendar-test", kieBaseTestConfiguration, drl);
        ksession = kbase.newKieSession();

        final CountDownLatch latch = new CountDownLatch(1);
        final List<Integer> list = Collections.synchronizedList(new ArrayList<>());
        ksession.setGlobal("list", list);
        ksession.setGlobal("latch", latch);

        ksession.fireAllRules();
        Thread.sleep(500); // this makes sure it actually enters a rule
        kbase.removeRule("org.drools.compiler.test", "TimerRule");
        ksession.fireAllRules();
        latch.countDown();
        Thread.sleep(500); // allow the last rule, if we were in the middle of one to actually fire, before clearing
        ksession.fireAllRules();
        list.clear();
        Thread.sleep(500); // now wait to see if any more fire, they shouldn't
        ksession.fireAllRules();
        assertEquals(0, list.size());
    }

    @Test
    public void testCronFire() {
        // BZ-1059372
        final String drl = "package test.drools\n" +
                           "rule TestRule " +
                           "  timer (cron:* * * * * ?) " +
                           "when\n" +
                           "    String() " +
                           "    Integer() " +
                           "then\n" +
                           "end\n";

        final KieBase kbase =
                KieBaseUtil.getKieBaseFromKieModuleFromDrl("timer-and-calendar-test", kieBaseTestConfiguration, drl);
        ksession = kbase.newKieSession();

        final int repetitions = 10000;
        for (int j = 0; j < repetitions; j++) {
            ksession.insert(j);
        }

        ksession.insert("go");
        ksession.fireAllRules();
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
        assertEquals(0, list.size());
        Thread.sleep(900);
        assertEquals(0, list.size());
        Thread.sleep(500);
        assertEquals(1, list.size());
    }

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
        Thread.sleep(1000);
        final FactHandle handle = ksession.insert("halt");
        Thread.sleep(2000);

        // now check that rule "halt" fired once, creating one Integer
        assertEquals(2, ksession.getFactCount());
        ksession.delete(handle);
    }

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

}
