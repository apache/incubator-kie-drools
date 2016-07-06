package org.drools.core.fluent.impl;

import org.kie.api.command.Command;
import org.kie.internal.fluent.Batch;

import java.util.ArrayList;
import java.util.List;

public class BatchImpl implements Batch {
    private long distance;

    private List<Command> commands = new ArrayList<Command>();

    public BatchImpl() {
    }

    public BatchImpl(long distance) {
        this.distance = distance;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void addCommand(Command cmd) {
        this.commands.add(cmd);
    }

    @Override
    public List<Command> getCommands() {
        return commands;
    }
}
