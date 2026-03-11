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
package org.drools.reactive.quarkus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.drools.reactive.api.ConnectorException;
import org.drools.reactive.api.ConnectorHealth;
import org.drools.reactive.api.ReactiveConnector;
import org.drools.ruleunits.api.DataStream;
import org.drools.ruleunits.api.RuleUnitInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CDI-managed lifecycle manager for reactive connectors in a Quarkus application.
 *
 * <p>Connectors are registered by name, started when bound to a DataStream,
 * and automatically shut down when the application stops.
 *
 * <p>Usage:
 * <pre>{@code
 * @Inject ReactiveConnectorManager manager;
 *
 * // Register and start a connector
 * manager.register("transactions", kafkaConnector);
 * manager.start("transactions", dataStream, ruleUnitInstance);
 *
 * // Check health
 * ConnectorHealth health = manager.health("transactions");
 * }</pre>
 */
@ApplicationScoped
public class ReactiveConnectorManager {

    private static final Logger logger = LoggerFactory.getLogger(ReactiveConnectorManager.class);

    private final Map<String, ReactiveConnector<?>> connectors = new ConcurrentHashMap<>();

    /**
     * Register a connector by name. Does not start it yet.
     */
    public void register(String name, ReactiveConnector<?> connector) {
        if (connectors.putIfAbsent(name, connector) != null) {
            throw new ConnectorException("Connector already registered with name: " + name);
        }
        logger.info("Registered reactive connector: {}", name);
    }

    /**
     * Start a previously registered connector, binding it to a DataStream
     * and optionally to a RuleUnitInstance for automatic firing.
     */
    @SuppressWarnings("unchecked")
    public <T> void start(String name, DataStream<T> target, RuleUnitInstance<?> ruleUnit) {
        ReactiveConnector<T> connector = (ReactiveConnector<T>) connectors.get(name);
        if (connector == null) {
            throw new ConnectorException("No connector registered with name: " + name);
        }
        connector.start(target, ruleUnit);
        logger.info("Started reactive connector: {}", name);
    }

    /**
     * Start a connector in EXTERNAL firing mode (insert only, no auto-firing).
     */
    public <T> void start(String name, DataStream<T> target) {
        start(name, target, null);
    }

    /**
     * Pause a running connector.
     */
    public void pause(String name) {
        ReactiveConnector<?> connector = getOrThrow(name);
        connector.pause();
    }

    /**
     * Resume a paused connector.
     */
    public void resume(String name) {
        ReactiveConnector<?> connector = getOrThrow(name);
        connector.resume();
    }

    /**
     * Get health information for a named connector.
     */
    public ConnectorHealth health(String name) {
        ReactiveConnector<?> connector = getOrThrow(name);
        return connector.health();
    }

    /**
     * Get all registered connector names.
     */
    public java.util.Set<String> getRegisteredNames() {
        return java.util.Collections.unmodifiableSet(connectors.keySet());
    }

    /**
     * Shut down a specific connector.
     */
    public void stop(String name) {
        ReactiveConnector<?> connector = connectors.remove(name);
        if (connector != null) {
            try {
                connector.close();
                logger.info("Stopped reactive connector: {}", name);
            } catch (Exception e) {
                logger.warn("Error stopping connector: {}", name, e);
            }
        }
    }

    /**
     * Shut down all registered connectors. Called automatically by CDI
     * when the application stops.
     */
    @PreDestroy
    void shutdown() {
        logger.info("Shutting down {} reactive connector(s)", connectors.size());
        for (Map.Entry<String, ReactiveConnector<?>> entry : connectors.entrySet()) {
            try {
                entry.getValue().close();
                logger.info("Stopped connector: {}", entry.getKey());
            } catch (Exception e) {
                logger.warn("Error stopping connector: {}", entry.getKey(), e);
            }
        }
        connectors.clear();
    }

    private ReactiveConnector<?> getOrThrow(String name) {
        ReactiveConnector<?> connector = connectors.get(name);
        if (connector == null) {
            throw new ConnectorException("No connector registered with name: " + name);
        }
        return connector;
    }
}
