package org.kie.internal.builder.fluent;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Executable;

public interface CommandBasedExecutable extends Executable {

    void addCommand(ExecutableCommand cmd);
}
