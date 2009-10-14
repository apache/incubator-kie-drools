package org.drools.runtime.pipeline.impl;

import org.drools.runtime.CommandExecutor;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.pipeline.ResultHandler;
import org.drools.runtime.pipeline.StatelessKnowledgeSessionPipelineContext;
import org.drools.vsm.ServiceManager;

public class ServiceManagerPipelineContextImpl extends BasePipelineContext {

    private ServiceManager serviceManager;

    private CommandExecutor commandExecutor;
    
    public ServiceManagerPipelineContextImpl(ServiceManager serviceManager,
                                             ClassLoader classLoader) {
        this( serviceManager,
              classLoader,
              null );
    }

    public ServiceManagerPipelineContextImpl(ServiceManager serviceManager,
                                             ClassLoader classLoader,
                                             ResultHandler resultHandler) {
        super( classLoader,
               resultHandler );
        this.serviceManager = serviceManager;
    }

    public ServiceManager getServiceManager() {
        return this.serviceManager;
    }

    public CommandExecutor getCommandExecutor() {
        return this.commandExecutor;
    }

    public void setCommandExecutor(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }
    
    

}
