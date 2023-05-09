/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.it.jobs;

import org.junit.jupiter.api.Test;

import io.restassured.path.json.JsonPath;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.test.utils.ProcessInstancesRESTTestUtils.assertProcessInstanceHasFinished;
import static org.kie.kogito.test.utils.ProcessInstancesRESTTestUtils.newProcessInstanceAndGetId;

public abstract class BaseSwitchStateTimeoutsIT {

    protected static final String SWITCH_STATE_TIMEOUTS_URL = "/switch_state_timeouts";
    private static final String SWITCH_STATE_TIMEOUTS_GET_BY_ID_URL = SWITCH_STATE_TIMEOUTS_URL + "/{id}";

    private static final String EVENT_DECISION_PATH = "data.decision";
    private static final String EVENT_PROCESS_INSTANCE_ID_PATH = "kogitoprocinstanceid";
    private static final String EVENT_TYPE_PATH = "type";

    protected static final String DECISION_NO_DECISION = "NoDecision";

    protected static final String PROCESS_RESULT_EVENT_TYPE = "process_result_event";

    private static final String EMPTY_WORKFLOW_DATA = "{\"workflowdata\" : \"\"}";

    @Test
    void switchStateEventConditionTimeoutsTransitionTimeoutsExceeded() throws Exception {
        // Start a new process instance.
        String processInstanceId = newProcessInstanceAndGetId(SWITCH_STATE_TIMEOUTS_URL, EMPTY_WORKFLOW_DATA);
        // Give enough time for the timeout to exceed.
        assertProcessInstanceHasFinished(SWITCH_STATE_TIMEOUTS_GET_BY_ID_URL, processInstanceId, 1, 180);
        // When the process has finished in the default case a no decision event must have been produced.
        verifyNoDecisionEventWasProduced(processInstanceId);
    }

    protected abstract void verifyNoDecisionEventWasProduced(String processInstanceId) throws Exception;

    protected static void assertDecisionEvent(JsonPath cloudEventJsonPath,
            String expectedProcessInstanceId,
            String expectedEventType,
            String expectedDecision) {
        assertThat(cloudEventJsonPath.getString(EVENT_PROCESS_INSTANCE_ID_PATH)).isEqualTo(expectedProcessInstanceId);
        assertThat(cloudEventJsonPath.getString(EVENT_TYPE_PATH)).isEqualTo(expectedEventType);
        assertThat(cloudEventJsonPath.getString(EVENT_DECISION_PATH)).isEqualTo(expectedDecision);
    }
}
