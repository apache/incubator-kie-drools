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

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEventData;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.jackson.JsonCloudEventData;
import io.cloudevents.jackson.PojoCloudEventDataMapper;

public class POJOCloudEventDataConverter<O> extends AbstractCloudEventDataConverter<O> {

    private ObjectMapper objectMapper;
    private Class<O> outputClass;

    public POJOCloudEventDataConverter(ObjectMapper objectMapper, Class<O> outputClass) {
        this.objectMapper = objectMapper;
        this.outputClass = outputClass;
    }

    @Override
    protected O toValue(CloudEventData value) throws IOException {
        if (value instanceof PojoCloudEventData) {
            return PojoCloudEventDataMapper.from(objectMapper, outputClass).map(value).getValue();
        } else if (value instanceof JsonCloudEventData) {
            return objectMapper.convertValue(((JsonCloudEventData) value).getNode(), outputClass);
        } else {
            return objectMapper.readValue(value.toBytes(), outputClass);
        }
    }
}
