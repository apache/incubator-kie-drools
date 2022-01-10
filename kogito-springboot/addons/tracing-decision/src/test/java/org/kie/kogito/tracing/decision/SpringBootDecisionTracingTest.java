/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.util.Map;

public class SpringBootDecisionTracingTest extends BaseSpringBootDecisionTracingTest {

    private static final Map<String, Object> TEST_CONTEXT_VARIABLES = Map.of(
            "Driver", Map.of(
                    "Age", 25,
                    "Points", 10),
            "Violation", Map.of(
                    "Type", "speed",
                    "Actual Speed", 105,
                    "Speed Limit", 100));

    @Override
    protected String getTestModelName() {
        return "Traffic Violation";
    }

    @Override
    protected String getTestModelNameSpace() {
        return "https://github.com/kiegroup/drools/kie-dmn/_A4BCA8B8-CF08-433F-93B2-A2598F19ECFF";
    }

    @Override
    protected Map<String, Object> getContextVariables() {
        return TEST_CONTEXT_VARIABLES;
    }

    @Override
    protected int getEvaluationEventCount() {
        return 14;
    }
}
