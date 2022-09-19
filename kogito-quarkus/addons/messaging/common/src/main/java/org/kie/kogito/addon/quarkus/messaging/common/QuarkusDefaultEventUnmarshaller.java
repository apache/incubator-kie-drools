/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.addon.quarkus.messaging.common;

import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.event.process.ProcessDataEvent;
import org.kie.kogito.services.event.impl.AbstractEventUnmarshaller;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.smallrye.reactive.messaging.ce.CloudEventMetadata;

public class QuarkusDefaultEventUnmarshaller extends AbstractEventUnmarshaller<Message<?>> {

    public QuarkusDefaultEventUnmarshaller(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public <T> T unmarshall(Message<?> message, Class<T> clazz, Class<?>... parametrizedClasses) throws IOException {
        Optional<CloudEventMetadata> metadata = message.getMetadata(CloudEventMetadata.class);
        return metadata.isPresent() ? (T) binaryCE(metadata.get(), message.getPayload(), clazz, parametrizedClasses) : unmarshallPayload(message.getPayload(), clazz, parametrizedClasses);
    }

    private Object binaryCE(CloudEventMetadata<?> meta, Object payload, Class<?> clazz, Class<?>... parametrizedClasses) throws IOException {
        if (ProcessDataEvent.class.isAssignableFrom(clazz)) {
            ProcessDataEvent processDataEvent = new ProcessDataEvent<>();
            processDataEvent.setData(unmarshallPayload(payload, parametrizedClasses[0]));
            return addCloudEventInfo(meta, processDataEvent);
        } else if (CloudEvent.class.isAssignableFrom(clazz)) {
            CloudEventBuilder builder =
                    CloudEventBuilder.fromSpecVersion(SpecVersion.parse(meta.getSpecVersion()))
                            .withType(meta.getType())
                            .withSource(meta.getSource())
                            .withId(meta.getId());
            meta.getDataContentType().ifPresent(builder::withDataContentType);
            meta.getDataSchema().ifPresent(builder::withDataSchema);
            meta.getTimeStamp().map(ZonedDateTime::toOffsetDateTime).ifPresent(builder::withTime);
            meta.getSubject().ifPresent(builder::withSubject);
            meta.getExtensions().forEach((k, v) -> addExtension(builder, k, v));

            if (payload instanceof byte[]) {
                builder.withData((byte[]) payload);
            } else if (payload != null) {
                builder.withData(payload.toString().getBytes());
            }
            return builder.build();
        } else {
            return unmarshallPayload(payload, clazz, parametrizedClasses);
        }
    }

    private void addExtension(CloudEventBuilder builder, String k, Object v) {
        if (v instanceof Number) {
            builder.withExtension(k, (Number) v);
        } else if (v instanceof Boolean) {
            builder.withExtension(k, (Boolean) v);
        } else if (v instanceof byte[]) {
            builder.withExtension(k, (byte[]) v);
        } else if (v instanceof URI) {
            builder.withExtension(k, (URI) v);
        } else if (v instanceof OffsetDateTime) {
            builder.withExtension(k, (OffsetDateTime) v);
        } else {
            builder.withExtension(k, v.toString());
        }
    }

    private ProcessDataEvent<?> addCloudEventInfo(CloudEventMetadata<?> meta, ProcessDataEvent<?> event) {
        meta.getDataContentType().ifPresent(event::setDataContentType);
        meta.getDataSchema().ifPresent(event::setDataSchema);
        meta.getSubject().ifPresent(event::setSubject);
        meta.getTimeStamp().map(ZonedDateTime::toOffsetDateTime).ifPresent(event::setTime);
        set(meta::getId, event::setId);
        set(meta::getType, event::setType);
        set(meta::getSource, event::setSource);
        set(meta::getSpecVersion, specVersion -> event.setSpecVersion(SpecVersion.parse(specVersion)));
        meta.getExtensions().forEach(event::addExtensionAttribute);
        return event;
    }

    private <T> void set(Supplier<T> supplier, Consumer<T> consumer) {
        T value = supplier.get();
        if (value != null) {
            consumer.accept(value);
        }
    }
}
