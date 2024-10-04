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
package org.kie.kogito.app.audit.api;

import java.util.List;
import java.util.ServiceLoader;

import org.kie.kogito.app.audit.spi.DataAuditStore;
import org.kie.kogito.event.job.JobInstanceDataEvent;
import org.kie.kogito.event.process.MultipleProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceErrorDataEvent;
import org.kie.kogito.event.process.ProcessInstanceNodeDataEvent;
import org.kie.kogito.event.process.ProcessInstanceSLADataEvent;
import org.kie.kogito.event.process.ProcessInstanceStateDataEvent;
import org.kie.kogito.event.process.ProcessInstanceVariableDataEvent;
import org.kie.kogito.event.usertask.MultipleUserTaskInstanceDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceAssignmentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceAttachmentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceCommentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceDeadlineDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceStateDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceVariableDataEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.kogito.app.audit.api.TypeCheck.typeCheckOf;

public class DataAuditStoreProxyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataAuditStoreProxyService.class);

    private DataAuditStore auditStoreService;

    private DataAuditStoreProxyService(DataAuditStore auditStoreService) {
        this.auditStoreService = auditStoreService;
    }

    public void storeProcessInstanceDataEvent(DataAuditContext context, ProcessInstanceDataEvent<?> event) {
        if (event instanceof MultipleProcessInstanceDataEvent) {
            ((MultipleProcessInstanceDataEvent) event).getData().forEach(e -> storeProcessInstanceDataEvent(context, e));
        } else {
            typeCheckOf(ProcessInstanceErrorDataEvent.class).ifType(context, event, auditStoreService::storeProcessInstanceDataEvent);
            typeCheckOf(ProcessInstanceNodeDataEvent.class).ifType(context, event, auditStoreService::storeProcessInstanceDataEvent);
            typeCheckOf(ProcessInstanceSLADataEvent.class).ifType(context, event, auditStoreService::storeProcessInstanceDataEvent);
            typeCheckOf(ProcessInstanceStateDataEvent.class).ifType(context, event, auditStoreService::storeProcessInstanceDataEvent);
            typeCheckOf(ProcessInstanceVariableDataEvent.class).ifType(context, event, auditStoreService::storeProcessInstanceDataEvent);
        }

    }

    public void storeUserTaskInstanceDataEvent(DataAuditContext context, UserTaskInstanceDataEvent<?> event) {
        if (event instanceof MultipleUserTaskInstanceDataEvent) {
            ((MultipleUserTaskInstanceDataEvent) event).getData().forEach(e -> storeUserTaskInstanceDataEvent(context, e));
        } else {
            typeCheckOf(UserTaskInstanceAssignmentDataEvent.class).ifType(context, event, auditStoreService::storeUserTaskInstanceDataEvent);
            typeCheckOf(UserTaskInstanceAttachmentDataEvent.class).ifType(context, event, auditStoreService::storeUserTaskInstanceDataEvent);
            typeCheckOf(UserTaskInstanceCommentDataEvent.class).ifType(context, event, auditStoreService::storeUserTaskInstanceDataEvent);
            typeCheckOf(UserTaskInstanceDeadlineDataEvent.class).ifType(context, event, auditStoreService::storeUserTaskInstanceDataEvent);
            typeCheckOf(UserTaskInstanceStateDataEvent.class).ifType(context, event, auditStoreService::storeUserTaskInstanceDataEvent);
            typeCheckOf(UserTaskInstanceVariableDataEvent.class).ifType(context, event, auditStoreService::storeUserTaskInstanceDataEvent);
        }

    }

    public void storeJobDataEvent(DataAuditContext context, JobInstanceDataEvent event) {
        auditStoreService.storeJobDataEvent(context, event);

    }

    public static DataAuditStoreProxyService newAuditStoreService() {
        DataAuditStore service = ServiceLoader.load(DataAuditStore.class).findFirst().orElseThrow(() -> new RuntimeException("DataAuditStore implementation not found"));
        LOGGER.debug("Creating new Data Audit Store proxy service with {}", service);
        return new DataAuditStoreProxyService(service);
    }

    public void storeQuery(DataAuditContext newDataAuditContext, DataAuditQuery dataAuditQuery) {
        LOGGER.info("Store query {}", dataAuditQuery);
        auditStoreService.storeQuery(newDataAuditContext, dataAuditQuery);
    }

    public List<DataAuditQuery> findQueries(DataAuditContext context) {
        return auditStoreService.findQueries(context);
    }
}
