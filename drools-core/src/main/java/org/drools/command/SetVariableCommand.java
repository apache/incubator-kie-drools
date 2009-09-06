package org.drools.command;

import org.drools.command.Command;
import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;

public class SetVariableCommand
    implements
    GenericCommand<Void> {
    private String  identifier;
    private String  contextName;
    private Command cmd;

    public SetVariableCommand(String contextName,
                              String identifier,
                              Command cmd) {
        this.identifier = identifier;
        this.contextName = contextName;
        this.cmd = cmd;
    }

    public Void execute(Context context) {
        if ( this.contextName == null ) {
            context.set( this.identifier,
                         ((GenericCommand) this.cmd).execute( context ) );
        } else {
            context.getContextManager().getContext( this.contextName ).set( this.identifier,
                                                                            ((GenericCommand) this.cmd).execute( context ) );
        }
        return null;
    }

}
