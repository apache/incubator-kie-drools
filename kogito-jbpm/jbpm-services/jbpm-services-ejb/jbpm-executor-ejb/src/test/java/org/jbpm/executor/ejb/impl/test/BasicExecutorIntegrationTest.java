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

package org.jbpm.executor.ejb.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import javax.ejb.EJB;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jbpm.executor.impl.jpa.ExecutorJPAAuditService;
import org.jbpm.services.ejb.api.ExecutorServiceEJB;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ErrorInfo;
import org.kie.api.executor.ExecutionResults;
import org.kie.api.executor.RequestInfo;
import org.kie.api.executor.STATUS;
import org.kie.api.runtime.query.QueryContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Arquillian.class)
public class BasicExecutorIntegrationTest {
    
    private static final Logger logger = LoggerFactory.getLogger(BasicExecutorIntegrationTest.class);
    public static final Map<String, Object> cachedEntities = new HashMap<String, Object>();
    
	@Deployment
	public static WebArchive createDeployment() {

		File archive = new File("target/executor-war-ejb-app.war");
		if (!archive.exists()) {
			throw new IllegalStateException("There is no archive yet generated, run maven build or mvn assembly:assembly");
		}
		WebArchive war = ShrinkWrap.createFromZipFile(WebArchive.class, archive);
		war.addPackage("org.jbpm.executor.ejb.impl.test"); // test cases
		
		return war;
	}
    
	@EJB
    protected ExecutorServiceEJB executorService;
    
    
    @PersistenceUnit(unitName="org.jbpm.domain")
    protected EntityManagerFactory emf = null;
    
    @Before
    public void setUp() {

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

        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new QueryContext());
        assertEquals(0, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        assertEquals(1, executedRequests.size());


    }

    @Test
    public void callbackTest() throws InterruptedException {

        CommandContext commandContext = new CommandContext();
        commandContext.setData("businessKey", UUID.randomUUID().toString());
        cachedEntities.put((String) commandContext.getData("businessKey"), new AtomicLong(1));

        commandContext.setData("callbacks", "org.jbpm.executor.ejb.impl.test.SimpleIncrementCallback");
        executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", commandContext);

        Thread.sleep(10000);

        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new QueryContext());
        assertEquals(0, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        assertEquals(1, executedRequests.size());

        assertEquals(2, ((AtomicLong) cachedEntities.get((String) commandContext.getData("businessKey"))).longValue());

    }
    
