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

package org.kie.kogito.quarkus.workflows;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.path.json.JsonPath;

import static org.kie.kogito.test.utils.ProcessInstancesRESTTestUtils.newProcessInstance;

@QuarkusIntegrationTest
class SwitchStateDataConditionBasedIT extends AbstractSwitchStateIT {

    private static final String SWITCH_STATE_DATA_CONDITION_TRANSITION_SERVICE_URL = "/switch_state_data_condition_transition";
    private static final String SWITCH_STATE_DATA_CONDITION_END_URL = "/switch_state_data_condition_end";
    private static final String DECISION_END_DECISION = "EndDecision";

    @Test
    void switchStateDataConditionTransitionApproved() {
        // Start a new process instance that must be "Approved" and check the result.
        JsonPath result = newProcessInstance(SWITCH_STATE_DATA_CONDITION_TRANSITION_SERVICE_URL, buildProcessInput(20));
        assertDecision(result, DECISION_APPROVED);
    }

    @Test
    void switchStateDataConditionTransitionDenied() {
        // Start a new process instance that must be "Denied" and check the result.
        JsonPath result = newProcessInstance(SWITCH_STATE_DATA_CONDITION_TRANSITION_SERVICE_URL, buildProcessInput(10));
        assertDecision(result, DECISION_DENIED);
    }

    @Test
    void switchStateDefaultConditionTransition() {
        // Start a new process instance that must go through the default condition check the result.
        JsonPath result = newProcessInstance(SWITCH_STATE_DATA_CONDITION_TRANSITION_SERVICE_URL, buildProcessInput(-20));
        assertDecision(result, DECISION_DENIED);
    }

    @Test
    void switchStateDataConditionEndApproved() {
        switchStateDataConditionEnd(20);
    }

    @Test
    void switchStateDataConditionEndDenied() {
        switchStateDataConditionEnd(10);
    }

    @Test
    void switchStateDefaultConditionEnd() {
        switchStateDataConditionEnd(-20);
    }

    private void switchStateDataConditionEnd(int age) {
        JsonPath result = newProcessInstance(SWITCH_STATE_DATA_CONDITION_END_URL, buildProcessInput(age));
        assertDecision(result, DECISION_END_DECISION);
    }
}
