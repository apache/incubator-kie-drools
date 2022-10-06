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
package org.kie.kogito.event.impl;

import org.kie.kogito.event.CloudEventUnmarshaller;
import org.kie.kogito.event.Converter;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;

public class DefaultCloudEventUnmarshaller<I, O> implements CloudEventUnmarshaller<I, O> {

    private Converter<I, CloudEvent> cloudEventConverter;
    private Converter<I, CloudEventData> cloudEventDataConverter;
    private Converter<CloudEventData, O> dataConverter;

    public DefaultCloudEventUnmarshaller(Converter<I, CloudEvent> cloudEventConverter, Converter<CloudEventData, O> dataConverter, Converter<I, CloudEventData> cloudEventDataConverter) {
        this.dataConverter = dataConverter;
        this.cloudEventConverter = cloudEventConverter;
        this.cloudEventDataConverter = cloudEventDataConverter;
    }

    @Override
    public Converter<I, CloudEvent> cloudEvent() {
        return cloudEventConverter;
    }

    @Override
    public Converter<CloudEventData, O> data() {
        return dataConverter;
    }

    @Override
    public Converter<I, CloudEventData> binaryCloudEvent() {
        return cloudEventDataConverter;
    }
}
