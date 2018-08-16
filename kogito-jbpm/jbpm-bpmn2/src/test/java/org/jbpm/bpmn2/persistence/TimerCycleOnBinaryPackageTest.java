/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.bpmn2.persistence;

import org.drools.core.command.SingleSessionCommandService;
import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.jbpm.bpmn2.JbpmBpmn2TestCase;
import org.jbpm.process.audit.AuditLoggerFactory;
import org.jbpm.process.audit.AuditLoggerFactory.Type;
import org.jbpm.process.instance.event.listeners.TriggerRulesEventListener;
import org.jbpm.test.listener.process.NodeLeftCountDownProcessEventListener;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.runtime.Environment;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TimerCycleOnBinaryPackageTest extends JbpmBpmn2TestCase {

    private static final Logger logger = LoggerFactory.getLogger(TimerCycleOnBinaryPackageTest.class);
    private StatefulKnowledgeSession ksession;

    public TimerCycleOnBinaryPackageTest() {
        super(true);
    }

    @BeforeClass
    public static void setup() throws Exception {
        setUpDataSource();
    }
    
    @Before
    public void prepare() {
        clearHistory();
    }

    @After
    public void dispose() {
        if (ksession != null) {
            ksession.dispose();
        }
    }

    @Test(timeout=20000)
    public void testStartTimerCycleFromDisc() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("start", 2);
        KieBase kbase = createKnowledgeBaseFromDisc("BPMN2-StartTimerCycle.bpmn2");
        try {
            StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
            ksession.addEventListener(countDownListener);
            
            assertEquals(0, getNumberOfProcessInstances("defaultPackage.TimerProcess"));
            long sessionId = ksession.getIdentifier();
            Environment env = ksession.getEnvironment();
    
            final List<Long> list = new ArrayList<Long>();
            ksession.addEventListener(new DefaultProcessEventListener() {
                public void beforeProcessStarted(ProcessStartedEvent event) {
                    list.add(event.getProcessInstance().getId());
                }
            });
    
            ( (SingleSessionCommandService) ( (CommandBasedStatefulKnowledgeSession) ksession ).getRunner() ).getKieSession()
                                                                                                             .addEventListener(new TriggerRulesEventListener(ksession));
    
            
    
            countDownListener.waitTillCompleted();
    
            assertEquals(2, getNumberOfProcessInstances("defaultPackage.TimerProcess"));
            logger.info("dispose");
            ksession.dispose();
            
            countDownListener = new NodeLeftCountDownProcessEventListener("start", 2);
    
            ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId,
                    kbase, null, env);
            ksession.addEventListener(countDownListener);
            AuditLoggerFactory.newInstance(Type.JPA, ksession, null);
    
            final List<Long> list2 = new ArrayList<Long>();
            ksession.addEventListener(new DefaultProcessEventListener() {
                public void beforeProcessStarted(ProcessStartedEvent event) {
                    list2.add(event.getProcessInstance().getId());
                }
            });
    
            ( (SingleSessionCommandService) ( (CommandBasedStatefulKnowledgeSession) ksession ).getRunner() ).getKieSession()
                    .addEventListener(new TriggerRulesEventListener(ksession));
    
            countDownListener.waitTillCompleted();        
            assertEquals(4, getNumberOfProcessInstances("defaultPackage.TimerProcess"));
            
            abortProcessInstances(ksession);
            ksession.dispose();
        } finally {
            ksession = createKnowledgeSession(kbase);
            abortProcessInstances(ksession);
            ksession.dispose();
        }
    }

    @Test(timeout=20000)
    public void testStartTimerCycleFromClassPath() throws Exception {
        
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("start", 2);
        KieBase kbase = createKnowledgeBase("BPMN2-StartTimerCycle.bpmn2");
        try {
            StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
            ksession.addEventListener(countDownListener);
    
            assertEquals(0, getNumberOfProcessInstances("defaultPackage.TimerProcess"));
            long sessionId = ksession.getIdentifier();
            Environment env = ksession.getEnvironment();
    
            final List<Long> list = new ArrayList<Long>();
            ksession.addEventListener(new DefaultProcessEventListener() {
                public void beforeProcessStarted(ProcessStartedEvent event) {
                    list.add(event.getProcessInstance().getId());
                }
            });
    
            ( (SingleSessionCommandService) ( (CommandBasedStatefulKnowledgeSession) ksession ).getRunner() ).getKieSession()
                    .addEventListener(new TriggerRulesEventListener(ksession));
    
            countDownListener.waitTillCompleted();
    
            assertEquals(2, getNumberOfProcessInstances("defaultPackage.TimerProcess"));
            logger.info("dispose");
            ksession.dispose();
    
            countDownListener = new NodeLeftCountDownProcessEventListener("start", 2);
            ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId,
                    kbase, null, env);
            ksession.addEventListener(countDownListener);
            AuditLoggerFactory.newInstance(Type.JPA, ksession, null);
    
            final List<Long> list2 = new ArrayList<Long>();
            ksession.addEventListener(new DefaultProcessEventListener() {
                public void beforeProcessStarted(ProcessStartedEvent event) {
                    list2.add(event.getProcessInstance().getId());
                }
            });
    
            ( (SingleSessionCommandService) ( (CommandBasedStatefulKnowledgeSession) ksession ).getRunner() ).getKieSession()
                    .addEventListener(new TriggerRulesEventListener(ksession));
    
            countDownListener.waitTillCompleted();
            ksession.dispose();
            assertEquals(4, getNumberOfProcessInstances("defaultPackage.TimerProcess"));
        } finally {
            ksession = createKnowledgeSession(kbase);
            abortProcessInstances(ksession);
            ksession.dispose();
        }
    }

    @Test @Ignore("beta4 phreak")
    public void testStartTimerCycleFromDiscDRL() throws Exception {
        KieBase kbase = createKnowledgeBaseFromDisc("rules-timer.drl");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        long sessionId = ksession.getIdentifier();
        Environment env = ksession.getEnvironment();

        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ( (SingleSessionCommandService) ( (CommandBasedStatefulKnowledgeSession) ksession ).getRunner() ).getKieSession()
                .addEventListener(new TriggerRulesEventListener(ksession));

        ksession.fireAllRules();

        Thread.sleep(5000);

        assertEquals(2, list.size());
        logger.info("dispose");
        ksession.dispose();

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId,
                kbase, null, env);
        AuditLoggerFactory.newInstance(Type.JPA, ksession, null);

        final List<String> list2 = new ArrayList<String>();
        ksession.setGlobal("list", list2);

        ( (SingleSessionCommandService) ( (CommandBasedStatefulKnowledgeSession) ksession ).getRunner() ).getKieSession()
                .addEventListener(new TriggerRulesEventListener(ksession));

        ksession.fireAllRules();

        Thread.sleep(6000);

        assertEquals(3, list2.size());
    }

    @Test @Ignore("beta4 phreak")
    public void testStartTimerCycleFromClasspathDRL() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("rules-timer.drl");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        long sessionId = ksession.getIdentifier();
        Environment env = ksession.getEnvironment();

        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ( (SingleSessionCommandService) ( (CommandBasedStatefulKnowledgeSession) ksession ).getRunner() ).getKieSession()
                .addEventListener(new TriggerRulesEventListener(ksession));

        ksession.fireAllRules();

        Thread.sleep(5000);

        assertEquals(2, list.size());
        logger.info("dispose");
        ksession.dispose();

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId,
                kbase, null, env);
        AuditLoggerFactory.newInstance(Type.JPA, ksession, null);

        final List<String> list2 = new ArrayList<String>();
        ksession.setGlobal("list", list2);

        ( (SingleSessionCommandService) ( (CommandBasedStatefulKnowledgeSession) ksession ).getRunner() ).getKieSession()
                .addEventListener(new TriggerRulesEventListener(ksession));

        ksession.fireAllRules();

        Thread.sleep(5000);

        assertEquals(3, list2.size());
    }


}
