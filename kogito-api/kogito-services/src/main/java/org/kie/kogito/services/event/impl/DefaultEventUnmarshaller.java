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
package org.kie.kogito.services.event.impl;

import java.io.IOException;
import java.util.Objects;

import org.kie.kogito.event.EventUnmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DefaultEventUnmarshaller implements EventUnmarshaller<Object> {

    private static final Logger logger = LoggerFactory.getLogger(DefaultEventUnmarshaller.class);
    private final ObjectMapper objectMapper;

    public DefaultEventUnmarshaller(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> T unmarshall(Object value, Class<T> clazz, Class<?>... parametrizedClasses) throws IOException {
        logger.debug("Converting event with payload {} to class {} ", value, clazz);
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        final JavaType type = Objects.isNull(parametrizedClasses) ? objectMapper.getTypeFactory().constructType(clazz)
                : objectMapper.getTypeFactory().constructParametricType(clazz, parametrizedClasses);
        if (value instanceof byte[]) {
            return objectMapper.readValue((byte[]) value, type);
        } else {
            return objectMapper.readValue(value.toString(), type);
        }
    }
}
