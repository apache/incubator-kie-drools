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
package org.kie.kogito.app.audit.spi;

import java.util.List;

import org.kie.kogito.app.audit.api.DataAuditContext;
import org.kie.kogito.app.audit.api.DataAuditQuery;
import org.kie.kogito.event.job.JobInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceErrorDataEvent;
import org.kie.kogito.event.process.ProcessInstanceNodeDataEvent;
import org.kie.kogito.event.process.ProcessInstanceSLADataEvent;
import org.kie.kogito.event.process.ProcessInstanceStateDataEvent;
import org.kie.kogito.event.process.ProcessInstanceVariableDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceAssignmentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceAttachmentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceCommentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceDeadlineDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceStateDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceVariableDataEvent;

public interface DataAuditStore {

    void storeProcessInstanceDataEvent(DataAuditContext context, ProcessInstanceErrorDataEvent event);

    void storeProcessInstanceDataEvent(DataAuditContext context, ProcessInstanceNodeDataEvent event);

    void storeProcessInstanceDataEvent(DataAuditContext context, ProcessInstanceSLADataEvent event);

    void storeProcessInstanceDataEvent(DataAuditContext context, ProcessInstanceStateDataEvent event);

    void storeProcessInstanceDataEvent(DataAuditContext context, ProcessInstanceVariableDataEvent event);

    void storeUserTaskInstanceDataEvent(DataAuditContext context, UserTaskInstanceAssignmentDataEvent event);

    void storeUserTaskInstanceDataEvent(DataAuditContext context, UserTaskInstanceAttachmentDataEvent event);

    void storeUserTaskInstanceDataEvent(DataAuditContext context, UserTaskInstanceCommentDataEvent event);

    void storeUserTaskInstanceDataEvent(DataAuditContext context, UserTaskInstanceDeadlineDataEvent event);

    void storeUserTaskInstanceDataEvent(DataAuditContext context, UserTaskInstanceStateDataEvent event);

    void storeUserTaskInstanceDataEvent(DataAuditContext context, UserTaskInstanceVariableDataEvent event);

    void storeJobDataEvent(DataAuditContext context, JobInstanceDataEvent event);

    void storeQuery(DataAuditContext context, DataAuditQuery dataAuditQuery);

    List<DataAuditQuery> findQueries(DataAuditContext context);

}
