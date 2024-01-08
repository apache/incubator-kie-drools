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
package org.kie.kogito.jobs.service.repository.infinispan;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.infinispan.client.hotrod.Flag;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.Search;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.repository.impl.BaseReactiveJobRepository;
import org.kie.kogito.jobs.service.stream.JobEventPublisher;

import io.vertx.core.Vertx;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import static org.kie.kogito.jobs.service.repository.infinispan.InfinispanConfiguration.Caches.JOB_DETAILS;

@ApplicationScoped
public class InfinispanJobRepository extends BaseReactiveJobRepository implements ReactiveJobRepository {

    private RemoteCache<String, JobDetails> cache;
    private QueryFactory queryFactory;
    private RemoteCacheManager remoteCacheManager;

    InfinispanJobRepository() {
        super(null, null);
    }

    @Inject
    public InfinispanJobRepository(Vertx vertx,
            JobEventPublisher jobEventPublisher,
            RemoteCacheManager remoteCacheManager) {
        super(vertx, jobEventPublisher);
        this.remoteCacheManager = remoteCacheManager;
    }

    void init(@Observes InfinispanInitialized event) {
        this.cache = remoteCacheManager.getCache(JOB_DETAILS);
        this.queryFactory = Search.getQueryFactory(cache);
    }

    @Override
    public CompletionStage<JobDetails> doSave(JobDetails job) {
        return runAsync(() -> cache.put(job.getId(), job))
                .thenApply(j -> job);
    }

    @Override
    public CompletionStage<JobDetails> get(String id) {
        return runAsync(() -> cache.get(id));
    }

    @Override
    public CompletionStage<Boolean> exists(String id) {
        return runAsync(() -> cache.containsKey(id));
    }

    @Override
    public CompletionStage<JobDetails> delete(String id) {
        return runAsync(() -> cache
                .withFlags(Flag.FORCE_RETURN_VALUE)
                .remove(id));
    }

    @Override
    public PublisherBuilder<JobDetails> findAll() {
        Query<JobDetails> query = queryFactory.<JobDetails> create("from job.service.JobDetails");
        return ReactiveStreams.fromIterable(query.execute().list());
    }

    @Override
    public PublisherBuilder<JobDetails> findByStatus(JobStatus... status) {
        Query<JobDetails> query = queryFactory.create("from job.service.JobDetails j " +
                "where " +
                "j.status in (" + createStatusQuery(status) + ")");
        return ReactiveStreams.fromIterable(query.execute().list());
    }

    @Override
    public PublisherBuilder<JobDetails> findByStatusBetweenDatesOrderByPriority(ZonedDateTime from, ZonedDateTime to,
            JobStatus... status) {
        Query<JobDetails> query = queryFactory.create("from job.service.JobDetails j " +
                "where " +
                "j.trigger.nextFireTime > :from " +
                "and j.trigger.nextFireTime < :to " +
                "and j.status in (" + createStatusQuery(status) + ") " +
                "order by j.priority desc");
        query.setParameter("to", to.toInstant().toEpochMilli());
        query.setParameter("from", from.toInstant().toEpochMilli());
        return ReactiveStreams.fromIterable(query.execute().list());
    }

    //building the query sentence for the status IN (not supported to use array in setParameter on the query)
    private String createStatusQuery(JobStatus[] status) {
        return Arrays.stream(status)
                .map(JobStatus::name)
                .collect(Collectors.joining("\', \'", "\'", "\'"));
    }
}
