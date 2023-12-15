/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.index.service.messaging;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceDataEvent;
import org.kie.kogito.index.service.IndexingService;
import org.kie.kogito.index.service.json.ProcessInstanceMetaMapper;
import org.kie.kogito.index.service.json.UserTaskInstanceMetaMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.quarkus.arc.Lock;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import static java.lang.String.format;

@ApplicationScoped
@Lock
public class DomainEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainEventConsumer.class);

    @ConfigProperty(name = "kogito.data-index.domain-indexing", defaultValue = "true")
    Boolean indexDomain;

    @Inject
    IndexingService indexingService;

    public void onDomainEvent(@Observes DataEvent<?> event) {
        if (!indexDomain) {
            return;
        }

        LOGGER.debug("Processing domain event: {}", event);
        indexingService.indexModel(getDomainData(event));
    }

    private ObjectNode getDomainData(DataEvent<?> event) {
        if (event instanceof ProcessInstanceDataEvent) {
            return new ProcessInstanceMetaMapper().apply((ProcessInstanceDataEvent<?>) event);
        }
        if (event instanceof UserTaskInstanceDataEvent) {
            return new UserTaskInstanceMetaMapper().apply((UserTaskInstanceDataEvent<?>) event);
        }
        throw new IllegalArgumentException(
                format("Unknown message type: '%s' for event class: '%s'", event.getType(), event.getClass().getName()));
    }
}
