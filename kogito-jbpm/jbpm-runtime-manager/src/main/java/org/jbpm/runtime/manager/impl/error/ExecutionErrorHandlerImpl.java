/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.runtime.manager.impl.error;

import java.util.List;

import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.task.model.Task;
import org.kie.internal.runtime.error.ExecutionError;
import org.kie.internal.runtime.error.ExecutionErrorContext;
import org.kie.internal.runtime.error.ExecutionErrorFilter;
import org.kie.internal.runtime.error.ExecutionErrorHandler;
import org.kie.internal.runtime.error.ExecutionErrorStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExecutionErrorHandlerImpl implements ExecutionErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(ExecutionErrorHandlerImpl.class);   
    
    private List<ExecutionErrorFilter> filters;
    private ExecutionErrorStorage storage;
    
    private NodeInstance firstExecutedNode;
    
    private NodeInstance lastExecutedNode;
    
    private Task lastExecutedTask;
    
    public ExecutionErrorHandlerImpl(List<ExecutionErrorFilter> filters, ExecutionErrorStorage storage) {
        super();
        this.filters = filters;
        this.storage = storage;
    }

    @Override
    public void processing(NodeInstance nodeInstance) {
        if (this.firstExecutedNode == null) {
            this.firstExecutedNode = nodeInstance;
        }        
        logger.debug("Node instance {} is being executed", nodeInstance);
        
        this.lastExecutedNode = nodeInstance;
    }

    @Override
    public void processing(Task task) {
        
        logger.debug("Task instance {} is being executed", task);
        
        this.lastExecutedTask = task;
    }

    @Override
    public void processed(NodeInstance nodeInstance) {
        if (this.firstExecutedNode == null) {
            this.firstExecutedNode = nodeInstance;
        }
        
        logger.debug("Node instance {} successfully executed", nodeInstance);
    }

    @Override
    public void processed(Task task) {

        logger.debug("Task instance {} successfully executed", task);
    }

    @Override
    public void handle(Throwable cause) {
        ExecutionErrorContext errorContext = new ExecutionErrorContext(cause, lastExecutedNode, lastExecutedTask, firstExecutedNode);
        for (ExecutionErrorFilter filter : filters) {
            
            if (filter.accept(errorContext)) {
                ExecutionError error = filter.filter(errorContext);
                logger.debug("Filter {} returned {} for {}", filter, error, cause);
                if (error != null) {
                    
                    try {
                        storage.store(error);
                        logger.debug("Error event {} stored successfully in {}", error, storage);
                    } catch (Throwable e) {
                        logger.warn("Could not persist execution error {} due to {}", error, e.getMessage());
                        logger.debug("Stack trace ", e);
                    }
                    
                } else {
                    logger.debug("Filter {} accepted error {} but didn't produce results (error should be ignored)", filter, cause);
                }
                return;
            }
        }
        
        // in case it ended up here - meaning not filters dealt with it, simply log the error
        logger.error("Unexpected error during processing ", cause);
    }

}
