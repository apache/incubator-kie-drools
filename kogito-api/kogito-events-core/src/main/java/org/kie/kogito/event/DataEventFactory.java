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
package org.kie.kogito.event;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.kie.kogito.event.cloudevents.extension.ProcessMeta;
import org.kie.kogito.event.impl.CloudEventWrapDataEvent;
import org.kie.kogito.event.process.ProcessDataEvent;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.CloudEventExtension;
import io.cloudevents.SpecVersion;

public class DataEventFactory {

    public static <T> DataEvent<T> from(T event) {
        return new ProcessDataEvent<>(event);
    }

    public static <T> DataEvent<T> from(CloudEvent event, Converter<CloudEventData, T> dataUnmarshaller) {
        return new CloudEventWrapDataEvent<>(event, dataUnmarshaller);
    }

    public static <T> DataEvent<T> from(T eventData, String trigger, KogitoProcessInstance pi) {
        return from(eventData, trigger, URI.create("/process/" + pi.getProcessId()), Optional.empty(), ProcessMeta.fromKogitoProcessInstance(pi));
    }

    public static <T> DataEvent<T> from(T eventData, String type, URI source, Optional<String> subject, CloudEventExtension... extensions) {
        ProcessDataEvent<T> ce = new ProcessDataEvent<>(eventData);
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
