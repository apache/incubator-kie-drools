package org.kie.fluent;

import org.kie.command.Command;


public interface CommandScript {
    void addCommand(Command<?> cmd);
}
