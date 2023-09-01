package org.kie.internal.simulation;

import java.util.List;

import org.kie.api.command.Command;

public interface SimulationStep {

    long getDistanceMillis();

    List<Command> getCommands();

}
