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
package org.kie.kogito.monitoring.core.common.integration;

import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.KogitoGAV;
import org.kie.kogito.monitoring.core.common.system.metrics.dmnhandlers.BooleanHandler;
import org.kie.kogito.monitoring.core.common.system.metrics.dmnhandlers.DecisionConstants;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import static org.assertj.core.api.Assertions.assertThat;

public class BooleanHandlerTest {

    private static final String ENDPOINT_NAME = "hello";

    SimpleMeterRegistry registry;
    BooleanHandler handler;
    String dmnType;

    @BeforeEach
    public void setUp() {
        dmnType = "boolean";
        registry = new SimpleMeterRegistry();
        handler = new BooleanHandler(dmnType, KogitoGAV.EMPTY_GAV, registry);
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

        assertThat(registry.find(dmnType + DecisionConstants.DECISIONS_NAME_SUFFIX)
                .tag("identifier", "true")
                .counter()
                .count()).isEqualTo(expectedTrue);

        assertThat(registry.find(dmnType + DecisionConstants.DECISIONS_NAME_SUFFIX)
                .tag("identifier", "false")
                .counter()
                .count()).isEqualTo(expectedFalse);
    }
}
