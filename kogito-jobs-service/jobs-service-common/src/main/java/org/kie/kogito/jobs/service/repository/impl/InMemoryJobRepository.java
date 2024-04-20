/*
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
package org.kie.kogito.jobs.service.repository.impl;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.stream.JobEventPublisher;
import org.kie.kogito.jobs.service.utils.DateUtil;

import io.quarkus.arc.DefaultBean;
import io.vertx.core.Vertx;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import static org.kie.kogito.jobs.service.utils.ModelUtil.jobWithCreatedAndLastUpdate;

@DefaultBean
@ApplicationScoped
public class InMemoryJobRepository extends BaseReactiveJobRepository implements ReactiveJobRepository {

    private final Map<String, JobDetails> jobMap = new ConcurrentHashMap<>();

    public InMemoryJobRepository() {
        super(null, null);
    }

    @Inject
    public InMemoryJobRepository(Vertx vertx, JobEventPublisher jobEventPublisher) {
        super(vertx, jobEventPublisher);
    }

    @Override
    public CompletionStage<JobDetails> doSave(JobDetails job) {
        return runAsync(() -> {
            boolean isNew = !jobMap.containsKey(job.getId());
            JobDetails timeStampedJob = jobWithCreatedAndLastUpdate(isNew, job);
            jobMap.put(timeStampedJob.getId(), timeStampedJob);
            return timeStampedJob;
        });
    }

    @Override
    public CompletionStage<JobDetails> get(String key) {
        return runAsync(() -> jobMap.get(key));
    }

    @Override
    public CompletionStage<Boolean> exists(String key) {
        return runAsync(() -> jobMap.containsKey(key));
    }

    @Override
    public CompletionStage<JobDetails> delete(String key) {
        return runAsync(() -> jobMap.remove(key));
    }

    @Override
    public PublisherBuilder<JobDetails> findByStatusBetweenDates(ZonedDateTime fromFireTime,
            ZonedDateTime toFireTime,
            JobStatus[] status,
            SortTerm[] orderBy) {
        Stream<JobDetails> unsortedResult = jobMap.values()
                .stream()
                .filter(j -> matchStatusFilter(j, status))
                .filter(j -> matchFireTimeFilter(j, fromFireTime, toFireTime));
        List<JobDetails> result = orderBy == null || orderBy.length == 0 ? unsortedResult.toList() : unsortedResult.sorted(orderByComparator(orderBy)).toList();
        return ReactiveStreams.fromIterable(result);
    }

    private static boolean matchStatusFilter(JobDetails job, JobStatus[] status) {
        if (status == null || status.length == 0) {
            return true;
        }
        return Stream.of(status).anyMatch(s -> job.getStatus() == s);
    }

    private static boolean matchFireTimeFilter(JobDetails job, ZonedDateTime fromFireTime, ZonedDateTime toFireTime) {
        ZonedDateTime fireTime = DateUtil.fromDate(job.getTrigger().hasNextFireTime());
        return (fireTime.isEqual(fromFireTime) || fireTime.isAfter(fromFireTime)) &&
                (fireTime.isEqual(toFireTime) || fireTime.isBefore(toFireTime));
    }

    private static Comparator<JobDetails> orderByComparator(SortTerm[] orderBy) {
        Comparator<JobDetails> comparator = createOrderByFieldComparator(orderBy[0]);
        for (int i = 1; i < orderBy.length; i++) {
            comparator = comparator.thenComparing(createOrderByFieldComparator(orderBy[i]));
        }
        return comparator;
    }

    private static Comparator<JobDetails> createOrderByFieldComparator(SortTerm field) {
        Comparator<JobDetails> comparator;
        switch (field.getField()) {
            case FIRE_TIME:
                comparator = Comparator.comparingLong(jobDetails -> {
                    Date nextFireTime = jobDetails.getTrigger().hasNextFireTime();
                    return nextFireTime != null ? nextFireTime.getTime() : Long.MIN_VALUE;
                });
                break;
            case CREATED:
                comparator = Comparator.comparingLong(jobDetails -> {
                    ZonedDateTime created = jobDetails.getCreated();
                    return created != null ? created.toInstant().toEpochMilli() : Long.MIN_VALUE;
                });
                break;
            case ID:
                comparator = Comparator.comparing(JobDetails::getId);
                break;
            default:
                throw new IllegalArgumentException("No comparator is defined for field: " + field.getField());
        }
        return field.isAsc() ? comparator : comparator.reversed();
    }

}
