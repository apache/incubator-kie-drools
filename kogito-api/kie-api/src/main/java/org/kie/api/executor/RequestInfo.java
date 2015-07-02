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

/**
 * Represents request information for the executor service.
 *
 */
public interface RequestInfo {

    /**
     * Sets status for the request
     * @param status
     */
    void setStatus(STATUS status);
    
    /**
     * Returns status of the request
     * @return
     */
    STATUS getStatus();
    
    /**
     * Returns unique id of the request
     * @return
     */
    Long getId();
    
    /**
     * Returns list of errors for this request if any
     * @return
     */
    List<? extends ErrorInfo> getErrorInfo();
    
    /**
     * Returns number of retries available for this request
     * @return
     */
    int getRetries();
    
    /**
     * Returns number of already executed attempts
     * @return
     */
    int getExecutions();
    
    /**
     * Returns command name for this request
     * @return
     */
    String getCommandName();
    
    /**
     * Returns business key assigned to this request
     * @return
     */
    String getKey();
    
    /**
     * Returns descriptive message assigned to this request
     * @return
     */
    String getMessage();
    
    /**
     * Returns time that this request shall be executed (for the first attempt)
     * @return
     */
    Date getTime();
    
    /**
     * Serialized bytes of the contextual request data
     * @return
     */
    byte[] getRequestData();
    
    /**
     * Serialized bytes of the response data
     * @return
     */
    byte[] getResponseData();
}
