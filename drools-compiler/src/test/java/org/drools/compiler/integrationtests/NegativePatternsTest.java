package org.drools.compiler.integrationtests;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.drools.core.time.SessionPseudoClock;
import org.junit.After;
import org.junit.Before;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.SessionEntryPoint;

import static org.junit.Assert.assertEquals;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.conf.ClockTypeOption;

/**
 * Tests negative patterns with or without additional constraints and events are
 * inserted through one or more entry points.
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
        KieServices ks = KieServices.Factory.get();

        KieModuleModel kieModule = ks.newKieModuleModel();
        KieBaseModel defaultBase = kieModule.newKieBaseModel("defaultKBase");
        defaultBase.setDefault(true);
        defaultBase.addPackage("*");
        defaultBase.setEventProcessingMode(EventProcessingOption.STREAM);
        defaultBase.newKieSessionModel(DEFAULT_SESSION_NAME)
                .setClockType(ClockTypeOption.get("pseudo")).setDefault(true);
        
        KieFileSystem kfs = ks.newKieFileSystem().write("src/main/resources/r1.drl", 
                ks.getResources().newClassPathResource("cep-negative-patterns.drl", 
                                                       NegativePatternsTest.class));

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
        SessionEntryPoint entryPoint = ksession.getEntryPoint("EventStream");

        int count = 0;

        // no rules should be fired in the beginning
        advanceTime(LONG_SLEEP_TIME);
        assertEquals(count, firedRulesListener.ruleFiredCount("SingleAbsence"));

        // after firing the rule will wait for 18ms
        ksession.fireAllRules();
        assertEquals(count, firedRulesListener.ruleFiredCount("SingleAbsence"));
        count++;
        advanceTime(LONG_SLEEP_TIME);
        assertEquals(count, firedRulesListener.ruleFiredCount("SingleAbsence"));

        FactHandle event = entryPoint.insert(new TestEvent(0, "EventA"));
        ksession.fireAllRules();
        advanceTime(LONG_SLEEP_TIME);
        assertEquals(count, firedRulesListener.ruleFiredCount("SingleAbsence"));

        entryPoint.delete(event);
        ksession.fireAllRules();
        count++;
        advanceTime(LONG_SLEEP_TIME);
        assertEquals(count, firedRulesListener.ruleFiredCount("SingleAbsence"));

        // rule was already fired and no changes were made to working memory
        ksession.fireAllRules();
        advanceTime(LONG_SLEEP_TIME);
        assertEquals(count, firedRulesListener.ruleFiredCount("SingleAbsence"));
    }

    @Test
    public void testConstrainedAbsence() throws InterruptedException {
        SessionEntryPoint entryPoint = ksession.getEntryPoint("EventStream");

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
            ksession.fireAllRules();
            advanceTime(LONG_SLEEP_TIME);

            entryPoint.delete(handle);
            count++;
            ksession.fireAllRules();
            advanceTime(LONG_SLEEP_TIME);
        }

        Thread.sleep(100);
        assertEquals(count, firedRulesListener.ruleFiredCount("SingleConstrained"));
    }

    @Test
    public void testMultipleEvents() throws InterruptedException {
        SessionEntryPoint entryPoint = ksession.getEntryPoint("EventStream");

        int count = 0;

        for (; count < LOOPS / 2;) {
            entryPoint.insert(new TestEvent(count, "EventA"));
            count++;
            ksession.fireAllRules();
            advanceTime(SHORT_SLEEP_TIME);
        }

        entryPoint.insert(new TestEvent(count, "EventA"));
        FactHandle handle = entryPoint.insert(new TestEvent(-1, "EventB"));
        ksession.fireAllRules();
        advanceTime(SHORT_SLEEP_TIME);

        entryPoint.delete(handle);
        count++;
        ksession.fireAllRules();
        advanceTime(SHORT_SLEEP_TIME);

        for (; count < LOOPS;) {
            entryPoint.insert(new TestEvent(count, "EventA"));
            count++;
            ksession.fireAllRules();
            advanceTime(SHORT_SLEEP_TIME);
        }

        Thread.sleep(100);
        assertEquals(count, firedRulesListener.ruleFiredCount("MultipleEvents"));
    }

    @Test
    public void testMultipleEntryPoints() throws InterruptedException {
        SessionEntryPoint entryPoint = ksession.getEntryPoint("EventStream");

        SessionEntryPoint otherStream = ksession.getEntryPoint("OtherStream");
        int count = 0;

        count++;
        ksession.fireAllRules();
        advanceTime(LONG_SLEEP_TIME);
        assertEquals(count, firedRulesListener.ruleFiredCount("MultipleEntryPoints"));

        FactHandle handle;
        for (int i = 0; i < LOOPS; i++) {
            handle = entryPoint.insert(new TestEvent(count, "EventA"));
            ksession.fireAllRules();
            advanceTime(LONG_SLEEP_TIME);

            entryPoint.delete(handle);
            count++;
            ksession.fireAllRules();
            advanceTime(LONG_SLEEP_TIME);
        }

        for (int i = 0; i < LOOPS; i++) {
            handle = otherStream.insert(new TestEvent(count, "EventB"));
            ksession.fireAllRules();
            advanceTime(LONG_SLEEP_TIME);

            otherStream.delete(handle);
            count++;
            ksession.fireAllRules();
            advanceTime(SHORT_SLEEP_TIME);
        }

        Thread.sleep(100);
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