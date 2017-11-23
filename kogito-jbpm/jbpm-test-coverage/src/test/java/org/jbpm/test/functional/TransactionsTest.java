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

import static org.junit.Assert.*;

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
        long processId;
        try {
            ut.begin();

            processId = startProcess(ksession);
            Assertions.assertThat(ksession.getProcessInstance(processId)).isNotNull();
            Assertions.assertThat(ksession.getProcessInstance(processId).getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);

            ut.commit();
        } catch (Exception ex) {
            ut.rollback();
            throw ex;
        }

        ksession = restoreKSession(resources);
        assertProcessInstanceActive(processId);
    }

    @Test(timeout = 60000)
    public void testStartProcessRollback() throws Exception {
        UserTransaction ut = getUserTransaction();
        long processId;
        try {
            ut.begin();

            processId = startProcess(ksession);
            assertProcessInstanceActive(processId);
        } finally {
            ut.rollback();
        }

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
        try {
            ut.begin();
            ksession.abortProcessInstance(processId);
            ut.commit();
        } catch (Exception ex) {
            ut.rollback();
            throw ex;
        }

        ksession = restoreKSession(resources);
        assertProcessInstanceAborted(processId);
    }

    @Test(timeout = 60000)
    public void testAbortProcessRollback() throws Exception {
        long processId = startProcess(ksession);
        assertProcessInstanceActive(processId);

        UserTransaction ut = getUserTransaction();
        try {
            ut.begin();
            ksession.abortProcessInstance(processId);
        } finally {
            ut.rollback();
        }

        ksession = restoreKSession(resources);
        assertProcessInstanceActive(processId);
    }

    @Test(timeout = 60000)
    public void testScript() throws Exception {
        TrackingProcessEventListener process = new TrackingProcessEventListener(false);
        ksession.addEventListener(process);

        long processId = startProcess(ksession);

        UserTransaction ut = getUserTransaction();
        try {
            ut.begin();

            ksession.signalEvent("start", "script", processId);
            Assertions.assertThat(process.wasNodeLeft("script")).isTrue();
        } finally {
            ut.rollback();
        }

        process.clear();
        ksession = restoreKSession(resources);
        ksession.addEventListener(process);

        String scriptNodeName = "script";
        ut = getUserTransaction();
        try {
            ut.begin();

            ksession.signalEvent("start", "script", processId);
            assertTrue( "Node '" +  scriptNodeName + "' was not left on time!", process.waitForNodeToBeLeft(scriptNodeName, 1000));

            Assertions.assertThat(ut.getStatus()).isEqualTo(Status.STATUS_ACTIVE);
            ut.commit();
        } catch (Exception ex) {
            ut.rollback();
            throw ex;
        }

        Assertions.assertThat(process.wasNodeLeft(scriptNodeName)).isTrue();
        ksession.signalEvent("finish", null, processId);
        
        assertTrue( "Process was not completed on time!", process.waitForProcessToComplete(1000));
        assertProcessInstanceCompleted(processId);
    }

    @Test(timeout = 60000)
    public void testRuleflowGroup() throws Exception {
        TrackingProcessEventListener process = new TrackingProcessEventListener(false);
        TrackingAgendaEventListener agenda = new TrackingAgendaEventListener();
        ksession.addEventListener(process);
        ksession.addEventListener(agenda);

        long processId = startProcess(ksession);

        UserTransaction ut = getUserTransaction();
        try {
            ut.begin();

            ksession.signalEvent("start", "rfg", processId);
            Assertions.assertThat(process.wasNodeLeft("rfg")).isTrue();
        } finally {
            ut.rollback();
        }

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

        String ruleFlowGroupNodeName = "rfg";
        ut = getUserTransaction();
        try {
            ut.begin();

            ksession.signalEvent("start", "rfg", processId);
            assertTrue( "Node '" + ruleFlowGroupNodeName + "' was not left on time!", process.waitForNodeToBeLeft(ruleFlowGroupNodeName, 1000));

            ut.commit();
        } catch (Exception ex) {
            ut.rollback();
            throw ex;
        }

        Assertions.assertThat(process.wasNodeLeft(ruleFlowGroupNodeName)).isTrue();
        ksession.signalEvent("finish", null, processId);
        ksession.fireAllRules();

        assertTrue( "Process did not complete on time!", process.waitForProcessToComplete(1000));
        Assertions.assertThat(agenda.isRuleFired("dummyRule")).isTrue();
        assertProcessInstanceCompleted(processId);
    }

    @Test
    public void testTimer() throws Exception {
        TrackingProcessEventListener process = new TrackingProcessEventListener();
        ksession.addEventListener(process);

        long processId = startProcess(ksession);

        UserTransaction ut = getUserTransaction();
        try {
            ut.begin();

            ksession.signalEvent("start", "timer", processId);
            Assertions.assertThat(process.wasNodeLeft("timer")).isTrue();
        } finally {
            ut.rollback();
        }

        Thread.sleep(600);

        process.clear();
        ksession = restoreKSession(resources);
        ksession.addEventListener(process);

        ut = getUserTransaction();
        try {
            ut.begin();

            ksession.signalEvent("start", "timer", processId);
            ut.commit();
        } catch (Exception ex) {
            ut.rollback();
            throw ex;
        }

        String timerNodeName = "timer";
        assertTrue( "Node '" + timerNodeName + "' was not left on time!", process.waitForNodeToBeLeft(timerNodeName, 1500));
        Assertions.assertThat(process.wasNodeLeft(timerNodeName)).isTrue();
       
        String finishScriptNodeName = "Finish-Script";
        assertTrue( "Node '" + finishScriptNodeName + "' was not triggered on time!", process.waitForNodeTobeTriggered(finishScriptNodeName, 1500));
        
        ksession.signalEvent("finish", null, processId);

        assertTrue( "Process did not complete on time!", process.waitForProcessToComplete(1500));
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
        try {
            ut.begin();

            ksession.signalEvent("start", "usertask", processId);
            Assertions.assertThat(process.wasNodeLeft("usertask")).isTrue();
            Assertions.assertThat(handler.getWorkItems()).hasSize(1);
        } finally {
            ut.rollback();
        }

        // human tasks are not aborted (as that would not cause the task to be cancelled
        process.clear();
        ksession = restoreKSession(resources);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        ksession.addEventListener(process);

        ut = getUserTransaction();
        try {
            ut.begin();

            ksession.signalEvent("start", "usertask", processId);

            ut.commit();
        } catch (Exception ex) {
            ut.rollback();
            throw ex;
        }

        String lastUserTaskNodeName = "User Task";
        assertTrue( "Node '" + lastUserTaskNodeName + "' was not left on time!", process.waitForNodeTobeTriggered(lastUserTaskNodeName, 1000));
        
        Assertions.assertThat(handler.getWorkItems()).hasSize(2);
        Assertions.assertThat(process.wasNodeLeft("usertask")).isTrue();
        Assertions.assertThat(process.wasNodeTriggered(lastUserTaskNodeName)).isTrue();
        
        Assertions.assertThat(process.wasNodeLeft(lastUserTaskNodeName)).isFalse();
        Assertions.assertThat(process.wasProcessCompleted("transactions")).isFalse();
    }

    @Test(timeout = 60000)
    public void testForLoop() throws Exception {
        TrackingProcessEventListener process = new TrackingProcessEventListener(false);
        ksession.addEventListener(process);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("collection", Arrays.asList("hello world", "25", "false", "1234567891011121314151617181920", ""));
        long processId = ksession.startProcess(TRANSACTIONS_ID, params).getId();

        String forLoopNodeName = "forloop";
        UserTransaction ut = getUserTransaction();
        try {
            ut.begin();

            ksession.signalEvent("start", forLoopNodeName, processId);
            assertTrue( "Node '" + forLoopNodeName + "' was not left on time!", process.waitForNodeToBeLeft(forLoopNodeName, 1000));
            Assertions.assertThat(process.wasNodeLeft(forLoopNodeName)).isTrue();
        } finally {
            ut.rollback();
        }

        process.clear();
        ksession = restoreKSession(resources);
        ksession.addEventListener(process);

        ut = getUserTransaction();
        try {
            ut.begin();

            ksession.signalEvent("start", "forloop", processId);

            ut.commit();
        } catch (Exception ex) {
            ut.rollback();
            throw ex;
        }

        String multipleInstancesNode = "Multiple Instances";
        assertTrue( "Process did not complete on time!", process.waitForNodeToBeLeft(multipleInstancesNode, 1000));
        
        Assertions.assertThat(process.wasNodeLeft(forLoopNodeName)).isTrue();
        Assertions.assertThat(process.wasNodeLeft(multipleInstancesNode)).isTrue();
        Assertions.assertThat(process.wasProcessCompleted("transactions")).isFalse();
    }

    @Test(timeout = 60000)
    public void testEmbedded() throws Exception {
        TrackingProcessEventListener process = new TrackingProcessEventListener(false);
        ksession.addEventListener(process);

        long processId = startProcess(ksession);

        UserTransaction ut = getUserTransaction();
        try {
            ut.begin();

            ksession.signalEvent("start", "embedded", processId);
            assertTrue( "Node 'embedded' was not left on time!", process.waitForNodeToBeLeft("embedded", 1000));
            Assertions.assertThat(process.wasNodeLeft("embedded")).isTrue();
        } finally {
            ut.rollback();
        }

        process.clear();
        ksession = restoreKSession(resources);
        ksession.addEventListener(process);

        ut = getUserTransaction();
        try {
            ut.begin();

            ksession.signalEvent("start", "embedded", processId);

            ut.commit();
        } catch (Exception ex) {
            ut.rollback();
            throw ex;
        }

        assertTrue( "Node 'embedded' was not left on time!", process.waitForNodeToBeLeft("embedded", 1000));

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
