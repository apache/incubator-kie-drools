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
package org.kie.kogito.jobs.service.api.serialization;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

import io.cloudevents.SpecVersion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class SpecVersionDeserializerTest {

    @Mock
    private JsonParser parser;

    @Mock
    private DeserializationContext context;

    private SpecVersionDeserializer deserializer = new SpecVersionDeserializer();

    @Test
    void deserializeV1() throws IOException {
        doReturn("1.0").when(parser).getText();
        assertThat(deserializer.deserialize(parser, context)).isEqualTo(SpecVersion.V1);
    }

    @Test
    void deserializeV03() throws IOException {
        doReturn("0.3").when(parser).getText();
        assertThat(deserializer.deserialize(parser, context)).isEqualTo(SpecVersion.V03);
    }

    @Test
    void deserializeUnknown() throws IOException {
        doReturn("unknown").when(parser).getText();
        assertThatThrownBy(() -> deserializer.deserialize(parser, context))
                .hasMessage("Invalid specversion: unknown");
    }
}
