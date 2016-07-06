package org.drools.core.fluent.impl;

import org.kie.internal.fluent.Batch;
import org.kie.internal.fluent.Executable;
import org.kie.internal.fluent.runtime.FluentBuilder;
import org.kie.api.command.Command;

import java.util.ArrayList;
import java.util.List;

public class ExecutableImpl implements Executable {
    private FluentComponentFactory factory;
    private FluentBuilder          fluentBuilder;

    private Batch batch;

    private List<Batch> batches;

    public ExecutableImpl() {
        batches = new ArrayList<Batch>();
    }

    public FluentBuilder getFluentBuilder() {
        return fluentBuilder;
    }

    public void setFluentBuilder(FluentBuilder fluentBuilder) {
        this.fluentBuilder = fluentBuilder;
    }

    public FluentComponentFactory getFactory() {
        if ( factory == null ) {
            factory = new FluentComponentFactory();
        }
        return factory;
    }

    public void setFactory(FluentComponentFactory factory) {
        this.factory = factory;
    }

    public void addCommand(Command cmd) {
        if ( batch == null ) {
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
