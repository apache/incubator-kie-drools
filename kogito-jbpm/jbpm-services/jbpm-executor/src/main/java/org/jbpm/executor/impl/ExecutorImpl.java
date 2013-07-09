/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.jbpm.executor.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.enterprise.event.Event;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.jboss.seam.transaction.Transactional;
import org.jbpm.executor.annotations.Cancelled;
import org.jbpm.executor.annotations.Pending;
import org.jbpm.executor.api.CommandContext;
import org.jbpm.executor.api.Executor;
import org.jbpm.executor.api.ExecutorQueryService;
import org.jbpm.executor.entities.RequestInfo;
import org.jbpm.executor.entities.STATUS;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author salaboy
 */
@Transactional
public class ExecutorImpl implements Executor {

    private static final Logger logger = LoggerFactory.getLogger(ExecutorImpl.class);

    @Inject
    private JbpmServicesPersistenceManager pm;
    @Inject
    private ExecutorRunnable runnableTask;
    @Inject
    private Event<RequestInfo> requestEvents;
    @Inject
    private ExecutorQueryService queryService;
    
    private ScheduledFuture<?> handle;
    private int threadPoolSize = 1;
    private int retries = 3;
    private int interval = 3;
    private ScheduledExecutorService scheduler;

    public ExecutorImpl() {
    }

    public void setPm(JbpmServicesPersistenceManager pm) {
        this.pm = pm;
    }

    public void setExecutorRunnable(ExecutorRunnable runnableTask) {
        this.runnableTask = runnableTask;
    }

    public void setRequestEvents(Event<RequestInfo> requestEvents) {
        this.requestEvents = requestEvents;
    }

    public void setQueryService(ExecutorQueryService queryService) {
        this.queryService = queryService;
    }
    
    

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public void init() {

        logger.info("Starting Executor Component ...\n" + " \t - Thread Pool Size: {}" + "\n"
                + " \t - Interval: {}" + " Seconds\n" + " \t - Retries per Request: {}\n",
                threadPoolSize, interval, retries);

        scheduler = Executors.newScheduledThreadPool(threadPoolSize);
        handle = scheduler.scheduleAtFixedRate(runnableTask, 2, interval, TimeUnit.SECONDS);
    }

    @Override
    public Long scheduleRequest(String commandId, CommandContext ctx) {
        return scheduleRequest(commandId, new Date(), ctx);
    }
    
    @Override
    public Long scheduleRequest(String commandId, Date date, CommandContext ctx) {

        if (ctx == null) {
            throw new IllegalStateException("A Context Must Be Provided! ");
        }
        String businessKey = (String) ctx.getData("businessKey");
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setCommandName(commandId);
        requestInfo.setKey(businessKey);
        requestInfo.setStatus(STATUS.QUEUED);
        requestInfo.setTime(date);
        requestInfo.setMessage("Ready to execute");
        if (ctx.getData("retries") != null) {
            requestInfo.setRetries(Integer.valueOf(String.valueOf(ctx.getData("retries"))));
        } else {
            requestInfo.setRetries(retries);
        }
        if (ctx != null) {
            try {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                ObjectOutputStream oout = new ObjectOutputStream(bout);
                oout.writeObject(ctx);
                requestInfo.setRequestData(bout.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
                requestInfo.setRequestData(null);
            }
        }


        pm.persist(requestInfo);

        requestEvents.select(new AnnotationLiteral<Pending>(){}).fire(requestInfo);
        
        logger.info("Scheduling request for Command: {} - requestId: {} with {} retries", commandId, requestInfo.getId(), requestInfo.getRetries());
        return requestInfo.getId();
    }

    public void cancelRequest(Long requestId) {
        logger.info("Before - Cancelling Request with Id: {}", requestId);

        List<?> result = queryService.getPendingRequestById(requestId);
        if (result.isEmpty()) {
            return;
        }
        RequestInfo r = (RequestInfo) result.iterator().next();

        r.setStatus(STATUS.CANCELLED);
        pm.merge(r);

        requestEvents.select(new AnnotationLiteral<Cancelled>(){}).fire(r);
        
        logger.info("After - Cancelling Request with Id: {}", requestId);
    }

    public void destroy() {
        logger.info(" >>>>> Destroying Executor !!!");
        handle.cancel(true);
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }
}
