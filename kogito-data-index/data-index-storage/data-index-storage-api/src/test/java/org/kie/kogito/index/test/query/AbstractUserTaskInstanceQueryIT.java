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
package org.kie.kogito.index.test.query;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.kie.kogito.event.usertask.UserTaskInstanceStateDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceStateEventBody;
import org.kie.kogito.index.model.UserTaskInstance;
import org.kie.kogito.index.storage.UserTaskInstanceStorage;
import org.kie.kogito.index.test.QueryTestBase;

import static java.util.Collections.singletonList;
import static org.kie.kogito.index.test.QueryTestUtils.assertWithId;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.equalTo;

public abstract class AbstractUserTaskInstanceQueryIT extends QueryTestBase<String, UserTaskInstance> {

    public abstract UserTaskInstanceStorage getStorage();

    @Test
    void testUserTaskInstanceQuery() {
        String taskId = UUID.randomUUID().toString();
        String processInstanceId = UUID.randomUUID().toString();
        UserTaskInstanceStorage storage = getStorage();
        UserTaskInstanceStateDataEvent event = new UserTaskInstanceStateDataEvent();
        event.setKogitoProcessInstanceId(processInstanceId);
        event.setKogitoUserTaskInstanceId(taskId);
        event.setData(UserTaskInstanceStateEventBody.create().processInstanceId(processInstanceId).state("InProgress").userTaskInstanceId(taskId).build());
        storage.indexState(event);
        queryAndAssert(assertWithId(), storage, singletonList(equalTo("state", "InProgress")), null, null, null, taskId);
    }
}
