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

import org.kie.kogito.event.EventMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StringEventMarshaller implements EventMarshaller<String> {

    private static final Logger logger = LoggerFactory.getLogger(StringEventMarshaller.class);

    private final ObjectMapper mapper;

    public StringEventMarshaller(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public <T> String marshall(T event) {
        logger.debug("Marshalling event {}", event);
        try {
            return mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            logger.error("Error marshalling event {}", event);
            throw new IllegalStateException(e);
        }
    }
}
