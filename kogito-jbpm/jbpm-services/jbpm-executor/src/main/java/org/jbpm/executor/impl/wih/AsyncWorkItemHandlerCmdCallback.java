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

import java.util.Collection;

import org.drools.core.command.impl.RegistryContext;
import org.jbpm.executor.AsyncJobException;
import org.jbpm.process.core.context.exception.ExceptionScope;
import org.jbpm.process.instance.context.exception.ExceptionScopeInstance;
import org.jbpm.process.instance.impl.NoOpExecutionErrorHandler;
import org.jbpm.runtime.manager.impl.AbstractRuntimeManager;
import org.jbpm.runtime.manager.impl.error.ExecutionErrorManagerImpl;
import org.jbpm.workflow.instance.NodeInstanceContainer;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.node.WorkItemNodeInstance;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.executor.CommandCallback;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.internal.runtime.error.ExecutionErrorHandler;
import org.kie.internal.runtime.error.ExecutionErrorManager;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dedicated callback for <code>AsyncWorkItemHandler</code> that is responsible for:
 * <ul>
 *  <li>completing work item in case of successful execution</li>
 *  <li>attempting to handle exception (by utilizing ExceptionScope mechanism) in case of unsuccessful execution</li>
 * </ul>
 */
public class AsyncWorkItemHandlerCmdCallback implements CommandCallback {
    
    private static final Logger logger = LoggerFactory.getLogger(AsyncWorkItemHandlerCmdCallback.class);

    @Override
    public void onCommandDone(CommandContext ctx, ExecutionResults results) {
        WorkItem workItem = (WorkItem) ctx.getData("workItem");
        logger.debug("About to complete work item {}", workItem);
        
        // find the right runtime to do the complete
        RuntimeManager manager = getRuntimeManager(ctx);
        RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get((Long) ctx.getData("processInstanceId")));
        try {
            engine.getKieSession().getWorkItemManager().completeWorkItem(workItem.getId(), results == null? null : results.getData());
        } finally {
            manager.disposeRuntimeEngine(engine);
        }
    }

    @Override
    public void onCommandError(CommandContext ctx, final Throwable exception) {
        
        final Long processInstanceId = (Long) ctx.getData("processInstanceId");
        final WorkItem workItem = (WorkItem) ctx.getData("workItem");
        
        
        // find the right runtime to do the complete
        RuntimeManager manager = getRuntimeManager(ctx);
        RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
        
        final ExecutionErrorHandler errorHandler = getExecutionErrorHandler(manager);
        
        try {
            
            boolean isErrorHandled = engine.getKieSession().execute(new ExecutableCommand<Boolean>() {

                private static final long serialVersionUID = 1L;

                @Override
                public Boolean execute(Context context) {
                    KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
                    WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.getProcessInstance(processInstanceId);        
                    NodeInstance nodeInstance = getNodeInstance(workItem, processInstance);
                    Throwable actualException = exception;
                    if (actualException instanceof AsyncJobException) {
                        actualException = exception.getCause();
                    }
                    String exceptionName = actualException.getClass().getName();
                    ExceptionScopeInstance exceptionScopeInstance = (ExceptionScopeInstance)
                        ((org.jbpm.workflow.instance.NodeInstance)nodeInstance).resolveContextInstance(ExceptionScope.EXCEPTION_SCOPE, exceptionName);
                    if (exceptionScopeInstance != null) {
                        logger.debug("Handling job error '{}' via process error handling", actualException.getMessage());
                        exceptionScopeInstance.handleException(exceptionName, actualException);
                        
                        return true;
                    } else {
                        logger.debug("No process level error handling for '{}' letting it to be handled by execution errors", exception.getMessage());
                        errorHandler.processing(nodeInstance);
                        
                        return false;
                    }
                    
                }

            });
            if (!isErrorHandled) {
                logger.debug("Error '{}' was not handled on process level, handling it via execution errors mechanism", exception.getMessage());
                errorHandler.handle(exception);
            }
        } catch(Exception e) {
          logger.error("Error when handling callback from executor", e);  
        } finally {
            manager.disposeRuntimeEngine(engine);
            closeErrorHandler(manager);
        }
    }
    
    private ExecutionErrorHandler getExecutionErrorHandler(RuntimeManager manager) {
        ExecutionErrorManager errorManager = ((AbstractRuntimeManager) manager).getExecutionErrorManager();
        if (errorManager == null) {
            return new NoOpExecutionErrorHandler();
        }
        return ((ExecutionErrorManagerImpl) errorManager).createHandler();
    }
    
    private void closeErrorHandler(RuntimeManager manager) {
        ExecutionErrorManager errorManager = ((AbstractRuntimeManager) manager).getExecutionErrorManager();
        
        if (errorManager == null) {
            return;
        }
        ((ExecutionErrorManagerImpl) errorManager).closeHandler();
    }

    protected RuntimeManager getRuntimeManager(CommandContext ctx) {
        String deploymentId = (String) ctx.getData("deploymentId");
        RuntimeManager runtimeManager = RuntimeManagerRegistry.get().getManager(deploymentId);
        
        if (runtimeManager == null) {
            throw new IllegalStateException("There is no runtime manager for deployment " + deploymentId);
        }
        
        return runtimeManager;
    }
    
    protected NodeInstance getNodeInstance(WorkItem workItem, WorkflowProcessInstance processInstance) {
        Collection<NodeInstance> nodeInstances = processInstance.getNodeInstances();
        
        return getNodeInstance(workItem, nodeInstances);
    }
    
    protected NodeInstance getNodeInstance(WorkItem workItem, Collection<NodeInstance> nodeInstances) {
    	for (NodeInstance nodeInstance : nodeInstances) {
            if (nodeInstance instanceof WorkItemNodeInstance) {
                if (((WorkItemNodeInstance) nodeInstance).getWorkItemId() == workItem.getId()) {
                    return nodeInstance;
                }
            } else if (nodeInstance instanceof NodeInstanceContainer) {
            	NodeInstance found = getNodeInstance(workItem, ((NodeInstanceContainer) nodeInstance).getNodeInstances());
            	if (found != null) {
            		return found;
            	}
            }
        }
    	
    	return null;
    }
    
}
