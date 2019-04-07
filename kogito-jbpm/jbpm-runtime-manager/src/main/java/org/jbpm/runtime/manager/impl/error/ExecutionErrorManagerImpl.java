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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

import org.jbpm.process.instance.impl.NoOpExecutionErrorHandler;
import org.kie.internal.runtime.error.ExecutionErrorFilter;
import org.kie.internal.runtime.error.ExecutionErrorHandler;
import org.kie.internal.runtime.error.ExecutionErrorManager;
import org.kie.internal.runtime.error.ExecutionErrorStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExecutionErrorManagerImpl implements ExecutionErrorManager {

    private static final Logger logger = LoggerFactory.getLogger(ExecutionErrorManagerImpl.class);
    
    private static ThreadLocal<ExecutionErrorHandler> handler = new ThreadLocal<>();
    
    private List<ExecutionErrorFilter> filters = new ArrayList<>();
    private ExecutionErrorStorage storage;
            

    public ExecutionErrorManagerImpl(ExecutionErrorStorage storage) {
        ServiceLoader<ExecutionErrorFilter> discoveredFilters = ServiceLoader.load(ExecutionErrorFilter.class);
        
        discoveredFilters.forEach(filter -> filters.add(filter));        
        filters.sort((ExecutionErrorFilter f1, ExecutionErrorFilter f2) -> { 
            return f1.getPriority().compareTo(f2.getPriority());
        }); 
        logger.debug("Error handling filters {}", this.filters);
        
        this.storage = storage;
        logger.debug("Execution error storage {}", this.storage);
    }
    
    @Override
    public ExecutionErrorHandler getHandler() {
        ExecutionErrorHandler activeHandler = handler.get();
        if (activeHandler == null) {
            activeHandler = new NoOpExecutionErrorHandler();
        }
        return activeHandler;
    }
    
    @Override
    public ExecutionErrorStorage getStorage() {
        return storage;
    }
    
    public ExecutionErrorHandler createHandler() {
        if (handler.get() == null) {        
            handler.set(new ExecutionErrorHandlerImpl(Collections.unmodifiableList(filters), storage));
        }
        return getHandler();
    }
    
    public void closeHandler() {
        handler.set(null);
    }

}
