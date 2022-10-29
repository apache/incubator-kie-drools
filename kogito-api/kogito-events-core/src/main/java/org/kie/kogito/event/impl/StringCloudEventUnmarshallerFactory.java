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
import org.kie.kogito.event.CloudEventUnmarshallerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class StringCloudEventUnmarshallerFactory implements CloudEventUnmarshallerFactory<String> {

    private final ObjectMapper objectMapper;

    public StringCloudEventUnmarshallerFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public <S> CloudEventUnmarshaller<String, S> unmarshaller(Class<S> targetClass) {
        return new DefaultCloudEventUnmarshaller<>(new StringCloudEventConverter(objectMapper),
                new JacksonCloudEventDataConverter<>(objectMapper, targetClass),
                new String2JsonCloudEventDataConverter());
    }
}
