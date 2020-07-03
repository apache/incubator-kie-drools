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

package org.kie.kogito.monitoring.integration;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.dmn.rest.DMNResult;
import org.kie.kogito.grafana.dmn.SupportedDecisionTypes;
import org.kie.kogito.monitoring.mocks.DMNDecisionResultMock;
import org.kie.kogito.monitoring.system.metrics.DMNResultMetricsBuilder;
import org.kie.kogito.monitoring.system.metrics.dmnhandlers.DecisionConstants;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DMNResultMetricsBuilderTest {

    private static final String ENDPOINT_NAME = "hello";
    CollectorRegistry registry;

    @BeforeEach
    public void setUp() {
        registry = CollectorRegistry.defaultRegistry;
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
        assertEquals(expectedAlphabetDecisionA, getLabelsValue(SupportedDecisionTypes.fromInternalToStandard(String.class), "AlphabetDecision", "A"));
        assertEquals(expectedDictionaryDecisionHello, getLabelsValue(SupportedDecisionTypes.fromInternalToStandard(String.class), "DictionaryDecision", "Hello"));
        assertEquals(expectedDictionaryDecisionWorld, getLabelsValue(SupportedDecisionTypes.fromInternalToStandard(String.class), "DictionaryDecision", "World"));
        // Boolean type
        assertEquals(expectedTrueBooleanDecision, getLabelsValue(SupportedDecisionTypes.fromInternalToStandard(Boolean.class), "BooleanDecision", "true"));
        // LocalDateTime Time
        assertTrue(getQuantile("LocalDateTimeDecision", SupportedDecisionTypes.fromInternalToStandard(LocalDateTime.class), ENDPOINT_NAME, 0.1) >= 0);
        // Duration type
        assertTrue(getQuantile("DaysAndTimeDurationDecision", SupportedDecisionTypes.fromInternalToStandard(Duration.class), ENDPOINT_NAME, 0.1) >= 0);
        // BigDecimal type
        assertTrue(getQuantile("BigDecimalDecision", SupportedDecisionTypes.fromInternalToStandard(BigDecimal.class), ENDPOINT_NAME, 0.1) >= 0);
        // Years And Months Duration type
        assertTrue(getQuantile("YearsAndMonthsDecision", SupportedDecisionTypes.fromInternalToStandard(Period.class), ENDPOINT_NAME, 0.1) >= 0);
        // LocalDate type
        assertTrue(getQuantile("LocalDateDecision", SupportedDecisionTypes.fromInternalToStandard(LocalDate.class), ENDPOINT_NAME, 0.1) >= 0);
        // LocalTime type
        assertTrue(getQuantile("LocalTimeDecision", SupportedDecisionTypes.fromInternalToStandard(LocalTime.class), ENDPOINT_NAME, 0.1) >= 0);
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

    private Double getLabelsValue(String name, String decisionName, String labelValue) {
        return registry.getSampleValue(name + DecisionConstants.DECISIONS_NAME_SUFFIX, DecisionConstants.DECISION_ENDPOINT_IDENTIFIER_LABELS, new String[]{decisionName, ENDPOINT_NAME, labelValue});
    }

    private double getQuantile(String decision, String name, String labelValue, double q) {
        return registry.getSampleValue(name.replace(" ", "_") + DecisionConstants.DECISIONS_NAME_SUFFIX, AbstractQuantilesTest.INTERNAL_PROMETHEUS_LABELS, new String[]{decision, labelValue, Collector.doubleToGoString(q)});
    }
}
