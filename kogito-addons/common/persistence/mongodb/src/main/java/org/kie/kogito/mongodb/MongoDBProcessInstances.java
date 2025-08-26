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
package org.kie.kogito.mongodb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.jbpm.flow.serialization.MarshallerContextName;
import org.jbpm.flow.serialization.ProcessInstanceMarshallerService;
import org.kie.kogito.Model;
import org.kie.kogito.internal.process.runtime.HeadersPersistentConfig;
import org.kie.kogito.mongodb.transaction.AbstractTransactionManager;
import org.kie.kogito.process.MutableProcessInstances;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceDuplicatedException;
import org.kie.kogito.process.ProcessInstanceOptimisticLockingException;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.impl.AbstractProcessInstance;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.UpdateResult;

import static org.kie.kogito.mongodb.utils.DocumentConstants.PROCESS_BUSINESS_KEY;
import static org.kie.kogito.mongodb.utils.DocumentConstants.PROCESS_BUSINESS_KEY_INDEX;
import static org.kie.kogito.mongodb.utils.DocumentConstants.PROCESS_INSTANCE_ID;
import static org.kie.kogito.mongodb.utils.DocumentConstants.PROCESS_INSTANCE_ID_INDEX;

public class MongoDBProcessInstances<T extends Model> implements MutableProcessInstances<T> {

    private static final String VERSION = "version";
    private org.kie.kogito.process.Process<?> process;
    private ProcessInstanceMarshallerService marshaller;
    private final MongoCollection<Document> collection;
    private final MongoCollection<Document> events;
    private final AbstractTransactionManager transactionManager;
    private final boolean lock;

    public MongoDBProcessInstances(MongoClient mongoClient, org.kie.kogito.process.Process<?> process, String dbName, AbstractTransactionManager transactionManager, boolean lock) {
        this(mongoClient, process, dbName, transactionManager, lock, null);
    }

    public MongoDBProcessInstances(MongoClient mongoClient, org.kie.kogito.process.Process<?> process, String dbName, AbstractTransactionManager transactionManager, boolean lock,
            HeadersPersistentConfig headersConfig) {
        this.process = process;
        this.collection = Objects.requireNonNull(getCollection(mongoClient, process.id(), dbName));
        this.events = Objects.requireNonNull(getCollection(mongoClient, process.id() + "-events", dbName));
        this.marshaller = ProcessInstanceMarshallerService.newBuilder()
                .withDefaultObjectMarshallerStrategies()
                .withDefaultListeners()
                .withContextEntry(MarshallerContextName.MARSHALLER_FORMAT, MarshallerContextName.MARSHALLER_FORMAT_JSON)
                .withContextEntry(MarshallerContextName.MARSHALLER_HEADERS_CONFIG, headersConfig)
                .build();
        this.transactionManager = Objects.requireNonNull(transactionManager);
        this.lock = lock;
    }

    @Override
    public Optional<ProcessInstance<T>> findById(String id, ProcessInstanceReadMode mode) {
        return find(id, PROCESS_INSTANCE_ID).map(piDoc -> unmarshall(piDoc, mode));
    }

    @Override
    public Optional<ProcessInstance<T>> findByBusinessKey(String id, ProcessInstanceReadMode mode) {
        return find(id, PROCESS_BUSINESS_KEY).map(piDoc -> unmarshall(piDoc, mode));
    }

