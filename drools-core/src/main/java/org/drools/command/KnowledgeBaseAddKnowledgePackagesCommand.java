package org.drools.command;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.ResourceConfiguration;
import org.drools.builder.ResourceType;
import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.io.Resource;

public class KnowledgeBaseAddKnowledgePackagesCommand
    implements
    GenericCommand<Void> {

    public KnowledgeBaseAddKnowledgePackagesCommand() {
    }

    public Void execute(Context context) {
        KnowledgeBuilder kbuilder = ((KnowledgeCommandContext) context).getKnowledgeBuilder();
        KnowledgeBase kbase = ((KnowledgeCommandContext) context).getKnowledgeBase();
        
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        return null;
    }

}
