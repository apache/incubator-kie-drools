package org.drools.runtime;

import java.util.List;

public interface BatchRequestMessage {
    List<org.drools.command.Command> getCommands();
}
