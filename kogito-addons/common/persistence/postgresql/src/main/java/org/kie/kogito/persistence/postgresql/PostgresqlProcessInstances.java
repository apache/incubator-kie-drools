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
package org.kie.kogito.persistence.postgresql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.jbpm.flow.serialization.MarshallerContextName;
import org.jbpm.flow.serialization.ProcessInstanceMarshallerService;
import org.kie.kogito.Model;
import org.kie.kogito.internal.process.runtime.HeadersPersistentConfig;
import org.kie.kogito.process.MutableProcessInstances;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceOptimisticLockingException;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.impl.AbstractProcessInstance;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PreparedQuery;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

public class PostgresqlProcessInstances<T extends Model> implements MutableProcessInstances<T> {

    private static final String VERSION = "version";
    private static final String PAYLOAD = "payload";

    private static final String IS_NULL = "is null";
    private static final String INSERT = "INSERT INTO process_instances (id, payload, process_id, process_version, version) VALUES ($1, $2, $3, $4, $5)";
    private static final String UPDATE = "UPDATE process_instances SET payload = $1 WHERE process_id = $2 and id = $3 and process_version ";
    private static final String DELETE = "DELETE FROM process_instances WHERE process_id = $1 and id = $2 and process_version ";
    private static final String FIND_BY_ID = "SELECT payload, version FROM process_instances WHERE process_id = $1 and id = $2 and process_version ";
    private static final String FIND_ALL = "SELECT payload, version FROM process_instances WHERE process_id = $1 and process_version ";
    private static final String UPDATE_WITH_LOCK = "UPDATE process_instances SET payload = $1, version = $2 WHERE process_id = $3 and id = $4 and version = $5 and process_version ";
    private static final String MIGRATE_BULK = "UPDATE process_instances SET process_id = $1, process_version = $2 WHERE process_id = $3 and process_version ";
    private static final String MIGRATE_INSTANCE = "UPDATE process_instances SET process_id = $1, process_version = $2 WHERE process_id = $3 and id = ANY ($4) and process_version ";
    static final String FIND_ALL_WAITING_FOR_EVENT_TYPE =
            "SELECT payload, version FROM event_types, process_instances WHERE process_instances.id = event_types.process_instance_id AND event_type = $1 AND process_id = $2 AND process_version ";
    static final String DELETE_ALL_WAITING_FOR_EVENT_TYPE = "DELETE FROM event_types WHERE process_instance_id = $1";
    static final String INSERT_WAITING_FOR_EVENT_TYPE = "INSERT INTO event_types (process_instance_id, event_type) VALUES($1,$2)";

    private final Process<?> process;
    private final PgPool client;
    private final ProcessInstanceMarshallerService marshaller;
    private final Long queryTimeoutMillis;
    private final boolean lock;

    public PostgresqlProcessInstances(Process<?> process, PgPool client, Long queryTimeoutMillis, boolean lock, HeadersPersistentConfig headersConfig) {
        this.process = process;
        this.client = client;
        this.queryTimeoutMillis = queryTimeoutMillis;
        this.marshaller = ProcessInstanceMarshallerService.newBuilder().withDefaultObjectMarshallerStrategies().withDefaultListeners()
                .withContextEntry(MarshallerContextName.MARSHALLER_HEADERS_CONFIG, headersConfig).build();
        this.lock = lock;
    }

    @Override
    public boolean exists(String id) {
        return findById(id).isPresent();
    }

    @Override
    public void create(String id, ProcessInstance<T> instance) {
        if (!isActive(instance) && instance.status() != ProcessInstance.STATE_PENDING) {
            return;
        }
        String[] eventTypes = getUniqueEvents(instance);
        insertInternal(id, marshaller.marshallProcessInstance(instance), eventTypes);
        connectProcessInstance(instance);
    }

    @Override
    public void update(String id, ProcessInstance<T> instance) {
        if (!isActive(instance) && instance.status() != ProcessInstance.STATE_PENDING) {
            return;
        }

        String[] eventTypes = getUniqueEvents(instance);
        if (lock) {
            updateWithLock(id, marshaller.marshallProcessInstance(instance), instance.version(), eventTypes);
            ((AbstractProcessInstance<T>) instance).setVersion(instance.version() + 1);
        } else {
            updateInternal(id, marshaller.marshallProcessInstance(instance), eventTypes);
        }

        connectProcessInstance(instance);

    }

