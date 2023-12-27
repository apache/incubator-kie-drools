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

import java.util.Collection;

import org.kie.kogito.app.audit.api.DataAuditStoreProxyService;
import org.kie.kogito.app.audit.spi.DataAuditContextFactory;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventPublisher;
import org.kie.kogito.event.job.JobInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceDataEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

@ApplicationScoped
public class QuarkusJPADataAuditEventPublisher implements EventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuarkusJPADataAuditEventPublisher.class);

    private DataAuditStoreProxyService proxy;

    @Inject
    DataAuditContextFactory dataAuditContextFactory;

    public QuarkusJPADataAuditEventPublisher() {
        proxy = DataAuditStoreProxyService.newAuditStoreService();
    }

    @Override
    @Transactional(value = TxType.REQUIRED)
    public void publish(Collection<DataEvent<?>> events) {
        events.forEach(this::publish);
    }

    @Override
    @Transactional(value = TxType.REQUIRED)
    public void publish(DataEvent<?> event) {

        if (event instanceof ProcessInstanceDataEvent) {
            LOGGER.debug("Processing process instance event {}", event);
            proxy.storeProcessInstanceDataEvent(dataAuditContextFactory.newDataAuditContext(), (ProcessInstanceDataEvent<?>) event);
            return;
        } else if (event instanceof UserTaskInstanceDataEvent) {
            LOGGER.debug("Processing user task instacne event {}", event);
            proxy.storeUserTaskInstanceDataEvent(dataAuditContextFactory.newDataAuditContext(), (UserTaskInstanceDataEvent<?>) event);
            return;
        } else if (event instanceof JobInstanceDataEvent) {
            LOGGER.debug("Processing job instance event {}", event);
            proxy.storeJobDataEvent(dataAuditContextFactory.newDataAuditContext(), (JobInstanceDataEvent) event);
            return;
        }

        LOGGER.info("Discard event {} as class {} is not supported by this", event, event.getClass().getName());
    }

}
