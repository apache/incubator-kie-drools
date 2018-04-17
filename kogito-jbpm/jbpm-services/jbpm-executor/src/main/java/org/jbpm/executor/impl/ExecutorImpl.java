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

package org.jbpm.executor.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

import org.apache.commons.io.input.ClassLoaderObjectInputStream;
import org.drools.core.process.instance.WorkItem;
import org.drools.core.time.TimeUtils;
import org.drools.persistence.api.TransactionManager;
import org.jbpm.executor.ExecutorNotStartedException;
import org.jbpm.executor.entities.RequestInfo;
import org.jbpm.executor.impl.concurrent.LoadAndScheduleRequestsTask;
import org.jbpm.executor.impl.concurrent.PrioritisedScheduledThreadPoolExecutor;
import org.jbpm.executor.impl.concurrent.ScheduleTaskTransactionSynchronization;
import org.jbpm.executor.impl.event.ExecutorEventSupportImpl;
import org.jbpm.executor.impl.event.ExecutorEventSupport;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutorStoreService;
import org.kie.api.executor.STATUS;
import org.kie.internal.executor.api.Executor;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
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
 * Executor can be used with JMS as the medium to notify about jobs to be executed instead of relying strictly 
 * on poll mechanism that is available by default. JMS support is configurable and is enabled by default
 * although it requires JMS resources (connection factory and destination) to properly operate. If any of
 * these will not be found it will deactivate JMS support.
 * Configuration parameters for JMS support:
 * <ul>
 *  <li>org.kie.executor.jms - allows to enable JMS support globally - default set to true</li>
 *  <li>org.kie.executor.jms.cf - JNDI name of connection factory to be used for sending messages</li>
 *  <li>org.kie.executor.jms.queue - JNDI name for destination (usually a queue) to be used to send messages to</li>
 * </ul>
 */
public class ExecutorImpl implements Executor {

    private static final Logger logger = LoggerFactory.getLogger(ExecutorImpl.class);

    private static final int DEFAULT_PRIORITY = 5;
    private static final int MAX_PRIORITY = 9;
    private static final int MIN_PRIORITY = 0;

    private String threadFactoryLookup = System.getProperty("org.kie.executor.thread.factory", "java:comp/DefaultManagedThreadFactory");
    private ExecutorStoreService executorStoreService;

    private int threadPoolSize = Integer.parseInt(System.getProperty("org.kie.executor.pool.size", "1"));
    private int retries = Integer.parseInt(System.getProperty("org.kie.executor.retry.count", "3"));
    private int interval = Integer.parseInt(System.getProperty("org.kie.executor.interval", "0"));
    private TimeUnit timeunit = TimeUnit.valueOf(System.getProperty("org.kie.executor.timeunit", "SECONDS"));

    // jms related instances
    private boolean useJMS = Boolean.parseBoolean(System.getProperty("org.kie.executor.jms", "true"));
    private String connectionFactoryName = System.getProperty("org.kie.executor.jms.cf", "java:/JmsXA");
    private String queueName = System.getProperty("org.kie.executor.jms.queue", "queue/KIE.EXECUTOR");
    private boolean transacted = Boolean.parseBoolean(System.getProperty("org.kie.executor.jms.transacted", "false"));
    private ConnectionFactory connectionFactory;
    private Queue queue;

    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> loadTaskFuture;

    private ExecutorEventSupport eventSupport = new ExecutorEventSupportImpl();
    private AvailableJobsExecutor jobProcessor;
    private TransactionManager transactionManager;

    public ExecutorImpl() {}

    public void setEventSupport(ExecutorEventSupport eventSupport) {
        this.eventSupport = eventSupport;
    }

    public void setExecutorStoreService(ExecutorStoreService executorStoreService) {
        this.executorStoreService = executorStoreService;
    }

    public ExecutorStoreService getExecutorStoreService() {
        return executorStoreService;
    }

