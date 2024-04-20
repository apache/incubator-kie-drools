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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.repository.impl.BaseReactiveJobRepository;
import org.kie.kogito.jobs.service.repository.marshaller.JobDetailsMarshaller;
import org.kie.kogito.jobs.service.stream.JobEventPublisher;

import com.mongodb.client.model.FindOneAndReplaceOptions;

import io.quarkus.mongodb.FindOptions;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Indexes.ascending;
import static com.mongodb.client.model.ReturnDocument.AFTER;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Sorts.orderBy;
import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.toList;
import static mutiny.zero.flow.adapters.AdaptersToReactiveStreams.publisher;
import static org.bson.Document.parse;
import static org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams.fromPublisher;
import static org.kie.kogito.jobs.service.utils.ModelUtil.jobWithCreatedAndLastUpdate;

@ApplicationScoped
public class MongoDBJobRepository extends BaseReactiveJobRepository implements ReactiveJobRepository {

    static final String DATABASE_PROPERTY = "quarkus.mongodb.database";

    static final String ID = "_id";

    static final String JOB_DETAILS_COLLECTION = "jobDetails.v2";

    static final String STATUS_COLUMN = "status";

    static final String FIRE_TIME_COLUMN = "trigger.nextFireTime";

    static final String CREATED_COLUMN = "created";

    private static final JsonWriterSettings jsonWriterSettings = JsonWriterSettings.builder()
            .int64Converter((value, writer) -> writer.writeNumber(value.toString())).build();

    private ReactiveMongoCollection<Document> collection;

    private JobDetailsMarshaller jobDetailsMarshaller;

    MongoDBJobRepository() {
        super(null, null);
    }

    @Inject
    public MongoDBJobRepository(Vertx vertx, JobEventPublisher jobEventPublisher, ReactiveMongoClient mongoClient,
            @ConfigProperty(name = DATABASE_PROPERTY) String database,
            JobDetailsMarshaller jobDetailsMarshaller) {
        super(vertx, jobEventPublisher);
        this.jobDetailsMarshaller = jobDetailsMarshaller;
        this.collection = mongoClient.getDatabase(database).getCollection(JOB_DETAILS_COLLECTION);
    }

    void onStart(@Observes StartupEvent ev) {
        this.collection.createIndex(ascending(STATUS_COLUMN, FIRE_TIME_COLUMN)).await().indefinitely();
        this.collection.createIndex(ascending(CREATED_COLUMN, FIRE_TIME_COLUMN, ID)).await().indefinitely();
    }

    @Override
    public CompletionStage<JobDetails> doSave(JobDetails job) {
        return collection.find(eq(ID, job.getId()))
                .collect().with(counting())
                .map(count -> jobWithCreatedAndLastUpdate(count == 0, job))
                .chain(this::findAndUpdate)
                .emitOn(Infrastructure.getDefaultExecutor())
                .convert()
                .toCompletionStage();
    }

    private Uni<JobDetails> findAndUpdate(JobDetails job) {
        return collection.findOneAndReplace(
                eq(ID, job.getId()),
                jsonToDocument(jobDetailsMarshaller.marshall(job)),
                new FindOneAndReplaceOptions().upsert(true).returnDocument(AFTER))
                .map(MongoDBJobRepository::documentToJson)
                .map(jobDetailsMarshaller::unmarshall);
    }

    @Override
    public CompletionStage<JobDetails> get(String id) {
        return collection.find(eq(ID, id))
                .collect().first()
                .map(MongoDBJobRepository::documentToJson)
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
                .map(MongoDBJobRepository::documentToJson)
                .map(jobDetailsMarshaller::unmarshall)
                .emitOn(Infrastructure.getDefaultExecutor())
                .convert()
                .toCompletionStage();
    }

    @Override
    public PublisherBuilder<JobDetails> findByStatusBetweenDates(ZonedDateTime fromFireTime,
            ZonedDateTime toFireTime,
            JobStatus[] status,
            SortTerm[] orderBy) {

        FindOptions findOptions = new FindOptions();
        List<Bson> filters = new ArrayList<>();
        if (status != null && status.length > 0) {
            filters.add(createStatusFilter(status));
        }
        filters.add(gte(FIRE_TIME_COLUMN, fromFireTime.toInstant().toEpochMilli()));
        filters.add(lte(FIRE_TIME_COLUMN, toFireTime.toInstant().toEpochMilli()));
        findOptions.filter(and(filters));

        if (orderBy != null && orderBy.length > 0) {
            findOptions.sort(createOrderBy(orderBy));
        }

        return fromPublisher(publisher(
                collection.find(findOptions)
                        .map(MongoDBJobRepository::documentToJson)
                        .map(jobDetailsMarshaller::unmarshall)
                        .emitOn(Infrastructure.getDefaultExecutor())
                        .convert()
                        .toPublisher()));
    }

    static JsonObject documentToJson(Document document) {
        return ofNullable(document).map(doc -> new JsonObject(doc.toJson(jsonWriterSettings))).orElse(null);
    }

    static Document jsonToDocument(JsonObject jsonNode) {
        return ofNullable(jsonNode).map(json -> parse(json.toString())).orElse(null);
    }

    static Bson createStatusFilter(JobStatus... status) {
        return in(STATUS_COLUMN, stream(status).map(Enum::name).collect(toList()));
    }

    static Bson createOrderBy(SortTerm[] sortTerms) {
        return orderBy(stream(sortTerms).map(MongoDBJobRepository::createOrderByTerm).collect(Collectors.toList()));
    }

    static Bson createOrderByTerm(SortTerm sortTerm) {
        String columnName = toColumName(sortTerm.getField());
        return sortTerm.isAsc() ? ascending(columnName) : descending(columnName);
    }

    static String toColumName(SortTermField field) {
        return switch (field) {
            case FIRE_TIME -> FIRE_TIME_COLUMN;
            case CREATED -> CREATED_COLUMN;
            case ID -> ID;
            default -> throw new IllegalArgumentException("No colum name is defined for field: " + field);
        };
    }
}
