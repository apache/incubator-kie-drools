package org.drools.runtime;

import org.drools.command.Command;

public interface BatchExecutor {
    public BatchExecutionResult execute(Command command);
}
