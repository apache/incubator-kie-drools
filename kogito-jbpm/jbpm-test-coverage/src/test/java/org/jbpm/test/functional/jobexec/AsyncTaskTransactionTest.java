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

package org.jbpm.test.functional.jobexec;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.assertj.core.api.Assertions;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.executor.impl.wih.AsyncWorkItemHandler;
import org.jbpm.persistence.util.PersistenceUtil;
import org.jbpm.test.JbpmAsyncJobTestCase;
import org.jbpm.test.listener.CountDownAsyncJobListener;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.query.QueryContext;

import static org.junit.Assert.*;

public class AsyncTaskTransactionTest extends JbpmAsyncJobTestCase {

    public static final String ASYNC_DATA_EXECUTOR = "org/jbpm/test/functional/jobexec/AsyncDataExecutor.bpmn2";
    public static final String ASYNC_DATA_EXECUTOR_ID = "org.jbpm.test.functional.jobexec.AsyncDataExecutor";

    public static final String ASYNC_EXECUTOR_2 = "org/jbpm/test/functional/jobexec/AsyncExecutor2.bpmn2";
    public static final String ASYNC_EXECUTOR_2_ID = "org.jbpm.test.functional.jobexec.AsyncExecutor2";

    public static final String USER_COMMAND = "org.jbpm.test.jobexec.UserCommand";
    public static final String USER_FAILING_COMMAND = "org.jbpm.test.jobexec.UserFailingCommand";

    @Test(timeout=10000)
    public void testJobCommitInAsyncExec() throws Exception {
        CountDownAsyncJobListener countDownListener = new CountDownAsyncJobListener(1);
        ((ExecutorServiceImpl) getExecutorService()).addAsyncJobListener(countDownListener);
        KieSession ksession = registerAsyncHandler(createKSession(ASYNC_EXECUTOR_2, ASYNC_DATA_EXECUTOR));

        ProcessInstance pi;
        UserTransaction ut = getUserTransaction();
        try {
            ut.begin();

            Map<String, Object> pm = new HashMap<String, Object>();
            pm.put("_command", USER_COMMAND);
            pi = ksession.startProcess(ASYNC_EXECUTOR_2_ID, pm);

            ut.commit();
        } catch (Exception ex) {
            ut.rollback();
            throw ex;
        }

        assertProcessInstanceCompleted(pi.getId());

        countDownListener.waitTillCompleted();
        Assertions.assertThat(getExecutorService().getCompletedRequests(new QueryContext())).hasSize(1);
    }

    @Test
    public void testJobRollbackInAsyncExec() throws Exception {
        KieSession ksession = registerAsyncHandler(createKSession(ASYNC_EXECUTOR_2, ASYNC_DATA_EXECUTOR));

        long processId;
        UserTransaction ut = getUserTransaction();
        try {
            ut.begin();

            Map<String, Object> pm = new HashMap<String, Object>();
            pm.put("_command", USER_COMMAND);
            ProcessInstance pi = ksession.startProcess(ASYNC_EXECUTOR_2_ID, pm);
            processId = pi.getId();
        } finally {
            ut.rollback();
        }

        assertProcessInstanceNeverRun(processId);
        Assertions.assertThat(getExecutorService().getCompletedRequests(new QueryContext())).hasSize(0);
    }

    @Test(timeout=10000)
    public void testJobCommit() throws Exception {
        CountDownAsyncJobListener countDownListener = new CountDownAsyncJobListener(1);
        ((ExecutorServiceImpl) getExecutorService()).addAsyncJobListener(countDownListener);
        KieSession ksession = registerAsyncHandler(createKSession(ASYNC_DATA_EXECUTOR));

        ProcessInstance pi;
        UserTransaction ut = getUserTransaction();
        try {
            ut.begin();

            Map<String, Object> pm = new HashMap<String, Object>();
            pm.put("command", USER_COMMAND);
            pm.put("delayAsync", "2s");
            pi = ksession.startProcess(ASYNC_DATA_EXECUTOR_ID, pm);
            // the JobExecutor will act on the job only after commit
            ut.commit();
        } catch (Exception ex) {
            ut.rollback();
            throw ex;
        }

        countDownListener.waitTillCompleted();
        ProcessInstance processInstance = ksession.getProcessInstance(pi.getId());
        assertNull(processInstance);
        assertProcessInstanceCompleted(pi.getId());
    }

    @Test
    public void testJobRollback() throws Exception {
        KieSession ksession = registerAsyncHandler(createKSession(ASYNC_DATA_EXECUTOR));

        UserTransaction ut = getUserTransaction();
        long processId;
        try {
            ut.begin();

            Map<String, Object> pm = new HashMap<String, Object>();
            pm.put("_command", USER_FAILING_COMMAND);
            ProcessInstance pi = ksession.startProcess(ASYNC_DATA_EXECUTOR_ID, pm);
            processId = pi.getId();
        } finally {
            ut.rollback();
        }

        assertProcessInstanceNeverRun(processId);
    }

    private UserTransaction getUserTransaction() throws Exception {
        return InitialContext.doLookup("java:comp/UserTransaction");
    }

    private KieSession registerAsyncHandler(KieSession ksession) {
        WorkItemManager wim = ksession.getWorkItemManager();
        wim.registerWorkItemHandler("async", new AsyncWorkItemHandler(getExecutorService()));

        return ksession;
    }

    @Override
    protected PoolingDataSource setupPoolingDataSource() {        
        
        Properties dsProps = PersistenceUtil.getDatasourceProperties();
        String jdbcUrl = dsProps.getProperty("url");
        String driverClass = dsProps.getProperty("driverClassName");        

        // Setup the datasource
        PoolingDataSource ds1 = PersistenceUtil.setupPoolingDataSource(dsProps, "jdbc/jbpm-ds", false);
        if (driverClass.startsWith("org.h2")) {
            ds1.getDriverProperties().setProperty("url", jdbcUrl);
        }
        ds1.getDriverProperties().setProperty("POOL_CONNECTIONS", "false");
        ds1.init();
        return ds1;
    }
}
