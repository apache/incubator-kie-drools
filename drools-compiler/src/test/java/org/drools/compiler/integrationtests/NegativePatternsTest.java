/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.integrationtests;

import org.drools.core.time.SessionPseudoClock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Tests negative patterns with or without additional constraints and events are
 * inserted through one or more entry points.
 * BZ-978979
 */
public class NegativePatternsTest {

    private static final int LOOPS = 300;
    private static final int SHORT_SLEEP_TIME = 20;
    private static final int LONG_SLEEP_TIME = 30;

    private static final String DEFAULT_SESSION_NAME = "defaultKSession";

    private KieSession ksession;
    private TrackingAgendaEventListener firedRulesListener;

    @Before
    public void prepareKieSession() {
        String drl = "package org.drools.compiler.integrationtests\n" +
                     "\n" +
                     "import org.drools.compiler.integrationtests.NegativePatternsTest.TestEvent\n" +
                     "\n" +
                     "declare TestEvent\n" +
                     "    @role( event )\n" +
                     "    @expires( 22ms )\n" +
                     "end\n" +
                     "\n" +
                     "rule \"SingleAbsence\"\n" +
                     "    duration( 18ms )\n" +
                     "    when\n" +
                     "        not ( TestEvent ( ) from entry-point EventStream )\n" +
                     "    then\n" +
                     "        // consequence\n" +
                     "end\n" +
                     "\n" +
                     "rule \"SingleConstrained\"\n" +
                     "    duration( 18ms )\n" +
                     "    when\n" +
                     "        not ( TestEvent ( name == \"EventA\" ) from entry-point EventStream )\n" +
                     "    then\n" +
                     "        // consequence\n" +
                     "end\n" +
                     "\n" +
                     "rule \"MultipleEvents\"\n" +
                     "    duration( 18ms )\n" +
                     "    when\n" +
                     "        TestEvent ( name == \"EventA\" ) over window:time( 22ms ) from entry-point EventStream\n" +
                     "        not ( TestEvent ( name == \"EventB\" ) over window:time( 22ms )  from entry-point EventStream )\n" +
                     "    then\n" +
                     "        // consequence\n" +
                     "end\n" +
                     "\n" +
                     "rule \"MultipleEntryPoints\"\n" +
                     "    duration( 18ms )\n" +
                     "    when\n" +
                     "        not (\n" +
                     "            TestEvent( name == \"EventA\" ) from entry-point EventStream\n" +
                     "        )\n" +
                     "        not (\n" +
                     "            TestEvent( name == \"EventB\" ) from entry-point OtherStream\n" +
                     "        )\n" +
                     "    then\n" +
                     "        // consequence\n" +
                     "end\n";

        KieServices ks = KieServices.Factory.get();

        KieModuleModel kieModule = ks.newKieModuleModel();
        KieBaseModel defaultBase = kieModule.newKieBaseModel("defaultKBase")
                                            .setDefault(true)
                                            .addPackage("*")
                                            .setEventProcessingMode(EventProcessingOption.STREAM);
        defaultBase.newKieSessionModel(DEFAULT_SESSION_NAME)
                   .setClockType(ClockTypeOption.get("pseudo"))
                   .setDefault(true);

        KieFileSystem kfs = ks.newKieFileSystem().write("src/main/resources/r1.drl", drl);

        kfs.writeKModuleXML(kieModule.toXML());
        KieModule builtModule = ks.newKieBuilder(kfs).buildAll().getKieModule();
        ks.getRepository().addKieModule(builtModule);

        ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession(DEFAULT_SESSION_NAME);
        firedRulesListener = new TrackingAgendaEventListener();
        ksession.addEventListener(firedRulesListener);
    }

    @After
    public void cleanKieSession() {
        if (ksession != null) {
            ksession.dispose();
        }
    }

    @Test
    public void testSingleEvent() throws InterruptedException {
        EntryPoint entryPoint = ksession.getEntryPoint("EventStream");

        int count = 0;

        // no rules should be fired in the beginning
        advanceTime(LONG_SLEEP_TIME);
        assertEquals(count, firedRulesListener.ruleFiredCount("SingleAbsence"));

        // after firing the rule will wait for 18ms
        ksession.fireAllRules();
        assertEquals(count, firedRulesListener.ruleFiredCount("SingleAbsence"));
        count++;
        advanceTime(LONG_SLEEP_TIME);
        ksession.fireAllRules();
        assertEquals(count, firedRulesListener.ruleFiredCount("SingleAbsence"));

        FactHandle event = entryPoint.insert(new TestEvent(0, "EventA"));
        ksession.fireAllRules();
        advanceTime(LONG_SLEEP_TIME);
        ksession.fireAllRules();
        assertEquals(count, firedRulesListener.ruleFiredCount("SingleAbsence"));

        entryPoint.delete(event);
        ksession.fireAllRules();
        count++;
        advanceTime(LONG_SLEEP_TIME);
        ksession.fireAllRules();
        assertEquals(count, firedRulesListener.ruleFiredCount("SingleAbsence"));

        // rule was already fired and no changes were made to working memory
        ksession.fireAllRules();
        advanceTime(LONG_SLEEP_TIME);
        ksession.fireAllRules();
        assertEquals(count, firedRulesListener.ruleFiredCount("SingleAbsence"));
    }

