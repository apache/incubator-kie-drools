package org.kie.internal.fluent;

import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;

import java.util.List;

public interface Batch extends BatchExecutionCommand {

    long getDistance();

    Batch addCommand(Command cmd);

    List<Command> getCommands();
}
