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
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipientStringPayloadData;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.Recipient;
import org.kie.kogito.jobs.service.model.RecipientInstance;
import org.kie.kogito.jobs.service.repository.marshaller.RecipientMarshaller;
import org.kie.kogito.jobs.service.repository.marshaller.TriggerMarshaller;
import org.kie.kogito.jobs.service.utils.DateUtil;
import org.kie.kogito.timer.Trigger;
import org.kie.kogito.timer.impl.PointInTimeTrigger;
import org.mockito.ArgumentCaptor;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.groups.MultiOnItem;
import io.smallrye.mutiny.groups.UniConvert;
import io.smallrye.mutiny.groups.UniOnItem;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.PreparedQuery;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings({ "unchecked", "rawtypes" })
class PostgreSqlJobRepositoryTest {

    public static final String PAYLOAD_TEST = "{\"payload\": \"test\"}";
    public static final String URL = "test";
    public static final String JOB_DETAILS = "job_details";
    PostgreSqlJobRepository repository;

    PgPool client;

    PreparedQuery<RowSet<Row>> query;

    CompletableFuture completableFuture;

    ZonedDateTime lastUpdateTime;

    ZonedDateTime fireTime;

    @BeforeEach
    void setUp() {
        lastUpdateTime = ZonedDateTime.parse("2023-03-23T13:05:55.000Z");
        fireTime = ZonedDateTime.parse("2023-03-24T14:06:56.000Z");

        client = mock(PgPool.class);
        query = mock(PreparedQuery.class);
        when(client.preparedQuery(anyString())).thenReturn(query);
        when(client.query(anyString())).thenReturn(query);

        Uni uni = mock(Uni.class);
        when(query.execute(any(Tuple.class))).thenReturn(uni);
        when(query.execute()).thenReturn(uni);
        UniOnItem uniOnItem = mock(UniOnItem.class);
        when(uni.onItem()).thenReturn(uniOnItem);
        when(uniOnItem.transform(any(Function.class))).thenReturn(uni);
        when(uni.emitOn(any(Executor.class))).thenReturn(uni);

        Multi multi = mock(Multi.class);
        when(uniOnItem.transformToMulti(any(Function.class))).thenReturn(multi);
        MultiOnItem multiOnItem = mock(MultiOnItem.class);
        when(multi.onItem()).thenReturn(multiOnItem);
        when(multiOnItem.transform(any(Function.class))).thenReturn(multi);
        when(multi.emitOn(any(Executor.class))).thenReturn(multi);

        completableFuture = mock(CompletableFuture.class);
        UniConvert convert = mock(UniConvert.class);
        when(uni.convert()).thenReturn(convert);
        when(convert.toCompletableFuture()).thenReturn(completableFuture);

        TriggerMarshaller triggerMarshaller = mock(TriggerMarshaller.class);
        when(triggerMarshaller.marshall(any(Trigger.class))).thenReturn(new JsonObject().put("triggerMarshaller", "test"));
        when(triggerMarshaller.unmarshall(any(JsonObject.class))).thenReturn(new PointInTimeTrigger(fireTime.toInstant().toEpochMilli(), null, null));
        RecipientMarshaller recipientMarshaller = mock(RecipientMarshaller.class);
        when(recipientMarshaller.marshall(any(Recipient.class))).thenReturn(new JsonObject().put("recipientMarshaller", "test"));
        when(recipientMarshaller.unmarshall(any(JsonObject.class)))
                .thenReturn(new RecipientInstance(HttpRecipient.builder().forStringPayload().url(URL).payload(HttpRecipientStringPayloadData.from(PAYLOAD_TEST)).build()));

        repository = new PostgreSqlJobRepository(null, null, client, triggerMarshaller, recipientMarshaller);
    }

