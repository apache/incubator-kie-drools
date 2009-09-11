package org.drools.command.vsm;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.ResourceConfiguration;
import org.drools.builder.ResourceType;
import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.io.Resource;
import org.drools.vsm.ServiceManager;
import org.drools.vsm.ServiceManagerServer;

public class ServiceManagerClientConnectCommand
    implements
    GenericCommand<Integer> {

    public ServiceManagerClientConnectCommand() {
    }
 
    
    public Integer execute(Context context) {
        ServiceManagerServer server = ( ServiceManagerServer ) ((ServiceManagerServerContext)context).getServiceManager();
        return server.getSessionIdCounter().getAndIncrement();
    }

}
