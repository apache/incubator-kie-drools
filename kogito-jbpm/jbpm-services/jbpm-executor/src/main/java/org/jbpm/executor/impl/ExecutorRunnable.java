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

package org.jbpm.executor.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.input.ClassLoaderObjectInputStream;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jbpm.executor.entities.ErrorInfo;
import org.jbpm.executor.entities.RequestInfo;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.shared.services.impl.commands.MergeObjectCommand;
import org.kie.internal.executor.api.Command;
import org.kie.internal.executor.api.CommandCallback;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;
import org.kie.internal.executor.api.ExecutorQueryService;
import org.kie.internal.executor.api.STATUS;
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
public class ExecutorRunnable implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ExecutorRunnable.class);

   
    @Inject
    private ExecutorQueryService queryService;
   
    @Inject
    private ClassCacheManager classCacheManager;

    @Inject
    private TransactionalCommandService commandService;
   
    public void setCommandService(TransactionalCommandService commandService) {
        this.commandService = commandService;
    }


    public void setQueryService(ExecutorQueryService queryService) {
        this.queryService = queryService;
    }    

    public void setClassCacheManager(ClassCacheManager classCacheManager) {
        this.classCacheManager = classCacheManager;
    }


    @SuppressWarnings("unchecked")
    public void run() {
        logger.debug("Executor Thread {} Waking Up!!!", this.toString());
        try {
            RequestInfo request = (RequestInfo) queryService.getRequestForProcessing();
            if (request != null) {
                CommandContext ctx = null;
                List<CommandCallback> callbacks = null;
                try {
    
                    logger.debug("Processing Request Id: {}, status {} command {}", request.getId(), request.getStatus(), request.getCommandName());
                    ClassLoader cl = getClassLoader(request.getDeploymentId());
                    
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
                    callbacks = classCacheManager.buildCommandCallback(ctx, cl);                
                    
                    Command cmd = classCacheManager.findCommand(request.getCommandName(), cl);
                    ExecutionResults results = cmd.execute(ctx);
                    for (CommandCallback handler : callbacks) {
                        
                        handler.onCommandDone(ctx, results);
                    }
                    
                    if (results != null) {
                        try {
                            ByteArrayOutputStream bout = new ByteArrayOutputStream();
                            ObjectOutputStream out = new ObjectOutputStream(bout);
                            out.writeObject(results);
                            byte[] respData = bout.toByteArray();
                            request.setResponseData(respData);
                        } catch (IOException e) {
                            request.setResponseData(null);
                        }
                    }
    
                    request.setStatus(STATUS.DONE);
                    commandService.execute(new MergeObjectCommand(request));                    
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Throwable e) {
                    logger.warn("Error during command {} execution {}", request.getCommandName(), e.getMessage());
    
                    ErrorInfo errorInfo = new ErrorInfo(e.getMessage(), ExceptionUtils.getFullStackTrace(e.fillInStackTrace()));
                    errorInfo.setRequestInfo(request);
    
                    ((List<ErrorInfo>)request.getErrorInfo()).add(errorInfo);
                    logger.debug("Error Number: {}", request.getErrorInfo().size());
                    if (request.getRetries() > 0) {
                        request.setStatus(STATUS.RETRYING);
                        request.setRetries(request.getRetries() - 1);
                        request.setExecutions(request.getExecutions() + 1);
                        logger.debug("Retrying ({}) still available!", request.getRetries());
                        
                        commandService.execute(new MergeObjectCommand(request));
                    } else {
                        logger.debug("Error no retries left!");
                        request.setStatus(STATUS.ERROR);
                        request.setExecutions(request.getExecutions() + 1);
                        
                        commandService.execute(new MergeObjectCommand(request));
                        
                        if (callbacks != null) {
                            for (CommandCallback handler : callbacks) {                        
                                handler.onCommandError(ctx, e);                        
                            }
                        }
    
                    }
    
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

}
