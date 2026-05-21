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

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.repository.impl.BaseReactiveJobRepository;
import org.kie.kogito.jobs.service.repository.jpa.model.JobDetailsEntity;
import org.kie.kogito.jobs.service.repository.jpa.repository.JobDetailsEntityRepository;
import org.kie.kogito.jobs.service.repository.jpa.utils.ReactiveRepositoryHelper;
import org.kie.kogito.jobs.service.repository.marshaller.RecipientMarshaller;
import org.kie.kogito.jobs.service.repository.marshaller.TriggerMarshaller;
import org.kie.kogito.jobs.service.stream.JobEventPublisher;
import org.kie.kogito.jobs.service.utils.DateUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Multi;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import static java.time.OffsetDateTime.now;
import static mutiny.zero.flow.adapters.AdaptersToReactiveStreams.publisher;
import static org.kie.kogito.jobs.service.utils.DateUtil.DEFAULT_ZONE;

@ApplicationScoped
public class JPAReactiveJobRepository extends BaseReactiveJobRepository implements ReactiveJobRepository {

    private static final String JOBS_BETWEEN_FIRE_TIMES_QUERY = "select job " +
            "from JobDetailsEntity job " +
            "where job.fireTime between :from and :to and job.status in :status";

    private final JobDetailsEntityRepository repository;
    private final ReactiveRepositoryHelper reactiveRepositoryHelper;

    private final TriggerMarshaller triggerMarshaller;
    private final RecipientMarshaller recipientMarshaller;

    JPAReactiveJobRepository() {
        this(null, null, null, null, null, null);
    }

    @Inject
    public JPAReactiveJobRepository(Vertx vertx, JobEventPublisher jobEventPublisher, JobDetailsEntityRepository repository,
            ReactiveRepositoryHelper reactiveRepositoryHelper,
            TriggerMarshaller triggerMarshaller, RecipientMarshaller recipientMarshaller) {
        super(vertx, jobEventPublisher);
        this.repository = repository;
        this.reactiveRepositoryHelper = reactiveRepositoryHelper;
        this.triggerMarshaller = triggerMarshaller;
        this.recipientMarshaller = recipientMarshaller;
    }

    @Override
    public CompletionStage<JobDetails> doSave(JobDetails job) {
        return this.reactiveRepositoryHelper.runAsync(() -> persist(job))
                .thenApply(this::from);
    }

    private JobDetailsEntity persist(JobDetails job) {
        JobDetailsEntity jobDetailsInstance = repository.findByIdOptional(job.getId()).orElseGet(JobDetailsEntity::new);

        merge(job, jobDetailsInstance);

        repository.persist(jobDetailsInstance);

        return repository.findById(job.getId());
    }

    @Override
    public CompletionStage<JobDetails> get(String id) {
        return this.reactiveRepositoryHelper.runAsync(() -> repository.findById(id))
                .thenApply(this::from);
    }

    @Override
    public CompletionStage<Boolean> exists(String id) {
        return this.reactiveRepositoryHelper.runAsync(() -> repository.findByIdOptional(id))
                .thenApply(Optional::isPresent);
    }

    @Override
    public CompletionStage<JobDetails> delete(String id) {
        return this.reactiveRepositoryHelper.runAsync(() -> this.deleteJob(id))
                .thenApply(this::from);

    }

    private JobDetailsEntity deleteJob(String id) {
        JobDetailsEntity jobDetailsInstance = repository.findById(id);

        if (Objects.isNull(jobDetailsInstance)) {
            return null;
        }

        repository.delete(jobDetailsInstance);

        return jobDetailsInstance;
    }

    String toColumName(SortTermField field) {
        return switch (field) {
            case FIRE_TIME -> "fireTime";
            case CREATED -> "created";
            case ID -> "id";
            default -> throw new IllegalArgumentException("No colum name is defined for field: " + field);
        };
    }

