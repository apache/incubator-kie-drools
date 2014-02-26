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

package org.jbpm.executor.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.persistence.NoResultException;

import org.drools.core.command.impl.GenericCommand;
import org.jbpm.executor.entities.RequestInfo;
import org.jbpm.shared.services.impl.JpaPersistenceContext;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.shared.services.impl.commands.PersistObjectCommand;
import org.kie.internal.command.Context;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.Executor;
import org.kie.internal.executor.api.STATUS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of the <code>Executor</code> that is baced by
 * <code>ScheduledExecutorService</code> for background task execution.
 * It can be configured for following:
 * <ul>
 *  <li>thread pool size - default 1 - use system property org.kie.executor.pool.size</li>
 *  <li>retry count - default 3 retries - use system property org.kie.executor.retry.count</li>
 *  <li>execution interval - default 3 seconds - use system property org.kie.executor.interval</li>
 * </ul>
 * Additionally executor can be disable to not start at all when system property org.kie.executor.disabled is 
 * set to true
 */
public class ExecutorImpl implements Executor {

    private static final Logger logger = LoggerFactory.getLogger(ExecutorImpl.class);

   
    private ExecutorRunnable runnableTask;
   
    private TransactionalCommandService commandService;
    
    private ScheduledFuture<?> handle;
    private int threadPoolSize = Integer.parseInt(System.getProperty("org.kie.executor.pool.size", "1"));
    private int retries = Integer.parseInt(System.getProperty("org.kie.executor.retry.count", "3"));
    private int interval = Integer.parseInt(System.getProperty("org.kie.executor.interval", "3"));
    private ScheduledExecutorService scheduler;

    public ExecutorImpl() {
    }

   
    public void setCommandService(TransactionalCommandService commandService) {
        this.commandService = commandService;
    }

    public void setExecutorRunnable(ExecutorRunnable runnableTask) {
        this.runnableTask = runnableTask;
    }


    /**
     * {@inheritDoc}
     */
    public int getInterval() {
        return interval;
    }

    /**
     * {@inheritDoc}
     */
    public void setInterval(int interval) {
        this.interval = interval;
    }

    /**
     * {@inheritDoc}
     */
    public int getRetries() {
        return retries;
    }

    /**
     * {@inheritDoc}
     */
    public void setRetries(int retries) {
        this.retries = retries;
    }

    /**
     * {@inheritDoc}
     */
    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    /**
     * {@inheritDoc}
     */
    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    /**
     * {@inheritDoc}
     */
    public void init() {
        if (!"true".equalsIgnoreCase(System.getProperty("org.kie.executor.disabled"))) {
            logger.info("Starting Executor Component ...\n" + " \t - Thread Pool Size: {}" + "\n"
                    + " \t - Interval: {}" + " Seconds\n" + " \t - Retries per Request: {}\n",
                    threadPoolSize, interval, retries);
    
            scheduler = Executors.newScheduledThreadPool(threadPoolSize);
            handle = scheduler.scheduleAtFixedRate(runnableTask, 2, interval, TimeUnit.SECONDS);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void destroy() {
        logger.info(" >>>>> Destroying Executor !!!");
        if (handle != null) {
            handle.cancel(true);
        }
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long scheduleRequest(String commandId, CommandContext ctx) {
        return scheduleRequest(commandId, new Date(), ctx);
    }
    
    /**
     * {@inheritDoc}
     */
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
        requestInfo.setDeploymentId((String)ctx.getData("deploymentId"));
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
                logger.warn("Error serializing context data", e);
                requestInfo.setRequestData(null);
            }
        }

        commandService.execute(new PersistObjectCommand(requestInfo));

        logger.debug("Scheduling request for Command: {} - requestId: {} with {} retries", commandId, requestInfo.getId(), requestInfo.getRetries());
        return requestInfo.getId();
    }

    /**
     * {@inheritDoc}
     */
    public void cancelRequest(Long requestId) {
        logger.debug("Before - Cancelling Request with Id: {}", requestId);

        commandService.execute(new LockAndCancelRequestInfoCommand(requestId));

        logger.debug("After - Cancelling Request with Id: {}", requestId);
    }

    private class LockAndCancelRequestInfoCommand implements GenericCommand<RequestInfo> {

		private static final long serialVersionUID = 8670412133363766161L;

		private Long requestId;
		
		LockAndCancelRequestInfoCommand(Long requestId) {
			this.requestId = requestId;
		}
		
		@Override
		public RequestInfo execute(Context context) {
			Map<String, Object> params = new HashMap<String, Object>();
	    	params.put("id", requestId);
	    	params.put("firstResult", 0);
	    	params.put("maxResults", 1);
	    	RequestInfo request = null;
	    	try {
				JpaPersistenceContext ctx = (JpaPersistenceContext) context;
				request = ctx.queryAndLockWithParametersInTransaction("PendingRequestById",params, true, RequestInfo.class);
				
				if (request != null) {
	                request.setStatus(STATUS.CANCELLED);
	                ctx.merge(request);
	            }
	    	} catch (NoResultException e) {
	    		
	    	}
			return request;
		}
    	
    }

}
