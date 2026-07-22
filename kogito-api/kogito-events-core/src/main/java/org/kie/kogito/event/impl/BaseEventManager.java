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
package org.kie.kogito.event.impl;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.kie.kogito.Addons;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventBatch;
import org.kie.kogito.event.EventManager;
import org.kie.kogito.event.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseEventManager implements EventManager {

    private static final Logger logger = LoggerFactory.getLogger(BaseEventManager.class);

    private String service;
    private Addons addons;
    private Set<EventPublisher> publishers = new LinkedHashSet<>();

    @Override
    public EventBatch newBatch() {
        return new DefaultInstanceEventBatch(service, addons);
    }

    @Override
    public void publish(EventBatch batch) {
        if (publishers.isEmpty()) {
            // don't even process the batch if there are no publishers
            return;
        }
        Collection<DataEvent<?>> events = batch.events();

        for (DataEvent<?> event : events) {
            logger.debug("publishing {}", event);
        }

        publishers.forEach(p -> p.publish(events));
    }

    @Override
    public void addPublisher(EventPublisher publisher) {
        this.publishers.add(publisher);
    }

    @Override
    public void setService(String service) {
        this.service = service;
    }

    @Override
    public void setAddons(Addons addons) {
        this.addons = addons;
    }

}
