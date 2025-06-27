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
package org.kie.kogito.serverless.workflow.executor.events;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.kogito.event.EventEmitter;
import org.kie.kogito.event.EventEmitterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemoryEventEmitterFactory implements EventEmitterFactory {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryEventEmitterFactory.class);

    private Map<String, EventEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public EventEmitter apply(String trigger) {
        return emitters.computeIfAbsent(trigger, t -> {
            CloudEventReceiver eventReceiver = InMemoryEventShared.INSTANCE.receivers().get(trigger);
            if (eventReceiver != null) {
                return new InMemoryEventEmitter(eventReceiver);
            } else {
                logger.warn("Unregisterd event type {}", trigger);
                return null;
            }
        });

    }

    // lower priority
    public int ordinal() {
        return Integer.MAX_VALUE;
    }
}
