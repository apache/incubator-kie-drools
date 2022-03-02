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
package org.kie.kogito.addon.quarkus.messaging.common;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.kie.kogito.addon.cloudevents.converters.ProcessDataJsonNodeConverter;
import org.kie.kogito.services.event.ProcessDataEventConverter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.arc.DefaultBean;

@ApplicationScoped
public class ProcessDataEventConverterProducer {

    @Inject
    ObjectMapper mapper;

    @Produces
    @DefaultBean
    public ProcessDataEventConverter<JsonNode> jsonNodeConverter() {
        return new ProcessDataJsonNodeConverter(mapper);
    }

}
