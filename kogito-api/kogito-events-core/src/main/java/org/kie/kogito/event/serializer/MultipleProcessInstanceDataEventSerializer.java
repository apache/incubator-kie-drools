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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import org.kie.kogito.event.process.KogitoMarshallEventSupport;
import org.kie.kogito.event.process.MultipleProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import static org.kie.kogito.event.process.KogitoEventBodySerializationHelper.writeInt;

public class MultipleProcessInstanceDataEventSerializer extends JsonSerializer<MultipleProcessInstanceDataEvent> {

    private static final Logger logger = LoggerFactory.getLogger(MultipleProcessInstanceDataEventDeserializer.class);

    private JsonSerializer<Object> defaultSerializer;

    public MultipleProcessInstanceDataEventSerializer(JsonSerializer<Object> serializer) {
        this.defaultSerializer = serializer;
    }

    @Override
    public void serialize(MultipleProcessInstanceDataEvent value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        if (MultipleProcessInstanceDataEvent.BINARY_CONTENT_TYPE.equals(value.getDataContentType())) {
            gen.writeStartObject();
            gen.writeStringField("datacontenttype", value.getDataContentType());
            gen.writeStringField("source", value.getSource().toString());
            gen.writeStringField("id", value.getId());
            gen.writeStringField("specversion", value.getSpecVersion().toString());
            gen.writeStringField("type", value.getType());
            boolean compress = value.isCompressed();
            if (compress) {
                gen.writeBooleanField(MultipleProcessInstanceDataEvent.COMPRESS_DATA, true);
            }
            gen.writeBinaryField("data", dataAsBytes(gen, value.getData(), compress));
            gen.writeEndObject();
        } else {
            defaultSerializer.serialize(value, gen, serializers);
        }
    }

    private byte[] dataAsBytes(JsonGenerator gen, Collection<ProcessInstanceDataEvent<? extends KogitoMarshallEventSupport>> data, boolean compress) throws IOException {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(compress ? new GZIPOutputStream(bytesOut) : bytesOut)) {
            logger.trace("Writing size {}", data.size());
            writeInt(out, data.size());
            Map<String, ProcessInstanceDataEventExtensionRecord> infos = new HashMap<>();
            for (ProcessInstanceDataEvent<? extends KogitoMarshallEventSupport> cloudEvent : data) {
                String key = cloudEvent.getKogitoProcessInstanceId();
                ProcessInstanceDataEventExtensionRecord info = infos.get(key);
                if (info == null) {
                    logger.trace("Writing marker byte -1");
                    out.writeByte((byte) -1);
                    info = new ProcessInstanceDataEventExtensionRecord(infos.size(), cloudEvent);
                    logger.trace("Writing info", info);
                    info.writeEvent(out);
                    infos.put(key, info);
                } else {
                    logger.trace("Writing marker byte {}", info.getOrdinal());
                    out.writeByte((byte) info.getOrdinal());
                }
                logger.trace("Writing type {}", cloudEvent.getType());
                out.writeUTF(cloudEvent.getType());
                int timeDelta = cloudEvent.getTime().compareTo(info.getTime());
                logger.trace("Writing time delta {}", timeDelta);
                writeInt(out, timeDelta);
                logger.trace("Writing cloud event attrs {}", cloudEvent);
                KogitoDataEventSerializationHelper.writeCloudEventAttrs(out, cloudEvent);
                KogitoMarshallEventSupport itemData = cloudEvent.getData();
                if (itemData != null) {
                    logger.trace("Writing data not null boolean");
                    out.writeBoolean(true);
                    logger.trace("Writing cloud event body {}", itemData);
                    itemData.writeEvent(out);
                } else {
                    logger.trace("Writing data null boolean");
                    out.writeBoolean(false);
                }
                logger.trace("individual event writing completed");
            }
        }
        return bytesOut.toByteArray();
    }

}
