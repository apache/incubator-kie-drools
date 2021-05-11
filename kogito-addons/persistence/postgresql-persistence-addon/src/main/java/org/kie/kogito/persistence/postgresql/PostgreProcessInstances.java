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
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.kie.kogito.process.MutableProcessInstances;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.impl.AbstractProcessInstance;
import org.kie.kogito.serialization.process.ProcessInstanceMarshallerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

@SuppressWarnings({ "rawtypes" })
public class PostgreProcessInstances implements MutableProcessInstances {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgreProcessInstances.class);

    private final Process<?> process;
    private final PgPool client;
    private final ProcessInstanceMarshallerService marshaller;
    private final boolean autoDDL;
    private final Long queryTimeoutMillis;

    public PostgreProcessInstances(Process<?> process, PgPool client, boolean autoDDL, Long queryTimeoutMillis) {
        this.process = process;
        this.client = client;
        this.autoDDL = autoDDL;
        this.queryTimeoutMillis = queryTimeoutMillis;
        this.marshaller = ProcessInstanceMarshallerService.newBuilder().withDefaultObjectMarshallerStrategies().build();
        init();
    }

    @Override
    public boolean exists(String id) {
        return findById(id).isPresent();
    }

    @Override
    public void create(String id, ProcessInstance instance) {
        insertInternal(UUID.fromString(id), marshaller.marshallProcessInstance(instance));
        disconnect(instance);
    }

    @Override
    public void update(String id, ProcessInstance instance) {
        updateInternal(UUID.fromString(id), marshaller.marshallProcessInstance(instance));
        disconnect(instance);
    }

    @Override
    public void remove(String id) {
        deleteInternal(UUID.fromString(id));
    }

    @Override
    public Optional<ProcessInstance> findById(String id, ProcessInstanceReadMode mode) {
        return findByIdInternal(UUID.fromString(id)).map(b -> marshaller.unmarshallProcessInstance(b, process));
    }

    @Override
    public Collection<ProcessInstance> values(ProcessInstanceReadMode mode) {
        return findAllInternal().stream().map(i -> marshaller.unmarshallProcessInstance(i, process)).collect(Collectors.toList());
    }

    @Override
    public Integer size() {
        return countInternal().intValue();
    }

    private void disconnect(ProcessInstance instance) {
        Supplier<byte[]> supplier = () -> findByIdInternal(UUID.fromString(instance.id())).get();
        ((AbstractProcessInstance<?>) instance).internalRemoveProcessInstance(marshaller.createdReloadFunction(supplier));
    }

    private boolean insertInternal(UUID id, byte[] payload) {
        try {
            final CompletableFuture<RowSet<Row>> future = new CompletableFuture<>();
            client.preparedQuery("INSERT INTO process_instances (id, payload, process_id) VALUES ($1, $2, $3)")
                    .execute(Tuple.of(id, Buffer.buffer(payload), process.id()), getAsyncResultHandler(future));
            return getExecutedResult(future);
        } catch (Exception e) {
            throw uncheckedException(e, "Error inserting process instance %s", id);
        }
    }

    private RuntimeException uncheckedException(Exception ex, String message, Object... param) {
        return new RuntimeException(String.format(message, param), ex);
    }

    private Handler<AsyncResult<RowSet<Row>>> getAsyncResultHandler(CompletableFuture<RowSet<Row>> future) {
        return ar -> {
            if (ar.succeeded()) {
                future.complete(ar.result());
            } else {
                future.completeExceptionally(ar.cause());
            }
        };
    }

    private boolean updateInternal(UUID id, byte[] payload) {
        try {
            final CompletableFuture<RowSet<Row>> future = new CompletableFuture<>();
            client.preparedQuery("UPDATE process_instances SET payload = $1 WHERE id = $2)")
                    .execute(Tuple.of(Buffer.buffer(payload), id), getAsyncResultHandler(future));
            return getExecutedResult(future);
        } catch (Exception e) {
            throw uncheckedException(e, "Error updating process instance %s", id);
        }
    }

    private boolean deleteInternal(UUID id) {
        try {
            final CompletableFuture<RowSet<Row>> future = new CompletableFuture<>();
            client.preparedQuery("DELETE FROM process_instances WHERE id = $1")
                    .execute(Tuple.of(id), getAsyncResultHandler(future));
            return getExecutedResult(future);
        } catch (Exception e) {
            throw uncheckedException(e, "Error deleting process instance %s", id);
        }
    }

    private Boolean getExecutedResult(CompletableFuture<RowSet<Row>> future) throws ExecutionException,
            TimeoutException, InterruptedException {
        return getResultFromFuture(future)
                .map(RowSet::rowCount)
                .map(count -> Objects.equals(count, 1))
                .orElse(false);
    }

    private Optional<RowSet<Row>> getResultFromFuture(CompletableFuture<RowSet<Row>> future) throws ExecutionException,
            TimeoutException, InterruptedException {
        try {
            return Optional.ofNullable(future.get(queryTimeoutMillis, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        }
    }

    private Optional<byte[]> findByIdInternal(UUID id) {
        try {
            final CompletableFuture<RowSet<Row>> future = new CompletableFuture<>();
            client.preparedQuery("SELECT payload FROM process_instances WHERE id = $1")
                    .execute(Tuple.of(id), getAsyncResultHandler(future));
            return getResultFromFuture(future)
                    .map(RowSet::iterator)
                    .filter(Iterator::hasNext)
                    .map(Iterator::next)
                    .map(row -> row.getBuffer("payload"))
                    .map(Buffer::getBytes);
        } catch (Exception e) {
            throw uncheckedException(e, "Error finding process instance %s", id);
        }
    }

    private List<byte[]> findAllInternal() {
        try {
            final CompletableFuture<RowSet<Row>> future = new CompletableFuture<>();
            client.preparedQuery("SELECT payload FROM process_instances WHERE process_id = $1")
                    .execute(Tuple.of(process.id()), getAsyncResultHandler(future));
            return getResultFromFuture(future)
                    .map(r -> StreamSupport.stream(r.spliterator(), false)
                            .map(row -> row.getBuffer("payload"))
                            .map(Buffer::getBytes)
                            .collect(Collectors.toList()))
                    .orElseGet(Collections::emptyList);
        } catch (Exception e) {
            throw uncheckedException(e, "Error finding all process instances, for processId %s", process.id());
        }
    }

    private Long countInternal() {
        try {
            final CompletableFuture<RowSet<Row>> future = new CompletableFuture<>();
            client.preparedQuery("SELECT COUNT(id) FROM process_instances WHERE process_id = $1")
                    .execute(Tuple.of(process.id()), getAsyncResultHandler(future));
            return getResultFromFuture(future)
                    .map(RowSet::iterator)
                    .map(RowIterator::next)
                    .map(row -> row.getLong("count"))
                    .orElse(0l);
        } catch (Exception e) {
            throw uncheckedException(e, "Error counting process instances, for processId %s", process.id());
        }
    }

    /**
     * Try to create the table using the same application user, this should not be necessary since the database
     * is recommended to be configured properly before starting the application.
     *
     * Note:
     * This method could be useful for development and testing purposes and does not break the execution flow,
     * throwing any exception.
     * This is only executed in case the configuration for auto DDL is enabled.
     */
    private void init() {
        if (!autoDDL) {
            LOGGER.debug("Auto DDL is disabled, do not running initializer scripts");
            return;
        }

        try {
            final CompletableFuture<RowSet<Row>> future = new CompletableFuture<>();
            client.query(getQueryFromFile("exists_tables"))
                    .execute(getAsyncResultHandler(future));
            final CompletableFuture<RowSet<Row>> futureCompose = future.thenCompose(rows -> {
                final CompletableFuture<RowSet<Row>> futureCreate = new CompletableFuture<>();
                return Optional.ofNullable(rows.iterator())
                        .filter(Iterator::hasNext)
                        .map(Iterator::next)
                        .map(row -> row.getBoolean("exists"))
                        .filter(Boolean.FALSE::equals)
                        .map(e -> client.query(getQueryFromFile("create_tables")))
                        .map(q -> {
                            q.execute(getAsyncResultHandler(futureCreate));
                            LOGGER.info("Creating process_instances table.");
                            return futureCreate;
                        })
                        .orElseGet(() -> {
                            futureCreate.complete(null);
                            LOGGER.info("Table process_instances already exists.");
                            return futureCreate;
                        });
            });
            getResultFromFuture(futureCompose)
                    .map(RowSet::rowCount)
                    .ifPresent(count -> {
                        if (count > 0) {
                            LOGGER.info("DDL successfully done for ProcessInstance");
                        } else {
                            LOGGER.info("DDL executed with no changes for ProcessInstance");
                        }
                    });
        } catch (Exception e) {
            //not break the execution flow in case of any missing permission for db application user, for instance.
            LOGGER.error("Error creating process_instances table, the database should be configured properly before " +
                    "starting the application", e);
        }
    }

    private String getQueryFromFile(String scriptName) {
        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(String.format("sql/%s.sql", scriptName))) {
            byte[] buffer = new byte[stream.available()];
            stream.read(buffer);
            return new String(buffer);
        } catch (Exception e) {
            throw uncheckedException(e, "Error reading query script file %s", scriptName);
        }
    }
}
