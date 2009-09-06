package org.drools.command;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceConfiguration;
import org.drools.builder.ResourceType;
import org.drools.command.Command;
import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.io.Resource;

public class GetVariableCommand
    implements
    GenericCommand<Object> {
    private String identifier;
    private String contextName;

    public GetVariableCommand(String identifier,
                              String contextName) {
        this.identifier = identifier;
        this.contextName = contextName;
    }

    public Object execute(Context ctx) {
        ctx = ctx.getContextManager().getContext( this.contextName );
        return ctx.get( this.identifier );
    }

}
