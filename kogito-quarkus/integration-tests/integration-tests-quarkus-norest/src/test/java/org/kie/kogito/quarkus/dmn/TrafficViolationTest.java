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
package org.kie.kogito.quarkus.dmn;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNResult;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.decision.DecisionModels;

import io.quarkus.test.junit.QuarkusTest;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class TrafficViolationTest {

    @Inject
    DecisionModels decisionModels;

    @Test
    public void testEvaluateTrafficViolation() {
        DecisionModel trafficViolation = decisionModels
                .getDecisionModel("https://github.com/kiegroup/drools/kie-dmn/_A4BCA8B8-CF08-433F-93B2-A2598F19ECFF", "Traffic Violation");

        Map<String, Object> driver = new HashMap<>();
        driver.put("Points", 2);

        Map<String, Object> violation = new HashMap<>();
        violation.put("Type", "speed");
        violation.put("Actual Speed", 120);
        violation.put("Speed Limit", 100);

        Map<String, Object> context = new HashMap<>();
        context.put("Driver", driver);
        context.put("Violation", violation);

        DMNResult dmnResult = trafficViolation.evaluateAll(trafficViolation.newContext(context));

        assertThat(dmnResult.getDecisionResultByName("Should the driver be suspended?").getResult())
                .isEqualTo("No");
    }
}
