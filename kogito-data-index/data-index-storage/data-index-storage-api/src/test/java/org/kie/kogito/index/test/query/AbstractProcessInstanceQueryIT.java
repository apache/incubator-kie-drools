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
import org.kie.kogito.event.process.ProcessInstanceStateDataEvent;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.storage.ProcessInstanceStorage;
import org.kie.kogito.index.test.QueryTestBase;
import org.kie.kogito.index.test.TestUtils;

import static java.util.Collections.singletonList;
import static org.kie.kogito.index.model.ProcessInstanceState.COMPLETED;
import static org.kie.kogito.index.test.QueryTestUtils.assertWithId;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.equalTo;

public abstract class AbstractProcessInstanceQueryIT extends QueryTestBase<String, ProcessInstance> {

    public abstract ProcessInstanceStorage getStorage();

    @Test
    void testProcessInstanceQuery() {
        String processId = "travels";
        String processInstanceId = UUID.randomUUID().toString();
        String subProcessId = processId + "_sub";
        String subProcessInstanceId = UUID.randomUUID().toString();
        ProcessInstanceStateDataEvent processInstanceEvent = TestUtils.createProcessInstanceEvent(processInstanceId, processId, subProcessInstanceId, subProcessId, COMPLETED.ordinal());
        ProcessInstanceStorage storage = getStorage();
        storage.indexState(processInstanceEvent);
        queryAndAssert(assertWithId(), storage, singletonList(equalTo("state", COMPLETED.ordinal())), null, null, null,
                processInstanceId);
    }

    @Test
    void testProcessRetriggerQuery() {
        String processId = "no_retrigger";
        String processInstanceId = UUID.randomUUID().toString();
        String subProcessId = processId + "_sub";
        String subProcessInstanceId = UUID.randomUUID().toString();
        ProcessInstanceStorage storage = getStorage();
        storage.indexState(TestUtils.createProcessInstanceEvent(processInstanceId, processId, subProcessId, subProcessInstanceId, COMPLETED.ordinal()));
        storage.indexNode(TestUtils.createProcessInstanceNodeDataEvent(processInstanceId, processId, "1", "1", "Javierito", "type", 1));
        storage.indexError(TestUtils.createProcessInstanceErrorDataEvent(processInstanceId, processId, "1", "kkdevaca", "1", "1"));
        queryAndAssert(assertWithId(), storage, singletonList(equalTo("nodes.retrigger", false)), null, null, null, processInstanceId);
    }
}
