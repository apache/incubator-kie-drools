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

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.dmn.rest.DMNResult;
import org.kie.kogito.grafana.dmn.SupportedDecisionTypes;
import org.kie.kogito.monitoring.core.MonitoringRegistry;
import org.kie.kogito.monitoring.core.mock.DMNDecisionResultMock;
import org.kie.kogito.monitoring.core.system.metrics.DMNResultMetricsBuilder;
import org.kie.kogito.monitoring.core.system.metrics.dmnhandlers.DecisionConstants;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DMNResultMetricsBuilderTest {

    private static final String ENDPOINT_NAME = "hello";
    MeterRegistry registry;

    @BeforeEach
    public void setUp() {
        registry = new SimpleMeterRegistry();
        MonitoringRegistry.addRegistry(registry);
    }

    @AfterEach
    public void cleanUp() {
        MonitoringRegistry.getDefaultMeterRegistry().remove(registry);
    }

    @Test
    public void givenADMNResultWhenMetricsAreStoredThenTheCollectorsAreProperlyWorking() {
        // Arrange
        DMNResult dmnResult = new DMNResult();
        List<DMNDecisionResultMock> decisions = new ArrayList<>();
        // String type
        decisions.add(new DMNDecisionResultMock("AlphabetDecision", "A"));
        decisions.add(new DMNDecisionResultMock("DictionaryDecision", "Hello"));
        decisions.add(new DMNDecisionResultMock("DictionaryDecision", "Hello"));
        decisions.add(new DMNDecisionResultMock("DictionaryDecision", "World"));
        // Boolean type
        decisions.add(new DMNDecisionResultMock("BooleanDecision", true));
        // LocalDateTime type
        decisions.add(new DMNDecisionResultMock("LocalDateTimeDecision", LocalDateTime.now()));
        // Days And Time Duration type
        decisions.add(new DMNDecisionResultMock("DaysAndTimeDurationDecision", Duration.ofSeconds(1)));
        // BigDecimal Type
        decisions.add(new DMNDecisionResultMock("BigDecimalDecision", new BigDecimal(1)));
        // Years And Months Duration type
        decisions.add(new DMNDecisionResultMock("YearsAndMonthsDecision", Period.ofMonths(12)));
        // LocalDate type
        decisions.add(new DMNDecisionResultMock("LocalDateDecision", LocalDate.of(2020, 1, 1)));
        // LocalTime type
        decisions.add(new DMNDecisionResultMock("LocalTimeDecision", LocalTime.of(12, 0)));

        dmnResult.setDecisionResults(decisions);

        int expectedAlphabetDecisionA = 1;
        int expectedDictionaryDecisionHello = 2;
        int expectedDictionaryDecisionWorld = 1;
        int expectedTrueBooleanDecision = 1;

        // Act
        DMNResultMetricsBuilder.generateMetrics(dmnResult, ENDPOINT_NAME);

        // Assert
        // String type
        String stringDmnType = SupportedDecisionTypes.fromInternalToStandard(String.class);
        assertEquals(expectedAlphabetDecisionA, registry.find(stringDmnType + DecisionConstants.DECISIONS_NAME_SUFFIX)
                .tag("decision", "AlphabetDecision")
                .tag("identifier", "A")
                .counter()
                .count());

        assertEquals(expectedDictionaryDecisionHello, registry.find(stringDmnType
                                                                      + DecisionConstants.DECISIONS_NAME_SUFFIX)
                .tag("decision", "DictionaryDecision")
                .tag("identifier", "Hello")
                .counter()
                .count());

        assertEquals(expectedDictionaryDecisionWorld, registry.find(stringDmnType
                                                                      + DecisionConstants.DECISIONS_NAME_SUFFIX)
                .tag("decision", "DictionaryDecision")
                .tag("identifier", "World")
                .counter()
                .count());

        // Boolean type
        String booleanDmnType = SupportedDecisionTypes.fromInternalToStandard(Boolean.class);
        assertEquals(expectedTrueBooleanDecision, registry.find(booleanDmnType
                                                                        + DecisionConstants.DECISIONS_NAME_SUFFIX)
                .tag("decision", "BooleanDecision")
                .tag("identifier", "true")
                .counter()
                .count());

        // LocalDateTime Time
        assertTrue(registry.find(SupportedDecisionTypes.fromInternalToStandard(LocalDateTime.class).replace(" ", "_") + DecisionConstants.DECISIONS_NAME_SUFFIX).summary().max() >= 5);
        // Duration type
        assertTrue(registry.find(SupportedDecisionTypes.fromInternalToStandard(Duration.class).replace(" ", "_") + DecisionConstants.DECISIONS_NAME_SUFFIX).summary().max() >= 5);
        // BigDecimal type
        assertTrue(registry.find(SupportedDecisionTypes.fromInternalToStandard(BigDecimal.class) + DecisionConstants.DECISIONS_NAME_SUFFIX).summary().max() >= 1);
        // Years And Months Duration type
        assertTrue(registry.find(SupportedDecisionTypes.fromInternalToStandard(Period.class).replace(" ", "_") + DecisionConstants.DECISIONS_NAME_SUFFIX).summary().max() >= 5);
        // LocalDate type
        assertTrue(registry.find(SupportedDecisionTypes.fromInternalToStandard(LocalDate.class) + DecisionConstants.DECISIONS_NAME_SUFFIX).summary().max() >= 5);
        // LocalTime type
        assertTrue(registry.find(SupportedDecisionTypes.fromInternalToStandard(LocalTime.class) + DecisionConstants.DECISIONS_NAME_SUFFIX).summary().max() >= 5);
    }

    // Keep aligned the mapping of types between kogito-codegen and prometheus-addon.
    @Test
    public void alighmentWithKogitoCodegenIsOk() {
        List addonSupportedTypes = DMNResultMetricsBuilder.getHandlers().values().stream().map(x -> x.getDmnType()).collect(Collectors.toList());
        assertTrue(addonSupportedTypes.containsAll(SupportedDecisionTypes.getSupportedDMNTypes()));
        assertTrue(SupportedDecisionTypes.getSupportedDMNTypes().containsAll(addonSupportedTypes));
    }

    @Test
    public void givenANullDMNResultWhenMetricsAreRegisteredThenTheSampleIsDiscarded() {
        // Assert
        assertDoesNotThrow(() -> DMNResultMetricsBuilder.generateMetrics(null, ENDPOINT_NAME));
    }
}
