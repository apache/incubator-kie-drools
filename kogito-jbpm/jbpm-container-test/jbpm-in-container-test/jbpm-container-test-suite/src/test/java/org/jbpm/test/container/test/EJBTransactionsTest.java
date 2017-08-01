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

package org.jbpm.test.container.test;

import java.util.Map;

import org.assertj.core.api.Assertions;
import org.jbpm.test.container.AbstractEJBTransactionsTest;
import org.jbpm.test.container.archive.EJBTransactions;
import org.jbpm.test.container.archive.ejbtransactions.ProcessEJB;
import org.jbpm.test.container.archive.ejbtransactions.ProcessScenario;
import org.jbpm.test.container.groups.EAP;
import org.jbpm.test.container.groups.WAS;
import org.jbpm.test.container.groups.WLS;
import org.jbpm.test.container.listeners.TrackingProcessEventListener;
import org.jbpm.test.container.tools.TrackingListenerAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

/**
 * Basic testing of process with 2 user tasks in container.
 */
@Category({EAP.class, WLS.class, WAS.class})
public class EJBTransactionsTest extends AbstractEJBTransactionsTest {

    private KieBase kbase;

    @Before
    public void setUp() {
        kbase = getKBase(et.getResource(EJBTransactions.BPMN_DOUBLE_HUMAN_TASKS));
    }

    @Test
    public void testSTFBMT() {
        startProcess(getStatefulBMT());
    }

    @Test
    public void testSTFCMT() {
        startProcess(getStatefulCMT());
    }

    @Test
    public void testSTLBMT() {
        startProcess(getStatelessBMT());
    }

    @Test
    public void testSTLCMT() {
        startProcess(getStatelessCMT());
    }

    private void startProcess(ProcessEJB ejb) {
        TrackingProcessEventListener listener = new TrackingProcessEventListener();
        final TestWorkItemHandler wih = new TestWorkItemHandler();
        Assertions.assertThat(kbase).isNotNull();

        ProcessScenario scenario = new ProcessScenario(kbase, EJBTransactions.PROCESS_DOUBLE_HUMAN_TASKS, null, listener) {

            @Override
            protected void runScenario(String procId, Map<String, Object> params, KieSession ksession) {
                ksession.getWorkItemManager().registerWorkItemHandler("Human Task", wih);
                ksession.startProcess(procId, params);
                // first task
                ksession.getWorkItemManager().completeWorkItem(wih.getWorkItem().getId(), null);
                // second task
                ksession.getWorkItemManager().completeWorkItem(wih.getWorkItem().getId(), null);
            }

        };

        ejb.startProcess(scenario);

        if (scenario.hasErrors()) {
            throw new RuntimeException(scenario.getErrors().get(0));
        }

        TrackingListenerAssert.assertProcessStarted(listener, EJBTransactions.PROCESS_DOUBLE_HUMAN_TASKS);
        TrackingListenerAssert.assertTriggeredAndLeft(listener, "Upload Form");
        TrackingListenerAssert.assertTriggeredAndLeft(listener, "Approve It");
        TrackingListenerAssert.assertProcessCompleted(listener, EJBTransactions.PROCESS_DOUBLE_HUMAN_TASKS);
    }

    class TestWorkItemHandler implements WorkItemHandler {

        private WorkItem wi;

        public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
            this.wi = wi;
        }

        public void abortWorkItem(WorkItem wi, WorkItemManager wim) {

        }

        public WorkItem getWorkItem() {
            return this.wi;
        }
    }
}
