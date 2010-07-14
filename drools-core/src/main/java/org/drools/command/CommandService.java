package org.drools.command;

import org.drools.runtime.CommandExecutor;

public interface CommandService extends CommandExecutor {

    public Context getContext();

}
