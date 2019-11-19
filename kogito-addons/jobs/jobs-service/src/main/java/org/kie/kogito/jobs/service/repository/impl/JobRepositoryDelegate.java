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

import java.util.Optional;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.qualifier.Repository;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.repository.infinispan.InfinispanConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Default
@ApplicationScoped
public class JobRepositoryDelegate implements ReactiveJobRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobRepositoryDelegate.class);

    private ReactiveJobRepository delegate;

    JobRepositoryDelegate() {
    }

    @Inject
    public JobRepositoryDelegate(@Any Instance<ReactiveJobRepository> instances,
                                 @ConfigProperty(name = InfinispanConfiguration.PERSISTENCE_CONFIG_KEY)
                                         Optional<String> persistence) {
        delegate = instances.select(new Repository.Literal(persistence.orElse("in-memory"))).get();
        LOGGER.info("JobRepository selected {}", delegate.getClass());
    }

    @Override
    public CompletionStage<ScheduledJob> save(ScheduledJob job) {
        return delegate.save(job);
    }

    @Override
    public CompletionStage<ScheduledJob> get(String id) {
        return delegate.get(id);
    }

    @Override
    public CompletionStage<Boolean> exists(String id) {
        return delegate.exists(id);
    }

    @Override
    public CompletionStage<ScheduledJob> delete(String id) {
        return delegate.delete(id);
    }

    @Override
    public PublisherBuilder<ScheduledJob> findByStatus(JobStatus... status) {
        return delegate.findByStatus(status);
    }

    @Override
    public PublisherBuilder<ScheduledJob> findAll() {
        return delegate.findAll();
    }
}
