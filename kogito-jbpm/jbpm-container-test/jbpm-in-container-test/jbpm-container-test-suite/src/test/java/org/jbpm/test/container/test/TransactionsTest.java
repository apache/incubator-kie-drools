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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.assertj.core.api.Assertions;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jbpm.test.container.JbpmContainerTest;
import org.jbpm.test.container.archive.LocalTransactions;
import org.jbpm.test.container.groups.EAP;
import org.jbpm.test.container.groups.EWS;
import org.jbpm.test.container.groups.WAS;
import org.jbpm.test.container.groups.WLS;
import org.jbpm.test.container.handlers.ListWorkItemHandler;
import org.jbpm.test.container.listeners.TrackingAgendaEventListener;
import org.jbpm.test.container.listeners.TrackingProcessEventListener;
import org.jbpm.test.container.tools.KieUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.command.KieCommands;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.persistence.jpa.KieStoreServices;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * jBPM transaction test. This test uses the transaction(s) provided by the container in
 * which the engine is running.
 *
 */
@Category({EAP.class, WLS.class, WAS.class, EWS.class})
public class TransactionsTest extends JbpmContainerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionsTest.class);

    private static final List<Resource> RESOURCES = new ArrayList<Resource>();

    private static LocalTransactions lt = new LocalTransactions();

    private static EntityManagerFactory emf;

    /*
     * Initialize the resource list.
     */
    static {
        RESOURCES.add(lt.getResource(LocalTransactions.BPMN_TRANSACTIONS));
        RESOURCES.add(lt.getResource(LocalTransactions.BPMN_HELLO_WORLD));
        RESOURCES.add(lt.getResource(LocalTransactions.RULES_TRANSACTIONS));
    }

    @Deployment(name = "LocalTransactions")
    @TargetsContainer(REMOTE_CONTAINER)
    public static Archive<?> deployLocalTransactions() {

        WebArchive war = lt.buildArchive();
        war.addClass(TransactionsTest.class);
        war.addClass(JbpmContainerTest.class);

        System.out.println("### Deploying war '" + war + "'");

        return war;
    }

    @Before
    public void createEMF() {
        emf = Persistence.createEntityManagerFactory("containerPU");
    }

    @After
    public void closeEMF() {
        if (emf != null) {
            emf.close();
        }
        emf = null;
    }

    // ---------------------------- Test Case Members

    @Test
    public void testStartProcessCommit() throws Exception {
        KieSession ksession = createJPASession(getKnowledgeBase());

        UserTransaction ut = getUserTransaction();
        ut.begin();

        long processId = ksession.startProcess(LocalTransactions.PROCESS_TRANSACTIONS).getId();
        Assertions.assertThat(ksession.getProcessInstance(processId)).isNotNull();
        Assertions.assertThat((long) ksession.getProcessInstance(processId).getState()).isEqualTo((long) ProcessInstance.STATE_ACTIVE);

        ut.commit();

        ksession = reloadSession(ksession);
        Assertions.assertThat(ksession.getProcessInstance(processId)).isNotNull();
        Assertions.assertThat((long) ksession.getProcessInstance(processId).getState()).isEqualTo((long) ProcessInstance.STATE_ACTIVE);
    }

    @Test
    public void testStartProcessRollback() throws Exception {
        KieSession ksession = createJPASession(getKnowledgeBase());

        UserTransaction ut = getUserTransaction();
        ut.begin();

        long processId = ksession.startProcess(LocalTransactions.PROCESS_TRANSACTIONS).getId();
        Assertions.assertThat(ksession.getProcessInstance(processId)).isNotNull();
        Assertions.assertThat((long) ksession.getProcessInstance(processId).getState()).isEqualTo((long) ProcessInstance.STATE_ACTIVE);

        ut.rollback();

        System.out.println(ksession.getIdentifier() + " " + ksession.toString());
        ksession = reloadSession(ksession);
        System.out.println(ksession.getIdentifier() + " " + ksession.toString());
        try {
            ProcessInstance pi = ksession.getProcessInstance(processId);
            Assertions.assertThat(pi).isNull();
        } catch (NullPointerException npe) {
            LOGGER.error("Non-XA database thrown NPE on process started before rollback", npe);
        }
    }

    @Test
    public void testAbortProcessCommit() throws Exception {
        KieSession ksession = createJPASession(getKnowledgeBase());
        long processId = ksession.startProcess(LocalTransactions.PROCESS_TRANSACTIONS).getId();
        TrackingProcessEventListener listener = new TrackingProcessEventListener();
        ksession.addEventListener(listener);
        Assertions.assertThat((long) ksession.getProcessInstance(processId).getState()).isEqualTo((long) ProcessInstance.STATE_ACTIVE);

        UserTransaction ut = getUserTransaction();
        ut.begin();

        ksession.abortProcessInstance(processId);

        ut.commit();

        ksession = reloadSession(ksession);
        Assertions.assertThat(ksession.getProcessInstance(processId)).isNull();
        Assertions.assertThat(listener.wasProcessAborted(LocalTransactions.PROCESS_TRANSACTIONS)).isTrue();
    }

    @Test
    public void testAbortProcessRollback() throws Exception {
        KieSession ksession = createJPASession(getKnowledgeBase());
        long processId = ksession.startProcess(LocalTransactions.PROCESS_TRANSACTIONS).getId();
        Assertions.assertThat(ksession.getProcessInstance(processId)).isNotNull();
        Assertions.assertThat((long) ksession.getProcessInstance(processId).getState()).isEqualTo((long) ProcessInstance.STATE_ACTIVE);

        UserTransaction ut = getUserTransaction();
        ut.begin();

        ksession.abortProcessInstance(processId);

        ut.rollback();
        ksession = reloadSession(ksession);

        Assertions.assertThat(ksession.getProcessInstance(processId)).isNotNull();
        Assertions.assertThat((long) ksession.getProcessInstance(processId).getState()).isEqualTo((long) ProcessInstance.STATE_ACTIVE);
    }

    @Test
    public void testScript() throws Exception {
        TrackingProcessEventListener process = new TrackingProcessEventListener();
        KieSession ksession = createJPASession(getKnowledgeBase());
        ksession.addEventListener(process);

        long processId = ksession.startProcess(LocalTransactions.PROCESS_TRANSACTIONS).getId();

        UserTransaction ut = getUserTransaction();
        ut.begin();

        ksession.signalEvent("start", "script", processId);
        Assertions.assertThat(process.wasNodeLeft("script")).isTrue();

        ut.rollback();

        process.clear();
        ksession = reloadSession(ksession);
        ksession.addEventListener(process);

        ut = getUserTransaction();
        ut.begin();

        ksession.signalEvent("start", "script", processId);
        Thread.sleep(1000);

        Assertions.assertThat((long) ut.getStatus()).isEqualTo((long) Status.STATUS_ACTIVE);
        ut.commit();

        Assertions.assertThat(process.wasNodeLeft("script")).isTrue();
        ksession.signalEvent("finish", null, processId);
        Thread.sleep(1000);

        Assertions.assertThat(process.wasProcessCompleted(LocalTransactions.PROCESS_TRANSACTIONS)).isTrue();
    }

    @Test
    public void testRuleflowGroup() throws Exception {
        TrackingProcessEventListener process = new TrackingProcessEventListener();
        TrackingAgendaEventListener agenda = new TrackingAgendaEventListener();
        KieSession ksession = createJPASession(getKnowledgeBase());
        ksession.addEventListener(process);
        ksession.addEventListener(agenda);

        long processId = ksession.startProcess(LocalTransactions.PROCESS_TRANSACTIONS).getId();

        UserTransaction ut = getUserTransaction();
        ut.begin();

        ksession.signalEvent("start", "rfg", processId);
        Assertions.assertThat(process.wasNodeLeft("rfg")).isTrue();

        ut.rollback();
        Thread.sleep(600);

        process.clear();
        agenda.clear();
        ksession = reloadSession(ksession);
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
        ksession.fireAllRules();
        ksession.signalEvent("finish", null, processId);
        Thread.sleep(1000);

        Assertions.assertThat(agenda.isRuleFired("dummyRule")).isTrue();
        Assertions.assertThat(process.wasProcessCompleted(LocalTransactions.PROCESS_TRANSACTIONS)).isTrue();
    }

    @Test
    public void testTimer() throws Exception {
        TrackingProcessEventListener process = new TrackingProcessEventListener();
        KieSession ksession = createJPASession(getKnowledgeBase());
        ksession.addEventListener(process);

        long processId = ksession.startProcess(LocalTransactions.PROCESS_TRANSACTIONS).getId();

        UserTransaction ut = getUserTransaction();
        ut.begin();

        ksession.signalEvent("start", "timer", processId);
        Assertions.assertThat(process.wasNodeLeft("timer")).isTrue();

        ut.rollback();

        Thread.sleep(600);

        process.clear();
        ksession = reloadSession(ksession);
        ksession.addEventListener(process);

        ut = getUserTransaction();
        ut.begin();

        ksession.signalEvent("start", "timer", processId);
        ut.commit();

        Thread.sleep(1000);

        Assertions.assertThat(process.wasNodeLeft("timer")).isTrue();
        ksession.signalEvent("finish", null, processId);

        Assertions.assertThat(process.wasProcessCompleted(LocalTransactions.PROCESS_TRANSACTIONS)).isTrue();
    }

    @Test
    public void testUsertask() throws Exception {
        TrackingProcessEventListener process = new TrackingProcessEventListener();
        KieSession ksession = createJPASession(getKnowledgeBase());
        ksession.addEventListener(process);
        ListWorkItemHandler handler = new ListWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);

        long processId = ksession.startProcess(LocalTransactions.PROCESS_TRANSACTIONS).getId();

        UserTransaction ut = getUserTransaction();
        ut.begin();

        ksession.signalEvent("start", "usertask", processId);
        Assertions.assertThat(process.wasNodeLeft("usertask")).isTrue();
        Assertions.assertThat((long) handler.getWorkItems().size()).isEqualTo((long) 1);

        ut.rollback();

        // human tasks are not aborted (as that would not cause the task to be
        // cancelled
        process.clear();
        ksession = reloadSession(ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        ksession.addEventListener(process);

        ut = getUserTransaction();
        ut.begin();

        ksession.signalEvent("start", "usertask", processId);

        ut.commit();

        Thread.sleep(1000);
        Assertions.assertThat((long) handler.getWorkItems().size()).isEqualTo((long) 2);

        Assertions.assertThat((long) handler.getWorkItems().size()).isEqualTo((long) 2);
        Assertions.assertThat(process.wasNodeLeft("usertask")).isTrue();
        Assertions.assertThat(process.wasNodeTriggered("User Task")).isTrue();
        Assertions.assertThat(process.wasNodeLeft("User Task")).isFalse();
        Assertions.assertThat(process.wasProcessCompleted(LocalTransactions.PROCESS_TRANSACTIONS)).isFalse();

    }

    @Test
    public void testForloop() throws Exception {
        TrackingProcessEventListener process = new TrackingProcessEventListener();
        KieSession ksession = createJPASession(getKnowledgeBase());
        ksession.addEventListener(process);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("collection", Arrays.asList("hello world", "25", "false", "1234567891011121314151617181920", ""));
        long processId = ksession.startProcess(LocalTransactions.PROCESS_TRANSACTIONS, params).getId();

        UserTransaction ut = getUserTransaction();
        ut.begin();

        ksession.signalEvent("start", "forloop", processId);
        Thread.sleep(1000);
        Assertions.assertThat(process.wasNodeLeft("forloop")).isTrue();

        ut.rollback();

        process.clear();
        ksession = reloadSession(ksession);
        ksession.addEventListener(process);

        ut = getUserTransaction();
        ut.begin();

        ksession.signalEvent("start", "forloop", processId);

        ut.commit();

        Thread.sleep(5000);

        Assertions.assertThat(process.wasNodeLeft("forloop")).isTrue();
        Assertions.assertThat(process.wasNodeLeft("Multiple Instances")).isTrue();
        Assertions.assertThat(process.wasProcessCompleted(LocalTransactions.PROCESS_TRANSACTIONS)).isFalse();

    }

    @Test
    public void testEmbedded() throws Exception {
        TrackingProcessEventListener process = new TrackingProcessEventListener();
        KieSession ksession = createJPASession(getKnowledgeBase());
        ksession.addEventListener(process);

        long processId = ksession.startProcess(LocalTransactions.PROCESS_TRANSACTIONS).getId();

        UserTransaction ut = getUserTransaction();
        ut.begin();

        ksession.signalEvent("start", "embedded", processId);
        Thread.sleep(1000);
        Assertions.assertThat(process.wasNodeLeft("embedded")).isTrue();

        ut.rollback();

        process.clear();
        ksession = reloadSession(ksession);
        ksession.addEventListener(process);

        ut = getUserTransaction();
        ut.begin();

        ksession.signalEvent("start", "embedded", processId);

        ut.commit();

        Thread.sleep(5000);

        Assertions.assertThat(process.wasNodeLeft("embedded")).isTrue();
        Assertions.assertThat(process.wasProcessCompleted(LocalTransactions.PROCESS_TRANSACTIONS)).isFalse();

    }

    // ---------------------------- Private Members

    private Environment getEnvironment() {
        if (emf == null) {
            throw new IllegalStateException("Uninitialised EntityManagerFactory");
        }

        Environment env = KieServices.get().newEnvironment();
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
        return env;
    }

    private KieSession createJPASession(KieBase kbase) {
        try {
            return getStore().newKieSession(kbase, null, getEnvironment());
        } catch (Exception e) {
            System.out.println("<<<<<<<< Printing stackTrace >>>>>>>>");
            e.printStackTrace();
            System.out.println(e.getCause());
            throw e;
        }
    }

    private KieSession loadJPASession(KieBase kbase, long sessionId) {
        return getStore().loadKieSession(sessionId, kbase, null, getEnvironment());
    }

    private KieSession reloadSession(KieSession ksession) {
        long id = ksession.getIdentifier();
        KieBase kbase = ksession.getKieBase();
        ksession.dispose();

        return loadJPASession(kbase, id);
    }

    protected static KieServices getServices() {
        return KieServices.Factory.get();
    }

    protected static KieResources getResources() {
        return getServices().getResources();
    }

    private static KieStoreServices getStore() {
        return getServices().getStoreServices();
    }

    protected static KieCommands getCommands() {
        return getServices().getCommands();
    }

    private static KieBase getKieBase(KieBuilder kbuilder) {
        return getServices().newKieContainer(kbuilder.getKieModule().getReleaseId()).getKieBase();
    }

    private KieBase getKnowledgeBase() {
        return getKieBase(KieUtils.newKieBuilder(RESOURCES));
    }

    private static UserTransaction getUserTransaction() {
        try {
            return (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        } catch (NamingException ex) {
            throw new RuntimeException("Failed to lookup transaction", ex);
        }
    }

}