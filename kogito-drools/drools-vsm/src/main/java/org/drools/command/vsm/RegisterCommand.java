package org.drools.command.vsm;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.ResourceConfiguration;
import org.drools.builder.ResourceType;
import org.drools.command.Context;
import org.drools.command.ExecuteCommand;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.concurrent.CommandExecutor;
import org.drools.io.Resource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.vsm.ServiceManager;
import org.drools.vsm.ServiceManagerClient;
import org.drools.vsm.ServiceManagerServer;

public class RegisterCommand
    implements
    GenericCommand<Void> {

    private String identifier;
    private String instanceId;
    private int    type;

    public RegisterCommand(String identifier,
                           String instanceId,
                           int type) {
        this.identifier = identifier;
        this.instanceId = instanceId;
        this.type = type;
    }

    public Void execute(Context context) {
        ServiceManagerServer server = (ServiceManagerServer) context.get( ServiceManagerServer.SERVICE_MANAGER );

        server.getRoot().set( identifier,
                              type + ":" + instanceId );

        return null;
    }

}
