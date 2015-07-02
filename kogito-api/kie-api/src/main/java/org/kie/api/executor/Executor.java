/*
 * Copyright 2013 JBoss Inc
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

package org.kie.api.executor;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Core logic of executor is encapsulated by this interface. 
 * It allows to operate on request instances of the execution.
 *
 */
public interface Executor {

    /**
     * Schedules execution of given command as soon as possible.
     * @param commandName - FQCN of the command
     * @param ctx - contextual data given by executor service
     * @return unique identifier of the request
     */
    public Long scheduleRequest(String commandName, CommandContext ctx);
    
    /**
     * Schedules execution of given command on defined time.
     * @param commandName  - FQCN of the command     
     * @param date - date at which given command shall be executed
     * @param ctx - contextual data given by executor service
     * @return unique identifier of the request
     */
    public Long scheduleRequest(String commandName, Date date, CommandContext ctx);

    /**
     * Cancels active (queued, running or retrying) request
     * @param requestId - id of the request to cancel
     */
    public void cancelRequest(Long requestId);

    /**
     * Returns configured interval at which executor threads are running
     * @return
     */
    public int getInterval();

    /**
     * Sets interval at which executor threads are running. 
     * Should not be used after <code>init</code> method has been called.
     * @param waitTime
     */
    public void setInterval(int waitTime);

    /**
     * Returns configured default number of retries that shall be attempted in case of an error
     * @return
     */
    public int getRetries();

    /**
     * Sets default number of retries that shall be attempted in case of an error. 
     * Should not be used after <code>init</code> method has been called.
     * @param defaultNroOfRetries
     */
    public void setRetries(int defaultNroOfRetries);

    /**
     * Returns configured executor thread pool size
     * @return
     */
    public int getThreadPoolSize();

    /**
     * Sets default executor thread pool size. Should not be used after <code>init</code> method has been called.
     * @param nroOfThreads
     */
    public void setThreadPoolSize(int nroOfThreads);
    
    /**
     * Returns time unit configured for executor intervals
     * @return
     */
    public TimeUnit getTimeunit();
    
    /**
     * Sets time unit for executor intervals
     * @param timeunit
     */
    public void setTimeunit(TimeUnit timeunit);
    
    /**
     * Initialized executor
     */
    public void init();
    
    /**
     * Destroys executor
     */
    public void destroy();
}
