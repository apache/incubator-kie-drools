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

package org.kie.kogito.index.messaging;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.index.event.KogitoCloudEvent;
import org.kie.kogito.index.event.KogitoProcessCloudEvent;
import org.kie.kogito.index.event.KogitoUserTaskCloudEvent;
import org.kie.kogito.index.json.ProcessInstanceMetaMapper;
import org.kie.kogito.index.json.UserTaskInstanceMetaMapper;
import org.kie.kogito.index.service.IndexingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.quarkus.arc.Lock;

import static java.lang.String.format;

@ApplicationScoped
@Lock
public class DomainEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainEventConsumer.class);

    @ConfigProperty(name = "kogito.domain.indexing", defaultValue = "true")
    Boolean indexDomain;

    @Inject
    IndexingService indexingService;

    public void onDomainEvent(@Observes KogitoCloudEvent event) {
        if (!indexDomain) {
            return;
        }

        LOGGER.debug("Processing domain event: {}", event);
        indexingService.indexModel(getDomainData(event));
    }

    private ObjectNode getDomainData(KogitoCloudEvent event) {
        if (event instanceof KogitoProcessCloudEvent) {
            return new ProcessInstanceMetaMapper().apply((KogitoProcessCloudEvent) event);
        }
        if (event instanceof KogitoUserTaskCloudEvent) {
            return new UserTaskInstanceMetaMapper().apply((KogitoUserTaskCloudEvent) event);
        }
        throw new IllegalArgumentException(
                format("Unknown message type: '%s' for event class: '%s'", event.getType(), event.getClass().getName()));
    }
}
