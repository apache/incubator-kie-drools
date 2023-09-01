package org.kie.api.runtime;

import java.util.List;

public interface BatchRequestMessage {
    List<org.kie.api.command.Command> getCommands();
}