    @Test
    void doSave() {
        PointInTimeTrigger trigger = new PointInTimeTrigger(fireTime.toInstant().toEpochMilli(), null, null);
        Recipient recipient = new RecipientInstance(HttpRecipient.builder().forStringPayload().url(URL).payload(HttpRecipientStringPayloadData.from(PAYLOAD_TEST)).build());

        JobDetails job = JobDetails.builder()
                .id("test")
                .correlationId("test")
                .status(JobStatus.SCHEDULED)
                .lastUpdate(lastUpdateTime)
                .retries(1)
                .executionCounter(1)
                .scheduledId("test")
                .priority(1)
                .recipient(recipient)
                .trigger(trigger)
                .executionTimeout(4L)
                .executionTimeoutUnit(ChronoUnit.MINUTES)
                .build();

        CompletionStage<JobDetails> result = repository.doSave(job);
        assertEquals(completableFuture, result);

        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Tuple> parameterCaptor = ArgumentCaptor.forClass(Tuple.class);
        verify(client, times(1)).preparedQuery(queryCaptor.capture());
        verify(query, times(1)).execute(parameterCaptor.capture());

        String query = "INSERT INTO " + JOB_DETAILS + " (id, correlation_id, status, last_update, retries, execution_counter, scheduled_id, " +
                "priority, recipient, trigger, fire_time, execution_timeout, execution_timeout_unit) VALUES ($1, $2, $3, now(), $4, $5, $6, $7, $8, $9, $10, $11, $12) " +
                "ON CONFLICT (id) DO UPDATE SET correlation_id = $2, status = $3, last_update = now(), retries = $4, " +
                "execution_counter = $5, scheduled_id = $6, priority = $7, " +
                "recipient = $8, trigger = $9, fire_time = $10, execution_timeout = $11, execution_timeout_unit = $12 RETURNING id, correlation_id, status, last_update, retries, " +
                "execution_counter, scheduled_id, priority, recipient, trigger, fire_time, execution_timeout, execution_timeout_unit";

        Tuple parameter = Tuple.tuple(Stream.of(
                job.getId(),
                job.getCorrelationId(),
                job.getStatus().name(),
                job.getRetries(),
                job.getExecutionCounter(),
                job.getScheduledId(),
                job.getPriority(),
                new JsonObject().put("recipientMarshaller", "test"),
                new JsonObject().put("triggerMarshaller", "test"),
                fireTime.toOffsetDateTime(),
                job.getExecutionTimeout(),
                job.getExecutionTimeoutUnit().name())
                .collect(toList()));

        assertEquals(query, queryCaptor.getValue());
        assertEquals(parameter.getString(0), parameterCaptor.getValue().getString(0));
        assertEquals(parameter.getString(1), parameterCaptor.getValue().getString(1));
        assertEquals(parameter.getString(2), parameterCaptor.getValue().getString(2));
        assertEquals(parameter.getInteger(3), parameterCaptor.getValue().getInteger(3));
        assertEquals(parameter.getInteger(4), parameterCaptor.getValue().getInteger(4));
        assertEquals(parameter.getString(5), parameterCaptor.getValue().getString(5));
        assertEquals(parameter.getInteger(6), parameterCaptor.getValue().getInteger(6));
        assertEquals(parameter.getJson(7), parameterCaptor.getValue().getJson(7));
        assertEquals(parameter.getJson(8), parameterCaptor.getValue().getJson(8));
        assertEquals(parameter.getOffsetDateTime(9), parameterCaptor.getValue().getOffsetDateTime(9));
        assertEquals(parameter.getJson(10), parameterCaptor.getValue().getJson(10));
        assertEquals(parameter.getJson(11), parameterCaptor.getValue().getJson(11));
    }

    @Test
    void get() {
        CompletionStage<JobDetails> result = repository.get("test");
        assertEquals(completableFuture, result);

        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Tuple> parameterCaptor = ArgumentCaptor.forClass(Tuple.class);
        verify(client, times(1)).preparedQuery(queryCaptor.capture());
        verify(query, times(1)).execute(parameterCaptor.capture());

        String query = "SELECT id, correlation_id, status, last_update, retries, execution_counter, scheduled_id, " +
                "priority, recipient, trigger, fire_time, execution_timeout, execution_timeout_unit FROM " + JOB_DETAILS + " WHERE id = $1";
        String parameter = "test";

        assertEquals(query, queryCaptor.getValue());
        assertEquals(parameter, parameterCaptor.getValue().getValue(0));
    }

    @Test
    void exists() {
        CompletionStage<Boolean> result = repository.exists("test");
        assertEquals(completableFuture, result);

        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Tuple> parameterCaptor = ArgumentCaptor.forClass(Tuple.class);
        verify(client, times(1)).preparedQuery(queryCaptor.capture());
        verify(query, times(1)).execute(parameterCaptor.capture());

        String query = "SELECT id FROM " + JOB_DETAILS + " WHERE id = $1";
        String parameter = "test";

        assertEquals(query, queryCaptor.getValue());
        assertEquals(parameter, parameterCaptor.getValue().getValue(0));
    }

    @Test
    void delete() {
        CompletionStage<JobDetails> result = repository.delete("test");
        assertEquals(completableFuture, result);

        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Tuple> parameterCaptor = ArgumentCaptor.forClass(Tuple.class);
        verify(client, times(1)).preparedQuery(queryCaptor.capture());
        verify(query, times(1)).execute(parameterCaptor.capture());

        String query = "DELETE FROM " + JOB_DETAILS + " WHERE id = $1 " +
                "RETURNING id, correlation_id, status, last_update, retries, " +
                "execution_counter, scheduled_id, priority, recipient, trigger, fire_time, execution_timeout, execution_timeout_unit";
        String parameter = "test";

        assertEquals(query, queryCaptor.getValue());
        assertEquals(parameter, parameterCaptor.getValue().getValue(0));
    }

