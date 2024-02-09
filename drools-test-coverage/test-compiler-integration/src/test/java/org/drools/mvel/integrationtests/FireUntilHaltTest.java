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
package org.drools.mvel.integrationtests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.compiler.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class FireUntilHaltTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public FireUntilHaltTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testSubmitOnFireUntilHalt() throws InterruptedException {
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list;" +
                "rule R when\n" +
                "    Person( happy, age >= 18 )\n" +
                "then\n" +
                "    list.add(\"happy adult\");" +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession kSession = kbase.newKieSession();

        final List<String> list = new ArrayList<>();
        kSession.setGlobal("list", list);

        new Thread(kSession::fireUntilHalt).start();

        final Person p = new Person("me", 17, true);
        final FactHandle fh = kSession.insert(p);

        Thread.sleep(100L);
        assertThat(list.size()).isEqualTo(0);

        kSession.submit(kieSession -> {
            p.setAge(18);
            p.setHappy(false);
            kieSession.update(fh, p);
        });

        Thread.sleep(100L);
        assertThat(list.size()).isEqualTo(0);

        kSession.submit(kieSession -> {
            p.setHappy(true);
            kieSession.update(fh, p);
        });

        Thread.sleep(100L);
        assertThat(list.size()).isEqualTo(1);

        kSession.halt();
        kSession.dispose();
    }

    @Test
    public void testFireAllWhenFiringUntilHalt() throws InterruptedException {
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration); // empty
        KieSession ksession = kbase.newKieSession();

        final Thread t1 = new Thread(ksession::fireUntilHalt);
        final Thread t2 = new Thread(ksession::fireAllRules);
        t1.start();
        Thread.sleep(500);
        t2.start();
        // give the chance for t2 to finish
        Thread.sleep(1000);
        final boolean aliveT2 = t2.isAlive();
        ksession.halt();
        Thread.sleep(1000);
        final boolean aliveT1 = t1.isAlive();
        if (t2.isAlive()) {
            t2.interrupt();
        }
        if (t1.isAlive()) {
            t1.interrupt();
        }
        assertThat(aliveT2).as("T2 should have finished").isFalse();
        assertThat(aliveT1).as("T1 should have finished").isFalse();
    }

    @Test
    public void testFireUntilHaltFailingAcrossEntryPoints() throws Exception {
        String rule1 = "package org.drools.mvel.compiler\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule testFireUntilHalt\n";
        rule1 += "when\n";
        rule1 += "       Cheese()\n";
        rule1 += "  $p : Person() from entry-point \"testep2\"\n";
        rule1 += "then \n";
        rule1 += "  list.add( $p ) ;\n";
        rule1 += "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, rule1);
        KieSession ksession = kbase.newKieSession();

        final EntryPoint ep = ksession.getEntryPoint("testep2");

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        ksession.insert(new Cheese("cheddar"));
        ksession.fireAllRules();

        final Thread t1 = new Thread(ksession::fireUntilHalt);
        t1.start();

        Thread.sleep(500);
        ep.insert(new Person("darth"));
        Thread.sleep(500);
        ksession.halt();
        t1.join(5000);
        final boolean alive = t1.isAlive();
        if (alive) {
            t1.interrupt();
        }
        assertThat(alive).as("Thread should have died!").isFalse();
        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    public void testAllFactsProcessedBeforeHalt() throws Exception {
        String drl = "package org.example.drools;\n" +
                "\n" +
                "global java.util.concurrent.CountDownLatch latch;\n" +
                "\n" +
                "rule \"R1\" when\n" +
                "    $s : String()\n" +
                "then\n" +
                "    latch.countDown();\n" +
                "end\n" +
                "rule \"R2\" when\n" +
                "    $s : String()\n" +
                "then\n" +
                "    latch.countDown();\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        CountDownLatch latch = new CountDownLatch(4);
        ksession.setGlobal("latch", latch);

        Executors.newSingleThreadExecutor().execute(ksession::fireUntilHalt);

        ksession.insert("aaa");
        ksession.insert("bbb");

        ksession.halt();

        // the 2 facts inserted should be processed before halt
        assertThat(latch.await(100, TimeUnit.MILLISECONDS)).isTrue();
    }
}
