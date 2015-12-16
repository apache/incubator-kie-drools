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

package org.kie.internal.executor.api;

import java.util.List;

import org.kie.api.executor.ErrorInfo;
import org.kie.api.executor.RequestInfo;
import org.kie.api.executor.STATUS;


/**
 * @see org.kie.api.executor.ExecutorQueryService
 *
 */
public interface ExecutorQueryService extends org.kie.api.executor.ExecutorQueryService {
    
    /**
     * Returns all queued requests
     * @return
     * @deprecated use equivalent method with paging arguments
     */
    @Deprecated
    List<RequestInfo> getQueuedRequests();
    
    /**
     * Returns all comleted requests.
     * @return
     * @deprecated use equivalent method with paging arguments
     */
    @Deprecated
    List<RequestInfo> getCompletedRequests();
    
    /**
     * Returns all requests that have errors.
     * @return
     * @deprecated use equivalent method with paging arguments
     */
    @Deprecated
    List<RequestInfo> getInErrorRequests();
    
    /**
     * Returns all requests that were cancelled
     * @return
     * @deprecated use equivalent method with paging arguments
     */
    @Deprecated
    List<RequestInfo> getCancelledRequests();
    
    /**
     * Returns all errors.
     * @return
     * @deprecated use equivalent method with paging arguments
     */
    @Deprecated
    List<ErrorInfo> getAllErrors(); 
    
    /**
     * Returns all requests
     * @return
     * @deprecated use equivalent method with paging arguments
     */
    @Deprecated
    List<RequestInfo> getAllRequests(); 
    
    /**
     * Returns all currently running requests
     * @return
     * @deprecated use equivalent method with paging arguments
     */
    @Deprecated
    List<RequestInfo> getRunningRequests();
    
    /**
     * Returns requests queued for future execution
     * @return
     * @deprecated use equivalent method with paging arguments
     */
    @Deprecated
    List<RequestInfo> getFutureQueuedRequests();
    
    /**
     * Returns requests based on their status
     * @param statuses - statuses that requests should be in
     * @return
     * @deprecated use equivalent method with paging arguments
     */
    @Deprecated
    List<RequestInfo> getRequestsByStatus(List<STATUS> statuses);
    
    /**
     * Returns list of pending execution requests.
     * @return
     * @deprecated use equivalent method with paging arguments
     */
    @Deprecated
    List<RequestInfo> getPendingRequests();
}
