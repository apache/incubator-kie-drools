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
package org.kie.kogito.jobs.service.repository.postgresql;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.repository.impl.BaseReactiveJobRepository;
import org.kie.kogito.jobs.service.repository.marshaller.RecipientMarshaller;
import org.kie.kogito.jobs.service.repository.marshaller.TriggerMarshaller;
import org.kie.kogito.jobs.service.stream.JobEventPublisher;
import org.kie.kogito.jobs.service.utils.DateUtil;
import org.kie.kogito.timer.Trigger;

import io.smallrye.mutiny.Multi;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import static java.util.stream.Collectors.toList;
import static mutiny.zero.flow.adapters.AdaptersToReactiveStreams.publisher;
import static org.kie.kogito.jobs.service.utils.DateUtil.DEFAULT_ZONE;

@ApplicationScoped
public class PostgreSqlJobRepository extends BaseReactiveJobRepository implements ReactiveJobRepository {

    private static final String JOB_DETAILS_TABLE = "job_details";

    private static final String JOB_DETAILS_COLUMNS = "id, correlation_id, status, last_update, retries, " +
            "execution_counter, scheduled_id, priority, recipient, trigger, fire_time, execution_timeout, execution_timeout_unit, created";

    private PgPool client;

    private final TriggerMarshaller triggerMarshaller;

    private final RecipientMarshaller recipientMarshaller;

    PostgreSqlJobRepository() {
        this(null, null, null, null, null);
    }

    @Inject
    public PostgreSqlJobRepository(Vertx vertx, JobEventPublisher jobEventPublisher, PgPool client,
            TriggerMarshaller triggerMarshaller, RecipientMarshaller recipientMarshaller) {
        super(vertx, jobEventPublisher);
        this.client = client;
        this.triggerMarshaller = triggerMarshaller;
        this.recipientMarshaller = recipientMarshaller;
    }