    private String[] getUniqueEvents(ProcessInstance<T> instance) {
        return Stream.of(((AbstractProcessInstance<T>) instance).internalGetProcessInstance().getEventTypes()).collect(Collectors.toCollection(HashSet::new)).toArray(String[]::new);
    }

    @Override
    public void remove(String id) {
        deleteInternal(id);
    }

    @Override
    public Optional<ProcessInstance<T>> findById(String id, ProcessInstanceReadMode mode) {
        return findByIdInternal(id).map(r -> {
            return (AbstractProcessInstance<T>) unmarshall(r, mode);
        });
    }

    @Override
    public Stream<ProcessInstance<T>> stream(ProcessInstanceReadMode mode) {
        try {
            return getResultFromFuture(client.preparedQuery(FIND_ALL + (process.version() == null ? IS_NULL : "= $2")).execute(tuple(process.id())))
                    .map(r -> StreamSupport.stream(r.spliterator(), false)).orElse(Stream.empty())
                    .map(row -> unmarshall(row, mode));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw uncheckedException(e, "Error finding all process instances, for processId %s", process.id());
        } catch (ExecutionException | TimeoutException e) {
            throw uncheckedException(e, "Error finding all process instances, for processId %s", process.id());
        }
    }

    @Override
    public Stream<ProcessInstance<T>> waitingForEventType(String eventType, ProcessInstanceReadMode mode) {
        try {
            Tuple parameters = tuple(eventType, process.id());
            return getResultFromFuture(client.preparedQuery(FIND_ALL_WAITING_FOR_EVENT_TYPE + (process.version() == null ? IS_NULL : "= $3")).execute(parameters))
                    .map(r -> StreamSupport.stream(r.spliterator(), false)).orElse(Stream.empty())
                    .map(row -> unmarshall(row, mode));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw uncheckedException(e, "Error finding all process instances, for processId %s", process.id());
        } catch (ExecutionException | TimeoutException e) {
            throw uncheckedException(e, "Error finding all process instances, for processId %s", process.id());
        }

    }

    private ProcessInstance<T> unmarshall(Row r, ProcessInstanceReadMode mode) {
        AbstractProcessInstance<T> instance = (AbstractProcessInstance<T>) marshaller.unmarshallProcessInstance(r.getBuffer(PAYLOAD).getBytes(), process, mode);
        instance.setVersion(r.getLong(VERSION));
        connectProcessInstance(instance);
        return instance;
    }

    @Override
    public boolean lock() {
        return this.lock;
    }

    private void connectProcessInstance(ProcessInstance<T> instance) {
        ((AbstractProcessInstance<T>) instance).internalSetReloadSupplier(marshaller.createdReloadFunction(() -> findByIdInternal(instance.id()).map(r -> {
            ((AbstractProcessInstance<T>) instance).setVersion(r.getLong(VERSION));
            return r.getBuffer(PAYLOAD).getBytes();
        }).orElseThrow()));
    }

