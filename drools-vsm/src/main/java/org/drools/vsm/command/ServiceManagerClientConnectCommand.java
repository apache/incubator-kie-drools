package org.drools.vsm.command;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.runtime.impl.ExecutionResultImpl;
import org.drools.vsm.ServiceManagerData;

public class ServiceManagerClientConnectCommand
    implements
    GenericCommand<Integer> {
    
    private String outIdentifier;

    

    public ServiceManagerClientConnectCommand(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }



    public Integer execute(Context context) {
        ServiceManagerData data = (ServiceManagerData) context.get( ServiceManagerData.SERVICE_MANAGER_DATA );
        
        Integer sessionId = data.getSessionIdCounter().getAndIncrement();
        if ( this.outIdentifier != null ) {
            ((ExecutionResultImpl)((KnowledgeCommandContext) context).getExecutionResults()).getResults().put( this.outIdentifier, sessionId );
        } 
        
        return sessionId;
    }

}
