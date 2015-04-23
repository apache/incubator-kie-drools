package org.jbpm.process.core.async;

import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.executor.api.Command;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;


public class AsyncSignalEventCommand implements Command {

    @Override
    public ExecutionResults execute(CommandContext ctx) throws Exception {
        String deploymentId = (String) ctx.getData("DeploymentId");
        Long processInstanceId = (Long) ctx.getData("ProcessInstanceId");
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
