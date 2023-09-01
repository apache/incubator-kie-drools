package org.drools.commands;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.internal.builder.KnowledgeBuilderFactory;

public class NewKnowledgeBuilderConfigurationCommand implements ExecutableCommand<Void> {
    private String kbuilderConfId;

    public NewKnowledgeBuilderConfigurationCommand(String kbuilderConfId) {
        this.kbuilderConfId = kbuilderConfId;
    }
    
    
    public Void execute(Context context) {
        
        context.set(kbuilderConfId, 
                    KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration());
        
        return null;
    }
    
}
