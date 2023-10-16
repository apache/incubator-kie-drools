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
package org.kie.kogito.it.jobs;

import org.junit.jupiter.api.Test;

import static org.kie.kogito.test.utils.ProcessInstancesRESTTestUtils.assertProcessInstanceHasFinished;
import static org.kie.kogito.test.utils.ProcessInstancesRESTTestUtils.newProcessInstanceAndGetId;

public abstract class BaseWorkflowTimeoutsIT {

    private static final String EMPTY_WORKFLOW_DATA = "{\"workflowdata\" : \"\"}";

    protected static final String WORKFLOW_TIMEOUTS_URL = "/workflow_timeouts";
    private static final String WORKFLOW_TIMEOUTS_GET_BY_ID_URL = WORKFLOW_TIMEOUTS_URL + "/{id}";

    @Test
    void workflowTimeoutExceeded() {
        // Start a new process instance.
        String processInstanceId = newProcessInstanceAndGetId(WORKFLOW_TIMEOUTS_URL, EMPTY_WORKFLOW_DATA);
        // Give enough time for the timeout to exceed.
        assertProcessInstanceHasFinished(WORKFLOW_TIMEOUTS_GET_BY_ID_URL, processInstanceId, 1, 180);
    }

}
