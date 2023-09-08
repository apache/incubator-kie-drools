/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
