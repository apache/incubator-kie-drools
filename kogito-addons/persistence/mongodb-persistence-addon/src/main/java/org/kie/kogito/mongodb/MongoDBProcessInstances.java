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
package org.kie.kogito.mongodb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.bson.Document;
import org.kie.kogito.Model;
import org.kie.kogito.process.MutableProcessInstances;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceDuplicatedException;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.impl.AbstractProcessInstance;
import org.kie.kogito.serialization.process.MarshallerContextName;
import org.kie.kogito.serialization.process.ProcessInstanceMarshallerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import static java.util.Collections.singletonMap;
import static org.kie.kogito.mongodb.utils.DocumentConstants.PROCESS_INSTANCE_ID;
import static org.kie.kogito.mongodb.utils.DocumentUtils.getCollection;
import static org.kie.kogito.process.ProcessInstanceReadMode.MUTABLE;

public class MongoDBProcessInstances<T extends Model> implements MutableProcessInstances<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBProcessInstances.class);
    private org.kie.kogito.process.Process<?> process;
    private ProcessInstanceMarshallerService marshaller;
    private final MongoCollection<Document> collection;

    public MongoDBProcessInstances(MongoClient mongoClient, org.kie.kogito.process.Process<?> process, String dbName) {
        this.process = process;
        collection = getCollection(mongoClient, process.id(), dbName);
        marshaller = ProcessInstanceMarshallerService.newBuilder().withDefaultObjectMarshallerStrategies().withContextEntries(singletonMap(MarshallerContextName.MARSHALLER_FORMAT, "json")).build();
    }

    @Override
    public Optional<ProcessInstance<T>> findById(String id, ProcessInstanceReadMode mode) {
        Document piDoc = find(id);
        if (piDoc == null) {
            return Optional.empty();
        }

        return Optional.of(unmarshall(piDoc, mode));
    }

    @Override
    public Collection<ProcessInstance<T>> values(ProcessInstanceReadMode mode) {
        List<ProcessInstance<T>> list = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                list.add(unmarshall(cursor.next(), mode));
            }
        }
        return list;
    }

    private ProcessInstance<T> unmarshall(Document document, ProcessInstanceReadMode mode) {
        byte[] content = document.toJson().getBytes();
        return mode == MUTABLE ? (ProcessInstance<T>) marshaller.unmarshallProcessInstance(content, process) : (ProcessInstance<T>) marshaller.unmarshallReadOnlyProcessInstance(content, process);
    }

    @Override
    public void create(String id, ProcessInstance<T> instance) {
        updateStorage(id, instance, true);
    }

    @Override
    public void update(String id, ProcessInstance<T> instance) {
        updateStorage(id, instance, false);
    }

    protected void updateStorage(String id, ProcessInstance<T> instance, boolean checkDuplicates) {
        if (isActive(instance)) {
            Document doc = Document.parse(new String(marshaller.marshallProcessInstance(instance)));
            if (checkDuplicates) {
                if (exists(id)) {
                    throw new ProcessInstanceDuplicatedException(id);
                } else {
                    collection.insertOne(doc);
                }
            } else {
                collection.replaceOne(Filters.eq(PROCESS_INSTANCE_ID, id), doc);
            }
        }
        reloadProcessInstance(instance, id);
    }

    private Document find(String id) {
        return collection.find(Filters.eq(PROCESS_INSTANCE_ID, id)).first();
    }

    @Override
    public boolean exists(String id) {
        return find(id) != null;
    }

    @Override
    public void remove(String id) {
        collection.deleteOne(Filters.eq(PROCESS_INSTANCE_ID, id));
    }

    private void reloadProcessInstance(ProcessInstance<T> instance, String id) {
        Supplier<byte[]> supplier = () -> {
            Document reloaded = find(id);
            if (reloaded != null) {
                return reloaded.toJson().getBytes();
            }
            throw new IllegalArgumentException("process instance id " + id + " does not exists in mongodb");
        };
        ((AbstractProcessInstance<?>) instance).internalRemoveProcessInstance(marshaller.createdReloadFunction(supplier));

    }

    @Override
    public Integer size() {
        return (int) collection.countDocuments();
    }
}
