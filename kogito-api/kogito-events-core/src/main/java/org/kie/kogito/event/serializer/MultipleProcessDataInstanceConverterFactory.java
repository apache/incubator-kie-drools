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
package org.kie.kogito.event.serializer;

import java.io.IOException;
import java.util.Base64;
import java.util.Collection;

import org.kie.kogito.event.Converter;
import org.kie.kogito.event.impl.JacksonTypeCloudEventDataConverter;
import org.kie.kogito.event.process.KogitoMarshallEventSupport;
import org.kie.kogito.event.process.MultipleProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.core.data.PojoCloudEventData.ToBytes;

public class MultipleProcessDataInstanceConverterFactory {
    private MultipleProcessDataInstanceConverterFactory() {
    }

    public static ToBytes<Collection<ProcessInstanceDataEvent<? extends KogitoMarshallEventSupport>>> toCloudEvent(MultipleProcessInstanceDataEvent event, ObjectMapper objectMapper) {
        if (MultipleProcessInstanceDataEvent.BINARY_CONTENT_TYPE.equals(event.getDataContentType())) {
            return event.isCompressed() ? compressedToBytes : binaryToBytes;
        } else {
            return objectMapper::writeValueAsBytes;
        }
    }

    public static Converter<CloudEventData, Collection<ProcessInstanceDataEvent<? extends KogitoMarshallEventSupport>>> fromCloudEvent(CloudEvent cloudEvent, ObjectMapper objectMapper) {
        if (MultipleProcessInstanceDataEvent.BINARY_CONTENT_TYPE.equals(cloudEvent.getDataContentType())) {
            return isCompressed(cloudEvent) ? compressedConverter : binaryConverter;
        } else {
            return new JacksonTypeCloudEventDataConverter<>(objectMapper, new TypeReference<Collection<ProcessInstanceDataEvent<? extends KogitoMarshallEventSupport>>>() {
            });
        }
    }

    private static boolean isCompressed(CloudEvent event) {
        return MultipleProcessInstanceDataEvent.isCompressed(event.getExtension(MultipleProcessInstanceDataEvent.COMPRESS_DATA));
    }

    private static ToBytes<Collection<ProcessInstanceDataEvent<? extends KogitoMarshallEventSupport>>> compressedToBytes = data -> serialize(data, true);

    private static ToBytes<Collection<ProcessInstanceDataEvent<? extends KogitoMarshallEventSupport>>> binaryToBytes = data -> serialize(data, false);

    private static Converter<CloudEventData, Collection<ProcessInstanceDataEvent<? extends KogitoMarshallEventSupport>>> binaryConverter =
            data -> deserialize(data, false);

    private static Converter<CloudEventData, Collection<ProcessInstanceDataEvent<? extends KogitoMarshallEventSupport>>> compressedConverter =
            data -> deserialize(data, true);

    private static Collection<ProcessInstanceDataEvent<? extends KogitoMarshallEventSupport>> deserialize(CloudEventData data, boolean compress) throws IOException {
        return MultipleProcessInstanceDataEventDeserializer.readFromBytes(Base64.getDecoder().decode(data.toBytes()), compress);
    }

    private static byte[] serialize(Collection<ProcessInstanceDataEvent<? extends KogitoMarshallEventSupport>> data,
            boolean compress) throws IOException {
        return Base64.getEncoder().encode(MultipleProcessInstanceDataEventSerializer.dataAsBytes(data, compress));
    }
}
