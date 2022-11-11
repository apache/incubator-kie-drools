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

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.KogitoGAV;
import org.kie.kogito.monitoring.core.common.system.metrics.SystemMetricsCollector;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import static org.assertj.core.api.Assertions.assertThat;

public class SystemMetricsCollectorTest {

    private static final String handler = "hello";
    MeterRegistry registry;
    SystemMetricsCollector systemMetricsCollector;

    @BeforeEach
    public void setUp() {
        registry = new SimpleMeterRegistry();
        systemMetricsCollector = new SystemMetricsCollector(KogitoGAV.EMPTY_GAV, registry);
    }

    @Test
    public void givenAStatusCodeWhenRegisterStatusCodeRequestIsCalledThenTheStatusCodeIsExportedToPrometheus() {
        // Arrange
        HashMap<String, Integer> mapCodeRepetition = new HashMap<>();
        mapCodeRepetition.put("400", 100);
        mapCodeRepetition.put("404", 50);

        // Act
        IntStream.range(0, mapCodeRepetition.get("400")).forEach(x -> systemMetricsCollector.registerStatusCodeRequest(handler, "400"));
        IntStream.range(0, mapCodeRepetition.get("404")).forEach(x -> systemMetricsCollector.registerStatusCodeRequest(handler, "404"));

        // Assert
        assertThat((int) registry.find("api_http_response_code").tag("identifier", "400").counter().count()).isEqualTo(mapCodeRepetition.get("400"));
        assertThat((int) registry.find("api_http_response_code").tag("identifier", "404").counter().count()).isEqualTo(mapCodeRepetition.get("404"));
    }

    @Test
    public void givenAnExceptionWhenRegisterExceptionIsCalledThenTheStatusCodeIsExportedToPrometheus() {
        // Arrange
        HashMap<String, Integer> mapExceptionRepetition = new HashMap<>();
        mapExceptionRepetition.put("NoSuchElement", 10);
        mapExceptionRepetition.put("Exception", 20);

        // Act
        IntStream.range(0, mapExceptionRepetition.get("NoSuchElement")).forEach(x -> systemMetricsCollector.registerException(handler, "NoSuchElement"));
        IntStream.range(0, mapExceptionRepetition.get("Exception")).forEach(x -> systemMetricsCollector.registerException(handler, "Exception"));

        // Assert
        assertThat((int) registry.find("api_http_stacktrace_exceptions")
                .tag("identifier", "NoSuchElement")
                .counter().count()).isEqualTo(mapExceptionRepetition.get("NoSuchElement"));
        assertThat((int) registry.find("api_http_stacktrace_exceptions")
                .tag("identifier", "Exception")
                .counter().count()).isEqualTo(mapExceptionRepetition.get("Exception"));
    }

    @Test
    public void givenAnElapsedTimeSampleWhenRegisterElapsedTimeSampleMetricsThenTheStatusCodeIsExportedToPrometheus() {
        // Act
        IntStream.range(1, 10001).forEach(x -> systemMetricsCollector.registerElapsedTimeSampleMetrics(handler, x));

        // Assert
        assertThat(registry.find("api_execution_elapsed")
                .timer().max(TimeUnit.NANOSECONDS)).isGreaterThanOrEqualTo(5000);
    }
}
