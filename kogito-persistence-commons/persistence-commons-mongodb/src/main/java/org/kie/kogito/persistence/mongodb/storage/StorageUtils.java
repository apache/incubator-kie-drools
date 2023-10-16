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
package org.kie.kogito.persistence.mongodb.storage;

import java.util.function.Function;

import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.kie.kogito.persistence.mongodb.model.MongoEntityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.ChangeStreamIterable;
import com.mongodb.client.MongoChangeStreamCursor;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.changestream.ChangeStreamDocument;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.infrastructure.Infrastructure;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.changestream.FullDocument.UPDATE_LOOKUP;
import static java.util.Collections.singletonList;
import static org.kie.kogito.persistence.mongodb.model.ModelUtils.MONGO_ID;

public class StorageUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageUtils.class);

    private StorageUtils() {

    }

    public static <V, E> Multi<V> watchCollectionEntries(MongoCollection<E> collection, Bson operationType, MongoEntityMapper<V, E> mapper) {
        return createMulti(collection, operationType, csd -> {
            E document = csd.getFullDocument();
            return document == null ? null : mapper.mapToModel(document);
        });
    }

    public static <E> Multi<String> watchCollectionKeys(MongoCollection<E> collection, Bson operationType) {
        return createMulti(collection, operationType, csd -> {
            BsonDocument keyDocument = csd.getDocumentKey();
            return keyDocument == null ? null : keyDocument.getString(MONGO_ID).getValue();
        });
    }

    private static <T, E> Multi<T> createMulti(MongoCollection<E> collection, Bson operationType, Function<ChangeStreamDocument<E>, T> mapper) {
        ChangeStreamIterable<E> changeStream = collection.watch(singletonList(match(operationType))).fullDocument(UPDATE_LOOKUP);

        MongoChangeStreamCursor<ChangeStreamDocument<E>> cursor = changeStream.cursor();

        return Multi.createFrom()
                .<ChangeStreamDocument<E>> emitter(emitter -> {
                    try {
                        while (cursor.hasNext()) {
                            emitter.emit(cursor.next());
                        }
                    } catch (IllegalStateException ex) {
                        LOGGER.warn("MongoDB cursor exception: " + ex.getMessage());
                    }
                })
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
                .onTermination().invoke(() -> cursor.close())
                .onFailure().recoverWithCompletion()
                .onItem().transform(mapper);
    }
}
