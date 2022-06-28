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
package org.kie.kogito.persistence.jdbc;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

import static org.kie.kogito.process.ProcessInstanceReadMode.MUTABLE;

public class JDBCProcessInstances implements MutableProcessInstances {

    static final String PAYLOAD = "payload";
    static final String VERSION = "version";

    private static final Logger LOGGER = LoggerFactory.getLogger(JDBCProcessInstances.class);

    private final Process<?> process;
    private final ProcessInstanceMarshallerService marshaller;
    private final boolean lock;
    private final Repository repository;

    public JDBCProcessInstances(Process<?> process, DataSource dataSource, boolean autoDDL, boolean lock) {
        this.process = process;
        this.lock = lock;
        this.marshaller = ProcessInstanceMarshallerService.newBuilder().withDefaultObjectMarshallerStrategies().build();
        this.repository = new GenericRepository(dataSource);
        DDLRunner.init(repository, autoDDL);
    }

    @Override
    public boolean exists(String id) {
        return findById(id).isPresent();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void create(String id, ProcessInstance instance) {
        LOGGER.debug("Creating process instance id: {}, processId: {}", id, instance.process().id());
        if (isActive(instance)) {
            repository.insertInternal(process.id(), UUID.fromString(id), marshaller.marshallProcessInstance(instance));
        } else {
            LOGGER.warn("Skipping create of process instance id: {}, state: {}", id, instance.status());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void update(String id, ProcessInstance instance) {
        LOGGER.debug("Updating process instance id: {}, processId: {}", id, instance.process().id());
        try {
            if (isActive(instance)) {
                if (lock) {
                    boolean isUpdated = repository.updateWithLock(process.id(), UUID.fromString(id), marshaller.marshallProcessInstance(instance), instance.version());
                    if (!isUpdated) {
                        throw new ProcessInstanceOptimisticLockingException(id);
                    }
                } else {
                    repository.updateInternal(process.id(), UUID.fromString(id), marshaller.marshallProcessInstance(instance));
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
        boolean isDeleted = repository.deleteInternal(process.id(), UUID.fromString(id));
        LOGGER.debug("Deleted: {}", isDeleted);
    }

    @Override
    public Optional<ProcessInstance> findById(String id, ProcessInstanceReadMode mode) {
        LOGGER.debug("Find process instance id: {}, mode: {}", id, mode);
        Map<String, Object> map = repository.findByIdInternal(process.id(), UUID.fromString(id));
        if (map.containsKey(PAYLOAD)) {
            byte[] b = (byte[]) map.get(PAYLOAD);
            ProcessInstance<?> instance = mode == MUTABLE ? marshaller.unmarshallProcessInstance(b, process)
                    : marshaller.unmarshallReadOnlyProcessInstance(b, process);
            ((AbstractProcessInstance<?>) instance).setVersion((Long) map.get(VERSION));
            return Optional.of(instance);
        }
        return Optional.empty();
    }

    @Override
    public Collection<ProcessInstance> values(ProcessInstanceReadMode mode) {
        LOGGER.debug("Find process instance values using mode: {}", mode);
        return repository.findAllInternal(process.id()).stream()
                .map(b -> mode == MUTABLE ? marshaller.unmarshallProcessInstance(b, process) : marshaller.unmarshallReadOnlyProcessInstance(b, process))
                .collect(Collectors.toList());
    }

    @Override
    public Integer size() {
        return repository.countInternal(process.id()).intValue();
    }

    @Override
    public boolean lock() {
        return this.lock;
    }

    private void disconnect(ProcessInstance instance) {
        Supplier<byte[]> supplier = () -> {
            Map<String, Object> map = repository.findByIdInternal(process.id(), UUID.fromString(instance.id()));
            ((AbstractProcessInstance<?>) instance).setVersion((Long) map.get(VERSION));
            return (byte[]) map.get(PAYLOAD);
        };
        ((AbstractProcessInstance<?>) instance).internalRemoveProcessInstance(marshaller.createdReloadFunction(supplier));
    }
}