    @Override
    public CompletionStage<JobDetails> doSave(JobDetails job) {
        return client.preparedQuery("INSERT INTO " + JOB_DETAILS_TABLE + " (" + JOB_DETAILS_COLUMNS +
                ") VALUES ($1, $2, $3, now(), $4, $5, $6, $7, $8, $9, $10, $11, $12, now()) " +
                "ON CONFLICT (id) DO " +
                "UPDATE SET correlation_id = $2, status = $3, last_update = now(), retries = $4, " +
                "execution_counter = $5, scheduled_id = $6, priority = $7, " +
                "recipient = $8, trigger = $9, fire_time = $10, execution_timeout = $11, execution_timeout_unit = $12 " +
                "RETURNING " + JOB_DETAILS_COLUMNS)
                .execute(Tuple.tuple(Stream.of(
                        job.getId(),
                        job.getCorrelationId(),
                        Optional.ofNullable(job.getStatus()).map(Enum::name).orElse(null),
                        job.getRetries(),
                        job.getExecutionCounter(),
                        job.getScheduledId(),
                        job.getPriority(),
                        recipientMarshaller.marshall(job.getRecipient()),
                        triggerMarshaller.marshall(job.getTrigger()),
                        Optional.ofNullable(job.getTrigger()).map(Trigger::hasNextFireTime).map(DateUtil::dateToOffsetDateTime).orElse(null),
                        job.getExecutionTimeout(),
                        Optional.ofNullable(job.getExecutionTimeoutUnit()).map(Enum::name).orElse(null))
                        .collect(toList())))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null)
                .convert()
                .toCompletableFuture();
    }

    @Override
    public CompletionStage<JobDetails> get(String id) {
        return client.preparedQuery("SELECT " + JOB_DETAILS_COLUMNS + " FROM " + JOB_DETAILS_TABLE + " WHERE id = $1").execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null)
                .convert()
                .toCompletableFuture();
    }

    @Override
    public CompletionStage<Boolean> exists(String id) {
        return client.preparedQuery("SELECT id FROM " + JOB_DETAILS_TABLE + " WHERE id = $1").execute(Tuple.of(id))
                .onItem().transform(rowSet -> rowSet.rowCount() > 0)
                .convert()
                .toCompletableFuture();
    }

    @Override
    public CompletionStage<JobDetails> delete(String id) {
        return client.preparedQuery("DELETE FROM " + JOB_DETAILS_TABLE + " WHERE id = $1 RETURNING " + JOB_DETAILS_COLUMNS).execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null)
                .convert()
                .toCompletableFuture();
    }

    @Override
    public PublisherBuilder<JobDetails> findByStatusBetweenDates(ZonedDateTime fromFireTime,
            ZonedDateTime toFireTime,
            JobStatus[] status,
            SortTerm[] orderBy) {

        String statusFilter = (status != null && status.length > 0) ? createStatusFilter(status) : null;
        String fireTimeFilter = createFireTimeFilter("$1", "$2");
        String orderByCriteria = (orderBy != null && orderBy.length > 0) ? createOrderBy(orderBy) : "";

        StringBuilder queryFilter = new StringBuilder();
        if (statusFilter != null) {
            queryFilter.append(statusFilter);
            queryFilter.append(" AND ");
        }
        queryFilter.append(fireTimeFilter);

        String findQuery = "SELECT " + JOB_DETAILS_COLUMNS +
                " FROM " + JOB_DETAILS_TABLE +
                " WHERE " + queryFilter +
                " " + orderByCriteria;

        Tuple params = Tuple.of(fromFireTime.toOffsetDateTime(), toFireTime.toOffsetDateTime());
        return ReactiveStreams.fromPublisher(publisher(
                client.preparedQuery(findQuery)
                        .execute(params)
                        .onItem().transformToMulti(rowSet -> Multi.createFrom().iterable(rowSet))
                        .onItem().transform(this::from)));
    }

    static String createStatusFilter(JobStatus... status) {
        return Arrays.stream(status).map(JobStatus::name)
                .collect(Collectors.joining("', '", "status IN ('", "')"));
    }

    static String createFireTimeFilter(String indexFrom, String indexTo) {
        return String.format("fire_time BETWEEN %s AND %s", indexFrom, indexTo);
    }

    static String createOrderBy(SortTerm[] sortTerms) {
        return Stream.of(sortTerms).map(PostgreSqlJobRepository::createOrderByTerm)
                .collect(Collectors.joining(", ", "ORDER BY ", ""));
    }

    static String createOrderByTerm(SortTerm sortTerm) {
        return toColumName(sortTerm.getField()) + (sortTerm.isAsc() ? " ASC" : " DESC");
    }

    static String toColumName(SortTermField field) {
        return switch (field) {
            case FIRE_TIME -> "fire_time";
            case CREATED -> "created";
            case ID -> "id";
            default -> throw new IllegalArgumentException("No colum name is defined for field: " + field);
        };
    }

    JobDetails from(Row row) {
        return JobDetails.builder()
                .id(row.getString("id"))
                .correlationId(row.getString("correlation_id"))
                .status(Optional.ofNullable(row.getString("status")).map(JobStatus::valueOf).orElse(null))
                .lastUpdate(Optional.ofNullable(row.getOffsetDateTime("last_update")).map(t -> t.atZoneSameInstant(DEFAULT_ZONE)).orElse(null))
                .retries(row.getInteger("retries"))
                .executionCounter(row.getInteger("execution_counter"))
                .scheduledId(row.getString("scheduled_id"))
                .priority(row.getInteger("priority"))
                .recipient(recipientMarshaller.unmarshall(row.get(JsonObject.class, "recipient")))
                .trigger(triggerMarshaller.unmarshall(row.get(JsonObject.class, "trigger")))
                .executionTimeout(row.getLong("execution_timeout"))
                .executionTimeoutUnit(Optional.ofNullable(row.getString("execution_timeout_unit")).map(ChronoUnit::valueOf).orElse(null))
                .created(Optional.ofNullable(row.getOffsetDateTime("created")).map(t -> t.atZoneSameInstant(DEFAULT_ZONE)).orElse(null))
                .build();
    }
}
