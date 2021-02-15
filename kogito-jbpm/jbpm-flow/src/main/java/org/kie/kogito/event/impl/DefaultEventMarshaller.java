/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.util.Optional;
import java.util.TimeZone;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import org.kie.kogito.services.event.AbstractProcessDataEvent;
import org.kie.kogito.services.event.EventMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultEventMarshaller implements EventMarshaller {

    private static final Logger logger = LoggerFactory.getLogger(DefaultEventMarshaller.class);

    private final ObjectMapper mapper;

    public DefaultEventMarshaller() {
        this(null);
    }

    public DefaultEventMarshaller(ObjectMapper mapper) {
        if(mapper == null) {
            this.mapper = new ObjectMapper().setDateFormat(new StdDateFormat().withColonInTimeZone(true).withTimeZone(TimeZone.getDefault()));
        } else {
            this.mapper = mapper;
        }
    }

    @Override
    public <T, P extends AbstractProcessDataEvent<T>> String marshall(T dataEvent,
                                                                      Function<T, P> cloudFunction,
                                                                      Optional<Boolean> isCloudEvent) {
        Object event = isCloudEvent.orElse(true) ? cloudFunction.apply(dataEvent) : dataEvent;
        logger.debug("Marshalling event {}", event);
        try {
            return mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            logger.error("Error marshalling event {}", event);
            throw new IllegalStateException(e);
        }
    }

}
