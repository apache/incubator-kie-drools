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
import java.time.ZonedDateTime;
import java.util.Optional;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.event.CloudEventUnmarshaller;
import org.kie.kogito.event.Converter;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.DataEventFactory;
import org.kie.kogito.event.cloudevents.utils.CloudEventUtils;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.smallrye.reactive.messaging.ce.CloudEventMetadata;

public class QuarkusCloudEventConverter<I, T> implements
        Converter<Message<I>, DataEvent<T>> {

    private final CloudEventUnmarshaller<I, T> unmarshaller;

    public QuarkusCloudEventConverter(CloudEventUnmarshaller<I, T> unmarshaller) {
        this.unmarshaller = unmarshaller;
    }

    @Override
    public DataEvent<T> convert(Message<I> message) throws IOException {
        Optional<CloudEventMetadata> metadata = message.getMetadata(CloudEventMetadata.class);
        return DataEventFactory.from(metadata.isPresent() ? binaryCE(metadata.get(), message.getPayload()) : unmarshaller.cloudEvent().convert(message.getPayload()), unmarshaller.data());
    }

    private CloudEvent binaryCE(CloudEventMetadata<?> meta, I payload) throws IOException {
        CloudEventBuilder builder =
                CloudEventBuilder.fromSpecVersion(SpecVersion.parse(meta.getSpecVersion()))
                        .withType(meta.getType())
                        .withSource(meta.getSource())
                        .withId(meta.getId());
        if (payload != null) {
            builder.withData(unmarshaller.binaryCloudEvent().convert(payload));
        }
        meta.getDataContentType().ifPresent(builder::withDataContentType);
        meta.getDataSchema().ifPresent(builder::withDataSchema);
        meta.getTimeStamp().map(ZonedDateTime::toOffsetDateTime).ifPresent(builder::withTime);
        meta.getSubject().ifPresent(builder::withSubject);
        meta.getExtensions().forEach((k, v) -> CloudEventUtils.withExtension(builder, k, v));
        return builder.build();
    }
}
