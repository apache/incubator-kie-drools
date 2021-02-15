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

package org.kie.kogito.persistence.kafka;

import java.util.UUID;

import org.apache.kafka.streams.KafkaStreams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.process.Process;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KafkaStreamsStateListenerTest {

    @Mock
    KafkaStreams streams;

    @Mock
    KafkaProcessInstances pi;

    @Mock
    Process process;

    @Spy
    KafkaStreamsStateListener listener;

    @BeforeEach
    public void setup() {
        when(process.id()).thenReturn(UUID.randomUUID().toString());
        when(pi.getProcess()).thenReturn(process);
        listener.setKafkaStreams(streams);
    }

    @Test
    public void testSetStore() {
        listener.addProcessInstances(pi);

        listener.onChange(KafkaStreams.State.RUNNING, KafkaStreams.State.REBALANCING);

        verify(pi).setStore(any());
        verify(streams).store(any());
        assertThat(listener.getInstances()).hasSize(1);
        assertThat(listener.getKafkaStreams()).isEqualTo(streams);

        listener.close();

        assertThat(listener.getInstances()).isEmpty();
    }

    @Test
    public void testSetStoreWhenRunning() {
        when(streams.state()).thenReturn(KafkaStreams.State.RUNNING);

        listener.addProcessInstances(pi);

        verify(pi).setStore(any());
        verify(streams).store(any());
        assertThat(listener.getInstances()).hasSize(1);

        listener.close();

        assertThat(listener.getInstances()).isEmpty();
    }

    @Test
    public void testStoreNoSet() {
        listener.addProcessInstances(pi);

        listener.onChange(KafkaStreams.State.REBALANCING, KafkaStreams.State.CREATED);

        verify(pi, never()).setStore(any());
        verify(streams, never()).store(any());
    }
}
