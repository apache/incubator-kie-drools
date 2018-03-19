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

package org.jbpm.executor.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.input.ClassLoaderObjectInputStream;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jbpm.executor.AsyncJobException;
import org.jbpm.executor.entities.ErrorInfo;
import org.jbpm.executor.entities.RequestInfo;
import org.jbpm.executor.impl.event.ExecutorEventSupportImpl;
import org.jbpm.executor.impl.event.ExecutorEventSupport;
import org.jbpm.process.core.async.AsyncExecutionMarker;
import org.kie.api.executor.Command;
import org.kie.api.executor.CommandCallback;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.kie.api.executor.Executor;
import org.kie.api.executor.ExecutorQueryService;
import org.kie.api.executor.ExecutorStoreService;
import org.kie.api.executor.Reoccurring;
import org.kie.api.executor.STATUS;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Heart of the executor component - executes the actual tasks.
 * Handles retries and error management. Based on results of execution notifies
 * defined callbacks about the execution results.
 *
 */
public abstract class AbstractAvailableJobsExecutor {

    private static final Logger logger = LoggerFactory.getLogger(AbstractAvailableJobsExecutor.class);
    protected int retries = Integer.parseInt(System.getProperty("org.kie.executor.retry.count", "3"));

    protected Map<String, Object> contextData = new HashMap<String, Object>();
       
    protected ExecutorQueryService queryService;
   
    protected ClassCacheManager classCacheManager;
   
    protected ExecutorStoreService executorStoreService;
    
    protected ExecutorEventSupport eventSupport = new ExecutorEventSupportImpl();
    
    protected Executor executor;

    public void setEventSupport(ExecutorEventSupport eventSupport) {
        this.eventSupport = eventSupport;
    }

    public void setQueryService(ExecutorQueryService queryService) {
        this.queryService = queryService;
    }    

    public void setClassCacheManager(ClassCacheManager classCacheManager) {
        this.classCacheManager = classCacheManager;
    }
    
	public void setExecutorStoreService(ExecutorStoreService executorStoreService) {
		this.executorStoreService = executorStoreService;
	}
         
	public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public void executeGivenJob(RequestInfo request) {
        Throwable exception = null;
        try {
            AsyncExecutionMarker.markAsync();
            eventSupport.fireBeforeJobExecuted(request, null);
            if (request != null) {
            	boolean processReoccurring = false;
            	Command cmd = null;
                CommandContext ctx = null;
                ExecutionResults results = null;
                List<CommandCallback> callbacks = null;
                ClassLoader cl = getClassLoader(request.getDeploymentId());
                try {
    
                    logger.debug("Processing Request Id: {}, status {} command {}", request.getId(), request.getStatus(), request.getCommandName());
                    byte[] reqData = request.getRequestData();
                    if (reqData != null) {
                        ObjectInputStream in = null;
                        try {
                            in = new ClassLoaderObjectInputStream(cl, new ByteArrayInputStream(reqData));
                            ctx = (CommandContext) in.readObject();
                        } catch (IOException e) {                        
                            logger.warn("Exception while serializing context data", e);
                            return;
                        } finally {
                            if (in != null) {
                                in.close();
                            }
                        }
                    }
                    if (request.getResponseData() == null) {                        
                       
                        for (Map.Entry<String, Object> entry : contextData.entrySet()) {
                        	ctx.setData(entry.getKey(), entry.getValue());
                        }
                        // add class loader so internally classes can be created with valid (kjar) deployment
                        ctx.setData("ClassLoader", cl);
                                                
                        cmd = classCacheManager.findCommand(request.getCommandName(), cl);
                        // increment execution counter directly to cover both success and failure paths
                        request.setExecutions(request.getExecutions() + 1);                        
                        results = cmd.execute(ctx);
                      
                        
                        if (results == null) {
                            results = new ExecutionResults();
                        }
                        try {
                            ByteArrayOutputStream bout = new ByteArrayOutputStream();
                            ObjectOutputStream out = new ObjectOutputStream(bout);
                            out.writeObject(results);
                            byte[] respData = bout.toByteArray();
                            request.setResponseData(respData);
                        } catch (IOException e) {
                            request.setResponseData(null);
                        }
                        results.setData("CompletedAt", new Date());                        
        
                        request.setStatus(STATUS.DONE);
                         
                        executorStoreService.updateRequest(request, null);
                        processReoccurring = true;
                    } else {
                        logger.debug("Job was already successfully executed, retrying callbacks only...");
                        byte[] resData = request.getResponseData();
                        if (resData != null) {
                            ObjectInputStream in = null;
                            try {
                                in = new ClassLoaderObjectInputStream(cl, new ByteArrayInputStream(resData));
                                results = (ExecutionResults) in.readObject();
                            } catch (IOException e) {                        
                                logger.warn("Exception while serializing response data", e);
                                return;
                            } finally {
                                if (in != null) {
                                    in.close();
                                }
                            }
                        }
                        request.setStatus(STATUS.DONE);
                        
                        executorStoreService.updateRequest(request, null);
                        processReoccurring = true;
                    }
                    // callback handling after job execution
                    callbacks = classCacheManager.buildCommandCallback(ctx, cl);                
                    
                    for (CommandCallback handler : callbacks) {
                        
                        handler.onCommandDone(ctx, results);
                    }
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Throwable e) {
                    exception = e;
                    if (callbacks == null) {
                        callbacks = classCacheManager.buildCommandCallback(ctx, cl);
                    }
                    
                	processReoccurring = handleException(request, e, ctx, callbacks);
                	
                } finally {
                    ((ExecutorImpl) executor).clearExecution(request.getId());
                    AsyncExecutionMarker.reset();
                	handleCompletion(processReoccurring, cmd, ctx);
                	eventSupport.fireAfterJobExecuted(request, exception);
                }
            }
        } catch (Exception e) {
            logger.warn("Unexpected error while processin executor's job {}", e.getMessage(), e);
        }
    }
    
