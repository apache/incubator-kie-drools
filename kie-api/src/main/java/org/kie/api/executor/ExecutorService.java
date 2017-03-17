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

package org.kie.api.executor;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.kie.api.runtime.manager.RuntimeManagerFactory.Factory;
import org.kie.api.runtime.query.QueryContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
     * if present, otherwise use simple 'default-executor'. Alternatively an jbpm-executor.id file can be dropped on root
     * of the classpath to provide application scoped id instead of JVM scoped (system property)
     */
    public static final String EXECUTOR_ID = IdProvider.get();

    List<RequestInfo> getQueuedRequests(QueryContext queryContext);

    List<RequestInfo> getCompletedRequests(QueryContext queryContext);

    List<RequestInfo> getInErrorRequests(QueryContext queryContext);

    List<RequestInfo> getCancelledRequests(QueryContext queryContext);

    List<ErrorInfo> getAllErrors(QueryContext queryContext);

    List<RequestInfo> getAllRequests(QueryContext queryContext);

    List<RequestInfo> getRequestsByStatus(List<STATUS> statuses, QueryContext queryContext);

    List<RequestInfo> getRequestsByBusinessKey(String businessKey, QueryContext queryContext);
    
    List<RequestInfo> getRequestsByBusinessKey(String businessKey, List<STATUS> statuses, QueryContext queryContext);

    List<RequestInfo> getRequestsByCommand(String command, QueryContext queryContext);
    
    List<RequestInfo> getRequestsByCommand(String command, List<STATUS> statuses, QueryContext queryContext);
    
    List<RequestInfo> getRequestsByDeployment(String deploymentId, List<STATUS> statuses, QueryContext queryContext);
    
    List<RequestInfo> getRequestsByProcessInstance(Long processInstanceId, List<STATUS> statuses, QueryContext queryContext);

    int clearAllRequests();

    int clearAllErrors();

    Long scheduleRequest(String commandName, CommandContext ctx);

    void cancelRequest(Long requestId);
    
    void updateRequestData(Long requestId, Map<String, Object> data);

    void init();

    void destroy();

    boolean isActive();

    int getInterval();

    void setInterval(int waitTime);

    int getRetries();

    void setRetries(int defaultNroOfRetries);

    int getThreadPoolSize();

    void setThreadPoolSize(int nroOfThreads);

    TimeUnit getTimeunit();

    void setTimeunit(TimeUnit timeunit);

    List<RequestInfo> getPendingRequests(QueryContext queryContext);

    List<RequestInfo> getPendingRequestById(Long id);

    Long scheduleRequest(String commandId, Date date, CommandContext ctx);

    List<RequestInfo> getRunningRequests(QueryContext queryContext);

    List<RequestInfo> getFutureQueuedRequests(QueryContext queryContext);

    RequestInfo getRequestById(Long requestId);

    List<ErrorInfo> getErrorsByRequestId(Long requestId);

    class IdProvider {
        private static boolean initialized = false;
        private static String EXECUTOR_ID;
        private static Logger logger = LoggerFactory.getLogger(Factory.class);



        public static String get() {
            if (!initialized) {
                EXECUTOR_ID = create();
            }
            return EXECUTOR_ID;
        }

        private static synchronized String create() {
            initialized = true;
            String idSystemProperty = System.getProperty("org.kie.executor.id",
                    System.getProperty("org.uberfire.cluster.local.id", "default-executor"));
            try {
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("/jbpm-executor.properties");
                if (is != null) {
                    Properties executorProps = new Properties();
                    executorProps.load(is);

                    return idSystemProperty+ "-" + executorProps.getProperty("executor.id");
                }
            } catch (Exception e) {
                logger.warn("Unable to find executor id due to '{}', using default...", e.getMessage());
            }
            return idSystemProperty;
        }
    }

}
