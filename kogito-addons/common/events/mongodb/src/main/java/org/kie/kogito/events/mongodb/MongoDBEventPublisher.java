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

import java.util.Collection;
import java.util.function.BooleanSupplier;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventPublisher;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.UserTaskInstanceDataEvent;
import org.kie.kogito.event.process.VariableInstanceDataEvent;
import org.kie.kogito.events.mongodb.codec.EventMongoDBCodecProvider;
import org.kie.kogito.mongodb.transaction.AbstractTransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

public abstract class MongoDBEventPublisher implements EventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(MongoDBEventPublisher.class);

    static final String ID = "_id";

    private MongoCollection<ProcessInstanceDataEvent> processInstanceDataEventCollection;
    private MongoCollection<UserTaskInstanceDataEvent> userTaskInstanceDataEventCollection;
    private MongoCollection<VariableInstanceDataEvent> variableInstanceDataEventCollection;

    protected abstract MongoClient mongoClient();

    protected abstract AbstractTransactionManager transactionManager();

    protected abstract boolean processInstancesEvents();

    protected abstract boolean userTasksEvents();

    protected abstract boolean variablesEvents();

    protected abstract String eventsDatabaseName();

    protected abstract String processInstancesEventsCollection();

    protected abstract String userTasksEventsCollection();

    protected abstract String variablesEventsCollection();

    protected void configure() {
        CodecRegistry registry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), fromProviders(new EventMongoDBCodecProvider(),
                PojoCodecProvider.builder().automatic(true).build()));
        MongoDatabase mongoDatabase = mongoClient().getDatabase(eventsDatabaseName()).withCodecRegistry(registry);
        processInstanceDataEventCollection = mongoDatabase.getCollection(processInstancesEventsCollection(), ProcessInstanceDataEvent.class).withCodecRegistry(registry);
        userTaskInstanceDataEventCollection = mongoDatabase.getCollection(userTasksEventsCollection(), UserTaskInstanceDataEvent.class).withCodecRegistry(registry);
        variableInstanceDataEventCollection = mongoDatabase.getCollection(variablesEventsCollection(), VariableInstanceDataEvent.class).withCodecRegistry(registry);
    }

    @Override
    public void publish(DataEvent<?> event) {
        switch (event.getType()) {
            case "ProcessInstanceEvent":
                publishEvent(processInstanceDataEventCollection, (ProcessInstanceDataEvent) event, this::processInstancesEvents);
                break;
            case "UserTaskInstanceEvent":
                publishEvent(userTaskInstanceDataEventCollection, (UserTaskInstanceDataEvent) event, this::userTasksEvents);
                break;
            case "VariableInstanceEvent":
                publishEvent(variableInstanceDataEventCollection, (VariableInstanceDataEvent) event, this::variablesEvents);
                break;
            default:
                logger.warn("Unknown type of event '{}', ignoring", event.getType());
        }
    }

    private <T extends DataEvent<?>> void publishEvent(MongoCollection<T> collection, T event, BooleanSupplier enabled) {
        if (enabled.getAsBoolean()) {
            if (transactionManager().enabled()) {
                collection.insertOne(transactionManager().getClientSession(), event);
                // delete the event immediately from the outbox collection
                collection.deleteOne(transactionManager().getClientSession(), Filters.eq(ID, event.getId()));
            } else {
                collection.insertOne(event);
                // delete the event from the outbox collection
                collection.deleteOne(Filters.eq(ID, event.getId()));
            }
        }
    }

    @Override
    public void publish(Collection<DataEvent<?>> events) {
        for (DataEvent<?> event : events) {
            publish(event);
        }
    }
}
