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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.drools.core.time.impl.PseudoClockScheduler;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.rule.FactHandle;

import static org.awaitility.Awaitility.await;

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

    private KieSession ksession;
    private KieBase kbase;
    private CountDownLatch stoppedLatch;
    private PseudoClockScheduler timeService;
    private RecordingRulesListener listener;
    private CountDownLatch startingLatch;
    private FactHandle triggerHandle;

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseStreamConfigurations(true).stream();
    }
    
    @AfterEach
    public void after() throws Exception {
        if (ksession != null) {
            ksession.dispose();
        }
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    @Timeout(10000)
    public void testTimerRuleFires(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        final String drl = "// fire once, for a String, create an Integer\n" +
                           "rule TimerRule\n" +
                           "timer(int:0 1000)\n" +
                           "when\n" +
                           "    $s: String( this == \"trigger\" )\n" +
                           "then\n" +
                           "    insert( new Integer(1) );\n" +
                           "end";
        setupKSessionFor(kieBaseTestConfiguration, drl);
        startEngine();

        activateRule();
        await().until(ruleHasFired("TimerRule", 1));

        advanceTimerOneSecond();
        await().until(ruleHasFired("TimerRule", 2));

        stopEngine();
    }
    
    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    @Timeout(10000)
    public void testTimerRuleHaltStopsFiring(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        final String drl = "// fire once, for a String, create an Integer\n" +
                           "rule TimerRule\n" +
                           "timer(int:0 1000)\n" +
                           "when\n" +
                           "    $s: String( this == \"trigger\" )\n" +
                           "then\n" +
                           "    insert( new Integer(1) );\n" +
                           "end";
        setupKSessionFor(kieBaseTestConfiguration, drl);
        startEngine();

        activateRule();
        await().until(ruleHasFired("TimerRule", 1));

        advanceTimerOneSecond();
        await().until(ruleHasFired("TimerRule", 2));
        
        stopEngine();

        advanceTimerOneSecond();
        await().during(Duration.ofSeconds(1)).atMost(Duration.ofSeconds(2)).until(ruleHasFired("TimerRule", 2));
    }
    
    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    @Timeout(10000)
    public void testTimerRuleRestartsAfterStop(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        final String drl = "// fire once, for a String, create an Integer\n" +
                           "rule TimerRule\n" +
                           "timer(int:0 1000)\n" +
                           "when\n" +
                           "    $s: String( this == \"trigger\" )\n" +
                           "then\n" +
                           "    insert( new Integer(1) );\n" +
                           "end";
        setupKSessionFor(kieBaseTestConfiguration, drl);
        startEngine();

        activateRule();
        await().until(ruleHasFired("TimerRule", 1));

        advanceTimerOneSecond();
        await().until(ruleHasFired("TimerRule", 2));
        
        stopEngine();
        startEngine();

        advanceTimerOneSecond();
        await().during(Duration.ofSeconds(1)).atMost(Duration.ofSeconds(2)).until(ruleHasFired("TimerRule", 3));

        stopEngine();
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    @Timeout(10000)
    public void testTimerRuleDoesRestartsIfNoLongerHolds(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        final String drl = "// fire once, for a String, create an Integer\n" +
                           "rule TimerRule\n" +
                           "timer(int:0 1000)\n" +
                           "when\n" +
                           "    $s: String( this == \"trigger\" )\n" +
                           "then\n" +
                           "    insert( new Integer(1) );\n" +
                           "end";
        setupKSessionFor(kieBaseTestConfiguration, drl);
        startEngine();

        activateRule();
        await().until(ruleHasFired("TimerRule", 1));

        advanceTimerOneSecond();
        await().until(ruleHasFired("TimerRule", 2));
        
        stopEngine();
        disactivateRule();
        startEngine();

        advanceTimerOneSecond();
        await().during(Duration.ofSeconds(1)).atMost(Duration.ofSeconds(2)).until(ruleHasFired("TimerRule", 2));

        stopEngine();
    }

 
    private void setupKSessionFor(KieBaseTestConfiguration kieBaseTestConfiguration, final String drl) {
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
