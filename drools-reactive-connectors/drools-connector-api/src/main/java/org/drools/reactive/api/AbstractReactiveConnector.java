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
package org.drools.reactive.api;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.drools.ruleunits.api.DataStream;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Skeleton implementation of {@link ReactiveConnector} that manages lifecycle
 * state transitions, counters, and the {@link FiringStrategy} dispatch.
 * Subclasses only need to implement the transport-specific polling loop.
 *
 * @param <T> the fact type produced by this connector
 */
public abstract class AbstractReactiveConnector<T> implements ReactiveConnector<T> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final ConnectorConfig config;
    protected final FactDeserializer<T> deserializer;

    private final AtomicReference<ConnectorState> state = new AtomicReference<>(ConnectorState.CREATED);
    private final AtomicLong messagesReceived = new AtomicLong();
    private final AtomicLong messagesProcessed = new AtomicLong();
    private final AtomicLong messagesFailed = new AtomicLong();
    private volatile Throwable lastError;

    protected volatile DataStream<T> target;
    protected volatile RuleUnitInstance<?> ruleUnit;

    protected AbstractReactiveConnector(ConnectorConfig config, FactDeserializer<T> deserializer) {
        this.config = config;
        this.deserializer = deserializer;
    }

    @Override
    public final void start(DataStream<T> target, RuleUnitInstance<?> ruleUnit) {
        if (!state.compareAndSet(ConnectorState.CREATED, ConnectorState.STARTING)) {
            throw new ConnectorException("Connector cannot be started from state " + state.get());
        }
        this.target = target;
        this.ruleUnit = ruleUnit;
        try {
            deserializer.configure(config.getProperties());
            doStart();
            state.set(ConnectorState.RUNNING);
            logger.info("Connector started successfully");
        } catch (Exception e) {
            state.set(ConnectorState.FAILED);
            lastError = e;
            throw new ConnectorException("Failed to start connector", e);
        }
    }

    @Override
    public final void pause() {
        if (state.compareAndSet(ConnectorState.RUNNING, ConnectorState.PAUSED)) {
            doPause();
            logger.info("Connector paused");
        }
    }

    @Override
    public final void resume() {
        if (state.compareAndSet(ConnectorState.PAUSED, ConnectorState.RUNNING)) {
            doResume();
            logger.info("Connector resumed");
        }
    }

    @Override
    public final void close() {
        ConnectorState current = state.get();
        if (current == ConnectorState.STOPPED || current == ConnectorState.STOPPING) {
            return;
        }
        state.set(ConnectorState.STOPPING);
        try {
            doStop();
            deserializer.close();
        } finally {
            state.set(ConnectorState.STOPPED);
            logger.info("Connector stopped");
        }
    }

    @Override
    public ConnectorState getState() {
        return state.get();
    }

    @Override
    public ConnectorHealth health() {
        return new ConnectorHealth(
                state.get(),
                messagesReceived.get(),
                messagesProcessed.get(),
                messagesFailed.get(),
                lastError,
                null);
    }

    /**
     * Process a batch of deserialized facts: append each to the DataStream
     * and fire rules according to the configured {@link FiringStrategy}.
     */
    protected void processBatch(List<T> batch) {
        for (T fact : batch) {
            target.append(fact);
        }
        fireIfNeeded(batch.size());
    }

    /**
     * Process a single deserialized fact.
     */
    protected void processSingle(T fact) {
        target.append(fact);
        if (config.getFiringStrategy() == FiringStrategy.PER_MESSAGE) {
            fireIfNeeded(1);
        }
    }

    private void fireIfNeeded(int count) {
        if (ruleUnit == null || config.getFiringStrategy() == FiringStrategy.EXTERNAL) {
            return;
        }
        ruleUnit.fire();
    }

    protected void recordReceived() {
        messagesReceived.incrementAndGet();
    }

    protected void recordProcessed() {
        messagesProcessed.incrementAndGet();
    }

    protected void recordFailed(Throwable error) {
        messagesFailed.incrementAndGet();
        lastError = error;
    }

    /**
     * Transport-specific start logic. Called after the deserializer is configured
     * and before the state transitions to {@link ConnectorState#RUNNING}.
     */
    protected abstract void doStart();

    /**
     * Transport-specific pause logic. Default is a no-op.
     */
    protected void doPause() {
    }

    /**
     * Transport-specific resume logic. Default is a no-op.
     */
    protected void doResume() {
    }

    /**
     * Transport-specific shutdown logic. Must release all resources.
     */
    protected abstract void doStop();
}
