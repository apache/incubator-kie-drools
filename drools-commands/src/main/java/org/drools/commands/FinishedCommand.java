package org.drools.commands;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;

public class FinishedCommand
    implements
    ExecutableCommand<Void> {

    public FinishedCommand() {
    }

    public Void execute(Context ctx) {
        return null;
    }

}
