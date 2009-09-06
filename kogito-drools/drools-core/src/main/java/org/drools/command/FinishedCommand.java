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

public class FinishedCommand
    implements
    GenericCommand<Void> {

    public FinishedCommand() {
    }

    public Void execute(Context ctx) {
        return null;
    }

}
