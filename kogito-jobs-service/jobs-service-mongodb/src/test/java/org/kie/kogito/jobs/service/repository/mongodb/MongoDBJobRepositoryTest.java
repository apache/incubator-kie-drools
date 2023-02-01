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

package org.kie.kogito.jobs.service.repository.mongodb;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobDetailsBuilder;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.Recipient;
import org.kie.kogito.jobs.service.model.RecipientInstance;
import org.kie.kogito.jobs.service.repository.marshaller.JobDetailsMarshaller;
import org.kie.kogito.timer.impl.PointInTimeTrigger;
import org.mockito.ArgumentCaptor;
import org.reactivestreams.Publisher;

import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;

import io.quarkus.mongodb.FindOptions;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.quarkus.mongodb.reactive.ReactiveMongoDatabase;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.groups.MultiCollect;
import io.smallrye.mutiny.groups.MultiConvert;
import io.smallrye.mutiny.groups.UniAwait;
import io.smallrye.mutiny.groups.UniConvert;
import io.vertx.core.json.JsonObject;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Indexes.ascending;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.kogito.jobs.service.repository.mongodb.MongoDBJobRepository.ID;
import static org.kie.kogito.jobs.service.utils.DateUtil.DEFAULT_ZONE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings({ "unchecked", "rawtypes" })
class MongoDBJobRepositoryTest {

    MongoDBJobRepository mongoDBJobRepository;

    ReactiveMongoClient mongoClient;

    ReactiveMongoCollection<Document> collection;

    JobDetailsMarshaller jobDetailsMarshaller;

    JsonObject marshalled;

    JobDetails unmarshalled;

    CompletableFuture completableFuture;

    @BeforeEach
    void setUp() {
        mongoClient = mock(ReactiveMongoClient.class);
        collection = mock(ReactiveMongoCollection.class);
        ReactiveMongoDatabase mongoDatabase = mock(ReactiveMongoDatabase.class);
        when(mongoClient.getDatabase(anyString())).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection(anyString())).thenReturn(collection);

        Uni uni = mock(Uni.class);
        Multi multi = mock(Multi.class);
        when(collection.findOneAndReplace(any(Bson.class), any(), any(FindOneAndReplaceOptions.class))).thenReturn(uni);
        when(collection.find(any(Bson.class))).thenReturn(multi);
        when(collection.findOneAndDelete(any(Bson.class))).thenReturn(uni);
        when(collection.find()).thenReturn(multi);
        when(collection.find(any(Bson.class), any(FindOptions.class))).thenReturn(multi);
        when(collection.createIndex(any())).thenReturn(uni);

        when(uni.map(any())).thenAnswer(invocationOnMock -> {
            jobDetailsMarshaller.unmarshall(marshalled);
            return uni;
        });
        when(uni.await()).thenReturn(mock(UniAwait.class));

        MultiCollect multiCollect = mock(MultiCollect.class);
        when(multi.collect()).thenReturn(multiCollect);
        when(multiCollect.first()).thenReturn(uni);
        when(multiCollect.with(any())).thenReturn(uni);
        when(multi.map(any())).thenAnswer(invocationOnMock -> {
            jobDetailsMarshaller.unmarshall(marshalled);
            return multi;
        });
        MultiConvert convertMulti = mock(MultiConvert.class);
        when(multi.emitOn(any())).thenReturn(multi);
        when(multi.convert()).thenReturn(convertMulti);
        Publisher publisher = mock(Publisher.class);
        when(convertMulti.toPublisher()).thenReturn(publisher);

        completableFuture = mock(CompletableFuture.class);
        UniConvert convert = mock(UniConvert.class);
        when(uni.emitOn(any())).thenReturn(uni);
        when(uni.convert()).thenReturn(convert);
        when(convert.toCompletionStage()).thenReturn(completableFuture);

        ZonedDateTime time = ZonedDateTime.now(DEFAULT_ZONE);
        PointInTimeTrigger trigger = new PointInTimeTrigger(time.toInstant().getEpochSecond(), null, null);
        Recipient recipient = new RecipientInstance(HttpRecipient.builder().forStringPayload().url("test").build());
        unmarshalled = new JobDetailsBuilder().id("test").trigger(trigger).recipient(recipient).build();
        marshalled = new JsonObject().put("id", "test");

        jobDetailsMarshaller = mock(JobDetailsMarshaller.class);
        when(jobDetailsMarshaller.marshall(any(JobDetails.class))).thenReturn(marshalled);
        when(jobDetailsMarshaller.unmarshall(any(JsonObject.class))).thenReturn(unmarshalled);