    @Test
    public void testConstrainedAbsence() throws InterruptedException {
        EntryPoint entryPoint = ksession.getEntryPoint("EventStream");

        int count = 0;

        count++;
        for (int i = 0; i < LOOPS; i++) {
            entryPoint.insert(new TestEvent(count, "EventB"));
            ksession.fireAllRules();
            advanceTime(LONG_SLEEP_TIME);
        }

        FactHandle handle;
        for (int i = 0; i < LOOPS; i++) {
            handle = entryPoint.insert(new TestEvent(i, "EventA"));
            advanceTime(LONG_SLEEP_TIME);
            ksession.fireAllRules();

            entryPoint.delete(handle);
            count++;
            advanceTime(LONG_SLEEP_TIME);
            ksession.fireAllRules();
        }

        ksession.fireAllRules();
        assertEquals(count, firedRulesListener.ruleFiredCount("SingleConstrained"));
    }

    @Test
    public void testMultipleEvents() throws InterruptedException {
        EntryPoint entryPoint = ksession.getEntryPoint("EventStream");

        int count = 0;

        for (; count < LOOPS / 2;) {
            entryPoint.insert(new TestEvent(count, "EventA"));
            ksession.fireAllRules();
            count++;
            advanceTime(SHORT_SLEEP_TIME);
            ksession.fireAllRules();
        }
        assertEquals(count, firedRulesListener.ruleFiredCount("MultipleEvents"));

        entryPoint.insert(new TestEvent(count, "EventA"));
        FactHandle handle = entryPoint.insert(new TestEvent(-1, "EventB"));
        advanceTime(SHORT_SLEEP_TIME);
        ksession.fireAllRules();

        entryPoint.delete(handle);
        ksession.fireAllRules();
        // it shouldn't fire because of the duration
        advanceTime(SHORT_SLEEP_TIME);
        ksession.fireAllRules();
        // it shouldn't fire because event A is gone out of window

        for (; count < LOOPS;) {
            entryPoint.insert(new TestEvent(count, "EventA"));
            ksession.fireAllRules();
            count++;
            advanceTime(SHORT_SLEEP_TIME);
            ksession.fireAllRules();
        }

        ksession.fireAllRules();
        assertEquals(count, firedRulesListener.ruleFiredCount("MultipleEvents"));
    }

    @Test
    public void testMultipleEntryPoints() throws InterruptedException {
        EntryPoint entryPoint = ksession.getEntryPoint("EventStream");

        EntryPoint otherStream = ksession.getEntryPoint("OtherStream");
        int count = 0;

        count++;
        ksession.fireAllRules();
        advanceTime(LONG_SLEEP_TIME);
        ksession.fireAllRules();
        assertEquals(count, firedRulesListener.ruleFiredCount("MultipleEntryPoints"));

        FactHandle handle;
        for (int i = 0; i < LOOPS; i++) {
            handle = entryPoint.insert(new TestEvent(count, "EventA"));
            ksession.fireAllRules();
            advanceTime(LONG_SLEEP_TIME);

            entryPoint.delete(handle);
            ksession.fireAllRules();
            count++;
            advanceTime(LONG_SLEEP_TIME);
            ksession.fireAllRules();
        }

        for (int i = 0; i < LOOPS; i++) {
            handle = otherStream.insert(new TestEvent(count, "EventB"));
            advanceTime(LONG_SLEEP_TIME);
            ksession.fireAllRules();

            otherStream.delete(handle);
            count++;
            advanceTime(SHORT_SLEEP_TIME);
            ksession.fireAllRules();
        }

        ksession.fireAllRules();
        assertEquals(count, firedRulesListener.ruleFiredCount("MultipleEntryPoints"));
    }

    private void advanceTime(final long amount) {
        SessionPseudoClock clock = ksession.getSessionClock();
        clock.advanceTime(amount, TimeUnit.MILLISECONDS);
    }

    /**
     * Simple event used for tests.
     */
    public static class TestEvent implements Serializable {

        private static final long serialVersionUID = -6985691286327371275L;
        private final Integer id;
        private final String name;

        public TestEvent(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return String.format("TestEvent[id=%s, name=%s]", id, name);
        }
    }

    /**
     * Listener tracking number of rules fired.
     */
    public static class TrackingAgendaEventListener extends DefaultAgendaEventListener {

        private Map<String, Integer> rulesFired = new HashMap<String, Integer>();

        @Override
        public void afterMatchFired(AfterMatchFiredEvent event) {
            String rule = event.getMatch().getRule().getName();
            if (isRuleFired(rule)) {
                rulesFired.put(rule, rulesFired.get(rule) + 1);
            } else {
                rulesFired.put(rule, 1);
            }
        }

        /**
         * Return true if the rule was fired at least once
         *
         * @param rule - name of the rule
         * @return true if the rule was fired
         */
        public boolean isRuleFired(String rule) {
            return rulesFired.containsKey(rule);
        }

        /**
         * Returns number saying how many times the rule was fired
         *
         * @param rule - name of the rule
         * @return number how many times rule was fired, 0 if rule wasn't fired
         */
        public int ruleFiredCount(String rule) {
            if (isRuleFired(rule)) {
                return rulesFired.get(rule);
            } else {
                return 0;
            }
        }

        public void clear() {
            rulesFired.clear();
        }
    }
}