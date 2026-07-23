/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.trusty.service.common.responses;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The execution headers response.
 */
public class ExecutionsResponse {

    @JsonProperty("total")
    private int total;

    @JsonProperty("limit")
    private int limit;

    @JsonProperty("offset")
    private int offset;

    @JsonProperty("headers")
    private Collection<ExecutionHeaderResponse> headers;

    private ExecutionsResponse() {
    }

    public ExecutionsResponse(int total, int returnedRecords, int offset, Collection<ExecutionHeaderResponse> headers) {
        this.total = total;
        this.limit = returnedRecords;
        this.offset = offset;
        this.headers = headers;
    }

    /**
     * Gets the total number of items returned.
     *
     * @return The total number of items returned.
     */
    public int getTotal() {
        return total;
    }

    /**
     * Gets the requested limit.
     *
     * @return The maximum number of items to be returned.
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Gets the starting offset for the pagination.
     *
     * @return The pagination offset.
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Gets the execution headers.
     *
     * @return THe execution headers.
     */
    public Collection<ExecutionHeaderResponse> getHeaders() {
        return headers;
    }
}