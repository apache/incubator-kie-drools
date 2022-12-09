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
package org.kie.kogito.persistence.postgresql;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.kie.kogito.process.MutableProcessInstances;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceOptimisticLockingException;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.impl.AbstractProcessInstance;
import org.kie.kogito.serialization.process.ProcessInstanceMarshallerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

import static org.kie.kogito.process.ProcessInstanceReadMode.MUTABLE;

@SuppressWarnings({ "rawtypes" })
public class PostgresqlProcessInstances implements MutableProcessInstances {

    private static final String VERSION = "version";

    private static final String PAYLOAD = "payload";

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresqlProcessInstances.class);
    private static final String IS_NULL = "is null";
    private static final String INSERT = "INSERT INTO process_instances (id, payload, process_id, process_version, version) VALUES ($1, $2, $3, $4, $5)";
    private static final String UPDATE = "UPDATE process_instances SET payload = $1 WHERE process_id = $2 and id = $3 and process_version ";
    private static final String DELETE = "DELETE FROM process_instances WHERE process_id = $1 and id = $2 and process_version ";
    private static final String FIND_BY_ID = "SELECT payload, version FROM process_instances WHERE process_id = $1 and id = $2 and process_version ";
    private static final String FIND_ALL = "SELECT payload FROM process_instances WHERE process_id = $1 and process_version ";
    private static final String COUNT = "SELECT COUNT(id) FROM process_instances WHERE process_id = $1 and process_version ";
    private static final String UPDATE_WITH_LOCK = "UPDATE process_instances SET payload = $1, version = $2 WHERE process_id = $3 and id = $4 and version = $5 and process_version ";

    private final Process<?> process;
    private final PgPool client;
    private final ProcessInstanceMarshallerService marshaller;
    private final Long queryTimeoutMillis;
    private final boolean lock;

    public PostgresqlProcessInstances(Process<?> process, PgPool client, Long queryTimeoutMillis, boolean lock) {
        this.process = process;
        this.client = client;
        this.queryTimeoutMillis = queryTimeoutMillis;
        this.marshaller = ProcessInstanceMarshallerService.newBuilder().withDefaultObjectMarshallerStrategies().build();
        this.lock = lock;
    }

    @Override
    public boolean exists(String id) {
        return findById(id).isPresent();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void create(String id, ProcessInstance instance) {
        if (!isActive(instance)) {
            disconnect(instance);
            return;
        }
        insertInternal(UUID.fromString(id), marshaller.marshallProcessInstance(instance));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void update(String id, ProcessInstance instance) {
        if (!isActive(instance)) {
            disconnect(instance);
            return;
        }
        try {
            if (lock) {
                updateWithLock(UUID.fromString(id), marshaller.marshallProcessInstance(instance), instance.version());
            } else {
                updateInternal(UUID.fromString(id), marshaller.marshallProcessInstance(instance));
            }
        } finally {
            disconnect(instance);
        }
    }

    @Override
    public void remove(String id) {
        deleteInternal(UUID.fromString(id));
    }

    @Override
    public Optional<ProcessInstance> findById(String id, ProcessInstanceReadMode mode) {
        Optional<Row> row = findByIdInternal(UUID.fromString(id));

        if (row.isPresent()) {
            Optional<byte[]> payload = row.map(r -> r.getBuffer(PAYLOAD)).map(Buffer::getBytes);
            if (payload.isPresent()) {
                ProcessInstance<?> instance = mode == MUTABLE ? marshaller.unmarshallProcessInstance(payload.get(), process) : marshaller.unmarshallReadOnlyProcessInstance(payload.get(), process);
                ((AbstractProcessInstance) instance).setVersion(row.get().getLong(VERSION));
                return Optional.of(instance);
            }
        }
        return Optional.empty();
    }

    @Override
    public Collection<ProcessInstance> values(ProcessInstanceReadMode mode) {
        return findAllInternal().stream().map(b -> mode == MUTABLE ? marshaller.unmarshallProcessInstance(b, process) : marshaller.unmarshallReadOnlyProcessInstance(b, process))
                .collect(Collectors.toList());
    }

    @Override
    public Integer size() {
        return countInternal().intValue();
    }

    @Override
    public boolean lock() {
        return this.lock;
    }

    private void disconnect(ProcessInstance instance) {
        Supplier<byte[]> supplier = () -> {
            Optional<Row> row = findByIdInternal(UUID.fromString(instance.id()));
            ((AbstractProcessInstance) instance).setVersion(row.get().getLong(VERSION));
            return row.map(r -> r.getBuffer(PAYLOAD)).map(Buffer::getBytes).get();
        };
        ((AbstractProcessInstance<?>) instance).internalRemoveProcessInstance(marshaller.createdReloadFunction(supplier));
    }

    private boolean insertInternal(UUID id, byte[] payload) {
        try {
            Future<RowSet<Row>> future = client.preparedQuery(INSERT)
                    .execute(Tuple.of(id, Buffer.buffer(payload), process.id(), process.version(), 0L));
            return getExecutedResult(future);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw uncheckedException(e, "Error inserting process instance %s", id);
        } catch (Exception e) {
            throw uncheckedException(e, "Error inserting process instance %s", id);
        }
    }

    private RuntimeException uncheckedException(Exception ex, String message, Object... param) {
        return new RuntimeException(String.format(message, param), ex);
    }

    private boolean updateInternal(UUID id, byte[] payload) {
        try {
            Future<RowSet<Row>> future =
                    client.preparedQuery(UPDATE + (process.version() == null ? IS_NULL : "= $4"))
                            .execute(tuple(Buffer.buffer(payload), process.id(), id));
            return getExecutedResult(future);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw uncheckedException(e, "Error updating process instance %s", id);
        } catch (Exception e) {
            throw uncheckedException(e, "Error updating process instance %s", id);
        }
    }

    private boolean deleteInternal(UUID id) {
        try {
            Future<RowSet<Row>> future = client.preparedQuery(DELETE + (process.version() == null ? IS_NULL : "= $3"))
                    .execute(tuple(process.id(), id));
            return getExecutedResult(future);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw uncheckedException(e, "Error deleting process instance %s", id);
        } catch (Exception e) {
            throw uncheckedException(e, "Error deleting process instance %s", id);
        }
    }

    private Boolean getExecutedResult(Future<RowSet<Row>> future) throws ExecutionException, TimeoutException, InterruptedException {
        try {
            return getResultFromFuture(future).map(RowSet::rowCount).map(count -> count == 1).orElse(false);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        }
    }

    private Optional<RowSet<Row>> getResultFromFuture(Future<RowSet<Row>> future) throws ExecutionException, TimeoutException, InterruptedException {
        try {
            return Optional.ofNullable(future.toCompletionStage().toCompletableFuture().get(queryTimeoutMillis, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        }
    }

    private Optional<Row> findByIdInternal(UUID id) {
        try {
            Future<RowSet<Row>> future =
                    client.preparedQuery(FIND_BY_ID + (process.version() == null ? IS_NULL : "= $3"))
                            .execute(tuple(process.id(), id));
            return getResultFromFuture(future).map(RowSet::iterator).filter(Iterator::hasNext).map(Iterator::next);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw uncheckedException(e, "Error finding process instance %s", id);
        } catch (Exception e) {
            throw uncheckedException(e, "Error finding process instance %s", id);
        }
    }

    private List<byte[]> findAllInternal() {
        try {
            Future<RowSet<Row>> future = client.preparedQuery(FIND_ALL + (process.version() == null ? IS_NULL : "= $2"))
                    .execute(tuple(process.id()));
            return getResultFromFuture(future).map(r -> StreamSupport.stream(r.spliterator(), false).map(row -> row.getBuffer(PAYLOAD)).map(Buffer::getBytes).collect(Collectors.toList()))
                    .orElseGet(Collections::emptyList);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw uncheckedException(e, "Error finding all process instances, for processId %s", process.id());
        } catch (Exception e) {
            throw uncheckedException(e, "Error finding all process instances, for processId %s", process.id());
        }
    }

    private Tuple tuple(Object... parameters) {
        Tuple tuple = Tuple.from(parameters);
        if (process.version() != null) {
            tuple.addValue(process.version());
        }
        return tuple;
    }

    private Long countInternal() {
        try {
            Future<RowSet<Row>> future = client.preparedQuery(COUNT + (process.version() == null ? IS_NULL : "= $2"))
                    .execute(tuple(process.id()));
            return getResultFromFuture(future).map(RowSet::iterator).map(RowIterator::next).map(row -> row.getLong("count")).orElse(0l);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw uncheckedException(e, "Error counting process instances, for processId %s", process.id());
        } catch (Exception e) {
            throw uncheckedException(e, "Error counting process instances, for processId %s", process.id());
        }
    }

    private String getQueryFromFile(String scriptName) {
        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(String.format("sql/%s.sql", scriptName))) {
            byte[] buffer = stream.readAllBytes();
            return new String(buffer);
        } catch (Exception e) {
            throw uncheckedException(e, "Error reading query script file %s", scriptName);
        }
    }

    private boolean updateWithLock(UUID id, byte[] payload, long version) {
        try {
            Future<RowSet<Row>> future = client.preparedQuery(UPDATE_WITH_LOCK + (process.version() == null ? IS_NULL : "= $6"))
                    .execute(tuple(Buffer.buffer(payload), version + 1, process.id(), id, version));
            boolean result = getExecutedResult(future);
            if (!result) {
                throw new ProcessInstanceOptimisticLockingException(id.toString());
            }
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ProcessInstanceOptimisticLockingException e) {
            throw e;
        } catch (Exception e) {
            throw uncheckedException(e, "Error updating process instance %s", id);
        }
        return false;
    }
}
