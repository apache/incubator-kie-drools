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

import org.drools.reactive.api.ConnectorException;
import org.drools.reactive.api.ConnectorHealth;
import org.drools.reactive.api.ConnectorState;
import org.drools.reactive.api.ReactiveConnector;
import org.drools.ruleunits.api.DataStream;
import org.drools.ruleunits.api.RuleUnitInstance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReactiveConnectorManagerTest {

    private ReactiveConnectorManager manager;

    @BeforeEach
    void setUp() {
        manager = new ReactiveConnectorManager();
    }

    @Test
    void shouldRegisterAndTrackConnectors() {
        ReactiveConnector<?> connector = mock(ReactiveConnector.class);
        manager.register("test", connector);

        assertThat(manager.getRegisteredNames()).containsExactly("test");
    }

    @Test
    void shouldRejectDuplicateRegistration() {
        ReactiveConnector<?> c1 = mock(ReactiveConnector.class);
        ReactiveConnector<?> c2 = mock(ReactiveConnector.class);

        manager.register("dup", c1);

        assertThatThrownBy(() -> manager.register("dup", c2))
                .isInstanceOf(ConnectorException.class)
                .hasMessageContaining("already registered");
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldStartRegisteredConnector() {
        ReactiveConnector<String> connector = mock(ReactiveConnector.class);
        DataStream<String> stream = mock(DataStream.class);
        RuleUnitInstance<?> ruleUnit = mock(RuleUnitInstance.class);

        manager.register("my-conn", connector);
        manager.start("my-conn", stream, ruleUnit);

        verify(connector).start(stream, ruleUnit);
    }

    @Test
    void shouldThrowWhenStartingUnregisteredConnector() {
        DataStream<?> stream = mock(DataStream.class);
        assertThatThrownBy(() -> manager.start("missing", stream))
                .isInstanceOf(ConnectorException.class)
                .hasMessageContaining("No connector registered");
    }

    @Test
    void shouldPauseAndResumeConnector() {
        ReactiveConnector<?> connector = mock(ReactiveConnector.class);
        manager.register("ctrl", connector);

        manager.pause("ctrl");
        verify(connector).pause();

        manager.resume("ctrl");
        verify(connector).resume();
    }

    @Test
    void shouldReturnHealthForConnector() {
        ReactiveConnector<?> connector = mock(ReactiveConnector.class);
        ConnectorHealth health = new ConnectorHealth(ConnectorState.RUNNING, 10, 9, 1);
        when(connector.health()).thenReturn(health);

        manager.register("monitored", connector);
        ConnectorHealth result = manager.health("monitored");

        assertThat(result.getState()).isEqualTo(ConnectorState.RUNNING);
        assertThat(result.getMessagesReceived()).isEqualTo(10);
        assertThat(result.getMessagesProcessed()).isEqualTo(9);
        assertThat(result.getMessagesFailed()).isEqualTo(1);
    }

    @Test
    void shouldStopIndividualConnector() {
        ReactiveConnector<?> connector = mock(ReactiveConnector.class);
        manager.register("to-stop", connector);

        manager.stop("to-stop");

        verify(connector).close();
        assertThat(manager.getRegisteredNames()).isEmpty();
    }

    @Test
    void shouldShutdownAllOnPreDestroy() {
        ReactiveConnector<?> c1 = mock(ReactiveConnector.class);
        ReactiveConnector<?> c2 = mock(ReactiveConnector.class);
        manager.register("a", c1);
        manager.register("b", c2);

        manager.shutdown();

        verify(c1).close();
        verify(c2).close();
        assertThat(manager.getRegisteredNames()).isEmpty();
    }
}
