/*
* Copyright 2020 Red Hat, Inc. and/or its affiliates.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
*
*      http://www.apache.org/licenses/LICENSE-2.0
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

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.kie.kogito.Model;
import org.kie.kogito.mongodb.marshalling.DocumentMarshallingStrategy;
import org.kie.kogito.mongodb.marshalling.DocumentProcessInstanceMarshaller;
import org.kie.kogito.mongodb.model.ProcessInstanceDocument;
import org.kie.kogito.process.MutableProcessInstances;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceDuplicatedException;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.impl.AbstractProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.kogito.mongodb.utils.DocumentConstants.DOCUMENT_ID;
import static org.kie.kogito.mongodb.utils.DocumentUtils.getCollection;
import static org.kie.kogito.process.ProcessInstanceReadMode.MUTABLE;

public class MongoDBProcessInstances<T extends Model> implements MutableProcessInstances<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBProcessInstances.class);
    private org.kie.kogito.process.Process<?> process;
    private DocumentProcessInstanceMarshaller marshaller;
    private final MongoCollection<ProcessInstanceDocument> collection;

    public MongoDBProcessInstances(MongoClient mongoClient, org.kie.kogito.process.Process<?> process, String dbName) {
        this.process = process;
        collection = getCollection(mongoClient, process.id(), dbName);
        marshaller = new DocumentProcessInstanceMarshaller(new DocumentMarshallingStrategy());
    }

    @Override
    public Optional<ProcessInstance<T>> findById(String id, ProcessInstanceReadMode mode) {
        ProcessInstanceDocument piDoc = find(id);
        if (piDoc == null) {
            return Optional.empty();
        }
        return Optional.of(mode == MUTABLE ? marshaller.unmarshallProcessInstance(piDoc, process) : marshaller.unmarshallReadOnlyProcessInstance(piDoc, process));
    }

    @Override
    public Collection<ProcessInstance<T>> values(ProcessInstanceReadMode mode) {
        List<ProcessInstance<T>> list = new ArrayList<>();
        try (MongoCursor<ProcessInstanceDocument> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                list.add(mode == MUTABLE ? marshaller.unmarshallProcessInstance(cursor.next(), process) : marshaller.unmarshallReadOnlyProcessInstance(cursor.next(), process));
            }
        }
        return list;
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
            ProcessInstanceDocument doc = marshaller.marshalProcessInstance(instance);
            if (checkDuplicates) {
                if (exists(id)) {
                    throw new ProcessInstanceDuplicatedException(id);
                } else {
                    collection.insertOne(doc);
                }
            } else {
                collection.replaceOne(Filters.eq(DOCUMENT_ID, id), doc);
            }
        }
        reloadProcessInstance(instance, id);
    }

    private ProcessInstanceDocument find(String id) {
        return collection.find(Filters.eq(DOCUMENT_ID, id)).first();
    }

    @Override
    public boolean exists(String id) {
        return find(id) != null;
    }

    @Override
    public void remove(String id) {
        collection.deleteOne(Filters.eq(DOCUMENT_ID, id));
    }

    private void reloadProcessInstance(ProcessInstance<T> instance, String id) {
        ((AbstractProcessInstance<?>) instance).internalRemoveProcessInstance(() -> {
            try {
                ProcessInstanceDocument reloaded = find(id);
                if (reloaded != null) {
                    return marshaller.unmarshallWorkflowProcessInstance(reloaded, process);
                }
            } catch (RuntimeException e) {
                LOGGER.error("Unexpected exception thrown when reloading process instance {}", instance.id(), e);
            }
            return null;
        });
    }

    @Override
    public Integer size() {
        return (int) collection.countDocuments();
    }
}
