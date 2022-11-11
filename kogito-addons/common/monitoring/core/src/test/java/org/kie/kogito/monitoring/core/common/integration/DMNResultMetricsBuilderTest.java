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

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.KogitoGAV;
import org.kie.kogito.dmn.rest.KogitoDMNResult;
import org.kie.kogito.grafana.dmn.SupportedDecisionTypes;
import org.kie.kogito.monitoring.core.common.mock.DMNDecisionResultMock;
import org.kie.kogito.monitoring.core.common.system.metrics.DMNResultMetricsBuilder;
import org.kie.kogito.monitoring.core.common.system.metrics.dmnhandlers.DecisionConstants;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

public class DMNResultMetricsBuilderTest {

    private static final String ENDPOINT_NAME = "hello";
    MeterRegistry registry;
    DMNResultMetricsBuilder dmnResultMetricsBuilder;

    @BeforeEach
    public void setUp() {
        registry = new SimpleMeterRegistry();
        dmnResultMetricsBuilder = new DMNResultMetricsBuilder(KogitoGAV.EMPTY_GAV, registry);
    }

    @Test
    public void givenADMNResultWhenMetricsAreStoredThenTheCollectorsAreProperlyWorking() {
        // Arrange
        KogitoDMNResult dmnResult = new KogitoDMNResult();
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
        dmnResultMetricsBuilder.generateMetrics(dmnResult, ENDPOINT_NAME);

        // Assert
        // String type
        String stringDmnType = SupportedDecisionTypes.fromInternalToStandard(String.class);
        assertThat(registry.find(stringDmnType + DecisionConstants.DECISIONS_NAME_SUFFIX)
                .tag("decision", "AlphabetDecision")
                .tag("identifier", "A")
                .counter()
                .count()).isEqualTo(expectedAlphabetDecisionA);

        assertThat(registry.find(stringDmnType
                + DecisionConstants.DECISIONS_NAME_SUFFIX)
                .tag("decision", "DictionaryDecision")
                .tag("identifier", "Hello")
                .counter()
                .count()).isEqualTo(expectedDictionaryDecisionHello);

        assertThat(registry.find(stringDmnType
                + DecisionConstants.DECISIONS_NAME_SUFFIX)
                .tag("decision", "DictionaryDecision")
                .tag("identifier", "World")
                .counter()
                .count()).isEqualTo(expectedDictionaryDecisionWorld);

        // Boolean type
        String booleanDmnType = SupportedDecisionTypes.fromInternalToStandard(Boolean.class);
        assertThat(registry.find(booleanDmnType
                + DecisionConstants.DECISIONS_NAME_SUFFIX)
                .tag("decision", "BooleanDecision")
                .tag("identifier", "true")
                .counter()
                .count()).isEqualTo(expectedTrueBooleanDecision);

        // LocalDateTime Time
        assertThat(registry.find(SupportedDecisionTypes.fromInternalToStandard(LocalDateTime.class).replace(" ", "_") + DecisionConstants.DECISIONS_NAME_SUFFIX).summary().max())
                .isGreaterThanOrEqualTo(5);
        // Duration type
        assertThat(registry.find(SupportedDecisionTypes.fromInternalToStandard(Duration.class).replace(" ", "_") + DecisionConstants.DECISIONS_NAME_SUFFIX).summary().max()).isGreaterThanOrEqualTo(5);
        // BigDecimal type
        assertThat(registry.find(SupportedDecisionTypes.fromInternalToStandard(BigDecimal.class) + DecisionConstants.DECISIONS_NAME_SUFFIX).summary().max()).isPositive();
        // Years And Months Duration type
        assertThat(registry.find(SupportedDecisionTypes.fromInternalToStandard(Period.class).replace(" ", "_") + DecisionConstants.DECISIONS_NAME_SUFFIX).summary().max()).isGreaterThanOrEqualTo(5);
        // LocalDate type
        assertThat(registry.find(SupportedDecisionTypes.fromInternalToStandard(LocalDate.class) + DecisionConstants.DECISIONS_NAME_SUFFIX).summary().max()).isGreaterThanOrEqualTo(5);
        // LocalTime type
        assertThat(registry.find(SupportedDecisionTypes.fromInternalToStandard(LocalTime.class) + DecisionConstants.DECISIONS_NAME_SUFFIX).summary().max()).isGreaterThanOrEqualTo(5);
    }

    // Keep aligned the mapping of types between kogito-codegen and prometheus-addon.
    @Test
    public void alighmentWithKogitoCodegenIsOk() {
        List addonSupportedTypes = dmnResultMetricsBuilder.getHandlers().values().stream().map(x -> x.getDmnType()).collect(Collectors.toList());
        assertThat(addonSupportedTypes).containsAll(SupportedDecisionTypes.getSupportedDMNTypes());
        assertThat(SupportedDecisionTypes.getSupportedDMNTypes()).containsAll(addonSupportedTypes);
    }

    @Test
    public void givenANullDMNResultWhenMetricsAreRegisteredThenTheSampleIsDiscarded() {
        // Assert
        assertThatNoException().isThrownBy(() -> dmnResultMetricsBuilder.generateMetrics(null, ENDPOINT_NAME));
    }
}
