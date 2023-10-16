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

import static org.kie.kogito.test.utils.ProcessInstancesRESTTestUtils.assertProcessInstanceExists;
import static org.kie.kogito.test.utils.ProcessInstancesRESTTestUtils.assertProcessInstanceHasFinished;
import static org.kie.kogito.test.utils.ProcessInstancesRESTTestUtils.newProcessInstanceAndGetId;

public abstract class BaseCallbackStateTimeoutsIT {

    private static final String CALLBACK_STATE_TIMEOUTS_SERVICE_URL = "/callback_state_timeouts";
    private static final String CALLBACK_STATE_TIMEOUTS_GET_BY_ID_URL = CALLBACK_STATE_TIMEOUTS_SERVICE_URL + "/{id}";
    private static final String NAME = "NAME";

    @Test
    void callbackStateTimeoutsExceeded() {
        // start a new process instance dnd collect the instance id.
        String processInput = buildProcessInput(NAME);
        String processInstanceId = newProcessInstanceAndGetId(CALLBACK_STATE_TIMEOUTS_SERVICE_URL, processInput);
        // assert the process instance is there
        assertProcessInstanceExists(CALLBACK_STATE_TIMEOUTS_GET_BY_ID_URL, processInstanceId);
        // do nothing more and wait until eventTimeout is fired and the process instance finalizes.
        assertProcessInstanceHasFinished(CALLBACK_STATE_TIMEOUTS_GET_BY_ID_URL, processInstanceId, 1, 10);
    }

    private static String buildProcessInput(String value) {
        return "{\"workflowdata\": {\"name\": \"" + value + "\"} }";
    }
}
