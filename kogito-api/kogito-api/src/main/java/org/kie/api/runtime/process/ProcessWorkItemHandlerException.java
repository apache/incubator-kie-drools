/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api.runtime.process;

public class ProcessWorkItemHandlerException extends RuntimeException {

    private static final long serialVersionUID = -5953387125605633663L;

    public enum HandlingStrategy {
        RETRY, COMPLETE, ABORT, RETHROW
    }

    private String processId;
    private HandlingStrategy strategy;
    
    public ProcessWorkItemHandlerException(String processId, String strategy, Throwable cause) {        
        this(processId, HandlingStrategy.valueOf(strategy), cause);
    }

    public ProcessWorkItemHandlerException(String processId, HandlingStrategy strategy, Throwable cause) {
        super(cause);
        this.processId = processId;
        this.strategy = strategy;
        if (processId == null || strategy == null) {
            throw new IllegalArgumentException("Process id and strategy are required");
        }
    }

    public String getProcessId() {
        return processId;
    }

    public HandlingStrategy getStrategy() {
        return strategy;
    }

}
