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
package org.kie.kogito.addon.quarkus.messaging.common;

import java.io.IOException;
import java.util.Optional;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.kie.kogito.event.CloudEventMarshaller;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventEmitter;
import org.kie.kogito.event.EventMarshaller;

import io.smallrye.reactive.messaging.ce.OutgoingCloudEventMetadata;
import io.smallrye.reactive.messaging.ce.OutgoingCloudEventMetadataBuilder;

public abstract class AbstractQuarkusCloudEventEmitter<M> implements EventEmitter {

    protected EventMarshaller<M> getEventMarshaller() {
        return null;
    }

    protected CloudEventMarshaller<M> getCloudEventMarshaller() {
        return null;
    }

    protected <T> Optional<OutgoingCloudEventMetadata<?>> getMetadata(DataEvent<T> event) {
        if (event.getId() == null || event.getType() == null || event.getSource() == null || event.getSpecVersion() == null) {
            return Optional.empty();
        }
        OutgoingCloudEventMetadataBuilder<Object> builder = OutgoingCloudEventMetadata.builder().withId(event.getId()).withSource(event.getSource()).withType(event.getType())
                .withSubject(event.getSubject())
                .withDataContentType(event.getDataContentType()).withDataSchema(event.getDataSchema()).withSpecVersion(event.getSpecVersion().toString())
                .withTimestamp(event.getTime().toZonedDateTime());
        for (String extName : event.getExtensionNames()) {
            builder.withExtension(extName, event.getExtension(extName));
        }
        return Optional.of(builder.build());
    }

    protected <T> Message<M> getMessage(DataEvent<T> event) throws IOException {
        if (getCloudEventMarshaller() != null) {
            return Message.of(getCloudEventMarshaller().marshall(event.asCloudEvent(getCloudEventMarshaller().cloudEventDataFactory())));
        } else {
            Optional<OutgoingCloudEventMetadata<?>> metadata = getMetadata(event);
            M payload = getEventMarshaller().marshall(event.getData());
            return metadata.isPresent() ? Message.of(payload, Metadata.of(metadata.orElseThrow())) : Message.of(payload);
        }
    }

}
