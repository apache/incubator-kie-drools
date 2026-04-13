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

import org.drools.ruleunits.api.DataStream;
import org.drools.ruleunits.api.RuleUnitInstance;

/**
 * A reactive connector bridges an external streaming system (Kafka, Pulsar,
 * database CDC, etc.) with a Drools {@link DataStream}. Messages consumed from
 * the external system are deserialized and appended to the DataStream, which
 * propagates them into the rule session's working memory.
 *
 * <p>Lifecycle: {@code start()} &rarr; {@code pause()/resume()} &rarr; {@code close()}.
 *
 * <p>Connectors are <b>not thread-safe</b> for lifecycle operations; callers must
 * ensure that {@code start}, {@code pause}, {@code resume}, and {@code close}
 * are not invoked concurrently. The internal polling loop runs on its own thread.
 *
 * @param <T> the fact type produced by this connector
 */
public interface ReactiveConnector<T> extends AutoCloseable {

    /**
     * Start consuming messages from the external system and appending
     * deserialized facts into the target {@link DataStream}. If a
     * {@link RuleUnitInstance} is provided and the {@link FiringStrategy}
     * is not {@code EXTERNAL}, the connector will also trigger rule firing.
     *
     * @param target       the DataStream to feed facts into
     * @param ruleUnit     optional RuleUnitInstance for automatic firing;
     *                     may be {@code null} when using {@link FiringStrategy#EXTERNAL}
     * @throws ConnectorException if the connector cannot be started
     */
    void start(DataStream<T> target, RuleUnitInstance<?> ruleUnit);

    /**
     * Convenience overload that starts the connector in {@link FiringStrategy#EXTERNAL}
     * mode, only inserting facts without triggering rule firing.
     */
    default void start(DataStream<T> target) {
        start(target, null);
    }

    /**
     * Temporarily pause consumption. Messages are not consumed while paused.
     * Has no effect if the connector is not currently running.
     */
    void pause();

    /**
     * Resume consumption after a {@link #pause()}.
     * Has no effect if the connector is not currently paused.
     */
    void resume();

    /**
     * Return a point-in-time health snapshot of this connector.
     */
    ConnectorHealth health();

    /**
     * Return the current lifecycle state.
     */
    ConnectorState getState();

    /**
     * Gracefully shut down the connector, releasing all resources.
     * After this call the connector cannot be restarted.
     */
    @Override
    void close();
}
