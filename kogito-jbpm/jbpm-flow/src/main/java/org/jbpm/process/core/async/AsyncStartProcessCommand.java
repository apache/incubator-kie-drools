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

package org.jbpm.process.core.async;

import java.util.Arrays;
import java.util.Map;

import org.drools.core.process.instance.impl.WorkItemImpl;
import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkItem;
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

/**
 * Executor command that allows asynchronously start process instance based on given parameters:
 * <ul>
 *  <li>DeploymentId - either given explicitly or will be used same as the project this command is triggered from</li>
 *  <li>ProcessId - identifier of the process to start - required</li>
 *  <li>CorrelationKey - correlation key to be associated with new process instance - optional</li>
 *  <li>Variables - Map of process variables to be given to new process instance</li>
 * </ul>
 */
public class AsyncStartProcessCommand implements Command {

    private static CorrelationKeyFactory correlationKeyFactory = KieInternalServices.Factory.get().newCorrelationKeyFactory();
    
    @SuppressWarnings("unchecked")
    @Override
    public ExecutionResults execute(CommandContext ctx) throws Exception {
        String deploymentId = getDeploymentId(ctx);
        String processId = (String) getData("ProcessId", ctx);
        String correlationKey = (String) getData("CorrelationKey", ctx);
        Map<String, Object> variables = (Map<String, Object>) getData("Variables", ctx);
        
        if (deploymentId == null || processId == null) {
            throw new IllegalArgumentException("Deployment id and process id is required");
        }
        
        RuntimeManager runtimeManager = RuntimeManagerRegistry.get().getManager(deploymentId);
        if (runtimeManager == null) {
            throw new IllegalArgumentException("No runtime manager found for deployment id " + deploymentId);  
        }
        RuntimeEngine engine = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get());        
        try {
            if (correlationKey == null || correlationKey.isEmpty()) {
                engine.getKieSession().startProcess(processId, variables);
            } else {
                String[] correlationKeyProperties = correlationKey.split(",");
                
                CorrelationKey ck = correlationKeyFactory.newCorrelationKey(Arrays.asList(correlationKeyProperties));
                ((CorrelationAwareProcessRuntime) engine.getKieSession()).startProcess(processId, ck, variables);
            }
            
            return new ExecutionResults();
        } finally {
            runtimeManager.disposeRuntimeEngine(engine);
        }
    }
    
    protected Object getData(String name, CommandContext ctx) {
        if (ctx.getData(name) != null) {
            return ctx.getData(name);
        }
        WorkItem workItem = (WorkItem) ctx.getData("workItem");
        
        if (workItem != null) {
            return workItem.getParameter(name);
        }
        
        return null;
    }
    
    protected String getDeploymentId(CommandContext ctx) {
        String deploymentId = (String) ctx.getData("DeploymentId");
        if (deploymentId != null) {
            return deploymentId;
        }
        WorkItem workItem = (WorkItem) ctx.getData("workItem");
        
        if (workItem != null) {
            deploymentId = (String) workItem.getParameter("DeploymentId");
            if (deploymentId == null) {
                deploymentId = ((WorkItemImpl)workItem).getDeploymentId();
            }
        }        
            
        return deploymentId;
    }

}
