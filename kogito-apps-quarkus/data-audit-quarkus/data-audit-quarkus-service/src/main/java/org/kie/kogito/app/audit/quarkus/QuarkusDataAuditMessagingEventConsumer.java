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
package org.kie.kogito.app.audit.quarkus;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.kie.kogito.event.EventPublisher;
import org.kie.kogito.event.job.JobInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceDataEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.common.annotation.Blocking;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import static org.kie.kogito.app.audit.api.SubsystemConstants.KOGITO_JOBS_EVENTS;
import static org.kie.kogito.app.audit.api.SubsystemConstants.KOGITO_PROCESSINSTANCES_EVENTS;
import static org.kie.kogito.app.audit.api.SubsystemConstants.KOGITO_USERTASKINSTANCES_EVENTS;

@ApplicationScoped
public class QuarkusDataAuditMessagingEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuarkusDataAuditMessagingEventConsumer.class);

    @Inject
    EventPublisher eventPublisher;

    @Incoming(KOGITO_PROCESSINSTANCES_EVENTS)
    @Blocking
    @Transactional
    public void onProcessInstanceEvent(ProcessInstanceDataEvent<?> event) {
        LOGGER.debug("Process instance consumer received ProcessInstanceDataEvent: \n{}", event);
        eventPublisher.publish(event);
    }

    @Incoming(KOGITO_USERTASKINSTANCES_EVENTS)
    @Blocking
    @Transactional
    public void onUserTaskInstanceEvent(UserTaskInstanceDataEvent<?> event) {
        LOGGER.debug("Task instance received UserTaskInstanceDataEvent \n{}", event);
        eventPublisher.publish(event);
    }

    @Incoming(KOGITO_JOBS_EVENTS)
    @Blocking
    @Transactional
    public void onJobEvent(JobInstanceDataEvent event) {
        LOGGER.debug("Job received KogitoJobCloudEvent \n{}", event);
        eventPublisher.publish(event);
    }

}