    @Test
    public void addAnotherCallbackTest() throws InterruptedException {

        CommandContext commandContext = new CommandContext();
        commandContext.setData("businessKey", UUID.randomUUID().toString());
        cachedEntities.put((String) commandContext.getData("businessKey"), new AtomicLong(1));

        commandContext.setData("callbacks", "org.jbpm.executor.ejb.impl.test.SimpleIncrementCallback");
        executorService.scheduleRequest("org.jbpm.executor.ejb.impl.test.AddAnotherCallbackCommand", commandContext);

        Thread.sleep(10000);

        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new QueryContext());
        assertEquals(0, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        assertEquals(1, executedRequests.size());

        assertEquals(2, ((AtomicLong) cachedEntities.get((String) commandContext.getData("businessKey"))).longValue());

        ExecutionResults results = null;
        byte[] responseData = executedRequests.get(0).getResponseData();
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new ByteArrayInputStream(responseData));
            results = (ExecutionResults) in.readObject();
        } catch (Exception e) {                        
            logger.warn("Exception while serializing context data", e);
            return;
        } finally {
            if (in != null) {
                try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        }
        
        String result = (String)results.getData("custom");
        assertNotNull(result);
        assertEquals("custom callback invoked", result);
    }
    
    @Test
    public void multipleCallbackTest() throws InterruptedException {

        CommandContext commandContext = new CommandContext();
        commandContext.setData("businessKey", UUID.randomUUID().toString());
        cachedEntities.put((String) commandContext.getData("businessKey"), new AtomicLong(1));

        commandContext.setData("callbacks", "org.jbpm.executor.ejb.impl.test.SimpleIncrementCallback, org.jbpm.executor.ejb.impl.test.CustomCallback");
        executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", commandContext);

        Thread.sleep(10000);

        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new QueryContext());
        assertEquals(0, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        assertEquals(1, executedRequests.size());

        assertEquals(2, ((AtomicLong) cachedEntities.get((String) commandContext.getData("businessKey"))).longValue());

        ExecutionResults results = null;
        byte[] responseData = executedRequests.get(0).getResponseData();
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new ByteArrayInputStream(responseData));
            results = (ExecutionResults) in.readObject();
        } catch (Exception e) {                        
            logger.warn("Exception while serializing context data", e);
            return;
        } finally {
            if (in != null) {
                try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        }
        
        String result = (String)results.getData("custom");
        assertNotNull(result);
        assertEquals("custom callback invoked", result);
    }

    @Test
    public void executorExceptionTest() throws InterruptedException {

        CommandContext commandContext = new CommandContext();
        commandContext.setData("businessKey", UUID.randomUUID().toString());
        cachedEntities.put((String) commandContext.getData("businessKey"), new AtomicLong(1));

        commandContext.setData("callbacks", "org.jbpm.executor.ejb.impl.test.SimpleIncrementCallback");
        commandContext.setData("retries", 0);
        executorService.scheduleRequest("org.jbpm.executor.ejb.impl.test.ThrowExceptionCommand", commandContext);
        logger.info("{} Sleeping for 10 secs", System.currentTimeMillis());
        Thread.sleep(10000);

        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(1, inErrorRequests.size());
        logger.info("Error: {}", inErrorRequests.get(0));

        List<ErrorInfo> errors = executorService.getAllErrors(new QueryContext());
        logger.info("Errors: {}", errors);
        assertEquals(1, errors.size());


    }

    @Test
    public void defaultRequestRetryTest() throws InterruptedException {
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());

        executorService.scheduleRequest("org.jbpm.executor.ejb.impl.test.ThrowExceptionCommand", ctxCMD);

        Thread.sleep(12000);



        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(1, inErrorRequests.size());

        List<ErrorInfo> errors = executorService.getAllErrors(new QueryContext());
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
        
        Date futureDate = new Date(System.currentTimeMillis() + 5000);

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
    
    @Test
    public void executorExceptionTrimmingTest() throws InterruptedException {
    	System.setProperty("org.kie.executor.msg.length", "10");
    	System.setProperty("org.kie.executor.stacktrace.length", "20");
        CommandContext commandContext = new CommandContext();
        commandContext.setData("businessKey", UUID.randomUUID().toString());
        cachedEntities.put((String) commandContext.getData("businessKey"), new AtomicLong(1));

        commandContext.setData("callbacks", "org.jbpm.executor.ejb.impl.test.SimpleIncrementCallback");
        commandContext.setData("retries", 0);
        executorService.scheduleRequest("org.jbpm.executor.ejb.impl.test.ThrowExceptionCommand", commandContext);
        logger.info("{} Sleeping for 10 secs", System.currentTimeMillis());
        Thread.sleep(10000);

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
    
    @Test
    public void reoccurringExcecutionTest() throws InterruptedException {
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());

        executorService.scheduleRequest("org.jbpm.executor.commands.ReoccurringPrintOutCommand", ctxCMD);

        Thread.sleep(4000);

        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new QueryContext());
        assertEquals(1, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        assertEquals(4, executedRequests.size());


    }
    
    @Test
    public void cleanupLogExcecutionTest() throws InterruptedException {
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());
        
        Long requestId = executorService.scheduleRequest("org.jbpm.executor.commands.ReoccurringPrintOutCommand", ctxCMD);

        Thread.sleep(3000);

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
        ctxCMD.setData("EmfName", "org.jbpm.domain");
        ctxCMD.setData("SkipProcessLog", "true");
        ctxCMD.setData("SkipTaskLog", "true");
        executorService.scheduleRequest("org.jbpm.executor.commands.LogCleanupCommand", ctxCMD);
        
        Thread.sleep(5000);
        
        inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(0, inErrorRequests.size());
        queuedRequests = executorService.getQueuedRequests(new QueryContext());
        assertEquals(0, queuedRequests.size());
        executedRequests = executorService.getCompletedRequests(new QueryContext());
        assertEquals(1, executedRequests.size());
    }

    @Test
    public void cancelRequestWithSearchByCommandTest() throws InterruptedException {

        CommandContext ctxCMD = new CommandContext();
        String businessKey = UUID.randomUUID().toString();
        ctxCMD.setData("businessKey", businessKey);
        
        Date futureDate = new Date(System.currentTimeMillis() + 5000);

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
    
}