        mongoDBJobRepository = new MongoDBJobRepository(null, null, mongoClient, "test", jobDetailsMarshaller);
    }

    @Test
    void doSave() {
        CompletionStage<JobDetails> result = mongoDBJobRepository.doSave(unmarshalled);
        assertEquals(completableFuture, result);

        ArgumentCaptor<Bson> filterCaptor = ArgumentCaptor.forClass(Bson.class);
        ArgumentCaptor<Document> documentCaptor = ArgumentCaptor.forClass(Document.class);
        ArgumentCaptor<FindOneAndReplaceOptions> optionCaptor = ArgumentCaptor.forClass(FindOneAndReplaceOptions.class);
        verify(collection, times(1)).findOneAndReplace(filterCaptor.capture(), documentCaptor.capture(), optionCaptor.capture());
        verify(jobDetailsMarshaller, times(1)).marshall(unmarshalled);
        verify(jobDetailsMarshaller, atLeastOnce()).unmarshall(marshalled);

        assertEquals(eq(ID, unmarshalled.getId()), filterCaptor.getValue());
        assertEquals(Document.parse(marshalled.toString()), documentCaptor.getValue());
        assertTrue(optionCaptor.getValue().isUpsert());
        assertEquals(ReturnDocument.AFTER, optionCaptor.getValue().getReturnDocument());
    }

    @Test
    void get() {
        CompletionStage<JobDetails> result = mongoDBJobRepository.get(unmarshalled.getId());
        assertEquals(completableFuture, result);

        ArgumentCaptor<Bson> filterCaptor = ArgumentCaptor.forClass(Bson.class);
        verify(collection, times(1)).find(filterCaptor.capture());
        verify(jobDetailsMarshaller, atLeastOnce()).unmarshall(marshalled);

        assertEquals(eq(ID, unmarshalled.getId()), filterCaptor.getValue());
    }

    @Test
    void exists() {
        CompletionStage<Boolean> result = mongoDBJobRepository.exists(unmarshalled.getId());
        assertEquals(completableFuture, result);

        ArgumentCaptor<Bson> filterCaptor = ArgumentCaptor.forClass(Bson.class);
        verify(collection, times(1)).find(filterCaptor.capture());
        verify(jobDetailsMarshaller, times(1)).unmarshall(marshalled);

        assertEquals(eq(ID, unmarshalled.getId()), filterCaptor.getValue());
    }

    @Test
    void delete() {
        CompletionStage<JobDetails> result = mongoDBJobRepository.delete(unmarshalled.getId());
        assertEquals(completableFuture, result);

        ArgumentCaptor<Bson> filterCaptor = ArgumentCaptor.forClass(Bson.class);
        verify(collection, times(1)).findOneAndDelete(filterCaptor.capture());
        verify(jobDetailsMarshaller, atLeastOnce()).unmarshall(marshalled);

        assertEquals(eq(ID, unmarshalled.getId()), filterCaptor.getValue());
    }

    @Test
    void findAll() {
        PublisherBuilder<JobDetails> result = mongoDBJobRepository.findAll();
        assertNotNull(result);

        verify(collection, times(1)).find();
        verify(jobDetailsMarshaller, atLeastOnce()).unmarshall(marshalled);
    }

    @Test
    void findByStatusBetweenDatesOrderByPriority() {
        ZonedDateTime from = ZonedDateTime.now();
        ZonedDateTime to = ZonedDateTime.now();

        PublisherBuilder<JobDetails> result = mongoDBJobRepository.findByStatusBetweenDatesOrderByPriority(from, to, JobStatus.SCHEDULED, JobStatus.RETRY);
        assertNotNull(result);

        ArgumentCaptor<Bson> filterCaptor = ArgumentCaptor.forClass(Bson.class);
        ArgumentCaptor<FindOptions> optionCaptor = ArgumentCaptor.forClass(FindOptions.class);
        verify(collection, times(1)).find(filterCaptor.capture(), optionCaptor.capture());
        verify(jobDetailsMarshaller, atLeastOnce()).unmarshall(marshalled);

        assertEquals(and(
                in("status", Arrays.stream(new JobStatus[] { JobStatus.SCHEDULED, JobStatus.RETRY }).map(Enum::name).collect(toList())),
                gt("trigger.nextFireTime", from.toInstant().toEpochMilli()),
                lt("trigger.nextFireTime", to.toInstant().toEpochMilli())),
                filterCaptor.getValue());
    }

    @Test
    void onStart() {
        mongoDBJobRepository.onStart(null);

        ArgumentCaptor<Bson> indexCaptor = ArgumentCaptor.forClass(Bson.class);
        verify(collection, times(1)).createIndex(indexCaptor.capture());

        assertEquals(ascending("status", "trigger.nextFireTime"), indexCaptor.getValue());
    }

    @Test
    void documentToJson() {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("testKey1", "testValue1");
        objectMap.put("testKey2", 1618276009165L);
        JsonObject object = new JsonObject(objectMap);

        Document document = new Document()
                .append("testKey1", "testValue1")
                .append("testKey2", 1618276009165L);

        assertEquals(object, MongoDBJobRepository.documentToJson(document));
    }

    @Test
    void jsonToDocument() {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("testKey1", "testValue1");
        objectMap.put("testKey2", 1618276009165L);
        JsonObject object = new JsonObject(objectMap);

        Document document = new Document()
                .append("testKey1", "testValue1")
                .append("testKey2", 1618276009165L);

        assertEquals(document, MongoDBJobRepository.jsonToDocument(object));
    }
}
