package org.kie.internal.fluent;

import org.kie.api.command.Command;

import java.util.List;

public interface Batch {

    public long getDistance();

    public void addCommand(Command cmd);

    List<Command> getCommands();
}
