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
package org.kie.kogito.jobs.messaging.quarkus;

import java.net.URI;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ReactiveMessagingJobsService extends AbstractReactiveMessagingJobsService {

    private static final String KOGITO_ADDON = "jobs-messaging";

    @Inject
    public ReactiveMessagingJobsService(
            @ConfigProperty(name = "kogito.service.url") URI serviceUrl,
            ObjectMapper objectMapper,
            @Channel(KOGITO_JOB_SERVICE_JOB_REQUEST_EVENTS_CHANNEL) Emitter<String> eventsEmitter) {
        super(serviceUrl, objectMapper, eventsEmitter);
    }

    @Override
    protected String getAddonName() {
        return KOGITO_ADDON;
    }
}
