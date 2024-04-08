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

import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.jbpm.flow.serialization.MarshallerContextName;
import org.jbpm.flow.serialization.ProcessInstanceMarshallerService;
import org.kie.kogito.Model;
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

import static java.util.Collections.singletonMap;
import static org.kie.kogito.mongodb.utils.DocumentConstants.PROCESS_INSTANCE_ID;
import static org.kie.kogito.mongodb.utils.DocumentConstants.PROCESS_INSTANCE_ID_INDEX;

public class MongoDBProcessInstances<T extends Model> implements MutableProcessInstances<T> {

    private static final String VERSION = "version";
    private org.kie.kogito.process.Process<?> process;
    private ProcessInstanceMarshallerService marshaller;
    private final MongoCollection<Document> collection;
    private final AbstractTransactionManager transactionManager;
    private final boolean lock;

    public MongoDBProcessInstances(MongoClient mongoClient, org.kie.kogito.process.Process<?> process, String dbName, AbstractTransactionManager transactionManager, boolean lock) {
        this.process = process;
        this.collection = Objects.requireNonNull(getCollection(mongoClient, process.id(), dbName));
        this.marshaller = ProcessInstanceMarshallerService.newBuilder()
                .withDefaultObjectMarshallerStrategies()
                .withDefaultListeners()
                .withContextEntries(singletonMap(MarshallerContextName.MARSHALLER_FORMAT, MarshallerContextName.MARSHALLER_FORMAT_JSON))
                .build();
        this.transactionManager = Objects.requireNonNull(transactionManager);
        this.lock = lock;
    }

    @Override
    public Optional<ProcessInstance<T>> findById(String id, ProcessInstanceReadMode mode) {
        return find(id).map(piDoc -> unmarshall(piDoc, mode));
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
        return instance;
    }

    @Override
    public void create(String id, ProcessInstance<T> instance) {
        updateStorage(id, instance, true);
    }

    @Override
    public void update(String id, ProcessInstance<T> instance) {
        if (isActive(instance)) {
            updateStorage(id, instance, false);
        }
        reloadProcessInstance(instance, id);
    }

    protected void updateStorage(String id, ProcessInstance<T> instance, boolean checkDuplicates) {
        ClientSession clientSession = transactionManager.getClientSession();
        Document doc = Document.parse(new String(marshaller.marshallProcessInstance(instance)));
        if (checkDuplicates) {
            createInternal(id, clientSession, doc);
        } else {
            updateInternal(id, instance, clientSession, doc);
        }
    }

    private void createInternal(String id, ClientSession clientSession, Document doc) {
        if (exists(id)) {
            throw new ProcessInstanceDuplicatedException(id);
        } else {
            doc.put(VERSION, 0L);
            if (clientSession != null) {
                collection.insertOne(clientSession, doc);
            } else {
                collection.insertOne(doc);
            }
        }
    }

    private void updateInternal(String id, ProcessInstance<T> instance, ClientSession clientSession, Document doc) {
        Bson filters = Filters.eq(PROCESS_INSTANCE_ID, id);
        UpdateResult result;
        if (lock) {
            doc.put(VERSION, instance.version() + 1);
            filters = Filters.and(Filters.eq(PROCESS_INSTANCE_ID, id), Filters.eq(VERSION, instance.version()));
        }
        if (clientSession != null) {
            result = collection.replaceOne(clientSession, filters, doc);
        } else {
            result = collection.replaceOne(filters, doc);
        }
        if (lock && result.getModifiedCount() != 1) {
            throw new ProcessInstanceOptimisticLockingException(id);
        }
    }

    private Optional<Document> find(String id) {
        ClientSession clientSession = transactionManager.getClientSession();
        return Optional.ofNullable((clientSession != null ? collection.find(clientSession, Filters.eq(PROCESS_INSTANCE_ID, id)) : collection.find(Filters.eq(PROCESS_INSTANCE_ID, id))).first());
    }

    @Override
    public boolean exists(String id) {
        return find(id).isPresent();
    }

    @Override
    public void remove(String id) {
        ClientSession clientSession = transactionManager.getClientSession();
        if (clientSession != null) {
            collection.deleteOne(clientSession, Filters.eq(PROCESS_INSTANCE_ID, id));
        } else {
            collection.deleteOne(Filters.eq(PROCESS_INSTANCE_ID, id));
        }
    }

    private void reloadProcessInstance(ProcessInstance<T> instance, String id) {
        ((AbstractProcessInstance<?>) instance).internalRemoveProcessInstance(marshaller.createdReloadFunction(() -> find(id).map(reloaded -> {
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
        return collection;
    }
}
