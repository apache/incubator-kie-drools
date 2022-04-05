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

import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.configuration.CodecRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.AbstractDataEvent;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.UserTaskInstanceDataEvent;
import org.kie.kogito.event.process.VariableInstanceDataEvent;
import org.kie.kogito.mongodb.transaction.AbstractTransactionManager;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import static org.kie.kogito.events.mongodb.MongoDBEventPublisher.ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MongoDBEventPublisherTest {

    private MongoClient mongoClient;

    private MongoDatabase mongoDatabase;

    private AbstractTransactionManager transactionManager;

    private MongoCollection mongoCollection;

    private ProcessInstanceDataEvent processInstanceDataEvent;

    private UserTaskInstanceDataEvent userTaskInstanceDataEvent;

    private VariableInstanceDataEvent variableInstanceDataEvent;

    private AbstractDataEvent<?> event;

    private MongoDBEventPublisher publisher = new MongoDBEventPublisher() {
        @Override
        protected MongoClient mongoClient() {
            return mongoClient;
        }

        @Override
        protected AbstractTransactionManager transactionManager() {
            return transactionManager;
        }

        @Override
        protected boolean processInstancesEvents() {
            return true;
        }

        @Override
        protected boolean userTasksEvents() {
            return true;
        }

        @Override
        protected boolean variablesEvents() {
            return true;
        }

        @Override
        protected String eventsDatabaseName() {
            return "testDB";
        }

        @Override
        protected String processInstancesEventsCollection() {
            return "testPICollection";
        }

        @Override
        protected String userTasksEventsCollection() {
            return "testTECollection";
        }

        @Override
        protected String variablesEventsCollection() {
            return "testVCollection";
        }
    };

    @BeforeEach
    void setUp() {
        mongoClient = mock(MongoClient.class);
        mongoDatabase = mock(MongoDatabase.class);
        mongoCollection = mock(MongoCollection.class);
        when(mongoClient.getDatabase(any())).thenReturn(mongoDatabase);
        when(mongoDatabase.withCodecRegistry(any())).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection(any(), any())).thenReturn(mongoCollection);
        when(mongoCollection.withCodecRegistry(any())).thenReturn(mongoCollection);

        transactionManager = mock(AbstractTransactionManager.class);

        processInstanceDataEvent = mock(ProcessInstanceDataEvent.class);
        when(processInstanceDataEvent.getType()).thenReturn("ProcessInstanceEvent");
        when(processInstanceDataEvent.getId()).thenReturn("testProcessInstanceEvent");

        userTaskInstanceDataEvent = mock(UserTaskInstanceDataEvent.class);
        when(userTaskInstanceDataEvent.getType()).thenReturn("UserTaskInstanceEvent");
        when(userTaskInstanceDataEvent.getId()).thenReturn("testUserTaskInstanceEvent");

        variableInstanceDataEvent = mock(VariableInstanceDataEvent.class);
        when(variableInstanceDataEvent.getType()).thenReturn("VariableInstanceEvent");
        when(variableInstanceDataEvent.getId()).thenReturn("testVariableInstanceEvent");

        event = mock(AbstractDataEvent.class);
        when(event.getType()).thenReturn("test");
    }

    @Test
    void configure() {
        publisher.configure();
        verify(mongoClient).getDatabase(eq("testDB"));
        verify(mongoDatabase).getCollection(eq("testPICollection"), eq(ProcessInstanceDataEvent.class));
        verify(mongoDatabase).getCollection(eq("testTECollection"), eq(UserTaskInstanceDataEvent.class));
        verify(mongoDatabase).getCollection(eq("testVCollection"), eq(VariableInstanceDataEvent.class));
        verify(mongoDatabase).withCodecRegistry(any(CodecRegistry.class));
        verify(mongoCollection, times(3)).withCodecRegistry(any(CodecRegistry.class));
    }

    @Test
    void publish() {
        publisher.configure();

        publisher.publish(processInstanceDataEvent);
        verify(mongoCollection).insertOne(eq(processInstanceDataEvent));
        verify(mongoCollection).deleteOne(eq(Filters.eq(ID, "testProcessInstanceEvent")));

        publisher.publish(userTaskInstanceDataEvent);
        verify(mongoCollection).insertOne(eq(userTaskInstanceDataEvent));
        verify(mongoCollection).deleteOne(eq(Filters.eq(ID, "testUserTaskInstanceEvent")));

        publisher.publish(variableInstanceDataEvent);
        verify(mongoCollection).insertOne(eq(variableInstanceDataEvent));
        verify(mongoCollection).deleteOne(eq(Filters.eq(ID, "testVariableInstanceEvent")));

        publisher.publish(event);
        verify(mongoCollection, times(3)).insertOne(any());
        verify(mongoCollection, times(3)).deleteOne(any());
    }

    @Test
    void publish_withTransaction() {
        ClientSession clientSession = mock(ClientSession.class);
        when(transactionManager.getClientSession()).thenReturn(clientSession);
        when(transactionManager.enabled()).thenReturn(true);

        publisher.configure();

        publisher.publish(processInstanceDataEvent);
        verify(mongoCollection).insertOne(eq(clientSession), eq(processInstanceDataEvent));
        verify(mongoCollection).deleteOne(eq(clientSession), eq(Filters.eq(ID, "testProcessInstanceEvent")));

        publisher.publish(userTaskInstanceDataEvent);
        verify(mongoCollection).insertOne(eq(clientSession), eq(userTaskInstanceDataEvent));
        verify(mongoCollection).deleteOne(eq(clientSession), eq(Filters.eq(ID, "testUserTaskInstanceEvent")));

        publisher.publish(variableInstanceDataEvent);
        verify(mongoCollection).insertOne(eq(clientSession), eq(variableInstanceDataEvent));
        verify(mongoCollection).deleteOne(eq(clientSession), eq(Filters.eq(ID, "testVariableInstanceEvent")));

        publisher.publish(event);
        verify(mongoCollection, times(3)).insertOne(eq(clientSession), any(AbstractDataEvent.class));
        verify(mongoCollection, times(3)).deleteOne(eq(clientSession), any());
    }

    @Test
    void testPublishEvents() {
        publisher.configure();

        List<DataEvent<?>> events = new ArrayList<>();
        events.add(processInstanceDataEvent);
        events.add(userTaskInstanceDataEvent);
        events.add(variableInstanceDataEvent);
        events.add(event);

        publisher.publish(events);

        verify(mongoCollection, times(3)).insertOne(any());
        verify(mongoCollection, times(3)).deleteOne(any());

        verify(mongoCollection).insertOne(eq(processInstanceDataEvent));
        verify(mongoCollection).deleteOne(eq(Filters.eq(ID, "testProcessInstanceEvent")));

        verify(mongoCollection).insertOne(eq(userTaskInstanceDataEvent));
        verify(mongoCollection).deleteOne(eq(Filters.eq(ID, "testUserTaskInstanceEvent")));

        verify(mongoCollection).insertOne(eq(variableInstanceDataEvent));
        verify(mongoCollection).deleteOne(eq(Filters.eq(ID, "testVariableInstanceEvent")));
    }

    @Test
    void testPublishEvents_withTransaction() {
        ClientSession clientSession = mock(ClientSession.class);
        when(transactionManager.getClientSession()).thenReturn(clientSession);
        when(transactionManager.enabled()).thenReturn(true);

        publisher.configure();

        List<DataEvent<?>> events = new ArrayList<>();
        events.add(processInstanceDataEvent);
        events.add(userTaskInstanceDataEvent);
        events.add(variableInstanceDataEvent);
        events.add(event);

        publisher.publish(events);

        verify(mongoCollection, times(3)).insertOne(eq(clientSession), any(AbstractDataEvent.class));
        verify(mongoCollection, times(3)).deleteOne(eq(clientSession), any());

        verify(mongoCollection).insertOne(eq(clientSession), eq(processInstanceDataEvent));
        verify(mongoCollection).deleteOne(eq(clientSession), eq(Filters.eq(ID, "testProcessInstanceEvent")));

        verify(mongoCollection).insertOne(eq(clientSession), eq(userTaskInstanceDataEvent));
        verify(mongoCollection).deleteOne(eq(clientSession), eq(Filters.eq(ID, "testUserTaskInstanceEvent")));

        verify(mongoCollection).insertOne(eq(clientSession), eq(variableInstanceDataEvent));
        verify(mongoCollection).deleteOne(eq(clientSession), eq(Filters.eq(ID, "testVariableInstanceEvent")));
    }
}
