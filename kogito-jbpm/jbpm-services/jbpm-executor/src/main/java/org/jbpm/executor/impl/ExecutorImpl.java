/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

import org.drools.core.time.TimeUtils;
import org.jbpm.executor.ExecutorNotStartedException;
import org.jbpm.executor.entities.RequestInfo;
import org.jbpm.executor.impl.event.ExecutorEventSupport;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutorStoreService;
import org.kie.api.executor.STATUS;
import org.kie.internal.executor.api.Executor;
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
    
    private ExecutorStoreService executorStoreService;

    private List<ScheduledFuture<?>> handle = new ArrayList<ScheduledFuture<?>>();
    private int threadPoolSize = Integer.parseInt(System.getProperty("org.kie.executor.pool.size", "1"));
    private int retries = Integer.parseInt(System.getProperty("org.kie.executor.retry.count", "3"));
    private int interval = Integer.parseInt(System.getProperty("org.kie.executor.interval", "3"));
    private int initialDelay = Integer.parseInt(System.getProperty("org.kie.executor.initial.delay", "100"));
    private TimeUnit timeunit = TimeUnit.valueOf(System.getProperty("org.kie.executor.timeunit", "SECONDS"));
    
    
    // jms related instances
    private boolean useJMS = Boolean.parseBoolean(System.getProperty("org.kie.executor.jms", "true"));
    private String connectionFactoryName = System.getProperty("org.kie.executor.jms.cf", "java:/JmsXA");
    private String queueName = System.getProperty("org.kie.executor.jms.queue", "queue/KIE.EXECUTOR");
    private boolean transacted = Boolean.parseBoolean(System.getProperty("org.kie.executor.jms.transacted", "false"));
    private ConnectionFactory connectionFactory;
    private Queue queue;

	private ScheduledExecutorService scheduler;
	
	private ExecutorEventSupport eventSupport = new ExecutorEventSupport();

    public ExecutorImpl() {
    }
    
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
            logger.info("Starting Executor Component ...\n" + " \t - Thread Pool Size: {}" + "\n"
                    + " \t - Interval: {} {} \n" + " \t - Retries per Request: {}\n",
                    threadPoolSize, interval, timeunit.toString(), retries);
            
            int delayIncremental = 0;
            
            scheduler = Executors.newScheduledThreadPool(threadPoolSize);
            for (int i = 0; i < threadPoolSize; i++) {
                long delay = 2000 + delayIncremental;
                long interval = TimeUnit.MILLISECONDS.convert(this.interval, timeunit);
                logger.debug("Starting executor thread with initial delay {} interval {} and time unit {}", delay, interval, TimeUnit.MILLISECONDS);
                handle.add(scheduler.scheduleAtFixedRate(executorStoreService.buildExecutorRunnable(), delay, interval, TimeUnit.MILLISECONDS));
                               
                delayIncremental += this.initialDelay;
                
            }
            
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
        } else {
        	throw new ExecutorNotStartedException();
        }
    }
    
    public void init(ThreadFactory threadFactory) {
        if (!"true".equalsIgnoreCase(System.getProperty("org.kie.executor.disabled"))) {
            logger.info("Starting Executor Component ...\n" + " \t - Thread Pool Size: {}" + "\n"
                    + " \t - Interval: {}" + " Seconds\n" + " \t - Retries per Request: {}\n",
                    threadPoolSize, interval, retries);
            
            int delayIncremental = 0;
            
            scheduler = Executors.newScheduledThreadPool(threadPoolSize, threadFactory);
            for (int i = 0; i < threadPoolSize; i++) {
                
                long delay = 2000 + delayIncremental;
                long interval = TimeUnit.MILLISECONDS.convert(this.interval, timeunit);
                logger.debug("Starting executor thread with initial delay {} interval {} and time unit {}", delay, interval, TimeUnit.MILLISECONDS);
                handle.add(scheduler.scheduleAtFixedRate(executorStoreService.buildExecutorRunnable(), delay, interval, TimeUnit.MILLISECONDS));
                
                delayIncremental += this.initialDelay;
            }
        } else {
        	throw new ExecutorNotStartedException();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void destroy() {
        logger.info(" >>>>> Destroying Executor !!!");
        if (handle != null) {
        	for (ScheduledFuture<?> h : handle) {
        		h.cancel(false);
        	}
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
        requestInfo.setOwner((String)ctx.getData("owner"));
        if (ctx.getData("retries") != null) {
            requestInfo.setRetries(Integer.valueOf(String.valueOf(ctx.getData("retries"))));
        } else {
            requestInfo.setRetries(retries);
        }
        int priority = 5;
        if (ctx.getData("priority") != null) {
            priority = (Integer) ctx.getData("priority");
            if (priority < 0) {
                logger.warn("Priority {} is not valid (cannot be less than 0) setting it to 0", priority);
                priority = 0;
                
            } else if (priority > 9) {
                logger.warn("Priority {} is not valid (cannot be more than 9) setting it to 9", priority);
                priority = 9;
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
            executorStoreService.persistRequest(requestInfo);
    
            if (useJMS) {
                // send JMS only for immediate job requests not for these that should be executed in future 
                long currentTimestamp = System.currentTimeMillis();
                
                if (currentTimestamp >= date.getTime()) {
                    logger.debug("Sending JMS message to trigger job execution for job {}", requestInfo.getId());
                    // send JMS message to trigger processing
                    sendMessage(String.valueOf(requestInfo.getId()), priority);
                } else {
                    logger.debug("JMS message not sent for job {} as the job should not be executed immediately but at {}", requestInfo.getId(), date);
                }
            }
            
            logger.debug("Scheduled request for Command: {} - requestId: {} with {} retries", commandId, requestInfo.getId(), requestInfo.getRetries());
            eventSupport.fireAfterJobScheduled(requestInfo, null);
        } catch (Throwable e) {
            eventSupport.fireAfterJobScheduled(requestInfo, e);
        }
        return requestInfo.getId();
    }

    /**
     * {@inheritDoc}
     */
    public void cancelRequest(Long requestId) {
        logger.debug("Before - Cancelling Request with Id: {}", requestId);
        RequestInfo job = (RequestInfo) executorStoreService.findRequest(requestId);
        eventSupport.fireBeforeJobCancelled(job, null);
        try {
            executorStoreService.removeRequest(requestId);
            eventSupport.fireAfterJobCancelled(job, null);
        } catch (Throwable e) {
            eventSupport.fireAfterJobCancelled(job, e);
        }

        logger.debug("After - Cancelling Request with Id: {}", requestId);
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


}