    private boolean insertInternal(String id, byte[] payload, String[] eventTypes) {
        try {
            Tuple tuple = Tuple.of(id, Buffer.buffer(payload), process.id(), process.version(), 0L);
            Future<RowSet<Row>> future = client.preparedQuery(INSERT).execute(tuple);
            boolean inserted = getExecutedResult(future);

            List<Tuple> tupleEvents = new ArrayList<>();
            for (String eventType : eventTypes) {
                tupleEvents.add(Tuple.of(id, eventType));
            }

            if (!tupleEvents.isEmpty()) {
                executeFuture(client.preparedQuery(INSERT_WAITING_FOR_EVENT_TYPE).executeBatch(tupleEvents));
            }
            return inserted;
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

    @Override
    public long migrateAll(String targetProcessId, String targetProcessVersion) {
        try {
            PreparedQuery<RowSet<Row>> rows = null;
            if (process.version() == null) {
                rows = client.preparedQuery(MIGRATE_BULK + IS_NULL);
            } else {
                rows = client.preparedQuery(MIGRATE_BULK + "= $4");
            }
            Future<RowSet<Row>> future = rows.execute(tuple(targetProcessId, targetProcessVersion, process.id()));
            return getResultFromFuture(future).map(RowSet::rowCount).orElse(0);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw uncheckedException(e, "Error migration process instance %s %s", process.id(), process.version());
        } catch (Exception e) {
            throw uncheckedException(e, "Error deleting process instance %s %s", process.id(), process.version());
        }
    }

    @Override
    public void migrateProcessInstances(String targetProcessId, String targetProcessVersion, String... processIds) {
        try {
            PreparedQuery<RowSet<Row>> rows = null;
            if (process.version() == null) {
                rows = client.preparedQuery(MIGRATE_INSTANCE + IS_NULL);
            } else {
                rows = client.preparedQuery(MIGRATE_INSTANCE + "= $5");
            }
            Future<RowSet<Row>> future = rows.execute(tuple(targetProcessId, targetProcessVersion, process.id(), processIds));
            getExecutedResult(future);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw uncheckedException(e, "Error deleting process instance %s", Arrays.toString(processIds));
        } catch (Exception e) {
            throw uncheckedException(e, "Error deleting process instance %s", Arrays.toString(processIds));
        }
    }

    private boolean updateInternal(String id, byte[] payload, String[] eventTypes) {
        try {
            Future<RowSet<Row>> future =
                    client.preparedQuery(UPDATE + (process.version() == null ? IS_NULL : "= $4"))
                            .execute(tuple(Buffer.buffer(payload), process.id(), id));

            boolean result = getExecutedResult(future);
            executeFuture(client.preparedQuery(DELETE_ALL_WAITING_FOR_EVENT_TYPE).execute(Tuple.of(id)));
            List<Tuple> tupleEvents = new ArrayList<>();
            for (String eventType : eventTypes) {
                tupleEvents.add(Tuple.of(id, eventType));
            }
            if (!tupleEvents.isEmpty()) {
                executeFuture(client.preparedQuery(INSERT_WAITING_FOR_EVENT_TYPE).executeBatch(tupleEvents));
            }
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw uncheckedException(e, "Error updating process instance %s", id);
        } catch (Exception e) {
            throw uncheckedException(e, "Error updating process instance %s", id);
        }
    }

    private boolean deleteInternal(String id) {
        try {
            Future<RowSet<Row>> future = client.preparedQuery(DELETE + (process.version() == null ? IS_NULL : "= $3"))
                    .execute(tuple(process.id(), id));
            boolean result = getExecutedResult(future);
            executeFuture(client.preparedQuery(DELETE_ALL_WAITING_FOR_EVENT_TYPE).execute(Tuple.of(id)));
            return result;
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
            return Optional.ofNullable(executeFuture(future));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        }
    }

    private <R> R executeFuture(Future<R> future) throws InterruptedException, ExecutionException, TimeoutException {
        return future.toCompletionStage().toCompletableFuture().get(queryTimeoutMillis, TimeUnit.MILLISECONDS);
    }

    private Optional<Row> findByIdInternal(String id) {
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

    private Tuple tuple(Object... parameters) {
        Tuple tuple = Tuple.from(parameters);
        if (process.version() != null) {
            tuple.addValue(process.version());
        }
        return tuple;
    }

    private boolean updateWithLock(String id, byte[] payload, long version, String[] eventTypes) {
        try {
            Future<RowSet<Row>> future = client.preparedQuery(UPDATE_WITH_LOCK + (process.version() == null ? IS_NULL : "= $6"))
                    .execute(tuple(Buffer.buffer(payload), version + 1, process.id(), id, version));
            if (!getExecutedResult(future)) {
                throw new ProcessInstanceOptimisticLockingException(id);
            }
            executeFuture(client.preparedQuery(DELETE_ALL_WAITING_FOR_EVENT_TYPE).execute(Tuple.of(id)));
            List<Tuple> tupleEvents = new ArrayList<>();
            for (String eventType : eventTypes) {
                tupleEvents.add(Tuple.of(id, eventType));
            }
            if (!tupleEvents.isEmpty()) {
                executeFuture(client.preparedQuery(INSERT_WAITING_FOR_EVENT_TYPE).executeBatch(tupleEvents));
            }
            return true;
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
