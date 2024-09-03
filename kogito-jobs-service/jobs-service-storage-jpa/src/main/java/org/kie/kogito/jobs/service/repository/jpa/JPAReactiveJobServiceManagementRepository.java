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

package org.kie.kogito.jobs.service.repository.jpa;

import java.util.Objects;
import java.util.function.Function;

import org.kie.kogito.jobs.service.model.JobServiceManagementInfo;
import org.kie.kogito.jobs.service.repository.JobServiceManagementRepository;
import org.kie.kogito.jobs.service.repository.jpa.model.JobServiceManagementEntity;
import org.kie.kogito.jobs.service.repository.jpa.repository.JobServiceManagementEntityRepository;
import org.kie.kogito.jobs.service.repository.jpa.utils.ReactiveRepositoryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import static java.time.OffsetDateTime.now;

@ApplicationScoped
public class JPAReactiveJobServiceManagementRepository implements JobServiceManagementRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(JPAReactiveJobServiceManagementRepository.class);

    private final JobServiceManagementEntityRepository repository;
    private final ReactiveRepositoryHelper reactiveRepositoryHelper;

    @Inject
    public JPAReactiveJobServiceManagementRepository(JobServiceManagementEntityRepository repository,
            ReactiveRepositoryHelper reactiveRepositoryHelper) {
        this.repository = repository;
        this.reactiveRepositoryHelper = reactiveRepositoryHelper;
    }

    @Override
    public Uni<JobServiceManagementInfo> getAndUpdate(String id, Function<JobServiceManagementInfo, JobServiceManagementInfo> computeUpdate) {
        LOGGER.info("get {}", id);
        return Uni.createFrom()
                .completionStage(this.reactiveRepositoryHelper.runAsync(() -> doGetAndUpdate(id, computeUpdate)))
                .onItem().ifNotNull().invoke(info -> LOGGER.trace("got {}", info));
    }

    private JobServiceManagementInfo doGetAndUpdate(String id, Function<JobServiceManagementInfo, JobServiceManagementInfo> computeUpdate) {

        JobServiceManagementInfo info = this.repository.findByIdOptional(id)
                .map(this::from)
                .orElse(null);

        return this.update(computeUpdate.apply(info));
    }

    @Override
    public Uni<JobServiceManagementInfo> set(JobServiceManagementInfo info) {
        LOGGER.info("set {}", info);
        return Uni.createFrom().completionStage(this.reactiveRepositoryHelper.runAsync(() -> this.doSet(info)));
    }

    public JobServiceManagementInfo doSet(JobServiceManagementInfo info) {
        return this.update(info);
    }

    private JobServiceManagementInfo update(JobServiceManagementInfo info) {

        if (Objects.isNull(info)) {
            return null;
        }

        JobServiceManagementEntity jobService = this.repository.findByIdOptional(info.getId()).orElse(new JobServiceManagementEntity());

        jobService.setId(info.getId());
        jobService.setToken(info.getToken());
        jobService.setLastHeartBeat(info.getLastHeartbeat());

        repository.persist(jobService);

        return from(jobService);
    }

    @Override
    public Uni<JobServiceManagementInfo> heartbeat(JobServiceManagementInfo info) {
        return Uni.createFrom().completionStage(this.reactiveRepositoryHelper.runAsync(() -> this.doHeartbeat(info)));
    }

    private JobServiceManagementEntity findById(String id) {
        return repository.findById(id);
    }

    private JobServiceManagementEntity findByIdAndToken(JobServiceManagementInfo info) {
        return repository.find("#JobServiceManagementEntity.GetServiceByIdAndToken", Parameters.with("id", info.getId()).and("token", info.getToken()).map())
                .firstResultOptional().orElse(null);
    }

    private JobServiceManagementInfo doHeartbeat(JobServiceManagementInfo info) {
        JobServiceManagementEntity jobService = findByIdAndToken(info);

        if (jobService == null) {
            return null;
        }

        jobService.setLastHeartBeat(now());
        repository.persist(jobService);

        return from(jobService);
    }

    JobServiceManagementInfo from(JobServiceManagementEntity jobService) {
        if (Objects.isNull(jobService)) {
            return null;
        }
        return new JobServiceManagementInfo(jobService.getId(), jobService.getToken(), jobService.getLastHeartBeat());
    }
}
