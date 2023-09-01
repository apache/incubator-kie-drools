package org.drools.commands.runtime;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;

public class KBuilderSetPropertyCommand implements ExecutableCommand<Void> {
	
    private String kbuilderConfId;
    private String name;
    private String value;

    public KBuilderSetPropertyCommand() {
    }
    
    public KBuilderSetPropertyCommand(String kbuilderConfId, String name, String value) {
        this.kbuilderConfId = kbuilderConfId;
        this.name = name;
        this.value = value;
    }
    
    public Void execute(Context context) {
       KnowledgeBuilderConfiguration kconf = (KnowledgeBuilderConfiguration) context.get(kbuilderConfId);
       kconf.setProperty(this.name, this.value);
       return null;
    }

}
