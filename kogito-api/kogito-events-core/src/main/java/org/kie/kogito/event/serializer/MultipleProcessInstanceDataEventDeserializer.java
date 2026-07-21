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

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.zip.GZIPInputStream;

import org.kie.kogito.event.process.CloudEventVisitor;
import org.kie.kogito.event.process.KogitoMarshallEventFlag;
import org.kie.kogito.event.process.KogitoMarshallEventSupport;
import org.kie.kogito.event.process.MultipleProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceErrorDataEvent;
import org.kie.kogito.event.process.ProcessInstanceErrorEventBody;
import org.kie.kogito.event.process.ProcessInstanceNodeDataEvent;
import org.kie.kogito.event.process.ProcessInstanceNodeEventBody;
import org.kie.kogito.event.process.ProcessInstanceSLADataEvent;
import org.kie.kogito.event.process.ProcessInstanceSLAEventBody;
import org.kie.kogito.event.process.ProcessInstanceStateDataEvent;
import org.kie.kogito.event.process.ProcessInstanceStateEventBody;
import org.kie.kogito.event.process.ProcessInstanceVariableDataEvent;
import org.kie.kogito.event.process.ProcessInstanceVariableEventBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;

import io.cloudevents.SpecVersion;

import static org.kie.kogito.event.process.KogitoEventBodySerializationHelper.readInt;

public class MultipleProcessInstanceDataEventDeserializer extends JsonDeserializer<MultipleProcessInstanceDataEvent> implements ResolvableDeserializer {

    private static final Logger logger = LoggerFactory.getLogger(MultipleProcessInstanceDataEventDeserializer.class);

    private JsonDeserializer<Object> defaultDeserializer;

    public MultipleProcessInstanceDataEventDeserializer(JsonDeserializer<Object> deserializer) {
        this.defaultDeserializer = deserializer;
    }

    @Override
    public void resolve(DeserializationContext ctxt) throws JsonMappingException {
        ((ResolvableDeserializer) defaultDeserializer).resolve(ctxt);
    }

    @Override
    public MultipleProcessInstanceDataEvent deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JacksonException {
        JsonNode node = p.getCodec().readTree(p);
        JsonNode dataContentType = node.get("datacontenttype");
        if (dataContentType != null && MultipleProcessInstanceDataEvent.BINARY_CONTENT_TYPE.equals(dataContentType.asText())) {
            MultipleProcessInstanceDataEvent event = new MultipleProcessInstanceDataEvent();
            event.setDataContentType(dataContentType.asText());
            event.setSource(URI.create(node.get("source").asText()));
            event.setType(node.get("type").asText());
            event.setSpecVersion(SpecVersion.parse(node.get("specversion").asText()));
            event.setId(node.get("id").asText());
            JsonNode data = node.get("data");
            if (data != null) {
                event.setData(readFromBytes(data.binaryValue(), isCompressed(node), buildFlagSet(node)));
            }
            return event;
        } else {
            JsonParser newParser = node.traverse(p.getCodec());
            newParser.nextToken();
            return (MultipleProcessInstanceDataEvent) defaultDeserializer.deserialize(newParser, ctxt);
        }
    }

    private Set<KogitoMarshallEventFlag> buildFlagSet(JsonNode node) {
        JsonNode flagsNode = node.get(MultipleProcessInstanceDataEvent.MARSHALL_FLAGS);
        return flagsNode != null && flagsNode.isNumber() ? KogitoMarshallEventFlag.buildFlagsSet(flagsNode.intValue()) : EnumSet.noneOf(KogitoMarshallEventFlag.class);
    }

    private static boolean isCompressed(JsonNode node) {
        JsonNode compress = node.get(MultipleProcessInstanceDataEvent.COMPRESS_DATA);
        return compress != null && compress.isBoolean() ? compress.asBoolean() : false;
    }

