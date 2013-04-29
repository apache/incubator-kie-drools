/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.executor;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import javax.inject.Inject;
import org.jbpm.executor.api.CommandContext;
import org.jbpm.executor.entities.ErrorInfo;
import org.jbpm.executor.entities.RequestInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author salaboy
 */
public abstract class BasicExecutorBaseTest {

    @Inject
    protected ExecutorServiceEntryPoint executorService;
    public static final Map<String, Object> cachedEntities = new HashMap<String, Object>();
    
    
    
    @Before
    public void setUp() {
        executorService.setThreadPoolSize(1);
        executorService.setInterval(3);
        executorService.init();
    }

    @After
    public void tearDown() {
        executorService.clearAllRequests();
        executorService.clearAllErrors();
        executorService.destroy();
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
        System.out.println(System.currentTimeMillis() + "  >>> Sleeping for 10 secs");
        Thread.sleep(10000);

        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests();
        assertEquals(1, inErrorRequests.size());
        System.out.println("Error: " + inErrorRequests.get(0));

        List<ErrorInfo> errors = executorService.getAllErrors();
        System.out.println(" >>> Errors: " + errors);
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
        System.out.println(" >>> Errors: " + errors);
        // Three retries means 4 executions in total 1(regular) + 3(retries)
        assertEquals(4, errors.size());

    }

    @Test
    public void cancelRequestTest() throws InterruptedException {

        //  The executor is on purpose not started to not fight against race condition 
        // with the request cancelations.
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());

        Long requestId = executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", ctxCMD);

        // cancel the task immediately
        executorService.cancelRequest(requestId);

        List<RequestInfo> cancelledRequests = executorService.getCancelledRequests();
        assertEquals(1, cancelledRequests.size());

    }
    
    public void FIXMEfutureRequestTest() throws InterruptedException {
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());

        Long requestId = executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", new Date(new Date().getTime() + 10000), ctxCMD);
        
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
