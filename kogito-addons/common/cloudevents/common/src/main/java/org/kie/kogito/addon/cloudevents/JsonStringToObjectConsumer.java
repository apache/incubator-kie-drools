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
package org.kie.kogito.addon.cloudevents;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonStringToObjectConsumer<T> implements Consumer<String> {

    private static final Logger logger = LoggerFactory.getLogger(JsonStringToObjectConsumer.class);
    private final Class<T> clazz;
    private final Consumer<T> realConsumer;
    private final ObjectMapper objectMapper;

    public JsonStringToObjectConsumer(ObjectMapper objectMapper, Consumer<T> realConsumer, Class<T> clazz) {
        this.objectMapper = objectMapper;
        this.realConsumer = realConsumer;
        this.clazz = clazz;
    }

    @Override
    public void accept(String value) {
        try {
            realConsumer.accept(objectMapper.readValue(value, clazz));
        } catch (JsonProcessingException e) {
            logger.info("Invalid payload {}", value, e);
        }
    }
}
