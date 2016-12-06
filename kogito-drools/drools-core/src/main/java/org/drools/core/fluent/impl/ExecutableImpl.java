/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.fluent.impl;

import org.kie.api.command.Command;
import org.kie.internal.fluent.runtime.FluentBuilder;

import java.util.ArrayList;
import java.util.List;

public class ExecutableImpl implements InternalExecutable {
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
