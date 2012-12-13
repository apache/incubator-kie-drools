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
    protected ExecutorServiceEntryPoint executor;
    public static final Map<String, Object> cachedEntities = new HashMap<String, Object>();
    
    @Before
    public void setUp() {
        executor.setThreadPoolSize(1);
        executor.setInterval(3);
        executor.init();
    }

    @After
    public void tearDown() {
        executor.clearAllRequests();
        executor.clearAllErrors();
        executor.destroy();
    }

    @Test
    public void simpleExcecutionTest() throws InterruptedException {
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());

        executor.scheduleRequest("PrintOutCmd", ctxCMD);

        Thread.sleep(10000);

        List<RequestInfo> inErrorRequests = executor.getInErrorRequests();
        assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executor.getQueuedRequests();
        assertEquals(0, queuedRequests.size());
        List<RequestInfo> executedRequests = executor.getCompletedRequests();
        assertEquals(1, executedRequests.size());


    }

    @Test
    public void callbackTest() throws InterruptedException {

        CommandContext commandContext = new CommandContext();
        commandContext.setData("businessKey", UUID.randomUUID().toString());
        cachedEntities.put((String) commandContext.getData("businessKey"), new AtomicLong(1));

        commandContext.setData("callbacks", "SimpleIncrementCallback");
        executor.scheduleRequest("PrintOutCmd", commandContext);

        Thread.sleep(10000);

        List<RequestInfo> inErrorRequests = executor.getInErrorRequests();
        assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executor.getQueuedRequests();
        assertEquals(0, queuedRequests.size());
        List<RequestInfo> executedRequests = executor.getCompletedRequests();
        assertEquals(1, executedRequests.size());

        assertEquals(2, ((AtomicLong) cachedEntities.get((String) commandContext.getData("businessKey"))).longValue());



    }

    @Test
    public void executorExceptionTest() throws InterruptedException {

        CommandContext commandContext = new CommandContext();
        commandContext.setData("businessKey", UUID.randomUUID().toString());
        cachedEntities.put((String) commandContext.getData("businessKey"), new AtomicLong(1));

        commandContext.setData("callbacks", "SimpleIncrementCallback");
        commandContext.setData("retries", 0);
        executor.scheduleRequest("ThrowExceptionCmd", commandContext);
        System.out.println(System.currentTimeMillis() + "  >>> Sleeping for 10 secs");
        Thread.sleep(10000);

        List<RequestInfo> inErrorRequests = executor.getInErrorRequests();
        assertEquals(1, inErrorRequests.size());
        System.out.println("Error: " + inErrorRequests.get(0));

        List<ErrorInfo> errors = executor.getAllErrors();
        System.out.println(" >>> Errors: " + errors);
        assertEquals(1, errors.size());


    }

    @Test
    public void defaultRequestRetryTest() throws InterruptedException {
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());

        executor.scheduleRequest("ThrowExceptionCmd", ctxCMD);

        Thread.sleep(12000);



        List<RequestInfo> inErrorRequests = executor.getInErrorRequests();
        assertEquals(1, inErrorRequests.size());

        List<ErrorInfo> errors = executor.getAllErrors();
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

        Long requestId = executor.scheduleRequest("PrintOutCmd", ctxCMD);

        // cancel the task immediately
        executor.cancelRequest(requestId);

        List<RequestInfo> cancelledRequests = executor.getCancelledRequests();
        assertEquals(1, cancelledRequests.size());

    }
    
    public void FIXMEfutureRequestTest() throws InterruptedException {
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());

        Long requestId = executor.scheduleRequest("PrintOutCmd", new Date(new Date().getTime() + 10000), ctxCMD);
        
        Thread.sleep(5000);
        
        List<RequestInfo> runningRequests = executor.getRunningRequests();
        assertEquals(0, runningRequests.size());
        
        List<RequestInfo> futureQueuedRequests = executor.getFutureQueuedRequests();
        assertEquals(1, futureQueuedRequests.size());
        
        Thread.sleep(10000);
        
        List<RequestInfo> completedRequests = executor.getCompletedRequests();
        assertEquals(1, completedRequests.size());
    }
    
}
