/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.monitoring.core.integration;

import java.util.stream.IntStream;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.monitoring.core.system.metrics.dmnhandlers.DecisionConstants;
import org.kie.kogito.monitoring.core.system.metrics.dmnhandlers.StringHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringHandlerTest {

    private static final String ENDPOINT_NAME = "hello";
    private static final String DECISION_NAME = "decision";
    SimpleMeterRegistry registry;
    StringHandler handler;
    String dmnType;

    @BeforeEach
    public void setUp() {
        dmnType = "string";
        registry = new SimpleMeterRegistry();
        handler = new StringHandler(dmnType, registry);
    }

    @AfterEach
    public void destroy() {
        registry.clear();
    }

    @Test
    public void givenSomeStringMetricsWhenMetricsAreStoredThenTheCountIsCorrect() {
        // Arrange
        Double expectedCountStringA = 3.0;
        Double expectedCountStringB = 2.0;
        Double expectedCountStringC = 5.0;

        // Act
        IntStream.rangeClosed(1, 3).forEach(x -> handler.record(DECISION_NAME, ENDPOINT_NAME, "A"));
        IntStream.rangeClosed(1, 2).forEach(x -> handler.record(DECISION_NAME, ENDPOINT_NAME, "B"));
        IntStream.rangeClosed(1, 5).forEach(x -> handler.record(DECISION_NAME, ENDPOINT_NAME, "C"));

        // Assert
        assertEquals(expectedCountStringA, registry.find(dmnType + DecisionConstants.DECISIONS_NAME_SUFFIX)
                .tag("identifier", "A")
                .counter().count());

        assertEquals(expectedCountStringB, registry.find(dmnType + DecisionConstants.DECISIONS_NAME_SUFFIX)
                .tag("identifier", "B")
                .counter().count());

        assertEquals(expectedCountStringC, registry.find(dmnType + DecisionConstants.DECISIONS_NAME_SUFFIX)
                .tag("identifier", "C")
                .counter().count());
    }
}