    static Collection<ProcessInstanceDataEvent<? extends KogitoMarshallEventSupport>> readFromBytes(byte[] binaryValue, boolean compressed, Set<KogitoMarshallEventFlag> flags) throws IOException {
        InputStream wrappedIn = new ByteArrayInputStream(binaryValue);
        if (compressed) {
            logger.trace("Gzip compressed byte array");
            wrappedIn = new GZIPInputStream(wrappedIn);
        }
        try (DataInputStream in = new DataInputStream(wrappedIn)) {
            int size = readInt(in);
            logger.trace("Reading collection of size {}", size);
            Collection<ProcessInstanceDataEvent<? extends KogitoMarshallEventSupport>> result = new ArrayList<>(size);
            List<ProcessInstanceDataEventExtensionRecord> infos = new ArrayList<>();
            while (size-- > 0) {
                byte readInfo = in.readByte();
                logger.trace("Info ordinal is {}", readInfo);
                ProcessInstanceDataEventExtensionRecord info;
                if (readInfo == -1) {
                    info = new ProcessInstanceDataEventExtensionRecord();
                    info.readEvent(in, flags);
                    logger.trace("Info readed is {}", info);
                    infos.add(info);
                } else {
                    info = infos.get(readInfo);
                    logger.trace("Info cached is {}", info);
                }
                String type = in.readUTF();
                logger.trace("Type is {}", info);
                result.add(getCloudEvent(in, type, info, flags));
                logger.trace("{} events remaining", size);
            }
            return result;
        }
    }

    private static ProcessInstanceDataEvent<? extends KogitoMarshallEventSupport> getCloudEvent(DataInputStream in, String type, ProcessInstanceDataEventExtensionRecord info,
            Set<KogitoMarshallEventFlag> flags) throws IOException {
        switch (type) {
            case ProcessInstanceVariableDataEvent.VAR_TYPE:
                ProcessInstanceVariableDataEvent item = buildDataEvent(in, new ProcessInstanceVariableDataEvent(), ProcessInstanceVariableEventBody::new, info, flags);
                item.setKogitoVariableName(item.getData().getVariableName());
                return item;
            case ProcessInstanceStateDataEvent.STATE_TYPE:
                return buildDataEvent(in, new ProcessInstanceStateDataEvent(), ProcessInstanceStateEventBody::new, info, flags);
            case ProcessInstanceNodeDataEvent.NODE_TYPE:
                return buildDataEvent(in, new ProcessInstanceNodeDataEvent(), ProcessInstanceNodeEventBody::new, info, flags);
            case ProcessInstanceErrorDataEvent.ERROR_TYPE:
                return buildDataEvent(in, new ProcessInstanceErrorDataEvent(), ProcessInstanceErrorEventBody::new, info, flags);
            case ProcessInstanceSLADataEvent.SLA_TYPE:
                return buildDataEvent(in, new ProcessInstanceSLADataEvent(), ProcessInstanceSLAEventBody::new, info, flags);
            default:
                throw new UnsupportedOperationException("Unrecognized event type " + type);
        }
    }

    private static <T extends ProcessInstanceDataEvent<V>, V extends KogitoMarshallEventSupport & CloudEventVisitor> T buildDataEvent(DataInput in, T cloudEvent, Supplier<V> bodySupplier,
            ProcessInstanceDataEventExtensionRecord info, Set<KogitoMarshallEventFlag> flags) throws IOException {
        int delta = readInt(in);
        logger.trace("Time delta is {}", delta);
        cloudEvent.setTime(info.getTime().plus(delta, ChronoUnit.MILLIS));
        KogitoDataEventSerializationHelper.readCloudEventAttrs(in, cloudEvent);
        logger.trace("Cloud event before population {}", cloudEvent);
        KogitoDataEventSerializationHelper.populateCloudEvent(cloudEvent, info);
        logger.trace("Cloud event after population {}", cloudEvent);

        boolean isNotNull = in.readBoolean();
        if (isNotNull) {
            logger.trace("Data is not null");
            V body = bodySupplier.get();
            body.readEvent(in, flags);
            logger.trace("Event body before population {}", body);
            body.visit(cloudEvent);
            logger.trace("Event body after population {}", body);
            cloudEvent.setData(body);
        } else {
            logger.trace("Data is null");
        }
        return cloudEvent;
    }

}
