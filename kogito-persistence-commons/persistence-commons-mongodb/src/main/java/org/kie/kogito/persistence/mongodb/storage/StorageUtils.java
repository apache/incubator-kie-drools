/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.persistence.mongodb.storage;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.kie.kogito.persistence.mongodb.model.MongoEntityMapper;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.changestream.FullDocument.UPDATE_LOOKUP;
import static java.util.Collections.singletonList;
import static org.kie.kogito.persistence.mongodb.model.ModelUtils.MONGO_ID;
import static org.kie.kogito.persistence.mongodb.model.ModelUtils.documentToObject;

public class StorageUtils {

    private StorageUtils() {

    }

    public static <V, E> void watchCollection(MongoCollection<E> reactiveMongoCollection, Bson operationType,
                                              BiConsumer<String, V> consumer, MongoEntityMapper<V, E> mongoEntityMapper) {
        reactiveMongoCollection.watch(singletonList(match(operationType)))
                .fullDocument(UPDATE_LOOKUP).subscribe(new ObjectListenerSubscriber<>(consumer, mongoEntityMapper));

        // There is no way to check if MongoDB Change Stream is ready https://jira.mongodb.org/browse/NODE-2247
        // Pause the execution to wait for the Change Stream to be ready
        try {
            TimeUnit.MILLISECONDS.sleep(1500L);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    private static class ObjectListenerSubscriber<V, E> implements Subscriber<ChangeStreamDocument<Document>> {

        Subscription subscription;
        BiConsumer<String, V> consumer;
        MongoEntityMapper<V, E> mongoEntityMapper;

        ObjectListenerSubscriber(BiConsumer<String, V> consumer, MongoEntityMapper<V, E> mongoEntityMapper) {
            this.consumer = consumer;
            this.mongoEntityMapper = mongoEntityMapper;
        }

        @Override
        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            this.subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(ChangeStreamDocument<Document> changeStreamDocument) {
            BsonDocument keyDocument = changeStreamDocument.getDocumentKey();
            Document document = changeStreamDocument.getFullDocument();
            consumer.accept(Optional.ofNullable(keyDocument).map(key -> key.getString(MONGO_ID).getValue()).orElse(null),
                            Optional.ofNullable(document).map(doc -> mongoEntityMapper.mapToModel(documentToObject(doc, mongoEntityMapper.getEntityClass(), mongoEntityMapper::convertToModelAttribute))).orElse(null));
        }

        @Override
        public void onError(Throwable throwable) {
            this.onComplete();
            throw new MongoObjectListenerException(throwable);
        }

        @Override
        public void onComplete() {
            if (Objects.nonNull(this.subscription)) {
                this.subscription.cancel();
            }
        }
    }
}
