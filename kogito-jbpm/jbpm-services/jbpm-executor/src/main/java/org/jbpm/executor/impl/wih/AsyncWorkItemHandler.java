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

package org.jbpm.executor.impl.wih;

import java.util.Date;
import java.util.List;

import org.drools.core.process.instance.impl.WorkItemImpl;
import org.drools.core.time.TimeUtils;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutorService;
import org.kie.api.executor.RequestInfo;
import org.kie.api.executor.STATUS;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.query.QueryContext;
import org.kie.internal.runtime.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Asynchronous work item handler that utilizes power of <code>ExecutorService</code>.
 * it expects following parameters to be present on work item for proper execution:
 * <ul>
 *  <li>CommandClass - FQCN of the command to be executed - mandatory unless this handler is configured with default command class</li>
 *  <li>Retries - number of retries for the command execution - optional</li>
 *  <li>RetryDelay - Comma separated list of time expressions (5s, 2m, 4h) to be used in case of errors and retry needed.</li>
 *  <li>Delay - optionally delay which job should be executed after given as time expression (5s, 2m, 4h) that will be calculated starting from current time</li>
 *  <li>AutoComplete - allows to use "fire and forget" style so it will not wait for job completion and allow to move on (default false)</li>
 *  <li>Priority - priority for the job execution - from 0 (the lowest) to 9 (the highest)</li>
 * </ul>
 * During execution it will set contextual data that will be available inside the command:
 * <ul>
 *  <li>businessKey - generated from process instance id and work item id in following format: [processInstanceId]:[workItemId]</li>
 *  <li>workItem - actual work item instance that is being executed (including all parameters)</li>
 *  <li>processInstanceId - id of the process instance that triggered this work item execution</li>  
 * </ul>
 * 
 * In case work item shall be aborted handler will attempt to cancel active requests based on business key (process instance id and work item id)
 */
public class AsyncWorkItemHandler implements WorkItemHandler, Cacheable {
    
    private static final Logger logger = LoggerFactory.getLogger(AsyncWorkItemHandler.class);
    
    private ExecutorService executorService;
    private String commandClass;
    
    public AsyncWorkItemHandler(ExecutorService executorService) {
        this.executorService = executorService;
    }
    
    public AsyncWorkItemHandler(ExecutorService executorService, String commandClass) {
        this(executorService);
        this.commandClass = commandClass;
    }
    
    public AsyncWorkItemHandler(Object executorService, String commandClass) {
        this((ExecutorService) executorService);
        this.commandClass = commandClass;
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        if (executorService == null || !executorService.isActive()) {
            throw new IllegalStateException("Executor is not set or is not active");
        }
        boolean autoComplete = false;
        if (workItem.getParameter("AutoComplete") != null) {
            autoComplete = Boolean.parseBoolean(workItem.getParameter("AutoComplete").toString());
        }
        
        String businessKey = buildBusinessKey(workItem);
        logger.debug("Executing work item {} with built business key {}", workItem, businessKey);
        String cmdClass = (String) workItem.getParameter("CommandClass");
        if (cmdClass == null) {
            cmdClass = this.commandClass;
        }
        logger.debug("Command class for this execution is {}", cmdClass);
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", businessKey);
        ctxCMD.setData("workItem", workItem);
        ctxCMD.setData("processInstanceId", getProcessInstanceId(workItem));
        ctxCMD.setData("deploymentId", ((WorkItemImpl)workItem).getDeploymentId());
        // in case auto complete is selected skip callback
        if (!autoComplete) {
            ctxCMD.setData("callbacks", AsyncWorkItemHandlerCmdCallback.class.getName());
        }
        
        if (workItem.getParameter("Retries") != null) {
            ctxCMD.setData("retries", Integer.parseInt(workItem.getParameter("Retries").toString()));
        }
        if (workItem.getParameter("Owner") != null) {
            ctxCMD.setData("owner", workItem.getParameter("Owner"));
        }
        
        if (workItem.getParameter("RetryDelay") != null) {
     
            ctxCMD.setData("retryDelay", workItem.getParameter("RetryDelay"));
        }
        
        if (workItem.getParameter("Priority") != null) {
            
            ctxCMD.setData("priority", Integer.parseInt(workItem.getParameter("Priority").toString()));
        }
        
        Date scheduleDate = null;
        if (workItem.getParameter("Delay") != null) {
            long delayInMillis = TimeUtils.parseTimeString((String)workItem.getParameter("Delay"));
            scheduleDate = new Date(System.currentTimeMillis() + delayInMillis);
        }
        

        logger.trace("Command context {}", ctxCMD);
        Long requestId = executorService.scheduleRequest(cmdClass, scheduleDate, ctxCMD);
        logger.debug("Request scheduled successfully with id {}", requestId);
        
        if (autoComplete) {
            logger.debug("Auto completing work item with id {}", workItem.getId());
            manager.completeWorkItem(workItem.getId(), null);
        }
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        String businessKey = buildBusinessKey(workItem);
        logger.info("Looking up for not cancelled and not done requests for business key {}", businessKey);
        List<RequestInfo> requests = executorService.getRequestsByBusinessKey(businessKey, new QueryContext());
        if (requests != null) {
            for (RequestInfo request : requests) {
                if (request.getStatus() != STATUS.CANCELLED && request.getStatus() != STATUS.DONE
                        && request.getStatus() != STATUS.ERROR && request.getStatus() != STATUS.RUNNING) {
                    logger.info("About to cancel request with id {} and business key {} request state {}",
                            request.getId(), businessKey, request.getStatus());
                    executorService.cancelRequest(request.getId());
                }
            }
        }
    }
    
    protected String buildBusinessKey(WorkItem workItem) {
        String businessKeyIn = (String) workItem.getParameter("BusinessKey");
        if (businessKeyIn != null && !businessKeyIn.isEmpty()) {
            return businessKeyIn;
        }
        
        StringBuffer businessKey = new StringBuffer();
        businessKey.append(getProcessInstanceId(workItem));
        businessKey.append(":");
        businessKey.append(workItem.getId());
        return businessKey.toString();
    }
    
    protected long getProcessInstanceId(WorkItem workItem) {
        return ((WorkItemImpl) workItem).getProcessInstanceId();
    }

    @Override
    public void close() {
        //no-op
    }
    
    

}
