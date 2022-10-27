/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.kie.kogito.event.CloudEventMarshaller;
import org.kie.kogito.event.EventMarshaller;
import org.kie.kogito.event.impl.ByteArrayCloudEventMarshaller;
import org.kie.kogito.event.impl.ByteArrayEventMarshaller;
import org.kie.kogito.event.impl.NoOpCloudEventMarshaller;
import org.kie.kogito.event.impl.NoOpEventMarshaller;
import org.kie.kogito.event.impl.StringCloudEventMarshaller;
import org.kie.kogito.event.impl.StringEventMarshaller;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.arc.DefaultBean;

@ApplicationScoped
public class EventMarshallerProducer {

    @Inject
    ObjectMapper mapper;

    @Produces
    @DefaultBean
    public EventMarshaller<String> stringEventMarshaller() {
        return new StringEventMarshaller(mapper);
    }

    @Produces
    @DefaultBean
    public EventMarshaller<byte[]> byteArrayEventMarshaller() {
        return new ByteArrayEventMarshaller(mapper);
    }

    @Produces
    @DefaultBean
    public EventMarshaller<Object> defaultEventMarshaller() {
        return new NoOpEventMarshaller();
    }

    @Produces
    @DefaultBean
    public CloudEventMarshaller<String> stringCloudEventMarshaller() {
        return new StringCloudEventMarshaller(mapper);
    }

    @Produces
    @DefaultBean
    public CloudEventMarshaller<byte[]> byteArrayCloudEventMarshaller() {
        return new ByteArrayCloudEventMarshaller(mapper);
    }

    @Produces
    @DefaultBean
    public CloudEventMarshaller<Object> defaultCloudEventMarshaller() {
        return new NoOpCloudEventMarshaller(mapper);
    }
}
