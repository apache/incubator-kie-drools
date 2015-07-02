/*
 * Copyright 2013 JBoss by Red Hat.
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

package org.jbpm.executor.impl.wih;

import java.util.List;

import org.drools.core.process.instance.impl.WorkItemImpl;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutorService;
import org.kie.internal.executor.api.RequestInfo;
import org.kie.internal.executor.api.STATUS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Asynchronous work item handler that utilizes power of <code>ExecutorService</code>.
 * it expects following parameters to be present on work item for proper execution:
 * <ul>
 *  <li>CommandClass - FQCN of the command to be executed - mandatory unless this handler is configured with default command class</li>
 *  <li>Retries - number of retires for the command execution - optional</li>
 *  <li>RetryDelay - Comma separated list of time expressions (5s, 2m, 4h) to be used in case of errors and retry needed.</li>
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
public class AsyncWorkItemHandler implements WorkItemHandler {
    
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
        ctxCMD.setData("callbacks", AsyncWorkItemHandlerCmdCallback.class.getName());
        if (workItem.getParameter("Retries") != null) {
            ctxCMD.setData("retries", Integer.parseInt(workItem.getParameter("Retries").toString()));
        }
        if (workItem.getParameter("Owner") != null) {
            ctxCMD.setData("owner", workItem.getParameter("Owner"));
        }
        
        if (workItem.getParameter("RetryDelay") != null) {
     
            ctxCMD.setData("retryDelay", workItem.getParameter("RetryDelay"));
        }
        
        logger.trace("Command context {}", ctxCMD);
        Long requestId = executorService.scheduleRequest(cmdClass, ctxCMD);
        logger.debug("Request scheduled successfully with id {}", requestId);
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        String businessKey = buildBusinessKey(workItem);
        logger.info("Looking up for not cancelled and not done requests for business key {}", businessKey);
        List<RequestInfo> requests = executorService.getRequestsByBusinessKey(businessKey);
        if (requests != null) {
            for (RequestInfo request : requests) {
                if (request.getStatus() != STATUS.CANCELLED && request.getStatus() != STATUS.DONE
                        && request.getStatus() != STATUS.ERROR) {
                    logger.info("About to cancel request with id {} and business key {} request state {}",
                            request.getId(), businessKey, request.getStatus());
                    executorService.cancelRequest(request.getId());
                }
            }
        }
    }
    
    protected String buildBusinessKey(WorkItem workItem) {
        StringBuffer businessKey = new StringBuffer();
        businessKey.append(getProcessInstanceId(workItem));
        businessKey.append(":");
        businessKey.append(workItem.getId());
        return businessKey.toString();
    }
    
    protected long getProcessInstanceId(WorkItem workItem) {
        return ((WorkItemImpl) workItem).getProcessInstanceId();
    }
    
    

}
