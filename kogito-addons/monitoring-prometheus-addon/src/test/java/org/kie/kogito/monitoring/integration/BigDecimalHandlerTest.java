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
import java.math.MathContext;
import java.util.HashMap;

import ch.obermuhlner.math.big.stream.BigDecimalStream;
import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.monitoring.system.metrics.dmnhandlers.BigDecimalHandler;
import org.kie.kogito.monitoring.system.metrics.dmnhandlers.DecisionConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BigDecimalHandlerTest {

    private static final String ENDPOINT_NAME = "hello";
    private static final String[] INTERNAL_PROMETHEUS_LABELS =
            new String[]{
                    DecisionConstants.DECISION_ENDPOINT_IDENTIFIER_LABELS[0],
                    DecisionConstants.DECISION_ENDPOINT_IDENTIFIER_LABELS[1],
                    "quantile"
            };

    CollectorRegistry registry;
    BigDecimalHandler handler;

    @BeforeEach
    public void setUp() {
        registry = new CollectorRegistry();
        handler = new BigDecimalHandler("hello", registry);
    }

    @AfterEach
    public void destroy() {
        registry.clear();
    }

    @Test
    public void GivenSomeSamples_WhenQuantilesAreCalculated_ThenTheQuantilesAreCorrect() {
        // Arrange
        HashMap<Double, Double> expectedQuantiles = new HashMap<>();
        expectedQuantiles.put(0.1, 999.0);
        expectedQuantiles.put(0.25, 2525.0);
        expectedQuantiles.put(0.5, 5042.0);
        expectedQuantiles.put(0.75, 7551.0);
        expectedQuantiles.put(0.9, 9062.0);
        expectedQuantiles.put(0.99, 10000.0);

        // Act
        BigDecimalStream.range(BigDecimal.valueOf(1), BigDecimal.valueOf(10001), BigDecimal.ONE, MathContext.DECIMAL64).forEach(x -> handler.record("decision", ENDPOINT_NAME, x));

        // Assert
        for (Double key : expectedQuantiles.keySet()) {
            assertEquals(expectedQuantiles.get(key), getQuantile("decision", ENDPOINT_NAME + DecisionConstants.DECISIONS_NAME_SUFFIX, ENDPOINT_NAME, key), 5);
        }
    }

    private double getQuantile(String decision, String name, String labelValue, double q) {
        return registry.getSampleValue(name, INTERNAL_PROMETHEUS_LABELS, new String[]{decision, labelValue, Collector.doubleToGoString(q)}).doubleValue();
    }
}
