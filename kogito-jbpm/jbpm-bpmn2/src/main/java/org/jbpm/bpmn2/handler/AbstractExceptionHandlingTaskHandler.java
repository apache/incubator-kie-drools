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

package org.jbpm.bpmn2.handler;

import org.kie.api.runtime.process.*;

public abstract class AbstractExceptionHandlingTaskHandler implements WorkItemHandler {

    private WorkItemHandler originalTaskHandler;

    
    public AbstractExceptionHandlingTaskHandler(WorkItemHandler originalTaskHandler) { 
        this.originalTaskHandler = originalTaskHandler;
    }
    
    public AbstractExceptionHandlingTaskHandler(Class<? extends WorkItemHandler> originalTaskHandlerClass) { 
        Class<?> [] clsParams = {};
        Object [] objParams = {};
        try { 
            this.originalTaskHandler = originalTaskHandlerClass.getConstructor(clsParams).newInstance(objParams);
        } catch( Exception e ) { 
            throw new UnsupportedOperationException("The " + WorkItemHandler.class.getSimpleName() + " parameter must have a public no-argument constructor." );
        }
    }
    
    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        try {
            originalTaskHandler.executeWorkItem(workItem, manager);
        } catch( Throwable cause ) { 
           handleExecuteException(cause, workItem, manager);
        }
    }
    
    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        try {
            originalTaskHandler.abortWorkItem(workItem, manager);
        } catch( RuntimeException re ) { 
           handleAbortException(re, workItem, manager);
        }
    }

    public WorkItemHandler getOriginalTaskHandler() {
        return originalTaskHandler;
    }



    public abstract void handleExecuteException(Throwable cause, WorkItem workItem, WorkItemManager manager);
    public abstract void handleAbortException(Throwable cause, WorkItem workItem, WorkItemManager manager);

    
}
