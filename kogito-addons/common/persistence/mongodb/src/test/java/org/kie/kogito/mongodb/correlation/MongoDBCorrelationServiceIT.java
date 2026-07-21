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

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.correlation.CompositeCorrelation;
import org.kie.kogito.correlation.CorrelationInstance;
import org.kie.kogito.correlation.SimpleCorrelation;
import org.kie.kogito.testcontainers.KogitoMongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class MongoDBCorrelationServiceIT {

    @Container
    final static KogitoMongoDBContainer mongoDBContainer = new KogitoMongoDBContainer();
    private static MongoDBCorrelationService correlationService;
    private static MongoClient mongoClient;
    private static final String DB_NAME = "test";
    private static final String COLLECTION_NAME = "correlations";

    @BeforeAll
    static void setUp() {
        mongoDBContainer.start();
        mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl());
        correlationService = new MongoDBCorrelationService(new MongoDBCorrelationRepository(
                mongoClient, DB_NAME));
    }

    @BeforeEach
    void beforeEach() {
        mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION_NAME).drop();
    }

    @Test
    void shouldSaveCorrelation() {
        // arrange
        String correlatedId = "id";
        CompositeCorrelation correlation = new CompositeCorrelation(Collections.singleton(new SimpleCorrelation<>("city", "Rio de Janeiro")));

        // act
        correlationService.create(correlation, correlatedId);

        // assert
        Optional<CorrelationInstance> byCorrelatedId = correlationService.findByCorrelatedId(correlatedId);
        assertThat(byCorrelatedId).isNotEmpty();
    }

    @Test
    void shouldDeleteCorrelation() {
        // arrange
        String correlatedId = "id";
        CompositeCorrelation correlation = new CompositeCorrelation(Collections.singleton(new SimpleCorrelation<>("city", "São Paulo")));
        correlationService.create(correlation, correlatedId);

        // act
        correlationService.delete(correlation);

        // assert
        assertThat(correlationService.findByCorrelatedId(correlatedId)).isEmpty();
    }

    @Test
    void shouldFindByCorrelatedId() {
        // arrange
        String correlatedId = "id";
        CompositeCorrelation correlation = new CompositeCorrelation(Collections.singleton(new SimpleCorrelation<>("city", "Goiânia")));
        correlationService.create(correlation, correlatedId);

        // act
        Optional<CorrelationInstance> byCorrelatedId = correlationService.findByCorrelatedId(correlatedId);

        // assert
        assertThat(byCorrelatedId).isNotEmpty();
    }

    @Test
    void shouldFindByCorrelation() {
        // arrange
        CompositeCorrelation correlation = new CompositeCorrelation(Collections.singleton(new SimpleCorrelation<>("city", "Osasco")));
        String correlatedId = "id";

        correlationService.create(correlation, correlatedId);

        // act
        Optional<CorrelationInstance> correlationInstance = correlationService.find(correlation);

        // assert
        assertThat(correlationInstance).isNotEmpty();
    }

}
