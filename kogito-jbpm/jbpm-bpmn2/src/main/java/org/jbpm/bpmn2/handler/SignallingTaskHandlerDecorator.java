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

import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

/**
 * This class will wrap a {@link WorkItemHandler} instance so that an event (signal, error or other) can be sent to the process 
 * instance if and when the wrapped {@link WorkItemHandler} instance throws an exception (during a 
 * {@link WorkItemHandler#executeWorkItem(WorkItem, WorkItemManager)} or 
 * {@link WorkItemHandler#abortWorkItem(WorkItem, WorkItemManager)} method. 
 * 
 * </p>In order to prevent an endless loop, the signal will only be sent once. If the signal should be sent the next time the same
 * wrapped {@link WorkItemHandler} instance throws an exception, the {@link SignallingTaskHandlerDecorator} instance must either be
 * reset via the {@link SignallingTaskHandlerDecorator#clear()} or {@link SignallingTaskHandlerDecorator#clearProcessInstance(Long)}
 * methods.
 * <p>Otherwise, the number of exceptions handled can be changed via the {@link WorkItemHandler#setExceptionCountLimit} method.
 * 
 * </p>This class is <b>not</b> thread-safe.
 */
public class SignallingTaskHandlerDecorator extends AbstractExceptionHandlingTaskHandler {

    final private String eventType;
    
    private String workItemExceptionParameterName = "jbpm.workitem.exception";
    
    final private Map<Long, Integer> processInstanceExceptionMap = new HashMap<Long, Integer>();
    private int exceptionCountLimit = 1;
    
    /**
     * Constructs an instance that uses the given <code>eventType</code> parameter to signal the process instance using the given
     * {@link KieSession} <code>ksession</code> parameter when an instance of the class specified by the 
     * <code>originalTaskHandlerClass</code> throws an exception upon {@link WorkItemHandler#executeWorkItem(WorkItem, WorkItemManager)}
     * @param originalTaskHandlerClass
     * @param eventType
     */
    public SignallingTaskHandlerDecorator(Class<? extends WorkItemHandler> originalTaskHandlerClass, String eventType) {
        super(originalTaskHandlerClass);
        this.eventType = eventType;
    }
    
    public SignallingTaskHandlerDecorator(WorkItemHandler originalTaskHandler, String eventType) {
        super(originalTaskHandler);
        this.eventType = eventType;
    }
    
    public SignallingTaskHandlerDecorator(Class<? extends WorkItemHandler> originalTaskHandlerClass, String eventType, int exceptionCountLimit) {
        super(originalTaskHandlerClass);
        this.eventType = eventType;
        this.exceptionCountLimit = exceptionCountLimit;
    }
    
    public SignallingTaskHandlerDecorator(WorkItemHandler originalTaskHandler, String eventType, int exceptionCountLimit) {
        super(originalTaskHandler);
        this.eventType = eventType;
        this.exceptionCountLimit = exceptionCountLimit;
    }
    
    public void setWorkItemExceptionParameterName(String parameterName) { 
        this.workItemExceptionParameterName = parameterName;
    }
    
    public String getWorkItemExceptionParameterName() { 
        return this.workItemExceptionParameterName;
    }

    @Override
    public void handleExecuteException(Throwable cause, WorkItem workItem, WorkItemManager manager) {
        if( getAndIncreaseExceptionCount(workItem.getProcessInstanceId()) < exceptionCountLimit ) { 
            workItem.getParameters().put(this.workItemExceptionParameterName, cause);
            ((org.drools.core.process.instance.WorkItemManager) manager).signalEvent(this.eventType, (org.drools.core.process.instance.WorkItem) workItem, workItem.getProcessInstanceId());
        } else { 
            if( cause instanceof RuntimeException ) { 
                throw (RuntimeException) cause;
            } else { 
                throw new WorkItemHandlerRuntimeException(cause, 
                        "Signalling process instance " + workItem.getProcessInstanceId() + " with '" + this.eventType + "' resulted this exception.");
            }
        }
    }

    @Override
    public void handleAbortException(Throwable cause, WorkItem workItem, WorkItemManager manager) {
        if( getAndIncreaseExceptionCount(workItem.getProcessInstanceId()) < exceptionCountLimit ) { 
            workItem.getParameters().put(this.workItemExceptionParameterName, cause);
            ((org.drools.core.process.instance.WorkItemManager) manager).signalEvent(this.eventType, (org.drools.core.process.instance.WorkItem) workItem, workItem.getProcessInstanceId());
        }
    }

    private int getAndIncreaseExceptionCount(Long processInstanceId) { 
        Integer count = processInstanceExceptionMap.get(processInstanceId);
        if( count == null ) {
            count = 0;
        }
        processInstanceExceptionMap.put( processInstanceId, ++count);
        return (count-1);
    }
    
    public void setExceptionCountLimit(int limit) { 
        this.exceptionCountLimit = limit;
    }
    
    public void clearProcessInstance(Long processInstanceId ) { 
       processInstanceExceptionMap.remove(processInstanceId);
    }
    
    public void clear() { 
       processInstanceExceptionMap.clear();
    }
    
}
