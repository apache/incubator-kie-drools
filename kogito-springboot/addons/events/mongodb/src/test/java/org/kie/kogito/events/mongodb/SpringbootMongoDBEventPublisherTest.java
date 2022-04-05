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

package org.kie.kogito.events.mongodb;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.mongodb.transaction.AbstractTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { SpringbootMongoDBEventPublisher.class, SpringbootMongoDBEventPublisherTest.TestConfig.class },
        properties = { "kogito.events.processinstances.enabled=false", "kogito.events.usertasks.enabled=false", "kogito.events.variables.enabled=false",
                "kogito.events.processinstances.collection=testPICollection", "kogito.events.usertasks.collection=testUTCollection",
                "kogito.events.variables.collection=testVCollection", "kogito.events.database=testDB" })
class SpringbootMongoDBEventPublisherTest {

    @TestConfiguration
    public static class TestConfig {
        @Bean
        @Primary
        public MongoClient mockMongoClient() {
            MongoClient mongoClient = mock(MongoClient.class);
            MongoDatabase mongoDatabase = mock(MongoDatabase.class);
            MongoCollection mongoCollection = mock(MongoCollection.class);
            when(mongoClient.getDatabase(any())).thenReturn(mongoDatabase);
            when(mongoDatabase.withCodecRegistry(any())).thenReturn(mongoDatabase);
            when(mongoDatabase.getCollection(any(), any())).thenReturn(mongoCollection);
            when(mongoCollection.withCodecRegistry(any())).thenReturn(mongoCollection);
            return mongoClient;
        }
    }

    @Autowired
    MongoClient mongoClient;

    @MockBean
    AbstractTransactionManager transactionManager;

    @Autowired
    SpringbootMongoDBEventPublisher publisher;

    @Test
    void setup() {
        publisher.setupSpringbootMongoDBEventPublisher();
        verify(mongoClient, atLeastOnce()).getDatabase(eq("testDB"));
    }

    @Test
    void mongoClient() {
        assertEquals(mongoClient, publisher.mongoClient());
    }

    @Test
    void setTransactionManager() {
        assertEquals(transactionManager, publisher.transactionManager());
    }

    @Test
    void processInstancesEvents() {
        assertFalse(publisher.processInstancesEvents());
    }

    @Test
    void userTasksEvents() {
        assertFalse(publisher.userTasksEvents());
    }

    @Test
    void variablesEvents() {
        assertFalse(publisher.variablesEvents());
    }

    @Test
    void eventsDatabaseName() {
        assertEquals("testDB", publisher.eventsDatabaseName());
    }

    @Test
    void processInstancesEventsCollection() {
        assertEquals("testPICollection", publisher.processInstancesEventsCollection());
    }

    @Test
    void userTasksEventsCollection() {
        assertEquals("testUTCollection", publisher.userTasksEventsCollection());
    }

    @Test
    void variablesEventsCollection() {
        assertEquals("testVCollection", publisher.variablesEventsCollection());
    }
}
