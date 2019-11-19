/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.jobs.service.repository.impl;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

import io.vertx.core.Vertx;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;

public abstract class BaseReactiveJobRepository implements ReactiveJobRepository {

    private Vertx vertx;

    public BaseReactiveJobRepository(Vertx vertx) {
        this.vertx = vertx;
    }

    public <T> CompletionStage<T> runAsync(Supplier<T> function) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        vertx.runOnContext((v) -> future.complete(function.get()));
        return future;
    }

    @Override
    public PublisherBuilder<ScheduledJob> findByStatus(JobStatus... status) {
        return findAll()
                .filter(job -> Objects.nonNull(job.getStatus()))
                .filter(job -> Arrays.stream(status).anyMatch(job.getStatus()::equals));
    }
}
