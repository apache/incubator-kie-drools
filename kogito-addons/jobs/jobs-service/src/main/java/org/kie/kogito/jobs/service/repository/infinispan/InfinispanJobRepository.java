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

package org.kie.kogito.jobs.service.repository.infinispan;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.vertx.core.Vertx;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.infinispan.client.hotrod.Flag;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.Search;
import org.infinispan.query.dsl.QueryFactory;
import org.infinispan.query.dsl.SortOrder;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.qualifier.Repository;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.repository.impl.BaseReactiveJobRepository;
import org.kie.kogito.jobs.service.stream.JobStreams;
import org.kie.kogito.jobs.service.utils.DateUtil;

import static org.kie.kogito.jobs.service.repository.infinispan.InfinispanConfiguration.Caches.SCHEDULED_JOBS;

@ApplicationScoped
@Repository("infinispan")
public class InfinispanJobRepository extends BaseReactiveJobRepository implements ReactiveJobRepository {

    private RemoteCache<String, ScheduledJob> cache;
    private QueryFactory queryFactory;

    InfinispanJobRepository() {
        super(null, null);
    }

    @Inject
    public InfinispanJobRepository(Vertx vertx,
                                   JobStreams jobStreams,
                                   RemoteCacheManager remoteCacheManager) {
        super(vertx, jobStreams);
        this.cache = remoteCacheManager.administration().getOrCreateCache(SCHEDULED_JOBS, (String) null);
        this.queryFactory = Search.getQueryFactory(cache);
    }

    @Override
    public CompletionStage<ScheduledJob> doSave(ScheduledJob job) {

        return runAsync(() -> cache.put(job.getId(), job))
                .thenCompose(j -> get(job.getId()));
    }

    @Override
    public CompletionStage<ScheduledJob> get(String id) {
        return runAsync(() -> cache.get(id));
    }

    @Override
    public CompletionStage<Boolean> exists(String id) {
        return runAsync(() -> cache.containsKey(id));
    }

    @Override
    public CompletionStage<ScheduledJob> delete(String id) {
        return runAsync(() -> cache
                .withFlags(Flag.FORCE_RETURN_VALUE)
                .remove(id));
    }

    @Override
    public PublisherBuilder<ScheduledJob> findAll() {
        return ReactiveStreams
                .fromIterable(queryFactory.from(ScheduledJob.class)
                                      .build()
                                      .list());
    }

    @Override
    public PublisherBuilder<ScheduledJob> findByStatus(JobStatus... status) {
        return ReactiveStreams.fromIterable(queryFactory.from(ScheduledJob.class)
                                                    .having("status")
                                                    .in(Arrays.stream(status)
                                                                .map(JobStatus::name)
                                                                .collect(Collectors.toList()))
                                                    .build()
                                                    .list());
    }

    public PublisherBuilder<ScheduledJob> findByStatusBetweenDatesOrderByPriority(ZonedDateTime from, ZonedDateTime to,
                                                                                  JobStatus... status) {
        return ReactiveStreams.fromIterable(queryFactory.from(ScheduledJob.class)
                                                    .having("status")
                                                    .in(Arrays.stream(status)
                                                                .map(JobStatus::name)
                                                                .collect(Collectors.toList()))
                                                    .and()
                                                    .having("expirationTime")
                                                    .between(DateUtil.zonedDateTimeToInstant(from),
                                                             DateUtil.zonedDateTimeToInstant(to))
                                                    .orderBy("priority", SortOrder.DESC)
                                                    .build()
                                                    .list());
    }
}