    @Override
    public Stream<ProcessInstance<T>> waitingForEventType(String eventType, ProcessInstanceReadMode mode) {
        ClientSession clientSession = transactionManager.getClientSession();
        Bson eventTypeFilter = Filters.all("eventTypes", eventType);
        List<String> processInstancesId = new ArrayList<>();

        events.find(eventTypeFilter).forEach(e -> processInstancesId.add(e.getString("id")));

        Bson filters = Filters.in("id", processInstancesId);
        MongoCursor<Document> docs = (clientSession == null ? collection.find(filters) : collection.find(clientSession, filters)).iterator();
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(docs, Spliterator.ORDERED), false).map(doc -> unmarshall(doc, mode)).onClose(docs::close);
    }

    @Override
    public Stream<ProcessInstance<T>> stream(ProcessInstanceReadMode mode) {
        ClientSession clientSession = transactionManager.getClientSession();
        MongoCursor<Document> docs = (clientSession == null ? collection.find() : collection.find(clientSession)).iterator();
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(docs, Spliterator.ORDERED), false).map(doc -> unmarshall(doc, mode)).onClose(docs::close);
    }

    private ProcessInstance<T> unmarshall(Document document, ProcessInstanceReadMode mode) {
        ProcessInstance<T> instance = (ProcessInstance<T>) marshaller.unmarshallProcessInstance(document.toJson().getBytes(), process, mode);
        setVersion(instance, document.getLong(VERSION));
        connectProcessInstance(instance, instance.id());
        return instance;
    }

    private Set<String> getUniqueEvents(ProcessInstance<T> instance) {
        return Stream.of(((AbstractProcessInstance<T>) instance).internalGetProcessInstance().getEventTypes()).collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public void create(String id, ProcessInstance<T> instance) {
        updateStorage(id, instance, true);
    }

    @Override
    public void update(String id, ProcessInstance<T> instance) {
        if (isActive(instance) || instance.status() == ProcessInstance.STATE_PENDING) {
            updateStorage(id, instance, false);
        }
    }

    protected void updateStorage(String id, ProcessInstance<T> instance, boolean checkDuplicates) {
        ClientSession clientSession = transactionManager.getClientSession();
        Document doc = Document.parse(new String(marshaller.marshallProcessInstance(instance)));
        Set<String> eventTypes = getUniqueEvents(instance);
        if (checkDuplicates) {
            createInternal(id, clientSession, doc, eventTypes);
        } else {
            updateInternal(id, instance, clientSession, doc, eventTypes);
        }
        connectProcessInstance(instance, id);
    }

    private void createInternal(String id, ClientSession clientSession, Document doc, Set<String> eventTypes) {
        if (exists(id)) {
            throw new ProcessInstanceDuplicatedException(id);
        } else {
            Document eventsDocument = new Document()
                    .append("id", id)
                    .append("eventTypes", eventTypes);
            doc.put(VERSION, 0L);
            if (clientSession != null) {
                collection.insertOne(clientSession, doc);
                events.insertOne(clientSession, eventsDocument);
            } else {
                collection.insertOne(doc);
                events.insertOne(eventsDocument);
            }
        }
    }

    private void updateInternal(String id, ProcessInstance<T> instance, ClientSession clientSession, Document doc, Set<String> eventTypes) {
        Bson filters = Filters.eq(PROCESS_INSTANCE_ID, id);
        UpdateResult result;
        if (lock) {
            doc.put(VERSION, instance.version() + 1);
            filters = Filters.and(Filters.eq(PROCESS_INSTANCE_ID, id), Filters.eq(VERSION, instance.version()));
        }
        Document eventsDocument = new Document()
                .append("id", id)
                .append("eventTypes", eventTypes);

        if (clientSession != null) {
            result = collection.replaceOne(clientSession, filters, doc);
            events.replaceOne(clientSession, filters, eventsDocument);
        } else {
            result = collection.replaceOne(filters, doc);
            events.replaceOne(filters, eventsDocument);
        }

        if (lock && result.getModifiedCount() != 1) {
            throw new ProcessInstanceOptimisticLockingException(id);
        }
    }

    private Optional<Document> find(String id, String key) {
        ClientSession clientSession = transactionManager.getClientSession();
        return Optional.ofNullable((clientSession != null ? collection.find(clientSession, Filters.eq(key, id)) : collection.find(Filters.eq(key, id))).first());
    }

    @Override
    public boolean exists(String id) {
        return find(id, PROCESS_INSTANCE_ID).isPresent();
    }

    @Override
    public void remove(String id) {
        ClientSession clientSession = transactionManager.getClientSession();
        if (clientSession != null) {
            collection.deleteOne(clientSession, Filters.eq(PROCESS_INSTANCE_ID, id));
            events.deleteOne(clientSession, Filters.eq(PROCESS_INSTANCE_ID, id));
        } else {
            collection.deleteOne(Filters.eq(PROCESS_INSTANCE_ID, id));
            events.deleteOne(Filters.eq(PROCESS_INSTANCE_ID, id));
        }
    }

    private void connectProcessInstance(ProcessInstance<T> instance, String id) {
        ((AbstractProcessInstance<?>) instance).internalSetReloadSupplier(marshaller.createdReloadFunction(() -> find(id, PROCESS_INSTANCE_ID).map(reloaded -> {
            setVersion(instance, reloaded.getLong(VERSION));
            return reloaded.toJson().getBytes();
        }).orElseThrow(() -> new IllegalArgumentException("process instance id " + id + " does not exists in mongodb"))));
    }

    private static void setVersion(ProcessInstance<?> instance, Long version) {
        ((AbstractProcessInstance<?>) instance).setVersion(version == null ? 0L : version);
    }

    @Override
    public boolean lock() {
        return this.lock;
    }

    protected MongoCollection<Document> getCollection() {
        return collection;
    }

    private MongoCollection<Document> getCollection(MongoClient mongoClient, String processId, String dbName) {
        CodecRegistry registry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry());
        MongoDatabase mongoDatabase = mongoClient.getDatabase(dbName).withCodecRegistry(registry);
        MongoCollection<Document> collection = mongoDatabase.getCollection(processId, Document.class).withCodecRegistry(registry);
        //Index creation (if the index already exists it is a no-op)
        collection.createIndex(Indexes.ascending(PROCESS_INSTANCE_ID),
                new IndexOptions().unique(true).name(PROCESS_INSTANCE_ID_INDEX).background(true));
        collection.createIndex(Indexes.ascending(PROCESS_BUSINESS_KEY),
                new IndexOptions().name(PROCESS_BUSINESS_KEY_INDEX).background(true));
        return collection;
    }
}
