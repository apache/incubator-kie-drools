package org.drools.command.builder;

import java.util.Collection;

import org.drools.builder.KnowledgeBuilder;
import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.definition.KnowledgePackage;

public class KnowledgeBuilderGetKnowledgePackagesCommand
    implements
    GenericCommand<Collection<KnowledgePackage>> {

    private String outIdentifier;

    public KnowledgeBuilderGetKnowledgePackagesCommand() {
    }

    public KnowledgeBuilderGetKnowledgePackagesCommand(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public Collection<KnowledgePackage> execute(Context context) {
        KnowledgeBuilder kbuilder = ((KnowledgeCommandContext) context).getKnowledgeBuilder();
        return kbuilder.getKnowledgePackages();
    }

}
