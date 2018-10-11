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
package org.jbpm.executor;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;

import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.executor.test.CountDownAsyncJobListener;
import org.jbpm.test.util.ExecutorTestUtil;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutorService;
import org.kie.api.executor.RequestInfo;
import org.kie.api.executor.STATUS;
import org.kie.api.runtime.query.QueryContext;


public class ReconfiguredExecutorTest {

    protected ExecutorService executorService;

    private static final long EXTRA_TIME = 2000;

    private PoolingDataSource pds;
    private EntityManagerFactory emf = null;
    
    @Before
    public void setUp() {
        pds = ExecutorTestUtil.setupPoolingDataSource();
        emf = Persistence.createEntityManagerFactory("org.jbpm.executor");

        executorService = ExecutorServiceFactory.newExecutorService(emf);
        executorService.setThreadPoolSize(2);
        executorService.setInterval(3000);
        executorService.setTimeunit(TimeUnit.MILLISECONDS);
        
        executorService.init();
    }
    
    @After
    public void tearDown() {
    	executorService.clearAllRequests();
        executorService.clearAllErrors();
        
        System.clearProperty("org.kie.executor.msg.length");
    	System.clearProperty("org.kie.executor.stacktrace.length");
        executorService.destroy();
        if (emf != null) {
        	emf.close();
        }
        pds.close();
    }
    
    protected CountDownAsyncJobListener configureListener(int threads) {
        CountDownAsyncJobListener countDownListener = new CountDownAsyncJobListener(threads);
        ((ExecutorServiceImpl) executorService).addAsyncJobListener(countDownListener);
        
        return countDownListener;
    }
   
    @Test
    public void simpleExcecutionTest() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(1);
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());

        executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", ctxCMD);

        countDownListener.waitTillCompleted();

        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new QueryContext());
        assertEquals(0, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        assertEquals(1, executedRequests.size());


    }

    @Test(timeout=10000)
    public void testRequeueWithMilliseconds() throws Exception {
        CountDownAsyncJobListener countDownListener = configureListener(1);

        // simulate a job which was left RUNNING (e.g. node crash)
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        EntityManager em = emf.createEntityManager();
        CommandContext ctxCMD = new CommandContext();
        String businessKey = UUID.randomUUID().toString();
        ctxCMD.setData("businessKey", businessKey);

        org.jbpm.executor.entities.RequestInfo requestInfo = new org.jbpm.executor.entities.RequestInfo();
        requestInfo.setCommandName("org.jbpm.executor.commands.PrintOutCommand");
        requestInfo.setKey(businessKey);
        requestInfo.setStatus(STATUS.RUNNING);
        Date originalScheduledTime = new Date();
        requestInfo.setTime(originalScheduledTime);
        requestInfo.setMessage("Ready to execute");
        requestInfo.setDeploymentId(null);
        requestInfo.setRetries(0);
        requestInfo.setPriority(5);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oout = new ObjectOutputStream(bout);
        oout.writeObject(ctxCMD);
        requestInfo.setRequestData(bout.toByteArray());

        em.persist(requestInfo);
        em.close();
        ut.commit();

        ((RequeueAware) executorService).requeue(2000L); // Executor's time unit is configured to MILLISECOND
        countDownListener.waitTillCompleted(EXTRA_TIME);

        List<RequestInfo> requests = executorService.getRequestsByBusinessKey(businessKey, new QueryContext());
        RequestInfo requestInfoAfterFirstRequeue = requests.get(0);

        assertEquals("The job should not be requeued yet", STATUS.RUNNING, requestInfoAfterFirstRequeue.getStatus());
        // To be sure that the job is not running yet we have to check the time of the last execution
        assertEquals("The job should not be requeued yet", originalScheduledTime, requestInfoAfterFirstRequeue.getTime());

        ((RequeueAware) executorService).requeue(2000L); // Executor's time unit is configured to MILLISECOND

        countDownListener.waitTillCompleted(EXTRA_TIME);

        requests = executorService.getRequestsByBusinessKey(businessKey, new QueryContext());
        RequestInfo requestInfoAfterSecondRequeue = requests.get(0);

        assertTrue("The job should be requeued and executed", requestInfoAfterSecondRequeue.getStatus() == STATUS.DONE);
    }
}