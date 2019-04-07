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

package org.jbpm.test.functional.async;

import org.assertj.core.api.Assertions;
import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.test.JbpmTestCase;
import org.jbpm.test.wih.FirstErrorWorkItemHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.executor.ExecutorService;

/**
 * process1: start -> catch signal -> first time exception -> end process2:
 * start -> async throw signal -> end --- should repeat when fails
 */
public class AsyncThrowSignalTest extends JbpmTestCase {

    private static Object LOCK = new Object();

    private static final String PROCESS_ATS = "org.jbpm.test.functional.async.AsyncThrowSignal";
    private static final String PROCESS_AICS = "org.jbpm.test.functional.async.AsyncIntermediateCatchSignal";
    private static final String BPMN_ATS = "org/jbpm/test/functional/async/AsyncThrowSignal.bpmn2";
    private static final String BPMN_AICS = "org/jbpm/test/functional/async/AsyncIntermediateCatchSignal.bpmn2";

    private ExecutorService executorService;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        executorService = ExecutorServiceFactory.newExecutorService(getEmf());
        executorService.setInterval(1);
        executorService.init();
        addEnvironmentEntry("ExecutorService", executorService);
        addWorkItemHandler("SyncError", new FirstErrorWorkItemHandler());
        addProcessEventListener(new DefaultProcessEventListener() {
            @Override
            public void afterProcessCompleted(ProcessCompletedEvent event) {
                synchronized (LOCK) {
                    LOCK.notifyAll();
                }
            }
        });
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();

        executorService.destroy();
    }

    @Test(timeout = 10000)
    public void testCorrectProcessStateAfterExceptionThrowSignal() {
        KieSession ksession = createKSession(BPMN_AICS, BPMN_ATS);
        ProcessInstance pi1 = ksession.startProcess(PROCESS_AICS, null);
        long pid1 = pi1.getId();

        ProcessInstance pi2 = ksession.startProcess(PROCESS_ATS, null);
        long pid2 = pi2.getId();

        synchronized (LOCK) {
            try {
                LOCK.wait();
            } catch (InterruptedException e) {
            }
        }

        pi1 = ksession.getProcessInstance(pid1);
        Assertions.assertThat(pi1).isNull();
        pi2 = ksession.getProcessInstance(pid2);
        Assertions.assertThat(pi2).isNull();
    }

}
