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

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.drools.core.time.impl.PseudoClockScheduler;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.rule.FactHandle;

import static org.awaitility.Awaitility.await;

@RunWith(Parameterized.class)
public class TimerAndCalendarFireUntilHaltTest {

    private final class RecordingRulesListener extends  DefaultAgendaEventListener {
        private Map<String, Integer> firedRules = new HashMap<>();
         

        public int timesRulesHasFired(String ruleName) {
            if (firedRules.containsKey(ruleName)) {
                return firedRules.get(ruleName).intValue();
            }
            return 0;
        }


        @Override
        public void afterMatchFired(AfterMatchFiredEvent event) {
            String ruleName = event.getMatch().getRule().getName();
            if (!firedRules.containsKey(ruleName)) {
                firedRules.put(ruleName, Integer.valueOf(0));
            } 
            
            firedRules.put(ruleName, firedRules.get(ruleName).intValue()+1);
        }
    }

    private final KieBaseTestConfiguration kieBaseTestConfiguration;
    private KieSession ksession;
    private KieBase kbase;
    private CountDownLatch stoppedLatch;
    private PseudoClockScheduler timeService;
    private RecordingRulesListener listener;
    private CountDownLatch startingLatch;
    private FactHandle triggerHandle;

    public TimerAndCalendarFireUntilHaltTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
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

    @Test(timeout = 10000)
    public void testTimerRuleFires() throws Exception {
        final String drl = "// fire once, for a String, create an Integer\n" +
                           "rule TimerRule\n" +
                           "timer(int:0 1000)\n" +
                           "when\n" +
                           "    $s: String( this == \"trigger\" )\n" +
                           "then\n" +
                           "    insert( new Integer(1) );\n" +
                           "end";
        setupKSessionFor(drl);
        startEngine();

        activateRule();
        advanceTimerOneSecond();
        
        await().until(ruleHasFired("TimerRule", 1));
    }
    
    @Test(timeout = 10000)
    public void testTimerRuleHaltStopsFiring() throws Exception {
        final String drl = "// fire once, for a String, create an Integer\n" +
                           "rule TimerRule\n" +
                           "timer(int:0 1000)\n" +
                           "when\n" +
                           "    $s: String( this == \"trigger\" )\n" +
                           "then\n" +
                           "    insert( new Integer(1) );\n" +
                           "end";
        setupKSessionFor(drl);
        startEngine();
        activateRule();
        advanceTimerOneSecond();
        await().until(ruleHasFired("TimerRule", 1));
        
        stopEngine();

        advanceTimerOneSecond();
        await().during(Duration.ofSeconds(1)).atMost(Duration.ofSeconds(2)).until(ruleHasFired("TimerRule", 1));
    }
    
    @Test(timeout = 10000)
    public void testTimerRuleRestartsAfterStop() throws Exception {
        final String drl = "// fire once, for a String, create an Integer\n" +
                           "rule TimerRule\n" +
                           "timer(int:0 1000)\n" +
                           "when\n" +
                           "    $s: String( this == \"trigger\" )\n" +
                           "then\n" +
                           "    insert( new Integer(1) );\n" +
                           "end";
        setupKSessionFor(drl);
        startEngine();
        activateRule();
        advanceTimerOneSecond();
        await().until(ruleHasFired("TimerRule", 1));
        
        stopEngine();
        startEngine();

        advanceTimerOneSecond();
        await().during(Duration.ofSeconds(1)).atMost(Duration.ofSeconds(2)).until(ruleHasFired("TimerRule", 2));
    }

    @Test(timeout = 10000)
    public void testTimerRuleDoesRestartsIfNoLongerHolds() throws Exception {
        final String drl = "// fire once, for a String, create an Integer\n" +
                           "rule TimerRule\n" +
                           "timer(int:0 1000)\n" +
                           "when\n" +
                           "    $s: String( this == \"trigger\" )\n" +
                           "then\n" +
                           "    insert( new Integer(1) );\n" +
                           "end";
        setupKSessionFor(drl);
        startEngine();
        activateRule();
        advanceTimerOneSecond();
        
        await().until(ruleHasFired("TimerRule", 1));
        
        stopEngine();
        disactivateRule();
        startEngine();

        advanceTimerOneSecond();

        await().during(Duration.ofSeconds(1)).atMost(Duration.ofSeconds(2)).until(ruleHasFired("TimerRule", 1));
    }

 
    private void setupKSessionFor(final String drl) {
        kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("timer-and-calendar-test", kieBaseTestConfiguration, drl);
        KieSessionConfiguration kieSessionConfiguration = KieSessionTestConfiguration.STATEFUL_PSEUDO.getKieSessionConfiguration();
        ksession = kbase.newKieSession(kieSessionConfiguration, null);      
        listener = new RecordingRulesListener();
        ksession.addEventListener(listener);
        timeService = ksession.getSessionClock();
    }

    private void startEngine() throws InterruptedException {
        startingLatch = new CountDownLatch(1);
        stoppedLatch = new CountDownLatch(1);
        
        Thread t = new Thread(() -> {
            startingLatch.countDown();
            ksession.fireUntilHalt();
            stoppedLatch.countDown();
        });
        t.start();
        startingLatch.await();
    }
    
    private void stopEngine() throws InterruptedException {
        ksession.halt();
        stoppedLatch.await();
    }

    private Callable<Boolean> ruleHasFired(String ruleName, int times) {
        return () -> listener.timesRulesHasFired(ruleName) == times;
    }

    private void advanceTimerOneSecond() {
        timeService.advanceTime(1, TimeUnit.SECONDS);
    }

    private void activateRule() {
        triggerHandle = ksession.insert("trigger");
    }

    private void disactivateRule() {
        ksession.delete(triggerHandle);
    }
}
