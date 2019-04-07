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

import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;


public class AsyncSignalEventCommand implements Command {

    @Override
    public ExecutionResults execute(CommandContext ctx) throws Exception {
        String deploymentId = (String) ctx.getData("deploymentId");
        if (deploymentId == null) {
            deploymentId = (String) ctx.getData("DeploymentId");
        }
        Long processInstanceId = (Long) ctx.getData("processInstanceId");
        if (processInstanceId == null) {
            processInstanceId = (Long) ctx.getData("ProcessInstanceId");
        }
        String signal = (String) ctx.getData("Signal");
        Object event = ctx.getData("Event");
        
        if (deploymentId == null || signal == null) {
            throw new IllegalArgumentException("Deployment id and signal name is required");
        }
        
        RuntimeManager runtimeManager = RuntimeManagerRegistry.get().getManager(deploymentId);
        if (runtimeManager == null) {
            throw new IllegalArgumentException("No runtime manager found for deployment id " + deploymentId);  
        }
        RuntimeEngine engine = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));        
        try {
            engine.getKieSession().signalEvent(signal, event, processInstanceId);
            
            return new ExecutionResults();
        } finally {
            runtimeManager.disposeRuntimeEngine(engine);
        }
    }

}
