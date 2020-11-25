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

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.monitoring.core.system.metrics.dmnhandlers.DecisionConstants;
import org.kie.kogito.monitoring.core.system.metrics.dmnhandlers.LocalDateTimeHandler;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LocalDateTimeHandlerTest extends AbstractQuantilesTest<LocalDateTimeHandler> {

    @BeforeEach
    public void setUp() {
        registry = new SimpleMeterRegistry();
        handler = new LocalDateTimeHandler("hello", registry);
    }

    @AfterEach
    public void destroy() {
        registry.clear();
    }

    @Test
    public void givenLocalDateTimeMetricsWhenMetricsAreStoredThenTheQuantilesAreCorrect() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Double[] quantiles = new Double[]{0.1, 0.25, 0.5, 0.75, 0.9, 0.99};

        // Act
        handler.record("decision", ENDPOINT_NAME, now);

        // Assert
        assertTrue(registry.find(ENDPOINT_NAME + DecisionConstants.DECISIONS_NAME_SUFFIX).summary().max() >= 5);
        assertTrue(registry.find(ENDPOINT_NAME + DecisionConstants.DECISIONS_NAME_SUFFIX).summary().mean() >= 2);
    }
}
