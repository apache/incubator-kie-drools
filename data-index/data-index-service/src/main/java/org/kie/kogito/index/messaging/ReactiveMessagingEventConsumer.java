/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates. 
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
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.kie.kogito.index.event.KogitoCloudEvent;
import org.kie.kogito.index.service.IndexingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ReactiveMessagingEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveMessagingEventConsumer.class);
    private static final String TOPIC = "kogito-processinstances-events";
    
    @Inject
    IndexingService indexingService;

    @Incoming(TOPIC)
    public void onProcessInstanceEvent(KogitoCloudEvent event) {
        try {
            LOGGER.debug("Received KogitoCloudEvent \n{}", event);
            indexingService.indexProcessInstance(event);
            indexingService.indexProcessInstanceModel(event);    
        } catch (Throwable t){
            LOGGER.error("Error processing KogitoCloudEvent: {}", t.getMessage(), t);   
        }
    }
}
