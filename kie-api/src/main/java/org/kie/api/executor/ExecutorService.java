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
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.kie.api.runtime.query.QueryContext;


/**
 * Top level facade that aggregates operations defined in:
 * <ul>
 *  <li><code>Executor</code></li>
 *  <li><code>ExecutorQueryService</code></li>
 *  <li><code>ExecutorAdminService</code></li>
 * </ul>
 * @see Executor
 * @see ExecutorQueryService
 * @see ExecutorAdminService
 */
public interface ExecutorService {
	
	/**
	 * Allow to use custom identifiers for the executor instance where default is to rely on local id of clustering of kie
	 * if present, otherwise use simple 'default-executor'
	 */
	public static final String EXECUTOR_ID = System.getProperty("org.kie.executor.id", 
			System.getProperty("org.uberfire.cluster.local.id", "default-executor"));

    public List<RequestInfo> getQueuedRequests(QueryContext queryContext);

    public List<RequestInfo> getCompletedRequests(QueryContext queryContext);

    public List<RequestInfo> getInErrorRequests(QueryContext queryContext);

    public List<RequestInfo> getCancelledRequests(QueryContext queryContext);

    public List<ErrorInfo> getAllErrors(QueryContext queryContext);

    public List<RequestInfo> getAllRequests(QueryContext queryContext);
    
    public List<RequestInfo> getRequestsByStatus(List<STATUS> statuses, QueryContext queryContext);
    
    public List<RequestInfo> getRequestsByBusinessKey(String businessKey, QueryContext queryContext);
    
    public List<RequestInfo> getRequestsByCommand(String command, QueryContext queryContext);

    public int clearAllRequests();

    public int clearAllErrors();

    public Long scheduleRequest(String commandName, CommandContext ctx);

    public void cancelRequest(Long requestId);

    public void init();

    public void destroy();
    
    public boolean isActive();

    public int getInterval();

    public void setInterval(int waitTime);

    public int getRetries();

    public void setRetries(int defaultNroOfRetries);

    public int getThreadPoolSize();

    public void setThreadPoolSize(int nroOfThreads);
    
    public TimeUnit getTimeunit();
   
    public void setTimeunit(TimeUnit timeunit);
    
    public List<RequestInfo> getPendingRequests(QueryContext queryContext);

    public List<RequestInfo> getPendingRequestById(Long id);

    public Long scheduleRequest(String commandId, Date date, CommandContext ctx);

    public List<RequestInfo> getRunningRequests(QueryContext queryContext);
    
    public List<RequestInfo> getFutureQueuedRequests(QueryContext queryContext);

    public RequestInfo getRequestById(Long requestId);

    public List<ErrorInfo> getErrorsByRequestId(Long requestId);
    
}
