/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.executor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ErrorInfo;
import org.kie.internal.executor.api.ExecutorService;
import org.kie.internal.executor.api.RequestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasicExecutorBaseTest {
    
    private static final Logger logger = LoggerFactory.getLogger(BasicExecutorBaseTest.class);

    @Inject
    protected ExecutorService executorService;
    public static final Map<String, Object> cachedEntities = new HashMap<String, Object>();
    
    
    
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

    @Test
    public void simpleExcecutionTest() throws InterruptedException {
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());

        executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", ctxCMD);

        Thread.sleep(10000);

        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests();
        assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests();
        assertEquals(0, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests();
        assertEquals(1, executedRequests.size());


    }

    @Test
    public void callbackTest() throws InterruptedException {

        CommandContext commandContext = new CommandContext();
        commandContext.setData("businessKey", UUID.randomUUID().toString());
        cachedEntities.put((String) commandContext.getData("businessKey"), new AtomicLong(1));

        commandContext.setData("callbacks", "org.jbpm.executor.SimpleIncrementCallback");
        executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", commandContext);

        Thread.sleep(10000);

        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests();
        assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests();
        assertEquals(0, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests();
        assertEquals(1, executedRequests.size());

        assertEquals(2, ((AtomicLong) cachedEntities.get((String) commandContext.getData("businessKey"))).longValue());



    }

    @Test
    public void executorExceptionTest() throws InterruptedException {

        CommandContext commandContext = new CommandContext();
        commandContext.setData("businessKey", UUID.randomUUID().toString());
        cachedEntities.put((String) commandContext.getData("businessKey"), new AtomicLong(1));

        commandContext.setData("callbacks", "org.jbpm.executor.SimpleIncrementCallback");
        commandContext.setData("retries", 0);
        executorService.scheduleRequest("org.jbpm.executor.ThrowExceptionCommand", commandContext);
        logger.info("{} Sleeping for 10 secs", System.currentTimeMillis());
        Thread.sleep(10000);

        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests();
        assertEquals(1, inErrorRequests.size());
        logger.info("Error: {}", inErrorRequests.get(0));

        List<ErrorInfo> errors = executorService.getAllErrors();
        logger.info("Errors: {}", errors);
        assertEquals(1, errors.size());


    }

    @Test
    public void defaultRequestRetryTest() throws InterruptedException {
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());

        executorService.scheduleRequest("org.jbpm.executor.ThrowExceptionCommand", ctxCMD);

        Thread.sleep(12000);



        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests();
        assertEquals(1, inErrorRequests.size());

        List<ErrorInfo> errors = executorService.getAllErrors();
        logger.info("Errors: {}", errors);
        // Three retries means 4 executions in total 1(regular) + 3(retries)
        assertEquals(4, errors.size());

    }

    @Test
    public void cancelRequestTest() throws InterruptedException {

        //  The executor is on purpose not started to not fight against race condition 
        // with the request cancelations.
        CommandContext ctxCMD = new CommandContext();
        String businessKey = UUID.randomUUID().toString();
        ctxCMD.setData("businessKey", businessKey);

        Long requestId = executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", ctxCMD);
        
        List<RequestInfo> requests = executorService.getRequestsByBusinessKey(businessKey);
        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertEquals(requestId, requests.get(0).getId());

        // cancel the task immediately
        executorService.cancelRequest(requestId);

        List<RequestInfo> cancelledRequests = executorService.getCancelledRequests();
        assertEquals(1, cancelledRequests.size());

    }
    
    @Test
    public void executorExceptionTrimmingTest() throws InterruptedException {
    	System.setProperty("org.kie.executor.msg.length", "10");
    	System.setProperty("org.kie.executor.stacktrace.length", "20");
        CommandContext commandContext = new CommandContext();
        commandContext.setData("businessKey", UUID.randomUUID().toString());
        cachedEntities.put((String) commandContext.getData("businessKey"), new AtomicLong(1));

        commandContext.setData("callbacks", "org.jbpm.executor.SimpleIncrementCallback");
        commandContext.setData("retries", 0);
        executorService.scheduleRequest("org.jbpm.executor.ThrowExceptionCommand", commandContext);
        logger.info("{} Sleeping for 10 secs", System.currentTimeMillis());
        Thread.sleep(10000);

        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests();
        assertEquals(1, inErrorRequests.size());
        logger.info("Error: {}", inErrorRequests.get(0));

        List<ErrorInfo> errors = executorService.getAllErrors();
        logger.info("Errors: {}", errors);
        assertEquals(1, errors.size());
        
        ErrorInfo error = errors.get(0);
        
        assertEquals(10, error.getMessage().length());
        assertEquals(20, error.getStacktrace().length());


    }

    
    public void FIXMEfutureRequestTest() throws InterruptedException {
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());

        Long requestId = executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", new Date(new Date().getTime() + 10000), ctxCMD);
        assertNotNull(requestId);
        Thread.sleep(5000);
        
        List<RequestInfo> runningRequests = executorService.getRunningRequests();
        assertEquals(0, runningRequests.size());
        
        List<RequestInfo> futureQueuedRequests = executorService.getFutureQueuedRequests();
        assertEquals(1, futureQueuedRequests.size());
        
        Thread.sleep(10000);
        
        List<RequestInfo> completedRequests = executorService.getCompletedRequests();
        assertEquals(1, completedRequests.size());
    }
    
}
