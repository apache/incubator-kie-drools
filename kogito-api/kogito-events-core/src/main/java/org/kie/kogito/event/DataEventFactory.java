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
package org.kie.kogito.event;

import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.kie.kogito.event.cloudevents.extension.ProcessMeta;
import org.kie.kogito.event.impl.CloudEventWrapDataEvent;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.CloudEventExtension;
import io.cloudevents.SpecVersion;

public class DataEventFactory {

    public static <T> DataEvent<T> from(T event) {
        return new ProcessInstanceDataEvent<>(event);
    }

    public static <T> DataEvent<T> from(CloudEvent event, Converter<CloudEventData, T> dataUnmarshaller) {
        return new CloudEventWrapDataEvent<>(event, dataUnmarshaller);
    }

    public static <T extends AbstractDataEvent<V>, V> T from(T dataEvent, CloudEvent cloudEvent, Converter<CloudEventData, V> dataUnmarshaller) throws IOException {
        dataEvent.setSpecVersion(cloudEvent.getSpecVersion());
        dataEvent.setId(cloudEvent.getId());
        dataEvent.setType(cloudEvent.getType());
        dataEvent.setSource(cloudEvent.getSource());
        dataEvent.setDataContentType(cloudEvent.getDataContentType());
        dataEvent.setDataSchema(cloudEvent.getDataSchema());
        dataEvent.setSubject(cloudEvent.getSubject());
        dataEvent.setTime(cloudEvent.getTime());
        cloudEvent.getExtensionNames().forEach(extensionName -> dataEvent.addExtensionAttribute(extensionName, cloudEvent.getExtension(extensionName)));
        if (cloudEvent.getData() != null) {
            dataEvent.setData(dataUnmarshaller.convert(cloudEvent.getData()));
        }
        return dataEvent;
    }

    public static <T> DataEvent<T> from(T eventData, String trigger, KogitoProcessInstance pi) {
        return from(eventData, trigger, pi, Collections.emptyMap());
    }

    public static <T> DataEvent<T> from(T eventData, String trigger, KogitoProcessInstance pi, Map<String, Object> contextAttributes) {
        AbstractDataEvent<T> ce = (AbstractDataEvent<T>) from(eventData, trigger, URI.create("/process/" + pi.getProcessId()), Optional.empty(), ProcessMeta.fromKogitoProcessInstance(pi));
        if (contextAttributes != null) {
            contextAttributes.forEach((k, v) -> ce.addExtensionAttribute(k, v));
        }
        return ce;
    }

    public static <T> DataEvent<T> from(T eventData, String type, URI source, Optional<String> subject, CloudEventExtension... extensions) {
        ProcessInstanceDataEvent<T> ce = new ProcessInstanceDataEvent<>(eventData);
        ce.setSpecVersion(SpecVersion.V1);
        ce.setId(UUID.randomUUID().toString());
        ce.setType(type);
        ce.setSource(source);
        ce.setTime(OffsetDateTime.now());
        subject.ifPresent(ce::setSubject);
        for (CloudEventExtension extension : extensions) {
            for (String key : extension.getKeys()) {
                ce.addExtensionAttribute(key, extension.getValue(key));
            }
        }
        return ce;
    }

    private DataEventFactory() {
    }
}
