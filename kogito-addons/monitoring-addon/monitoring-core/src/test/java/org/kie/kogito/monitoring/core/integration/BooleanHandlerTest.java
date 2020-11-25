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
import org.kie.kogito.monitoring.core.system.metrics.dmnhandlers.BooleanHandler;
import org.kie.kogito.monitoring.core.system.metrics.dmnhandlers.DecisionConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BooleanHandlerTest {

    private static final String ENDPOINT_NAME = "hello";

    SimpleMeterRegistry registry;
    BooleanHandler handler;
    String dmnType;

    @BeforeEach
    public void setUp() {
        dmnType = "boolean";
        registry = new SimpleMeterRegistry();
        handler = new BooleanHandler(dmnType, registry);
    }

    @AfterEach
    public void destroy() {
        registry.clear();
    }

    @Test
    public void givenSomeBooleanMetricsWhenMetricsAreStoredThenTheCountIsCorrect() {
        // Arrange
        Double expectedTrue = 3.0;
        Double expectedFalse = 2.0;

        // Act
        IntStream.rangeClosed(1, 3).forEach(x -> handler.record("decision", ENDPOINT_NAME, true));
        IntStream.rangeClosed(1, 2).forEach(x -> handler.record("decision", ENDPOINT_NAME, false));

        assertEquals(expectedTrue, registry.find(dmnType + DecisionConstants.DECISIONS_NAME_SUFFIX)
                .tag("identifier", "true")
                .counter()
                .count());

        assertEquals(expectedFalse, registry.find(dmnType + DecisionConstants.DECISIONS_NAME_SUFFIX)
                .tag("identifier", "false")
                .counter()
                .count());
    }
}