    @Override
    public PublisherBuilder<JobDetails> findByStatusBetweenDates(ZonedDateTime fromFireTime,
            ZonedDateTime toFireTime,
            JobStatus[] status,
            SortTerm[] orderBy) {

        Parameters params = Parameters.with("from", fromFireTime.toOffsetDateTime())
                .and("to", toFireTime.toOffsetDateTime())
                .and("status", Arrays.stream(status).map(Enum::toString).toList());

        Sort sort = Sort.empty();

        Arrays.stream(orderBy).forEach(sortTerm -> {
            String columnName = toColumName(sortTerm.getField());
            sort.and(columnName, sortTerm.isAsc() ? Sort.Direction.Ascending : Sort.Direction.Descending);
        });

        return ReactiveStreams.fromPublisher(publisher(Multi.createFrom()
                .completionStage(this.reactiveRepositoryHelper.runAsync(() -> repository.list(JOBS_BETWEEN_FIRE_TIMES_QUERY, sort, params.map())))
                .flatMap(jobDetailsEntities -> Multi.createFrom().iterable(jobDetailsEntities))
                .map(this::from)));

    }

    JobDetailsEntity merge(JobDetails job, JobDetailsEntity instance) {
        if (Objects.isNull(instance)) {
            instance = new JobDetailsEntity();
        }

        ObjectMapper mapper = ObjectMapperFactory.get();

        OffsetDateTime lastUpdate = now().truncatedTo(ChronoUnit.MILLIS);

        instance.setId(job.getId());
        instance.setCorrelationId(job.getCorrelationId());
        instance.setStatus(mapOptionalValue(job.getStatus(), Enum::name));
        instance.setLastUpdate(lastUpdate);
        instance.setRetries(job.getRetries());
        instance.setExecutionCounter(job.getExecutionCounter());
        instance.setScheduledId(job.getScheduledId());
        instance.setPriority(job.getPriority());

        instance.setRecipient(mapOptionalValue(job.getRecipient(), recipient -> mapper.valueToTree(recipientMarshaller.marshall(recipient).getMap())));
        instance.setTrigger(mapOptionalValue(job.getTrigger(), trigger -> mapper.valueToTree(triggerMarshaller.marshall(job.getTrigger()).getMap())));
        instance.setFireTime(mapOptionalValue(job.getTrigger().hasNextFireTime(), DateUtil::dateToOffsetDateTime));

        instance.setExecutionTimeout(job.getExecutionTimeout());
        instance.setExecutionTimeoutUnit(mapOptionalValue(job.getExecutionTimeoutUnit(), Enum::name));

        instance.setCreated(Optional.ofNullable(job.getCreated()).map(ZonedDateTime::toOffsetDateTime).orElse(lastUpdate));

        return instance;
    }

    JobDetails from(JobDetailsEntity instance) {
        if (instance == null) {
            return null;
        }

        return JobDetails.builder()
                .id(instance.getId())
                .correlationId(instance.getCorrelationId())
                .status(mapOptionalValue(instance.getStatus(), JobStatus::valueOf))
                .lastUpdate(instance.getLastUpdate().atZoneSameInstant(DEFAULT_ZONE))
                .retries(instance.getRetries())
                .executionCounter(instance.getExecutionCounter())
                .scheduledId(instance.getScheduledId())
                .priority(instance.getPriority())
                .recipient(mapOptionalValue(instance.getRecipient(), recipient -> recipientMarshaller.unmarshall(JsonObject.mapFrom(recipient))))
                .trigger(mapOptionalValue(instance.getTrigger(), trigger -> triggerMarshaller.unmarshall(JsonObject.mapFrom(trigger))))
                .executionTimeout(instance.getExecutionTimeout())
                .executionTimeoutUnit(mapOptionalValue(instance.getExecutionTimeoutUnit(), ChronoUnit::valueOf))
                .created(instance.getCreated().atZoneSameInstant(DEFAULT_ZONE))
                .build();
    }

    private <T, R> R mapOptionalValue(T object, Function<T, R> mapper) {
        return Optional.ofNullable(object)
                .map(mapper)
                .orElse(null);
    }
}
