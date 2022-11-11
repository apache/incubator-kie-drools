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

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.KogitoGAV;
import org.kie.kogito.monitoring.core.common.system.metrics.dmnhandlers.DaysAndTimeDurationHandler;
import org.kie.kogito.monitoring.core.common.system.metrics.dmnhandlers.DecisionConstants;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import static org.assertj.core.api.Assertions.assertThat;

public class DaysAndTimeDurationHandlerTest extends AbstractQuantilesTest<DaysAndTimeDurationHandler> {

    @BeforeEach
    public void setUp() {
        dmnType = "daysandtimeduration";
        registry = new SimpleMeterRegistry();
        handler = new DaysAndTimeDurationHandler(dmnType, KogitoGAV.EMPTY_GAV, registry);
    }

    @AfterEach
    public void destroy() {
        registry.clear();
    }

    @Test
    public void givenDurationMetricsWhenMetricsAreStoredThenTheQuantilesAreCorrect() {
        // Arrange
        Duration duration = Duration.ofMillis(10000);

        // Act
        handler.record("decision", ENDPOINT_NAME, duration);

        // Assert
        assertThat(registry.find(dmnType + DecisionConstants.DECISIONS_NAME_SUFFIX).summary().max()).isGreaterThanOrEqualTo(5);
        assertThat(registry.find(dmnType + DecisionConstants.DECISIONS_NAME_SUFFIX).summary().mean()).isGreaterThanOrEqualTo(2);
    }
}
