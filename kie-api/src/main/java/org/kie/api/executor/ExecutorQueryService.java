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

import java.util.List;

import org.kie.api.runtime.query.QueryContext;

/**
 * Executor query interface that provides runtime access to data.
 */
public interface ExecutorQueryService {
    /**
     * @return list of pending execution requests.
     */
    List<RequestInfo> getPendingRequests(QueryContext queryContext);

    /**
     * @param id unique id of the request
     * @return given pending request identified by <code>id</code>
     */
    List<RequestInfo> getPendingRequestById(Long id);

    /**
     * @param id unique id of the request
     * @return request identified by <code>id</code> regardless of its status
     */
    RequestInfo getRequestById(Long id);

    /**
     * Returns requests identified by <code>businessKey</code> usually it should be only one with given
     * business key but it does not have to as same business key requests can be processed sequentially and
     * thus might be in different statuses.
     *
     * @param businessKey business key of the request
     * @return requests identified by the business key
     */
    List<RequestInfo> getRequestByBusinessKey(String businessKey, QueryContext queryContext);
    
    /**
     * Returns requests identified by <code>businessKey</code> usually it should be only one with given
     * business key but it does not have to as same business key requests can be processed sequentially and
     * thus might be in different statuses.
     *
     * @param businessKey business key of the request
     * @param statuses filter by job status
     * @param queryContext paging and sorting controls
     * @return requests identified by the business key
     */
    List<RequestInfo> getRequestsByBusinessKey(String businessKey, List<STATUS> statuses, QueryContext queryContext);

    /**
     * Returns requests that are scheduled to run given command
     * @param command command configured in the request
     * @param queryContext paging and sorting controls
     * @return requests configured with given <code>command</code>
     */
    List<RequestInfo> getRequestByCommand(String command, QueryContext queryContext);
    
    /**
     * Returns requests that are scheduled to run given command
     * @param command command configured in the request
     * @param statuses filter by job status
     * @param queryContext paging and sorting controls
     * @return requests configured with given <code>command</code>
     */
    List<RequestInfo> getRequestsByCommand(String command, List<STATUS> statuses, QueryContext queryContext);
    
    /**
     * Returns requests by deployment id
     * @param deploymentId deployment id from process execution context
     * @param statuses filter by job status
     * @param queryContext paging and sorting controls
     * @return requests scheduled for given deployment
     */
    List<RequestInfo> getRequestsByDeployment(String deploymentId, List<STATUS> statuses, QueryContext queryContext);
    
    /**
     * Returns requests by process instance id
     * @param processInstanceId process instance id from process execution context
     * @param statuses filter by job status
     * @param queryContext paging and sorting controls
     * @return requests scheduled for given deployment
     */
    List<RequestInfo> getRequestsByProcessInstance(Long processInstanceId, List<STATUS> statuses, QueryContext queryContext);

    /**
     * @param id unique id of the request
     * @return all errors (if any) for given request
     */
    List<ErrorInfo> getErrorsByRequestId(Long id);

    /**
     * @return all queued requests
     */
    List<RequestInfo> getQueuedRequests(QueryContext queryContext);

    /**
     * @return all comleted requests.
     */
    List<RequestInfo> getCompletedRequests(QueryContext queryContext);

    /**
     * @return all requests that have errors.
     */
    List<RequestInfo> getInErrorRequests(QueryContext queryContext);

    /**
     * @return all requests that were cancelled
     */
    List<RequestInfo> getCancelledRequests(QueryContext queryContext);

    /**
     * @return all errors.
     */
    List<ErrorInfo> getAllErrors(QueryContext queryContext);

    /**
     * @return all requests
     */
    List<RequestInfo> getAllRequests(QueryContext queryContext);

    /**
     * @return all currently running requests
     */
    List<RequestInfo> getRunningRequests(QueryContext queryContext);

    /**
     * @return requests queued for future execution
     */
    List<RequestInfo> getFutureQueuedRequests(QueryContext queryContext);

    /**
     * @param statuses statuses that requests should be in
     * @return requests based on their status
     */
    List<RequestInfo> getRequestsByStatus(List<STATUS> statuses, QueryContext queryContext);

    /**
     * Dedicated method for handling special case that is get the request for processing.
     * To ensure its efficient use it shall perform necessary operation to minimize risk of
     * race conditions and deadlock.
     */
    RequestInfo getRequestForProcessing();

    /**
     * Dedicated method for handling special case that is get the request for processing by id.
     * To ensure its efficient use it shall perform necessary operation to minimize risk of
     * race conditions and deadlock.
     */
    RequestInfo getRequestForProcessing(Long requestId);
}
