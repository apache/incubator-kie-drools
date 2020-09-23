/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import org.kie.kogito.Model;
import org.kie.kogito.services.event.AbstractProcessDataEvent;
import org.kie.kogito.services.event.EventConsumer;
import org.kie.kogito.services.event.EventConsumerFactory;

public class DefaultEventConsumerFactory implements EventConsumerFactory {

    private ObjectMapper mapper;

    public DefaultEventConsumerFactory() {
        this(null);
    }

    public DefaultEventConsumerFactory(ObjectMapper mapper) {
        if(mapper == null) {
            this.mapper = new ObjectMapper().setDateFormat(new StdDateFormat().withColonInTimeZone(true).withTimeZone(TimeZone.getDefault()));
        } else {
            this.mapper = mapper;
        }
    }

    public <M extends Model, D, T extends AbstractProcessDataEvent<D>> EventConsumer<M> get(Function<D, M> function,
            Class<D> dataEventClass, Class<T> cloudEventClass, Optional<Boolean> cloudEvents) {
        return cloudEvents.orElse(true)
                ? new CloudEventConsumer<>(function, cloudEventClass, mapper)
                : new DataEventConsumer<>(function, dataEventClass, mapper);
    }

}
