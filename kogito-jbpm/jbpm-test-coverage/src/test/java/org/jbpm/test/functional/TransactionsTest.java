/*
 * Copyright 2015 JBoss Inc
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

package org.jbpm.test.functional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.naming.InitialContext;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.assertj.core.api.Assertions;
import org.jbpm.test.JbpmTestCase;
import org.jbpm.test.listener.TrackingAgendaEventListener;
import org.jbpm.test.listener.TrackingProcessEventListener;
import org.jbpm.test.wih.ListWorkItemHandler;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;

public class TransactionsTest extends JbpmTestCase {

    private static final String TRANSACTIONS = "org/jbpm/test/functional/Transactions.bpmn";
    private static final String TRANSACTIONS_ID = "org.jbpm.test.functional.Transactions";
    private static final String TRANSACTIONS_DRL = "org/jbpm/test/functional/Transactions.drl";

    private static final String HELLO_WORLD = "org/jbpm/test/functional/common/HelloWorldProcess1.bpmn";

    private KieSession ksession;
    private Map<String, ResourceType> resources;

    @Before
    public void init() throws Exception {
        resources = new HashMap<String, ResourceType>();
        resources.put(TRANSACTIONS, ResourceType.BPMN2);
        resources.put(HELLO_WORLD, ResourceType.BPMN2);
        resources.put(TRANSACTIONS_DRL, ResourceType.DRL);
        ksession = createKSession(resources);
    }

    @Test(timeout = 60000)
    public void testStartProcessCommit() throws Exception {
        UserTransaction ut = getUserTransaction();
        ut.begin();

        long processId = startProcess(ksession);
        Assertions.assertThat(ksession.getProcessInstance(processId)).isNotNull();
        Assertions.assertThat(ksession.getProcessInstance(processId).getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        ut.commit();

        ksession = restoreKSession(resources);
        assertProcessInstanceActive(processId);
    }

    @Test(timeout = 60000)
    public void testStartProcessRollback() throws Exception {
        UserTransaction ut = getUserTransaction();
        ut.begin();

        long processId = startProcess(ksession);
        assertProcessInstanceActive(processId);

        ut.rollback();

        System.out.println(ksession.getId() + " " + ksession.toString());
        ksession = restoreKSession(resources);
        System.out.println(ksession.getId() + " " + ksession.toString());
        try {
            ProcessInstance pi = ksession.getProcessInstance(processId);
            Assertions.assertThat(pi).isNull();
        } catch (NullPointerException npe) {
            logger.error("Non-XA database thrown NPE on process started before rollback", npe);
        }
    }

    @Test(timeout = 60000)
    public void testAbortProcessCommit() throws Exception {
        long processId = startProcess(ksession);
        TrackingProcessEventListener listener = new TrackingProcessEventListener();
        ksession.addEventListener(listener);
        assertProcessInstanceActive(processId);

        UserTransaction ut = getUserTransaction();
        ut.begin();

        ksession.abortProcessInstance(processId);

        ut.commit();

        ksession = restoreKSession(resources);
        assertProcessInstanceAborted(processId);
    }

    @Test(timeout = 60000)
    public void testAbortProcessRollback() throws Exception {
        long processId = startProcess(ksession);
        assertProcessInstanceActive(processId);

        UserTransaction ut = getUserTransaction();
        ut.begin();

        ksession.abortProcessInstance(processId);

        ut.rollback();
        ksession = restoreKSession(resources);

        assertProcessInstanceActive(processId);
    }

    @Test(timeout = 60000)
    public void testScript() throws Exception {
        TrackingProcessEventListener process = new TrackingProcessEventListener();
        ksession.addEventListener(process);

        long processId = startProcess(ksession);

        UserTransaction ut = getUserTransaction();
        ut.begin();

        ksession.signalEvent("start", "script", processId);
        Assertions.assertThat(process.wasNodeLeft("script")).isTrue();

        ut.rollback();

        process.clear();
        ksession = restoreKSession(resources);
        ksession.addEventListener(process);

        ut = getUserTransaction();
        ut.begin();

        ksession.signalEvent("start", "script", processId);
        Thread.sleep(1000);

        Assertions.assertThat(ut.getStatus()).isEqualTo(Status.STATUS_ACTIVE);
        ut.commit();

        Assertions.assertThat(process.wasNodeLeft("script")).isTrue();
        ksession.signalEvent("finish", null, processId);
        Thread.sleep(1000);

        assertProcessInstanceCompleted(processId);
    }

    @Test(timeout = 60000)
    public void testRuleflowGroup() throws Exception {
        TrackingProcessEventListener process = new TrackingProcessEventListener();
        TrackingAgendaEventListener agenda = new TrackingAgendaEventListener();
        ksession.addEventListener(process);
        ksession.addEventListener(agenda);

        long processId = startProcess(ksession);

        UserTransaction ut = getUserTransaction();
        ut.begin();

        ksession.signalEvent("start", "rfg", processId);
        Assertions.assertThat(process.wasNodeLeft("rfg")).isTrue();

        ut.rollback();
        Thread.sleep(600);

        process.clear();
        agenda.clear();
        ksession = restoreKSession(resources);
        ksession.addEventListener(process);
        ksession.addEventListener(agenda);

        ksession.fireAllRules();
        Assertions.assertThat(agenda.isRuleFired("dummyRule")).isFalse();
        agenda.clear();
        process.clear();

        ut = getUserTransaction();
        ut.begin();

        ksession.signalEvent("start", "rfg", processId);
        Thread.sleep(1000);

        ut.commit();

        Assertions.assertThat(process.wasNodeLeft("rfg")).isTrue();
        ksession.signalEvent("finish", null, processId);
        ksession.fireAllRules();
        Thread.sleep(1000);

        Assertions.assertThat(agenda.isRuleFired("dummyRule")).isTrue();
        assertProcessInstanceCompleted(processId);
    }

    @Test(timeout = 60000)
    public void testTimer() throws Exception {
        TrackingProcessEventListener process = new TrackingProcessEventListener();
        ksession.addEventListener(process);

        long processId = startProcess(ksession);

        UserTransaction ut = getUserTransaction();
        ut.begin();

        ksession.signalEvent("start", "timer", processId);
        Assertions.assertThat(process.wasNodeLeft("timer")).isTrue();

        ut.rollback();

        Thread.sleep(600);

        process.clear();
        ksession = restoreKSession(resources);
        ksession.addEventListener(process);

        ut = getUserTransaction();
        ut.begin();

        ksession.signalEvent("start", "timer", processId);
        ut.commit();

        Thread.sleep(1000);

        Assertions.assertThat(process.wasNodeLeft("timer")).isTrue();
        ksession.signalEvent("finish", null, processId);

        assertProcessInstanceCompleted(processId);
    }

    @Test(timeout = 60000)
    public void testUsertask() throws Exception {
        TrackingProcessEventListener process = new TrackingProcessEventListener();
        ksession.addEventListener(process);
        ListWorkItemHandler handler = new ListWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);

        long processId = startProcess(ksession);

        UserTransaction ut = getUserTransaction();
        ut.begin();

        ksession.signalEvent("start", "usertask", processId);
        Assertions.assertThat(process.wasNodeLeft("usertask")).isTrue();
        Assertions.assertThat(handler.getWorkItems()).hasSize(1);

        ut.rollback();

        // human tasks are not aborted (as that would not cause the task to be cancelled
        process.clear();
        ksession = restoreKSession(resources);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        ksession.addEventListener(process);

        ut = getUserTransaction();
        ut.begin();

        ksession.signalEvent("start", "usertask", processId);

        ut.commit();

        Thread.sleep(1000);
        Assertions.assertThat(handler.getWorkItems()).hasSize(2);
        Assertions.assertThat(process.wasNodeLeft("usertask")).isTrue();
        Assertions.assertThat(process.wasNodeTriggered("User Task")).isTrue();
        Assertions.assertThat(process.wasNodeLeft("User Task")).isFalse();
        Assertions.assertThat(process.wasProcessCompleted("transactions")).isFalse();
    }

    @Test(timeout = 60000)
    public void testForLoop() throws Exception {
        TrackingProcessEventListener process = new TrackingProcessEventListener();
        ksession.addEventListener(process);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("collection", Arrays.asList("hello world", "25", "false", "1234567891011121314151617181920", ""));
        long processId = ksession.startProcess(TRANSACTIONS_ID, params).getId();

        UserTransaction ut = getUserTransaction();
        ut.begin();

        ksession.signalEvent("start", "forloop", processId);
        Thread.sleep(1000);
        Assertions.assertThat(process.wasNodeLeft("forloop")).isTrue();

        ut.rollback();

        process.clear();
        ksession = restoreKSession(resources);
        ksession.addEventListener(process);

        ut = getUserTransaction();
        ut.begin();

        ksession.signalEvent("start", "forloop", processId);

        ut.commit();

        Thread.sleep(5000);

        Assertions.assertThat(process.wasNodeLeft("forloop")).isTrue();
        Assertions.assertThat(process.wasNodeLeft("Multiple Instances")).isTrue();
        Assertions.assertThat(process.wasProcessCompleted("transactions")).isFalse();

    }

    @Test(timeout = 60000)
    public void testEmbedded() throws Exception {
        TrackingProcessEventListener process = new TrackingProcessEventListener();
        ksession.addEventListener(process);

        long processId = startProcess(ksession);

        UserTransaction ut = getUserTransaction();
        ut.begin();

        ksession.signalEvent("start", "embedded", processId);
        Thread.sleep(1000);
        Assertions.assertThat(process.wasNodeLeft("embedded")).isTrue();

        ut.rollback();

        process.clear();
        ksession = restoreKSession(resources);
        ksession.addEventListener(process);

        ut = getUserTransaction();
        ut.begin();

        ksession.signalEvent("start", "embedded", processId);

        ut.commit();

        Thread.sleep(5000);

        Assertions.assertThat(process.wasNodeLeft("embedded")).isTrue();
        Assertions.assertThat(process.wasProcessCompleted("transactions")).isFalse();

    }

    private long startProcess(KieSession ksession) {
        return startProcess(ksession, null);
    }

    private long startProcess(KieSession ksession, String nodeType) {
        ProcessInstance pi = ksession.startProcess(TRANSACTIONS_ID);

        if (nodeType != null) {
            ksession.signalEvent("start", nodeType);
        }

        return pi.getId();
    }

    private UserTransaction getUserTransaction() throws Exception {
        UserTransaction tx = InitialContext.doLookup("java:comp/UserTransaction");
        return tx;
    }

    public KieSession restoreKSession(Map<String, ResourceType> res) {
        disposeRuntimeManager();
        createRuntimeManager(res);
        return getRuntimeEngine().getKieSession();
    }

}
