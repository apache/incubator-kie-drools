package org.drools.commands.fluent;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.command.Command;

public class BatchImpl implements Batch {

    private final long distance;

    private List<Command> commands = new ArrayList<>();

    public BatchImpl() {
        this(0L);
    }

    public BatchImpl(long distance) {
        this.distance = distance;
    }

    public long getDistance() {
        return distance;
    }

    public BatchImpl addCommand(Command cmd) {
        this.commands.add(cmd);
        return this;
    }

    @Override
    public List<Command> getCommands() {
        return commands;
    }
}
