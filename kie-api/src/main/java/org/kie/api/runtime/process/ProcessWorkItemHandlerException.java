/**
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
package org.kie.api.runtime.process;

public class ProcessWorkItemHandlerException extends RuntimeException {

    private static final long serialVersionUID = 328927887939759492L;

    private static final int MAX_NUMBER_OF_RETRIES = 3;
    public enum HandlingStrategy {
        RETRY, COMPLETE, ABORT, RETHROW
    }

    private String processId;
    private HandlingStrategy strategy;
    private int retries;

    public ProcessWorkItemHandlerException(String processId, String strategy, Throwable cause) {        
        this(processId, HandlingStrategy.valueOf(strategy), cause, MAX_NUMBER_OF_RETRIES);
    }

    public ProcessWorkItemHandlerException(String processId, String strategy, Throwable cause, int retries) {
        this(processId, HandlingStrategy.valueOf(strategy), cause, retries);
    }

    public ProcessWorkItemHandlerException(String processId, HandlingStrategy strategy, Throwable cause) {
        this(processId, strategy, cause, MAX_NUMBER_OF_RETRIES);
    }

    public ProcessWorkItemHandlerException(String processId, HandlingStrategy strategy, Throwable cause, int retries) {
        super(cause);
        this.processId = processId;
        this.strategy = strategy;
        this.retries = retries;
        if (processId == null || strategy == null) {
            throw new IllegalArgumentException("Process id and strategy are required");
        }
        if (retries < 0) {
            throw new IllegalArgumentException("Retries cannot be negative");
        }
    }

    public String getProcessId() {
        return processId;
    }

    public HandlingStrategy getStrategy() {
        return strategy;
    }

    public int getRetries() {
        return retries;
    }

}
