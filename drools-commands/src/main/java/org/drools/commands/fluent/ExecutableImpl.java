package org.drools.commands.fluent;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.command.ExecutableCommand;
import org.kie.internal.builder.fluent.CommandBasedExecutable;
import org.kie.internal.builder.fluent.ExecutableBuilder;

public class ExecutableImpl implements InternalExecutable,
                                       CommandBasedExecutable {

    private FluentComponentFactory factory;
    private ExecutableBuilder executableBuilder;

    private Batch batch;

    private List<Batch> batches;

    public ExecutableImpl() {
        batches = new ArrayList<>();
    }

    public ExecutableBuilder getExecutableBuilder() {
        return executableBuilder;
    }

    public void setExecutableBuilder(ExecutableBuilder executableBuilder) {
        this.executableBuilder = executableBuilder;
    }

    public FluentComponentFactory getFactory() {
        if (factory == null) {
            factory = new FluentComponentFactory();
        }
        return factory;
    }

    public void setFactory(FluentComponentFactory factory) {
        this.factory = factory;
    }

    @Override
    public void addCommand(ExecutableCommand cmd) {
        if (batch == null) {
            batch = new BatchImpl();
            addBatch(batch);
        }
        batch.addCommand(cmd);
    }

    public void addBatch(Batch batch) {
        batches.add(batch);
        this.batch = batch;
    }

    public Batch getBatch() {
        return batch;
    }

    @Override
    public List<Batch> getBatches() {
        return batches;
    }
}
