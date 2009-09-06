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

public class KnowledgeBuilderHasErrorsCommand
    implements
    GenericCommand<Boolean> {

    private String outIdentifier;

    public KnowledgeBuilderHasErrorsCommand() {
    }
    
    public KnowledgeBuilderHasErrorsCommand(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }    

    public Boolean execute(Context context) {
        KnowledgeBuilder kbuilder = ((KnowledgeCommandContext) context).getKnowledgeBuilder();
        boolean errors = kbuilder.hasErrors();
        if ( this.outIdentifier != null ) {
            ((ExecutionResultImpl)((KnowledgeCommandContext) context).getExecutionResults()).getResults().put( this.outIdentifier, errors );
        }
        return errors;
    }

}
