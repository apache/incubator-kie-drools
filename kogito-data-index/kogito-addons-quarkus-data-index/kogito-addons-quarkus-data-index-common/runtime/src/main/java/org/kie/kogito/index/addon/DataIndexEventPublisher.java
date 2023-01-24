/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.addon;

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventPublisher;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.UserTaskInstanceDataEvent;
import org.kie.kogito.index.event.ProcessInstanceEventMapper;
import org.kie.kogito.index.event.UserTaskInstanceEventMapper;
import org.kie.kogito.index.service.IndexingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DataIndexEventPublisher implements EventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataIndexEventPublisher.class);

    @Inject
    IndexingService indexingService;

    @Override
    public void publish(DataEvent<?> event) {
        LOGGER.debug("Sending event to embedded data index: {}", event);
        switch (event.getType()) {
            case "ProcessInstanceEvent":
                indexingService.indexProcessInstance(new ProcessInstanceEventMapper().apply((ProcessInstanceDataEvent) event));
                break;
            case "UserTaskInstanceEvent":
                indexingService.indexUserTaskInstance(new UserTaskInstanceEventMapper().apply((UserTaskInstanceDataEvent) event));
                break;
            default:
                LOGGER.debug("Unknown type of event '{}', ignoring for this publisher", event.getType());
        }
    }

    @Override
    public void publish(Collection<DataEvent<?>> events) {
        events.forEach(this::publish);
    }

}
