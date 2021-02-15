/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.test.util;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.jbpm.process.instance.impl.util.LoggingPrintStream;
import org.jbpm.process.test.TestProcessEventListener;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.kie.api.KieBase;
import org.kie.api.definition.process.Process;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractBaseTest {

    protected Logger logger;

    @BeforeEach
    public void before(TestInfo testInfo) {
        addLogger();
        logger.debug( "> " + testInfo.getDisplayName() );
    }
   
    public abstract void addLogger();
    
    protected static AtomicInteger uniqueIdGen = new AtomicInteger(0);

    public KieSession createKieSession(Process... process) {
        KieBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        for (Process processToAdd : process) {
            ((KnowledgeBaseImpl) kbase).addProcess(processToAdd);
        }
        return kbase.newKieSession();
    }

    public void showEventHistory(KieSession ksession) {
        TestProcessEventListener procEventListener = (TestProcessEventListener) ksession.getProcessEventListeners().iterator().next();
        for (String event : procEventListener.getEventHistory()) {
            System.out.println("\"" + event + "\",");
        }
    }

    public void verifyEventHistory(String[] eventOrder, List<String> eventHistory) {
        int max = eventOrder.length > eventHistory.size() ? eventOrder.length : eventHistory.size();
        logger.debug("{} | {}", "EXPECTED", "TEST" );
        for (int i = 0; i < max; ++i) {
            String expected = "", real = "";
            if (i < eventOrder.length) {
                expected = eventOrder[i];
            }
            if (i < eventHistory.size()) {
                real = eventHistory.get(i);
            }
            logger.debug("{} | {}", expected, real);
            assertEquals(expected, real, "Mismatch in expected event");
        }
        assertEquals(eventOrder.length, eventHistory.size(), "Mismatch in number of events expected.");
    }

    @BeforeAll
    public static void configure() {
        LoggingPrintStream.interceptSysOutSysErr();
    }

    @AfterAll
    public static void reset() {
        LoggingPrintStream.resetInterceptSysOutSysErr();
    }
}
