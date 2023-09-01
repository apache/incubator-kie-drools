package org.drools.commands.fluent;

import java.util.List;

import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;

public interface Batch extends BatchExecutionCommand {

    long getDistance();

    Batch addCommand(Command cmd);

    List<Command> getCommands();
}
