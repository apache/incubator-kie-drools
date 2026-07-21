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
package org.kie.kogito.mongodb.correlation;

import java.io.UncheckedIOException;
import java.util.Map;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.kie.kogito.correlation.CompositeCorrelation;
import org.kie.kogito.correlation.Correlation;
import org.kie.kogito.correlation.CorrelationInstance;
import org.kie.kogito.correlation.SimpleCorrelation;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;

public class MongoDBCorrelationRepository {

    private final MongoCollection<Document> collection;
    private final ObjectMapper objectMapper;

    private static final String ENCODED_CORRELATION_ID_FIELD = "encodedCorrelationId";
    private static final String CORRELATED_ID_FIELD = "correlatedId";
    private static final String CORRELATION_FIELD = "correlation";
    private static final String CORRELATION_COLLECTION_NAME = "correlations";

    public MongoDBCorrelationRepository(MongoClient mongoClient, String dbName) {
        CodecRegistry registry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry());
        this.collection = mongoClient.getDatabase(dbName).getCollection(CORRELATION_COLLECTION_NAME).withCodecRegistry(registry);
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addAbstractTypeMapping(Correlation.class, SimpleCorrelation.class);
        this.objectMapper = ObjectMapperFactory.get().copy().registerModule(simpleModule);
    }

    public CorrelationInstance insert(final String encodedCorrelationId, final String correlatedId, final Correlation correlation) {

        CorrelationInstance correlationInstance = new CorrelationInstance(encodedCorrelationId, correlatedId, correlation);
        try {
            Map<String, Object> object = Map.of(
                    ENCODED_CORRELATION_ID_FIELD, encodedCorrelationId,
                    CORRELATED_ID_FIELD, correlatedId,
                    CORRELATION_FIELD, correlation);
            String json = this.objectMapper.writeValueAsString(object);
            InsertOneResult insertOneResult = this.collection.insertOne(Document.parse(json));
            return insertOneResult.getInsertedId() != null ? correlationInstance : null;
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    public CorrelationInstance findByEncodedCorrelationId(String encoded) {
        Bson eq = Filters.eq(ENCODED_CORRELATION_ID_FIELD, encoded);
        return getCorrelationInstanceByFilter(eq);
    }

    public CorrelationInstance findByCorrelatedId(String correlatedId) {
        Bson eq = Filters.eq(CORRELATED_ID_FIELD, correlatedId);
        return getCorrelationInstanceByFilter(eq);
    }

    private CorrelationInstance getCorrelationInstanceByFilter(Bson eq) {
        Document first = this.collection.find(eq).first();
        if (first == null) {
            return null;
        } else {
            Document document = first.get(CORRELATION_FIELD, Document.class);
            try {
                CompositeCorrelation compositeCorrelation = this.objectMapper.readValue(document.toJson(), CompositeCorrelation.class);
                return new CorrelationInstance(
                        first.getString(ENCODED_CORRELATION_ID_FIELD),
                        first.getString(CORRELATED_ID_FIELD),
                        compositeCorrelation);
            } catch (JsonProcessingException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    public void delete(String encoded) {
        Bson eq = Filters.eq(ENCODED_CORRELATION_ID_FIELD, encoded);
        this.collection.deleteOne(eq);
    }
}
