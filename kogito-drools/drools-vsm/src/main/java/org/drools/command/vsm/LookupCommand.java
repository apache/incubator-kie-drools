package org.drools.command.vsm;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.ResourceConfiguration;
import org.drools.builder.ResourceType;
import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.io.Resource;
import org.drools.runtime.impl.ExecutionResultImpl;
import org.drools.vsm.ServiceManager;
import org.drools.vsm.ServiceManagerServer;
import org.drools.vsm.ServiceManagerServerResponseHandler;

public class LookupCommand
    implements
    GenericCommand<String> {
    
    private String identifier;
    
    private String outIdentifier;

    public LookupCommand(String identfier) {
        this.identifier = identfier;
    }
    
    public LookupCommand(String identfier,
                         String outIdentifier) {
        this.identifier = identfier;
        this.outIdentifier = outIdentifier;
    }



    public String execute(Context context) {
        ServiceManagerServer server = (ServiceManagerServer) context.get( ServiceManagerServer.SERVICE_MANAGER );
        
        String instanceId = (String ) server.getRoot().get( identifier );        
        
        if ( this.outIdentifier != null ) {
            ((ExecutionResultImpl) ((KnowledgeCommandContext) context).getExecutionResults()).getResults().put( this.outIdentifier,
                                                                                                                instanceId );
        }
        return instanceId;
    }

}
