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

import io.restassured.path.json.JsonPath;

import static org.assertj.core.api.Assertions.assertThat;

abstract class AbstractSwitchStateIT {

    protected static final String DECISION_APPROVED = "Approved";
    protected static final String DECISION_DENIED = "Denied";
    protected static final String DECISION_INVALIDATED = "Invalidated";
    protected static final String DECISION_NO_DECISION = "NoDecision";

    private static final String DECISION_PATH = "workflowdata.decision";

    protected static void assertDecision(JsonPath jsonPath, String expectedDecision) {
        String currentDecision = jsonPath.get(DECISION_PATH);
        assertThat(currentDecision).isEqualTo(expectedDecision);
    }

    protected static String buildProcessInput(int age) {
        return "{\"workflowdata\": {\"age\": " + age + "} }";
    }
}
