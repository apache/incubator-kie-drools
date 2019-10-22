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

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.vertx.core.Vertx;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.reactivestreams.Publisher;

@ApplicationScoped
public class InMemoryJobRepository implements ReactiveJobRepository {

    private final Map<String, ScheduledJob> jobMap = new ConcurrentHashMap<>();
    private final Map<ZonedDateTime, List<String>> jobTimeMap = new ConcurrentHashMap<>();

    @Inject
    private Vertx vertx;

    @Override
    public CompletionStage<ScheduledJob> save(ScheduledJob job) {
        return runAsync(() -> {
            jobMap.put(job.getJob().getId(), job);
            jobTimeMap.putIfAbsent(getExpirationTime(job), new ArrayList<>());
            jobTimeMap.get(getExpirationTime(job)).add(job.getJob().getId());
            return job;
        });
    }

    @Override
    public CompletionStage<ScheduledJob> get(String key) {
        return runAsync(() -> jobMap.get(key));
    }

    @Override
    public CompletionStage<Boolean> exists(String key) {
        return runAsync(() -> jobMap.containsKey(key));
    }

    @Override
    public CompletionStage<ScheduledJob> delete(String key) {
        return runAsync(() -> {
            Optional.ofNullable(jobTimeMap.get(getExpirationTime(jobMap.get(key))))
                    .map(jobs-> jobs.remove(key));
            return jobMap.remove(key);
        });
    }

    private ZonedDateTime getExpirationTime(ScheduledJob job) {
        return job.getJob().getExpirationTime();
    }

    @Override
    public Publisher<ScheduledJob> getByTime(ZonedDateTime expirationTime) {
        return ReactiveStreams.fromIterable(jobTimeMap.getOrDefault(expirationTime, Collections.emptyList()))
                .map(this::get)
                .flatMapCompletionStage(j -> j)
                .buildRs();
    }

    private <T> CompletionStage<T> runAsync(Supplier<T> function) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        vertx.runOnContext((v) -> future.complete(function.get()));
        return future;
    }
}
