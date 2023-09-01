package org.drools.commands;

import org.drools.commands.impl.ContextManagerImpl;
import org.kie.api.command.Command;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.internal.command.RegistryContext;

public class SetVariableCommandFromCommand
    implements
    ExecutableCommand<Void> {
    private String identifier;
    private String contextName;
    private Command cmd;

    public SetVariableCommandFromCommand(String contextName,
                                         String identifier,
                                         Command cmd) {
        this.identifier = identifier;
        this.contextName = contextName;
        this.cmd = cmd;
    }

    public Void execute(Context context) {
        if ( this.contextName == null ) {
            ( (RegistryContext) context ).getContextManager().getContext( ContextManagerImpl.ROOT ).set( this.identifier,
                                                                                                     ((ExecutableCommand) this.cmd).execute( context ) );
        } else {
            ( (RegistryContext) context ).getContextManager().getContext( this.contextName ).set( this.identifier,
                                                                            ((ExecutableCommand) this.cmd).execute( context ) );
        }
        return null;
    }

}
