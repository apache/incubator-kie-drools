package org.drools.command.builder;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.ResourceConfiguration;
import org.drools.builder.ResourceType;
import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.io.Resource;
import org.drools.runtime.impl.ExecutionResultImpl;

public class KnowledgeBuilderGetErrorsCommand
    implements
    GenericCommand<KnowledgeBuilderErrors> {

    private String outIdentifier;

    public KnowledgeBuilderGetErrorsCommand() {
    }
    
    public KnowledgeBuilderGetErrorsCommand(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public KnowledgeBuilderErrors execute(Context context) {
        KnowledgeBuilder kbuilder = ((KnowledgeCommandContext) context).getKnowledgeBuilder();
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if ( this.outIdentifier != null ) {
            ((ExecutionResultImpl)((KnowledgeCommandContext) context).getExecutionResults()).getResults().put( this.outIdentifier, errors );
        }
        return errors;
    }

}
