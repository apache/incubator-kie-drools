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
package org.kie.kogito.tracing.decision;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.Map;

public class QuarkusDecisionTracingOneOfEachTypeTest extends BaseQuarkusDecisionTracingTest {

    private static final Map<String, Object> TEST_CONTEXT_VARIABLES = Map.of(
            "InputBoolean", true,
            "InputDate", LocalDate.of(2020, 1, 4),
            "InputDTDuration", Period.ofDays(1),
            "InputDateAndTime", LocalDateTime.of(2020, 1, 4, 16, 30),
            "InputNumber", 1,
            "InputString", "John Doe",
            "InputTime", LocalTime.of(16, 30),
            "InputYMDuration", Period.ofMonths(1));

    @Override
    protected String getTestModelName() {
        return "OneOfEachType";
    }

    @Override
    protected String getTestModelNameSpace() {
        return "http://www.trisotech.com/definitions/_4f5608e9-4d74-4c22-a47e-ab657257fc9c";
    }

    @Override
    protected Map<String, Object> getContextVariables() {
        return TEST_CONTEXT_VARIABLES;
    }

    @Override
    protected int getEvaluationEventCount() {
        return 18;
    }
}
