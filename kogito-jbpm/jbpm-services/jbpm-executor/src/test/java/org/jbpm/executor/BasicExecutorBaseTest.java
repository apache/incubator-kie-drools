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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.EntityManagerFactory;

import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.executor.impl.jpa.ExecutorJPAAuditService;
import org.jbpm.executor.test.CountDownAsyncJobListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ErrorInfo;
import org.kie.api.executor.ExecutorService;
import org.kie.api.executor.RequestInfo;
import org.kie.api.executor.STATUS;
import org.kie.api.runtime.query.QueryContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasicExecutorBaseTest {
    
    private static final Logger logger = LoggerFactory.getLogger(BasicExecutorBaseTest.class);
    
    private static final long EXTRA_TIME = 2000;

    protected ExecutorService executorService;
    public static final Map<String, Object> cachedEntities = new HashMap<String, Object>();
    
    protected EntityManagerFactory emf = null;
    
    @Before
    public void setUp() {
        executorService.setThreadPoolSize(1);
        executorService.setInterval(3);
    }

    @After
    public void tearDown() {
        executorService.clearAllRequests();
        executorService.clearAllErrors();
        
        System.clearProperty("org.kie.executor.msg.length");
    	System.clearProperty("org.kie.executor.stacktrace.length");
    }
    
    protected CountDownAsyncJobListener configureListener(int threads) {
        CountDownAsyncJobListener countDownListener = new CountDownAsyncJobListener(threads);
        ((ExecutorServiceImpl) executorService).addAsyncJobListener(countDownListener);
        
        return countDownListener;
    }

    @Test(timeout=10000)
    public void simpleExecutionTest() throws InterruptedException {
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

        assertEquals(1, executedRequests.get(0).getExecutions());
    }

    @Test(timeout=10000)
    public void callbackTest() throws InterruptedException {

        CountDownAsyncJobListener countDownListener = configureListener(1);
        CommandContext commandContext = new CommandContext();
        commandContext.setData("businessKey", UUID.randomUUID().toString());
        cachedEntities.put((String) commandContext.getData("businessKey"), new AtomicLong(1));

        commandContext.setData("callbacks", "org.jbpm.executor.SimpleIncrementCallback");
        executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", commandContext);

        countDownListener.waitTillCompleted();

        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new QueryContext());
        assertEquals(0, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        assertEquals(1, executedRequests.size());

        assertEquals(2, ((AtomicLong) cachedEntities.get((String) commandContext.getData("businessKey"))).longValue());

    }
    
    @Test(timeout=10000)
    public void addAnotherCallbackTest() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(1);
        CommandContext commandContext = new CommandContext();
        commandContext.setData("businessKey", UUID.randomUUID().toString());
        cachedEntities.put((String) commandContext.getData("businessKey"), new AtomicLong(1));

        commandContext.setData("callbacks", "org.jbpm.executor.SimpleIncrementCallback");
        executorService.scheduleRequest("org.jbpm.executor.test.AddAnotherCallbackCommand", commandContext);

        countDownListener.waitTillCompleted();

        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new QueryContext());
        assertEquals(0, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        assertEquals(1, executedRequests.size());

        assertEquals(3, ((AtomicLong) cachedEntities.get((String) commandContext.getData("businessKey"))).longValue());

    }
    
    @Test(timeout=10000)
    public void multipleCallbackTest() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(1);
        CommandContext commandContext = new CommandContext();
        commandContext.setData("businessKey", UUID.randomUUID().toString());
        cachedEntities.put((String) commandContext.getData("businessKey"), new AtomicLong(1));

        commandContext.setData("callbacks", "org.jbpm.executor.SimpleIncrementCallback, org.jbpm.executor.test.CustomCallback");
        executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", commandContext);

        countDownListener.waitTillCompleted();

        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new QueryContext());
        assertEquals(0, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        assertEquals(1, executedRequests.size());

        assertEquals(3, ((AtomicLong) cachedEntities.get((String) commandContext.getData("businessKey"))).longValue());
    }

    @Test(timeout=10000)
    public void executorExceptionTest() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(1);
        CommandContext commandContext = new CommandContext();
        commandContext.setData("businessKey", UUID.randomUUID().toString());
        cachedEntities.put((String) commandContext.getData("businessKey"), new AtomicLong(1));

        commandContext.setData("callbacks", "org.jbpm.executor.SimpleIncrementCallback");
        commandContext.setData("retries", 0);
        executorService.scheduleRequest("org.jbpm.executor.ThrowExceptionCommand", commandContext);
        logger.info("{} Sleeping for 10 secs", System.currentTimeMillis());
        
        countDownListener.waitTillCompleted();

        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(1, inErrorRequests.size());
        logger.info("Error: {}", inErrorRequests.get(0));
        
        assertEquals(1, inErrorRequests.get(0).getExecutions());

        List<ErrorInfo> errors = executorService.getAllErrors(new QueryContext());
        logger.info("Errors: {}", errors);
        assertEquals(1, errors.size());


    }

    @Test(timeout=10000)
    public void defaultRequestRetryTest() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(4);
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());

        executorService.scheduleRequest("org.jbpm.executor.ThrowExceptionCommand", ctxCMD);

        countDownListener.waitTillCompleted();

        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(1, inErrorRequests.size());
        
        RequestInfo failedJob = inErrorRequests.get(0);
        assertEquals(4, failedJob.getExecutions());

        List<ErrorInfo> errors = executorService.getAllErrors(new QueryContext());
        logger.info("Errors: {}", errors);
        // Three retries means 4 executions in total 1(regular) + 3(retries)
        assertEquals(4, errors.size());

    }

    @Test(timeout=10000)
    public void cancelRequestTest() throws InterruptedException {

        //  The executor is on purpose not started to not fight against race condition 
        // with the request cancelations.
        CommandContext ctxCMD = new CommandContext();
        String businessKey = UUID.randomUUID().toString();
        ctxCMD.setData("businessKey", businessKey);
        
        Date futureDate = new Date(System.currentTimeMillis() + EXTRA_TIME);

        Long requestId = executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", futureDate, ctxCMD);
        
        List<RequestInfo> requests = executorService.getRequestsByBusinessKey(businessKey, new QueryContext());
        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertEquals(requestId, requests.get(0).getId());

        // cancel the task immediately
        executorService.cancelRequest(requestId);

        List<RequestInfo> cancelledRequests = executorService.getCancelledRequests(new QueryContext());
        assertEquals(1, cancelledRequests.size());

    }
    
    @Test(timeout=10000)
    public void executorExceptionTrimmingTest() throws InterruptedException {
    	System.setProperty("org.kie.executor.msg.length", "10");
    	System.setProperty("org.kie.executor.stacktrace.length", "20");
    	
    	CountDownAsyncJobListener countDownListener = configureListener(1);
    	
        CommandContext commandContext = new CommandContext();
        commandContext.setData("businessKey", UUID.randomUUID().toString());
        cachedEntities.put((String) commandContext.getData("businessKey"), new AtomicLong(1));

        commandContext.setData("callbacks", "org.jbpm.executor.SimpleIncrementCallback");
        commandContext.setData("retries", 0);
        executorService.scheduleRequest("org.jbpm.executor.ThrowExceptionCommand", commandContext);
        logger.info("{} Sleeping for 10 secs", System.currentTimeMillis());
        countDownListener.waitTillCompleted();

        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(1, inErrorRequests.size());
        logger.info("Error: {}", inErrorRequests.get(0));

        List<ErrorInfo> errors = executorService.getAllErrors(new QueryContext());
        logger.info("Errors: {}", errors);
        assertEquals(1, errors.size());
        
        ErrorInfo error = errors.get(0);
        
        assertEquals(10, error.getMessage().length());
        assertEquals(20, error.getStacktrace().length());


    }
    
    @Test(timeout=10000)
    public void reoccurringExecutionTest() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(3);
        
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());

        executorService.scheduleRequest("org.jbpm.executor.commands.ReoccurringPrintOutCommand", ctxCMD);

        countDownListener.waitTillCompleted();
        
        List<RequestInfo> rescheduled = executorService.getRequestsByBusinessKey((String)ctxCMD.getData("businessKey"), Arrays.asList(STATUS.QUEUED), new QueryContext());
        assertEquals(1, rescheduled.size());
        
        executorService.cancelRequest(rescheduled.get(0).getId());
        
        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new QueryContext());
        assertEquals(0, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        assertEquals(3, executedRequests.size());


    }
    
    @Test(timeout=10000)
    public void cleanupLogExecutionTest() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(3);
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());
        
        Long requestId = executorService.scheduleRequest("org.jbpm.executor.commands.ReoccurringPrintOutCommand", ctxCMD);

        countDownListener.waitTillCompleted();

        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new QueryContext());
        assertEquals(1, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        assertEquals(3, executedRequests.size());
        
        executorService.cancelRequest(requestId+3);
        
        List<RequestInfo> canceled = executorService.getCancelledRequests(new QueryContext());
        
        ExecutorJPAAuditService auditService = new ExecutorJPAAuditService(emf);
        int resultCount = auditService.requestInfoLogDeleteBuilder()
                .date(canceled.get(0).getTime())
                .status(STATUS.ERROR)
                .build()
                .execute();
        
        assertEquals(0, resultCount);
        
        resultCount = auditService.errorInfoLogDeleteBuilder()
                .date(canceled.get(0).getTime())
                .build()
                .execute();
        
        assertEquals(0, resultCount);

        ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());
        ctxCMD.setData("SingleRun", "true");
        ctxCMD.setData("EmfName", "org.jbpm.executor");
        ctxCMD.setData("SkipProcessLog", "true");
        ctxCMD.setData("SkipTaskLog", "true");
        executorService.scheduleRequest("org.jbpm.executor.commands.LogCleanupCommand", ctxCMD);
        
        countDownListener.reset(1);
        countDownListener.waitTillCompleted();
        
        inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(0, inErrorRequests.size());
        queuedRequests = executorService.getQueuedRequests(new QueryContext());
        assertEquals(0, queuedRequests.size());
        executedRequests = executorService.getCompletedRequests(new QueryContext());
        assertEquals(1, executedRequests.size());
    }

    @Test(timeout=10000)
    public void testCustomConstantRequestRetry() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(3);
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());
        ctxCMD.setData("retryDelay", "2s");
        ctxCMD.setData("retries", 2);

        executorService.scheduleRequest("org.jbpm.executor.ThrowExceptionCommand", ctxCMD);

        countDownListener.waitTillCompleted();

        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(1, inErrorRequests.size());
        
        RequestInfo failedJob = inErrorRequests.get(0);
        assertEquals(3, failedJob.getExecutions());

        List<ErrorInfo> errors = executorService.getAllErrors(new QueryContext());
        // Three retries means 4 executions in total 1(regular) + 2(retries)
        assertEquals(3, errors.size());
        
        long firstError = errors.get(0).getTime().getTime();
        long secondError = errors.get(1).getTime().getTime();
        long thirdError = errors.get(2).getTime().getTime();

        // time difference between first and second should be at least 3 seconds
        long diff = secondError - firstError;
        assertTrue(diff > 2000);
        // time difference between second and third should be at least 6 seconds
        diff = thirdError - secondError;
        assertTrue(diff > 2000);

    }
    
    @Test(timeout=10000)
    public void testCustomIncrementingRequestRetry() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(3);
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());
        ctxCMD.setData("retryDelay", "3s, 6s");
        ctxCMD.setData("retries", 2);

        executorService.scheduleRequest("org.jbpm.executor.ThrowExceptionCommand", ctxCMD);

        countDownListener.waitTillCompleted();

        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(1, inErrorRequests.size());

        List<ErrorInfo> errors = executorService.getAllErrors(new QueryContext());
        // Three retries means 4 executions in total 1(regular) + 3(retries)
        assertEquals(3, errors.size());
        
        long firstError = errors.get(0).getTime().getTime();
        long secondError = errors.get(1).getTime().getTime();
        long thirdError = errors.get(2).getTime().getTime();

        // time difference between first and second should be at least 3 seconds
        long diff = secondError - firstError;
        assertTrue(diff > 3000);
        // time difference between second and third should be at least 6 seconds
        diff = thirdError - secondError;
        assertTrue(diff > 6000);
    }

    @Test(timeout=10000)
    public void testCustomIncrementingRequestRetrySpecialValues() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(2);
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());
        ctxCMD.setData("retryDelay", "-1ms, 1m 80s");
        ctxCMD.setData("retries", 2);

        executorService.scheduleRequest("org.jbpm.executor.ThrowExceptionCommand", ctxCMD);

        countDownListener.waitTillCompleted();

        List<ErrorInfo> errors = executorService.getAllErrors(new QueryContext());
        // 2 executions in total 1(regular) + 1(retry)
        assertEquals(2, errors.size());

        long firstError = errors.get(0).getTime().getTime();
        long secondError = errors.get(1).getTime().getTime();

        // Time difference between first and second shouldn't be bigger than 4 seconds as executor has 3 second interval and
        // should start executing second command immediately.
        long diff = secondError - firstError;
        assertTrue(diff < 4000);

        List<RequestInfo> allRequests = executorService.getAllRequests(new QueryContext());
        assertEquals(1, allRequests.size());

        // Future execution is planned to be started 2 minutes and 20 seconds after last fail.
        // Time difference vary because of test thread sleeping for 10 seconds.
        diff = allRequests.get(0).getTime().getTime() - Calendar.getInstance().getTimeInMillis();
        assertTrue(diff < 140000);
        assertTrue(diff > 130000);

        executorService.clearAllRequests();
    }
    
    @Test(timeout=10000)
    public void cancelRequestWithSearchByCommandTest() throws InterruptedException {

        CommandContext ctxCMD = new CommandContext();
        String businessKey = UUID.randomUUID().toString();
        ctxCMD.setData("businessKey", businessKey);
        
        Date futureDate = new Date(System.currentTimeMillis() + EXTRA_TIME);

        Long requestId = executorService.scheduleRequest("org.jbpm.executor.test.CustomCommand", futureDate, ctxCMD);
        
        List<RequestInfo> requests = executorService.getRequestsByCommand("org.jbpm.executor.test.CustomCommand", new QueryContext());
        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertEquals(requestId, requests.get(0).getId());

        // cancel the task immediately
        executorService.cancelRequest(requestId);

        List<RequestInfo> cancelledRequests = executorService.getCancelledRequests(new QueryContext());
        assertEquals(1, cancelledRequests.size());

    }

    @Test(timeout=10000)
    public void executorPagingTest() throws InterruptedException {
        CommandContext ctxCMD = new CommandContext();
        String businessKey = UUID.randomUUID().toString();
        ctxCMD.setData("businessKey", businessKey);
        
        Date futureDate = new Date(System.currentTimeMillis() + EXTRA_TIME);

        Long requestId1 = executorService.scheduleRequest("org.jbpm.executor.test.CustomCommand", futureDate, ctxCMD);
        Long requestId2 = executorService.scheduleRequest("org.jbpm.executor.test.CustomCommand", futureDate, ctxCMD);

        QueryContext queryContextFirstPage = new QueryContext(0, 1);
        QueryContext queryContextSecondPage = new QueryContext(1, 1);

        List<RequestInfo> firstRequests = executorService.getRequestsByCommand("org.jbpm.executor.test.CustomCommand", queryContextFirstPage);
        List<RequestInfo> secondRequests = executorService.getRequestsByCommand("org.jbpm.executor.test.CustomCommand", queryContextSecondPage);
        compareRequestsAreNotSame(firstRequests.get(0), secondRequests.get(0));

        firstRequests = executorService.getRequestsByBusinessKey(businessKey, queryContextFirstPage);
        secondRequests = executorService.getRequestsByBusinessKey(businessKey, queryContextSecondPage);
        compareRequestsAreNotSame(firstRequests.get(0), secondRequests.get(0));

        firstRequests = executorService.getQueuedRequests(queryContextFirstPage);
        secondRequests = executorService.getQueuedRequests(queryContextSecondPage);
        compareRequestsAreNotSame(firstRequests.get(0), secondRequests.get(0));

        // cancel the task immediately
        executorService.cancelRequest(requestId1);
        executorService.cancelRequest(requestId2);

        firstRequests = executorService.getCancelledRequests(queryContextFirstPage);
        secondRequests = executorService.getCancelledRequests(queryContextSecondPage);
        compareRequestsAreNotSame(firstRequests.get(0), secondRequests.get(0));

        firstRequests = executorService.getAllRequests(queryContextFirstPage);
        secondRequests = executorService.getAllRequests(queryContextSecondPage);
        compareRequestsAreNotSame(firstRequests.get(0), secondRequests.get(0));

        // Setting too far page
        QueryContext queryContextBigOffset = new QueryContext(10, 1);
        List<RequestInfo> offsetRequests = executorService.getCancelledRequests(queryContextBigOffset);
        assertNotNull(offsetRequests);
        assertEquals(0, offsetRequests.size());
    }

    @Test(timeout=10000)
    public void clearAllRequestsTest() throws InterruptedException {
        CommandContext ctxCMD = new CommandContext();
        String businessKey = UUID.randomUUID().toString();
        ctxCMD.setData("businessKey", businessKey);
        
        Date futureDate = new Date(System.currentTimeMillis() + EXTRA_TIME);

        // Testing clearing of active request.
        Long requestId = executorService.scheduleRequest("org.jbpm.executor.test.CustomCommand", futureDate, ctxCMD);

        List<RequestInfo> allRequests = executorService.getAllRequests(new QueryContext());
        assertEquals(1, allRequests.size());

        executorService.clearAllRequests();

        allRequests = executorService.getAllRequests(new QueryContext());
        assertEquals(0, allRequests.size());

        // Testing clearing of cancelled request.
        requestId = executorService.scheduleRequest("org.jbpm.executor.test.CustomCommand", futureDate, ctxCMD);

        allRequests = executorService.getAllRequests(new QueryContext());
        assertEquals(1, allRequests.size());

        executorService.cancelRequest(requestId);
        executorService.clearAllRequests();

        allRequests = executorService.getAllRequests(new QueryContext());
        assertEquals(0, allRequests.size());
    }
    
    @Test(timeout=10000)
    public void testReturnNullCommand() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(1);
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());

        executorService.scheduleRequest("org.jbpm.executor.test.ReturnNullCommand", ctxCMD);

        countDownListener.waitTillCompleted();

        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new QueryContext());
        assertEquals(0, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        assertEquals(1, executedRequests.size());


    }
    
    @Test(timeout=10000)
    public void testPrioritizedJobsExecution() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(2);        
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", "low priority");
        ctxCMD.setData("priority", 2);
        
        Date futureDate = new Date(System.currentTimeMillis() + EXTRA_TIME);

        executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", futureDate, ctxCMD);
        
        CommandContext ctxCMD2 = new CommandContext();
        ctxCMD2.setData("businessKey", "high priority");
        ctxCMD2.setData("priority", 8);

        executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", futureDate, ctxCMD2);

        countDownListener.waitTillCompleted();

        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new QueryContext());
        assertEquals(0, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        assertEquals(2, executedRequests.size());
        
        RequestInfo executedHigh = executedRequests.get(1);
        assertNotNull(executedHigh);
        assertEquals("high priority", executedHigh.getKey());
                
        RequestInfo executedLow = executedRequests.get(0);
        assertNotNull(executedLow);
        assertEquals("low priority", executedLow.getKey());
        
        assertTrue(executedLow.getTime().getTime() > executedHigh.getTime().getTime());
    }
    
    @Test(timeout=10000)
    public void testPrioritizedJobsExecutionInvalidProrities() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(2);
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", "low priority");
        ctxCMD.setData("priority", -1);
        
        Date futureDate = new Date(System.currentTimeMillis() + EXTRA_TIME);

        executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", futureDate, ctxCMD);
        
        CommandContext ctxCMD2 = new CommandContext();
        ctxCMD2.setData("businessKey", "high priority");
        ctxCMD2.setData("priority", 10);

        executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", futureDate, ctxCMD2);
        
        countDownListener.waitTillCompleted();

        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new QueryContext());
        assertEquals(0, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        assertEquals(2, executedRequests.size());
        
        RequestInfo executedHigh = executedRequests.get(1);
        assertNotNull(executedHigh);
        assertEquals("high priority", executedHigh.getKey());
                
        RequestInfo executedLow = executedRequests.get(0);
        assertNotNull(executedLow);
        assertEquals("low priority", executedLow.getKey());
        
        assertTrue(executedLow.getTime().getTime() > executedHigh.getTime().getTime());
    }
    
    @Test(timeout=10000)
    public void testProcessContextJobsExecution() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(1);        
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", "low priority");
        ctxCMD.setData("deploymentId", "not-deployed-here");
        ctxCMD.setData("processInstanceId", 2L);

        Long requestId = executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", ctxCMD);
        
        List<STATUS> statuses = Arrays.asList(STATUS.QUEUED);
        
        List<RequestInfo> byDeploymentRequests = executorService.getRequestsByDeployment("not-deployed-here", statuses, new QueryContext());
        assertEquals(1, byDeploymentRequests.size());
        
        List<RequestInfo> byProcessInstanceRequests = executorService.getRequestsByProcessInstance(2L, statuses, new QueryContext());
        assertEquals(1, byProcessInstanceRequests.size());
               
        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new QueryContext());
        assertEquals(1, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        assertEquals(0, executedRequests.size());
        
        countDownListener.waitTillCompleted(5000);
        
        inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(0, inErrorRequests.size());
        queuedRequests = executorService.getQueuedRequests(new QueryContext());
        assertEquals(1, queuedRequests.size());
        executedRequests = executorService.getCompletedRequests(new QueryContext());
        assertEquals(0, executedRequests.size());
        
        executorService.cancelRequest(requestId);
    }
    
    @Test(timeout=10000)
    public void testJobsQueryWithStatus() throws InterruptedException {
               
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", "low priority");
        ctxCMD.setData("deploymentId", "not-deployed-here");
        ctxCMD.setData("processInstanceId", 2L);

        Long requestId = executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", ctxCMD);
        
        List<STATUS> statuses = Arrays.asList(STATUS.QUEUED);
        
        List<RequestInfo> byDeploymentRequests = executorService.getRequestsByDeployment("not-deployed-here", statuses, new QueryContext());
        assertEquals(1, byDeploymentRequests.size());
        
        List<RequestInfo> byProcessInstanceRequests = executorService.getRequestsByProcessInstance(2L, statuses, new QueryContext());
        assertEquals(1, byProcessInstanceRequests.size());
        
        List<RequestInfo> byKeyRequests = executorService.getRequestsByBusinessKey("low priority", statuses, new QueryContext());
        assertEquals(1, byKeyRequests.size());
        
        List<RequestInfo> byCommandRequests = executorService.getRequestsByCommand("org.jbpm.executor.commands.PrintOutCommand", statuses, new QueryContext());
        assertEquals(1, byCommandRequests.size());
               
        
        statuses = Arrays.asList(STATUS.DONE);
        
        byDeploymentRequests = executorService.getRequestsByDeployment("not-deployed-here", statuses, new QueryContext());
        assertEquals(0, byDeploymentRequests.size());
        
        byProcessInstanceRequests = executorService.getRequestsByProcessInstance(2L, statuses, new QueryContext());
        assertEquals(0, byProcessInstanceRequests.size());
        
        byKeyRequests = executorService.getRequestsByBusinessKey("low priority", statuses, new QueryContext());
        assertEquals(0, byKeyRequests.size());
        
        byCommandRequests = executorService.getRequestsByCommand("org.jbpm.executor.commands.PrintOutCommand", statuses, new QueryContext());
        assertEquals(0, byCommandRequests.size());
        
        executorService.cancelRequest(requestId);
    }
    
    @Test(timeout=10000)
    public void testUpdateRequestData() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(2);
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());    
        ctxCMD.setData("retryDelay", "1s, 2s");

        Long requestId = executorService.scheduleRequest("org.jbpm.executor.test.MissingDataCommand", ctxCMD);

        countDownListener.waitTillCompleted();
        
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        assertEquals(0, executedRequests.size());
        
        Map<String, Object> fixedData = new HashMap<>();
        fixedData.put("amount", 200);
        
        executorService.updateRequestData(requestId, fixedData);
        
        countDownListener.reset(1);
        countDownListener.waitTillCompleted();
        
        executedRequests = executorService.getCompletedRequests(new QueryContext());
        assertEquals(1, executedRequests.size());
    }
    
    @Test(timeout=10000)
    public void testUpdateRequestDataFromErrorState() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(1);
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());
        ctxCMD.setData("retries", 0);
        

        Long requestId = executorService.scheduleRequest("org.jbpm.executor.test.MissingDataCommand", ctxCMD);

        countDownListener.waitTillCompleted();
        
        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(1, inErrorRequests.size());
        
        Map<String, Object> fixedData = new HashMap<>();
        fixedData.put("amount", 200);
        
        executorService.updateRequestData(requestId, fixedData);
        
        countDownListener.reset(1);
        ((RequeueAware)executorService).requeueById(requestId);
                
        countDownListener.waitTillCompleted();
        
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        assertEquals(1, executedRequests.size());
    }

    private void compareRequestsAreNotSame(RequestInfo firstRequest, RequestInfo secondRequest) {
        assertNotNull(firstRequest);
        assertNotNull(secondRequest);
        assertNotEquals("Requests are same!", firstRequest.getId(), secondRequest.getId());
    }

    
}