    protected ClassLoader getClassLoader(String deploymentId) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (deploymentId == null) {
            return cl;
        }
        
        InternalRuntimeManager manager = ((InternalRuntimeManager)RuntimeManagerRegistry.get().getManager(deploymentId));
        if (manager != null && manager.getEnvironment().getClassLoader() != null) {            
            cl = manager.getEnvironment().getClassLoader();
        }
        
        return cl;
    }
    
    public void addContextData(String name, Object data) {
    	this.contextData.put(name, data);
    }
    
    @SuppressWarnings("unchecked")
    protected boolean handleException(RequestInfo request, Throwable e, CommandContext ctx, List<CommandCallback> callbacks) {
        logger.warn("Error during command {} error message {}", request.getCommandName(), e.getMessage(), e);
        
        ErrorInfo errorInfo = new ErrorInfo(e.getMessage(), ExceptionUtils.getStackTrace(e.fillInStackTrace()));
        errorInfo.setRequestInfo(request);

        ((List<ErrorInfo>)request.getErrorInfo()).add(errorInfo);
        logger.debug("Error Number: {}", request.getErrorInfo().size());
        if (request.getRetries() > 0) {
            request.setStatus(STATUS.RETRYING);
            request.setRetries(request.getRetries() - 1);
            
            
            // calculate next retry time
            List<Long> retryDelay = (List<Long>) ctx.getData("retryDelay");
            if (retryDelay != null) {
                long retryAdd = 0l;

                try {
                    retryAdd = retryDelay.get(request.getExecutions() - 1); // need to decrement it as executions are directly incremented upon execution
                } catch (IndexOutOfBoundsException ex) {
                    // in case there is no element matching given execution, use last one
                    retryAdd = retryDelay.get(retryDelay.size()-1);
                }
                
                request.setTime(new Date(System.currentTimeMillis() + retryAdd));                
                logger.info("Retrying request ( with id {}) - delay configured, next retry at {}", request.getId(), request.getTime());
            }
            
            logger.debug("Retrying ({}) still available!", request.getRetries());
            
            executorStoreService.updateRequest(request, ((ExecutorImpl) executor).scheduleExecution(request, request.getTime()));
            
            
            return false;
        } else {
            logger.debug("Error no retries left!");
            request.setStatus(STATUS.ERROR);
            
            executorStoreService.updateRequest(request, null);
            AsyncJobException wrappedException = new AsyncJobException(request.getId(), request.getCommandName(), e);
            if (callbacks != null) {
                for (CommandCallback handler : callbacks) {                        
                    handler.onCommandError(ctx, wrappedException);                        
                }
            }
            return true;
        }
    }
    
    protected void handleCompletion(boolean processReoccurring, Command cmd, CommandContext ctx) {
        if (processReoccurring && cmd != null && cmd instanceof Reoccurring) {
            Date current = new Date();
            Date nextScheduleTime = ((Reoccurring) cmd).getScheduleTime();
            
            if (nextScheduleTime != null && nextScheduleTime.after(current)) {
                String businessKey = (String) ctx.getData("businessKey");
                RequestInfo requestInfo = new RequestInfo();
                requestInfo.setCommandName(cmd.getClass().getName());
                requestInfo.setKey(businessKey);
                requestInfo.setStatus(STATUS.QUEUED);
                requestInfo.setTime(nextScheduleTime);
                requestInfo.setMessage("Rescheduled reoccurring job");
                requestInfo.setDeploymentId((String)ctx.getData("deploymentId"));
                requestInfo.setProcessInstanceId((Long)ctx.getData("processInstanceId"));
                requestInfo.setOwner((String)ctx.getData("owner"));
                if (ctx.getData("retries") != null) {
                    requestInfo.setRetries(Integer.valueOf(String.valueOf(ctx.getData("retries"))));
                } else {
                    requestInfo.setRetries(retries);
                }
                if (ctx != null) {
                    try {
                        // remove transient data
                        ctx.getData().remove("ClassLoader");
                        
                        ByteArrayOutputStream bout = new ByteArrayOutputStream();
                        ObjectOutputStream oout = new ObjectOutputStream(bout);
                        oout.writeObject(ctx);
                        requestInfo.setRequestData(bout.toByteArray());
                    } catch (IOException e) {
                        logger.warn("Error serializing context data", e);
                        requestInfo.setRequestData(null);
                    }
                }
                
                executorStoreService.persistRequest(requestInfo, ((ExecutorImpl) executor).scheduleExecution(requestInfo, requestInfo.getTime()));
            }
        }
    }

}
