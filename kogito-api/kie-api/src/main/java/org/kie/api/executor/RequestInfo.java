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
     * @return status of the request
     */
    STATUS getStatus();

    /**
     * @return unique id of the request
     */
    Long getId();

    /**
     * @return list of errors for this request if any
     */
    List<? extends ErrorInfo> getErrorInfo();

    /**
     * @return number of retries available for this request
     */
    int getRetries();

    /**
     * @return number of already executed attempts
     */
    int getExecutions();

    /**
     * @return command name for this request
     */
    String getCommandName();

    /**
     * @return business key assigned to this request
     */
    String getKey();

    /**
     * @return descriptive message assigned to this request
     */
    String getMessage();

    /**
     * @return time that this request shall be executed (for the first attempt)
     */
    Date getTime();

    /**
     * @return serialized bytes of the contextual request data
     */
    byte[] getRequestData();

    /**
     * @return serialized bytes of the response data
     */
    byte[] getResponseData();
}
