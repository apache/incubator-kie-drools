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
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

import io.vertx.core.Vertx;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.stream.JobStreams;

public abstract class BaseReactiveJobRepository implements ReactiveJobRepository {

    private Vertx vertx;

    private JobStreams jobStreams;

    public BaseReactiveJobRepository(Vertx vertx, JobStreams jobStreams) {
        this.vertx = vertx;
        this.jobStreams = jobStreams;
    }

    public <T> CompletionStage<T> runAsync(Supplier<T> function) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        vertx.executeBlocking(v -> future.complete(function.get()), r -> {
        });
        return future;
    }

    @Override
    public PublisherBuilder<ScheduledJob> findByStatus(JobStatus... status) {
        return findAll()
                .filter(job -> Objects.nonNull(job.getStatus()))
                .filter(job -> Arrays.stream(status).anyMatch(job.getStatus()::equals));
    }

    @Override
    public CompletionStage<ScheduledJob> save(ScheduledJob job) {
        return doSave(job)
                .thenApply(jobStreams::publishJobStatusChange);
    }

    public abstract CompletionStage<ScheduledJob> doSave(ScheduledJob job);

    @Override
    public CompletionStage<ScheduledJob> delete(ScheduledJob job) {
        return delete(job.getId())
                .thenApply(j -> jobStreams.publishJobStatusChange(job));
    }

    @Override
    public CompletionStage<ScheduledJob> merge(String id, ScheduledJob jobToMerge) {
        return Optional.ofNullable(id)
                //do validations
                .filter(StringUtils::isNotBlank)
                .filter(s -> StringUtils.isBlank(jobToMerge.getId()) || s.equals(jobToMerge.getId()))
                //perform merge
                .map(jobId -> this.get(jobId)
                        .thenApply(Optional::ofNullable)
                        .thenApply(j -> j.map(currentJob -> doMerge(jobToMerge, currentJob)))
                        .thenCompose(j -> j.map(this::save).orElse(CompletableFuture.completedFuture(null))))//save it
                .orElseThrow(() -> new IllegalArgumentException("Id is empty or not equals to Job.id : " + id));
    }

    private ScheduledJob doMerge(ScheduledJob toMerge, ScheduledJob current) {
        return ScheduledJob.builder()
                .of(current)
                .merge(toMerge)
                .build();
    }
}
