/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.event.impl;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessService;
import org.kie.kogito.services.event.EventConsumer;

public class DataEventConsumer<D, M extends Model> implements EventConsumer<M> {

    private Optional<Function<D, M>> function;

    private ProcessService processService;

    private ExecutorService executorService;

    public DataEventConsumer(ProcessService processService, ExecutorService executorService, Optional<Function<D, M>> function) {
        this.processService = processService;
        this.executorService = executorService;
        this.function = function;
    }

    @Override
    public CompletionStage<Void> consume(Application application, Process<M> process, Object eventData, String trigger) {
        //TODO right now it is only possible to start a new process instance when not using cloudevent
        return CompletableFuture.runAsync(() -> processService.createProcessInstance(process, function.get().apply((D) eventData), null, trigger, null), executorService);
    }

}