    @Test
    void findAll() {
        PublisherBuilder<JobDetails> result = repository.findAll();
        assertNotNull(result);

        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(client, times(1)).preparedQuery(queryCaptor.capture());

        String query = "SELECT id, correlation_id, status, last_update, retries, " +
                "execution_counter, scheduled_id, priority, recipient, trigger, fire_time, execution_timeout, execution_timeout_unit FROM " + JOB_DETAILS + " LIMIT $1";

        assertEquals(query, queryCaptor.getValue());
    }

    @Test
    void findByStatusBetweenDatesOrderByPriority() {
        ZonedDateTime from = ZonedDateTime.now();
        ZonedDateTime to = ZonedDateTime.now();

        PublisherBuilder<JobDetails> result = repository.findByStatusBetweenDatesOrderByPriority(from, to, JobStatus.SCHEDULED, JobStatus.RETRY);
        assertNotNull(result);

        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(client, times(1)).preparedQuery(queryCaptor.capture());

        String query = "SELECT id, correlation_id, status, last_update, retries, execution_counter, scheduled_id, " +
                "priority, recipient, trigger, fire_time, execution_timeout, execution_timeout_unit FROM " + JOB_DETAILS + " " +
                "WHERE status IN ('SCHEDULED', 'RETRY') AND fire_time BETWEEN $2 AND $3 ORDER BY priority DESC LIMIT $1";

        assertEquals(query, queryCaptor.getValue());
    }

    @Test
    void findByStatusBetweenDatesOrderByPriorityNoCondition() {
        ZonedDateTime from = ZonedDateTime.now();
        ZonedDateTime to = ZonedDateTime.now();

        PublisherBuilder<JobDetails> result = repository.findByStatusBetweenDatesOrderByPriority(from, to, JobStatus.SCHEDULED);
        assertNotNull(result);

        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(client, times(1)).preparedQuery(queryCaptor.capture());

        String query = "SELECT id, correlation_id, status, last_update, retries, execution_counter, scheduled_id, " +
                "priority, recipient, trigger, fire_time, execution_timeout, execution_timeout_unit FROM " + JOB_DETAILS + " " +
                "WHERE status IN ('SCHEDULED') AND fire_time BETWEEN $2 AND $3 ORDER BY priority DESC LIMIT $1";

        assertEquals(query, queryCaptor.getValue());
    }

    @Test
    void createStatusQuery() {
        String statusQuery = PostgreSqlJobRepository.createStatusQuery(JobStatus.SCHEDULED, JobStatus.RETRY);
        assertEquals("status IN ('SCHEDULED', 'RETRY')", statusQuery);
    }

    @Test
    void createTimeQuery() {
        String timeQuery = PostgreSqlJobRepository.createTimeQuery("$1", "$2");
        assertEquals("fire_time BETWEEN $1 AND $2", timeQuery);
    }

    @Test
    void from() {
        PointInTimeTrigger trigger = new PointInTimeTrigger(fireTime.toInstant().toEpochMilli(), null, null);
        Recipient recipient = new RecipientInstance(HttpRecipient.builder().forStringPayload().url(URL).payload(HttpRecipientStringPayloadData.from(PAYLOAD_TEST)).build());

        Row row = mock(Row.class);
        when(row.getString("id")).thenReturn("test");
        when(row.getString("correlation_id")).thenReturn("test");
        when(row.getString("status")).thenReturn("SCHEDULED");
        when(row.getOffsetDateTime("last_update")).thenReturn(lastUpdateTime.toOffsetDateTime());
        when(row.getInteger("retries")).thenReturn(1);
        when(row.getInteger("execution_counter")).thenReturn(1);
        when(row.getString("scheduled_id")).thenReturn("test");
        when(row.getInteger("priority")).thenReturn(1);
        when(row.get(JsonObject.class, "recipient")).thenReturn(new JsonObject().put("recipientMarshaller", "test"));
        when(row.get(JsonObject.class, "trigger")).thenReturn(new JsonObject().put("triggerMarshaller", "test"));
        when(row.getLong("execution_timeout")).thenReturn(3L);
        when(row.getString("execution_timeout_unit")).thenReturn(ChronoUnit.SECONDS.name());

        JobDetails jobDetails = repository.from(row);

        JobDetails expected = JobDetails.builder()
                .id("test")
                .correlationId("test")
                .status(JobStatus.SCHEDULED)
                .lastUpdate(lastUpdateTime.toOffsetDateTime().atZoneSameInstant(DateUtil.DEFAULT_ZONE))
                .retries(1)
                .executionCounter(1)
                .scheduledId("test")
                .priority(1)
                .recipient(recipient)
                .trigger(trigger)
                .executionTimeout(3L)
                .executionTimeoutUnit(ChronoUnit.SECONDS)
                .build();

        assertEquals(expected, jobDetails);
    }
}
