/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.jobs.service.repository.postgresql;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.job.JobDetails;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.repository.impl.BaseReactiveJobRepository;
import org.kie.kogito.jobs.service.repository.marshaller.RecipientMarshaller;
import org.kie.kogito.jobs.service.repository.marshaller.TriggerMarshaller;
import org.kie.kogito.jobs.service.stream.JobStreams;
import org.kie.kogito.jobs.service.utils.DateUtil;
import org.kie.kogito.timer.Trigger;

import io.smallrye.mutiny.Multi;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

import static java.util.stream.Collectors.toList;
import static org.kie.kogito.jobs.service.utils.DateUtil.DEFAULT_ZONE;

@ApplicationScoped
public class PostgreSqlJobRepository extends BaseReactiveJobRepository implements ReactiveJobRepository {

    public static final Integer MAX_ITEMS_QUERY = 10000;

    private static final String JOB_DETAILS_TABLE = "job_details";

    private static final String JOB_DETAILS_COLUMNS = "id, correlation_id, status, last_update, retries, " +
            "execution_counter, scheduled_id, payload, type, priority, recipient, trigger, fire_time";

    private PgPool client;

    private TriggerMarshaller triggerMarshaller;

    private RecipientMarshaller recipientMarshaller;

    PostgreSqlJobRepository() {
        super(null, null);
    }

    @Inject
    public PostgreSqlJobRepository(Vertx vertx, JobStreams jobStreams, PgPool client,
            TriggerMarshaller triggerMarshaller, RecipientMarshaller recipientMarshaller) {
        super(vertx, jobStreams);
        this.client = client;
        this.triggerMarshaller = triggerMarshaller;
        this.recipientMarshaller = recipientMarshaller;
    }

    @Override
    public CompletionStage<JobDetails> doSave(JobDetails job) {
        return client.preparedQuery("INSERT INTO " + JOB_DETAILS_TABLE + " (" + JOB_DETAILS_COLUMNS +
                ") VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13) " +
                "ON CONFLICT (id) DO " +
                "UPDATE SET correlation_id = $2, status = $3, last_update = $4, retries = $5, " +
                "execution_counter = $6, scheduled_id = $7, payload = $8, type = $9, priority = $10, " +
                "recipient = $11, trigger = $12, fire_time = $13 " +
                "RETURNING " + JOB_DETAILS_COLUMNS)
                .execute(Tuple.tuple(Stream.of(
                        job.getId(),
                        job.getCorrelationId(),
                        Optional.ofNullable(job.getStatus()).map(Enum::name).orElse(null),
                        Optional.ofNullable(job.getLastUpdate()).map(ZonedDateTime::toOffsetDateTime).orElse(null),
                        job.getRetries(),
                        job.getExecutionCounter(),
                        job.getScheduledId(),
                        Optional.ofNullable(job.getPayload()).map(p -> new JsonObject(p.toString())).orElse(null),
                        Optional.ofNullable(job.getType()).map(Enum::name).orElse(null),
                        job.getPriority(),
                        recipientMarshaller.marshall(job.getRecipient()),
                        triggerMarshaller.marshall(job.getTrigger()),
                        Optional.ofNullable(job.getTrigger()).map(Trigger::hasNextFireTime).map(DateUtil::dateToOffsetDateTime).orElse(null))
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
    public PublisherBuilder<JobDetails> findByStatus(JobStatus... status) {
        String statusQuery = createStatusQuery(status);
        String query = " WHERE " + statusQuery;
        return ReactiveStreams.fromPublisher(
                client.preparedQuery("SELECT " + JOB_DETAILS_COLUMNS + " FROM " + JOB_DETAILS_TABLE + query + " ORDER BY priority DESC LIMIT $1").execute(Tuple.of(MAX_ITEMS_QUERY))
                        .onItem().transformToMulti(rowSet -> Multi.createFrom().iterable(rowSet))
                        .onItem().transform(this::from));
    }

    @Override
    public PublisherBuilder<JobDetails> findAll() {
        return ReactiveStreams.fromPublisher(
                client.preparedQuery("SELECT " + JOB_DETAILS_COLUMNS + " FROM " + JOB_DETAILS_TABLE + " LIMIT $1").execute(Tuple.of(MAX_ITEMS_QUERY))
                        .onItem().transformToMulti(rowSet -> Multi.createFrom().iterable(rowSet))
                        .onItem().transform(this::from));
    }

    @Override
    public PublisherBuilder<JobDetails> findByStatusBetweenDatesOrderByPriority(ZonedDateTime from, ZonedDateTime to, JobStatus... status) {
        String statusQuery = createStatusQuery(status);
        String timeQuery = createTimeQuery("$2", "$3");
        String query = " WHERE " + statusQuery + " AND " + timeQuery;

        return ReactiveStreams.fromPublisher(
                client.preparedQuery("SELECT " + JOB_DETAILS_COLUMNS + " FROM " + JOB_DETAILS_TABLE + query + " ORDER BY priority DESC LIMIT $1")
                        .execute(Tuple.of(MAX_ITEMS_QUERY, from.toOffsetDateTime(), to.toOffsetDateTime()))
                        .onItem().transformToMulti(rowSet -> Multi.createFrom().iterable(rowSet))
                        .onItem().transform(this::from));
    }

    static String createStatusQuery(JobStatus... status) {
        return Arrays.stream(status).map(JobStatus::name)
                .collect(Collectors.joining("', '", "status IN ('", "')"));
    }

    static String createTimeQuery(String indexFrom, String indexTo) {
        return String.format("fire_time BETWEEN %s AND %s", indexFrom, indexTo);
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
                .payload(Optional.ofNullable(row.get(JsonObject.class, 7)).map(JsonObject::toString).orElse(null))
                .type(Optional.ofNullable(row.getString("type")).map(JobDetails.Type::valueOf).orElse(null))
                .priority(row.getInteger("priority"))
                .recipient(recipientMarshaller.unmarshall(row.get(JsonObject.class, 10)))
                .trigger(triggerMarshaller.unmarshall(row.get(JsonObject.class, 11)))
                .build();
    }
}
