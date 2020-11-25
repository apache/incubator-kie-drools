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
import java.math.MathContext;

import ch.obermuhlner.math.big.stream.BigDecimalStream;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.monitoring.core.system.metrics.dmnhandlers.BigDecimalHandler;
import org.kie.kogito.monitoring.core.system.metrics.dmnhandlers.DecisionConstants;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BigDecimalHandlerTest extends AbstractQuantilesTest<BigDecimalHandler> {
    @BeforeEach
    public void setUp() {
        dmnType = "bigdecimal";
        registry = new SimpleMeterRegistry();
        handler = new BigDecimalHandler(dmnType, registry);
    }

    @AfterEach
    public void destroy() {
        registry.clear();
    }

    @Test
    public void givenSomeSamplesWhenQuantilesAreCalculatedThenTheQuantilesAreCorrect() {
        // Act
        BigDecimalStream.range(BigDecimal.valueOf(1), BigDecimal.valueOf(10001), BigDecimal.ONE, MathContext.DECIMAL64).forEach(x -> handler.record("decision", ENDPOINT_NAME, x));
        assertTrue(registry.find(dmnType + DecisionConstants.DECISIONS_NAME_SUFFIX).summary().max() >= 10000);
        assertTrue(registry.find(dmnType + DecisionConstants.DECISIONS_NAME_SUFFIX).summary().mean() > 0);
    }
}
