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
package org.drools.reactive.debezium;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;

import org.drools.reactive.api.AbstractReactiveConnector;
import org.drools.reactive.api.FactDeserializer;

/**
 * A reactive connector that uses Debezium Engine to capture database change
 * events (CDC) and feed them as typed facts into a Drools
 * {@link org.drools.ruleunits.api.DataStream}.
 *
 * <p>Each database row change (INSERT/UPDATE/DELETE) is deserialized into
 * a {@link org.drools.reactive.debezium.ChangeEvent} wrapping the typed
 * fact and CDC metadata (operation, source, table, timestamp).
 *
 * @param <T> the row type extracted from change events
 */
public class DebeziumReactiveConnector<T> extends AbstractReactiveConnector<org.drools.reactive.debezium.ChangeEvent<T>> {

    private final DebeziumConnectorConfig debeziumConfig;
    private final ChangeEventDeserializer<T> changeDeserializer;
    private final DebeziumEngineFactory engineFactory;

    private DebeziumEngine<ChangeEvent<String, String>> engine;
    private ExecutorService executor;
    private final CountDownLatch shutdownLatch = new CountDownLatch(1);

    public DebeziumReactiveConnector(DebeziumConnectorConfig config,
                                     ChangeEventDeserializer<T> changeDeserializer) {
        this(config, changeDeserializer, null);
    }

    /**
     * Constructor accepting an engine factory for test injection.
     */
    DebeziumReactiveConnector(DebeziumConnectorConfig config,
                              ChangeEventDeserializer<T> changeDeserializer,
                              DebeziumEngineFactory engineFactory) {
        super(config, new NoOpFactDeserializer<>());
        this.debeziumConfig = config;
        this.changeDeserializer = changeDeserializer;
        this.engineFactory = engineFactory;
    }

    @Override
    protected void doStart() {
        changeDeserializer.configure(debeziumConfig.getProperties());

        Consumer<ChangeEvent<String, String>> handler = this::handleChangeEvent;
        if (engineFactory != null) {
            engine = engineFactory.create(debeziumConfig, handler);
        } else {
            engine = DebeziumEngine.create(Json.class)
                    .using(debeziumConfig.getDebeziumProperties())
                    .notifying(handler)
                    .build();
        }

        executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "drools-debezium-" + debeziumConfig.getConnectorName());
            t.setDaemon(true);
            return t;
        });
        executor.execute(() -> {
            try {
                engine.run();
            } finally {
                shutdownLatch.countDown();
            }
        });

        logger.info("Debezium connector started: name={}", debeziumConfig.getConnectorName());
    }

    private void handleChangeEvent(ChangeEvent<String, String> record) {
        recordReceived();
        try {
            org.drools.reactive.debezium.ChangeEvent<T> event =
                    changeDeserializer.deserialize(record.value());
            if (event != null) {
                processSingle(event);
                recordProcessed();
            }
        } catch (Exception e) {
            logger.warn("Failed to process Debezium change event: key={}", record.key(), e);
            recordFailed(e);
        }
    }

    @Override
    protected void doStop() {
        if (engine != null) {
            try {
                engine.close();
            } catch (Exception e) {
                logger.warn("Error closing Debezium engine", e);
            }
        }
        try {
            if (!shutdownLatch.await(10, TimeUnit.SECONDS)) {
                logger.warn("Debezium engine did not shut down within 10 seconds");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (executor != null) {
            executor.shutdownNow();
        }
        changeDeserializer.close();
    }

    @FunctionalInterface
    interface DebeziumEngineFactory {
        DebeziumEngine<ChangeEvent<String, String>> create(
                DebeziumConnectorConfig config,
                Consumer<ChangeEvent<String, String>> handler);
    }

    /**
     * No-op deserializer since DebeziumReactiveConnector uses ChangeEventDeserializer
     * directly rather than the generic FactDeserializer SPI.
     */
    private static class NoOpFactDeserializer<T> implements FactDeserializer<T> {
        @Override
        public T deserialize(String topic, byte[] data) {
            return null;
        }
    }
}
