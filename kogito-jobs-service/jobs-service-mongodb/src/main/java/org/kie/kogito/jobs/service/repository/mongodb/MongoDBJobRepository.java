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
package org.kie.kogito.jobs.service.repository.mongodb;

import java.time.ZonedDateTime;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.repository.impl.BaseReactiveJobRepository;
import org.kie.kogito.jobs.service.repository.marshaller.JobDetailsMarshaller;
import org.kie.kogito.jobs.service.stream.JobStreams;

import com.mongodb.client.model.FindOneAndReplaceOptions;

import io.quarkus.mongodb.FindOptions;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Indexes.ascending;
import static com.mongodb.client.model.ReturnDocument.AFTER;
import static com.mongodb.client.model.Sorts.descending;
import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.toList;
import static org.bson.Document.parse;
import static org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams.fromPublisher;

@ApplicationScoped
public class MongoDBJobRepository extends BaseReactiveJobRepository implements ReactiveJobRepository {

    static final String DATABASE_PROPERTY = "quarkus.mongodb.database";

    static final String ID = "_id";

    static final String JOB_DETAILS_COLLECTION = "jobDetails.v2";

    static final String STATUS_COLUMN = "status";

    static final String FIRE_TIME_COLUMN = "trigger.nextFireTime";

    private static final JsonWriterSettings jsonWriterSettings = JsonWriterSettings.builder()
            .int64Converter((value, writer) -> writer.writeNumber(value.toString())).build();

    private ReactiveMongoCollection<Document> collection;

    private JobDetailsMarshaller jobDetailsMarshaller;

    MongoDBJobRepository() {
        super(null, null);
    }

    @Inject
    public MongoDBJobRepository(Vertx vertx, JobStreams jobStreams, ReactiveMongoClient mongoClient,
            @ConfigProperty(name = DATABASE_PROPERTY) String database,
            JobDetailsMarshaller jobDetailsMarshaller) {
        super(vertx, jobStreams);
        this.jobDetailsMarshaller = jobDetailsMarshaller;
        this.collection = mongoClient.getDatabase(database).getCollection(JOB_DETAILS_COLLECTION);
    }

    void onStart(@Observes StartupEvent ev) {
        this.collection.createIndex(ascending(STATUS_COLUMN, FIRE_TIME_COLUMN)).await().indefinitely();
    }

    @Override
    public CompletionStage<JobDetails> doSave(JobDetails job) {
        return collection.findOneAndReplace(
                eq(ID, job.getId()),
                jsonToDocument(jobDetailsMarshaller.marshall(job)),
                new FindOneAndReplaceOptions().upsert(true).returnDocument(AFTER))
                .map(document -> documentToJson(document))
                .map(jobDetailsMarshaller::unmarshall)
                .emitOn(Infrastructure.getDefaultExecutor())
                .convert()
                .toCompletionStage();
    }

    @Override
    public CompletionStage<JobDetails> get(String id) {
        return collection.find(eq(ID, id))
                .collect().first()
                .map(document -> documentToJson(document))
                .map(jobDetailsMarshaller::unmarshall)
                .emitOn(Infrastructure.getDefaultExecutor())
                .convert()
                .toCompletionStage();
    }

    @Override
    public CompletionStage<Boolean> exists(String id) {
        return collection.find(eq(ID, id))
                .collect().with(counting())
                .map(count -> count > 0)
                .emitOn(Infrastructure.getDefaultExecutor())
                .convert()
                .toCompletionStage();
    }

    @Override
    public CompletionStage<JobDetails> delete(String id) {
        return collection.findOneAndDelete(eq(ID, id))
                .map(document -> documentToJson(document))
                .map(jobDetailsMarshaller::unmarshall)
                .emitOn(Infrastructure.getDefaultExecutor())
                .convert()
                .toCompletionStage();
    }

    @Override
    public PublisherBuilder<JobDetails> findAll() {
        return fromPublisher(collection.find()
                .map(document -> documentToJson(document))
                .map(jobDetailsMarshaller::unmarshall)
                .emitOn(Infrastructure.getDefaultExecutor())
                .convert()
                .toPublisher());
    }

    @Override
    public PublisherBuilder<JobDetails> findByStatusBetweenDatesOrderByPriority(ZonedDateTime from, ZonedDateTime to, JobStatus... status) {
        return fromPublisher(
                collection.find(
                        and(
                                in(STATUS_COLUMN, stream(status).map(Enum::name).collect(toList())),
                                gt(FIRE_TIME_COLUMN, from.toInstant().toEpochMilli()),
                                lt(FIRE_TIME_COLUMN, to.toInstant().toEpochMilli())),
                        new FindOptions().sort(descending("priority")))
                        .map(document -> documentToJson(document))
                        .map(jobDetailsMarshaller::unmarshall)
                        .emitOn(Infrastructure.getDefaultExecutor())
                        .convert()
                        .toPublisher());
    }

    static JsonObject documentToJson(Document document) {
        return ofNullable(document).map(doc -> new JsonObject(doc.toJson(jsonWriterSettings))).orElse(null);
    }

    static Document jsonToDocument(JsonObject jsonNode) {
        return ofNullable(jsonNode).map(json -> parse(json.toString())).orElse(null);
    }
}