    public String getConnectionFactoryName() {
        return connectionFactoryName;
    }

    public void setConnectionFactoryName(String connectionFactoryName) {
        this.connectionFactoryName = connectionFactoryName;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Queue getQueue() {
        return queue;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public AvailableJobsExecutor getJobProcessor() {
        return jobProcessor;
    }

    public void setJobProcessor(AvailableJobsExecutor jobProcessor) {
        this.jobProcessor = jobProcessor;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
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
    public TimeUnit getTimeunit() {
        return timeunit;
    }

    /**
     * {@inheritDoc}
     */
    public void setTimeunit(TimeUnit timeunit) {
        this.timeunit = timeunit;
    }

    /**
     * {@inheritDoc}
     */
    public void init() {
        if (!"true".equalsIgnoreCase(System.getProperty("org.kie.executor.disabled"))) {
            logger.info("Starting jBPM Executor Component ...\n" + 
                        " \t - Thread Pool Size: {}" + "\n" + 
                        " \t - Retries per Request: {}\n" +
                        " \t - Load from storage interval: {} {} (if less or equal 0 only initial sync with storage) \n",                         
                        threadPoolSize, retries, interval, timeunit.toString());

            scheduler = getScheduledExecutorService();

            if (useJMS) {
                try {
                    InitialContext ctx = new InitialContext();
                    if (this.connectionFactory == null) {
                        this.connectionFactory = (ConnectionFactory) ctx.lookup(connectionFactoryName);
                    }
                    if (this.queue == null) {
                        this.queue = (Queue) ctx.lookup(queueName);
                    }
                    logger.info("Executor JMS based support successfully activated on queue {}", queue);
                } catch (Exception e) {
                    logger.warn("Disabling JMS support in executor because: unable to initialize JMS configuration for executor due to {}", e.getMessage());
                    logger.debug("JMS support executor failed due to {}", e.getMessage(), e);
                    // since it cannot be initialized disable jms
                    useJMS = false;
                }
            }
            
            LoadAndScheduleRequestsTask loadTask = new LoadAndScheduleRequestsTask(executorStoreService, scheduler, jobProcessor);
            if (interval <= 0) {
                scheduler.execute(loadTask);
            } else {
                logger.info("Interval ({}) is more than 0, scheduling periodic load of jobs from the storage", interval);
                loadTaskFuture = scheduler.scheduleAtFixedRate(loadTask, 0, interval, timeunit);
            }
        } else {
            throw new ExecutorNotStartedException();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void destroy() {
        logger.info("Stopping jBPM Executor component");
        if (scheduler != null) {
            if (loadTaskFuture != null) {
                loadTaskFuture.cancel(true);
            }
            scheduler.shutdownNow();
            boolean terminated;
            try {
                terminated = scheduler.awaitTermination(60, TimeUnit.SECONDS);

                if (!terminated) {
                    logger.warn("Timeout occured while waiting on all jobs to be terminated");

                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {

            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long scheduleRequest(String commandId, CommandContext ctx) {
        return scheduleRequest(commandId, null, ctx);
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
        requestInfo.setTime(date == null ? new Date() : date);
        requestInfo.setMessage("Ready to execute");
        requestInfo.setDeploymentId(getDeploymentId(ctx));
        if (ctx.getData("processInstanceId") != null) {
            requestInfo.setProcessInstanceId(((Number) ctx.getData("processInstanceId")).longValue());
        }
        requestInfo.setOwner((String) ctx.getData("owner"));
        if (ctx.getData("retries") != null) {
            requestInfo.setRetries(Integer.valueOf(String.valueOf(ctx.getData("retries"))));
        } else {
            requestInfo.setRetries(retries);
        }
        int priority = DEFAULT_PRIORITY;
        if (ctx.getData("priority") != null) {
            priority = (Integer) ctx.getData("priority");
            if (priority < MIN_PRIORITY) {
                logger.warn("Priority {} is not valid (cannot be less than {}) setting it to {}", MIN_PRIORITY, MIN_PRIORITY, priority);
                priority = MIN_PRIORITY;

            } else if (priority > MAX_PRIORITY) {
                logger.warn("Priority {} is not valid (cannot be more than {}) setting it to {}", MAX_PRIORITY, MAX_PRIORITY, priority);
                priority = MAX_PRIORITY;
            }

        }
        requestInfo.setPriority(priority);

        if (ctx.getData("retryDelay") != null) {
            List<Long> retryDelay = new ArrayList<Long>();
            String[] timeExpressions = ((String) ctx.getData("retryDelay")).split(",");

            for (String timeExpr : timeExpressions) {
                retryDelay.add(TimeUtils.parseTimeString(timeExpr));
            }
            ctx.setData("retryDelay", retryDelay);
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
        eventSupport.fireBeforeJobScheduled(requestInfo, null);
        try {
            
            Consumer<Object> function = null;
            if (useJMS) {
                // send JMS only for immediate job requests not for these that should be executed in future                 
                if (date == null) {
                    executorStoreService.persistRequest(requestInfo, null);
                    logger.debug("Sending JMS message to trigger job execution for job {}", requestInfo.getId());
                    // send JMS message to trigger processing
                    sendMessage(String.valueOf(requestInfo.getId()), priority);
                } else {
                    logger.debug("JMS message not sent for job {} as the job should not be executed immediately but at {}", requestInfo.getId(), date);
                    function = scheduleExecution(requestInfo, date);
                    executorStoreService.persistRequest(requestInfo, function);
                }
            } else {
                logger.debug("Scheduling request {} for execution at {}", requestInfo.getId(), requestInfo.getTime());
                function = scheduleExecution(requestInfo, date);
                executorStoreService.persistRequest(requestInfo, function);
            }
            
            

            logger.debug("Scheduled request for Command: {} - requestId: {} with {} retries", commandId, requestInfo.getId(), requestInfo.getRetries());
            eventSupport.fireAfterJobScheduled(requestInfo, null);
        } catch (Throwable e) {
            eventSupport.fireAfterJobScheduled(requestInfo, e);
        }
        return requestInfo.getId();
    }

    public Consumer<Object> scheduleExecution(final RequestInfo requestInfo, final Date date) {
        return (T) -> {
            scheduleExecutionViaSync(requestInfo, date);
        };
    }
    
    public void scheduleExecutionViaSync(final RequestInfo requestInfo, final Date date) {
        
        transactionManager.registerTransactionSynchronization(new ScheduleTaskTransactionSynchronization(scheduler, requestInfo, date, jobProcessor));
        
    }

    public void clearExecution(Long requestId) {
        ((PrioritisedScheduledThreadPoolExecutor) scheduler).done(requestId);
    }

    /**
     * {@inheritDoc}
     */
    public void cancelRequest(Long requestId) {
        logger.debug("Before - Cancelling Request with Id: {}", requestId);
        RequestInfo job = (RequestInfo) executorStoreService.findRequest(requestId);
        eventSupport.fireBeforeJobCancelled(job, null);
        try {
            
            executorStoreService.removeRequest(requestId, (T) -> {((PrioritisedScheduledThreadPoolExecutor) scheduler).cancel(requestId);});
            eventSupport.fireAfterJobCancelled(job, null);
        } catch (Throwable e) {
            eventSupport.fireAfterJobCancelled(job, e);
        }

        logger.debug("After - Cancelling Request with Id: {}", requestId);
    }

    @Override
    public void updateRequestData(Long requestId, Map<String, Object> data) {
        logger.debug("About to update request {} data with following {}", requestId, data);

        RequestInfo request = (RequestInfo) executorStoreService.findRequest(requestId);
        if (request.getStatus().equals(STATUS.CANCELLED) || request.getStatus().equals(STATUS.DONE) || request.getStatus().equals(STATUS.RUNNING)) {
            throw new IllegalStateException("Request data can't be updated when request is in status " + request.getStatus());
        }

        CommandContext ctx = null;
        ClassLoader cl = getClassLoader(request.getDeploymentId());
        try {

            logger.debug("Processing Request Id: {}, status {} command {}", request.getId(), request.getStatus(), request.getCommandName());
            byte[] reqData = request.getRequestData();
            if (reqData != null) {
                ObjectInputStream in = null;
                try {
                    in = new ClassLoaderObjectInputStream(cl, new ByteArrayInputStream(reqData));
                    ctx = (CommandContext) in.readObject();
                } catch (IOException e) {
                    logger.warn("Exception while serializing context data", e);
                } finally {
                    if (in != null) {
                        in.close();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Unexpected error when reading request data", e);
            throw new RuntimeException(e);
        }

        if (ctx == null) {
            ctx = new CommandContext();
        }

        WorkItem workItem = (WorkItem) ctx.getData("workItem");
        if (workItem != null) {
            logger.debug("Updating work item {} parameters with data {}", workItem, data);
            for (Entry<String, Object> entry : data.entrySet()) {
                workItem.setParameter(entry.getKey(), entry.getValue());
            }
        } else {
            logger.debug("Updating request context with data {}", data);
            for (Entry<String, Object> entry : data.entrySet()) {
                ctx.setData(entry.getKey(), entry.getValue());
            }
        }

        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ObjectOutputStream oout = new ObjectOutputStream(bout);
            oout.writeObject(ctx);
            request.setRequestData(bout.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Unable to save updated request data", e);
        }

        executorStoreService.updateRequest(request, null);
        logger.debug("Request {} data updated successfully", requestId);
    }

    protected void sendMessage(String messageBody, int priority) {
        if (connectionFactory == null && queue == null) {
            throw new IllegalStateException("ConnectionFactory and Queue cannot be null");
        }
        Connection queueConnection = null;
        Session queueSession = null;
        MessageProducer producer = null;
        try {
            queueConnection = connectionFactory.createConnection();
            queueSession = queueConnection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);

            TextMessage message = queueSession.createTextMessage(messageBody);
            producer = queueSession.createProducer(queue);
            producer.setPriority(priority);

            queueConnection.start();

            producer.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error when sending JMS message with executor job request", e);
        } finally {
            if (producer != null) {
                try {
                    producer.close();
                } catch (JMSException e) {
                    logger.warn("Error when closing producer", e);
                }
            }

            if (queueSession != null) {
                try {
                    queueSession.close();
                } catch (JMSException e) {
                    logger.warn("Error when closing queue session", e);
                }
            }

            if (queueConnection != null) {
                try {
                    queueConnection.close();
                } catch (JMSException e) {
                    logger.warn("Error when closing queue connection", e);
                }
            }
        }
    }

    protected ClassLoader getClassLoader(String deploymentId) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (deploymentId == null) {
            return cl;
        }

        InternalRuntimeManager manager = ((InternalRuntimeManager) RuntimeManagerRegistry.get().getManager(deploymentId));
        if (manager != null && manager.getEnvironment().getClassLoader() != null) {
            cl = manager.getEnvironment().getClassLoader();
        }

        return cl;
    }

    protected ScheduledExecutorService getScheduledExecutorService() {
        ThreadFactory threadFactory = null;

        try {
            threadFactory = InitialContext.doLookup(threadFactoryLookup);
        } catch (Exception e) {
            threadFactory = Executors.defaultThreadFactory();
        }

        return new PrioritisedScheduledThreadPoolExecutor(threadPoolSize, threadFactory);
    }
    
    protected String getDeploymentId(CommandContext ctx) {
        String deploymentId = (String) ctx.getData("DeploymentId");
        if (deploymentId == null) {
            deploymentId = (String) ctx.getData("deploymentId");
        }
        
        return deploymentId;
    }

}
