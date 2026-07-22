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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

import io.cloudevents.SpecVersion;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SpecVersionSerializerTest {

    @Mock
    private JsonGenerator generator;

    @Mock
    private SerializerProvider provider;

    private SpecVersionSerializer serializer = new SpecVersionSerializer();

    @Test
    void serializeV1() throws IOException {
        serializer.serialize(SpecVersion.V1, generator, provider);
        verify(generator).writeString("1.0");
    }

    @Test
    void serializeV03() throws IOException {
        serializer.serialize(SpecVersion.V03, generator, provider);
        verify(generator).writeString("0.3");
    }
}
