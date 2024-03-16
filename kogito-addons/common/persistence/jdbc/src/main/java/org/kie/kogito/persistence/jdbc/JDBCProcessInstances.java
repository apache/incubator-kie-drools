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
package org.kie.kogito.persistence.jdbc;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.kie.kogito.process.MutableProcessInstances;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceOptimisticLockingException;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.impl.AbstractProcessInstance;
import org.kie.kogito.serialization.process.ProcessInstanceMarshallerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDBCProcessInstances implements MutableProcessInstances {

    private static final Logger LOGGER = LoggerFactory.getLogger(JDBCProcessInstances.class);

    private final Process<?> process;
    private final ProcessInstanceMarshallerService marshaller;
    private final boolean lock;
    private final Repository repository;

    public JDBCProcessInstances(Process<?> process, DataSource dataSource, boolean lock) {
        this.process = process;
        this.lock = lock;
        this.marshaller = ProcessInstanceMarshallerService.newBuilder().withDefaultObjectMarshallerStrategies().build();
        this.repository = new GenericRepository(dataSource);
    }

    @Override
    public boolean exists(String id) {
        return findById(id).isPresent();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void create(String id, ProcessInstance instance) {
        LOGGER.debug("Creating process instance id: {}, processId: {}, processVersion: {}", id, process.id(), process.version());
        if (isActive(instance)) {
            repository.insertInternal(process.id(), process.version(), UUID.fromString(id), marshaller.marshallProcessInstance(instance), instance.businessKey());
        } else {
            LOGGER.warn("Skipping create of process instance id: {}, state: {}", id, instance.status());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void update(String id, ProcessInstance instance) {
        LOGGER.debug("Updating process instance id: {}, processId: {}, processVersion: {}", id, process.id(), process.version());
        try {
            if (isActive(instance)) {
                if (lock) {
                    boolean isUpdated = repository.updateWithLock(process.id(), process.version(), UUID.fromString(id), marshaller.marshallProcessInstance(instance), instance.version());
                    if (!isUpdated) {
                        throw new ProcessInstanceOptimisticLockingException(id);
                    }
                } else {
                    repository.updateInternal(process.id(), process.version(), UUID.fromString(id), marshaller.marshallProcessInstance(instance));
                }
            } else {
                LOGGER.warn("Process instance id: {}, state: {} is not active, skipping update", id, instance.status());
            }
        } finally {
            disconnect(instance);
        }
    }

    @Override
    public void remove(String id) {
        LOGGER.debug("Removing process instance id: {}, processId: {}", id, process.id());
        boolean isDeleted = repository.deleteInternal(process.id(), process.version(), UUID.fromString(id));
        LOGGER.debug("Deleted: {}", isDeleted);
    }

    @Override
    public Optional<ProcessInstance<?>> findById(String id, ProcessInstanceReadMode mode) {
        LOGGER.debug("Find process instance id: {}, mode: {}", id, mode);
        return repository.findByIdInternal(process.id(), process.version(), UUID.fromString(id)).map(r -> unmarshall(r, mode));
    }

    @Override
    public Optional<ProcessInstance<?>> findByBusinessKey(String businessKey, ProcessInstanceReadMode mode) {
        LOGGER.debug("Find process instance using business Key : {}", businessKey);
        return repository.findByBusinessKey(process.id(), process.version(), businessKey).map(r -> unmarshall(r, mode));
    }

    @Override
    public Stream<ProcessInstance<?>> stream(ProcessInstanceReadMode mode) {
        LOGGER.debug("Find process instance values using mode: {}", mode);
        return repository.findAllInternal(process.id(), process.version())
                .map(r -> unmarshall(r, mode));
    }

    private ProcessInstance<?> unmarshall(Repository.Record record, ProcessInstanceReadMode mode) {
        ProcessInstance<?> instance = marshaller.unmarshallProcessInstance(record.getPayload(), process, mode);
        ((AbstractProcessInstance<?>) instance).setVersion(record.getVersion());
        return instance;
    }

    @Override
    public boolean lock() {
        return this.lock;
    }

    private void disconnect(ProcessInstance<?> instance) {
        ((AbstractProcessInstance<?>) instance).internalRemoveProcessInstance(marshaller.createdReloadFunction(() -> {
            Repository.Record r = repository.findByIdInternal(process.id(), process.version(), UUID.fromString(instance.id())).orElseThrow();
            ((AbstractProcessInstance<?>) instance).setVersion(r.getVersion());
            return r.getPayload();
        }));
    }
}
