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

package org.kie.kogito.jobs.service.messaging;

import org.apache.kafka.common.header.Headers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.jobs.api.event.JobCloudEvent;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class JobCloudEventDeserializerTest {

    private static final String TOPIC = "TOPIC";

    @Mock
    private org.kie.kogito.jobs.api.event.serialization.JobCloudEventDeserializer internalDeserializer;

    @Mock
    private JobCloudEvent<?> event;

    @Mock
    private Headers headers;

    private JobCloudEventDeserializer testedDeserializer;

    private final byte[] data = new byte[0];

    @BeforeEach
    void setUp() {
        testedDeserializer = new JobCloudEventDeserializer();
        testedDeserializer.deserializer = internalDeserializer;
        doReturn(event).when(internalDeserializer).deserialize(data);
    }

    @Test
    void deserialize() {
        JobCloudEvent<?> result = testedDeserializer.deserialize(TOPIC, data);
        assertThat(result).isSameAs(event);
    }

    @Test
    void deserializeWithHeaders() {
        JobCloudEvent<?> result = testedDeserializer.deserialize(TOPIC, headers, data);
        assertThat(result).isSameAs(event);
    }
}
